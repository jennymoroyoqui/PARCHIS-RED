package parchis.broker;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Lo que viaja por el socket entre un cliente y el broker.
 *
 * - accion:    SUSCRIBIR (quiero recibir lo de un canal) o PUBLICAR (manda esto a un canal).
 * - canal:     "buzon" al que va el mensaje (ej. "sala.peticiones").
 * - tipo:      para que es el mensaje (ej. "ENTRAR"). El broker no lo usa, solo lo reenvia.
 * - remitente: id del cliente que lo mando (sirve para responderle).
 * - contenido: el objeto (un DTO, un texto...) convertido a bytes.
 *
 * El contenido va como bytes a proposito: asi el broker NO necesita conocer
 * las clases del Parchis (PerfilDTO, SalaDTO...). Solo mueve bytes de un lado a otro.
 */
public class Mensaje implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String SUSCRIBIR = "SUSCRIBIR";
    public static final String PUBLICAR = "PUBLICAR";

    private final String accion;
    private final String canal;
    private final String tipo;
    private final String remitente;
    private final byte[] contenido;

    public Mensaje(String accion, String canal, String tipo, String remitente, Serializable datos) {
        this.accion = accion;
        this.canal = canal;
        this.tipo = tipo;
        this.remitente = remitente;
        this.contenido = aBytes(datos);
    }

    public String getAccion() {
        return accion;
    }

    public String getCanal() {
        return canal;
    }

    public String getTipo() {
        return tipo;
    }

    public String getRemitente() {
        return remitente;
    }

    /** Convierte los bytes de vuelta al objeto original (lo usa quien recibe el mensaje). */
    public Object leerDatos() {
        if (contenido == null) {
            return null;
        }
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(contenido))) {
            return in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("No se pudo leer el contenido del mensaje", e);
        }
    }

    private static byte[] aBytes(Serializable datos) {
        if (datos == null) {
            return null;
        }
        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(bytes)) {
            out.writeObject(datos);
            out.flush();
            return bytes.toByteArray();
        } catch (IOException e) {
            throw new IllegalArgumentException("No se pudo convertir a bytes: " + datos, e);
        }
    }

    @Override
    public String toString() {
        return accion + " canal=" + canal + (tipo != null ? " tipo=" + tipo : "") + " de=" + remitente;
    }
}
