package parchis.dtos;

/**
 * Los cuatro colores de fichas del Parchis.
 * Cada jugador elige uno y no se puede repetir dentro de la sala.
 */
public enum ColorFicha {
    AMARILLO("Amarillo"),
    ROJO("Rojo"),
    VERDE("Verde"),
    AZUL("Azul");

    private final String nombre;

    ColorFicha(String nombre) {
        this.nombre = nombre;
    }

    /** Nombre bonito para mostrar en pantalla (ej. "Rojo"). */
    public String getNombre() {
        return nombre;
    }
}
