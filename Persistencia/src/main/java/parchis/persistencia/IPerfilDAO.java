package parchis.persistencia;

/**
 * Lo que se puede hacer con los perfiles en la base de datos.
 * PerfilBO solo conoce esta interfaz, nunca a PerfilDAOMySQL.
 */
public interface IPerfilDAO {

    /** Regresa el perfil con ese nombre, o null si no existe. */
    Perfil buscarPorNombre(String nombre) throws PersistenciaException;

    void agregar(Perfil perfil) throws PersistenciaException;

    /** Actualiza avatar y color del perfil que tenga ese nombre. */
    void actualizar(Perfil perfil) throws PersistenciaException;
}
