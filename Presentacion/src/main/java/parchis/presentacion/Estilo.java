package parchis.presentacion;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JToggleButton;
import parchis.dtos.ColorFicha;

/**
 * Colores, letras y componentes que usan todas las pantallas,
 * para que se parezcan al storyboard (fondo gris, botones verdes, etc.).
 */
final class Estilo {

    static final Color FONDO = new Color(240, 240, 240);
    static final Color TARJETA = new Color(214, 214, 214);
    static final Color AZUL_TEXTO = new Color(30, 110, 230);
    static final Color VERDE_BOTON = new Color(120, 232, 120);
    static final Color ROJO_ERROR = new Color(200, 30, 30);

    static final Font TITULO = new Font("SansSerif", Font.BOLD, 20);
    static final Font SUBTITULO = new Font("SansSerif", Font.BOLD, 13);
    static final Font NORMAL = new Font("SansSerif", Font.PLAIN, 13);

    private Estilo() {
    }

    /** Color de pantalla de cada color de ficha. */
    static Color colorDe(ColorFicha color) {
        return switch (color) {
            case AMARILLO -> new Color(255, 200, 0);
            case ROJO -> new Color(230, 20, 20);
            case VERDE -> new Color(0, 200, 0);
            case AZUL -> new Color(20, 50, 230);
        };
    }

    /** Igual que colorDe, pero el amarillo un poco mas oscuro para que se lea sobre blanco. */
    static Color colorTexto(ColorFicha color) {
        return color == ColorFicha.AMARILLO ? new Color(200, 150, 0) : colorDe(color);
    }

    static JButton boton(String texto) {
        JButton b = new JButton(texto);
        b.setFont(SUBTITULO);
        b.setBackground(VERDE_BOTON);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 2),
                BorderFactory.createEmptyBorder(6, 22, 6, 22)));
        return b;
    }

    static JLabel etiqueta(String texto, Font fuente, Color color) {
        JLabel l = new JLabel(texto, JLabel.CENTER);
        l.setFont(fuente);
        l.setForeground(color);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }

    /** Texto centrado que se parte en varias lineas si es largo. */
    static String centrado(String texto, int ancho) {
        return "<html><div style='text-align:center;width:" + ancho + "px'>" + texto + "</div></html>";
    }

    /** Boton que se puede "marcar" (avatar o color). Cuando esta marcado usa el icono "marcado". */
    static JToggleButton seleccionable(Icon normal, Icon marcado) {
        JToggleButton b = new JToggleButton(normal);
        b.setSelectedIcon(marcado);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    /** Circulo de color. Si esta "marcado" lleva un anillo negro alrededor. */
    static Icon circulo(Color color, int tam, boolean marcado) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g0, int x, int y) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(color);
                g.fillOval(x + 5, y + 5, tam - 10, tam - 10);
                if (marcado) {
                    g.setColor(Color.BLACK);
                    g.setStroke(new BasicStroke(4f));
                    g.drawOval(x + 3, y + 3, tam - 6, tam - 6);
                }
                g.dispose();
            }

            @Override
            public int getIconWidth() {
                return tam;
            }

            @Override
            public int getIconHeight() {
                return tam;
            }
        };
    }

    /** Cuadrito verde con palomita: el jugador ya esta listo. */
    static Icon palomita(int tam) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g0, int x, int y) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(new Color(110, 200, 70));
                g.fillRoundRect(x, y, tam, tam, 8, 8);
                g.setColor(Color.WHITE);
                g.setStroke(new BasicStroke(tam / 8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.drawPolyline(new int[]{x + tam / 5, x + tam * 2 / 5, x + tam * 4 / 5},
                        new int[]{y + tam / 2, y + tam * 3 / 4, y + tam / 4}, 3);
                g.dispose();
            }

            @Override
            public int getIconWidth() {
                return tam;
            }

            @Override
            public int getIconHeight() {
                return tam;
            }
        };
    }
}
