# Spring WebFlux - Programación Reactiva

En este proyecto se trabaja una introducción a la **programación reactiva con Spring WebFlux**, utilizando `Mono` y `Flux`.

El objetivo principal es demostrar qué pasa cuando una operación tarda varios segundos y llegan varias peticiones al mismo tiempo, comparando una versión **reactiva** contra una versión **bloqueante**.

Los ejemplos se encuentran en:

```text
03-webflux/
├── 01-webflux-mono/
└── 02-webflux-flux/
```

---

## ¿Qué es Spring WebFlux y qué problema resuelve?

Spring WebFlux permite desarrollar aplicaciones reactivas y no bloqueantes.

En una aplicación tradicional, una operación lenta puede mantener ocupado un hilo mientras espera una respuesta. Si llegan muchas solicitudes al mismo tiempo, varios hilos pueden quedar bloqueados esperando.

WebFlux busca evitar ese problema permitiendo que el hilo quede disponible mientras una operación de entrada/salida todavía no termina.

Los dos tipos principales utilizados son:

```java
Mono<T>
Flux<T>
```

`Mono<T>` representa **cero o un elemento**, mientras que `Flux<T>` representa **cero o muchos elementos**.

---

## ¿Qué es un flujo?

Un flujo representa una secuencia de datos que pueden aparecer a lo largo del tiempo.

Puede emitir:

```text
onNext     -> aparece un dato
onError    -> ocurre un error
onComplete -> el flujo termina
```

Los flujos reactivos también son **lazy**. Esto significa que definir un `Mono` o un `Flux` no ejecuta inmediatamente el procesamiento.

Por ejemplo:

```java
Mono<Employee> empleado = repo.findById(1);
```

solamente define el flujo.

El procesamiento comienza cuando existe una **suscripción**. En los endpoints del proyecto, Spring WebFlux realiza esta suscripción automáticamente cuando llega una petición HTTP.

---

# 1. `Mono` y comparación reactivo vs bloqueante

El ejemplo se encuentra en:

```text
01-webflux-mono/
```

Las clases principales son:

```text
EmployeeRepository.java
EmployeeRestController.java
BloqueanteRestController.java
HiloRestController.java
```

En `EmployeeRepository.java` se simula una operación lenta de **5 segundos**.

## Versión reactiva

La versión reactiva utiliza:

```java
public Mono<Employee> findById(int id) {
    return Mono.justOrEmpty(tabla.get(id))
               .delayElement(LATENCIA);
}
```

y se expone mediante:

```text
GET /api/employees/{id}
```

desde `EmployeeRestController.java`.

`delayElement()` simula la espera sin mantener bloqueado el hilo durante los 5 segundos.

```text
Petición
   ↓
Mono<Employee>
   ↓
delayElement(5 s)
   ↓
el hilo puede quedar disponible
   ↓
Respuesta
```

---

## Versión bloqueante

En la misma clase existe:

```java
public Employee findByIdBloqueante(int id) {
    try {
        Thread.sleep(LATENCIA.toMillis());
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }

    return tabla.get(id);
}
```

Esta versión se utiliza desde `BloqueanteRestController.java`:

```text
GET /api/mvc/employees/{id}
```

Aquí:

```java
Thread.sleep(5000)
```

mantiene ocupado el hilo durante toda la espera.

```text
Petición
   ↓
Thread.sleep(5 s)
   ↓
hilo ocupado
   ↓
Respuesta
```

Las dos operaciones tardan aproximadamente lo mismo. La diferencia es que la versión reactiva no necesita mantener un hilo detenido mientras espera.

---

## Demostración con varias peticiones

El proyecto incluye:

```text
01-webflux-mono/scripts/comparar.sh
```

Este script lanza varias peticiones concurrentes contra:

```text
/api/employees/1
```

para la versión reactiva y:

```text
/api/mvc/employees/1
```

para la versión bloqueante.

Por ejemplo:

```bash
./scripts/comparar.sh 50
```

La comparación demuestra que WebFlux no hace que una operación de 5 segundos termine mágicamente más rápido.

Su ventaja aparece cuando llegan muchas solicitudes concurrentes, porque los hilos no permanecen ocupados solamente esperando.

`HiloRestController.java` también permite observar los hilos utilizados por Netty mediante:

```text
GET /api/hilo
```

---

# 2. `Flux` - cero o muchos elementos

El segundo ejemplo se encuentra en:

```text
02-webflux-flux/
```

Las clases principales son:

```text
SensorService.java
LecturaRestController.java
Lectura.java
```

En `SensorService.java` se genera una lectura cada segundo:

```java
public Flux<Lectura> lecturas() {
    return Flux.interval(CADENCIA)
               .map(this::medir);
}
```

`Flux.interval()` produce datos continuamente, por lo que el resultado es:

```java
Flux<Lectura>
```

que puede emitir muchos elementos.

---

## ¿Dónde se utiliza?

`LecturaRestController.java` expone el flujo de distintas maneras.

Como JSON:

```text
GET /api/lecturas
```

y como stream:

```text
GET /api/lecturas/stream
```

En el segundo caso se utiliza:

```java
MediaType.TEXT_EVENT_STREAM_VALUE
```

para enviar las lecturas conforme se generan.

También se utilizan operadores como:

```java
filter()
take()
takeUntil()
collectList()
```

---

# ¿Qué pasa si no se utiliza correctamente?

No utilizar WebFlux no significa que una aplicación esté mal. Spring MVC puede ser suficiente para muchos sistemas.

El problema aparece cuando existen:

```text
muchas peticiones concurrentes
+
operaciones lentas
+
hilos esperando
```

En ese escenario una implementación bloqueante puede provocar:

- hilos ocupados durante mucho tiempo;
- peticiones esperando turno;
- mayor tiempo de respuesta;
- menor capacidad para atender solicitudes concurrentes.

También es importante entender que usar WebFlux no elimina automáticamente los bloqueos.

Si dentro de WebFlux se utiliza:

```java
Thread.sleep(...)
```

o una dependencia bloqueante, el hilo seguirá quedando ocupado.

---

# ¿Cuándo WebFlux no vale la pena?

WebFlux puede no aportar una ventaja importante cuando:

- la aplicación recibe pocas solicitudes concurrentes;
- las operaciones son rápidas;
- la mayor parte del trabajo consume CPU;
- las dependencias utilizadas son bloqueantes;
- se utiliza JPA o JDBC tradicional dentro del flujo.

Su mayor beneficio aparece en aplicaciones con muchas operaciones de entrada/salida no bloqueantes, como llamadas a APIs, streaming o servicios de red.

En esos casos WebFlux permite manejar muchas conexiones utilizando pocos hilos.

---

# Cómo ejecutar

Para `Mono`:

```powershell
cd 01-webflux-mono
.\mvnw spring-boot:run
```

Para `Flux`:

```powershell
cd 02-webflux-flux
.\mvnw spring-boot:run
```

Para comparar la versión reactiva y bloqueante:

```bash
./scripts/comparar.sh 50
```

---

[← Volver a Semana 03](../README.md)
