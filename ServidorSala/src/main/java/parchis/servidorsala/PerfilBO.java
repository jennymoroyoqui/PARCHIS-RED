package parchis.servidorsala;

import parchis.dtos.PerfilDTO;
import parchis.persistencia.FabricaDAO;
import parchis.persistencia.IPerfilDAO;
import parchis.persistencia.Perfil;
import parchis.persistencia.PersistenciaException;

/**
 * Objeto de negocio del perfil. Usa el DAO SOLO por su interfaz (IPerfilDAO);
 * no sabe que detras esta MySQL. Convierte el PerfilDTO en la entidad Perfil.
 */
public class PerfilBO {

    private final IPerfilDAO perfilDAO;

    public PerfilBO() {
        this.perfilDAO = FabricaDAO.crearPerfilDAO();
    }

    /**
     * Guarda el perfil. Si ya existia (mismo nombre de otra partida) lo actualiza.
     * Si MySQL falla, se avisa en la consola pero el jugador SI entra a la sala.
     */
    public void guardarPerfil(PerfilDTO dto) {
        Perfil perfil = new Perfil(dto.getNombre(), dto.getAvatar(), dto.getColor().name());
        try {
            if (perfilDAO.buscarPorNombre(dto.getNombre()) == null) {
                perfilDAO.agregar(perfil);
                System.out.println("[PerfilBO] Perfil de " + dto.getNombre() + " guardado en MySQL");
            } else {
                perfilDAO.actualizar(perfil);
                System.out.println("[PerfilBO] Perfil de " + dto.getNombre() + " actualizado en MySQL");
            }
        } catch (PersistenciaException e) {
            System.out.println("[PerfilBO] ERROR de base de datos: " + e.getMessage());
        }
    }
}
