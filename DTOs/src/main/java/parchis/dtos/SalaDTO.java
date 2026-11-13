package parchis.dtos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Foto de como esta la sala en un momento dado: quienes estan y si la
 * partida ya empezo. Cada vez que algo cambia se manda una nueva SalaDTO
 * a todos los jugadores para que redibujen su pantalla.
 */
public class SalaDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** El Parchis es de 2 a 4 jugadores. */
    public static final int MINIMO_JUGADORES = 2;
    public static final int MAXIMO_JUGADORES = 4;

    private final List<JugadorDTO> jugadores;
    private final boolean partidaIniciada;

    public SalaDTO(List<JugadorDTO> jugadores, boolean partidaIniciada) {
        this.jugadores = new ArrayList<>(jugadores);   // copia, para que nadie la modifique desde afuera
        this.partidaIniciada = partidaIniciada;
    }

    public List<JugadorDTO> getJugadores() {
        return jugadores;
    }

    public int getNumeroJugadores() {
        return jugadores.size();
    }

    public boolean isPartidaIniciada() {
        return partidaIniciada;
    }
}
