# 2026-08-22 · Etapa 01 — HTTP Basic

Proyecto 01-security-basic, puerto 8071.

Primera de las tres etapas del tema de seguridad. Protege con HTTP Basic la API REST
de empleados del proyecto 16, con usuarios en MySQL y contraseñas cifradas con BCrypt.
Stack: Spring Boot 4.1.0, Spring Security 7.1.0, Java 21, MySQL 9.7.

---

## GUÍA DE LABORATORIO
Seguridad 01 — HTTP Basic

-

-

-

Spring Boot 4.1 · Spring Security 7.1 · Guía de laboratorio · 01 de 03

# Cerrar la puerta: HTTP Basic

La API que construiste en el proyecto 16 funciona perfecto y tiene un problema: cualquiera que sepa la URL puede borrar a todos tus empleados. En esta guía le pones usuarios, contraseñas y roles — sin tocar una sola línea del CRUD que ya escribiste.

- ~45 min

- Java 21

- MySQL 9.7 en Docker

- localhost:8071

- Spring Security 7.1

## 00 Poner en marcha

- [ ]**Java 21 o superior** `java -version` debe decir `21` o más.

- [ ]**Docker corriendo** `docker ps` debe responder sin errores.

- [ ]**curl instalado** Viene de fábrica en macOS, Linux y Windows 10 en adelante.

- [ ]**El proyecto 16 a mano** — lo vas a necesitar corriendo en el `8070` para la demostración de la sección 01.

```
# 1. la base de datos de empleados
$ docker start mysql-9.7

# 2. las tablas de usuarios (solo la primera vez)
$ docker exec -i mysql-9.7 mysql -uroot -pTU_PASSWORD employee_directory < ../sql-scripts/01-security-tables.sql

# 3. arrancar esta aplicación — escucha en el 8071
$ cd 01-security-basic
$ ./mvnw spring-boot:run

# 4. comprobar que quedó cerrada
$ curl -i http://localhost:8071/api/employees
HTTP/1.1 401
```

> NOTA: Si el paso 2 falla o no sabes la contraseña de root, todo el detalle está en `17-seguridad-autenticacion/instalacion.txt`. Ese `401` del paso 4 no es un error: es la señal de que la seguridad ya está puesta.

> ATENCION: **Si trabajas en Windows.** Los comandos de arriba son de macOS y Linux. En PowerShell: usa `mvnw.cmd spring-boot:run` en lugar de `./mvnw`, y si un comando ocupa varias líneas, la barra `\` del final se cambia por acento grave `` ` ``. Los scripts `.sh` de la carpeta `scripts/` necesitan **Git Bash** o **WSL**; en `instalacion.txt` están las versiones para PowerShell.

## 01 La API desnuda

Arranca el proyecto 16 — el de la semana pasada, el que funciona — y ejecuta esto desde cualquier terminal de la red:

```
$ curl -X DELETE http://localhost:8070/api/employees/1
Deleted employee id - 1
```

Sin usuario. Sin contraseña. Sin preguntas. El empleado ya no existe.

No es un bug: es *exactamente* lo que programaste. Un `@RestController` responde a quien le hable. Nunca le dijiste que preguntara quién es el que llama, así que no pregunta.

> ATENCION: **Esto es la norma, no la excepción.** Una API sin seguridad es una base de datos con una URL pública enfrente. Lo único que protegía tus datos hasta hoy era que nadie más conocía el puerto.

### El plan de las tres etapas

La pregunta “¿cómo le demuestro a la API quién soy?” tiene tres respuestas famosas, y vas a implementar las tres. Cada una arregla un defecto de la anterior:

01
**HTTP Basic** — mandas tu usuario y contraseña en cada petición. La prueba *es* el secreto. Simple, y por eso mismo frágil.

02
**JWT** — te identificas una vez y recibes un pase firmado con fecha de caducidad. Dejas de mandar la contraseña en cada llamada.

