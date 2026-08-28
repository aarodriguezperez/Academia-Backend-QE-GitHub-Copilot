# Spring Data MongoDB

Este proyecto implementa un **CRUD de vehículos** utilizando **Spring Boot, Spring Data MongoDB y MongoDB**.

La base de datos utilizada es:

```text
vehiculo_directory
```

La colección utilizada es:

```text
vehiculos
```

La aplicación se conecta a MongoDB mediante:

```text
mongodb://localhost:27017/vehiculo_directory
```

MongoDB utiliza el puerto:

```text
27017
```

Para levantar la aplicación Spring Boot:

```powershell
.\mvnw spring-boot:run
```

La API queda disponible en:

```text
http://localhost:8081
```

Se utiliza un puerto diferente al Proyecto 1 para permitir que ambos proyectos puedan ejecutarse al mismo tiempo.

### Mongo Express

También se utilizó **Mongo Express** como interfaz gráfica para consultar la base de datos, las colecciones y los documentos almacenados en MongoDB.

Mongo Express se ejecuta con Docker mediante:

```powershell
docker run --name mongo-express `
  --network mongo-net `
  -p 8082:8081 `
  -e ME_CONFIG_MONGODB_URL="mongodb://mongo-8:27017/" `
  -e ME_CONFIG_BASICAUTH=false `
  -d mongo-express
```

Mongo Express queda disponible en:

```text
http://localhost:8082
```

Los principales puertos utilizados son:

```text
MongoDB          -> 27017
Spring Boot API  -> 8081
Mongo Express    -> 8082
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

En este proyecto el `id` es de tipo `String`, ya que MongoDB genera un `ObjectId` para identificar cada documento.

---

[← Volver a Semana 02](../README.md)
