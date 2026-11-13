package parchis.dtos;

import java.io.Serializable;

/**
 * Respuesta que el ServidorSala le regresa a UN cliente despues de una peticion
 * (entrar, listo o salir). Si algo fallo, "exito" es false y "error" dice por que.
 */
public class RespuestaDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private final boolean exito;
    private final String error;
    private final SalaDTO sala;

    private RespuestaDTO(boolean exito, String error, SalaDTO sala) {
        this.exito = exito;
        this.error = error;
        this.sala = sala;
    }

    public static RespuestaDTO exito(SalaDTO sala) {
        return new RespuestaDTO(true, null, sala);
    }

    public static RespuestaDTO error(String mensaje) {
        return new RespuestaDTO(false, mensaje, null);
    }

    public boolean isExito() {
        return exito;
    }

    public String getError() {
        return error;
    }

    public SalaDTO getSala() {
        return sala;
    }
}