03
**OAuth2** — el pase lo emite un tercero de confianza. Tu API deja de conocer contraseñas: ni siquiera tiene tabla de usuarios.

## 02 Dos preguntas distintas

Casi todo el tema se ordena solo cuando separas dos preguntas que suenan parecido y no lo son:

| Pregunta | Nombre | Ejemplo | Si falla |
|---|---|---|---|
| ¿Quién eres? | Autenticación authentication | “Soy john, y esta es mi contraseña.” | 401 Unauthorized |
| ¿Qué puedes hacer? | Autorización authorization | “john puede leer, pero no borrar.” | 403 Forbidden |

> NOTA: **Los dos códigos están mal nombrados y confunden a todo el mundo.** El `401` se llama *Unauthorized* pero significa “no autenticado”: no sé quién eres. El `403` es el que de verdad habla de autorización: sé perfectamente quién eres, y no te toca. Aprende a leerlos así y vas a depurar el doble de rápido: **401 = revisa tus credenciales; 403 = revisa tus roles.**

### Y una tercera pregunta, que es la del curso

Autenticación y autorización son *qué*. La pregunta que separa a Basic de JWT y de OAuth2 es otra: **¿cómo viaja la prueba de quién eres, y quién la emitió?** Con Basic, la prueba es tu contraseña y la emites tú, en cada petición. Guarda esa frase: en la etapa 02 vas a ver cómo cambia.

## 03 La cadena de filtros

Antes del detalle técnico, la idea completa en una imagen. Tu petición sale llevando las credenciales **a la vista**, cruza al servidor y atraviesa **dos controles distintos**: de cada uno puede salir rechazada, y por motivos que no son el mismo.

> **Imagen:** Un portátil envía una petición dibujada como una postal abierta con el usuario john y la contraseña test123 legibles, apoyada en un cristal transparente rotulado base64. Debajo, escondido tras un muro, alguien con prismáticos lee la misma contraseña. La petición entra en un recinto punteado rotulado SERVER y atraviesa dos puertas: la primera pregunta WHO ARE YOU y de ella sale una flecha roja marcada 401; la segunda pregunta WHAT CAN YOU DO y de ella sale otra flecha roja marcada 403. Quien supera las dos llega por una flecha verde marcada 200 hasta una caja fuerte abierta con las fichas de los empleados.

Fig. 1 — Las credenciales viajan legibles: base64 es un cristal, no una cerradura. Dentro del servidor hay dos controles, y cada uno rechaza por su cuenta. Ojo: al fisgón le funciona porque la petición va **sin HTTPS**; con TLS solo vería ruido.

Spring Security no vive dentro de tu controlador. Vive *antes*. Se mete en la fila de filtros por la que pasa todo request antes de que Tomcat se lo entregue a Spring MVC:

> **Diagrama:** curl / navegador · Authorization: Basic am9objp0ZXN0MTIz · SECURITYFILTERCHAIN · BasicAuthenticationFilter · decodifica base64 → busca en members → compara BCrypt · AuthorizationFilter · compara tus roles contra las reglas de filterChain() · 401 · no sé quién eres · 403 · sé quién eres, no te toca · pasa · DispatcherServlet · EmployeeRestController · tu código del proyecto 16 · sin una sola línea modificada

Fig. 2 — El mismo recorrido, con los nombres reales de los filtros. Todo request los atraviesa antes de tocar tu controlador.

Esa es la idea que hay que llevarse de esta etapa, porque las tres etapas del curso usan **la misma cadena**. En JWT y en OAuth2 solo cambia el primer filtro: quién valida y contra qué. El controlador nunca se entera de que existe la seguridad — y por eso `EmployeeRestController` queda idéntico al del proyecto 16.

> NOTA: **No te lo imagines: míralo.** Descomenta esta línea en `application.properties` y Spring te imprime, en cada petición, la lista completa de filtros que atravesó:

