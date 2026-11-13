package parchis.persistencia;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Abre conexiones a MySQL.
 *
 * Los datos (url, usuario y CONTRASENA) se leen del archivo "bd.properties"
 * que esta en la carpeta desde donde se corre el servidor (ServidorSala/).
 * Ese archivo esta en .gitignore: la contrasena NUNCA se sube a GitHub.
 * En GitHub solo esta "bd.properties.ejemplo" para copiarlo.
 */
class ConexionBD {

    private static final String ARCHIVO = "bd.properties";

    private ConexionBD() {
    }

    static Connection obtenerConexion() throws SQLException {
        Properties config = new Properties();
        File archivo = new File(ARCHIVO);
        try (FileInputStream entrada = new FileInputStream(archivo)) {
            config.load(entrada);
        } catch (IOException e) {
            throw new SQLException("No encontre " + archivo.getAbsolutePath()
                    + ". Copia bd.properties.ejemplo como bd.properties y pon tu contrasena.");
        }
        return DriverManager.getConnection(
                config.getProperty("url"),
                config.getProperty("usuario"),
                config.getProperty("contrasena"));
    }
}
