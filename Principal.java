package co.edu.sena.gcm.principal;

import co.edu.sena.gcm.excepcion.AccesoDatosException;
import co.edu.sena.gcm.modelo.Cita;
import co.edu.sena.gcm.modelo.EstadoCita;
import co.edu.sena.gcm.servicio.CitaServicio;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Interfaz de consola del modulo de citas de GCM. Permite probar las cuatro
 * operaciones CRUD (insercion, consulta, actualizacion y eliminacion).
 *
 * @author Santiago Quiroz Galvis
 */
public class Principal {

    private static final DateTimeFormatter FORMATO_FECHA_HORA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final CitaServicio citaServicio = new CitaServicio();
    private final Scanner lectorConsola = new Scanner(System.in);

    public static void main(String[] argumentos) {
        new Principal().iniciar();
    }

    private void iniciar() {
        int opcionSeleccionada;
        do {
            mostrarMenu();
            opcionSeleccionada = leerEntero("Seleccione una opcion: ");
            ejecutarOpcion(opcionSeleccionada);
        } while (opcionSeleccionada != 0);
        System.out.println("Hasta pronto.");
    }

    private void mostrarMenu() {
        System.out.println();
        System.out.println("=== GCM - Modulo de Gestion de Citas ===");
        System.out.println("1. Registrar cita");
        System.out.println("2. Consultar cita por id");
        System.out.println("3. Listar todas las citas");
        System.out.println("4. Listar citas de un paciente");
        System.out.println("5. Actualizar cita");
        System.out.println("6. Cancelar cita");
        System.out.println("7. Eliminar cita");
        System.out.println("0. Salir");
    }

    private void ejecutarOpcion(int opcion) {
        try {
            switch (opcion) {
                case 1 -> registrarCita();
                case 2 -> consultarCita();
                case 3 -> imprimirCitas(citaServicio.listarCitas());
                case 4 -> imprimirCitas(citaServicio.listarCitasDelPaciente(
                        leerEntero("Id del paciente: ")));
                case 5 -> actualizarCita();
                case 6 -> cancelarCita();
                case 7 -> eliminarCita();
                case 0 -> { }
                default -> System.out.println("Opcion no valida.");
            }
        } catch (IllegalArgumentException excepcion) {
            System.out.println("Dato invalido: " + excepcion.getMessage());
        } catch (AccesoDatosException excepcion) {
            System.out.println(excepcion.getMessage()
                    + (excepcion.getCause() != null
                            ? " (" + excepcion.getCause().getMessage() + ")" : ""));
        }
    }

    private void registrarCita() throws AccesoDatosException {
        int idPaciente = leerEntero("Id del paciente: ");
        int idProfesional = leerEntero("Id del profesional: ");
        LocalDateTime fechaHora = leerFechaHora("Fecha y hora (yyyy-MM-dd HH:mm): ");
        String motivoConsulta = leerTexto("Motivo de consulta: ");
        int idGenerado = citaServicio.registrarCita(
                idPaciente, idProfesional, fechaHora, motivoConsulta);
        System.out.println("Cita registrada con id " + idGenerado);
    }

    private void consultarCita() throws AccesoDatosException {
        Optional<Cita> cita = citaServicio.buscarCita(leerEntero("Id de la cita: "));
        System.out.println(cita.map(Cita::toString).orElse("No existe una cita con ese id."));
    }

    private void actualizarCita() throws AccesoDatosException {
        Optional<Cita> citaEncontrada = citaServicio.buscarCita(leerEntero("Id de la cita: "));
        if (citaEncontrada.isEmpty()) {
            System.out.println("No existe una cita con ese id.");
            return;
        }
        Cita cita = citaEncontrada.get();
        System.out.println("Actual: " + cita);
        cita.setIdProfesional(leerEntero("Nuevo id del profesional: "));
        cita.setFechaHora(leerFechaHora("Nueva fecha y hora (yyyy-MM-dd HH:mm): "));
        cita.setMotivoConsulta(leerTexto("Nuevo motivo de consulta: "));
        cita.setEstadoCita(leerEstado());
        System.out.println(citaServicio.actualizarCita(cita)
                ? "Cita actualizada." : "No se actualizo ninguna cita.");
    }

    private void cancelarCita() throws AccesoDatosException {
        System.out.println(citaServicio.cancelarCita(leerEntero("Id de la cita: "))
                ? "Cita cancelada." : "No existe una cita con ese id.");
    }

    private void eliminarCita() throws AccesoDatosException {
        System.out.println(citaServicio.eliminarCita(leerEntero("Id de la cita: "))
                ? "Cita eliminada." : "No existe una cita con ese id.");
    }

    private void imprimirCitas(List<Cita> listaCitas) {
        if (listaCitas.isEmpty()) {
            System.out.println("No hay citas para mostrar.");
            return;
        }
        listaCitas.forEach(System.out::println);
    }

    private int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return Integer.parseInt(lectorConsola.nextLine().trim());
            } catch (NumberFormatException excepcion) {
                System.out.println("Ingrese un numero entero valido.");
            }
        }
    }

    private String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return lectorConsola.nextLine().trim();
    }

    private LocalDateTime leerFechaHora(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return LocalDateTime.parse(lectorConsola.nextLine().trim(), FORMATO_FECHA_HORA);
            } catch (DateTimeParseException excepcion) {
                System.out.println("Formato invalido. Ejemplo: 2026-11-05 14:30");
            }
        }
    }

    private EstadoCita leerEstado() {
        while (true) {
            System.out.print("Estado (PROGRAMADA, CONFIRMADA, CANCELADA, ATENDIDA): ");
            try {
                return EstadoCita.valueOf(lectorConsola.nextLine().trim().toUpperCase());
            } catch (IllegalArgumentException excepcion) {
                System.out.println("Estado no valido.");
            }
        }
    }
}
