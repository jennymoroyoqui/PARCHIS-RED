package parchis.broker;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Atiende a UN cliente dentro del broker (corre en su propio hilo).
 * Lee sus mensajes y, segun la accion, lo suscribe a un canal o publica.
 */
class ConexionCliente implements Runnable {

    private final Socket socket;
    private final ServidorBroker broker;
    private ObjectOutputStream salida;
    private String idCliente;   // se conoce con el primer mensaje que manda

    ConexionCliente(Socket socket, ServidorBroker broker) {
        this.socket = socket;
        this.broker = broker;
    }

    @Override
    public void run() {
        try {
            // Primero la salida (y flush) y luego la entrada; si no, los dos lados se quedan esperando.
            salida = new ObjectOutputStream(socket.getOutputStream());
            salida.flush();
            ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());

            while (true) {
                Mensaje mensaje = (Mensaje) entrada.readObject();
                if (idCliente == null) {
                    idCliente = mensaje.getRemitente();
                }
                if (Mensaje.SUSCRIBIR.equals(mensaje.getAccion())) {
                    broker.suscribir(mensaje.getCanal(), this);
                } else if (Mensaje.PUBLICAR.equals(mensaje.getAccion())) {
                    broker.publicar(mensaje);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            // El cliente cerro el programa o se cayo la red: es normal, salimos del ciclo.
        } finally {
            broker.desconectar(this);
            try {
                socket.close();
            } catch (IOException e) {
                // nada que hacer
            }
        }
    }

    /** synchronized: varios hilos pueden querer escribirle a este cliente al mismo tiempo. */
    synchronized void enviar(Mensaje mensaje) {
        try {
            salida.writeObject(mensaje);
            salida.reset();   // para que no reutilice objetos viejos ya enviados
            salida.flush();
        } catch (IOException e) {
            ServidorBroker.log("No se pudo enviar a " + idCliente + ": " + e.getMessage());
        }
    }

    String getIdCliente() {
        return idCliente;
    }
}
