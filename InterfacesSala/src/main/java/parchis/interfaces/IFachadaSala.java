package parchis.interfaces;

import parchis.dtos.PerfilDTO;
import parchis.dtos.SalaDTO;

/**
 * Fachada del subsistema Sala (CU-01 "Ingresar a la sala").
 *
 * La presentacion (ControlSala) SOLO conoce esta interfaz. No sabe si detras
 * esta el mock (Hito 2A) o la implementacion real que usa la red (Hito 2B).
 * Eso lo decide unicamente la fabrica.
 *
 * IMPORTANTE: NO cambiar esta interfaz sin avisar al equipo y sin actualizar
 * el modelo de StarUML (es uno de los errores que cuestan puntos).
 */
public interface IFachadaSala {

    /**
     * Mete al jugador en la sala de espera y regresa como quedo la sala.
     *
     * @throws SalaException en los flujos alternos:
     *         A2 - el nombre ya lo usa otro jugador de la sala,
     *         A3 - el color ya lo eligio otro jugador,
     *         A4 - la partida ya comenzo.
     */
    SalaDTO entrarASala(PerfilDTO perfil) throws SalaException;

    /** El jugador presiono "ESTOY LISTO". Si todos estan listos (minimo 2) empieza la partida. */
    void confirmarListo(String nombreJugador) throws SalaException;

    /** El jugador presiono "SALIR" en la sala de espera (A5). Se libera su lugar y su color. */
    void salirDeSala(String nombreJugador) throws SalaException;

    /** Registra a quien quiere enterarse cuando la sala cambie (patron Observador). */
    void agregarObservador(IObservadorSala observador);
}
