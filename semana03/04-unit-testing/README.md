# Unit Testing - JUnit y Mockito

En este proyecto se trabajan **pruebas unitarias con JUnit y Mockito**.

El objetivo es comprobar que el código se comporte correctamente tanto en casos válidos como en situaciones de error, y demostrar que una suite en verde no significa necesariamente que todo esté bien probado.

Los ejemplos están separados en:

```text
04-unit-testing/
├── 01-junit-fundamentos/
├── 02-junit-catalogo/
├── 03-junit-datos/
└── 04-mockito-dobles/
```

---

## 1. JUnit

### ¿Qué es y qué problema resuelve?

JUnit permite automatizar pruebas sobre pequeñas partes del código.

En lugar de ejecutar manualmente el programa después de cada cambio, los tests permiten comprobar de forma repetible que los métodos siguen produciendo los resultados esperados.

En estos proyectos se utilizan aserciones como:

```java
assertEquals(...)
assertTrue(...)
assertFalse(...)
assertThrows(...)
```

---

### ¿Dónde se ve en el código?

El ejemplo principal está en:

```text
01-junit-fundamentos/
```

En `AsercionesTest.java` se utilizan distintas aserciones para comprobar el comportamiento de `Boleta`.

Por ejemplo:

```java
assertEquals(80.0, boleta.promedio());
assertTrue(boleta.aprobado());
```

---

### `@BeforeEach`

En clases como `CicloDeVidaTest.java` se utiliza:

```java
@BeforeEach
void matricularAlumno() {
    boleta = new Boleta(new Alumno("A01", "Ana Torres"));
}
```

Esto permite crear un escenario limpio antes de cada test y evita que una prueba dependa del estado dejado por otra.

También aparece en `ServicioInscripcionTest.java`, donde antes de cada prueba se crea un curso real:

```java
@BeforeEach
void cursoRealConDosLugares() {
    java101 = new Curso("JAVA-101", 2);
}
```

---

### Camino de error con `assertThrows`

Los tests no solamente comprueban casos correctos.

En `BoletaTest.java` se verifica que una calificación fuera del rango permitido genere una excepción:

```java
assertThrows(
    IllegalArgumentException.class,
    () -> boleta.registrar("Java", 101)
);
```

También se prueba:

```java
boleta.registrar("Java", -1)
```

De esta manera se comprueba el comportamiento esperado cuando los datos son inválidos.

---

## Verde no significa probado

El proyecto incluye:

```text
01-junit-fundamentos/scripts/ver-fallar.sh
```

Este script modifica intencionalmente un solo carácter en la regla de aprobación de `Boleta`.

La condición correcta es:

```java
promedio() >= 70
```

y el script la cambia temporalmente por:

```java
promedio() > 70
```

Con este bug, un alumno con exactamente `70` pasa a reprobar.

Antes del cambio:

```text
22 de 22 tests pasan
```

Después de introducir el bug:

```text
20 de 22 tests siguen pasando
```

Solo fallan los tests que prueban exactamente el límite de `70`.

En `BoletaTest.java` existe precisamente una prueba para:

```text
69 -> reprueba
70 -> aprueba
71 -> aprueba
```

El experimento demuestra que tener muchos tests en verde no garantiza que todas las reglas estén bien comprobadas. También es necesario probar límites, errores y casos especiales.

---

## 2. Mockito

### ¿Qué es y qué problema resuelve?

Mockito permite reemplazar temporalmente dependencias de una clase por objetos controlados llamados **mocks**.

Esto es útil cuando una dependencia es lenta, externa o difícil de reproducir durante una prueba.

El ejemplo principal está en:

```text
04-mockito-dobles/
```

y utiliza:

```text
ServicioInscripcion.java
ServicioInscripcionTest.java
```

---

### ¿Dónde se ve en el código?

En `ServicioInscripcionTest.java` se crean mocks para las dependencias externas:

```java
@Mock
private RepositorioAlumnos repoAlumnos;

@Mock
private RepositorioCursos repoCursos;

@Mock
private Notificador notificador;
```

El servicio real se crea con:

```java
@InjectMocks
private ServicioInscripcion servicio;
```

Mockito inyecta los mocks en `ServicioInscripcion`.

---

### `when()` y `verify()`

Con:

```java
when(...)
```

se define qué debe responder una dependencia durante el test.

Por ejemplo:

```java
when(repoAlumnos.buscar("A01"))
        .thenReturn(Optional.of(ANA));
```

Después se puede comprobar una interacción utilizando:

```java
verify(...)
```

Por ejemplo:

```java
verify(notificador).enviarConfirmacion(ANA, java101);
```

También se comprueba que ciertas acciones **no ocurran**:

```java
verify(notificador, never())
        .enviarConfirmacion(any(), any());
```

Esto permite verificar no solamente el resultado final, sino también el comportamiento del servicio.

---

## ¿Por qué mockear estos colaboradores?

En `ServicioInscripcionTest` se mockean:

```text
RepositorioAlumnos
RepositorioCursos
Notificador
```

porque representan dependencias externas o costosas de utilizar en una prueba.

Por ejemplo, un repositorio real podría conectarse a una base de datos y `Notificador` podría enviar un correo.

Con Mockito se pueden controlar sus respuestas sin depender de esos servicios reales.

---

## ¿Qué NO mockearía?

En este proyecto **no se mockea `Curso`** dentro de `ServicioInscripcionTest`.

Se crea uno real:

```java
java101 = new Curso("JAVA-101", 2);
```

La razón es que `Curso` contiene reglas de negocio, como determinar si todavía existen lugares disponibles.

Si se mockeara:

```java
when(curso.estaLleno()).thenReturn(true);
```

el test ya no estaría comprobando la regla real de `Curso`; solamente estaría comprobando una respuesta que el propio test configuró.

Esto se demuestra en:

```text
SobreMockeoTest.java
```

donde `Curso` sí se mockea intencionalmente como ejemplo de una mala práctica.

La idea principal es:

```text
Mockear -> dependencias externas, lentas o difíciles de controlar.

No mockear -> objetos rápidos que contienen reglas de negocio que queremos probar.
```

---

## ¿Qué pasa si no se utilizan pruebas correctamente?

Sin pruebas automatizadas, un cambio pequeño puede romper una regla existente sin que se detecte inmediatamente.

Pero tener tests tampoco es suficiente si solamente se prueban casos fáciles.

El experimento de `ver-fallar.sh` demuestra que, aunque exista un bug, la mayoría de los tests del proyecto pueden seguir pasando. Por eso una ejecución completamente en verde no garantiza por sí sola que todos los casos importantes estén correctamente probados.

Por eso es importante probar:

- casos normales;
- casos de error;
- valores límite;
- comportamiento de las dependencias.

---

## Cómo ejecutar

Desde cualquiera de los proyectos JUnit o Mockito:

```powershell
.\mvnw test
```

Para ejecutar el experimento que introduce el bug:

```bash
cd 01-junit-fundamentos
./scripts/ver-fallar.sh
```

---

[← Volver a Semana 03](../README.md)
