package parchis.servidorsala;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import parchis.dtos.JugadorDTO;
import parchis.dtos.PerfilDTO;
import parchis.dtos.SalaDTO;
import parchis.interfaces.SalaException;

/**
 * Objeto de negocio de la sala: aqui viven las REGLAS del CU-01.
 * Solo hay una sala (la "oficial") y esta en el servidor; las computadoras
 * de los jugadores solo reciben copias (SalaDTO).
 */
public class SalaBO {

    private final PerfilBO perfilBO;
    private final List<JugadorDTO> jugadores = new ArrayList<>();
    /** nombre del jugador -> id de su cliente en el broker (para saber quien se desconecto) */
    private final Map<String, String> clientePorJugador = new HashMap<>();
    private boolean partidaIniciada = false;

    public SalaBO(PerfilBO perfilBO) {
        this.perfilBO = perfilBO;
    }

    public void entrar(PerfilDTO perfil, String idCliente) throws SalaException {
        if (partidaIniciada) {   // A4
            throw new SalaException("La partida ya comenzó, ya no puedes entrar a la sala.");
        }
        for (JugadorDTO j : jugadores) {   // A2
            if (j.getNombre().equalsIgnoreCase(perfil.getNombre())) {
                throw new SalaException("El nombre \"" + perfil.getNombre() + "\" ya lo usa otro jugador. Escribe otro.");
            }
        }
        for (JugadorDTO j : jugadores) {   // A3 (con 4 colores, tambien limita la sala a 4)
            if (j.getColor() == perfil.getColor()) {
                throw new SalaException("El color " + perfil.getColor().getNombre() + " ya lo eligió otro jugador. Elige otro color.");
            }
        }

        perfilBO.guardarPerfil(perfil);   // MySQL
        jugadores.add(new JugadorDTO(perfil.getNombre(), perfil.getAvatar(), perfil.getColor(), false));
        clientePorJugador.put(perfil.getNombre(), idCliente);
        log(perfil.getNombre() + " entro con el color " + perfil.getColor().getNombre()
                + " (" + jugadores.size() + "/" + SalaDTO.MAXIMO_JUGADORES + ")");
    }

    public void confirmarListo(String nombre) throws SalaException {
        int i = buscar(nombre);
        JugadorDTO j = jugadores.get(i);
        jugadores.set(i, new JugadorDTO(j.getNombre(), j.getAvatar(), j.getColor(), true));
        log(nombre + " esta listo");

        if (jugadores.size() >= SalaDTO.MINIMO_JUGADORES && todosListos()) {
            partidaIniciada = true;
            log("Todos listos: la partida comienza");
        }
    }

    public void salir(String nombre) throws SalaException {
        int i = buscar(nombre);
        String nombreGuardado = jugadores.get(i).getNombre();
        jugadores.remove(i);
        clientePorJugador.remove(nombreGuardado);
        log(nombre + " salio de la sala");
        reiniciarSiQuedoVacia();
    }

    /** El broker aviso que un cliente se desconecto: si estaba en la sala, se le saca. */
    public boolean quitarPorCliente(String idCliente) {
        for (Map.Entry<String, String> e : clientePorJugador.entrySet()) {
            if (e.getValue().equals(idCliente)) {
                String nombre = e.getKey();
                jugadores.removeIf(j -> j.getNombre().equals(nombre));
                clientePorJugador.remove(nombre);
                log(nombre + " se desconecto y se saco de la sala");
                reiniciarSiQuedoVacia();
                return true;
            }
        }
        return false;
    }

    public SalaDTO crearSalaDTO() {
        return new SalaDTO(jugadores, partidaIniciada);
    }

    // ---------------- apoyo ----------------

    private int buscar(String nombre) throws SalaException {
        for (int i = 0; i < jugadores.size(); i++) {
            if (jugadores.get(i).getNombre().equalsIgnoreCase(nombre)) {
                return i;
            }
        }
        throw new SalaException("No estás en la sala.");
    }

    private boolean todosListos() {
        for (JugadorDTO j : jugadores) {
            if (!j.isListo()) {
                return false;
            }
        }
        return true;
    }

    /** Cuando ya no queda nadie, la sala se vuelve a abrir para una nueva partida. */
    private void reiniciarSiQuedoVacia() {
        if (jugadores.isEmpty() && partidaIniciada) {
            partidaIniciada = false;
            log("La sala quedo vacia, se abre de nuevo");
        }
    }

    private static void log(String texto) {
        System.out.println("[SalaBO] " + texto);
    }
}
