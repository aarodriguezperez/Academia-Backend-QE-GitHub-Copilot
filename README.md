# Academia Backend Java - QE - GitHub - Copilot

Este repositorio contiene los proyectos y ejercicios realizados durante la Academia, organizados por semana.

El objetivo es aplicar de manera práctica los conceptos vistos durante las sesiones, comenzando con fundamentos de **Programación en Java** durante la primera semana, avanzando hacia el desarrollo de aplicaciones con **Spring Boot** y uso de **Docker** en esta segunda semana.

---

# Semana 1

Durante la primera semana se trabajaron conceptos fundamentales de **Java y Programación Orientada a Objetos (POO)** mediante ejercicios y un proyecto práctico.

Entre los principales conceptos revisados se encuentran:

* Clases y objetos
* Encapsulamiento
* Herencia
* Clases abstractas
* Interfaces
* Relaciones `IS-A` y `HAS-A`
* Polimorfismo
* Upcasting y Downcasting
* Generics
* `static`
* `final`
* Singleton
* Comparable y Comparator
* Expresiones Lambda
* Manejo de excepciones

---
## Proyecto 1 - Taller Automotriz

Como proyecto principal de la semana se desarrolló un programa en **Java puro** que simula parte del funcionamiento de un taller automotriz.

El proyecto utiliza diferentes clases para representar elementos como vehículos, clientes, mecánicos, servicios, refacciones y órdenes de trabajo.

Se creó una clase abstracta para representar un vehículo y diferentes clases que heredan de ella, permitiendo trabajar con distintos tipos de vehículos.

Ejemplo de la jerarquía:

```text
Vehiculo
├── Automovil
├── Motocicleta
└── Camioneta
```

También se utilizaron clases abstractas e interfaces para definir comportamientos comunes sin depender directamente de una implementación específica.

---

# Semana 2

Durante esta semana se desarrollaron tres programas enfocados en persistencia de datos con Spring Boot e inyección de dependencias en Java.

Para los primeros dos programas se utilizó como entidad principal **Vehiculo**, con los campos:

* `id`
* `marca`
* `modelo`
* `color`
* `anio`

---

## Proyecto 1 - Spring Data JPA con MySQL

El primer proyecto implementa un CRUD de vehículos utilizando **Spring Boot, Spring Data JPA y MySQL**.

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

## Proyecto 2 - Spring Data MongoDB

El segundo proyecto implementa el mismo CRUD de vehículos utilizando **Spring Boot, Spring Data MongoDB y MongoDB**.

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

## Proyecto 3 - Inyección de dependencias en Java

El tercer programa demuestra el funcionamiento de la **inyección de dependencias utilizando Java puro**, sin utilizar Spring.

Se creó la interfaz:

```text
Vehiculo
```

con dos implementaciones:

```text
Automovil
Camioneta
```

La clase `Orden` necesita un `Vehiculo`, pero no crea directamente un `Automovil` o una `Camioneta` dentro de la clase.

En su lugar, recibe la dependencia desde afuera por medio del constructor.

Ejemplo:

```java
Vehiculo vehiculo =
        new Automovil("Toyota", "Corolla", "blanco", 2026);

Orden o1 =
        new Orden(1, "Diego", "Torres", vehiculo);
```

Para cambiar el comportamiento solo es necesario cambiar la implementación:

```java
Vehiculo vehiculo =
        new Camioneta("Ford", "Lobo", "negro", 2021);
```

La clase `Orden` no necesita modificarse.

Esto es inyección de dependencias porque la clase recibe desde afuera el objeto que necesita, en lugar de crearlo por sí misma.

Esto ayuda a reducir el **acoplamiento**, ya que `Orden` depende de la interfaz `Vehiculo` y no directamente de `Automovil` o `Camioneta`.

También mejora la **testeabilidad**, porque se puede utilizar otra implementación de `Vehiculo` sin modificar la clase `Orden`.

Dentro del proyecto también se dejaron comentados ejemplos de:

* Inyección por constructor.
* Inyección por setter.
* Inyección por atributo.
* Versión sin inyección de dependencias para mostrar el alto acoplamiento.