```
logging.level.org.springframework.security=DEBUG
```

## 04 Preparar el terreno

### 1. La dependencia

Una sola, en el `pom.xml`:

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

Antes de escribir nada más, arranca la app y mira la consola. Vas a encontrar una línea así:

```
Using generated security password: 809b7001-dd01-4a94-b0ee-681936fc82e5
```

Y ahora la API ya está cerrada, sin haber escrito una línea de código:

```
$ curl -i http://localhost:8071/api/employees
HTTP/1.1 401

# con el usuario "user" y esa contraseña generada:
$ curl -u user:809b7001-dd01-4a94-b0ee-681936fc82e5 http://localhost:8071/api/employees
[{"firstName":"Patrobas", ...}]
```

> NOTA: **Esto es Spring Boot en su forma más pura:** agregas una dependencia y el comportamiento por defecto es *seguro* (todo cerrado), no *cómodo* (todo abierto). La contraseña cambia en cada arranque, precisamente para que no se te ocurra usarla en serio. Todo lo que sigue existe para reemplazar ese usuario de juguete por usuarios de verdad.

### 2. Las tablas de usuarios

Los usuarios van en la misma base `employee_directory`, en dos tablas nuevas. Ejecuta el script `sql-scripts/01-security-tables.sql`:

```
$ docker start mysql-9.7
$ docker exec -i mysql-9.7 mysql -uroot -pTU_PASSWORD employee_directory < sql-scripts/01-security-tables.sql
```

Quedan así — `members` con las contraseñas, `roles` con los permisos:

| Usuario | Contraseña | Roles | Puede |
|---|---|---|---|
| john | test123 | EMPLOYEE | leer |
| mary | test123 | EMPLOYEE MANAGER | leer, crear, modificar |
| susan | test123 | EMPLOYEE MANAGER ADMIN | todo, incluido borrar |

Abre la tabla `members` y mira la columna `pw`. Los tres usuarios tienen **la misma contraseña**, y sin embargo:

```
john   {bcrypt}$2y$10$q5C89SItU5ZKPZlTspXrZOOcm7njHEeRF7dys6b.Bgo7NhKWbMGfG
mary   {bcrypt}$2y$10$y0UvRlnLKlOh7nBfH8sNvuXUIVhvwOMYaz1ysyJoPYvwY8tCg.K/i
susan  {bcrypt}$2y$10$6eOesXl7A1E7kaE7UYulPu4h5o5r6Yqd/F/dPFMWx2kDTZA64qU1W
```

Tres hashes completamente distintos para la palabra `test123`. Eso no es un error: es el **salt**, y es la razón por la que BCrypt sigue siendo la respuesta correcta en 2026. Lo desarmamos en la sección de referencia.

> ATENCION: **El prefijo `{bcrypt}` no es decorativo.** Es como Spring sabe con qué algoritmo comparar. Si lo omites, el arranque truena con `There is no PasswordEncoder mapped for the id "null"`. Y sí, la columna es `char(68)` por una razón exacta: 8 caracteres de `{bcrypt}` + 60 del hash = 68.

## 05 El código: dos beans

Toda la seguridad de este proyecto cabe en una clase, `security/SecurityConfig.java`, con dos beans que responden a las dos preguntas de la sección 02.

### Bean 1 — ¿de dónde salen los usuarios?

```
@Bean
public UserDetailsService userDetailsService(DataSource theDataSource) {

    JdbcUserDetailsManager theUserDetailsManager = new JdbcUserDetailsManager(theDataSource);

    theUserDetailsManager.setUsersByUsernameQuery(
            "select user_id, pw, active from members where user_id=?");

    theUserDetailsManager.setAuthoritiesByUsernameQuery(
            "select user_id, role from roles where user_id=?");

    return theUserDetailsManager;
}
```

