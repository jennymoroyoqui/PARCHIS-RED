package parchis.broker;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.UUID;

/**
 * Lo que usa cualquier programa (cliente del juego, ServidorSala, prototipo)
 * para hablar con el broker: conectarse, suscribirse a canales y publicar.
 * Los mensajes que llegan se le pasan al IReceptorMensajes desde un hilo aparte.
 */
public class ClienteBroker {

    private static final int MILISEGUNDOS_PARA_CONECTAR = 3000;

    /** Id corto y unico de este cliente, ej. "c3f9a1b2". */
    private final String id = "c" + UUID.randomUUID().toString().substring(0, 8);

    private Socket socket;
    private ObjectOutputStream salida;
    private IReceptorMensajes receptor;
    private volatile boolean cerrando = false;

    public void setReceptor(IReceptorMensajes receptor) {
        this.receptor = receptor;
    }

    public void conectar(String host, int puerto) throws IOException {
        socket = new Socket();
        socket.connect(new InetSocketAddress(host, puerto), MILISEGUNDOS_PARA_CONECTAR);
        salida = new ObjectOutputStream(socket.getOutputStream());
        salida.flush();
        ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());
        cerrando = false;

        Thread hilo = new Thread(() -> escuchar(entrada), "escucha-broker-" + id);
        hilo.start();
    }

    public void suscribir(String canal) throws IOException {
        enviar(new Mensaje(Mensaje.SUSCRIBIR, canal, null, id, null));
    }

    public void publicar(String canal, String tipo, Serializable datos) throws IOException {
        enviar(new Mensaje(Mensaje.PUBLICAR, canal, tipo, id, datos));
    }

    public String getId() {
        return id;
    }

    public void cerrar() {
        cerrando = true;
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            // ya estaba cerrado
        }
    }

    private synchronized void enviar(Mensaje mensaje) throws IOException {
        if (salida == null) {
            throw new IOException("No estas conectado al broker");
        }
        salida.writeObject(mensaje);
        salida.reset();
        salida.flush();
    }

    /** Corre en su propio hilo: espera mensajes del broker y se los da al receptor. */
    private void escuchar(ObjectInputStream entrada) {
        try {
            while (true) {
                Mensaje mensaje = (Mensaje) entrada.readObject();
                if (receptor != null) {
                    receptor.mensajeRecibido(mensaje);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            if (!cerrando && receptor != null) {
                receptor.conexionPerdida("Se cerró la conexión con el broker.");
            }
        }
    }
}
