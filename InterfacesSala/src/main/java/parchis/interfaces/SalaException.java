package parchis.interfaces;

/**
 * Error del subsistema Sala. El mensaje esta pensado para mostrarse
 * directamente al jugador en la pantalla.
 */
public class SalaException extends Exception {

    private static final long serialVersionUID = 1L;

    public SalaException(String mensaje) {
        super(mensaje);
    }
}
