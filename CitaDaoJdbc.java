package co.edu.sena.gcm.dao;

import co.edu.sena.gcm.conexion.ConexionBD;
import co.edu.sena.gcm.excepcion.AccesoDatosException;
import co.edu.sena.gcm.modelo.Cita;
import co.edu.sena.gcm.modelo.EstadoCita;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementacion JDBC de {@link CitaDao} sobre la tabla citas de gcm_bd.
 * Todas las consultas usan PreparedStatement para evitar inyeccion SQL.
 *
 * @author Santiago Quiroz Galvis
 */
public class CitaDaoJdbc implements CitaDao {

    private static final String SQL_INSERTAR =
            "INSERT INTO citas (id_paciente, id_profesional, fecha_hora, motivo_consulta, estado) "
            + "VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_SELECCIONAR_BASE =
            "SELECT id_cita, id_paciente, id_profesional, fecha_hora, motivo_consulta, estado "
            + "FROM citas";

    private static final String SQL_CONSULTAR_POR_ID =
            SQL_SELECCIONAR_BASE + " WHERE id_cita = ?";

    private static final String SQL_CONSULTAR_TODAS =
            SQL_SELECCIONAR_BASE + " ORDER BY fecha_hora";

    private static final String SQL_CONSULTAR_POR_PACIENTE =
            SQL_SELECCIONAR_BASE + " WHERE id_paciente = ? ORDER BY fecha_hora";

    private static final String SQL_ACTUALIZAR =
            "UPDATE citas SET id_paciente = ?, id_profesional = ?, fecha_hora = ?, "
            + "motivo_consulta = ?, estado = ? WHERE id_cita = ?";

    private static final String SQL_ELIMINAR =
            "DELETE FROM citas WHERE id_cita = ?";

    private static final String SQL_EXISTE_CITA_ACTIVA =
            "SELECT COUNT(*) FROM citas WHERE id_profesional = ? AND fecha_hora = ? "
            + "AND estado IN ('PROGRAMADA', 'CONFIRMADA') AND id_cita <> ?";

    private final ConexionBD conexionBd = ConexionBD.obtenerInstancia();

    @Override
    public int insertarCita(Cita cita) throws AccesoDatosException {
        try (Connection conexion = conexionBd.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(
                        SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {
            sentencia.setInt(1, cita.getIdPaciente());
            sentencia.setInt(2, cita.getIdProfesional());
            sentencia.setTimestamp(3, Timestamp.valueOf(cita.getFechaHora()));
            sentencia.setString(4, cita.getMotivoConsulta());
            sentencia.setString(5, cita.getEstadoCita().name());
            sentencia.executeUpdate();

            try (ResultSet clavesGeneradas = sentencia.getGeneratedKeys()) {
                if (clavesGeneradas.next()) {
                    return clavesGeneradas.getInt(1);
                }
            }
            throw new AccesoDatosException(
                    "La cita se inserto pero no se obtuvo su identificador", null);
        } catch (SQLException excepcion) {
            throw new AccesoDatosException("Error al insertar la cita", excepcion);
        }
    }

    @Override
    public Optional<Cita> consultarCitaPorId(int idCita) throws AccesoDatosException {
        try (Connection conexion = conexionBd.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(SQL_CONSULTAR_POR_ID)) {
            sentencia.setInt(1, idCita);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return Optional.of(mapearCita(resultado));
                }
                return Optional.empty();
            }
        } catch (SQLException excepcion) {
            throw new AccesoDatosException("Error al consultar la cita " + idCita, excepcion);
        }
    }

    @Override
    public List<Cita> consultarTodasLasCitas() throws AccesoDatosException {
        List<Cita> listaCitas = new ArrayList<>();
        try (Connection conexion = conexionBd.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(SQL_CONSULTAR_TODAS);
                ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                listaCitas.add(mapearCita(resultado));
            }
            return listaCitas;
        } catch (SQLException excepcion) {
            throw new AccesoDatosException("Error al consultar las citas", excepcion);
        }
    }

    @Override
    public List<Cita> consultarCitasPorPaciente(int idPaciente) throws AccesoDatosException {
        List<Cita> listaCitas = new ArrayList<>();
        try (Connection conexion = conexionBd.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(
                        SQL_CONSULTAR_POR_PACIENTE)) {
            sentencia.setInt(1, idPaciente);
            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    listaCitas.add(mapearCita(resultado));
                }
            }
            return listaCitas;
        } catch (SQLException excepcion) {
            throw new AccesoDatosException(
                    "Error al consultar las citas del paciente " + idPaciente, excepcion);
        }
    }

    @Override
    public boolean actualizarCita(Cita cita) throws AccesoDatosException {
        try (Connection conexion = conexionBd.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(SQL_ACTUALIZAR)) {
            sentencia.setInt(1, cita.getIdPaciente());
            sentencia.setInt(2, cita.getIdProfesional());
            sentencia.setTimestamp(3, Timestamp.valueOf(cita.getFechaHora()));
            sentencia.setString(4, cita.getMotivoConsulta());
            sentencia.setString(5, cita.getEstadoCita().name());
            sentencia.setInt(6, cita.getIdCita());
            return sentencia.executeUpdate() > 0;
        } catch (SQLException excepcion) {
            throw new AccesoDatosException(
                    "Error al actualizar la cita " + cita.getIdCita(), excepcion);
        }
    }

    @Override
    public boolean eliminarCita(int idCita) throws AccesoDatosException {
        try (Connection conexion = conexionBd.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(SQL_ELIMINAR)) {
            sentencia.setInt(1, idCita);
            return sentencia.executeUpdate() > 0;
        } catch (SQLException excepcion) {
            throw new AccesoDatosException("Error al eliminar la cita " + idCita, excepcion);
        }
    }

    @Override
    public boolean existeCitaActivaDelProfesional(int idProfesional, LocalDateTime fechaHora,
            int idCitaExcluida) throws AccesoDatosException {
        try (Connection conexion = conexionBd.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(
                        SQL_EXISTE_CITA_ACTIVA)) {
            sentencia.setInt(1, idProfesional);
            sentencia.setTimestamp(2, Timestamp.valueOf(fechaHora));
            sentencia.setInt(3, idCitaExcluida);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() && resultado.getInt(1) > 0;
            }
        } catch (SQLException excepcion) {
            throw new AccesoDatosException(
                    "Error al verificar la disponibilidad del profesional", excepcion);
        }
    }

    private Cita mapearCita(ResultSet resultado) throws SQLException {
        Cita cita = new Cita();
        cita.setIdCita(resultado.getInt("id_cita"));
        cita.setIdPaciente(resultado.getInt("id_paciente"));
        cita.setIdProfesional(resultado.getInt("id_profesional"));
        cita.setFechaHora(resultado.getTimestamp("fecha_hora").toLocalDateTime());
        cita.setMotivoConsulta(resultado.getString("motivo_consulta"));
        cita.setEstadoCita(EstadoCita.valueOf(resultado.getString("estado")));
        return cita;
    }
}
