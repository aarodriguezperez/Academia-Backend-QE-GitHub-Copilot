# Spring Security - HTTP Basic, JWT y OAuth2

En este proyecto se trabajan tres mecanismos de autenticación y autorización con **Spring Security**: HTTP Basic, JWT y OAuth2.

Los ejemplos están separados en:

```text
02-spring-security/
├── 01-security-basic/
├── 02-security-jwt/
├── 03-security-oauth2/
└── sql-scripts/
```

Los tres buscan proteger los recursos de una API, pero utilizan diferentes formas de identificar y autorizar al usuario.

---

## 1. HTTP Basic

### ¿Qué es y qué problema resuelve?

HTTP Basic permite proteger una API enviando el usuario y contraseña en cada petición.

Spring Security valida las credenciales y posteriormente revisa si el usuario tiene el rol necesario para acceder al recurso.

### ¿Dónde se ve en el código?

El ejemplo se encuentra en:

```text
01-security-basic/
```

La configuración principal está en:

```text
SecurityConfig.java
```

donde se habilita mediante:

```java
http.httpBasic(Customizer.withDefaults());
```

También se utilizan roles como:

```java
.hasRole("EMPLOYEE")
.hasRole("MANAGER")
.hasRole("ADMIN")
```

Los usuarios se consultan desde la base de datos mediante:

```java
JdbcUserDetailsManager
```

y las tablas, usuarios, roles y contraseñas se encuentran en:

```text
sql-scripts/01-security-tables.sql
```

El archivo:

```text
scripts/test-endpoints.sh
```

permite comprobar los accesos permitidos y rechazados.

### 401 y 403

```text
401 Unauthorized
```

significa que el usuario no se autenticó correctamente.

```text
403 Forbidden
```

significa que sí está autenticado, pero no tiene el rol necesario.

### ¿Qué pasa si no se utiliza?

Sin autenticación, cualquier cliente podría acceder a los endpoints.

Además, sin autorización por roles, un usuario podría ejecutar operaciones que no le corresponden.

HTTP Basic debe utilizarse sobre HTTPS en un sistema real, ya que las credenciales se envían en cada petición.

---

## 2. JWT

### ¿Qué es y qué problema resuelve?

JWT permite autenticar al usuario una vez y utilizar posteriormente un token para acceder a los endpoints protegidos.

El flujo utilizado es:

```text
Usuario + contraseña
        ↓
POST /api/auth/login
        ↓
Servidor genera JWT
        ↓
Cliente recibe token
        ↓
Authorization: Bearer <token>
        ↓
API protegida
```

De esta manera, el usuario y contraseña no tienen que enviarse nuevamente en cada petición.

### ¿Dónde se ve en el código?

El ejemplo está en:

```text
02-security-jwt/
```

El login se implementa en:

```text
AuthController.java
```

mediante:

```text
POST /api/auth/login
```

El token se genera utilizando:

```java
JwtEncoder
```

y se valida utilizando la configuración de Spring Security como Resource Server.

Las claves utilizadas están en:

```text
src/main/resources/certs/
├── private.pem
└── public.pem
```

La clave privada firma el token y la pública permite comprobar que no fue modificado.

También se configura un tiempo de expiración:

```properties
jwt.ttl-seconds=3600
```

### Demostración del 401

El archivo:

```text
scripts/test-endpoints.sh
```

permite probar casos como:

```text
Sin token       -> 401
Token alterado  -> 401
Token inválido  -> 401
```

Esto demuestra que la API no solamente acepta tokens válidos, sino que también bloquea los incorrectos.

### ¿Qué pasa si no se valida correctamente?

Si la firma o la expiración no se verificaran, un usuario podría modificar el contenido del token o reutilizarlo indefinidamente.

Por eso la API debe comprobar que el JWT:

- tenga una firma válida;
- no haya sido alterado;
- no esté vencido;
- contenga los permisos necesarios.

---

## 3. OAuth2

### ¿Qué es y qué problema resuelve?

OAuth2 permite separar la autenticación de la API protegida.

En este proyecto se utiliza **Keycloak** como Authorization Server. La API no autentica directamente al usuario, sino que confía en los tokens emitidos por Keycloak.

El flujo general es:

```text
Usuario
   ↓
Keycloak
   ↓
Access Token
   ↓
Cliente
   ↓
API protegida
```

### Actores de OAuth2

**Resource Owner:** el usuario.

**Client:** la aplicación que quiere acceder a la API.

**Authorization Server:** autentica al usuario y genera tokens. En este proyecto es **Keycloak**.

**Resource Server:** la API que contiene los recursos protegidos y valida los tokens.

### ¿Por qué la aplicación no ve la contraseña?

En un flujo recomendado como **Authorization Code + PKCE**, el usuario introduce sus credenciales directamente en el Authorization Server.

```text
Usuario
   ↓
Keycloak
   ↓
autenticación
   ↓
token
   ↓
aplicación
```

La aplicación recibe un token, no la contraseña del usuario.

Esto reduce el riesgo de tener credenciales almacenadas o procesadas por varias aplicaciones.

### ¿Dónde se ve en el código?

El ejemplo se encuentra en:

```text
03-security-oauth2/
```

La API confía en Keycloak mediante:

```properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8090/realms/academy
```

El realm utilizado es:

```text
academy
```

En:

```text
SecurityConfig.java
```

los roles se obtienen de:

```text
realm_access.roles
```

La configuración de Keycloak se automatiza mediante:

```text
scripts/keycloak-setup.sh
```

donde se configuran el realm, el cliente, los usuarios y los roles.

Para las pruebas desde terminal, `test-endpoints.sh` utiliza:

```text
grant_type=password
```

como una forma sencilla de obtener un token. Para una aplicación real se recomienda **Authorization Code + PKCE**.

### ¿Qué pasa si no se utiliza correctamente?

Si cada aplicación administra sus propios usuarios y contraseñas:

- aumenta el número de lugares donde existen credenciales;
- aumenta el riesgo de filtraciones;
- se duplica la lógica de autenticación;
- administrar usuarios y roles se vuelve más complicado.

Con Keycloak, la autenticación se centraliza y la API solamente necesita validar los tokens.

---

## 4. BCrypt

Las contraseñas de los ejemplos Basic y JWT no se almacenan en texto plano.

En:

```text
sql-scripts/01-security-tables.sql
```

aparecen valores similares a:

```text
{bcrypt}$2y$10$...
```

El prefijo:

```text
{bcrypt}
```

indica que la contraseña fue almacenada utilizando BCrypt.

Spring Security utiliza el encoder correspondiente para comparar la contraseña proporcionada con el hash almacenado.

### ¿Por qué no guardar contraseñas en texto plano?

Si una base de datos con contraseñas en texto plano fuera comprometida, las credenciales quedarían visibles inmediatamente.

Con BCrypt se almacena un hash y no es necesario guardar la contraseña original.

---

## Comparación

| Mecanismo | Qué utiliza el cliente | Quién valida |
|---|---|---|
| HTTP Basic | Usuario y contraseña | La API |
| JWT | Bearer token | La API |
| OAuth2 | Access token | API + Authorization Server |

---

## Cómo ejecutar

Desde cada proyecto:

```powershell
.\mvnw spring-boot:run
```

Para OAuth2 también debe estar disponible Keycloak:

```bash
./scripts/keycloak-setup.sh
```

Los archivos `scripts/test-endpoints.sh` permiten probar los accesos correctos y los casos de `401` y `403`.

---

[← Volver a Semana 03](../README.md)