Spring trae un esquema por defecto (tablas `users` y `authorities`). Si usaras esos nombres exactos, `new JdbcUserDetailsManager(dataSource)` bastaría y las dos consultas sobrarían. Las nuestras se llaman `members` y `roles`, así que hay que decirle cómo buscar: **Spring no adivina tu esquema**. El `?` de cada consulta es el username.

### Bean 2 — ¿quién puede hacer qué?

```
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http.authorizeHttpRequests(configurer -> configurer
            .requestMatchers(HttpMethod.GET,    "/api/employees").hasRole("EMPLOYEE")
            .requestMatchers(HttpMethod.GET,    "/api/employees/**").hasRole("EMPLOYEE")
            .requestMatchers(HttpMethod.POST,   "/api/employees").hasRole("MANAGER")
            .requestMatchers(HttpMethod.PUT,    "/api/employees").hasRole("MANAGER")
            .requestMatchers(HttpMethod.PATCH,  "/api/employees/**").hasRole("MANAGER")
            .requestMatchers(HttpMethod.DELETE, "/api/employees/**").hasRole("ADMIN")
            .anyRequest().authenticated());

    http.httpBasic(Customizer.withDefaults());
    http.csrf(csrf -> csrf.disable());
    http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    return http.build();
}
```

> ATENCION: **La trampa número uno del tema.** En la base de datos el rol se guarda como `ROLE_EMPLOYEE`, pero en Java se escribe `hasRole("EMPLOYEE")`, *sin* el prefijo: Spring lo agrega solo. Si escribes `hasRole("ROLE_EMPLOYEE")`, buscará `ROLE_ROLE_EMPLOYEE`, no lo va a encontrar nunca, y vas a recibir `403` en todo sin ninguna pista de por qué. (Si prefieres escribirlo completo, existe `hasAuthority("ROLE_EMPLOYEE")`, que no agrega nada.)

### Las tres líneas del final

- `httpBasic(...)` — activa HTTP Basic. Es lo que hace que Spring devuelva la cabecera `WWW-Authenticate` y lea la cabecera `Authorization`.

- `csrf(...disable())` — **solo** porque esto es una API REST sin cookies de sesión. CSRF protege contra que *otra* página use la cookie de sesión de tu navegador a tus espaldas; si no hay cookie de sesión, no hay nada que robar. En una app web con formularios y login por sesión, CSRF se deja **encendido**. Desactivarlo “porque me daba error” es de las decisiones que crean vulnerabilidades reales.

- `sessionCreationPolicy(STATELESS)` — el servidor no guarda nada entre peticiones. Cada request llega con sus credenciales y se autentica desde cero. Eso es ser *stateless*, y es la razón por la que Basic manda la contraseña una y otra vez.

> NOTA: **Fíjate en lo que NO cambió:** `EmployeeRestController`, `EmployeeService`, `EmployeeRepository` y `Employee` son byte por byte los del proyecto 16. Toda la seguridad entró por un archivo nuevo y una dependencia. Eso es la cadena de filtros trabajando.

## 06 La matriz de roles

Esta es la tabla que tu configuración acaba de crear. Cada celda es comprobable desde la terminal:

| Operación | Rol necesario | john | mary | susan |
|---|---|---|---|---|
| GET /api/employees | EMPLOYEE | 200 | 200 | 200 |
| POST /api/employees | MANAGER | 403 | 200 | 200 |
| PUT /api/employees | MANAGER | 403 | 200 | 200 |
| PATCH /api/employees/{id} | MANAGER | 403 | 200 | 200 |
| DELETE /api/employees/{id} | ADMIN | 403 | 403 | 200 |

Las cuatro pruebas que más enseñan, con la salida real:

