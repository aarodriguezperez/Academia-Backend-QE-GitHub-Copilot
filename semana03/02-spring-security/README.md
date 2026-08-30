# Spring Security - HTTP Basic, JWT y OAuth2

En este proyecto se trabajan tres mecanismos de autenticación y autorización utilizando **Spring Security**: HTTP Basic, JWT y OAuth2.

Los tres buscan resolver el mismo problema general: evitar que cualquier usuario pueda acceder libremente a los recursos de una API. La diferencia está en la forma en que se identifica al usuario y en cómo se demuestra su identidad en cada petición.

Los ejemplos se encuentran separados en:

```text
02-spring-security/
├── 01-security-basic/
├── 02-security-jwt/
├── 03-security-oauth2/
└── sql-scripts/
```

Cada proyecto muestra una evolución en la forma de proteger la API.

---

## 1. HTTP Basic

### ¿Qué es y qué problema resuelve?

HTTP Basic es una de las formas más sencillas de autenticación.

El cliente envía su usuario y contraseña en cada petición.

Spring Security recibe esas credenciales, verifica que sean correctas y posteriormente decide si el usuario tiene permiso para acceder al endpoint solicitado.

Este mecanismo permite comenzar a proteger una API sin necesidad de manejar tokens.

---

### ¿Dónde se ve en el código?

El ejemplo se encuentra en:

```text
01-security-basic/
```

La configuración principal está en:

```text
SecurityConfig.java
```

donde se habilita HTTP Basic mediante:

```java
http.httpBasic(Customizer.withDefaults());
```

En la misma configuración se restringen diferentes endpoints dependiendo de los roles del usuario.

Por ejemplo, se utilizan reglas como:

```java
.hasRole("EMPLOYEE")
.hasRole("MANAGER")
.hasRole("ADMIN")
```

Los usuarios se consultan desde la base de datos mediante:

```java
JdbcUserDetailsManager
```

La estructura de usuarios, roles y contraseñas se encuentra en:

```text
sql-scripts/01-security-tables.sql
```

También se incluye:

```text
scripts/test-endpoints.sh
```

para comprobar los diferentes resultados de autenticación y autorización.

---

### 401 y 403

Este proyecto también permite observar la diferencia entre dos errores importantes.

Un:

```text
401 Unauthorized
```

aparece cuando el usuario no se autenticó correctamente, por ejemplo porque no envió credenciales o son incorrectas.

Un:

```text
403 Forbidden
```

significa que el usuario sí fue autenticado, pero no tiene el rol necesario para utilizar determinado recurso.

Por ejemplo:

```text
Sin usuario/contraseña
        ↓
       401

Usuario válido
pero sin el rol requerido
        ↓
       403
```

---

### ¿Qué pasa si no se utiliza?

Sin un mecanismo de autenticación, cualquier cliente podría consumir los endpoints protegidos.

Además, si solamente se verifica que el usuario existe pero no sus roles, un usuario con pocos privilegios podría ejecutar operaciones que deberían estar reservadas para administradores o managers.

La principal desventaja de HTTP Basic es que las credenciales deben enviarse en cada petición, por lo que debe utilizarse siempre sobre HTTPS en un sistema real.

---

## 2. JWT

### ¿Qué es y qué problema resuelve?

JWT permite autenticar al usuario una vez y posteriormente utilizar un **token** para acceder a los recursos protegidos.

En lugar de enviar usuario y contraseña en cada petición, el flujo utilizado en este proyecto es:

```text
Usuario + contraseña
        ↓
POST /api/auth/login
        ↓
Servidor valida las credenciales
        ↓
Genera JWT
        ↓
Cliente recibe token
        ↓
Authorization: Bearer <token>
        ↓
Endpoints protegidos
```

Esto evita que la contraseña tenga que enviarse nuevamente en cada petición a la API.

---

### ¿Dónde se ve en el código?

El ejemplo se encuentra en:

```text
02-security-jwt/
```

El endpoint de autenticación está implementado en:

```text
AuthController.java
```

mediante:

```text
POST /api/auth/login
```

Después de validar las credenciales, se genera un JWT utilizando:

```java
JwtEncoder
```

La configuración de Spring Security utiliza el soporte de Resource Server para validar esos tokens.

junto con claves RSA:

```text
src/main/resources/certs/
├── private.pem
└── public.pem
```

La clave privada permite firmar los tokens generados por la aplicación y la clave pública permite verificar posteriormente que el token no fue alterado.

---

### Expiración del token

En la configuración se define un tiempo de vida para el JWT:

```properties
jwt.ttl-seconds=3600
```

Esto evita que un token pueda utilizarse indefinidamente.

Una vez vencido, el servidor debe rechazarlo.

---

### Demostración del 401

Dentro del proyecto se incluye:

```text
scripts/test-endpoints.sh
```

que permite comprobar diferentes situaciones.

Por ejemplo:

```text
Petición sin token
        ↓
       401
```

También se prueban casos como:

```text
Token alterado
      ↓
     401
```

y:

```text
JWT inválido o vencido
        ↓
       401
```

Esta parte es importante porque no solamente se demuestra que un token válido funciona, sino también que la API realmente bloquea peticiones que no deberían tener acceso.

---

### ¿Qué pasa si no se valida correctamente?

Si la API simplemente aceptara cualquier cadena enviada como token, un usuario podría modificar información del JWT y obtener privilegios que no le corresponden.

Por eso la firma es importante.

El servidor verifica que el token:

- tenga una firma válida;
- no haya sido modificado;
- no esté vencido;
- contenga la información necesaria para autorizar al usuario.

