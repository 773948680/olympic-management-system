-- =====================================================================
-- Olympic Management System - Schema de base de donnees
-- Moteur cible : MySQL 8 (InnoDB, utf8mb4)
--
-- Ce script est la source de verite du schema : le backend Spring Boot
-- est configure en hibernate.ddl-auto=validate, il ne cree ni ne modifie
-- jamais les tables, il verifie seulement que les entites JPA correspondent
-- a ce schema.
--
-- Utilise automatiquement par Docker Compose (monte dans
-- /docker-entrypoint-initdb.d) au premier demarrage du conteneur MySQL.
-- Peut aussi etre execute manuellement via phpMyAdmin ou le client mysql.
--
-- La creation des utilisateurs applicatifs (MYSQL_USER/MYSQL_PASSWORD)
-- est geree separement par les variables d'environnement Docker Compose
-- (voir .env.example), pas par ce script.
-- =====================================================================

CREATE DATABASE IF NOT EXISTS olympic_management
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

USE olympic_management;

-- ---------------------------------------------------------------------
-- Table : disciplines
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS disciplines (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(500) NULL,
    CONSTRAINT uk_discipline_name UNIQUE (name)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- ---------------------------------------------------------------------
-- Table : athletes
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS athletes (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name    VARCHAR(100) NOT NULL,
    last_name     VARCHAR(100) NOT NULL,
    gender        ENUM('FEMALE', 'MALE') NOT NULL,
    date_of_birth DATE NOT NULL,
    nationality   VARCHAR(100) NOT NULL,
    discipline_id BIGINT NOT NULL,
    height        INT NOT NULL,
    weight        DOUBLE NOT NULL,
    created_at    DATETIME(6) NOT NULL,
    updated_at    DATETIME(6) NOT NULL,
    CONSTRAINT fk_athlete_discipline FOREIGN KEY (discipline_id) REFERENCES disciplines (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- Nationalite filtree/groupee frequemment (recherche multicritere, dashboard,
-- tableau des medailles, historique SOAP par nation).
CREATE INDEX idx_athlete_nationality ON athletes (nationality);

-- ---------------------------------------------------------------------
-- Table : events (epreuves)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS events (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(150) NOT NULL,
    discipline_id BIGINT NOT NULL,
    event_date    DATETIME(6) NOT NULL,
    venue         VARCHAR(150) NULL,
    status        ENUM('COMPLETED', 'IN_PROGRESS', 'SCHEDULED') NOT NULL,
    CONSTRAINT fk_event_discipline FOREIGN KEY (discipline_id) REFERENCES disciplines (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE INDEX idx_event_date ON events (event_date);

-- ---------------------------------------------------------------------
-- Table : results (resultats)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS results (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id       BIGINT NOT NULL,
    athlete_id     BIGINT NOT NULL,
    position_value INT NOT NULL,
    time_value     VARCHAR(50) NULL,
    score_value    DOUBLE NULL,
    medal          ENUM('BRONZE', 'GOLD', 'NONE', 'SILVER') NOT NULL,
    CONSTRAINT uk_result_event_athlete UNIQUE (event_id, athlete_id),
    CONSTRAINT uk_result_event_position UNIQUE (event_id, position_value),
    CONSTRAINT fk_result_event FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT fk_result_athlete FOREIGN KEY (athlete_id) REFERENCES athletes (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- Filtre "medal <> NONE" present dans toutes les requetes d'agregation
-- (dashboard, tableau des medailles, historique des medailles par nation).
CREATE INDEX idx_result_medal ON results (medal);

-- ---------------------------------------------------------------------
-- Table : users (comptes d'acces a l'application, authentification JWT)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at    DATETIME(6) NOT NULL,
    CONSTRAINT uk_user_username UNIQUE (username)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
