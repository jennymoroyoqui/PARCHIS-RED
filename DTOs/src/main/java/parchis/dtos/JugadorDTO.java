package parchis.dtos;

import java.io.Serializable;

/**
 * Un jugador que ya esta dentro de la sala de espera.
 * Es lo que se dibuja en cada tarjeta de la pantalla "Sala de espera".
 */
public class JugadorDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String nombre;
    private final String avatar;
    private final ColorFicha color;
    private final boolean listo;

    public JugadorDTO(String nombre, String avatar, ColorFicha color, boolean listo) {
        this.nombre = nombre;
        this.avatar = avatar;
        this.color = color;
        this.listo = listo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getAvatar() {
        return avatar;
    }

    public ColorFicha getColor() {
        return color;
    }

    /** true cuando el jugador ya presiono "ESTOY LISTO". */
    public boolean isListo() {
        return listo;
    }
}
