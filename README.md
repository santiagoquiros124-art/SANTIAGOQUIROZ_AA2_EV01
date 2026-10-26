# GCM - Módulo de Gestión de Citas

Evidencia **GA7-220501096-AA2-EV01** - Codificación de módulos del software.
Aprendiz: Santiago Quiroz Galvis - SENA, Tecnología en Análisis y Desarrollo de Software (ficha 3336049).

Módulo del proyecto **Gestor de Citas Médicas (GCM)** que implementa las operaciones de inserción, consulta, actualización y eliminación sobre la tabla `citas` de la base de datos `gcm_bd` (MySQL), conectándose por JDBC.

## Tecnologías

Java 17 o superior, JDBC, MySQL 8 (`mysql-connector-j` 8.4.0), Maven y Git.

## Estructura del proyecto

```
gcm-modulo-citas/
├── pom.xml
├── sql/gcm_modulo_citas.sql          Script con gcm_bd (pacientes, profesionales, citas) y datos de prueba
├── src/main/resources/db.properties  URL, usuario y clave de la conexión JDBC
└── src/main/java/co/edu/sena/gcm/
    ├── conexion/   ConexionBD            (Singleton, entrega conexiones JDBC)
    ├── modelo/     Cita, EstadoCita      (entidad y enumeración)
    ├── excepcion/  AccesoDatosException
    ├── dao/        CitaDao, CitaDaoJdbc  (CRUD con PreparedStatement)
    ├── servicio/   CitaServicio          (reglas de negocio y fachada del módulo)
    └── principal/  Principal             (menú de consola para probar el CRUD)
```

## Funcionalidades

| Operación | Método del DAO | Sentencia SQL |
|---|---|---|
| Inserción | `insertarCita` | `INSERT INTO citas ...` |
| Consulta | `consultarCitaPorId`, `consultarTodasLasCitas`, `consultarCitasPorPaciente` | `SELECT ...` |
| Actualización | `actualizarCita` | `UPDATE citas ...` |
| Eliminación | `eliminarCita` | `DELETE FROM citas ...` |

Reglas de negocio en `CitaServicio`: la fecha de una cita nueva debe ser futura, el motivo es obligatorio (máximo 255 caracteres) y un profesional no puede tener dos citas activas (PROGRAMADA o CONFIRMADA) en la misma fecha y hora. Cancelar una cita cambia su estado a CANCELADA y libera el horario.

## Estándares de codificación aplicados

| Elemento | Convención | Ejemplos del proyecto |
|---|---|---|
| Paquetes | minúsculas, nombre de dominio invertido | `co.edu.sena.gcm.dao` |
| Clases e interfaces | PascalCase, sustantivos | `CitaDaoJdbc`, `ConexionBD` |
| Métodos | camelCase, empiezan con verbo | `insertarCita`, `validarDisponibilidad` |
| Variables y atributos | camelCase, nombres descriptivos | `idProfesional`, `fechaHora` |
| Constantes | MAYÚSCULAS con guion bajo | `SQL_INSERTAR`, `FORMATO_FECHA_HORA` |

Además: sentencias parametrizadas (`PreparedStatement`), recursos cerrados con `try-with-resources` y documentación Javadoc en cada clase.

## Ejecución

1. Ejecutar `sql/gcm_modulo_citas.sql` en MySQL Workbench.
2. Editar `src/main/resources/db.properties` con el usuario y la clave de MySQL.
3. Desde la carpeta del proyecto: `mvn compile exec:java`
   (o abrir el proyecto en NetBeans/IntelliJ/Eclipse como proyecto Maven y ejecutar `Principal`).

## Control de versiones

El proyecto se gestiona con Git; el historial de commits muestra la construcción por capas (conexión, modelo, DAO, servicio, interfaz y documentación). El enlace del repositorio remoto está en `ENLACE_REPOSITORIO.txt`.
