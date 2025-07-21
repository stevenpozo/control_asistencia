-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema corpsur
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema corpsur
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS corpsur DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE corpsur ;

-- -----------------------------------------------------
-- Table corpsur.asistencias
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS corpsur.asistencias (
  id INT NOT NULL AUTO_INCREMENT,
  fecha DATE NOT NULL,
  tipo VARCHAR(40) NOT NULL,
  estado tinyint DEFAULT 1,
  PRIMARY KEY (id)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;



-- -----------------------------------------------------
-- Table corpsur.provincia
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS corpsur.provincia (
  id INT NOT NULL AUTO_INCREMENT,
  provincia VARCHAR(45) NULL DEFAULT NULL,
  provinciacol VARCHAR(45) NOT NULL,
  PRIMARY KEY (id))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table corpsur.ciudad
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS corpsur.ciudad (
  id INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(45) NOT NULL,
  provincia_id INT NOT NULL,
  PRIMARY KEY (id),
  INDEX fk_ciudad_provincia1_idx (provincia_id ASC) VISIBLE,
  CONSTRAINT fk_ciudad_provincia1
    FOREIGN KEY (provincia_id)
    REFERENCES corpsur.provincia (id))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table corpsur.profesiones
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS corpsur.profesiones (
  id INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(45) NOT NULL,
  PRIMARY KEY (id))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table corpsur.usuarios
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS corpsur.usuarios (
  id INT NOT NULL AUTO_INCREMENT,
  usuario VARCHAR(45) NULL DEFAULT NULL,
  contraseña VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (id))
ENGINE = InnoDB
AUTO_INCREMENT = 4
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table corpsur.laboratorio
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS corpsur.laboratorio (
  id INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(45) NULL,
  PRIMARY KEY (id))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table corpsur.profesional
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS corpsur.profesional (
  idProfesional INT NOT NULL AUTO_INCREMENT,
  cedula VARCHAR(13) NOT NULL,
  apellido VARCHAR(45) NOT NULL,
  nombre VARCHAR(45) NOT NULL,
  fechaNace DATE NULL DEFAULT NULL,
  telefonos VARCHAR(45) NOT NULL,
  invitacion VARCHAR(45) NULL,
  provincia_id INT NOT NULL,
  ciudad_id INT NOT NULL,
  usuarios_id INT NOT NULL,
  profesiones_id INT NOT NULL,
  laboratorio_id INT NOT NULL,
  activo TINYINT NULL DEFAULT 1,
  PRIMARY KEY (idProfesional),
  INDEX fk_profesional_profesion_idx (profesiones_id),
  INDEX fk_profesional_ciudad_idx (ciudad_id),
  INDEX fk_profesional_provincia_idx (provincia_id),
  INDEX fk_profesional_usuario_idx (usuarios_id),
  INDEX fk_profesional_laboratorio_idx (laboratorio_id),
  CONSTRAINT fk_profesional_ciudad
    FOREIGN KEY (ciudad_id)
    REFERENCES corpsur.ciudad (id),
  CONSTRAINT fk_profesional_provincia
    FOREIGN KEY (provincia_id)
    REFERENCES corpsur.provincia (id),
  CONSTRAINT fk_profesional_usuario
    FOREIGN KEY (usuarios_id)
    REFERENCES corpsur.usuarios (id),
  CONSTRAINT fk_profesional_profesion
    FOREIGN KEY (profesiones_id)
    REFERENCES corpsur.profesiones (id),
  CONSTRAINT fk_profesional_laboratorio
    FOREIGN KEY (laboratorio_id)
    REFERENCES corpsur.laboratorio (id)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;



-- -----------------------------------------------------
-- Table corpsur.asiste
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS corpsur.asiste (
  id INT NOT NULL AUTO_INCREMENT,
  asistencias_id INT NOT NULL,
  profesional_id INT NOT NULL,
  hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY unique_asistencia (asistencias_id, profesional_id),
  CONSTRAINT fk_asiste_asistencia
    FOREIGN KEY (asistencias_id)
    REFERENCES corpsur.asistencias (id),
  CONSTRAINT fk_asiste_profesional
    FOREIGN KEY (profesional_id)
    REFERENCES corpsur.profesional (idProfesional)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;



-- -----------------------------------------------------
-- Table corpsur.historiaingresousuarios
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS corpsur.historiaingresousuarios (
  id INT NOT NULL AUTO_INCREMENT,
  ingreso DATETIME NULL DEFAULT NULL,
  salida DATETIME NULL DEFAULT NULL,
  usuarioActivo TINYINT NULL DEFAULT NULL,
  usuarioIngresado INT NOT NULL,
  PRIMARY KEY (id),
  INDEX fk_historiaIngresoUsuarios_usuarios1_idx (usuarioIngresado ASC) VISIBLE,
  CONSTRAINT fk_historiaIngresoUsuarios_usuarios1
    FOREIGN KEY (usuarioIngresado)
    REFERENCES corpsur.usuarios (id))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;



-- usuario default
INSERT INTO usuarios (usuario, contraseña)
VALUES ('admin', 'admin');


-- insert provincias y ciudades (cantones)
INSERT INTO provincia (id, provincia, provinciacol) VALUES (1, 'Azuay', 'Azuay');
INSERT INTO provincia (id, provincia, provinciacol) VALUES (2, 'Bolívar', 'Bolívar');
INSERT INTO provincia (id, provincia, provinciacol) VALUES (3, 'Cañar', 'Cañar');
INSERT INTO provincia (id, provincia, provinciacol) VALUES (4, 'Carchi', 'Carchi');
INSERT INTO provincia (id, provincia, provinciacol) VALUES (5, 'Cotopaxi', 'Cotopaxi');
INSERT INTO provincia (id, provincia, provinciacol) VALUES (6, 'Chimborazo', 'Chimborazo');
INSERT INTO provincia (id, provincia, provinciacol) VALUES (7, 'El Oro', 'El Oro');
INSERT INTO provincia (id, provincia, provinciacol) VALUES (8, 'Esmeraldas', 'Esmeraldas');
INSERT INTO provincia (id, provincia, provinciacol) VALUES (9, 'Guayas', 'Guayas');
INSERT INTO provincia (id, provincia, provinciacol) VALUES (10, 'Imbabura', 'Imbabura');
INSERT INTO provincia (id, provincia, provinciacol) VALUES (11, 'Loja', 'Loja');
INSERT INTO provincia (id, provincia, provinciacol) VALUES (12, 'Los Rios', 'Los Rios');
INSERT INTO provincia (id, provincia, provinciacol) VALUES (13, 'Manabi', 'Manabi');
INSERT INTO provincia (id, provincia, provinciacol) VALUES (14, 'Morona Santiago', 'Morona Santiago');
INSERT INTO provincia (id, provincia, provinciacol) VALUES (15, 'Napo', 'Napo');
INSERT INTO provincia (id, provincia, provinciacol) VALUES (16, 'Pastaza', 'Pastaza');
INSERT INTO provincia (id, provincia, provinciacol) VALUES (17, 'Pichincha', 'Pichincha');
INSERT INTO provincia (id, provincia, provinciacol) VALUES (18, 'Tungurahua', 'Tungurahua');
INSERT INTO provincia (id, provincia, provinciacol) VALUES (19, 'Zamora Chinchipe', 'Zamora Chinchipe');
INSERT INTO provincia (id, provincia, provinciacol) VALUES (20, 'Galápagos', 'Galápagos');
INSERT INTO provincia (id, provincia, provinciacol) VALUES (21, 'Sucumbíos', 'Sucumbíos');
INSERT INTO provincia (id, provincia, provinciacol) VALUES (22, 'Orellana', 'Orellana');
INSERT INTO provincia (id, provincia, provinciacol) VALUES (23, 'Santo Domingo de Los Tsáchilas', 'Santo Domingo de Los Tsáchilas');
INSERT INTO provincia (id, provincia, provinciacol) VALUES (24, 'Santa Elena', 'Santa Elena');
INSERT INTO ciudad (id, nombre, provincia_id) VALUES (1, 'Cuenca', 1),
  (2, 'Girón', 1),
  (3, 'Gualaceo', 1),
  (4, 'Nabón', 1),
  (5, 'Paute', 1),
  (6, 'Pucara', 1),
  (7, 'San Fernando', 1),
  (8, 'Santa Isabel', 1),
  (9, 'Sigsig', 1),
  (10, 'Oña', 1),
  (11, 'Chordeleg', 1),
  (12, 'El Pan', 1),
  (13, 'Sevilla de Oro', 1),
  (14, 'Guachapala', 1),
  (15, 'Camilo Ponce Enríquez', 1),
  (16, 'Guaranda', 2),
  (17, 'Chillanes', 2),
  (18, 'Chimbo', 2),
  (19, 'Echeandía', 2),
  (20, 'San Miguel', 2),
  (21, 'Caluma', 2),
  (22, 'Las Naves', 2),
  (23, 'Azogues', 3),
  (24, 'Biblián', 3),
  (25, 'Cañar', 3),
  (26, 'La Troncal', 3),
  (27, 'El Tambo', 3),
  (28, 'Déleg', 3),
  (29, 'Suscal', 3),
  (30, 'Tulcán', 4),
  (31, 'Bolívar', 4),
  (32, 'Espejo', 4),
  (33, 'Mira', 4),
  (34, 'Montúfar', 4),
  (35, 'San Pedro de Huaca', 4),
  (36, 'Latacunga', 5),
  (37, 'La Maná', 5),
  (38, 'Pangua', 5),
  (39, 'Pujili', 5),
  (40, 'Salcedo', 5),
  (41, 'Saquisilí', 5),
  (42, 'Sigchos', 5),
  (43, 'Riobamba', 6),
  (44, 'Alausi', 6),
  (45, 'Colta', 6),
  (46, 'Chambo', 6),
  (47, 'Chunchi', 6),
  (48, 'Guamote', 6),
  (49, 'Guano', 6),
  (50, 'Pallatanga', 6),
  (51, 'Penipe', 6),
  (52, 'Cumandá', 6),
  (53, 'Machala', 7),
  (54, 'Arenillas', 7),
  (55, 'Atahualpa', 7),
  (56, 'Balsas', 7),
  (57, 'Chilla', 7),
  (58, 'El Guabo', 7),
  (59, 'Huaquillas', 7),
  (60, 'Marcabelí', 7),
  (61, 'Pasaje', 7),
  (62, 'Piñas', 7),
  (63, 'Portovelo', 7),
  (64, 'Santa Rosa', 7),
  (65, 'Zaruma', 7),
  (66, 'Las Lajas', 7),
  (67, 'Esmeraldas', 8),
  (68, 'Eloy Alfaro', 8),
  (69, 'Muisne', 8),
  (70, 'Quinindé', 8),
  (71, 'San Lorenzo', 8),
  (72, 'Atacames', 8),
  (73, 'Rioverde', 8),
  (74, 'La Concordia', 8),
  (75, 'Guayaquil', 9),
  (76, 'Alfredo Baquerizo Moreno (Juján)', 9),
  (77, 'Balao', 9),
  (78, 'Balzar', 9),
  (79, 'Colimes', 9),
  (80, 'Daule', 9),
  (81, 'Durán', 9),
  (82, 'El Empalme', 9),
  (83, 'El Triunfo', 9),
  (84, 'Milagro', 9),
  (85, 'Naranjal', 9),
  (86, 'Naranjito', 9),
  (87, 'Palestina', 9),
  (88, 'Pedro Carbo', 9),
  (89, 'Samborondón', 9),
  (90, 'Santa Lucía', 9),
  (91, 'Salitre (Urbina Jado)', 9),
  (92, 'San Jacinto de Yaguachi', 9),
  (93, 'Playas', 9),
  (94, 'Simón Bolívar', 9),
  (95, 'Coronel Marcelino Maridueña', 9),
  (96, 'Lomas de Sargentillo', 9),
  (97, 'Nobol', 9),
  (98, 'General Antonio Elizalde', 9),
  (99, 'Isidro Ayora', 9),
  (100, 'Ibarra', 10),
  (101, 'Antonio Ante', 10),
  (102, 'Cotacachi', 10),
  (103, 'Otavalo', 10),
  (104, 'Pimampiro', 10),
  (105, 'San Miguel de Urcuquí', 10),
  (106, 'Loja', 11),
  (107, 'Calvas', 11),
  (108, 'Catamayo', 11),
  (109, 'Celica', 11),
  (110, 'Chaguarpamba', 11),
  (111, 'Espíndola', 11),
  (112, 'Gonzanamá', 11),
  (113, 'Macará', 11),
  (114, 'Paltas', 11),
  (115, 'Puyango', 11),
  (116, 'Saraguro', 11),
  (117, 'Sozoranga', 11),
  (118, 'Zapotillo', 11),
  (119, 'Pindal', 11),
  (120, 'Quilanga', 11),
  (121, 'Olmedo', 11),
  (122, 'Babahoyo', 12),
  (123, 'Baba', 12),
  (124, 'Montalvo', 12),
  (125, 'Puebloviejo', 12),
  (126, 'Quevedo', 12),
  (127, 'Urdaneta', 12),
  (128, 'Ventanas', 12),
  (129, 'Vínces', 12),
  (130, 'Palenque', 12),
  (131, 'Buena Fé', 12),
  (132, 'Valencia', 12),
  (133, 'Mocache', 12),
  (134, 'Quinsaloma', 12),
  (135, 'Portoviejo', 13),
  (136, 'Bolívar', 13),
  (137, 'Chone', 13),
  (138, 'El Carmen', 13),
  (139, 'Flavio Alfaro', 13),
  (140, 'Jipijapa', 13),
  (141, 'Junín', 13),
  (142, 'Manta', 13),
  (143, 'Montecristi', 13),
  (144, 'Paján', 13),
  (145, 'Pichincha', 13),
  (146, 'Rocafuerte', 13),
  (147, 'Santa Ana', 13),
  (148, 'Sucre', 13),
  (149, 'Tosagua', 13),
  (150, '24 de Mayo', 13),
  (151, 'Pedernales', 13),
  (152, 'Olmedo', 13),
  (153, 'Puerto López', 13),
  (154, 'Jama', 13),
  (155, 'Jaramijó', 13),
  (156, 'San Vicente', 13),
  (157, 'Morona', 14),
  (158, 'Gualaquiza', 14),
  (159, 'Limón Indanza', 14),
  (160, 'Palora', 14),
  (161, 'Santiago', 14),
  (162, 'Sucúa', 14),
  (163, 'Huamboya', 14),
  (164, 'San Juan Bosco', 14),
  (165, 'Taisha', 14),
  (166, 'Logroño', 14),
  (167, 'Pablo Sexto', 14),
  (168, 'Tiwintza', 14),
  (169, 'Tena', 15),
  (170, 'Archidona', 15),
  (171, 'El Chaco', 15),
  (172, 'Quijos', 15),
  (173, 'Carlos Julio Arosemena Tola', 15),
  (174, 'Pastaza', 16),
  (175, 'Mera', 16),
  (176, 'Santa Clara', 16),
  (177, 'Arajuno', 16),
  (178, 'Quito', 17),
  (179, 'Cayambe', 17),
  (180, 'Mejia', 17),
  (181, 'Pedro Moncayo', 17),
  (182, 'Rumiñahui', 17),
  (183, 'San Miguel de Los Bancos', 17),
  (184, 'Pedro Vicente Maldonado', 17),
  (185, 'Puerto Quito', 17),
  (186, 'Ambato', 18),
  (187, 'Baños de Agua Santa', 18),
  (188, 'Cevallos', 18),
  (189, 'Mocha', 18),
  (190, 'Patate', 18),
  (191, 'Quero', 18),
  (192, 'San Pedro de Pelileo', 18),
  (193, 'Santiago de Píllaro', 18),
  (194, 'Tisaleo', 18),
  (195, 'Zamora', 19),
  (196, 'Chinchipe', 19),
  (197, 'Nangaritza', 19),
  (198, 'Yacuambi', 19),
  (199, 'Yantzaza (Yanzatza)', 19),
  (200, 'El Pangui', 19),
  (201, 'Centinela del Cóndor', 19),
  (202, 'Palanda', 19),
  (203, 'Paquisha', 19),
  (204, 'San Cristóbal', 20),
  (205, 'Isabela', 20),
  (206, 'Santa Cruz', 20),
  (207, 'Lago Agrio', 21),
  (208, 'Gonzalo Pizarro', 21),
  (209, 'Putumayo', 21),
  (210, 'Shushufindi', 21),
  (211, 'Sucumbíos', 21),
  (212, 'Cascales', 21),
  (213, 'Cuyabeno', 21),
  (214, 'Orellana', 22),
  (215, 'Aguarico', 22),
  (216, 'La Joya de Los Sachas', 22),
  (217, 'Loreto', 22),
  (218, 'Santo Domingo', 23),
  (219, 'Santa Elena', 24),
  (220, 'La Libertad', 24),
  (221, 'Salinas', 24);


-- laboratorio de ejemplo:
INSERT INTO laboratorio (nombre)
VALUES 
  ('Laboratorio Central Quito'),
  ('Laboratorio Médico Ecuador'),
  ('LabExpress Guayaquil'),
  ('Laboratorio Vida y Salud'),
  ('jajajja');
