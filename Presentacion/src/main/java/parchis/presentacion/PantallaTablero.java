package parchis.presentacion;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import parchis.dtos.SalaDTO;

/**
 * Pantalla 4 del storyboard: "La partida ha comenzado".
 * Es el final del CU-01: se ve el tablero con las fichas de cada jugador en su base.
 * (Mover fichas ya es otro caso de uso: Ejecutar turno.)
 */
public class PantallaTablero extends JFrame {

    public PantallaTablero(SalaDTO sala) {
        super("Parchís - Partida");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel encabezado = new JPanel(new GridLayout(2, 1));
        encabezado.setOpaque(false);
        encabezado.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        encabezado.add(Estilo.etiqueta("PARCHÍS", Estilo.TITULO, Color.BLACK));
        encabezado.add(Estilo.etiqueta("La partida ha comenzado", Estilo.SUBTITULO, Estilo.AZUL_TEXTO));

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(Estilo.FONDO);
        raiz.add(encabezado, BorderLayout.NORTH);
        raiz.add(new PanelTablero(sala), BorderLayout.CENTER);
        setContentPane(raiz);

        pack();
        setLocationRelativeTo(null);
    }
}
