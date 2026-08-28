# Spring Data JPA con MySQL

Este proyecto implementa un **CRUD de vehículos** utilizando **Spring Boot, Spring Data JPA y MySQL**.

El objetivo es aplicar persistencia de datos en una base de datos relacional utilizando Spring Data JPA y exponer las operaciones mediante una API REST.

---

La base de datos utilizada es:

```text
vehiculo_directory
```

La tabla utilizada es:

```text
vehiculo
```

Dentro del proyecto se incluye el archivo `vehiculo.sql`, encargado de crear la base de datos, la tabla y registros de prueba.

Para levantar la aplicación:

```powershell
.\mvnw spring-boot:run
```

La API queda disponible en:

```text
http://localhost:8070
```
Los principales puertos utilizados son:

```text
MySQL            -> 3306
Spring Boot API  -> 8070
```

### Endpoints

```text
GET    /api/vehiculos
GET    /api/vehiculos/{vehiculoId}
POST   /api/vehiculos
PUT    /api/vehiculos
PATCH  /api/vehiculos/{vehiculoId}
DELETE /api/vehiculos/{vehiculoId}
```

En este proyecto el `id` es de tipo `int` y MySQL lo genera automáticamente mediante `AUTO_INCREMENT`.

---

[← Volver a Semana 02](./README.md)
