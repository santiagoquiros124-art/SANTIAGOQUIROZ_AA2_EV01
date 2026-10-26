package co.edu.sena.gcm.servicio;

import co.edu.sena.gcm.dao.CitaDao;
import co.edu.sena.gcm.dao.CitaDaoJdbc;
import co.edu.sena.gcm.excepcion.AccesoDatosException;
import co.edu.sena.gcm.modelo.Cita;
import co.edu.sena.gcm.modelo.EstadoCita;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Capa de logica de negocio del modulo de citas. Valida las reglas antes de
 * delegar en el DAO y ofrece a la interfaz una fachada simple del modulo.
 *
 * @author Santiago Quiroz Galvis
 */
public class CitaServicio {

    private static final int LONGITUD_MAXIMA_MOTIVO = 255;

    private final CitaDao citaDao;

    public CitaServicio() {
        this(new CitaDaoJdbc());
    }

    public CitaServicio(CitaDao citaDao) {
        this.citaDao = citaDao;
    }

    public int registrarCita(int idPaciente, int idProfesional, LocalDateTime fechaHora,
            String motivoConsulta) throws AccesoDatosException {
        Cita cita = new Cita(idPaciente, idProfesional, fechaHora,
                motivoConsulta, EstadoCita.PROGRAMADA);
        validarCita(cita);
        if (!fechaHora.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha de la cita debe ser futura");
        }
        validarDisponibilidad(cita, 0);
        return citaDao.insertarCita(cita);
    }

    public Optional<Cita> buscarCita(int idCita) throws AccesoDatosException {
        return citaDao.consultarCitaPorId(idCita);
    }

    public List<Cita> listarCitas() throws AccesoDatosException {
        return citaDao.consultarTodasLasCitas();
    }

    public List<Cita> listarCitasDelPaciente(int idPaciente) throws AccesoDatosException {
        return citaDao.consultarCitasPorPaciente(idPaciente);
    }

    public boolean actualizarCita(Cita cita) throws AccesoDatosException {
        validarCita(cita);
        if (cita.getEstadoCita() == EstadoCita.PROGRAMADA
                || cita.getEstadoCita() == EstadoCita.CONFIRMADA) {
            validarDisponibilidad(cita, cita.getIdCita());
        }
        return citaDao.actualizarCita(cita);
    }

    public boolean cancelarCita(int idCita) throws AccesoDatosException {
        Optional<Cita> citaEncontrada = citaDao.consultarCitaPorId(idCita);
        if (citaEncontrada.isEmpty()) {
            return false;
        }
        Cita cita = citaEncontrada.get();
        cita.setEstadoCita(EstadoCita.CANCELADA);
        return citaDao.actualizarCita(cita);
    }

    public boolean eliminarCita(int idCita) throws AccesoDatosException {
        return citaDao.eliminarCita(idCita);
    }

    private void validarCita(Cita cita) {
        if (cita.getIdPaciente() <= 0) {
            throw new IllegalArgumentException("El identificador del paciente no es valido");
        }
        if (cita.getIdProfesional() <= 0) {
            throw new IllegalArgumentException("El identificador del profesional no es valido");
        }
        if (cita.getFechaHora() == null) {
            throw new IllegalArgumentException("La fecha y hora de la cita son obligatorias");
        }
        String motivo = cita.getMotivoConsulta();
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("El motivo de consulta es obligatorio");
        }
        if (motivo.length() > LONGITUD_MAXIMA_MOTIVO) {
            throw new IllegalArgumentException(
                    "El motivo de consulta no puede superar " + LONGITUD_MAXIMA_MOTIVO
                    + " caracteres");
        }
        if (cita.getEstadoCita() == null) {
            throw new IllegalArgumentException("El estado de la cita es obligatorio");
        }
    }

    private void validarDisponibilidad(Cita cita, int idCitaExcluida)
            throws AccesoDatosException {
        if (citaDao.existeCitaActivaDelProfesional(cita.getIdProfesional(),
                cita.getFechaHora(), idCitaExcluida)) {
            throw new IllegalArgumentException(
                    "El profesional ya tiene una cita activa en esa fecha y hora");
        }
    }
}
