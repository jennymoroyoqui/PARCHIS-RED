package parchis.broker.prototipo;

import java.io.IOException;
import java.util.Scanner;
import parchis.broker.ClienteBroker;
import parchis.broker.IReceptorMensajes;
import parchis.broker.Mensaje;
import parchis.broker.ServidorBroker;

/**
 * Prototipo del estilo (Hito 2A, punto 6): dos computadoras se mandan un mensaje
 * PASANDO POR EL BROKER. Ninguna conoce la IP de la otra, solo la del broker.
 *
 * Como probarlo:
 *   Computadora 1: correr MainBroker y despues este PrototipoMensaje (IP: Enter = localhost).
 *   Computadora 2: correr este PrototipoMensaje y escribir la IP de la computadora 1.
 *   Lo que escribas en una aparece en la otra.
 *
 * En NetBeans: clic derecho sobre este archivo -> Run File.
 */
public class PrototipoMensaje {

    private static final String CANAL = "prototipo";

    public static void main(String[] args) throws IOException {
        Scanner teclado = new Scanner(System.in);
        System.out.print("IP del broker (Enter = localhost): ");
        String host = teclado.nextLine().trim();
        if (host.isEmpty()) {
            host = "localhost";
        }
        System.out.print("Tu nombre: ");
        String nombre = teclado.nextLine().trim();

        ClienteBroker cliente = new ClienteBroker();
        cliente.setReceptor(new IReceptorMensajes() {
            @Override
            public void mensajeRecibido(Mensaje mensaje) {
                System.out.println("  << recibido por el broker: " + mensaje.leerDatos());
            }

            @Override
            public void conexionPerdida(String motivo) {
                System.out.println("  !! " + motivo);
            }
        });

        cliente.conectar(host, ServidorBroker.PUERTO_POR_DEFECTO);
        cliente.suscribir(CANAL);
        cliente.publicar(CANAL, "TEXTO", nombre + " se conecto");

        System.out.println("Conectado. Escribe un mensaje y presiona Enter ('salir' para terminar):");
        while (teclado.hasNextLine()) {
            String linea = teclado.nextLine();
            if (linea.equalsIgnoreCase("salir")) {
                break;
            }
            cliente.publicar(CANAL, "TEXTO", nombre + ": " + linea);
        }
        cliente.cerrar();
    }
}
