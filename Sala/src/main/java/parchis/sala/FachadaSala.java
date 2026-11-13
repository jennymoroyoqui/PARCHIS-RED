package parchis.sala;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import parchis.broker.ClienteBroker;
import parchis.broker.IReceptorMensajes;
import parchis.broker.Mensaje;
import parchis.dtos.PerfilDTO;
import parchis.dtos.ProtocoloSala;
import parchis.dtos.RespuestaDTO;
import parchis.dtos.SalaDTO;
import parchis.interfaces.IFachadaSala;
import parchis.interfaces.IObservadorSala;
import parchis.interfaces.SalaException;

/**
 * Fachada REAL de la sala (Hito 2B). Implementa la MISMA interfaz que el mock,
 * por eso la presentacion no tuvo que cambiar nada.
 *
 * Cada operacion se convierte en un mensaje que se publica en el broker:
 *   1. Publica la peticion en "sala.peticiones".
 *   2. Espera (maximo 5 s) la respuesta del ServidorSala en su canal privado "cliente.<id>".
 *   3. Si la respuesta trae error (A2, A3, A4) lanza SalaException.
 * Ademas escucha "sala.eventos" para avisarle a la presentacion cuando OTRO jugador cambia la sala.
 */
public class FachadaSala implements IFachadaSala, IReceptorMensajes {

    private static final int SEGUNDOS_ESPERA_RESPUESTA = 5;

    private final String hostBroker;
    private final int puertoBroker;
    private final ClienteBroker cliente = new ClienteBroker();
    private final List<IObservadorSala> observadores = new CopyOnWriteArrayList<>();
    private final BlockingQueue<RespuestaDTO> respuestas = new LinkedBlockingQueue<>();
    private boolean conectado = false;

    public FachadaSala(String hostBroker, int puertoBroker) {
        this.hostBroker = hostBroker;
        this.puertoBroker = puertoBroker;
        cliente.setReceptor(this);
    }

    // ---------------- IFachadaSala ----------------

    @Override
    public SalaDTO entrarASala(PerfilDTO perfil) throws SalaException {
        RespuestaDTO respuesta = enviarPeticion(ProtocoloSala.ENTRAR, perfil);
        return respuesta.getSala();
    }

    @Override
    public void confirmarListo(String nombreJugador) throws SalaException {
        enviarPeticion(ProtocoloSala.LISTO, nombreJugador);
    }

    @Override
    public void salirDeSala(String nombreJugador) throws SalaException {
        enviarPeticion(ProtocoloSala.SALIR, nombreJugador);
    }

    @Override
    public void agregarObservador(IObservadorSala observador) {
        observadores.add(observador);
    }

    // ---------------- IReceptorMensajes (lo que llega del broker) ----------------

    @Override
    public void mensajeRecibido(Mensaje mensaje) {
        switch (mensaje.getTipo()) {
            case ProtocoloSala.RESPUESTA -> respuestas.offer((RespuestaDTO) mensaje.leerDatos());
            case ProtocoloSala.SALA_ACTUALIZADA -> {
                SalaDTO sala = (SalaDTO) mensaje.leerDatos();
                for (IObservadorSala o : observadores) {
                    o.salaActualizada(sala);
                }
            }
            default -> System.out.println("[FachadaSala] Mensaje no esperado: " + mensaje);
        }
    }

    @Override
    public void conexionPerdida(String motivo) {
        conectado = false;
        for (IObservadorSala o : observadores) {
            o.conexionPerdida(motivo);
        }
    }

    // ---------------- apoyo ----------------

    /** Publica la peticion y se queda esperando la respuesta del servidor. */
    private synchronized RespuestaDTO enviarPeticion(String tipo, Serializable datos) throws SalaException {
        conectarSiHaceFalta();
        respuestas.clear();
        try {
            cliente.publicar(ProtocoloSala.CANAL_PETICIONES, tipo, datos);
            RespuestaDTO respuesta = respuestas.poll(SEGUNDOS_ESPERA_RESPUESTA, TimeUnit.SECONDS);
            if (respuesta == null) {
                throw new SalaException("El servidor de la sala no respondió. ¿Está corriendo ServidorSala?");
            }
            if (!respuesta.isExito()) {
                throw new SalaException(respuesta.getError());
            }
            return respuesta;
        } catch (IOException e) {
            conectado = false;
            throw new SalaException("Se perdió la conexión con el broker. Intenta de nuevo.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SalaException("Se interrumpió la espera de la respuesta.");
        }
    }

    /** La primera vez (o si se cayo la red) se conecta al broker y se suscribe a sus canales. */
    private void conectarSiHaceFalta() throws SalaException {
        if (conectado) {
            return;
        }
        try {
            cliente.conectar(hostBroker, puertoBroker);
            cliente.suscribir(ProtocoloSala.CANAL_EVENTOS);
            cliente.suscribir(ProtocoloSala.PREFIJO_CANAL_CLIENTE + cliente.getId());
            conectado = true;
        } catch (IOException e) {
            throw new SalaException("No se pudo conectar con el broker en " + hostBroker + ":" + puertoBroker
                    + ". Revisa que MainBroker esté corriendo y la IP en parchis.properties.");
        }
    }
}
