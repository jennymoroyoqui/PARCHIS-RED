package parchis.presentacion;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import parchis.dtos.ColorFicha;
import parchis.dtos.PerfilDTO;
import parchis.dtos.SalaDTO;
import parchis.interfaces.IFachadaSala;
import parchis.interfaces.IObservadorSala;
import parchis.interfaces.SalaException;

/**
 * Controlador del CU-01 "Ingresar a la sala".
 *
 * - Recibe lo que hace el jugador en las pantallas (continuar, listo, salir).
 * - Se lo pide a la fachada (que solo conoce como IFachadaSala).
 * - Escucha a la fachada (IObservadorSala) para redibujar cuando otro jugador cambia algo.
 *
 * Las pantallas NO hablan con la fachada; siempre pasan por aqui.
 */
public class ControlSala implements IObservadorSala {

    private static final int MAXIMO_LETRAS_NOMBRE = 15;

    private final IFachadaSala fachada;
    private PantallaPerfil pantallaPerfil;
    private PantallaSalaEspera pantallaSala;

    /** Nombre del jugador de ESTA computadora. Es null mientras no entra a la sala. */
    private String nombreLocal;
    private boolean partidaEmpezando = false;

    public ControlSala(IFachadaSala fachada) {
        this.fachada = fachada;
        this.fachada.agregarObservador(this);
    }

    /** Paso 1 del flujo basico: se muestra "Crea tu perfil de juego". */
    public void iniciar() {
        pantallaPerfil = new PantallaPerfil(this);
        pantallaPerfil.setVisible(true);
    }

    /** Pasos 2 a 4 del flujo basico: el jugador presiono CONTINUAR. */
    public void continuar(String nombre, String avatar, ColorFicha color) {
        // A1: datos incompletos -> se avisa y NI SIQUIERA se llama a la fachada
        String error = validarDatos(nombre, avatar, color);
        if (error != null) {
            pantallaPerfil.mostrarError(error);
            return;
        }

        try {
            PerfilDTO perfil = new PerfilDTO(nombre.trim(), avatar, color);
            SalaDTO sala = fachada.entrarASala(perfil);

            nombreLocal = perfil.getNombre();
            partidaEmpezando = false;
            pantallaPerfil.dispose();
            pantallaPerfil = null;

            pantallaSala = new PantallaSalaEspera(this);
            pantallaSala.mostrarSala(sala, nombreLocal);
            pantallaSala.setVisible(true);
        } catch (SalaException e) {
            // A2 (nombre en uso), A3 (color ocupado), A4 (partida iniciada) o sin conexion
            pantallaPerfil.mostrarError(e.getMessage());
        }
    }

    /** Paso 5 del flujo basico: el jugador presiono ESTOY LISTO. */
    public void confirmarListo() {
        try {
            fachada.confirmarListo(nombreLocal);
        } catch (SalaException e) {
            pantallaSala.mostrarError(e.getMessage());
        }
    }

    /** A5: el jugador presiono SALIR en la sala de espera. Regresa a la pantalla de perfil. */
    public void salir() {
        try {
            fachada.salirDeSala(nombreLocal);
        } catch (SalaException e) {
            System.out.println("[ControlSala] No se pudo avisar la salida: " + e.getMessage());
        }
        nombreLocal = null;
        pantallaSala.dispose();
        pantallaSala = null;
        iniciar();
    }

    /** El jugador cerro la ventana de la sala con la X. */
    public void cerrarAplicacion() {
        if (nombreLocal != null) {
            try {
                fachada.salirDeSala(nombreLocal);
            } catch (SalaException e) {
                // ya nos vamos, no importa
            }
        }
        System.exit(0);
    }

    // ---------------- IObservadorSala ----------------

    /**
     * La fachada nos avisa que la sala cambio. Puede llegar desde otro hilo
     * (el que escucha la red), por eso todo lo de Swing va dentro de invokeLater.
     */
    @Override
    public void salaActualizada(SalaDTO sala) {
        SwingUtilities.invokeLater(() -> {
            if (pantallaSala == null || nombreLocal == null) {
                return;   // todavia no entramos a la sala: no hay nada que redibujar
            }
            pantallaSala.mostrarSala(sala, nombreLocal);

            // Pasos 6 y 7: todos listos -> cuenta regresiva -> tablero
            if (sala.isPartidaIniciada() && !partidaEmpezando) {
                partidaEmpezando = true;
                pantallaSala.iniciarCuentaRegresiva(() -> mostrarTablero(sala));
            }
        });
    }

    @Override
    public void conexionPerdida(String motivo) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null,
                "Se perdió la conexión con el servidor.\n" + motivo,
                "Sin conexión", JOptionPane.ERROR_MESSAGE));
    }

    // ---------------- apoyo ----------------

    private String validarDatos(String nombre, String avatar, ColorFicha color) {
        if (nombre == null || nombre.isBlank()) {
            return "Escribe tu nombre de usuario.";
        }
        if (nombre.trim().length() > MAXIMO_LETRAS_NOMBRE) {
            return "El nombre debe tener máximo " + MAXIMO_LETRAS_NOMBRE + " letras.";
        }
        if (avatar == null) {
            return "Elige un avatar.";
        }
        if (color == null) {
            return "Selecciona el color de tus fichas.";
        }
        return null;   // todo bien
    }

    private void mostrarTablero(SalaDTO sala) {
        pantallaSala.dispose();
        pantallaSala = null;
        new PantallaTablero(sala).setVisible(true);
    }
}
