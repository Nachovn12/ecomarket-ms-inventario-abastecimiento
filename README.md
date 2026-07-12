# MS Inventario y Abastecimiento

Microservicio responsable de gestionar productos de inventario, stock por tienda, ajustes manuales, pedidos de reabastecimiento y recepcion de mercancia para EcoMarket SPA.

## Responsable

| Campo                 | Detalle                                |
| --------------------- | -------------------------------------- |
| Responsable principal | Benjamin Palma                         |
| Rama de trabajo       | `feature/ms-inventario-abastecimiento` |
| Base de datos         | `bd_inventario`                        |
| Puerto local          | `8085`                                 |
| URL base local        | `http://localhost:8085`                |

## Que hace

- Registra productos disponibles para inventario.
- Consulta stock por producto, SKU, nombre, categoria o sucursal.
- Administra registros de inventario por tienda.
- Realiza ajustes manuales de stock con motivo.
- Gestiona pedidos de reabastecimiento (crear, aprobar, rechazar).
- Registra recepciones de mercancia e incrementa stock automaticamente.
- Expone respuestas REST con validaciones y manejo global de errores.

## Tecnologias

- Java 25
- Spring Boot 4.0.6
- Spring Web MVC
- Spring Data JPA / Hibernate
- MySQL
- Lombok
- Swagger UI
- Spring Actuator
- Maven
- JUnit + JaCoCo

## Estructura CSR

- `controller`: expone endpoints REST.
- `service`: concentra reglas de negocio y validaciones del dominio.
- `repository`: encapsula el acceso a datos con Spring Data JPA.
- `model`: contiene las clases persistentes JPA (`@Entity`, `@Table`, `@Id`).
- `dto`: define contratos de entrada y salida de la API.

## Configuracion

```text
src/main/resources/application.properties
```

```properties
spring.application.name=ms-inventario-abastecimiento
server.port=8085
spring.datasource.url=${INVENTARIO_DB_URL:jdbc:mysql://localhost:3306/bd_inventario}
spring.datasource.username=${DB_USER:root}
spring.datasource.password=${DB_PASSWORD:}
```

Crear la base de datos antes de ejecutar:

```sql
CREATE DATABASE IF NOT EXISTS bd_inventario
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
```

## Como ejecutar

```powershell
.\mvnw.cmd spring-boot:run
```

## Como probar

```powershell
.\mvnw.cmd clean test
```

## Reporte de cobertura JaCoCo

Ejecutar el siguiente comando para generar el reporte:

```powershell
.\mvnw.cmd clean test jacoco:report
```

El reporte HTML se genera en:

```text
target/site/jacoco/index.html
```

Abrir ese archivo en el navegador para ver la cobertura de codigo por clase, metodo y linea.

> **Nota:** Los puertos 8080 y 8082 estan reservados y no deben usarse. Este microservicio corre en el puerto `8085`.

## Swagger UI

```
http://localhost:8085/doc/swagger-ui.html
```

## Endpoints

### Inventario (Legado) — `/api/inventario`

| Metodo | Ruta                        | Descripcion                          |
| ------ | --------------------------- | ------------------------------------ |
| POST   | `/api/inventario`           | Crear registro de inventario         |
| GET    | `/api/inventario`           | Listar todos los registros           |
| GET    | `/api/inventario/{id}`      | Obtener inventario por ID            |
| PUT    | `/api/inventario/{id}/stock` | Actualizar stock (`?cantidad=N`)    |
| DELETE | `/api/inventario/{id}`      | Eliminar registro de inventario      |
| GET    | `/api/inventario/proveedores` | Listar proveedores del sistema     |

### Productos de Inventario — `/api/inventario/productos`

