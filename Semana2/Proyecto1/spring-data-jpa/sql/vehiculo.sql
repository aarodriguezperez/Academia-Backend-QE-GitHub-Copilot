-- Crear usuario para Spring Boot
DROP USER IF EXISTS 'springstudent'@'%';
CREATE USER 'springstudent'@'%' IDENTIFIED BY 'springstudent';
GRANT ALL PRIVILEGES ON *.* TO 'springstudent'@'%';

-- Crear base de datos
CREATE DATABASE IF NOT EXISTS vehiculo_directory;

USE vehiculo_directory;

-- Crear tabla
DROP TABLE IF EXISTS vehiculo;

CREATE TABLE vehiculo (
                          id INT NOT NULL AUTO_INCREMENT,
                          marca VARCHAR(45) DEFAULT NULL,
                          modelo VARCHAR(45) DEFAULT NULL,
                          color VARCHAR(45) DEFAULT NULL,
                          anio INT DEFAULT NULL,
                          PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

-- Registro de prueba
INSERT INTO vehiculo (marca, modelo, color, anio)
VALUES ('Ford', 'Lobo', 'Negro', 2021);
INSERT INTO vehiculo (marca, modelo, color, anio)
VALUES ('Toyota', 'Corolla', 'Blanco', 2026);