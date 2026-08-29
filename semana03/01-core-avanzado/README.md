# Core Avanzado - Threading, Manejo de Archivos y Serialización

En este proyecto se trabajan conceptos de **Core avanzado de Java**, principalmente manejo de archivos y serialización de objetos.

El bloque también incluye **threading**. En este caso no se desarrolló un ejercicio específico de hilos. Sin embargo, se incluye su explicación como parte de la documentación.

---

## 1. Threading

### ¿Qué es y qué problema resuelve?

Un hilo permite ejecutar una tarea dentro de un programa. Java permite trabajar con hilos utilizando herramientas como:

```java
Thread
Runnable
ExecutorService
```

El uso de varios hilos permite que distintas tareas puedan avanzar de manera concurrente, en lugar de esperar a que una termine completamente para comenzar la siguiente.

Por ejemplo, si un programa necesita procesar varios archivos, una ejecución secuencial tendría que procesarlos uno por uno. Utilizando varios hilos, diferentes tareas pueden realizarse de manera concurrente.

`ExecutorService` permite administrar un conjunto de hilos sin tener que crearlos y controlarlos manualmente uno por uno.

---

### El problema de la concurrencia

Utilizar varios hilos también puede generar problemas cuando dos o más de ellos intentan modificar el mismo dato.

Por ejemplo:

```java
contador++;
```

Aunque parece una sola operación, internamente implica leer el valor, modificarlo y volverlo a guardar.

Si dos hilos realizan esta operación al mismo tiempo, ambos podrían leer el mismo valor antes de actualizarlo y provocar que uno de los incrementos se pierda.

Java proporciona diferentes mecanismos para controlar el acceso concurrente a recursos compartidos, por ejemplo:

```java
synchronized
AtomicInteger
```
---

### ¿Dónde se ve en este proyecto?

En esta entrega no se incluye una implementación específica de threading.

Sin embargo, threading se relaciona directamente con los otros temas del proyecto, ya que en una aplicación real varios hilos podrían procesar información al mismo tiempo y posteriormente guardar los resultados utilizando manejo de archivos o serialización.

---

### ¿Qué pasa si no se controla correctamente?

Si todas las operaciones se realizan de forma secuencial, ciertas tareas pueden tardar más porque deben esperar a que termine la operación anterior.

Por otro lado, utilizar varios hilos sin controlar el acceso a datos compartidos puede producir:
- Actualizaciones perdidas.
- Datos incorrectos.
- Resultados diferentes entre ejecuciones.

Por esta razón, al trabajar con concurrencia también es necesario controlar el acceso a los recursos compartidos.

---

## 2. Manejo de archivos

### ¿Qué es y qué problema resuelve?

El manejo de archivos permite leer y escribir información almacenada en disco.

Esto permite que un programa trabaje con información que existe fuera de la memoria de la aplicación y que los datos puedan conservarse incluso después de terminar la ejecución.

En este proyecto se utiliza principalmente:

```java
java.nio.file.Files
```

junto con `Path`, streams y `try-with-resources`.

---

### ¿Dónde se ve en el código?

Los ejemplos se encuentran dentro del paquete:

```text
src/manejoArchivos/
```

y utilizan como archivo de entrada:

```text
data/origen.txt
```

---

### `PrincipalPath01.java`

Esta clase muestra diferentes formas de leer y escribir el contenido completo de un archivo.

Primero se trabaja con el contenido como un `String`:

```java
String string = Files.readString(input);
Files.writeString(output, string);
```

También se realiza la misma operación utilizando bytes:

```java
byte[] bytes = Files.readAllBytes(input);
Files.write(output, bytes);
```

Finalmente, el archivo se trabaja como una lista de líneas:

```java
List<String> lines = Files.readAllLines(input);
Files.write(output, lines);
```

Con estos ejemplos se observa que `java.nio.file.Files` permite manejar el mismo archivo de distintas formas dependiendo de lo que necesite el programa.

---

### `PrincipalPath02.java`

En esta clase se utiliza:

```java
Files.lines(path)
```

para obtener un:

```java
Stream<String>
```

El stream se utiliza dentro de un `try-with-resources`:

```java
try (Stream<String> s = Files.lines(path)) {
    s.forEach(System.out::println);
}
```

Esto permite procesar las líneas del archivo utilizando streams de Java y cerrar automáticamente el recurso al terminar.

---

### `PrincipalPath03.java`

Esta clase utiliza:

```java
Files.newBufferedReader(...)
Files.newBufferedWriter(...)
```

para leer y escribir el contenido del archivo utilizando buffers.

Los recursos se abren dentro de un `try-with-resources`:

```java
try (BufferedReader reader = Files.newBufferedReader(input);
     BufferedWriter writer = Files.newBufferedWriter(output)) {

    // Lectura y escritura
}
```

El uso de `try-with-resources` permite que Java cierre automáticamente los recursos aunque ocurra una excepción durante la ejecución.

---

### ¿Qué pasa si no se utiliza correctamente?

Si los datos solamente se almacenan en variables, desaparecen cuando termina la ejecución del programa.

El uso de archivos permite conservar o recuperar información desde disco.

Además, si se utilizan streams sin cerrarlos correctamente, pueden quedar recursos del sistema abiertos innecesariamente.

`try-with-resources` ayuda a evitar este problema porque se encarga de cerrar automáticamente los recursos utilizados.

---

## 3. Serialización

### ¿Qué es y qué problema resuelve?

La serialización permite convertir el estado de un objeto Java en una secuencia de bytes para almacenarlo en disco y posteriormente reconstruirlo.

