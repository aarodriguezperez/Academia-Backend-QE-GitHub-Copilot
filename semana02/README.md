# Semana 02 - Spring Boot, Persistencia e Inyección de Dependencias

Durante esta semana se trabajó con **Spring Boot**, persistencia de datos e **inyección de dependencias**, mediante el desarrollo de tres proyectos.

Los primeros dos proyectos implementan una API REST para gestionar vehículos utilizando diferentes tecnologías de persistencia: **MySQL con Spring Data JPA** y **MongoDB con Spring Data MongoDB**.

El tercer proyecto utiliza **Java puro** para demostrar el funcionamiento de la inyección de dependencias sin utilizar Spring.

---

## Proyectos

### [01 - Spring Data JPA con MySQL](./01-spring-data-jpa/)

API REST para gestionar vehículos utilizando:

- Spring Boot
- Spring Data JPA
- MySQL
- Maven

Se implementan operaciones CRUD mediante endpoints REST y persistencia en una base de datos relacional.

---

### [02 - Spring Data MongoDB](./02-spring-data-mongodb/)

API REST para gestionar vehículos utilizando:

- Spring Boot
- Spring Data MongoDB
- MongoDB
- Mongo Express
- Docker

Se utiliza la misma idea del proyecto anterior, pero almacenando la información como documentos en MongoDB.

---

### [03 - Inyección de Dependencias en Java](./03-inyeccion-dependencias-java/)

Proyecto desarrollado con **Java puro** para demostrar cómo una clase puede recibir sus dependencias desde el exterior en lugar de crearlas directamente.

Se trabajan conceptos como:

- Interfaces
- Inyección por constructor
- Inyección por setter
- Inyección por atributo
- Bajo acoplamiento
- Testeabilidad

---

> La explicación detallada, configuración y funcionamiento de cada proyecto se encuentra en su respectivo README.

---

[← Volver al README principal](../README.md)
