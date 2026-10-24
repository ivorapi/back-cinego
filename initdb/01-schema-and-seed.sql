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

CREATE TABLE candy (
  id BIGINT NOT NULL AUTO_INCREMENT, nombre VARCHAR(100) NOT NULL, descripcion TEXT,
  precio DECIMAL(10,2) NOT NULL, imagen_url VARCHAR(255), PRIMARY KEY (id)
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
-- Clientes de desarrollo: password Cinego123!
INSERT INTO usuarios (id,email,nombre,apellido,password_hash,fecha_nacimiento,sexo,rol) VALUES
  (5,'cliente@cinego.test','Lucia','Perez','$2a$10$RF.wn0y5WODGbn0tYqk1IuuKrPg7OLmrpytepZKikDh26VbP/YqSG','1998-05-12','F','CLIENTE'),
  (6,'otro.cliente@cinego.test','Martin','Diaz','$2a$10$RF.wn0y5WODGbn0tYqk1IuuKrPg7OLmrpytepZKikDh26VbP/YqSG','1995-08-20','M','CLIENTE');
INSERT INTO peliculas
  (id, titulo, duracion, clasificacion, sinopsis, poster_url)
VALUES
  (
    1,
    'La odisea',
    150,
    18,
    'Una epopeya mitológica que sigue la historia de Odiseo y su largo viaje a casa, de 10 años de duración, tras la guerra de Troya.',
    'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRnYKwLld4fwjRJ0SdxGc_WPNnZm9zUhL8WxEB7_6QUmQ&s=10'
  ),
  (
    2,
    'Spiderman: brand new day',
    105,
    14,
    'Tras el éxito mundial sin precedentes de Spider-Man: Sin regreso a casa, Spider-Man: Un nuevo día marca un capítulo completamente nuevo para Peter Parker y Spider-Man. Han pasado cuatro años desde los acontecimientos de Sin regreso a casa, y Peter ahora es un adulto que vive completamente solo, habiéndose borrado voluntariamente de la vida y los recuerdos de sus seres queridos.',
    'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRV4Ymhbyu4rSv8b7zluLsjzjkvZ4DtLXOjbp_OR-Rx2A&s=10'
  ),
  (
    3,
    'Dune: Part 3',
    130,
    18,z
    'Duna: Parte Tres se ambienta casi dos décadas después de que Paul Atreides tomó el control del Imperio. Convertido ahora en un despiadado Emperador, Paul deberá enfrentar las consecuencias de su reinado a medida que regresan viejos aliados, surgen aterradoras amenazas nuevas y la traición acecha en cada sombra.',
    'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSn6zu1u7Xudb3SG7pMkMZvIguVLOGU-JSs6Jd6FWZ9XA&s=10'
  );

INSERT INTO candy (id, nombre, descripcion, precio, imagen_url) VALUES
  (1, 'Pochoclos clásicos', 'Pochoclos salados recién preparados.', 4500.00, NULL),
  (2, 'Pochoclos con caramelo', 'Pochoclos dulces cubiertos con caramelo.', 5200.00, NULL),
  (3, 'Nachos con queso', 'Porción de nachos con salsa de queso.', 4800.00, NULL),
  (4, 'Gaseosa grande', 'Gaseosa de 750 ml.', 3500.00, NULL),
  (5, 'Combo clásico', 'Pochoclos clásicos y dos gaseosas grandes.', 10500.00, NULL);

INSERT INTO asientos (id,sala_id,fila,numero) VALUES
  (1,1,'A',1),(2,1,'A',2),(3,1,'B',1),(4,1,'B',2),(5,2,'A',1),(6,2,'A',2),(7,2,'B',1),(8,2,'B',2),
  (9,1,'A',3),(10,1,'A',4),(11,1,'A',5),(12,1,'A',6),(13,1,'A',7),(14,1,'A',8),
  (15,1,'B',3),(16,1,'B',4),(17,1,'B',5),(18,1,'B',6),(19,1,'B',7),(20,1,'B',8),
  (21,1,'C',1),(22,1,'C',2),(23,1,'C',3),(24,1,'C',4),(25,1,'C',5),(26,1,'C',6),(27,1,'C',7),(28,1,'C',8),
  (29,2,'A',3),(30,2,'A',4),(31,2,'A',5),(32,2,'A',6),(33,2,'A',7),(34,2,'A',8),
  (35,2,'B',3),(36,2,'B',4),(37,2,'B',5),(38,2,'B',6),(39,2,'B',7),(40,2,'B',8),
  (41,2,'C',1),(42,2,'C',2),(43,2,'C',3),(44,2,'C',4),(45,2,'C',5),(46,2,'C',6),(47,2,'C',7),(48,2,'C',8);
-- Fechas de ejemplo: 24 y 25 de octubre de 2026.
INSERT INTO funciones (id,pelicula_id,sala_id,horario_inicio,precio) VALUES
  (1,1,1,'2026-10-24 19:30:00',8500.00),(2,2,2,'2026-10-24 18:00:00',12000.00),
  (3,3,1,'2026-10-24 22:30:00',9500.00),(4,1,2,'2026-10-24 21:00:00',12000.00),
  (5,2,1,'2026-10-25 19:30:00',8500.00),(6,3,2,'2026-10-25 21:00:00',13000.00);
INSERT INTO reservas (id,usuario_id,funcion_id,estado,creada_en,total) VALUES
  (1,5,1,'CONFIRMADA','2026-10-23 10:15:00',17000.00),
  (2,6,1,'PENDIENTE','2026-10-23 11:00:00',8500.00),
  (3,5,1,'CANCELADA','2026-10-23 09:00:00',8500.00),
  (4,5,2,'PENDIENTE','2026-10-23 11:30:00',24000.00),
  (5,6,3,'CONFIRMADA','2026-10-23 12:00:00',9500.00),
  (6,6,4,'CANCELADA','2026-10-23 12:30:00',12000.00);
INSERT INTO entradas (id,reserva_id,asiento_id,funcion_id,precio,codigo) VALUES
  (1,1,1,1,8500.00,'CNG-DEMO-000001'),(2,1,2,1,8500.00,'CNG-DEMO-000002'),
  (3,2,3,1,8500.00,'CNG-DEMO-000003'),(4,3,4,1,8500.00,'CNG-DEMO-000004'),
  (5,4,5,2,12000.00,'CNG-DEMO-000005'),(6,4,6,2,12000.00,'CNG-DEMO-000006'),
  (7,5,4,3,9500.00,'CNG-DEMO-000007'),(8,6,7,4,12000.00,'CNG-DEMO-000008');
INSERT INTO descuentos (id,nombre,porcentaje,activo,aplica_a_todas) VALUES
  (1,'Promo estreno',15.00,b'1',b'0'),(2,'Descuento estudiante',10.00,b'1',b'1');
INSERT INTO descuento_pelicula (descuento_id,pelicula_id) VALUES (1,1);
