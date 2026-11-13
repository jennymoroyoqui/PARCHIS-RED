package parchis.servidorsala;

import java.io.IOException;
import parchis.broker.ClienteBroker;
import parchis.broker.ServidorBroker;

/**
 * Arranca el ServidorSala. Se corre en la computadora 1 DESPUES de MainBroker.
 * Opcional: argumentos [ipBroker] [puerto] (por defecto localhost 5000).
 */
public class MainServidorSala {

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int puerto = args.length > 1 ? Integer.parseInt(args[1]) : ServidorBroker.PUERTO_POR_DEFECTO;

        SalaBO sala = new SalaBO(new PerfilBO());
        ServicioSala servicio = new ServicioSala(new ClienteBroker(), sala);
        try {
            servicio.iniciar(host, puerto);
            System.out.println("[ServidorSala] Conectado al broker en " + host + ":" + puerto + ". Esperando jugadores...");
        } catch (IOException e) {
            System.out.println("[ServidorSala] No se pudo conectar al broker en " + host + ":" + puerto
                    + ". Primero corre MainBroker.");
            System.exit(1);
        }
    }
}