```
# 1. sin credenciales: ya no entra nadie
$ curl -i http://localhost:8071/api/employees
HTTP/1.1 401
WWW-Authenticate: Basic realm="Realm", charset="UTF-8"

# 2. john es EMPLOYEE: puede leer
$ curl -u john:test123 http://localhost:8071/api/employees
[{"firstName":"Patrobas","lastName":"Filologo","email":"patrobas@gmail.com","id":1}, ...]

# 3. john intenta borrar: sé quién eres, y no te toca
$ curl -u john:test123 -X DELETE http://localhost:8071/api/employees/1
{"status":403,"error":"Forbidden","path":"/api/employees/1"}

# 4. susan es ADMIN: ella sí
$ curl -u susan:test123 -X DELETE http://localhost:8071/api/employees/5
Deleted employee id - 5
```

> NOTA: **Compara la 3 con la 1.** Son fallos distintos y el código lo dice: en la 1 la API no sabe quién llama (`401`); en la 3 sabe perfectamente que es john, y por eso puede decirle que no (`403`). Ese par es la pregunta de examen más probable de todo el tema.

### La matriz completa, automatizada

El script `scripts/test-endpoints.sh` corre las seis comprobaciones de la matriz, marca cada una con `OK` o `!!`, y crea y borra un empleado temporal para que tus datos queden como estaban:

```
$ ./scripts/test-endpoints.sh
OK  john                   GET    -> HTTP 200   (esperado 200)
OK  john                   POST   -> HTTP 403   (esperado 403)
OK  mary                   DELETE -> HTTP 403   (esperado 403)
OK  susan                  DELETE -> HTTP 200   (esperado 200)
```

## 07 El talón de Aquiles

Ya tienes la API protegida. Ahora vamos a romperla. Pregunta: cuando escribes `-u john:test123`, ¿qué manda curl exactamente?

```
$ curl -v -u john:test123 http://localhost:8071/api/employees
> Authorization: Basic am9objp0ZXN0MTIz
```

Se ve cifrado. No lo está. Es **base64**, que no es un algoritmo de seguridad sino de empaquetado — y se revierte con un comando que ya tienes instalado:

```
$ echo -n 'am9objp0ZXN0MTIz' | base64 -d
john:test123
```

Usuario y contraseña, en claro, en un solo paso y sin fuerza bruta. Y no viajan una vez: viajan **en cada petición**, porque la app es stateless.

> ATENCION: **Conclusión operativa: HTTP Basic sin HTTPS es mandar la contraseña en texto plano.** Cualquiera con acceso al tráfico — un WiFi público, un proxy corporativo, un log mal configurado — la lee. Basic *no* está prohibido en producción: está prohibido *sin TLS*. Y fíjate en el detalle cruel: tu contraseña acaba también en los logs de acceso, en el historial de tu terminal y en la memoria de cualquier cliente que la reenvíe.

### Los cuatro defectos que arregla la etapa 02

- **La contraseña viaja siempre.** Mil peticiones son mil oportunidades de interceptarla.

- **No caduca.** Si alguien la captura, sirve hasta que la cambies — y no te vas a enterar.

- **No hay logout.** No existe forma de invalidar unas credenciales desde el servidor.

- **No se puede acotar.** No puedes dar acceso “solo de lectura y solo por una hora” a una app de terceros: o le das la contraseña completa, o nada.

Los cuatro tienen el mismo origen: **la prueba de identidad es el secreto mismo**. La etapa 02 lo cambia por un pase firmado, temporal y revocable. Eso es JWT.

## BCrypt y el salt

Recuerda los tres hashes distintos para la misma contraseña. Así se lee uno:

```
$2y$ 10 $ q5C89SItU5ZKPZlTspXrZO Ocm7njHEeRF7dys6b.Bgo7NhKWbMGfG
  │    │        │                          │
  │    │        │                          └─ el hash (31 chars)
  │    │        └─ el SALT: 22 chars aleatorios, distintos por usuario
  │    └─ coste 10: el hash se recalcula 2^10 = 1024 veces
  └─ versión del algoritmo
```

- **El salt va dentro del hash.** No hay que guardarlo aparte — por eso dos usuarios con la misma contraseña tienen hashes distintos, y por eso *mirar la tabla no te dice quién repitió contraseña*.