Esto permite guardar un objeto completo sin tener que escribir manualmente cada uno de sus atributos en un archivo.

En este proyecto se serializan objetos de tipo `Gorilla`.

Los ejemplos se encuentran dentro del paquete:

```text
src/serializacion/
```

---

### `Gorilla.java`

La clase `Gorilla` implementa la interfaz:

```java
Serializable
```

por medio de:

```java
public class Gorilla implements Serializable {
```

Esto permite que sus objetos puedan ser utilizados por `ObjectOutputStream`.

La clase contiene atributos como:

```java
private String name;
private int age;
private Boolean friendly;
private transient String favoriteFood;
```

También se declara:

```java
private static final long serialVersionUID = 1L;
```

para controlar la compatibilidad de la clase durante la serialización y deserialización.

---

### `PrincipalObjectOutput.java`

Esta clase crea objetos `Gorilla` y los guarda en:

```text
data/gorillas.data
```

Para escribir los objetos se utiliza:

```java
ObjectOutputStream
```

dentro de un `try-with-resources`.

La operación principal es:

```java
out.writeObject(gorilla);
```

Esta línea convierte el estado del objeto en información que puede almacenarse dentro del archivo.

El flujo es:

```text
Objeto Gorilla
      ↓
ObjectOutputStream
      ↓
writeObject()
      ↓
gorillas.data
```

---

### `PrincipalObjectInput.java`

Esta clase realiza el proceso contrario.

El archivo se abre utilizando:

```java
ObjectInputStream
```

y los objetos se recuperan mediante:

```java
in.readObject();
```

El proceso es:

```text
gorillas.data
      ↓
ObjectInputStream
      ↓
readObject()
      ↓
Objeto Gorilla
```

De esta manera, los objetos guardados anteriormente pueden volver a utilizarse dentro del programa.

---

## ¿Para qué sirve `serialVersionUID`?

En `Gorilla.java` se utiliza:

```java
private static final long serialVersionUID = 1L;
```

`serialVersionUID` funciona como un identificador de versión para una clase serializable.

Cuando `PrincipalObjectOutput` ejecuta:

```java
out.writeObject(gorilla);
```

Java guarda el objeto junto con información sobre la clase utilizada para crearlo.

En este caso, el objeto fue creado utilizando una clase `Gorilla` con:

```text
serialVersionUID = 1
```

Cuando posteriormente `PrincipalObjectInput` ejecuta:

```java
in.readObject();
```

Java compara el identificador almacenado con el de la clase `Gorilla` que existe actualmente.

Mientras ambos coincidan:

```text
Archivo gorillas.data       Clase Gorilla actual
---------------------       ---------------------
serialVersionUID = 1   ==   serialVersionUID = 1
```

Java permite reconstruir el objeto.

Si después de guardar los objetos se cambiara la clase a:

```java
private static final long serialVersionUID = 2L;
```

el archivo anterior seguiría teniendo la versión `1`.

La comparación sería:

```text
gorillas.data               Gorilla actual
-------------               --------------
UID = 1                !=   UID = 2
```

Java podría considerar ambas versiones incompatibles y lanzar:

```text
InvalidClassException
```

Declarar `serialVersionUID` manualmente permite tener control sobre la compatibilidad de los objetos serializados cuando la clase cambia.

Si no se declara, Java puede generar automáticamente un identificador a partir de la estructura de la clase, pero algunos cambios en la clase pueden provocar que dicho identificador también cambie.

---

## ¿Qué ocurre con `transient`?

En `Gorilla` se tiene el atributo:

```java
private transient String favoriteFood;
```

La palabra `transient` indica que ese atributo **no debe almacenarse durante la serialización**.

Por ejemplo, se puede crear:

```java
new Gorilla("Koko", 12, true, "Bananas");
```

Antes de serializar, el objeto contiene:

```text
name = Koko
age = 12
friendly = true
favoriteFood = Bananas
```

Después de utilizar:

```java
writeObject()
```

y posteriormente:

```java
readObject()
```

el resultado será similar a:

```text
Gorilla [
    name=Koko,
    age=12,
    friendly=true,
    favoriteFood=null
]
```

`favoriteFood` aparece como `null` porque ese campo fue marcado como `transient` y por lo tanto no fue almacenado en `gorillas.data`.

Esto puede ser útil cuando un objeto contiene información que no debe guardarse como parte de su estado serializado.

---

### ¿Qué pasa si no se utiliza serialización?

Si se intenta ejecutar:

```java
writeObject(...)
```

sobre un objeto cuya clase no implementa `Serializable`, Java no puede serializarlo de esta manera y puede lanzar:

```text
NotSerializableException
```

Sin serialización sería necesario utilizar otro mecanismo para guardar el estado del objeto, por ejemplo:

- Archivos de texto.
- JSON.
- XML.
- Una base de datos.

En este ejercicio, `Serializable`, `ObjectOutputStream` y `ObjectInputStream` permiten guardar y recuperar directamente objetos Java.

---

## Cómo ejecutar el proyecto

Desde la carpeta `01-core-avanzado`:

```powershell
javac -d out src/manejoArchivos/*.java src/serializacion/*.java
```

```powershell
java -cp out serializacion.PrincipalObjectOutput; java -cp out serializacion.PrincipalObjectInput
```

```powershell
java -cp out manejoArchivos.PrincipalPath01; java -cp out manejoArchivos.PrincipalPath02; java -cp out manejoArchivos.PrincipalPath03
```

Los comandos fueron probados en la terminal de Windows PowerShell.

---

[← Volver a Semana 03](../README.md)
