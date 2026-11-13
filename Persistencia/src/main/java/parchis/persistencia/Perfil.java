package parchis.persistencia;

/**
 * Entidad: un renglon de la tabla "perfiles".
 * (No es un DTO: este objeto solo vive en la capa de datos.)
 */
public class Perfil {

    private int id;
    private String nombre;
    private String avatar;
    private String color;

    public Perfil() {
    }

    public Perfil(String nombre, String avatar, String color) {
        this.nombre = nombre;
        this.avatar = avatar;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
