package parchis.presentacion;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import parchis.dtos.JugadorDTO;
import parchis.dtos.SalaDTO;

/**
 * Pantallas 2 y 3 del storyboard: "SALA DE ESPERA".
 * Muestra a los jugadores (2/4), quien ya esta listo, y la cuenta regresiva.
 */
public class PantallaSalaEspera extends JFrame {

    private static final int SEGUNDOS_CUENTA = 3;
    private static final int ANCHO_TEXTO = 420;

    private final ControlSala control;
    private final JLabel lblEstado = Estilo.etiqueta(" ", Estilo.SUBTITULO, Estilo.AZUL_TEXTO);
    private final JLabel lblDetalle = Estilo.etiqueta(" ", Estilo.NORMAL, Color.DARK_GRAY);
    private final JLabel lblContador = Estilo.etiqueta("0/4", Estilo.SUBTITULO, Estilo.AZUL_TEXTO);
    private final JPanel panelJugadores = new JPanel(new GridLayout(2, 2, 16, 16));
    private final JButton btnListo = Estilo.boton("ESTOY LISTO");
    private final JButton btnSalir = Estilo.boton("SALIR");

    private boolean contando = false;
    private int segundos;

    public PantallaSalaEspera(ControlSala control) {
        super("Parchís - Sala de espera");
        this.control = control;
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                control.cerrarAplicacion();
            }
        });
        construir();
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void construir() {
        PanelRedondeado tarjeta = new PanelRedondeado(Estilo.TARJETA, null, 24);
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        tarjeta.add(Estilo.etiqueta("SALA DE ESPERA", Estilo.TITULO, Color.BLACK));
        tarjeta.add(Box.createVerticalStrut(8));
        tarjeta.add(lblEstado);
        tarjeta.add(lblDetalle);
        tarjeta.add(lblContador);
        tarjeta.add(Box.createVerticalStrut(14));

        panelJugadores.setOpaque(false);
        tarjeta.add(panelJugadores);
        tarjeta.add(Box.createVerticalStrut(16));

        btnListo.addActionListener(e -> control.confirmarListo());
        btnSalir.addActionListener(e -> control.salir());
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        botones.setOpaque(false);
        botones.add(btnListo);
        botones.add(btnSalir);
        tarjeta.add(botones);

        JPanel fondo = new JPanel(new GridBagLayout());
        fondo.setBackground(Estilo.FONDO);
        fondo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        fondo.add(tarjeta);
        setContentPane(fondo);

        // Se dibuja vacia para que pack() calcule bien el tamano
        for (int i = 0; i < SalaDTO.MAXIMO_JUGADORES; i++) {
            panelJugadores.add(tarjetaVacia());
        }
        lblEstado.setText(Estilo.centrado("Esperando más jugadores para empezar la partida", ANCHO_TEXTO));
        lblDetalle.setText(Estilo.centrado(" ", ANCHO_TEXTO));
    }

    /** Redibuja la sala. Lo llama ControlSala cada vez que la fachada avisa un cambio. */
    void mostrarSala(SalaDTO sala, String nombreLocal) {
        lblContador.setText(sala.getNumeroJugadores() + "/" + SalaDTO.MAXIMO_JUGADORES);

        panelJugadores.removeAll();
        boolean localListo = false;
        for (JugadorDTO j : sala.getJugadores()) {
            boolean esLocal = j.getNombre().equalsIgnoreCase(nombreLocal);
            if (esLocal) {
                localListo = j.isListo();
            }
            panelJugadores.add(tarjetaJugador(j, esLocal));
        }
        for (int i = sala.getNumeroJugadores(); i < SalaDTO.MAXIMO_JUGADORES; i++) {
            panelJugadores.add(tarjetaVacia());
        }

        if (!contando) {
            lblDetalle.setForeground(Color.DARK_GRAY);
            if (sala.isPartidaIniciada()) {
                ponerTextos("¡TODOS LOS JUGADORES LISTOS!", " ");
            } else if (sala.getNumeroJugadores() < SalaDTO.MINIMO_JUGADORES) {
                ponerTextos("Esperando más jugadores para empezar la partida",
                        "Se necesitan al menos " + SalaDTO.MINIMO_JUGADORES + " jugadores");
            } else {
                ponerTextos("Esperando a que todos estén listos",
                        "La partida empieza cuando todos presionen ESTOY LISTO");
            }
        }

        btnListo.setEnabled(!localListo && !sala.isPartidaIniciada());
        btnSalir.setEnabled(!sala.isPartidaIniciada());
        panelJugadores.revalidate();
        panelJugadores.repaint();
    }

    /** Pantalla 3 del storyboard: "La partida comenzara en 3...". */
    void iniciarCuentaRegresiva(Runnable alTerminar) {
        contando = true;
        segundos = SEGUNDOS_CUENTA;
        lblDetalle.setForeground(Estilo.ROJO_ERROR);
        ponerTextos("¡TODOS LOS JUGADORES LISTOS!", "La partida comenzará en " + segundos + "...");
        Timer timer = new Timer(1000, null);
        timer.addActionListener(e -> {
            segundos--;
            if (segundos > 0) {
                lblDetalle.setText(Estilo.centrado("La partida comenzará en " + segundos + "...", ANCHO_TEXTO));
            } else {
                timer.stop();
                alTerminar.run();
            }
        });
        timer.start();
    }

    void mostrarError(String mensaje) {
        lblDetalle.setForeground(Estilo.ROJO_ERROR);
        lblDetalle.setText(Estilo.centrado(mensaje, ANCHO_TEXTO));
    }

    private void ponerTextos(String estado, String detalle) {
        lblEstado.setText(Estilo.centrado(estado, ANCHO_TEXTO));
        lblDetalle.setText(Estilo.centrado(detalle, ANCHO_TEXTO));
    }

    // -------- tarjetas de jugador --------

    private JPanel tarjetaJugador(JugadorDTO j, boolean esLocal) {
        PanelRedondeado t = new PanelRedondeado(Color.WHITE, esLocal ? Color.DARK_GRAY : null, 16);
        t.setLayout(new BorderLayout(10, 0));
        t.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        t.setPreferredSize(new Dimension(240, 72));

        t.add(new JLabel(Avatares.icono(j.getAvatar(), 52, false)), BorderLayout.WEST);

        JPanel textos = new JPanel(new GridLayout(2, 1));
        textos.setOpaque(false);
        JLabel nombre = new JLabel(j.getNombre() + (esLocal ? " (tú)" : ""));
        nombre.setFont(Estilo.SUBTITULO.deriveFont(15f));
        JLabel equipo = new JLabel("EQUIPO " + j.getColor().getNombre().toUpperCase());
        equipo.setFont(Estilo.SUBTITULO.deriveFont(11f));
        equipo.setForeground(Estilo.colorTexto(j.getColor()));
        textos.add(nombre);
        textos.add(equipo);
        t.add(textos, BorderLayout.CENTER);

        if (j.isListo()) {
            t.add(new JLabel(Estilo.palomita(30)), BorderLayout.EAST);
        }
        return t;
    }

    private JPanel tarjetaVacia() {
        PanelRedondeado t = new PanelRedondeado(Color.WHITE, null, 16);
        t.setLayout(new BorderLayout());
        t.setPreferredSize(new Dimension(240, 72));
        JLabel texto = new JLabel("ESPERANDO JUGADOR...", JLabel.CENTER);
        texto.setFont(Estilo.SUBTITULO.deriveFont(12f));
        texto.setForeground(Color.GRAY);
        t.add(texto, BorderLayout.CENTER);
        return t;
    }
}
