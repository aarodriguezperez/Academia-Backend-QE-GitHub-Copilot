# AWS - Semana 05

Durante esta parte de la Semana 05 se trabajó con diferentes servicios de **Amazon Web Services (AWS)** mediante el despliegue de la aplicación TaskFlow y la posterior automatización de ese proceso.

Las actividades se realizaron en dos bloques.

---

## Actividades

### [Día 3 - Fundamentos, EC2, S3, VPC y RDS](./actividades-aws.md#día-3---fundamentos-ec2-s3-vpc-y-rds)

Se realizó el despliegue manual de TaskFlow utilizando:

- AWS Budgets
- IAM y MFA
- EC2
- Security Groups
- SSH
- Java 21
- RDS con PostgreSQL
- S3
- Presigned URLs

También se validó la comunicación entre EC2 y RDS y el funcionamiento público de la API mediante Swagger.

La topología utilizada se encuentra en:

[Topología AWS - Día 3](./infra/topologia-aws.md)

---

### [Día 4 - DynamoDB, CodePipeline y CodeDeploy](./actividades-aws.md#día-4---dynamodb-codepipeline-y-codedeploy)

Durante el segundo bloque se trabajó con:

- DynamoDB
- `Query` y `Scan`
- Modelado según patrones de acceso
- S3 con Versioning
- IAM Roles para servicios
- CodeDeploy Agent
- `taskflow.service`
- `buildspec.yml`
- `appspec.yml`
- CodeBuild
- CodeDeploy
- CodePipeline

El despliegue manual del día anterior se convirtió en un flujo automatizado que parte de un `git push` y termina con una nueva versión de TaskFlow desplegada en EC2.

La topología utilizada se encuentra en:

[Topología AWS - Día 4](./infra/topologia-aws-dia04.md)

---

## Documentación

### [Actividades AWS](./actividades-aws.md)

Documento principal de la entrega. Incluye las actividades realizadas durante ambos días, las evidencias, problemas encontrados, reflexiones y limpieza de recursos.

Las capturas utilizadas en el documento se encuentran en:

```text
evidencias/
├── dia-03/
└── dia-04/
```

---

> La explicación detallada de cada actividad y las evidencias obtenidas se encuentran en `actividades-aws.md`.

---

[← Volver a Semana 05](../README.md)
