package parchis.presentacion;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import parchis.dtos.ColorFicha;

/**
 * Pantalla 1 del storyboard: "CREA TU PERFIL DE JUEGO".
 * Solo junta los datos y se los pasa a ControlSala. No valida reglas ni habla con la fachada.
 */
public class PantallaPerfil extends JFrame {

    private static final int ANCHO_TEXTO = 300;

    private final ControlSala control;
    private final JTextField txtNombre = new JTextField(16);
    private final JLabel lblMensaje = Estilo.etiqueta(" ", Estilo.NORMAL, Color.DARK_GRAY);

    private String avatarElegido;        // null = todavia no elige
    private ColorFicha colorElegido;     // null = todavia no elige

    public PantallaPerfil(ControlSala control) {
        super("Parchís - Ingresar al juego");
        this.control = control;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        construir();
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void construir() {
        PanelRedondeado tarjeta = new PanelRedondeado(Estilo.TARJETA, null, 24);
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        tarjeta.add(Estilo.etiqueta("CREA TU PERFIL DE JUEGO", Estilo.TITULO, Color.BLACK));
        tarjeta.add(Box.createVerticalStrut(16));

        // Nombre
        tarjeta.add(Estilo.etiqueta("NOMBRE DE USUARIO", Estilo.SUBTITULO, Estilo.AZUL_TEXTO));
        tarjeta.add(Box.createVerticalStrut(4));
        txtNombre.setFont(Estilo.SUBTITULO);
        txtNombre.setMaximumSize(new Dimension(260, 30));
        txtNombre.setAlignmentX(Component.CENTER_ALIGNMENT);
        tarjeta.add(txtNombre);
        tarjeta.add(Box.createVerticalStrut(14));

        // Avatares
        tarjeta.add(Estilo.etiqueta("ELIGE TU AVATAR", Estilo.SUBTITULO, Estilo.AZUL_TEXTO));
        JPanel filaAvatares = filaTransparente();
        ButtonGroup grupoAvatares = new ButtonGroup();
        for (String avatar : Avatares.NOMBRES) {
            JToggleButton b = Estilo.seleccionable(Avatares.icono(avatar, 64, false), Avatares.icono(avatar, 64, true));
            b.setToolTipText(avatar);
            b.addActionListener(e -> avatarElegido = avatar);
            grupoAvatares.add(b);
            filaAvatares.add(b);
        }
        tarjeta.add(filaAvatares);
        tarjeta.add(Box.createVerticalStrut(8));

        // Colores
        tarjeta.add(Estilo.etiqueta("SELECCIONA TU COLOR", Estilo.SUBTITULO, Estilo.AZUL_TEXTO));
        JPanel filaColores = filaTransparente();
        ButtonGroup grupoColores = new ButtonGroup();
        for (ColorFicha color : ColorFicha.values()) {
            Color c = Estilo.colorDe(color);
            JToggleButton b = Estilo.seleccionable(Estilo.circulo(c, 60, false), Estilo.circulo(c, 60, true));
            b.setToolTipText(color.getNombre());
            b.addActionListener(e -> {
                colorElegido = color;
                mostrarInfo("Elegiste el color " + color.getNombre().toLowerCase() + ".");
            });
            grupoColores.add(b);
            filaColores.add(b);
        }
        tarjeta.add(filaColores);
        tarjeta.add(Box.createVerticalStrut(8));

        // Mensajes (errores de A1, A2, A3, A4)
        lblMensaje.setPreferredSize(new Dimension(ANCHO_TEXTO + 60, 54));
        lblMensaje.setMaximumSize(new Dimension(ANCHO_TEXTO + 60, 54));
        tarjeta.add(lblMensaje);
        tarjeta.add(Box.createVerticalStrut(8));

        JButton btnContinuar = Estilo.boton("CONTINUAR");
        btnContinuar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnContinuar.addActionListener(e -> control.continuar(txtNombre.getText(), avatarElegido, colorElegido));
        getRootPane().setDefaultButton(btnContinuar);   // Enter = Continuar
        tarjeta.add(btnContinuar);

        JPanel fondo = new JPanel(new GridBagLayout());
        fondo.setBackground(Estilo.FONDO);
        fondo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        fondo.add(tarjeta);
        setContentPane(fondo);
    }

    private JPanel filaTransparente() {
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
        fila.setOpaque(false);
        fila.setAlignmentX(Component.CENTER_ALIGNMENT);
        return fila;
    }

    /** Lo usa ControlSala para avisar errores (A1 a A4). */
    void mostrarError(String mensaje) {
        lblMensaje.setForeground(Estilo.ROJO_ERROR);
        lblMensaje.setText(Estilo.centrado(mensaje, ANCHO_TEXTO));
    }

    private void mostrarInfo(String mensaje) {
        lblMensaje.setForeground(Color.DARK_GRAY);
        lblMensaje.setText(Estilo.centrado(mensaje, ANCHO_TEXTO));
    }
}
