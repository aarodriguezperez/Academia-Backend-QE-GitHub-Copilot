# Automatización Web Santander con Selenium

Proyecto de automatización de pruebas web desarrollado con **Java, Selenium WebDriver, TestNG y Maven**, aplicando el patrón de diseño **Page Object Model (POM)**.

El objetivo es validar la navegación de diferentes secciones del portal de **Santander México**, manteniendo separadas las acciones de las páginas y los casos de prueba para conseguir un framework organizado, reutilizable y fácil de mantener.

> Proyecto realizado con fines educativos y de práctica de automatización. No tiene afiliación oficial con Santander.

## Tecnologías utilizadas

- Java 21
- Selenium WebDriver 4.35.0
- TestNG 7.11.0
- Maven
- Page Object Model (POM)
- Google Chrome
- Selenium Manager para la administración automática de ChromeDriver
- SLF4J Simple 2.0.17

## Cobertura de pruebas

La suite automatiza la navegación de las principales secciones del sitio:

- **Inicio:** apertura de los menús Personas, Empresas, PyMEs, Banca Privada y Acerca del Banco.
- **Personas:** tarjetas de crédito, créditos personales, hipotecarios y automotrices, Buró de Crédito, Santander Digital, App Santander, Santander Web, cuentas y otros productos.
- **Empresas:** Empresas y Gobierno y Multinacionales.
- **PyMEs:** cuentas, paquetes, seguros, inversiones, créditos, negocio transaccional, negocio internacional, alianzas y otros servicios.
- **Banca Privada:** Nuestras cuentas, Quiénes somos, Productos y Podcast.
- **Acerca del Banco:** Fundación Santander, Responsabilidad Social, Educación Financiera, Inversionistas, Sala de Comunicación, Bolsa de Trabajo y Blog.

## Arquitectura

El proyecto utiliza **Page Object Model**, de modo que los localizadores y acciones de cada página se encuentran separados de las pruebas.

```text
selenium-santander/
├── src/
│   ├── main/java/
│   │   ├── base/
│   │   │   └── BasePage.java
│   │   ├── pages/
│   │   │   ├── SantanderHomePage.java
│   │   │   ├── PersonasMenuPage.java
│   │   │   ├── EmpresasMenuPage.java
│   │   │   ├── PymesMenuPage.java
│   │   │   ├── BancaPrivadaMenuPage.java
│   │   │   └── AcercaDelBancoMenuPage.java
│   │   └── utils/
│   │       └── DriverFactory.java
│   └── test/java/
│       ├── base/
│       │   └── BaseTest.java
│       ├── listeners/
│       │   └── ScreenshotListener.java
│       └── tests/
│           ├── SantanderHomeTest.java
│           ├── PersonasMenuTest.java
│           ├── EmpresasMenuTest.java
│           ├── PymesMenuTest.java
│           ├── BancaPrivadaTest.java
│           └── AcercaDelBancoMenuTest.java
├── pom.xml
└── testng.xml
```

## Componentes principales

### `BasePage`

Contiene comportamiento reutilizable para las páginas, incluyendo:

- Esperas explícitas con `WebDriverWait`.
- Localización de elementos.
- Clic sobre elementos.
- Obtención de texto.
- Validación de cambios en la URL.

### `DriverFactory`

Centraliza la creación y configuración de `ChromeDriver`. También permite ejecutar las pruebas en modo **headless**.

El proyecto utiliza **Selenium Manager**, por lo que no es necesario descargar manualmente ChromeDriver.

### `BaseTest`

Administra el ciclo de vida del navegador:

- Inicializa Chrome antes de cada prueba.
- Abre `https://www.santander.com.mx/`.
- Cierra el navegador al terminar cada prueba.

### `ScreenshotListener`

Implementa un listener de TestNG que genera automáticamente una captura de pantalla cuando una prueba falla.

Las capturas se almacenan en:

```text
test-output/screenshots/
```

## Casos de prueba

Actualmente el proyecto contiene **68 pruebas automatizadas**:

| Clase | Pruebas |
|---|---:|
| `SantanderHomeTest` | 5 |
| `PersonasMenuTest` | 39 |
| `EmpresasMenuTest` | 2 |
| `PymesMenuTest` | 11 |
| `BancaPrivadaTest` | 4 |
| `AcercaDelBancoMenuTest` | 7 |
| **Total** | **68** |

La última ejecución incluida en el proyecto registra:

```text
Passed:  68
Failed:   0
Skipped:  0
```

## Requisitos

- JDK 21
- Maven
- Google Chrome

Puedes comprobar tu instalación con:

```bash
java -version
mvn -version
```

## Instalación

Clona el repositorio y entra a la carpeta del proyecto:

```bash
git clone <URL_DEL_REPOSITORIO>
cd selenium-santander
```

Maven descargará automáticamente las dependencias definidas en `pom.xml`.

## Ejecución

Para ejecutar toda la suite definida en `testng.xml`:

```bash
mvn clean test
```

Para ejecutar las pruebas sin mostrar la ventana de Chrome:

```bash
mvn clean test -Dheadless=true
```

## Reportes

Después de la ejecución, TestNG genera los resultados dentro de:

```text
test-output/
```

Los principales reportes son:

```text
test-output/index.html
test-output/emailable-report.html
test-output/testng-results.xml
```

Si una prueba falla, la evidencia se guarda en:

```text
test-output/screenshots/
```

## Ejecución desde Eclipse

1. Ir a **File > Import**.
2. Seleccionar **Existing Maven Projects**.
3. Elegir la carpeta del proyecto.
4. Ejecutar **Maven > Update Project** si es necesario.
5. Abrir `testng.xml`.
6. Ejecutar **Run As > TestNG Suite**.

## Objetivos de aprendizaje

Este proyecto permite practicar:

- Automatización con Selenium WebDriver.
- Localizadores CSS.
- Esperas explícitas.
- TestNG.
- Page Object Model.
- Reutilización de código.
- Manejo del ciclo de vida del navegador.
- Ejecución de suites con Maven.
- Ejecución headless.
- Reportes y captura de evidencias ante fallos.

## Autor

**Alberto Rodríguez**

Proyecto desarrollado como práctica de automatización de pruebas con Selenium WebDriver y Java.
