# Semana 05 - Automatización de Pruebas y AWS

Durante la Semana 05 se trabajaron dos bloques principales:

- Automatización de pruebas web con Selenium WebDriver.
- Servicios de AWS, despliegue de aplicaciones y automatización mediante CI/CD.

La entrega de esta semana está dividida en dos partes: el proyecto de Selenium con su documentación y un documento que explica las actividades realizadas en AWS.

---

## 01 - Selenium

En esta parte se desarrolló un proyecto de automatización de pruebas sobre el sitio de Santander México utilizando:

- Java 21
- Selenium WebDriver
- TestNG
- Maven
- Page Object Model (POM)
- Selenium Manager

La suite automatiza la navegación de distintas secciones del portal y actualmente contiene **68 pruebas automatizadas**.

La documentación del proyecto incluye:

- Objetivo.
- Tecnologías utilizadas.
- Arquitectura.
- Organización mediante Page Object Model.
- Casos de prueba.
- Ejecución con Maven y TestNG.
- Ejecución en modo headless.
- Capturas automáticas ante fallos.
- Resultados de la suite.

### Documentación

[Ver proyecto y documentación de Selenium](./01-selenium/)

---

## 02 - AWS

Durante las actividades de AWS se trabajó en dos etapas.

### Día 3 - Fundamentos, EC2, S3, VPC y RDS

Se realizaron actividades relacionadas con:

- Seguridad de la cuenta e IAM.
- Presupuesto de AWS.
- Creación y configuración de una instancia EC2.
- Conexión por SSH.
- Instalación y validación de Java 21.
- Empaquetado y transferencia del JAR de TaskFlow.
- Ejecución pública de TaskFlow.
- Amazon RDS con PostgreSQL.
- Configuración de red entre EC2 y RDS.
- Amazon S3.
- Presigned URLs.
- Smoke test de la aplicación.

### Día 4 - DynamoDB, CodeBuild, CodeDeploy y CodePipeline

Se trabajó con:

- DynamoDB.
- Comparación entre `Query` y `Scan`.
- Modelado según patrones de acceso.
- Versionado de artefactos en S3.
- IAM para servicios.
- CodeDeploy Agent.
- `taskflow.service`.
- `buildspec.yml`.
- `appspec.yml`.
- Lifecycle hooks.
- CodeBuild.
- CodeDeploy.
- CodePipeline.
- Despliegue automático de la versión `3.0.1`.
- Fallo intencional de un hook y recuperación del pipeline.
- Limpieza final de recursos.

### Documentación

[Ver actividades y evidencias de AWS](./02-aws/)

---

## Estructura de la semana

```text
semana05/
├── README.md
├── 01-selenium/
│   ├── README.md
│   ├── src/
│   ├── pom.xml
│   └── testng.xml
└── 02-aws/
    ├── README.md
    ├── actividades-aws.md
    ├── evidencias/
    │   ├── dia-03/
    │   └── dia-04/
    └── infra/
        ├── topologia-aws.md
        └── topologia-aws-dia04.md
```

---

## Entrega

La documentación principal de esta semana se encuentra en:

- [Selenium](./01-selenium/)
- [AWS](./02-aws/actividades-aws.md)

---

[← Volver al README principal](../README.md)
