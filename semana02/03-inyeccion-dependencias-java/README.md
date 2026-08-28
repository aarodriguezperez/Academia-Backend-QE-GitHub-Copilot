# Inyección de dependencias en Java

Este proyecto demuestra el funcionamiento de la **inyección de dependencias utilizando Java puro**, sin utilizar Spring.

El objetivo es comprender cómo una clase puede recibir las dependencias que necesita desde el exterior, reduciendo el acoplamiento entre clases.

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

---

[← Volver a Semana 02](../README.md)
