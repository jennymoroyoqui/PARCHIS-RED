package parchis.presentacion;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JPanel;
import parchis.dtos.ColorFicha;
import parchis.dtos.JugadorDTO;
import parchis.dtos.SalaDTO;

/**
 * Dibuja el tablero en forma de cruz (rejilla de 19 x 19 casillas, la misma
 * geometria que usamos en la Unidad I): bases de 8 x 8 en las esquinas,
 * brazos de 3 casillas de ancho y el centro con los 4 triangulos.
 *
 * Solo dibuja: las fichas de los colores con jugador aparecen en su base
 * y los colores sin jugador quedan inactivos (sin fichas).
 */
class PanelTablero extends JPanel {

    private static final int N = 19;
    private static final double MARGEN = 2.2;   // casillas extra arriba y abajo para los nombres

    private final SalaDTO sala;

    // escala (pixeles por casilla) y origen; se recalculan en cada paint
    private double s;
    private double ox;
    private double oy;

    PanelTablero(SalaDTO sala) {
        this.sala = sala;
        setBackground(Estilo.FONDO);
        setPreferredSize(new Dimension(600, 680));
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        s = Math.min(getWidth() / (N + 1.0), getHeight() / (N + 2 * MARGEN));
        ox = (getWidth() - N * s) / 2;
        oy = (getHeight() - (N + 2 * MARGEN) * s) / 2 + MARGEN * s;

        // Marco
        g.setColor(Estilo.TARJETA);
        g.fill(new RoundRectangle2D.Double(x(-0.3), y(-0.3), (N + 0.6) * s, (N + 0.6) * s, s, s));

        // Casillas grises de los 4 brazos
        for (int fila = 0; fila < N; fila++) {
            for (int col = 0; col < N; col++) {
                boolean brazoVertical = col >= 8 && col <= 10;
                boolean brazoHorizontal = fila >= 8 && fila <= 10;
                if (brazoVertical != brazoHorizontal) {
                    casilla(g, col, fila, new Color(245, 245, 245));
                }
            }
        }

        // Pasillo de meta y casilla de salida (con estrella) de cada color
        for (ColorFicha color : ColorFicha.values()) {
            for (int k = 0; k <= 7; k++) {
                Point p = pasillo(color, k);
                casilla(g, p.x, p.y, Estilo.colorDe(color));
            }
            Point salida = salida(color);
            casilla(g, salida.x, salida.y, Estilo.colorDe(color));
            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, (int) (s * 0.6)));
            g.drawString("★", (float) (x(salida.x) + 0.2 * s), (float) (y(salida.y) + 0.75 * s));
        }

        // Centro: 4 triangulos
        triangulo(g, ColorFicha.AMARILLO, 8, 8, 11, 8);
        triangulo(g, ColorFicha.ROJO, 11, 8, 11, 11);
        triangulo(g, ColorFicha.AZUL, 11, 11, 8, 11);
        triangulo(g, ColorFicha.VERDE, 8, 11, 8, 8);

        // Bases con sus fichas
        for (ColorFicha color : ColorFicha.values()) {
            dibujarBase(g, color, jugadorDe(color) != null);
        }

        // Nombre y avatar de cada jugador junto a su base
        for (JugadorDTO j : sala.getJugadores()) {
            dibujarEtiqueta(g, j);
        }
        g.dispose();
    }

    // ---------------- geometria (en casillas) ----------------

    private double x(double casillas) {
        return ox + casillas * s;
    }

    private double y(double casillas) {
        return oy + casillas * s;
    }

    private static Point origenBase(ColorFicha c) {
        return switch (c) {
            case AMARILLO -> new Point(0, 0);
            case ROJO -> new Point(11, 0);
            case VERDE -> new Point(0, 11);
            case AZUL -> new Point(11, 11);
        };
    }

    /** Carril de color que lleva a la meta. k = 0 es la orilla, k = 7 junto al centro. */
    private static Point pasillo(ColorFicha c, int k) {
        return switch (c) {
            case AMARILLO -> new Point(9, k);
            case VERDE -> new Point(k, 9);
            case AZUL -> new Point(9, 18 - k);
            case ROJO -> new Point(18 - k, 9);
        };
    }

    /** Casilla donde aparece la ficha al salir de la base (la de la estrella). */
    private static Point salida(ColorFicha c) {
        return switch (c) {
            case AMARILLO -> new Point(8, 4);
            case VERDE -> new Point(4, 10);
            case AZUL -> new Point(10, 14);
            case ROJO -> new Point(14, 8);
        };
    }

    private JugadorDTO jugadorDe(ColorFicha color) {
        for (JugadorDTO j : sala.getJugadores()) {
            if (j.getColor() == color) {
                return j;
            }
        }
        return null;
    }

    // ---------------- dibujo ----------------

    private void casilla(Graphics2D g, int col, int fila, Color relleno) {
        Rectangle2D r = new Rectangle2D.Double(x(col), y(fila), s, s);
        g.setColor(relleno);
        g.fill(r);
        g.setColor(new Color(140, 140, 140));
        g.setStroke(new BasicStroke(1f));
        g.draw(r);
    }

    private void triangulo(Graphics2D g, ColorFicha color, double x1, double y1, double x2, double y2) {
        Path2D t = new Path2D.Double();
        t.moveTo(x(x1), y(y1));
        t.lineTo(x(x2), y(y2));
        t.lineTo(x(9.5), y(9.5));
        t.closePath();
        g.setColor(Estilo.colorDe(color));
        g.fill(t);
    }

    private void dibujarBase(Graphics2D g, ColorFicha color, boolean activo) {
        Point o = origenBase(color);
        Color c = Estilo.colorDe(color);
        RoundRectangle2D base = new RoundRectangle2D.Double(x(o.x + 0.2), y(o.y + 0.2), 7.6 * s, 7.6 * s, s, s);
        g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), activo ? 90 : 45));
        g.fill(base);
        g.setColor(activo ? c : new Color(c.getRed(), c.getGreen(), c.getBlue(), 130));
        g.setStroke(new BasicStroke(activo ? 3f : 2f));
        g.draw(base);
        if (!activo) {
            return;   // color sin jugador: queda inactivo
        }
        for (int id = 1; id <= 4; id++) {
            double cx = o.x + ((id == 2 || id == 4) ? 5.5 : 2.5);
            double cy = o.y + ((id == 3 || id == 4) ? 5.5 : 2.5);
            dibujarFicha(g, color, id, cx, cy);
        }
    }

    /** Ficha con su color y su identificador (1 a 4), como pide el enunciado. */
    private void dibujarFicha(Graphics2D g, ColorFicha color, int id, double cx, double cy) {
        double r = 0.8 * s;
        Ellipse2D ficha = new Ellipse2D.Double(x(cx) - r, y(cy) - r, 2 * r, 2 * r);
        g.setColor(Estilo.colorDe(color));
        g.fill(ficha);
        g.setColor(Estilo.colorDe(color).darker().darker());
        g.setStroke(new BasicStroke(2f));
        g.draw(ficha);
        g.setColor(color == ColorFicha.AMARILLO ? Color.BLACK : Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, (int) (s * 0.8)));
        FontMetrics fm = g.getFontMetrics();
        String texto = String.valueOf(id);
        g.drawString(texto, (float) (x(cx) - fm.stringWidth(texto) / 2.0), (float) (y(cy) + fm.getAscent() / 2.0 - 2));
    }

    private void dibujarEtiqueta(Graphics2D g, JugadorDTO j) {
        boolean arriba = j.getColor() == ColorFicha.AMARILLO || j.getColor() == ColorFicha.ROJO;
        boolean izquierda = j.getColor() == ColorFicha.AMARILLO || j.getColor() == ColorFicha.VERDE;
        double ancho = 7.0;
        double alto = 1.7;
        double ex = izquierda ? 0 : N - ancho;
        double ey = arriba ? -MARGEN + 0.1 : N + 0.4;

        RoundRectangle2D caja = new RoundRectangle2D.Double(x(ex), y(ey), ancho * s, alto * s, s, s);
        g.setColor(Color.WHITE);
        g.fill(caja);
        g.setColor(Estilo.colorDe(j.getColor()));
        g.setStroke(new BasicStroke(3f));
        g.draw(caja);

        int tamAvatar = (int) (1.4 * s);
        Avatares.icono(j.getAvatar(), tamAvatar, false)
                .paintIcon(this, g, (int) (x(ex) + 0.2 * s), (int) (y(ey) + 0.15 * s));

        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.BOLD, (int) (s * 0.75)));
        g.drawString(j.getNombre(), (float) (x(ex) + 1.9 * s), (float) (y(ey) + 1.15 * s));
    }
}
