# AWS - Semana 05

Esta carpeta contiene la documentación y evidencias de las actividades realizadas en Amazon Web Services durante la Semana 05.

El trabajo se divide en dos jornadas:

- **Día 3:** despliegue manual de TaskFlow utilizando EC2, RDS y S3.
- **Día 4:** DynamoDB y automatización del despliegue mediante CodeBuild, CodeDeploy y CodePipeline.

---

## Documento principal

### [Actividades AWS](./actividades-aws.md)

Este es el documento principal de la entrega.

Incluye:

- Qué se realizó en cada bloque.
- Para qué sirve cada servicio utilizado.
- Evidencias de la implementación.
- Problemas encontrados.
- Soluciones aplicadas.
- Reflexiones solicitadas en la actividad.
- Limpieza final de los recursos.

---

## Día 3 - Fundamentos, EC2, S3, VPC y RDS

Durante el Día 3 se realizó el despliegue manual de TaskFlow.

Se trabajó con:

- AWS Budgets.
- IAM y MFA.
- Amazon EC2.
- Security Groups.
- SSH.
- Java 21.
- Maven.
- Transferencia del JAR mediante SCP.
- Swagger.
- Amazon RDS con PostgreSQL.
- Comunicación EC2 → RDS.
- Amazon S3.
- Presigned URLs.
- Smoke test de TaskFlow.

La topología utilizada se encuentra en:

[Topología AWS - Día 3](./infra/topologia-aws-dia03.md)

---

## Día 4 - DynamoDB y CI/CD

Durante el Día 4 se trabajó con DynamoDB y se automatizó el despliegue realizado manualmente el día anterior.

Se realizaron actividades con:

- Amazon DynamoDB.
- `Query` y `Scan`.
- Modelado según patrones de acceso.
- S3 con Bucket Versioning.
- IAM Roles para servicios.
- AWS CodeDeploy Agent.
- `taskflow.service`.
- `buildspec.yml`.
- `appspec.yml`.
- Hooks de CodeDeploy.
- AWS CodeBuild.
- AWS CodeDeploy.
- AWS CodePipeline.

El flujo final quedó automatizado de la siguiente manera:

```text
GitHub
   |
   v
CodePipeline
   |
   v
CodeBuild
   |
   v
Amazon S3
   |
   v
CodeDeploy
   |
   v
EC2
   |
   v
TaskFlow API
```

También se realizó:

- Despliegue automático de la versión `3.0.1`.
- Fallo intencional del hook `AfterInstall`.
- Identificación del error `ScriptMissing`.
- Corrección del hook.
- Recuperación del pipeline.

La topología de este día se encuentra en:

[Topología AWS - Día 4](./infra/topologia-aws-dia04.md)

---

## Evidencias

Las capturas utilizadas dentro del documento principal están organizadas por día:

```text
evidencias/
├── dia-03/
└── dia-04/
```

Cada imagen está referenciada desde `actividades-aws.md` y sirve para comprobar los resultados obtenidos durante la práctica.

---

## Infraestructura

La carpeta `infra/` contiene las topologías utilizadas durante ambos días:

```text
infra/
├── topologia-aws.md
└── topologia-aws-dia04.md
```

### Día 3

Representa principalmente:

```text
Usuario
   |
   v
EC2 / TaskFlow
   |
   v
RDS PostgreSQL

S3
└── JAR + Presigned URL
```

### Día 4

Representa el proceso automatizado:

```text
GitHub
   |
   v
CodePipeline
   |
   v
CodeBuild
   |
   v
S3
   |
   v
CodeDeploy
   |
   v
EC2 / TaskFlow
```

---

## Estructura

```text
02-aws/
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

## Seguridad

El repositorio no incluye:

- Contraseña de RDS.
- `JWT_SECRET`.
- Access Key.
- Secret Access Key.
- Archivos `.pem`.
- Endpoint completo de RDS.

Las credenciales utilizadas durante la práctica se mantuvieron fuera del repositorio.

---

[← Volver a Semana 05](../README.md)
