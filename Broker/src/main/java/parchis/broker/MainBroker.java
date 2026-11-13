package parchis.broker;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;

/**
 * Arranca el broker. Se corre UNA vez, en la computadora 1, antes que todo lo demas.
 * Imprime las IPs de la computadora para saber que poner en la computadora 2.
 */
public class MainBroker {

    public static void main(String[] args) throws IOException {
        int puerto = args.length > 0 ? Integer.parseInt(args[0]) : ServidorBroker.PUERTO_POR_DEFECTO;
        imprimirIPs();
        new ServidorBroker(puerto).iniciar();
    }

    private static void imprimirIPs() {
        System.out.println("[Broker] IPs de esta computadora (usa una de estas en la otra computadora):");
        try {
            for (NetworkInterface red : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (!red.isUp() || red.isLoopback()) {
                    continue;
                }
                for (InetAddress ip : Collections.list(red.getInetAddresses())) {
                    if (ip instanceof Inet4Address) {
                        System.out.println("           " + ip.getHostAddress() + "   (" + red.getDisplayName() + ")");
                    }
                }
            }
        } catch (SocketException e) {
            System.out.println("[Broker] No se pudieron leer las IPs: " + e.getMessage());
        }
    }
}
