package parchis.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implementacion del DAO para MySQL. TODO el SQL del proyecto esta aqui.
 * Se usan PreparedStatement (con ?) para evitar inyeccion SQL.
 */
public class PerfilDAOMySQL implements IPerfilDAO {

    @Override
    public Perfil buscarPorNombre(String nombre) throws PersistenciaException {
        String sql = "SELECT id, nombre, avatar, color FROM perfiles WHERE nombre = ?";
        try (Connection con = ConexionBD.obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Perfil p = new Perfil(rs.getString("nombre"), rs.getString("avatar"), rs.getString("color"));
                p.setId(rs.getInt("id"));
                return p;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo buscar el perfil: " + e.getMessage(), e);
        }
    }

    @Override
    public void agregar(Perfil perfil) throws PersistenciaException {
        String sql = "INSERT INTO perfiles (nombre, avatar, color) VALUES (?, ?, ?)";
        try (Connection con = ConexionBD.obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, perfil.getNombre());
            ps.setString(2, perfil.getAvatar());
            ps.setString(3, perfil.getColor());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo agregar el perfil: " + e.getMessage(), e);
        }
    }

    @Override
    public void actualizar(Perfil perfil) throws PersistenciaException {
        String sql = "UPDATE perfiles SET avatar = ?, color = ? WHERE nombre = ?";
        try (Connection con = ConexionBD.obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, perfil.getAvatar());
            ps.setString(2, perfil.getColor());
            ps.setString(3, perfil.getNombre());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo actualizar el perfil: " + e.getMessage(), e);
        }
    }
}
