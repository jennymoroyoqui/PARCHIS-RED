package parchis.salamock;

import java.util.ArrayList;
import java.util.List;
import parchis.dtos.ColorFicha;
import parchis.dtos.JugadorDTO;
import parchis.dtos.PerfilDTO;
import parchis.dtos.SalaDTO;
import parchis.interfaces.IFachadaSala;
import parchis.interfaces.IObservadorSala;
import parchis.interfaces.SalaException;

/**
 * Mock de la fachada de la sala (ejercicio 6 / Hito 2A).
 *
 * - Guarda los datos en memoria (una lista), no usa red ni base de datos.
 * - Para poder probar en UNA sola computadora, simula que "Sofia" ya entro
 *   desde otra computadora con el color ROJO y que ya esta lista.
 * - Falla igual que la fachada real en los flujos alternos:
 *     A2: el nombre ya esta en uso   (prueba: escribe "Sofia")
 *     A3: el color ya esta ocupado   (prueba: elige el color Rojo)
 *     A4: la partida ya comenzo
 */
public class FachadaSalaMock implements IFachadaSala {

    private final List<JugadorDTO> jugadores = new ArrayList<>();
    private final List<IObservadorSala> observadores = new ArrayList<>();
    private boolean partidaIniciada = false;

    public FachadaSalaMock() {
        // Jugador simulado "de otra computadora"
        jugadores.add(new JugadorDTO("Sofia", "Pirata", ColorFicha.ROJO, true));
    }

    @Override
    public SalaDTO entrarASala(PerfilDTO perfil) throws SalaException {
        // A4: ya no se puede entrar
        if (partidaIniciada) {
            throw new SalaException("La partida ya comenzó, ya no puedes entrar a la sala.");
        }
        // A2: nombre repetido
        for (JugadorDTO j : jugadores) {
            if (j.getNombre().equalsIgnoreCase(perfil.getNombre())) {
                throw new SalaException("El nombre \"" + perfil.getNombre() + "\" ya lo usa otro jugador. Escribe otro.");
            }
        }
        // A3: color ocupado (como hay 4 colores, esto tambien evita que entren mas de 4)
        for (JugadorDTO j : jugadores) {
            if (j.getColor() == perfil.getColor()) {
                throw new SalaException("El color " + perfil.getColor().getNombre() + " ya lo eligió otro jugador. Elige otro color.");
            }
        }

        jugadores.add(new JugadorDTO(perfil.getNombre(), perfil.getAvatar(), perfil.getColor(), false));
        notificarObservadores();
        return crearSalaDTO();
    }

    @Override
    public void confirmarListo(String nombreJugador) throws SalaException {
        int i = buscarJugador(nombreJugador);
        JugadorDTO j = jugadores.get(i);
        jugadores.set(i, new JugadorDTO(j.getNombre(), j.getAvatar(), j.getColor(), true));

        if (jugadores.size() >= SalaDTO.MINIMO_JUGADORES && todosListos()) {
            partidaIniciada = true;
        }
        notificarObservadores();
    }

    @Override
    public void salirDeSala(String nombreJugador) throws SalaException {
        jugadores.remove(buscarJugador(nombreJugador));
        notificarObservadores();
    }

    @Override
    public void agregarObservador(IObservadorSala observador) {
        observadores.add(observador);
    }

    // ----------------- metodos de apoyo -----------------

    private int buscarJugador(String nombre) throws SalaException {
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

    private SalaDTO crearSalaDTO() {
        return new SalaDTO(jugadores, partidaIniciada);
    }

    private void notificarObservadores() {
        SalaDTO sala = crearSalaDTO();
        for (IObservadorSala o : observadores) {
            o.salaActualizada(sala);
        }
    }
}
