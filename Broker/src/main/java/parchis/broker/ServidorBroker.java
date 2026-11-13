package parchis.broker;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * El broker. Acepta conexiones y reenvia cada mensaje PUBLICADO en un canal
 * a todos los que estan SUSCRITOS a ese canal. No sabe nada del juego.
 *
 * Ejemplo: el ServidorSala se suscribe a "sala.peticiones". Cuando un jugador
 * publica ahi un "ENTRAR", el broker se lo pasa al ServidorSala. Ninguno de los
 * dos conoce la IP del otro: solo conocen al broker.
 */
public class ServidorBroker {

    public static final int PUERTO_POR_DEFECTO = 5000;

    /** Canal donde el broker avisa cuando un cliente se desconecto (su id va en los datos). */
    public static final String CANAL_DESCONEXIONES = "broker.desconexiones";
    public static final String DESCONECTADO = "DESCONECTADO";

    private final int puerto;

    /** canal -> conexiones suscritas a ese canal */
    private final Map<String, List<ConexionCliente>> suscripciones = new HashMap<>();

    public ServidorBroker(int puerto) {
        this.puerto = puerto;
    }

    /** Se queda aceptando clientes para siempre. Cada cliente se atiende en su propio hilo. */
    public void iniciar() throws IOException {
        try (ServerSocket servidor = new ServerSocket(puerto)) {
            log("Broker escuchando en el puerto " + puerto);
            while (true) {
                Socket socket = servidor.accept();
                log("Se conecto un cliente desde " + socket.getInetAddress().getHostAddress());
                new Thread(new ConexionCliente(socket, this)).start();
            }
        }
    }

    synchronized void suscribir(String canal, ConexionCliente conexion) {
        suscripciones.computeIfAbsent(canal, k -> new ArrayList<>()).add(conexion);
        log(conexion.getIdCliente() + " se suscribio a '" + canal + "'");
    }

    /** Reenvia el mensaje a todos los suscritos al canal. */
    void publicar(Mensaje mensaje) {
        List<ConexionCliente> destinos;
        synchronized (this) {
            destinos = new ArrayList<>(suscripciones.getOrDefault(mensaje.getCanal(), List.of()));
        }
        log(mensaje + " -> " + destinos.size() + " suscriptor(es)");
        for (ConexionCliente destino : destinos) {
            destino.enviar(mensaje);
        }
    }

    /** Un cliente se fue: se borra de todos los canales y se avisa a quien le interese. */
    void desconectar(ConexionCliente conexion) {
        synchronized (this) {
            for (List<ConexionCliente> lista : suscripciones.values()) {
                lista.remove(conexion);
            }
        }
        log("Se desconecto " + conexion.getIdCliente());
        if (conexion.getIdCliente() != null) {
            publicar(new Mensaje(Mensaje.PUBLICAR, CANAL_DESCONEXIONES, DESCONECTADO, "broker", conexion.getIdCliente()));
        }
    }

    static void log(String texto) {
        System.out.println("[Broker] " + texto);
    }
}
