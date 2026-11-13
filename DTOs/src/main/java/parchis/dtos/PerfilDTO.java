package parchis.dtos;

import java.io.Serializable;

/**
 * Lo que el jugador captura en la pantalla "Crea tu perfil de juego":
 * nombre de usuario, avatar y color de fichas.
 *
 * Es Serializable porque tambien viaja por la red (cliente -> broker -> servidor).
 */
public class PerfilDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String nombre;
    private final String avatar;
    private final ColorFicha color;

    public PerfilDTO(String nombre, String avatar, ColorFicha color) {
        this.nombre = nombre;
        this.avatar = avatar;
        this.color = color;
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
}
