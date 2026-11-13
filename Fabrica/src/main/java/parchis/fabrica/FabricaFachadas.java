package parchis.fabrica;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import parchis.interfaces.IFachadaSala;
import parchis.sala.FachadaSala;
import parchis.salamock.FachadaSalaMock;

/**
 * Fabrica de fachadas. Es la UNICA clase que conoce las implementaciones
 * concretas. ControlSala le pide una IFachadaSala y no sabe cual le dieron.
 *
 * Hito 2A: USAR_MOCK = true  -> FachadaSalaMock (todo en memoria)
 * Hito 2B: USAR_MOCK = false -> FachadaSala (real, en red con el broker)
 *
 * Para volver a correr las pruebas con el mock basta con cambiar USAR_MOCK.
 */
public final class FabricaFachadas {

    private static final boolean USAR_MOCK = false;

    /** Archivo (en la carpeta desde donde se corre) con la IP y puerto del broker. */
    private static final String ARCHIVO_CONFIGURACION = "parchis.properties";

    private FabricaFachadas() {
    }

    public static IFachadaSala crearFachadaSala() {
        if (USAR_MOCK) {
            return new FachadaSalaMock();
        }
        Properties config = leerConfiguracion();
        String host = config.getProperty("broker.host", "localhost").trim();
        int puerto = Integer.parseInt(config.getProperty("broker.puerto", "5000").trim());
        System.out.println("[Fabrica] Se usara la FachadaSala real. Broker en " + host + ":" + puerto);
        return new FachadaSala(host, puerto);
    }

    private static Properties leerConfiguracion() {
        Properties config = new Properties();
        File archivo = new File(ARCHIVO_CONFIGURACION);
        if (archivo.exists()) {
            try (FileInputStream entrada = new FileInputStream(archivo)) {
                config.load(entrada);
            } catch (IOException e) {
                System.out.println("[Fabrica] No se pudo leer " + archivo.getAbsolutePath() + ", se usa localhost:5000");
            }
        } else {
            System.out.println("[Fabrica] No existe " + archivo.getAbsolutePath() + ", se usa localhost:5000");
        }
        return config;
    }
}
