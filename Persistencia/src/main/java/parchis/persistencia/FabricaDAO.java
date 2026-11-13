package parchis.persistencia;

/**
 * Fabrica de DAOs: el unico lugar que conoce a PerfilDAOMySQL.
 * PerfilBO le pide un IPerfilDAO y no sabe que es MySQL.
 */
public final class FabricaDAO {

    private FabricaDAO() {
    }

    public static IPerfilDAO crearPerfilDAO() {
        return new PerfilDAOMySQL();
    }
}