Sin estas validaciones, el token dejaría de ser una prueba confiable de identidad.

---

## 3. OAuth2

### ¿Qué es y qué problema resuelve?

OAuth2 separa la aplicación que utiliza un recurso de la aplicación encargada de autenticar al usuario.

En este proyecto se utiliza **Keycloak** como servidor de autorización.

La API ya no necesita administrar directamente el inicio de sesión del usuario. En su lugar, confía en los tokens emitidos por Keycloak.

El flujo general es:

```text
Usuario
   ↓
Keycloak
   ↓
Autenticación
   ↓
Access Token
   ↓
Cliente
   ↓
API protegida
```

---

## Actores de OAuth2

En OAuth2 participan diferentes actores.

### Resource Owner

Es el usuario propietario de los datos o permisos.

En este ejemplo sería la persona que inicia sesión.

---

### Client

Es la aplicación que quiere acceder a la API en nombre del usuario.

Un cliente puede ser, por ejemplo:

- una aplicación web;
- una aplicación móvil;
- otro sistema.

---

### Authorization Server

Es quien autentica al usuario y emite los tokens.

En este proyecto ese papel lo realiza:

```text
Keycloak
```

ejecutándose sobre el realm:

```text
academy
```

---

### Resource Server

Es la API que contiene los recursos protegidos.

La API recibe el token y verifica que haya sido emitido por el Authorization Server correcto antes de permitir el acceso.

---

## ¿Por qué la aplicación no ve la contraseña?

Una de las ventajas más importantes de OAuth2 es que la aplicación cliente no necesita conocer ni almacenar la contraseña del usuario.

El usuario se autentica directamente contra el Authorization Server.

La contraseña se entrega a Keycloak, no a la aplicación cliente.

Esto reduce el riesgo porque cada aplicación que utilice la API no necesita almacenar las credenciales del usuario.

---

### ¿Dónde se ve en el código?

El ejemplo está en:

```text
03-security-oauth2/
```

La conexión con Keycloak se configura mediante:

```properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8090/realms/academy
```

Esto indica que la API confía en tokens emitidos por el realm:

```text
academy
```

de Keycloak.

La configuración principal vuelve a encontrarse en:

```text
SecurityConfig.java
```

donde los roles incluidos en el token se obtienen de:

```text
realm_access.roles
```

y posteriormente se convierten en autoridades que Spring Security puede utilizar.

La configuración de Keycloak se automatiza mediante:

```text
scripts/keycloak-setup.sh
```

Este script crea y configura elementos como:

```text
Realm   -> academy
Client  -> employee-api
Usuarios
Roles
```

---

### Sobre el flujo utilizado para las pruebas

En `03-security-oauth2/scripts/test-endpoints.sh` se obtiene un token de Keycloak utilizando:

```text
grant_type=password
```

para facilitar las pruebas desde terminal.

Este mecanismo permite obtener directamente un token proporcionando usuario y contraseña.

Sin embargo, en una aplicación real se recomienda utilizar un flujo como:

```text
Authorization Code + PKCE
```

porque permite que el cliente no tenga acceso directo a la contraseña del usuario.

---

### ¿Qué pasa si no se utiliza correctamente?

Si cada aplicación administra directamente usuarios y contraseñas:

- aumenta la cantidad de lugares donde se almacenan credenciales;
- aumenta el riesgo de filtraciones;
- cada aplicación tendría que implementar autenticación;
- administrar roles y accesos se vuelve más complicado.

Con un Authorization Server como Keycloak, la autenticación puede centralizarse y las APIs solamente necesitan validar los tokens recibidos.

---

## 4. BCrypt y almacenamiento de contraseñas

Las contraseñas utilizadas por los ejemplos Basic y JWT no se almacenan directamente como texto.

En:

```text
sql-scripts/01-security-tables.sql
```

se almacenan valores similares a:

```text
{bcrypt}$2y$10$...
```

El prefijo:

```text
{bcrypt}
```

indica que la contraseña fue procesada utilizando BCrypt.

Por ejemplo, aunque diferentes usuarios puedan tener como contraseña de prueba:

```text
test123
```

los hashes almacenados pueden ser diferentes.

Esto sucede porque BCrypt utiliza un **salt** durante la generación del hash.

Spring Security reconoce el identificador:

```text
{bcrypt}
```

y utiliza el encoder correspondiente para validar la contraseña recibida contra el hash almacenado.

---

### ¿Por qué no guardar contraseñas en texto plano?

Si una base de datos que contiene contraseñas en texto plano fuera comprometida, todas las credenciales quedarían expuestas inmediatamente.

Con BCrypt lo que se almacena es un hash.

El sistema compara la contraseña proporcionada por el usuario con ese hash, pero no necesita recuperar la contraseña original desde la base de datos.

---

## Comparación de los tres mecanismos

| Mecanismo | Qué se envía después de autenticar | Quién valida |
|---|---|---|
| HTTP Basic | Usuario y contraseña en cada petición | La propia API |
| JWT | Bearer token | La propia API |
| OAuth2 | Access token | API confiando en el Authorization Server |

Los tres permiten proteger recursos, pero distribuyen de manera diferente la responsabilidad de autenticar al usuario.

---

## Cómo ejecutar

Cada ejemplo puede levantarse desde su propia carpeta con:

```powershell
.\mvnw spring-boot:run
```

Para OAuth2 también debe estar disponible Keycloak y puede utilizarse:

```bash
./scripts/keycloak-setup.sh
```

Los scripts `test-endpoints.sh` incluidos en cada proyecto permiten comprobar los endpoints protegidos y los casos de `401` y `403`.

---

[← Volver a Semana 03](../README.md)
