# Taller Automotriz - Fundamentos de Java y POO

Este proyecto simula el funcionamiento básico de un **taller automotriz**, manejando clientes, mecánicos, vehículos, servicios, refacciones y órdenes de trabajo.

El objetivo del proyecto es aplicar de manera práctica diferentes conceptos de **Java y Programación Orientada a Objetos (POO)** dentro de un mismo escenario.

---

## Conceptos utilizados

### 1. Clases abstractas

Se usan en `Vehiculo`, `Persona` y `Servicio`.

Estas clases sirven como base para otras clases y no se crean directamente.

**Ejemplo:**

`Vehiculo` es la clase padre de:

* `Automovil`
* `Camioneta`
* `Motocicleta`

---

### 2. Herencia / IS-A

Se usa cuando una clase es un tipo de otra.

**Ejemplos:**

* `Automovil` **IS-A** `Vehiculo`
* `Camioneta` **IS-A** `Vehiculo`
* `Motocicleta` **IS-A** `Vehiculo`
* `Cliente` **IS-A** `Persona`
* `Mecanico` **IS-A** `Persona`
* `Reparacion` **IS-A** `Servicio`

---

### 3. HAS-A

Se usa cuando una clase contiene objetos de otra clase.

**Ejemplos:**

* `Cliente` tiene una lista de `Vehiculo`.
* `Orden` tiene `Cliente`, `Mecanico`, `Vehiculo`, `Servicio` y `Refaccion`.

---

### 4. Polimorfismo

Se usa con el método `costoInspeccion()` de `Vehiculo`.

`Automovil`, `Camioneta` y `Motocicleta` tienen su propia versión de este método.

Aunque las variables se declaran como `Vehiculo`, se ejecuta el método correspondiente a la clase real del objeto.

---

### 5. Upcasting

Se usa al guardar un objeto de una clase hija en una referencia de la clase padre.

**Ejemplo:**

```java
Vehiculo v1 = new Automovil(...);
```

El objeto sigue siendo `Automovil`, pero se maneja mediante una referencia de tipo `Vehiculo`.

---

### 6. Downcasting

Se usa para convertir una referencia de la clase padre nuevamente a una clase hija específica.

**Ejemplo:**

```java
if (v2 instanceof Camioneta) {
    Camioneta camioneta = (Camioneta) v2;
}
```

Esto permite utilizar métodos propios de `Camioneta`, como:

```java
getCapacidadCarga()
```

---

### 7. Interfaces

Se creó la interfaz `EstrategiaCosto`.

Esta interfaz contiene el método:

```java
calcularCosto(double subtotal)
```

La implementan:

* `CostoNormal`
* `CostoDescuento`

También se utilizan las interfaces:

* `Comparable`
* `Comparator`

---

### 8. Strategy

Se utiliza para cambiar la forma en que se calcula el total de una orden.

La orden puede utilizar:

* `CostoNormal`
* `CostoDescuento`

La estrategia se puede cambiar mediante:

```java
asignarEstrategiaCosto()
```

---

### 9. Singleton

Se utiliza en la clase `Taller`.

La clase tiene un constructor privado y el método:

```java
getInstancia()
```

Esto hace que durante la ejecución del programa se trabaje con una sola instancia de `Taller`.

En `Principal` se comprueba comparando:

```java
taller1 == taller2
```

---

### 10. Comparable

`Orden` implementa:

```java
Comparable<Orden>
```

Se utiliza `compareTo()` para ordenar las órdenes por su `id`.

Después se utiliza:

```java
Collections.sort(listaOrden);
```

---

### 11. Comparator

Se creó la clase `ComparatorPorCosto`.

Sirve para ordenar las órdenes utilizando otro criterio, en este caso el costo total.

Se utiliza con:

```java
Collections.sort(listaOrden, new ComparatorPorCosto());
```

---

### 12. Generics

Se utilizan para indicar qué tipo de objetos puede guardar una colección.

**Ejemplos:**

```java
List<Vehiculo>
List<Servicio>
List<Refaccion>
List<Orden>
```

También aparecen en:

```java
Comparable<Orden>
Comparator<Orden>
```

---

### 13. Lambdas

Se utiliza una expresión lambda para recorrer la lista de órdenes.

**Ejemplo:**

```java
listaOrden.forEach(orden ->
        System.out.println("Orden: " + orden.getIdOrden()));
```

La lambda toma cada orden de la lista y ejecuta una acción.

---

### 14. `final`

Se utiliza `final` en los identificadores.

**Ejemplos:**

* `idVehiculo`
* `idPersona`
* `idServicio`
* `idRefaccion`
* `idOrden`

Esto evita que el identificador cambie después de crear el objeto.

---

### 15. `static`

Se utiliza principalmente en el patrón Singleton.

**Ejemplo:**

```java
private static Taller instancia;
```

También se utiliza en el método `main`:

```java
public static void main(String[] args)
```

---

### 16. Constructores

Las clases utilizan constructores para recibir los datos iniciales de los objetos.

Las clases hijas utilizan:

```java
super(...)
```

para llamar al constructor de la clase padre.

---

### 17. Encapsulación

Los atributos de las clases se manejan principalmente como `private`.

Para acceder o modificar los datos se utilizan getters, setters o métodos controlados.

**Ejemplo:**

```java
actualizarKilometraje()
```

---

### 18. Getters y Setters

Se utilizan para consultar o cambiar atributos privados.

No todos los atributos tienen setter.

Por ejemplo, los identificadores son `final`, por lo que solamente tienen getter.

---

### 19. Exceptions

Se utiliza `IllegalArgumentException` para evitar valores incorrectos.

Un ejemplo se encuentra en:

```java
actualizarKilometraje()
```

Si se intenta colocar un kilometraje menor al actual, se lanza una excepción.

En `Principal` se utiliza `try-catch` para capturarla y permitir que el programa continúe funcionando.

---

### 20. Modificadores de acceso

#### `public`

Se utiliza en métodos y clases que deben poder utilizarse desde otros paquetes.

#### `private`

Se utiliza principalmente en los atributos para proteger los datos.

#### `default`

Se utiliza cuando no se escribe ningún modificador de acceso.

En el proyecto aparece en:

```java
calcularSubtotal()
```

de la clase `Orden`.

#### `protected`

No se agregó en el proyecto porque no había una necesidad real de utilizarlo.

---

### 21. `@Override`

Se utiliza `@Override` cuando una clase cambia la implementación de un método heredado o implementa un método definido por una interfaz.

**Ejemplos:**

* `costoInspeccion()`
* `calcularCosto()`
* `compareTo()`
* `compare()`
* `toString()`

---

## Resumen

El proyecto **Taller Automotriz** utiliza Programación Orientada a Objetos para manejar clientes, mecánicos, vehículos, servicios, refacciones y órdenes.

Los conceptos principales utilizados fueron:

* Clases abstractas
* Herencia
* IS-A
* HAS-A
* Polimorfismo
* Upcasting y Downcasting
* Interfaces
* Strategy
* Singleton
* Comparable y Comparator
* Generics
* Lambdas
* `final` y `static`
* Constructores
* Encapsulación
* Getters y Setters
* Excepciones
* Modificadores de acceso
* `@Override`
---
[← Volver a Semana 01](../README.md)