- **El coste es deliberadamente lento.** MD5 y SHA-256 están diseñados para ser rápidos: una GPU prueba miles de millones por segundo. BCrypt está diseñado para ser *lento*, y el coste se sube cada pocos años conforme el hardware mejora.

- **Es de una sola vía.** Ni Spring ni tú pueden recuperar la contraseña desde el hash. Por eso las apps serias te dejan *restablecer* la contraseña, nunca *recordártela*: si una web te manda tu contraseña por correo, la tenía guardada en claro.

- **Nunca escribas tu propio hash.** Ni MD5, ni SHA con salt casero, ni “yo le doy la vuelta al string”. Este es el ejemplo clásico de no inventar tu propia criptografía.

> NOTA: **¿Y cómo genero un hash para un usuario nuevo?** Con la herramienta que ya viene en macOS y Linux: `htpasswd -bnBC 10 "" mipassword`. Copia lo que sale después de los dos puntos y pégalo en la columna `pw` precedido de `{bcrypt}`.

## Por qué los tutoriales de internet no te van a funcionar

Este proyecto usa **Spring Boot 4.1 con Spring Security 7.1**. Casi todo lo que vas a encontrar buscando “spring security rest api” — vídeos, blogs, respuestas de Stack Overflow, y también lo que te conteste una IA — está escrito para Boot 3 y Security 6, o peor, para Boot 2 y Security 5. **Ese código no compila aquí.** Traducción rápida:

| Lo que vas a encontrar | Lo que va aquí | Desde |
|---|---|---|
| extends WebSecurityConfigurerAdapter | un @Bean SecurityFilterChain | Security 5.7 |
| .authorizeRequests() | .authorizeHttpRequests() | Security 5.8 |
| .antMatchers(...) | .requestMatchers(...) | Security 6.0 |
| cadenas con .and() | lambdas: http.csrf(c -> c.disable()) | Security 6.1 |
| spring-boot-starter-web | spring-boot-starter-webmvc | Boot 4.0 |
| spring-boot-starter-oauth2-resource-server | spring-boot-starter-security-oauth2-resource-server | Boot 4.0 etapa 03 |

> ATENCION: **Esto no es un obstáculo del curso, es el curso.** La habilidad que separa a un junior de alguien que se puede soltar solo no es memorizar la API de este año: es abrir las notas de versión, mirar la fecha del tutorial antes de copiarlo, y confiar en el compilador por encima del blog. Regla práctica: **si el ejemplo no dice para qué versión es, asume que está viejo.**

## Errores comunes

| Síntoma | Causa | Arreglo |
|---|---|---|
| Arranca y truena: There is no PasswordEncoder mapped for the id "null" | Al hash de la base de datos le falta el prefijo {bcrypt} . | Agrégalo. Spring usa ese prefijo para saber qué algoritmo comparar. |
| 403 en absolutamente todo, incluso con susan | hasRole("ROLE_ADMIN") : Spring busca ROLE_ROLE_ADMIN . | hasRole("ADMIN") , sin prefijo. O hasAuthority("ROLE_ADMIN") . |
| 401 siempre, con la contraseña correcta | La columna active está en 0 , o la columna pw es más corta que 68 y MySQL truncó el hash en silencio. | active = 1 y char(68) . |
| GET funciona, pero POST, PUT, PATCH y DELETE devuelven 401 con el cuerpo vacío | Falta http.csrf(csrf -> csrf.disable()) . Es un fallo de CSRF disfrazado: el filtro rechaza el request antes de autenticarte, así que la respuesta parece de credenciales malas y tus credenciales están bien. | Desactiva CSRF en APIs REST stateless. Fíjate en que solo se rompen los métodos que modifican : esa es la firma del problema. |
| JOHN en mayúsculas entra igual que john | No es Spring: es MySQL. La collation de las tablas es latin1_swedish_ci , y ese _ci significa case-insensitive — el where user_id=? ignora mayúsculas. | Si necesitas distinguirlas, la collation de la columna tiene que ser _bin o _cs . Compruébalo tú: select * from members where user_id='JOHN'; |
| El navegador me pide usuario y contraseña en una ventanita | Eso es la cabecera WWW-Authenticate haciendo su trabajo. No es un error. | Para probar la API usa curl o Postman. El navegador, además, cachea esas credenciales hasta que lo cierras. |
| Cambié la contraseña en la tabla y sigue sin entrar | Guardaste el texto plano, no el hash. | Genera el hash con htpasswd -bnBC 10 "" nueva y guárdalo con el prefijo. |
| Port 8071 was already in use | Ya tienes esta app corriendo, en otra terminal o en Eclipse. | lsof -i :8071 y mata el proceso, o cambia server.port . |

