package parchis.persistencia;

/** Error al usar la base de datos. */
public class PersistenciaException extends Exception {

    private static final long serialVersionUID = 1L;

    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
