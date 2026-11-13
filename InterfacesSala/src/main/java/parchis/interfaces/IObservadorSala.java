package parchis.interfaces;

import parchis.dtos.SalaDTO;

/**
 * Patron Observador: quien implemente esta interfaz (ControlSala) se entera
 * cada vez que la sala cambia, aunque el cambio lo haya hecho OTRO jugador
 * desde OTRA computadora. Asi se "sincroniza la informacion con el resto de los jugadores".
 */
public interface IObservadorSala {

    /** Alguien entro, salio o confirmo que esta listo (o empezo la partida). */
    void salaActualizada(SalaDTO sala);

    /** Se perdio la conexion con el broker (solo pasa en la version en red). */
    void conexionPerdida(String motivo);
}
