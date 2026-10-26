package co.edu.sena.gcm.modelo;

import java.time.LocalDateTime;

/**
 * Entidad que representa una fila de la tabla citas de gcm_bd.
 *
 * @author Santiago Quiroz Galvis
 */
public class Cita {

    private int idCita;
    private int idPaciente;
    private int idProfesional;
    private LocalDateTime fechaHora;
    private String motivoConsulta;
    private EstadoCita estadoCita;

    public Cita() {
    }

    public Cita(int idPaciente, int idProfesional, LocalDateTime fechaHora,
            String motivoConsulta, EstadoCita estadoCita) {
        this.idPaciente = idPaciente;
        this.idProfesional = idProfesional;
        this.fechaHora = fechaHora;
        this.motivoConsulta = motivoConsulta;
        this.estadoCita = estadoCita;
    }

    public int getIdCita() {
        return idCita;
    }

    public void setIdCita(int idCita) {
        this.idCita = idCita;
    }

    public int getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(int idPaciente) {
        this.idPaciente = idPaciente;
    }

    public int getIdProfesional() {
        return idProfesional;
    }

    public void setIdProfesional(int idProfesional) {
        this.idProfesional = idProfesional;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getMotivoConsulta() {
        return motivoConsulta;
    }

    public void setMotivoConsulta(String motivoConsulta) {
        this.motivoConsulta = motivoConsulta;
    }

    public EstadoCita getEstadoCita() {
        return estadoCita;
    }

    public void setEstadoCita(EstadoCita estadoCita) {
        this.estadoCita = estadoCita;
    }

    @Override
    public String toString() {
        return String.format("Cita #%d | paciente=%d | profesional=%d | %s | %s | %s",
                idCita, idPaciente, idProfesional, fechaHora, estadoCita, motivoConsulta);
    }
}
