package parchis.servidorsala;

import java.io.IOException;
import java.io.Serializable;
import parchis.broker.ClienteBroker;
import parchis.broker.IReceptorMensajes;
import parchis.broker.Mensaje;
import parchis.broker.ServidorBroker;
import parchis.dtos.PerfilDTO;
import parchis.dtos.ProtocoloSala;
import parchis.dtos.RespuestaDTO;
import parchis.interfaces.SalaException;

/**
 * Recibe las peticiones que llegan por el broker, se las pasa a SalaBO
 * y publica el resultado:
 *   - la respuesta SOLO al cliente que pregunto   (canal "cliente.<id>")
 *   - la sala actualizada a TODOS los jugadores    (canal "sala.eventos")
 */
public class ServicioSala implements IReceptorMensajes {

    private final ClienteBroker cliente;
    private final SalaBO sala;

    public ServicioSala(ClienteBroker cliente, SalaBO sala) {
        this.cliente = cliente;
        this.sala = sala;
    }

    public void iniciar(String hostBroker, int puertoBroker) throws IOException {
        cliente.setReceptor(this);
        cliente.conectar(hostBroker, puertoBroker);
        cliente.suscribir(ProtocoloSala.CANAL_PETICIONES);
        cliente.suscribir(ServidorBroker.CANAL_DESCONEXIONES);
    }

    /** synchronized: se atiende una peticion a la vez, asi dos jugadores no ganan el mismo color. */
    @Override
    public synchronized void mensajeRecibido(Mensaje mensaje) {
        // Un jugador cerro su programa sin presionar SALIR
        if (ServidorBroker.DESCONECTADO.equals(mensaje.getTipo())) {
            if (sala.quitarPorCliente((String) mensaje.leerDatos())) {
                publicarSala();
            }
            return;
        }

        RespuestaDTO respuesta;
        try {
            switch (mensaje.getTipo()) {
                case ProtocoloSala.ENTRAR -> sala.entrar((PerfilDTO) mensaje.leerDatos(), mensaje.getRemitente());
                case ProtocoloSala.LISTO -> sala.confirmarListo((String) mensaje.leerDatos());
                case ProtocoloSala.SALIR -> sala.salir((String) mensaje.leerDatos());
                default -> {
                    System.out.println("[ServicioSala] Peticion desconocida: " + mensaje);
                    return;
                }
            }
            respuesta = RespuestaDTO.exito(sala.crearSalaDTO());
        } catch (SalaException e) {
            respuesta = RespuestaDTO.error(e.getMessage());
            System.out.println("[ServicioSala] Rechazado (" + mensaje.getTipo() + "): " + e.getMessage());
        }

        publicar(ProtocoloSala.PREFIJO_CANAL_CLIENTE + mensaje.getRemitente(), ProtocoloSala.RESPUESTA, respuesta);
        if (respuesta.isExito()) {
            publicarSala();
        }
    }

    @Override
    public void conexionPerdida(String motivo) {
        System.out.println("[ServicioSala] Se perdio la conexion con el broker: " + motivo);
        System.out.println("[ServicioSala] Vuelve a correr MainBroker y despues este servidor.");
        System.exit(1);
    }

    private void publicarSala() {
        publicar(ProtocoloSala.CANAL_EVENTOS, ProtocoloSala.SALA_ACTUALIZADA, sala.crearSalaDTO());
    }

    private void publicar(String canal, String tipo, Serializable datos) {
        try {
            cliente.publicar(canal, tipo, datos);
        } catch (IOException e) {
            System.out.println("[ServicioSala] No se pudo publicar en " + canal + ": " + e.getMessage());
        }
    }
}
