package parchis.presentacion;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import javax.swing.Icon;

/**
 * Los 4 avatares que se pueden elegir. Se dibujan con figuras sencillas
 * para no depender de imagenes (si quieren, despues se cambian por PNGs).
 */
final class Avatares {

    static final String[] NOMBRES = {"Ninja", "Pirata", "Hechicera", "Vaquero"};

    private static final Color PIEL = new Color(241, 194, 125);

    private Avatares() {
    }

    static Icon icono(String avatar, int tam, boolean marcado) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g0, int x, int y) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.translate(x, y);
                dibujar(g, avatar, tam);
                // contorno (negro y grueso si esta marcado)
                g.setColor(marcado ? Color.BLACK : Color.GRAY);
                g.setStroke(new BasicStroke(marcado ? 4f : 1.5f));
                g.draw(new Ellipse2D.Double(2, 2, tam - 4, tam - 4));
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

    private static void dibujar(Graphics2D g, String avatar, double t) {
        Ellipse2D circulo = new Ellipse2D.Double(2, 2, t - 4, t - 4);
        g.setColor(Color.WHITE);
        g.fill(circulo);
        g.setClip(circulo);

        String a = avatar == null ? "" : avatar;
        Color ropa = switch (a) {
            case "Ninja" -> new Color(30, 30, 30);
            case "Pirata" -> new Color(40, 110, 100);
            case "Hechicera" -> new Color(90, 60, 150);
            default -> new Color(150, 90, 40);
        };

        // Lo que va DETRAS de la cabeza
        if (a.equals("Hechicera")) {   // capucha
            g.setColor(ropa);
            g.fill(new Ellipse2D.Double(t * 0.22, t * 0.12, t * 0.56, t * 0.66));
        }

        // Hombros y cabeza
        g.setColor(ropa);
        g.fill(new Ellipse2D.Double(t * 0.16, t * 0.70, t * 0.68, t * 0.55));
        g.setColor(PIEL);
        g.fill(new Ellipse2D.Double(t * 0.31, t * 0.24, t * 0.38, t * 0.42));

        // Lo que va ENCIMA de la cabeza
        switch (a) {
            case "Ninja" -> {
                g.setColor(ropa);   // mascara: tapa todo menos los ojos
                g.fill(new Rectangle2D.Double(t * 0.29, t * 0.22, t * 0.42, t * 0.13));
                g.fill(new Rectangle2D.Double(t * 0.29, t * 0.46, t * 0.42, t * 0.22));
                g.setColor(new Color(220, 30, 30));   // banda roja
                g.fill(new Rectangle2D.Double(t * 0.29, t * 0.30, t * 0.42, t * 0.05));
            }
            case "Pirata" -> {
                g.setColor(new Color(25, 25, 25));   // sombrero
                g.fill(new Ellipse2D.Double(t * 0.20, t * 0.12, t * 0.60, t * 0.26));
                g.fill(new Rectangle2D.Double(t * 0.16, t * 0.27, t * 0.68, t * 0.05));
                g.setColor(Color.WHITE);
                g.fill(new Ellipse2D.Double(t * 0.46, t * 0.17, t * 0.08, t * 0.08));
            }
            case "Vaquero" -> {
                g.setColor(new Color(120, 70, 30));   // sombrero
                g.fill(new Rectangle2D.Double(t * 0.32, t * 0.10, t * 0.36, t * 0.18));
                g.fill(new Ellipse2D.Double(t * 0.14, t * 0.24, t * 0.72, t * 0.09));
            }
            default -> {
                // Hechicera: ya tiene su capucha
            }
        }

        // Ojos
        g.setColor(new Color(40, 40, 40));
        g.fill(new Ellipse2D.Double(t * 0.40, t * 0.39, t * 0.06, t * 0.06));
        g.fill(new Ellipse2D.Double(t * 0.54, t * 0.39, t * 0.06, t * 0.06));
        g.setClip(null);
    }
}
