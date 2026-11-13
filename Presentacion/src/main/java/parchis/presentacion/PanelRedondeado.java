package parchis.presentacion;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/** Panel con esquinas redondeadas (las "tarjetas" grises y blancas del storyboard). */
class PanelRedondeado extends JPanel {

    private final Color fondo;
    private final Color borde;
    private final int radio;

    PanelRedondeado(Color fondo, Color borde, int radio) {
        this.fondo = fondo;
        this.borde = borde;
        this.radio = radio;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(fondo);
        g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radio, radio);
        if (borde != null) {
            g.setColor(borde);
            g.setStroke(new BasicStroke(2f));
            g.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, radio, radio);
        }
        g.dispose();
        super.paintComponent(g0);
    }
}