## Ejercicios

- [ ]**1. Un usuario nuevo** Crea a `peter` con rol EMPLOYEE y contraseña `abc123`, generando tú el hash. Compruébalo con curl: debe leer y no debe poder crear.

- [ ]**2. Degrada a susan** Quítale `ROLE_ADMIN` en la tabla `roles` y reinicia. ¿Qué código devuelve ahora su DELETE? Explica por qué es `403` y no `401`.

- [ ]**3. Abre una puerta** Haz que `GET /api/employees` sea público (sin credenciales) pero que el resto siga protegido. Pista: `permitAll()`. Y responde: ¿por qué el *orden* de los `requestMatchers` importa?

- [ ]**4. Rompe CSRF a propósito** Comenta la línea `http.csrf(...)`, reinicia y prueba un POST con mary. Anota el código exacto y el cuerpo de la respuesta. Ahora ya reconoces ese error cuando te pase de verdad.

- [ ]**5. Mira la cadena** Activa `logging.level.org.springframework.security=DEBUG`, haz un GET y cuenta cuántos filtros atraviesa el request. Encuentra en la lista los dos de la Fig. 2.

- [ ]**6. Piensa como atacante** Estás en el WiFi de un café y tu compañero usa esta API sin HTTPS. Describe, en tres pasos, cómo obtendrías su contraseña. Luego di qué cambia exactamente si la API usara HTTPS.

> NOTA: **Siguiente parada:** etapa 02 — JWT. Vas a dejar de mandar la contraseña en cada petición y a cambiarla por un pase firmado con fecha de caducidad. Todo lo que construiste hoy (las tablas, BCrypt, los roles, la cadena de filtros) se queda: solo cambia lo que viaja en la cabecera `Authorization`.

Academia MTY · Seguridad y autenticación 01/03
Spring Boot 4.1.0 · Spring Security 7.1.0 · Java 21

---

## CÓDIGO DISTINTIVO DE ESTA ETAPA

El CRUD (Employee, EmployeeRestController, EmployeeService, EmployeeRepository) es
idéntico al de las otras dos etapas y está en el documento de setup y referencia.
Aquí va solo lo que hace distinta a esta etapa.

### `01-security-basic/src/main/java/com/luv2code/springboot/cruddemo/security/SecurityConfig.java`

