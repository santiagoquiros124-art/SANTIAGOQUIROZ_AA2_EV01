-- ============================================================
-- GCM - Gestor de Citas Medicas
-- Script de apoyo del modulo de citas (MySQL 8)
-- Crea las tablas que el modulo usa de gcm_bd: pacientes,
-- profesionales y citas. Si ya cuenta con el script completo
-- de EV03 (forward engineering), use ese y ajuste los nombres
-- de columna en CitaDaoJdbc.java si difieren de estos.
-- ============================================================
CREATE DATABASE IF NOT EXISTS gcm_bd CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE gcm_bd;

CREATE TABLE IF NOT EXISTS pacientes (
    id_paciente INT AUTO_INCREMENT PRIMARY KEY,
    nombres     VARCHAR(80)  NOT NULL,
    apellidos   VARCHAR(80)  NOT NULL,
    documento   VARCHAR(20)  NOT NULL UNIQUE
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS profesionales (
    id_profesional INT AUTO_INCREMENT PRIMARY KEY,
    nombres        VARCHAR(80) NOT NULL,
    apellidos      VARCHAR(80) NOT NULL,
    registro_medico VARCHAR(30) NOT NULL UNIQUE
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS citas (
    id_cita         INT AUTO_INCREMENT PRIMARY KEY,
    id_paciente     INT          NOT NULL,
    id_profesional  INT          NOT NULL,
    fecha_hora      DATETIME     NOT NULL,
    motivo_consulta VARCHAR(255) NOT NULL,
    estado          ENUM('PROGRAMADA', 'CONFIRMADA', 'CANCELADA', 'ATENDIDA')
                    NOT NULL DEFAULT 'PROGRAMADA',
    CONSTRAINT fk_citas_pacientes
        FOREIGN KEY (id_paciente) REFERENCES pacientes (id_paciente),
    CONSTRAINT fk_citas_profesionales
        FOREIGN KEY (id_profesional) REFERENCES profesionales (id_profesional)
) ENGINE = InnoDB;

-- Datos de prueba
INSERT INTO pacientes (nombres, apellidos, documento) VALUES
    ('Laura', 'Ramirez Soto', '1005123456'),
    ('Carlos', 'Mejia Duque', '1144987654');

INSERT INTO profesionales (nombres, apellidos, registro_medico) VALUES
    ('Andrea', 'Cardona Lopez', 'RM-1001'),
    ('Julian', 'Ospina Vera', 'RM-1002');
