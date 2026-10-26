package co.edu.sena.gcm.dao;

import co.edu.sena.gcm.excepcion.AccesoDatosException;
import co.edu.sena.gcm.modelo.Cita;
import java.util.List;
import java.util.Optional;

/**
 * Contrato de acceso a datos para la tabla citas.
 *
 * @author Santiago Quiroz Galvis
 */
public interface CitaDao {

    /** Inserta la cita y devuelve el identificador generado por la base de datos. */
    int insertarCita(Cita cita) throws AccesoDatosException;

    Optional<Cita> consultarCitaPorId(int idCita) throws AccesoDatosException;

    List<Cita> consultarTodasLasCitas() throws AccesoDatosException;

    List<Cita> consultarCitasPorPaciente(int idPaciente) throws AccesoDatosException;

    /** Devuelve true si se actualizo una fila. */
    boolean actualizarCita(Cita cita) throws AccesoDatosException;

    /** Devuelve true si se elimino una fila. */
    boolean eliminarCita(int idCita) throws AccesoDatosException;

    /** Indica si el profesional ya tiene una cita activa en esa fecha y hora. */
    boolean existeCitaActivaDelProfesional(int idProfesional, java.time.LocalDateTime fechaHora,
            int idCitaExcluida) throws AccesoDatosException;
}