```java
package com.luv2code.springboot.cruddemo.security;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuracion de seguridad con HTTP Basic.
 *
 * Dos beans, dos responsabilidades distintas:
 *
 *   userDetailsService -> DE DONDE salen los usuarios  (autenticacion: quien eres)
 *   filterChain        -> QUIEN puede hacer QUE        (autorizacion: que puedes hacer)
 */
@Configuration
public class SecurityConfig {

    /**
     * Le decimos a Spring que los usuarios viven en la base de datos.
     *
     * Spring trae un esquema por defecto (tablas "users" y "authorities"). El nuestro
     * se llama distinto (members / roles), asi que hay que decirle como consultarlo.
     * Spring NO adivina tu esquema.
     *
     * Las dos consultas reciben el username como unico parametro (el "?").
     */
    @Bean
    public UserDetailsService userDetailsService(DataSource theDataSource) {

        JdbcUserDetailsManager theUserDetailsManager = new JdbcUserDetailsManager(theDataSource);

        // como buscar un usuario: debe devolver username, password y si esta activo
        theUserDetailsManager.setUsersByUsernameQuery(
                "select user_id, pw, active from members where user_id=?");

        // como buscar sus roles: debe devolver username y rol
        theUserDetailsManager.setAuthoritiesByUsernameQuery(
                "select user_id, role from roles where user_id=?");

        return theUserDetailsManager;
    }

    /**
     * La cadena de filtros: TODO request pasa por aqui antes de llegar al @RestController.
     *
     * OJO con hasRole("EMPLOYEE"): en la base de datos el rol se guarda como
     * "ROLE_EMPLOYEE", pero aqui se escribe SIN el prefijo. Spring lo agrega solo.
     * Si escribes hasRole("ROLE_EMPLOYEE") buscara "ROLE_ROLE_EMPLOYEE" y nada funcionara.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(configurer -> configurer
                // leer: cualquier empleado
                .requestMatchers(HttpMethod.GET, "/api/employees").hasRole("EMPLOYEE")
                .requestMatchers(HttpMethod.GET, "/api/employees/**").hasRole("EMPLOYEE")
                // crear y modificar: solo managers
                .requestMatchers(HttpMethod.POST, "/api/employees").hasRole("MANAGER")
                .requestMatchers(HttpMethod.PUT, "/api/employees").hasRole("MANAGER")
                .requestMatchers(HttpMethod.PATCH, "/api/employees/**").hasRole("MANAGER")
                // borrar: solo admins
                .requestMatchers(HttpMethod.DELETE, "/api/employees/**").hasRole("ADMIN")
                // cualquier otra cosa: al menos hay que estar autenticado
                .anyRequest().authenticated());

        // usar autenticacion HTTP Basic
        http.httpBasic(Customizer.withDefaults());

        // Desactivamos CSRF porque esta es una API REST sin sesiones ni cookies.
        // CSRF protege contra que OTRA pagina use la cookie de sesion del navegador;
        // si no hay cookie de sesion, no hay nada que robar. En una app web con
        // formularios y login por sesion, CSRF se deja ENCENDIDO.
        http.csrf(csrf -> csrf.disable());

        // Sin sesion en el servidor: cada request llega con sus credenciales
        // y se autentica desde cero. Eso es ser "stateless".
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
```

### `01-security-basic/src/main/resources/application.properties`

```properties
#
# JDBC properties
#
spring.datasource.url=jdbc:mysql://localhost:3306/employee_directory
spring.datasource.username=springstudent
spring.datasource.password=springstudent

#
# Puerto de esta etapa (01 = 8071, 02 = 8072, 03 = 8073)
#
server.port=8071

#
# Descomenta para ver que filtros de seguridad atraviesa cada request.
# Es la mejor forma de ENTENDER la cadena de filtros: no la imagines, miralo.
#
#logging.level.org.springframework.security=DEBUG
```

### `01-security-basic/pom.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>4.1.0</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.luv2code.springboot</groupId>
	<artifactId>security-basic</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>security-basic</name>
	<description>Seguridad REST con HTTP Basic - Spring Security 7</description>
	<url/>
	<licenses>
		<license/>
	</licenses>
	<developers>
		<developer/>
	</developers>
	<scm>
		<connection/>
		<developerConnection/>
		<tag/>
		<url/>
	</scm>
	<properties>
		<java.version>21</java.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-security</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-webmvc</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-jackson</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-devtools</artifactId>
			<scope>runtime</scope>
			<optional>true</optional>
		</dependency>
		<dependency>
			<groupId>com.mysql</groupId>
			<artifactId>mysql-connector-j</artifactId>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>

</project>
```
