package parchis.dtos;

/**
 * Nombres de los canales del broker y de los tipos de mensaje que usan
 * la FachadaSala (en cada computadora) y el ServidorSala.
 * Estan aqui para que los dos lados usen exactamente los mismos textos.
 *
 *   Cliente --(sala.peticiones)--> Broker --> ServidorSala
 *   ServidorSala --(cliente.<id>)--> Broker --> ese cliente   (la respuesta)
 *   ServidorSala --(sala.eventos)--> Broker --> TODOS         (la sala actualizada)
 */
public final class ProtocoloSala {

    // Canales
    public static final String CANAL_PETICIONES = "sala.peticiones";
    public static final String CANAL_EVENTOS = "sala.eventos";
    public static final String PREFIJO_CANAL_CLIENTE = "cliente.";

    // Tipos de mensaje que manda el cliente
    public static final String ENTRAR = "ENTRAR";
    public static final String LISTO = "LISTO";
    public static final String SALIR = "SALIR";

    // Tipos de mensaje que manda el servidor
    public static final String RESPUESTA = "RESPUESTA";
    public static final String SALA_ACTUALIZADA = "SALA_ACTUALIZADA";

    private ProtocoloSala() {
        // solo constantes, no se crean objetos de esta clase
    }
}
