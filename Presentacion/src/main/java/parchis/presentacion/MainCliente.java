package parchis.presentacion;

import javax.swing.SwingUtilities;
import parchis.fabrica.FabricaFachadas;
import parchis.interfaces.IFachadaSala;

/**
 * Punto de entrada del juego (lo que corre cada jugador en su computadora).
 * Fijate que aqui NO se nombra ni al mock ni a la fachada real: se le pide a la fabrica.
 */
public class MainCliente {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            IFachadaSala fachada = FabricaFachadas.crearFachadaSala();
            ControlSala control = new ControlSala(fachada);
            control.iniciar();
        });
    }
}
