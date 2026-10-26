package co.edu.sena.gcm.excepcion;

/**
 * Excepcion lanzada por la capa de acceso a datos cuando una operacion JDBC falla.
 *
 * @author Santiago Quiroz Galvis
 */
public class AccesoDatosException extends Exception {

    private static final long serialVersionUID = 1L;

    public AccesoDatosException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
