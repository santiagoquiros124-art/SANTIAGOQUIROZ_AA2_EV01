package co.edu.sena.gcm.conexion;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Administra la conexion JDBC con la base de datos gcm_bd.
 * Aplica el patron Singleton: existe una unica instancia que guarda la
 * configuracion leida de db.properties y entrega conexiones nuevas.
 *
 * @author Santiago Quiroz Galvis
 */
public final class ConexionBD {

    private static final String ARCHIVO_CONFIGURACION = "db.properties";

    private static ConexionBD instancia;

    private final String urlConexion;
    private final String usuarioBaseDatos;
    private final String claveBaseDatos;

    private ConexionBD() {
        Properties propiedades = new Properties();
        try (InputStream flujoEntrada = ConexionBD.class.getClassLoader()
                .getResourceAsStream(ARCHIVO_CONFIGURACION)) {
            if (flujoEntrada == null) {
                throw new IllegalStateException(
                        "No se encontro el archivo " + ARCHIVO_CONFIGURACION);
            }
            propiedades.load(flujoEntrada);
        } catch (IOException excepcion) {
            throw new IllegalStateException(
                    "No fue posible leer " + ARCHIVO_CONFIGURACION, excepcion);
        }
        this.urlConexion = propiedades.getProperty("db.url");
        this.usuarioBaseDatos = propiedades.getProperty("db.usuario");
        this.claveBaseDatos = propiedades.getProperty("db.clave");
    }

    public static synchronized ConexionBD obtenerInstancia() {
        if (instancia == null) {
            instancia = new ConexionBD();
        }
        return instancia;
    }

    /**
     * Abre una conexion nueva. Quien la solicita debe cerrarla,
     * preferiblemente con try-with-resources.
     */
    public Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(urlConexion, usuarioBaseDatos, claveBaseDatos);
    }
}
