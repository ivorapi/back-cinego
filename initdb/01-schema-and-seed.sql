CREATE TABLE usuarios (
  id BIGINT NOT NULL AUTO_INCREMENT, email VARCHAR(100) NOT NULL, nombre VARCHAR(100) NOT NULL,
  apellido VARCHAR(100) NOT NULL, password_hash VARCHAR(255) NOT NULL, fecha_nacimiento DATE, sexo VARCHAR(20),
  rol ENUM('ADMIN','CLIENTE','SUPER_ADMIN') NOT NULL,
  PRIMARY KEY (id), UNIQUE KEY uk_usuarios_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE salas (
  id BIGINT NOT NULL AUTO_INCREMENT, nombre VARCHAR(100) NOT NULL, PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE peliculas (
  id BIGINT NOT NULL AUTO_INCREMENT, titulo VARCHAR(150) NOT NULL, duracion INT NOT NULL,
  clasificacion DOUBLE NOT NULL, sinopsis TEXT, poster_url VARCHAR(255), PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE asientos (
  id BIGINT NOT NULL AUTO_INCREMENT, sala_id BIGINT NOT NULL, fila VARCHAR(10) NOT NULL, numero INT NOT NULL,
  PRIMARY KEY (id), CONSTRAINT fk_asiento_sala FOREIGN KEY (sala_id) REFERENCES salas(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE funciones (
  id BIGINT NOT NULL AUTO_INCREMENT, pelicula_id BIGINT NOT NULL, sala_id BIGINT NOT NULL,
  horario_inicio DATETIME(6) NOT NULL, precio DECIMAL(10,2) NOT NULL, PRIMARY KEY (id),
  CONSTRAINT uk_funcion_sala_horario UNIQUE (sala_id, horario_inicio),
  CONSTRAINT fk_funcion_pelicula FOREIGN KEY (pelicula_id) REFERENCES peliculas(id),
  CONSTRAINT fk_funcion_sala FOREIGN KEY (sala_id) REFERENCES salas(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE reservas (
  id BIGINT NOT NULL AUTO_INCREMENT, usuario_id BIGINT NOT NULL, funcion_id BIGINT NOT NULL,
  estado VARCHAR(20) NOT NULL, creada_en DATETIME(6) NOT NULL, total DECIMAL(10,2), PRIMARY KEY (id),
  CONSTRAINT fk_reserva_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
  CONSTRAINT fk_reserva_funcion FOREIGN KEY (funcion_id) REFERENCES funciones(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE entradas (
  id BIGINT NOT NULL AUTO_INCREMENT, reserva_id BIGINT NOT NULL, asiento_id BIGINT NOT NULL,
  funcion_id BIGINT NOT NULL, precio DECIMAL(10,2) NOT NULL, codigo VARCHAR(100) NOT NULL, PRIMARY KEY (id),
  UNIQUE KEY uk_entrada_codigo (codigo),
  CONSTRAINT fk_entrada_reserva FOREIGN KEY (reserva_id) REFERENCES reservas(id),
  CONSTRAINT fk_entrada_asiento FOREIGN KEY (asiento_id) REFERENCES asientos(id),
  CONSTRAINT fk_entrada_funcion FOREIGN KEY (funcion_id) REFERENCES funciones(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE descuentos (
  id BIGINT NOT NULL AUTO_INCREMENT, nombre VARCHAR(100) NOT NULL, porcentaje DOUBLE NOT NULL,
  activo BIT NOT NULL, aplica_a_todas BIT NOT NULL, PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE descuento_pelicula (
  descuento_id BIGINT NOT NULL, pelicula_id BIGINT NOT NULL, PRIMARY KEY (descuento_id, pelicula_id),
  CONSTRAINT fk_descuento_pelicula_descuento FOREIGN KEY (descuento_id) REFERENCES descuentos(id),
  CONSTRAINT fk_descuento_pelicula_pelicula FOREIGN KEY (pelicula_id) REFERENCES peliculas(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO usuarios (id,email,nombre,apellido,password_hash,fecha_nacimiento,sexo,rol) VALUES
  (1,'ana.garcia@cinego.test','Ana','Garcia','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','1998-04-15','F','ADMIN'),
  (2,'bruno.lopez@cinego.test','Bruno','Lopez','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','1995-09-02','M','ADMIN'),
  (4,'superadmin@cinego.com','Super','Admin','$2y$10$ECkvd9P5..F9ILIiEJTIYeCBpdSrn2od3TdeFc5gy.Won6u6szVeW',NULL,NULL,'SUPER_ADMIN');
INSERT INTO salas (id,nombre) VALUES (1,'Sala A'),(2,'Sala Premium');
INSERT INTO peliculas (id,titulo,duracion,clasificacion,sinopsis,poster_url) VALUES
  (1,'El viaje de Luna',112,8.2,'Una aventura espacial para toda la familia.','https://example.test/posters/luna.jpg'),
  (2,'Ciudad de sombras',126,7.6,'Un thriller ambientado en Buenos Aires.','https://example.test/posters/sombras.jpg');
INSERT INTO asientos (id,sala_id,fila,numero) VALUES
  (1,1,'A',1),(2,1,'A',2),(3,1,'B',1),(4,1,'B',2),(5,2,'A',1),(6,2,'A',2),(7,2,'B',1),(8,2,'B',2);
INSERT INTO funciones (id,pelicula_id,sala_id,horario_inicio,precio) VALUES
  (1,1,1,'2026-09-05 19:30:00',8500.00),(2,2,2,'2026-09-05 21:00:00',12000.00);
INSERT INTO reservas (id,usuario_id,funcion_id,estado,creada_en,total) VALUES
  (1,1,1,'CONFIRMADA','2026-09-01 10:15:00',17000.00),(2,2,2,'PENDIENTE','2026-09-02 16:40:00',12000.00);
INSERT INTO entradas (id,reserva_id,asiento_id,funcion_id,precio,codigo) VALUES
  (1,1,1,1,8500.00,'CNG-000001'),(2,1,2,1,8500.00,'CNG-000002'),(3,2,5,2,12000.00,'CNG-000003');
INSERT INTO descuentos (id,nombre,porcentaje,activo,aplica_a_todas) VALUES
  (1,'Promo estreno',15.00,b'1',b'0'),(2,'Descuento estudiante',10.00,b'1',b'1');
INSERT INTO descuento_pelicula (descuento_id,pelicula_id) VALUES (1,1);