| Metodo | Ruta                                         | Descripcion                  |
| ------ | -------------------------------------------- | ---------------------------- |
| POST   | `/api/inventario/productos`                  | Agregar producto al inventario |
| GET    | `/api/inventario/productos`                  | Listar productos             |
| GET    | `/api/inventario/productos/{id}`             | Obtener producto por ID      |
| GET    | `/api/inventario/productos/sku/{sku}`        | Obtener producto por SKU     |
| GET    | `/api/inventario/productos/buscar/nombre`    | Buscar por nombre `?nombre=X` |
| GET    | `/api/inventario/productos/buscar/categoria` | Buscar por categoria `?categoria=X` |
| GET    | `/api/inventario/productos/buscar/sucursal`  | Buscar por sucursal `?sucursal=X` |
| PUT    | `/api/inventario/productos/{id}`             | Actualizar producto          |
| DELETE | `/api/inventario/productos/{id}`             | Eliminar producto            |

### Ajustes de Stock — `/api/inventario/ajustes-stock`

| Metodo | Ruta                                                      | Descripcion                     |
| ------ | --------------------------------------------------------- | ------------------------------- |
| POST   | `/api/inventario/ajustes-stock`                           | Registrar ajuste manual de stock |
| GET    | `/api/inventario/ajustes-stock`                           | Listar todos los ajustes        |
| GET    | `/api/inventario/ajustes-stock/producto/{productoId}`     | Historial de ajustes por producto |

### Pedidos de Reabastecimiento — `/api/inventario/pedidos-reabastecimiento`

| Metodo | Ruta                                                        | Descripcion                   |
| ------ | ----------------------------------------------------------- | ----------------------------- |
| POST   | `/api/inventario/pedidos-reabastecimiento`                  | Crear pedido de reabastecimiento |
| GET    | `/api/inventario/pedidos-reabastecimiento`                  | Listar pedidos                |
| GET    | `/api/inventario/pedidos-reabastecimiento/{id}`             | Obtener pedido por ID         |
| PUT    | `/api/inventario/pedidos-reabastecimiento/{id}/aprobar`     | Aprobar pedido                |
| PUT    | `/api/inventario/pedidos-reabastecimiento/{id}/rechazar`    | Rechazar pedido (`?motivo=X`) |

### Recepciones de Mercancia — `/api/inventario/recepciones-mercancia`

| Metodo | Ruta                                                      | Descripcion                       |
| ------ | --------------------------------------------------------- | --------------------------------- |
| POST   | `/api/inventario/recepciones-mercancia`                   | Registrar recepcion de mercancia  |
| GET    | `/api/inventario/recepciones-mercancia`                   | Listar todas las recepciones      |
| GET    | `/api/inventario/recepciones-mercancia/pedido/{pedidoId}` | Recepciones por pedido            |

## Ejemplo de uso

```http
GET http://localhost:8085/api/inventario/productos
```

```http
GET http://localhost:8085/api/inventario/productos/sku/ECO-001
```

## Diagramas

### Casos de uso

![Casos de uso MS Inventario y Abastecimiento](https://raw.githubusercontent.com/Nachovn12/ecomarket-spa-docs/main/docs/diagramas/casos-uso/diagrama-casos-uso-ms-inventario-abastecimiento.png)

### Diagrama de clases

![Diagrama de clases MS Inventario y Abastecimiento](https://raw.githubusercontent.com/Nachovn12/ecomarket-spa-docs/main/docs/diagramas/clases/diagrama-clases-ms-inventario-abastecimiento.png)

## Documentacion relacionada

- [Evidencia Postman](https://github.com/Nachovn12/ecomarket-spa-docs/blob/main/docs/postman/evidencia-postman.md)
- [Evidencias tecnicas](https://github.com/Nachovn12/ecomarket-spa-docs/tree/main/docs/evidencias-tecnicas)
- [Arquitectura de microservicios](https://github.com/Nachovn12/ecomarket-spa-docs/blob/main/docs/arquitectura/arquitectura-microservicios.md)
- [Bases de datos MySQL](https://github.com/Nachovn12/ecomarket-spa-docs/blob/main/docs/arquitectura/bases-datos-mysql.md)
- [Repositorio de documentacion](https://github.com/Nachovn12/ecomarket-spa-docs)
