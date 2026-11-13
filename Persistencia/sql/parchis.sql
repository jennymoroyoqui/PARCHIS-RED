-- Base de datos del Parchis en red.
-- Correr este script UNA vez en MySQL Workbench (en la computadora donde corre ServidorSala).

CREATE DATABASE IF NOT EXISTS parchis;
USE parchis;

-- Perfil del jugador (lo que captura en "Crea tu perfil de juego").
CREATE TABLE IF NOT EXISTS perfiles (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    nombre          VARCHAR(15) NOT NULL UNIQUE,
    avatar          VARCHAR(20) NOT NULL,
    color           VARCHAR(10) NOT NULL,
    fecha_registro  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Para revisar que se guardaron (evidencia para las pruebas):
-- SELECT * FROM perfiles;
