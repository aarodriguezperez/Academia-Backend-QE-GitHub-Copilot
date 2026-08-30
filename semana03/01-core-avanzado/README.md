# Core Avanzado - Threading, Manejo de Archivos y Serialización

En este proyecto se trabajan conceptos de **Core avanzado de Java**: threading, manejo de archivos y serialización.

Para threading no se desarrolló un ejercicio específico, pero se documenta el concepto. El manejo de archivos y la serialización sí cuentan con ejemplos dentro del proyecto.

---

## 1. Threading

### ¿Qué es y qué problema resuelve?

Un hilo permite ejecutar una tarea dentro de un programa. Java permite trabajar con concurrencia mediante herramientas como:

```java
Thread
Runnable
ExecutorService
```

Utilizar varios hilos permite que diferentes tareas puedan avanzar de manera concurrente en lugar de ejecutarse siempre una después de otra.

`ExecutorService`, por ejemplo, permite administrar varios hilos sin tener que crearlos y controlarlos manualmente.

### El problema de la concurrencia

Cuando varios hilos modifican el mismo dato pueden aparecer problemas.

Por ejemplo:

```java
contador++;
```

no es necesariamente una operación atómica. Si dos hilos modifican el contador al mismo tiempo, puede perderse alguna actualización.

Para proteger recursos compartidos Java proporciona mecanismos como:

```java
synchronized
AtomicInteger
```

### ¿Dónde se ve en este proyecto?

En esta entrega no se incluye una implementación específica de threading.

Sin embargo, se relaciona con los demás conceptos porque varios hilos podrían procesar información al mismo tiempo y posteriormente almacenar los resultados mediante archivos o serialización.

### ¿Qué pasa si no se controla correctamente?

Sin concurrencia, algunas tareas pueden tardar más al ejecutarse solamente de forma secuencial.

Pero usar varios hilos sin controlar los datos compartidos puede provocar:

- Actualizaciones perdidas.
- Datos incorrectos.
- Resultados diferentes entre ejecuciones.

---

## 2. Manejo de archivos

### ¿Qué es y qué problema resuelve?

El manejo de archivos permite leer y escribir información en disco para que los datos puedan utilizarse fuera de la memoria del programa.

En este proyecto se utiliza principalmente:

```java
java.nio.file.Files
```

junto con streams y `try-with-resources`.

Los ejemplos están en:

```text
src/manejoArchivos/
```

y utilizan como entrada:

```text
data/origen.txt
```

### ¿Dónde se ve en el código?

#### `PrincipalPath01.java`

Muestra distintas formas de leer y escribir un archivo.

Como texto:

```java
String string = Files.readString(input);
Files.writeString(output, string);
```

Como bytes:

```java
byte[] bytes = Files.readAllBytes(input);
Files.write(output, bytes);
```

Y como líneas:

```java
List<String> lines = Files.readAllLines(input);
Files.write(output, lines);
```

#### `PrincipalPath02.java`

Utiliza:

```java
Files.lines(path)
```

para obtener un `Stream<String>`:

```java
try (Stream<String> s = Files.lines(path)) {
    s.forEach(System.out::println);
}
```

#### `PrincipalPath03.java`

Utiliza:

```java
Files.newBufferedReader(...)
Files.newBufferedWriter(...)
```

dentro de un `try-with-resources` para leer y escribir utilizando buffers.

El `try-with-resources` permite cerrar automáticamente los recursos al terminar, incluso si ocurre una excepción.

### ¿Qué pasa si no se utiliza correctamente?

Si los datos solamente se guardan en variables, desaparecen cuando termina la aplicación.

Además, dejar streams o lectores abiertos innecesariamente puede mantener recursos del sistema ocupados. `try-with-resources` ayuda a evitar este problema.

---

## 3. Serialización

### ¿Qué es y qué problema resuelve?

La serialización permite convertir el estado de un objeto Java en bytes para guardarlo en disco y posteriormente reconstruirlo.

Los ejemplos están en:

```text
src/serializacion/
```

y trabajan con objetos de tipo `Gorilla`.

### ¿Dónde se ve en el código?

#### `Gorilla.java`

La clase implementa:

```java
public class Gorilla implements Serializable {
```

Esto permite que sus objetos puedan serializarse.

También contiene:

```java
private static final long serialVersionUID = 1L;
private transient String favoriteFood;
```

#### `PrincipalObjectOutput.java`

Guarda objetos `Gorilla` en:

```text
data/gorillas.data
```

mediante:

```java
ObjectOutputStream
```

La operación principal es:

```java
out.writeObject(gorilla);
```

El flujo es:

```text
Objeto Gorilla
      ↓
ObjectOutputStream
      ↓
gorillas.data
```

#### `PrincipalObjectInput.java`

Realiza el proceso contrario mediante:

```java
ObjectInputStream
```

y:

```java
in.readObject();
```

El archivo se lee y los objetos `Gorilla` son reconstruidos nuevamente en memoria.

---

## ¿Para qué sirve `serialVersionUID`?

En `Gorilla.java` se declara:

```java
private static final long serialVersionUID = 1L;
```

Este valor identifica la versión de una clase serializable.

Cuando se ejecuta:

```java
out.writeObject(gorilla);
```

el objeto se guarda asociado a esa versión.

Después, cuando se ejecuta:

```java
in.readObject();
```

Java compara el `serialVersionUID` guardado con el de la clase actual.

Si ambos son:

```text
1 == 1
```

el objeto puede reconstruirse.

Si el archivo fue creado con:

```text
serialVersionUID = 1
```

pero la clase cambia a:

```java
private static final long serialVersionUID = 2L;
```

Java puede considerar ambas versiones incompatibles y lanzar:

```text
InvalidClassException
```

Declararlo manualmente permite controlar mejor la compatibilidad cuando la clase cambia.

---

## ¿Qué ocurre con `transient`?

En `Gorilla` se utiliza:

```java
private transient String favoriteFood;
```

`transient` indica que ese atributo no debe guardarse durante la serialización.

Por ejemplo:

```java
new Gorilla("Koko", 12, true, "Bananas");
```

después de serializarse y volver a leerse puede mostrar:

```text
Gorilla [
    name=Koko,
    age=12,
    friendly=true,
    favoriteFood=null
]
```

`favoriteFood` queda como `null` porque no fue almacenado en el archivo.

### ¿Qué pasa si no se utiliza serialización?

Si se intenta utilizar:

```java
writeObject(...)
```

sobre un objeto cuya clase no implementa `Serializable`, puede producirse:

```text
NotSerializableException
```

Sin serialización habría que utilizar otro mecanismo para almacenar el objeto, como JSON, texto o una base de datos.

---

## Cómo ejecutar el proyecto

Desde `01-core-avanzado`:

```powershell
javac -d out src/manejoArchivos/*.java src/serializacion/*.java
```

Para probar la serialización:

```powershell
java -cp out serializacion.PrincipalObjectOutput; java -cp out serializacion.PrincipalObjectInput
```

Para probar el manejo de archivos:

```powershell
java -cp out manejoArchivos.PrincipalPath01; java -cp out manejoArchivos.PrincipalPath02; java -cp out manejoArchivos.PrincipalPath03
```

Los comandos fueron probados en Windows PowerShell.

---

[← Volver a Semana 03](../README.md)
