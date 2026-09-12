# Topología AWS - TaskFlow

## Descripción

Durante el Día 3 desplegué TaskFlow utilizando **Amazon EC2**, **Amazon RDS** y **Amazon S3**.

La aplicación se ejecuta en una instancia EC2, utiliza PostgreSQL alojado en RDS para la persistencia de información y mantiene el artefacto compilado en un bucket S3.

## Arquitectura

```text
                              Internet
                                 |
                +----------------+----------------+
                |                                 |
          SSH :22                           HTTP :8080
        solo mi IP                           Swagger/API
                |                                 |
                +---------------+-----------------+
                                |
                                v
                       +------------------+
                       |   Amazon EC2     |
                       |  taskflow-ec2    |
                       |                  |
                       | TaskFlow API     |
                       | Java 21          |
                       +--------+---------+
                                |
                                | PostgreSQL
                                | TCP :5432
                                v
                       +------------------+
                       |   Amazon RDS     |
                       |   taskflow-db    |
                       |                  |
                       | PostgreSQL       |
                       +------------------+


                       +------------------+
                       |    Amazon S3     |
                       |                  |
                       | taskflow-api-    |
                       |   3.0.0.jar      |
                       +------------------+
                                |
                                |
                        Presigned URL
                        acceso temporal
```

## Amazon EC2

La instancia `taskflow-ec2` funciona como servidor de la aplicación.

Dentro de EC2 se utiliza:

- Amazon Linux 2023.
- Java 21.
- El archivo `taskflow-api.jar`.
- Puerto `8080` para exponer TaskFlow y Swagger.

### Reglas principales de entrada

```text
SSH         TCP 22     Mi IP
Custom TCP  TCP 8080   0.0.0.0/0
```

El puerto SSH se limita a mi dirección IP para evitar exponer el acceso administrativo a internet.

El puerto `8080` se abrió públicamente de forma intencional durante la práctica para acceder a Swagger desde mi computadora.

---

## Amazon RDS

La instancia `taskflow-db` aloja PostgreSQL.

TaskFlow se conecta desde EC2 hacia RDS utilizando el puerto estándar de PostgreSQL:

```text
TCP 5432
```

RDS no necesita una IP pública porque la base no debe ser consumida directamente desde internet.

La comunicación ocurre dentro de la infraestructura de red de AWS y el acceso se controla mediante Security Groups.

El flujo es:

```text
taskflow-ec2
      |
      | TCP 5432
      v
taskflow-db
```

De esta forma PostgreSQL queda aislado del acceso público y solamente la infraestructura autorizada puede comunicarse con la base.

---

## Amazon S3

El bucket:

```text
taskflow-artefactos-aarodriguezperez
```

almacena el artefacto:

```text
taskflow-api-3.0.0.jar
```

S3 funciona como almacenamiento independiente de la instancia EC2.

Además, generé una **presigned URL** para permitir acceso temporal al JAR sin hacer público el bucket completo.

El flujo conceptual es:

```text
Aplicación compilada
        |
        v
taskflow-api-3.0.0.jar
        |
        v
     Amazon S3
        |
        +----> Presigned URL temporal
```

---

## Flujo general de TaskFlow

1. Desde mi computadora accedo a la dirección pública de EC2 por el puerto `8080`.
2. La petición llega a TaskFlow, que se está ejecutando con Java dentro de `taskflow-ec2`.
3. Cuando TaskFlow necesita consultar o guardar información, se comunica con PostgreSQL en RDS mediante el puerto `5432`.
4. La base RDS no se expone directamente a internet.
5. El archivo JAR de la aplicación también se almacena en S3 como artefacto.
6. Cuando se necesita acceso temporal al objeto de S3 se puede utilizar una presigned URL.

## Resumen

```text
Usuario / Navegador
        |
        | :8080
        v
      EC2
   TaskFlow API
        |
        | :5432
        v
      RDS
   PostgreSQL

      S3
       |
       +--- taskflow-api-3.0.0.jar
       |
       +--- Presigned URL temporal
```

Esta separación permite que cada servicio tenga una responsabilidad específica:

- **EC2:** ejecutar la aplicación.
- **RDS:** almacenar la información.
- **S3:** almacenar el artefacto de despliegue.
