# `testing` — JUnit paso a paso

Material de la Academia Monterrey para el tema de pruebas unitarias. Sigue el temario de
[la guía de JUnit 5 de Baeldung](https://www.baeldung.com/junit-5), con el código actualizado
a **JUnit 6.1.3** y verificado ejecutándolo.

Cinco guías, un anexo y cuatro proyectos Maven independientes — **315 tests** en total.
Cada proyecto tiene al menos un script que demuestra, corriéndolo, la idea central de su guía.

---

## Las guías

| | Guía | De qué va |
|---|---|---|
| `00` | [Por qué se prueba](guias/guia-00-por-que-se-prueba.html) | Conceptos, **sin código**. Qué es un test, qué no es, y por qué verde no significa probado. Requisito de la 01. |
| `01` | [Anatomía de un test](guias/guia-01-anatomia-de-un-test.html) | `@Test`, ciclo de vida, aserciones, la trampa del `double` — y el test que atrapa un bug de un carácter. |
| `02` | [El catálogo](guias/guia-02-el-catalogo.html) | `assertThrows`, timeouts, *assumptions*, `@Nested`, `@Tag`, inyección de parámetros, `@Suite`. |
| `03` | [Un test, muchos datos](guias/guia-03-un-test-muchos-datos.html) | `@ParameterizedTest` con todas sus fuentes, `@RepeatedTest`, `@TestFactory`. |
| `04` | [Dobles de prueba](guias/guia-04-dobles-de-prueba.html) | **Mockito**: `@Mock`, `when`/`verify`, `@InjectMocks`, `ArgumentCaptor`, espías, estrictez — y cuándo NO mockear. |
| `··` | [JUnit 5 contra JUnit 6](guias/anexo-junit5-vs-junit6.html) | Fe de erratas: qué versión usas de verdad, y la tabla de migración desde JUnit 4. |

Son archivos HTML autocontenidos: se abren con **doble clic**, sin servidor y sin internet,
y navegan entre sí. (También están publicadas en la web; las páginas detectan solas dónde
se están viendo y ajustan sus enlaces.)

## Los proyectos

| Proyecto | Tests | Código bajo prueba | Script |
|---|---|---|---|
| [`01-junit-fundamentos`](proyectos/01-junit-fundamentos) | 22 | `Boleta` — promedio y regla de aprobación | `ver-fallar.sh` |
| [`02-junit-catalogo`](proyectos/02-junit-catalogo) | 37 (4 abortados a propósito) | `Curso` — cupo, cierre, acta lenta | `filtrar.sh` |
| [`03-junit-datos`](proyectos/03-junit-datos) | 211 (desde 32 métodos) | `ValidadorCurp`, `Calificador` | `contar.sh` |
| [`04-mockito-dobles`](proyectos/04-mockito-dobles) | 45 | `ServicioInscripcion` y sus 3 colaboradores | `por-que-mockear.sh`, `la-mentira.sh` |

```bash
cd proyectos/01-junit-fundamentos
./mvnw test
./scripts/ver-fallar.sh
```

En Windows: `mvnw.cmd test`. Los `.sh` necesitan **Git Bash** o **WSL**.

### Los cinco scripts

Cada uno demuestra algo que no se puede enseñar solo con texto:

- **`ver-fallar.sh`** — cambia un carácter en `Boleta.aprobado()` (`>=` pasa a `>`), corre la
  suite y restaura el código. **20 de los 22 tests siguen en verde con el bug dentro.** Solo
  caen los dos que se pararon justo encima del límite. Es la lección de la guía 01 en un número.
- **`filtrar.sh`** — corre la misma suite con tres filtros de `@Tag` y mide el tiempo real de
  ejecución (no el reloj de pared, que en un proyecto de este tamaño lo domina el arranque de
  Maven). Los 3 tests lentos de 37 se llevan más de la mitad del tiempo.
- **`contar.sh`** — cuenta los métodos escritos a mano contra los tests ejecutados: 32 → 211.
- **`por-que-mockear.sh`** — la misma prueba contra el repositorio real (300 ms por consulta) y
  con dobles. **5 tests reales tardan más que los 40 con mocks.** Justifica Mockito midiéndolo.
- **`la-mentira.sh`** — el más importante de los cinco. Rompe la regla del cupo dentro de `Curso`
  y corre dos clases que prueban lo mismo: la que usa un `Curso` real **cae**, la que lo mockea
  **sigue en verde**. Las dos «cubren» el mismo código; solo una lo protege.

## Los proyectos de clase, fuera de esta carpeta

En la raíz del repositorio hay otros cuatro proyectos sobre lo mismo. **No son parte de este
temario**: son el código que se escribió en vivo durante la sesión, en paquete `com.curso.v0`,
con sus atajos y sus huecos. Se conservan para que el alumno reencuentre lo que vio en pantalla.

| Proyecto | Tests | De qué va | Guía |
|---|---|---|---|
| [`demoTestJunit`](../demoTestJunit) | 9 | Aserciones sueltas: `assertAll`, `assertThrows`, el `assertTrue` con mensaje diferido. | `01` |
| [`demoTestJunit2`](../demoTestJunit2) | 7 (desde 5 métodos) | `Calculator` con `@BeforeEach` y `@RepeatedTest(3)`. | `01`, `03` |
| [`mockitoWithout`](../mockitoWithout) | — | `ServiceCalculoImpuesto` contra la implementación **real** de `ICalculoComplejo`. Sin tests: solo un `main`. | `04` |
| [`mockito`](../mockito) | 12 | El mismo servicio cuando esa implementación **no existe**. | `04` |

**`mockitoWithout` y `mockito` son el mismo código**, y funcionan como apertura de la guía 04.
Ejecuta el `main` de los dos: el primero imprime `3.4236650365470685E7`; el segundo revienta con
`NullPointerException`, porque ahí solo tenemos la interfaz —la implementación la escribe un
tercero y en producción la inyecta el framework—. Ese contraste plantea en treinta segundos la
pregunta que Mockito responde, y aun así `./mvnw test` da 12 en verde: el servicio sí se puede
probar entero.

Los dos alcances se complementan, y conviene no confundirlos. `mockito` enseña **por qué** existe
Mockito, con un caso donde mockear es obligatorio porque no hay otra cosa que usar.
[`04-mockito-dobles`](proyectos/04-mockito-dobles) enseña además **cuándo NO** hacerlo
—`SobreMockeoTest`, `la-mentira.sh`—, que es la mitad que se olvida. Para dar el tema completo:
el par de clase primero, este proyecto después.

Una diferencia práctica: a diferencia de los de esta carpeta, esos cuatro **sí llevan `.project`
y `.classpath` versionados**, así que se importan con *Existing Projects into Workspace*. Dos de
ellos, `demoTestJunit2` y `mockito`, son además proyectos Maven.

---

## La auditoría de coherencia

```bash
./scripts/auditar.py            # comprobaciones estáticas, en un segundo
./scripts/auditar.py --tests    # además corre las suites y compara cifras
```

Existe porque el mismo dato —la latencia del proyecto Mono— se desincronizó **dos veces
el mismo día** entre el código, un script y una guía. La lección no fue «hay que fijarse
más»: fue que un número repetido a mano en cuatro sitios se desincroniza solo.

Comprueba nueve cosas: el HTML cierra bien, las anclas del índice existen, las guías se
enlazan entre sí por su URL publicada (sin relativos, sin autoenlaces, y con el texto del
enlace nombrando de verdad su destino), la numeración de secciones es correlativa, los
archivos que las guías citan existen, las versiones que muestran en un `<version>` son de
verdad las de los `pom.xml`, las cifras de la cabecera cuadran con lo que reporta Surefire,
y los `.sh` y `mvnw` son ejecutables. Devuelve `1` si algo falla.

Está probado **en negativo**: se le rompieron doce cosas a propósito —una guía sin
registrar, un ancla huérfana, una etiqueta sin cerrar, una cifra mentida, una versión
inventada, un `.sh` sin permisos…— y cazó las doce. Un verificador que nunca has visto
fallar no sabes si funciona.

**Y dice en voz alta lo que NO mira**, que es la mitad de su valor: no comprueba que las
páginas rendericen, ni que el botón *Copiar* funcione, ni que los comandos corran, ni si
la prosa dice la verdad. Un verde suyo significa «todo lo que miro está bien», no «todo
está bien».

## Cómo abrirlo en Eclipse

**File → Import… → Maven → Existing Maven Projects**, y selecciona la carpeta `testing`.
Los cuatro proyectos aparecen a la vez. No uses *Existing Projects into Workspace*: no llevan
`.project` — los genera m2e al importar.

Para correr un test suelto: clic derecho sobre la clase → **Run As → JUnit Test**.

## Versiones

| | |
|---|---|
| JUnit | 6.1.3 (vía `junit-bom`) |
| Mockito | 5.23.0 (vía `mockito-bom`) — solo en el proyecto 04 |
| Java | 21 (JUnit 6 exige 17 como mínimo) |
| Maven Surefire | 3.5.6 — **con 2.x no se ejecuta ningún test, y no falla** |

Los proyectos **no** usan Spring Boot: son `pom.xml` mínimos, a propósito, para que se vea qué
pide JUnit por sí solo. En un proyecto de Spring Boot no hace falta nada de esto —
`spring-boot-starter-test` ya trae JUnit, Mockito y AssertJ con las versiones coordinadas.

### Dos cosas del proyecto 04 que conviene saber

**Mockito dice depender de JUnit 5, y no pasa nada.** `mockito-junit-jupiter:5.23.0` declara
`junit-jupiter-api:5.13.4`, pero el `junit-bom` va antes en el `dependencyManagement` y fija
6.1.3 para todo el árbol. Verificado: `./mvnw dependency:tree "-Dincludes=org.junit*:*"`.

**El `pom` carga Mockito como `-javaagent`, y no es opcional.** Sin esa línea, cada corrida en
Java 21 avisa de que Mockito se auto-engancha y que *«dynamic loading of agents will be
disallowed by default in a future release»*. Con ella, el aviso desaparece y además los tests
van más rápido (medido: 0.427 s → 0.169 s).
