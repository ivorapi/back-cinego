# CineGo

## Asientos y reservas

`GET /api/funciones/{id}/asientos` es público y devuelve un array ordenado por
fila y número: `[{"id":1,"fila":"A","numero":1,"ocupado":false}]`.
La ocupación corresponde a esa función: las reservas pendientes y confirmadas
ocupan asientos; las canceladas los liberan. Una función inexistente devuelve 404.

`POST /api/reservas` mantiene el cuerpo `{"funcionId":1,"asientoIds":[1,2]}`.
Las respuestas de creación, consulta, listado de mis reservas y cancelación
conservan `idsEntradas` y agregan `asientos`, con `id`, `fila` y `numero`.
Las reservas simultáneas para una misma función se serializan con un bloqueo
transaccional; intentar reservar un asiento ocupado devuelve 409.

La prueba `ReservasHttpTest` inicia la API con H2 en un puerto aleatorio y valida
el flujo mediante HTTP real, incluyendo autenticación, cancelación y concurrencia.

CineGo es una API backend para la gestión de un sistema de cine.
Permite administrar películas, salas y funciones de proyección.
También contempla usuarios, reservas, entradas, asientos y estados de reserva.
Incluye descuentos configurables y cálculo del precio final de las entradas.
Su alcance comprende la exposición de estos servicios mediante una API REST.

## Datos de ejemplo para reservas

El seed de `initdb/01-schema-and-seed.sql` incluye 24 asientos por sala
(filas A, B y C; números del 1 al 8) y seis funciones para el 24 y 25 de octubre
de 2026. Cada cliente tiene reservas propias para probar `mis-reservas` y los
controles de acceso. Credenciales exclusivas de desarrollo:

| Email | Contraseña |
| --- | --- |
| `cliente@cinego.test` | `Cinego123!` |
| `otro.cliente@cinego.test` | `Cinego123!` |

| Función | Estado esperado del mapa |
| --- | --- |
| 1, Sala A | A1 y A2 confirmados; B1 pendiente; B2 libre por cancelación |
| 2, Sala Premium | A1 y A2 pendientes; el resto libre |
| 3, Sala A | B2 confirmado; A1, A2 y B1 libres, a diferencia de la función 1 |
| 4, Sala Premium | Todos libres, con una reserva cancelada de B1 |
| 5 y 6 | Todos libres, sin reservas |

Por ejemplo, con el JWT de cualquiera de esos clientes,
`POST /api/reservas` con `{"funcionId":1,"asientoIds":[4,9]}` reserva B2 y A3;
intentar reservar el asiento 1 para esa función devuelve 409.

Estos datos se cargan automáticamente **solo al inicializar una base vacía**.
No se reemplazan reservas ni usuarios en una base existente. No vuelvas a ejecutar
el script completo sobre ella: también contiene la creación de tablas.

## Aspectos técnicos

- Desarrollado con Java 17 y Spring Boot.
- Persistencia mediante Spring Data JPA y MySQL.
- Seguridad gestionada con Spring Security.
- Documentación automática de endpoints con OpenAPI y Swagger UI.
- Construcción y ejecución mediante Maven Wrapper.

## Catálogo de películas de desarrollo

### Base nueva

Desde la raíz del repositorio, con Docker en ejecución:

```bash
docker compose up -d --build
```

Este comando compila y levanta la API en `http://localhost:8080` junto con MySQL.
Swagger UI queda disponible en `http://localhost:8080/swagger-ui.html`.
La API espera a que MySQL pase su healthcheck y se conecta por la red interna de
Compose. Para levantar solamente la base y ejecutar Java desde el IDE, usá
`docker compose up -d mysql`.

Cuando el volumen de MySQL está vacío, Docker ejecuta `initdb/01-schema-and-seed.sql`.
El catálogo inicial incluye La odisea, Spiderman: brand new day y Dune: Part 3.
La duración se guarda en minutos y la clasificación representa la edad mínima.

### Base existente

Actualizar el repositorio o reiniciar Docker no vuelve a ejecutar el script inicial
si el volumen ya contiene una base. Para actualizar el catálogo, con el contenedor
`cinego-mysql` en ejecución, ejecutá desde la raíz del repositorio:

```bash
docker exec -i cinego-mysql sh -c 'MYSQL_PWD="$MYSQL_ROOT_PASSWORD" exec mysql --default-character-set=utf8mb4 -uroot cinego' < scripts/actualizar-peliculas.sql
```

El comando usa la contraseña configurada en el contenedor. Si la contraseña de root
se cambió directamente en MySQL, deberá utilizarse la credencial actual.

Este script es para bases de desarrollo: reemplaza los datos de las películas con
IDs 1, 2 y 3; las inserta si faltan. Conserva los IDs, sus relaciones y las demás
películas. No elimina registros de prueba como Test. Puede repetirse sin duplicar
esas películas, pero volverá a aplicar los valores definidos en el script.

Para comprobar el resultado:

```bash
docker exec cinego-mysql sh -c 'MYSQL_PWD="$MYSQL_ROOT_PASSWORD" exec mysql --default-character-set=utf8mb4 -uroot cinego -e "SELECT id, titulo, duracion, clasificacion FROM peliculas WHERE id IN (1,2,3) ORDER BY id;"'
```

Después, recargá el frontend para consultar los datos actualizados.
No es necesario borrar el volumen ni ejecutar nuevamente el script completo de
creación de tablas sobre una base existente.
