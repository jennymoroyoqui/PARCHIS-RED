package parchis.broker;

/** Lo implementa quien quiere recibir los mensajes que le llegan del broker. */
public interface IReceptorMensajes {

    void mensajeRecibido(Mensaje mensaje);

    void conexionPerdida(String motivo);
}
