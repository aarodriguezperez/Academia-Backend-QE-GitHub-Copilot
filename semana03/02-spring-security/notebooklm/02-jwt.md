# 2026-08-22 · Etapa 02 — JWT

Proyecto 02-security-jwt, puerto 8072.

Segunda etapa. La contraseña deja de viajar en cada petición: se cambia una sola vez
por un token JWT firmado con RSA (RS256) y con fecha de caducidad. Usa las MISMAS
tablas members y roles de la etapa 01.
Stack: Spring Boot 4.1.0, Spring Security 7.1.0, Java 21, MySQL 9.7, RSA 2048.

---

## GUÍA DE LABORATORIO
Seguridad 02 — JWT

-

-

-

Spring Boot 4.1 · Spring Security 7.1 · Guía de laboratorio · 02 de 03

# El pase: JWT

En el proyecto 01 la contraseña viajaba en cada petición. Aquí viaja *una sola vez*: la cambias por un pase firmado con fecha de caducidad. Las tablas, los roles y las reglas de autorización se quedan exactamente como estaban — solo cambia lo que llevas en la cabecera.

- ~60 min

- Java 21

- MySQL 9.7 en Docker

- localhost:8072

- RSA 2048 · RS256

## 00 Poner en marcha

- [ ]**Java 21 y Docker** `java -version` debe decir `21` o más, y `docker ps` debe responder sin errores.

- [ ]**Las tablas del proyecto 01** — este proyecto usa *las mismas* `members` y `roles`. Si ya hiciste la etapa 01, no hay nada que crear.

- [ ]**Las llaves RSA** ya vienen dentro del proyecto (`src/main/resources/certs/`). Para generar las tuyas: `instalacion.txt`.

```
# 1. la base de datos
$ docker start mysql-9.7

# 2. solo si NO hiciste la etapa 01: crear las tablas de usuarios
$ docker exec -i mysql-9.7 mysql -uroot -pTU_PASSWORD employee_directory < ../sql-scripts/01-security-tables.sql

# 3. arrancar esta aplicación — escucha en el 8072
$ cd 02-security-jwt
$ ./mvnw spring-boot:run

# 4. comprobar: esto debe devolverte un token
$ curl -u john:test123 -X POST http://localhost:8072/api/auth/login
{"accessToken":"eyJhbGciOiJSUzI1NiJ9...","tokenType":"Bearer","expiresIn":3600,"user":"john"}
```

> NOTA: Puedes tener la etapa 01 (`8071`) y esta (`8072`) corriendo **a la vez**: usan puertos distintos a propósito, para que compares la misma llamada contra las dos.

> ATENCION: **Si trabajas en Windows.** Los comandos de arriba son de macOS y Linux. En PowerShell: usa `mvnw.cmd spring-boot:run` en lugar de `./mvnw`, y si un comando ocupa varias líneas, la barra `\` del final se cambia por acento grave `` ` ``. Los scripts `.sh` de la carpeta `scripts/` necesitan **Git Bash** o **WSL**; en `instalacion.txt` están las versiones para PowerShell.

## 01 Lo que arrastra Basic

El proyecto 01 quedó funcionando y seguro… mientras uses HTTPS. Pero tenía cuatro defectos que no se arreglan con más configuración, porque nacen todos de la misma raíz: **la prueba de identidad era el secreto mismo**.

| Defecto de Basic | Qué hace JWT |
|---|---|
| La contraseña viaja en cada petición | Viaja una vez, en el login. Después viaja un token. |
| No caduca nunca | El token trae fecha de expiración dentro ( exp ). |
| No hay logout posible | Tiras el token. Y como dura poco, la ventana es corta. |
| No se puede acotar | El token dice qué puedes hacer: puedes emitir uno limitado. |

> NOTA: **Lo que NO cambia.** Las tablas `members` y `roles` son las mismas — este proyecto usa el mismo script SQL del 01. Los usuarios son los mismos (`john`, `mary`, `susan`, todos con `test123`). Y las reglas de autorización son idénticas, línea por línea. Guarda esa idea: al terminar vas a poder comparar los dos `SecurityConfig` y ver que la parte de *quién puede qué* no se tocó.

## 02 Anatomía de un JWT

Un JWT es un texto largo con **dos puntos** que lo parten en tres. Eso es todo:

```
eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJzZWN1cml0eS1qd3QiLCJzdWIiOiJqb2huIiwi....Aiw4Heg6MZJTHpCTKkXWcTPOTJ0EYp43oC9D54tJ1_hIr6Z8...
└──── HEADER ────┘ └──────── PAYLOAD ────────┘ └──────── FIRMA ────────┘
```

Las tres partes son **base64** — el mismo base64 del proyecto 01. Así que se leen con el mismo comando, sin ninguna llave:

```
# el HEADER dice con qué algoritmo se firmó
$ echo 'eyJhbGciOiJSUzI1NiJ9' | base64 -d
{"alg":"RS256"}

# el PAYLOAD es el contenido: quién eres y qué puedes hacer
$ echo $TOKEN | cut -d. -f2 | base64 -d
{
  "iss": "security-jwt",
  "sub": "john",
  "exp": 1787352693,
  "iat": 1787349093,
  "roles": ["ROLE_EMPLOYEE"]
}
```

> ATENCION: **Léelo dos veces: acabas de abrir el token sin ninguna contraseña.** Un JWT *no está cifrado*. Cualquiera que lo intercepte lee su contenido completo. Por eso jamás se mete ahí una contraseña, un número de tarjeta ni nada privado. Lo que un JWT garantiza no es el secreto: es que **nadie pudo modificarlo**.

### Los claims que importan

| Claim | Significa | En nuestro token |
|---|---|---|
| iss | issuer : quién lo emitió | security-jwt |
| sub | subject : de quién es | john |
| iat | issued at : cuándo se emitió | timestamp Unix |
| exp | expires : hasta cuándo vale | iat + 3600 |
| roles | claim propio nuestro | ["ROLE_EMPLOYEE"] |

Los cuatro primeros son estándar (RFC 7519); `roles` nos lo inventamos nosotros. Un JWT admite los claims que quieras — con la condición de que quepan, porque el token viaja en una cabecera HTTP en *cada* petición.

## 03 Firmar no es cifrar

Si cualquiera puede leer el payload… ¿qué impide que lo reescriba y se ponga `ROLE_ADMIN`? La tercera parte del token: **la firma**.

Este proyecto usa **RS256** = RSA + SHA-256, que trabaja con *dos* llaves distintas:

| Llave | Quién la tiene | Para qué sirve |
|---|---|---|
| private.pem | Solo el servidor que emite | Firmar tokens nuevos |
| public.pem | Cualquiera, sin problema | Validar que una firma es auténtica |

Esa asimetría es todo el truco: **validar y firmar son operaciones distintas, con llaves distintas**. Puedes repartir la llave pública al mundo entero y nadie podrá emitir un token falso con ella. Si reescribes el payload, la firma deja de corresponder y el servidor lo detecta al instante.

> NOTA: **¿Y por qué no una sola contraseña compartida (HS256)?** También existe y es más simple: un único secreto que firma y valida. El problema es que *todo el que puede validar, puede falsificar*. Con RSA puedes tener veinte servicios validando tokens y uno solo capaz de emitirlos. Esa es exactamente la arquitectura del proyecto 03, así que empezamos ya con RSA.

### Generar el par de llaves

```
$ mkdir -p src/main/resources/certs
$ openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -out src/main/resources/certs/private.pem
$ openssl rsa -pubout -in src/main/resources/certs/private.pem -out src/main/resources/certs/public.pem
```

> ATENCION: **En un proyecto real, `private.pem` NUNCA se sube a git.** Aquí está dentro del repositorio a propósito, para que la clase funcione sin pasos extra. En producción va en una variable de entorno, en un gestor de secretos o en un almacén de llaves — y el `.gitignore` la excluye. Si tu llave privada se filtra, cualquiera puede emitir tokens válidos con el rol que se le antoje.

## 04 Taquilla y torniquete

Con JWT la aplicación tiene **dos puertas distintas**, y por eso el `SecurityConfig` tiene dos cadenas de filtros. Piensa en un concierto: compras el boleto una vez en la taquilla, y luego lo enseñas en el torniquete cada vez que entras.

> **Imagen:** Dos bandas horizontales. Arriba, rotulada ONCE: john entrega en la ventanilla de una taquilla una tarjeta con una llave y una contraseña oculta; la tarjeta se queda dentro, marcada con una flecha roja que apunta al interior. El empleado consulta un archivador abierto lleno de fichas y de la taquilla sale un pase que muestra el nombre john, el rol EMPLOYEE, un reloj con 60:00 y un sello de cera roja estampado con la palabra SEALED. Abajo, rotulada EVERY REQUEST: john cruza tres torniquetes seguidos enseñando solo el pase, sin ninguna contraseña, y al final el mismo archivador aparece cerrado con cadena y candado. En un recuadro punteado aparte, a la izquierda, el encapuchado con prismáticos lee el contenido del pase en un globo de pensamiento, y debajo su copia falsificada tiene el sello de cera partido en dos y una equis roja la rechaza.

Fig. 1 — La contraseña entra en la taquilla y se queda ahí; a partir de entonces solo circula el pase. El archivador se consulta una vez y luego queda encadenado: validar el token no toca la base de datos. Ojo con el rincón del fisgón: lo que se rechaza es la **falsificación**, la del sello de cera roto. Un pase **robado intacto** sí funciona — hasta que el reloj llega a cero.

> **Diagrama:** 1 · LOGIN — UNA SOLA VEZ · cliente · -u john:test123 · cadena 1 · HTTP Basic · /api/auth/** · members / roles · BCrypt · MySQL · JwtEncoder · firma con la llave PRIVADA · token → al cliente · 2 · CADA PETICIÓN — YA SIN CONTRASEÑA · cliente · Bearer  · JwtDecoder · valida con la llave PÚBLICA · AuthorizationFilter · roles del claim, no de la BD · Controller · La base de datos NO se consulta aquí. · Todo lo que hace falta ya viene firmado dentro del token.

Fig. 2 — El mismo reparto, con los nombres reales: qué cadena de filtros atiende cada puerta y con qué llave.

Ese recuadro de abajo es la ventaja operativa que hace famoso a JWT: **validar un token no toca la base de datos**. Con Basic, cada petición era una consulta a `members` más un cálculo de BCrypt (que, recuérdalo, es lento a propósito). Con JWT es una verificación de firma en memoria. Por eso escala.

> ATENCION: **Y ese mismo recuadro es su gran desventaja.** Si borras a john de la base de datos o le quitas `ROLE_ADMIN`, su token sigue funcionando hasta que caduque — porque nadie va a preguntarle nada a la base de datos. Un JWT no se puede “apagar”. Por eso se emiten con vida corta, y por eso existen los *refresh tokens* y las listas de revocación.

## 05 El código

### La dependencia

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security-oauth2-resource-server</artifactId>
</dependency>
```

> ATENCION: **Sí, dice “oauth2” aunque todavía no estemos haciendo OAuth2.** No es un error de copiar y pegar. JWT nació dentro del ecosistema OAuth2 y Spring metió ahí todo el soporte de tokens. Esa misma dependencia es la que usarás sin cambios en el proyecto 03 — lo único que cambiará es de dónde viene la llave. Ojo también con el nombre: en Spring Boot 3 era `spring-boot-starter-oauth2-resource-server`, *sin* el `security-`.

### Dos cadenas, porque hay dos puertas

```
// CADENA 1 — la taquilla: solo /api/auth/**, y con contraseña
@Bean
@Order(1)
public SecurityFilterChain loginFilterChain(HttpSecurity http) throws Exception {
    http.securityMatcher("/api/auth/**");
    http.authorizeHttpRequests(c -> c.anyRequest().authenticated());
    http.httpBasic(Customizer.withDefaults());
    ...
}

// CADENA 2 — el torniquete: todo lo demás, y solo con token
@Bean
@Order(2)
public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(c -> c
            .requestMatchers(HttpMethod.GET,    "/api/employees").hasRole("EMPLOYEE")
            .requestMatchers(HttpMethod.DELETE, "/api/employees/**").hasRole("ADMIN")
            ...);
    http.oauth2ResourceServer(oauth2 -> oauth2.jwt(...));
    ...
}
```

`securityMatcher` es la pieza nueva: acota una cadena a unas rutas. Con `@Order(1)` Spring prueba primero la de login; si la ruta no encaja, pasa a la siguiente. Las reglas de roles de la cadena 2 son un copiar y pegar del proyecto 01 — **ni una coma distinta**.

### Las dos llaves, en dos beans

```
// FIRMAR: llave privada. Solo este servidor emite tokens.
@Bean
public JwtEncoder jwtEncoder() {
    JWK jwk = new RSAKey.Builder(publicKey).privateKey(privateKey).build();
    return new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(jwk)));
}

// VALIDAR: llave pública. Cualquiera podría validar; nadie más puede firmar.
@Bean
public JwtDecoder jwtDecoder() {
    return NimbusJwtDecoder.withPublicKey(publicKey).build();
}
```

Las llaves se inyectan solas desde `application.properties` con `@Value`: Spring convierte el archivo `.pem` en un objeto `RSAPublicKey` sin que escribas una línea de parseo.

### El login

```
@PostMapping("/login")
public TokenResponse login(Authentication authentication) {

    JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer("security-jwt")
            .issuedAt(ahora)
            .expiresAt(ahora.plusSeconds(ttlSeconds))
            .subject(authentication.getName())
            .claim("roles", roles)
            .build();

    return new TokenResponse(jwtEncoder.encode(...).getTokenValue(), "Bearer", ttlSeconds, ...);
}
```

> NOTA: **Fíjate en lo que este método NO recibe.** No hay `@RequestBody` con usuario y contraseña, ni comparación de hashes, ni consulta SQL. Cuando este código se ejecuta, la cadena 1 *ya* validó el HTTP Basic contra la base de datos, y Spring nos entrega el resultado en el parámetro `Authentication`. Si las credenciales fueran malas, la ejecución nunca habría llegado aquí. Es la cadena de filtros del proyecto 01, reutilizada tal cual.

## 06 Probarlo

```
# 1. la taquilla: la ÚNICA vez que viaja la contraseña
$ TOKEN=$(curl -s -u john:test123 -X POST http://localhost:8072/api/auth/login \
        | python3 -c "import json,sys;print(json.load(sys.stdin)['accessToken'])")

# 2. el torniquete: a partir de aquí, solo el token
$ curl -H "Authorization: Bearer $TOKEN" http://localhost:8072/api/employees
[{"firstName":"Patrobas","lastName":"Filologo","email":"patrobas@gmail.com","id":1}, ...]

# 3. john sigue siendo EMPLOYEE: los roles viajan DENTRO del token
$ curl -H "Authorization: Bearer $TOKEN" -X DELETE http://localhost:8072/api/employees/1
{"status":403,"error":"Forbidden"}

# 4. y esto ya NO funciona — esta es la lección del proyecto
$ curl -u susan:test123 http://localhost:8072/api/employees
HTTP 401
```

> ATENCION: **Trampa de zsh (el shell por defecto del Mac).** Si escribes `curl -u "$USUARIO:test123"` con una variable, zsh interpreta el `:t` como un modificador de historia y te manda `johnest123` como nombre de usuario. Vas a ver un `401` desconcertante y una petición de contraseña interactiva. La forma correcta es con llaves: `"${USUARIO}:test123"`.

### La matriz completa

El script `scripts/test-endpoints.sh` corre 11 comprobaciones, incluida la de manipular el token:

```
$ ./scripts/test-endpoints.sh
OK  GET con token de john                            -> HTTP 200  (esperado 200)
OK  POST con token de john (EMPLOYEE)                -> HTTP 403  (esperado 403)
OK  HTTP Basic contra /api/employees                 -> HTTP 401  (esperado 401)
OK  firma alterada (1 caracter del medio)            -> HTTP 401  (esperado 401)
OK  payload reescrito a ROLE_ADMIN                   -> HTTP 401  (esperado 401)
OK  DELETE con token de susan (ADMIN)                -> HTTP 200  (esperado 200)
```

## 07 Romperlo

### Ascenderse a ADMIN

El payload se lee sin llaves… así que reescríbelo. Cambia `ROLE_EMPLOYEE` por `ROLE_ADMIN`, vuelve a codificarlo en base64, pégale la firma original y mándalo:

```
$ curl -X DELETE -H "Authorization: Bearer $TOKEN_FALSIFICADO" .../api/employees/1
HTTP 401  invalid_token
```

La firma se calculó sobre el payload *original*. Al cambiar una letra, deja de corresponder — y sin la llave privada no puedes recalcularla. **Ese es el valor entero de un JWT: es de lectura pública y de escritura imposible.**

> ATENCION: **La demo que sale mal en clase.** Casi todos los tutoriales dicen “cambia un carácter del token y verás que falla”. Si cambias el **último**, hay una posibilidad real de que *siga funcionando*: la firma son 2048 bits pero ocupa 342 caracteres base64, que dan para 2052 — al último carácter le sobran 4 bits. Medido en este proyecto: **15 de los 63 caracteres posibles (~24%) producen exactamente la misma firma**. No es un fallo de seguridad, son bits que no se usan. Cambia siempre un carácter *del medio*.

### Robar el token

Un token robado funciona. No hay contraseña que adivinar: quien lo tenga, *es* john hasta que el token caduque. De ahí salen las tres reglas prácticas:

- **HTTPS igual que en Basic.** El token viaja en claro en la cabecera; interceptarlo es tan fácil como interceptar la contraseña.

- **Vida corta.** Minutos, no días. La caducidad es tu única defensa automática.

- **No lo guardes en `localStorage` si tu página tiene riesgo de XSS** — cualquier script inyectado lo lee. La alternativa es una cookie `HttpOnly`, que a cambio te devuelve el problema de CSRF. No hay opción gratis; hay que elegir con criterio.

## La caducidad y los 60 segundos

Para ver caducar un token en vivo, arranca con un TTL corto:

```
$ java -jar target/security-jwt-0.0.1-SNAPSHOT.jar --jwt.ttl-seconds=3
```

Y entonces pasa algo que descoloca a todo el mundo. Esto está medido en este proyecto, con un token de 3 segundos:

```
t = 0s    -> 200
t = 10s   -> 200  ← ¡caducó hace 7 segundos y sigue entrando!
t = 65s   -> 401  ← ahora sí
WWW-Authenticate: Bearer error="invalid_token",
  error_description="Jwt expired at 2026-08-21T21:54:13Z"
```

> NOTA: **No es un bug: es tolerancia de reloj.** `JwtTimestampValidator` acepta por defecto **60 segundos** de desfase, porque el servidor que firma y el que valida suelen ser máquinas distintas y sus relojes nunca están perfectamente sincronizados. Sin esa holgura, unos pocos segundos de deriva causarían rechazos aleatorios imposibles de depurar. Si quieres que caduque puntualmente, hay que configurar el validador a mano — es el ejercicio 4.

## Trampas de Spring Security 7

Tres cosas que este proyecto hace de una forma concreta, y que ningún tutorial de Boot 3 te va a explicar porque en Boot 3 no pasaban:

### 1. El rol fantasma FACTOR_PASSWORD

Spring Security 7 añade autoridades que describen *cómo* te autenticaste (soporte de multifactor). Si vuelcas `authentication.getAuthorities()` tal cual dentro del token, sale esto:

```
"roles": ["ROLE_EMPLOYEE", "FACTOR_PASSWORD"]
```

No rompe nada, pero es información interna del servidor filtrándose a un token que viaja al cliente. Por eso el `AuthController` filtra:

```
.filter(authority -> authority.startsWith("ROLE_"))
```

### 2. El prefijo SCOPE_

Por defecto Spring lee el claim `scope` y le antepone `SCOPE_`, con lo que `hasRole("EMPLOYEE")` dejaría de funcionar y tendrías `403` en todo. Como nuestro claim se llama `roles` y ya trae el prefijo `ROLE_`, se lo decimos explícitamente:

```
authoritiesConverter.setAuthoritiesClaimName("roles");
authoritiesConverter.setAuthorityPrefix("");
```

Gracias a esas dos líneas, las reglas de autorización del proyecto 01 funcionan aquí sin tocarlas. En el proyecto 03 volverás a encontrarte este mismo problema, porque Keycloak coloca los roles en otro sitio distinto.

### 3. El nombre del starter

Boot 4 renombró el starter a `spring-boot-starter-security-oauth2-resource-server`. El nombre viejo (`spring-boot-starter-oauth2-resource-server`) todavía existe, así que copiar de un tutorial de Boot 3 puede *parecer* que funciona. Usa el nuevo.

## Errores comunes

| Síntoma | Causa | Arreglo |
|---|---|---|
| El login pide contraseña interactivamente y devuelve 401 | zsh se comió parte del argumento: "$U:test123" aplica el modificador :t . | Escribe "${U}:test123" , con llaves. |
| 403 en todo, con un token que parece correcto | Los roles no llegaron como autoridades: falta el JwtAuthenticationConverter , o el claim se llama distinto. | Decodifica el payload y comprueba que el claim roles existe y trae ROLE_* . |
| 401 invalid_token justo después de reiniciar | Regeneraste las llaves: los tokens viejos se firmaron con la privada anterior. | Vuelve a hacer login. Es el comportamiento correcto. |
| El token caducado sigue entrando | Los 60 segundos de tolerancia de reloj. | Espera 60 s más, o configura el validador (ejercicio 4). |
| Cambié un carácter del token y sigue funcionando | Cambiaste el último : le sobran 4 bits (~24% de las sustituciones no cambian nada). | Cambia uno del medio de la firma. |
| Failed to load ApplicationContext mencionando RSAPrivateKey | Falta el archivo .pem , la ruta está mal, o la llave no es PKCS#8. | Debe empezar por -----BEGIN PRIVATE KEY----- . Si dice RSA PRIVATE KEY es PKCS#1: regenérala con openssl genpkey . |
| Cambié el código y no pasa nada | mvn spring-boot:run con devtools solo reinicia cuando cambia target/classes , y Maven no recompila solo. | Reinicia, o recompila desde el IDE. |

## Ejercicios

- [ ]**1. Lee tu propio token** Haz login como `susan` y decodifica el payload. ¿Cuántos roles trae? Compara con el de `john` y explica de dónde salió la diferencia.

- [ ]**2. Falsifica (y fracasa)** Reescribe el payload de john poniéndole `ROLE_ADMIN`, recodifícalo y úsalo para borrar un empleado. Anota el código y explica en una frase por qué no funcionó.

- [ ]**3. Rompe la firma bien** Cambia el último carácter del token y prueba. Ahora cambia uno del medio. Explica por qué los resultados pueden ser distintos.

- [ ]**4. Caducidad puntual** Haz que el token caduque exactamente en su `exp`, sin los 60 s de gracia. Pista: `NimbusJwtDecoder.setJwtValidator(...)` con un `JwtTimestampValidator(Duration.ofSeconds(0))`. Después argumenta si te parece buena idea en producción.

- [ ]**5. Revocación imposible** Haz login como susan, guarda el token, y BORRA a susan de la tabla `members`. Usa el token otra vez. ¿Funciona? Explica por qué, y propón dos formas de arreglarlo.

- [ ]**6. Compara los dos proyectos** Abre lado a lado el `SecurityConfig` del 01 y el del 02. Haz una lista de lo que cambió y otra de lo que no. La segunda lista es más larga: esa es la idea.

> NOTA: **Siguiente parada:** etapa 03 — OAuth2. El `AuthController`, las llaves RSA y hasta la tabla de usuarios van a *desaparecer*: los tokens los emitirá Keycloak y tu API se limitará a validarlos. El `oauth2ResourceServer(...)` que escribiste hoy se queda casi igual — solo cambia de dónde sale la llave pública.

Academia MTY · Seguridad y autenticación 02/03
Spring Boot 4.1.0 · Spring Security 7.1.0 · Java 21

---

## CÓDIGO DISTINTIVO DE ESTA ETAPA

El CRUD es idéntico al de las otras etapas (ver documento de setup y referencia).
Lo nuevo aquí: dos cadenas de filtros, el encoder/decoder RSA y el controlador de login.

### `02-security-jwt/src/main/java/com/luv2code/springboot/cruddemo/security/SecurityConfig.java`

```java
package com.luv2code.springboot.cruddemo.security;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

/**
 * Configuracion de seguridad con JWT.
 *
 * La gran diferencia con el proyecto 01: aqui hay DOS cadenas de filtros,
 * porque hay dos formas distintas de entrar a la aplicacion.
 *
 *   /api/auth/login  -> con usuario y contrasena (HTTP Basic). Se usa UNA vez.
 *   /api/employees   -> con el token que devolvio el login (Bearer).
 */
@Configuration
public class SecurityConfig {

    // Spring convierte solo los archivos .pem en objetos de llave RSA
    @Value("${rsa.public-key}")
    private RSAPublicKey publicKey;

    @Value("${rsa.private-key}")
    private RSAPrivateKey privateKey;

    /**
     * De donde salen los usuarios. IDENTICO al proyecto 01: las mismas tablas.
     * El login sigue necesitando comprobar usuario y contrasena contra la base de datos;
     * lo que cambia es que eso ya solo pasa UNA vez, no en cada peticion.
     */
    @Bean
    public UserDetailsService userDetailsService(DataSource theDataSource) {

        JdbcUserDetailsManager theUserDetailsManager = new JdbcUserDetailsManager(theDataSource);

        theUserDetailsManager.setUsersByUsernameQuery(
                "select user_id, pw, active from members where user_id=?");

        theUserDetailsManager.setAuthoritiesByUsernameQuery(
                "select user_id, role from roles where user_id=?");

        return theUserDetailsManager;
    }

    /**
     * CADENA 1 - solo para /api/auth/**  (la taquilla donde compras el boleto).
     *
     * securityMatcher dice "esta cadena solo aplica a estas rutas". Como tiene
     * @Order(1), Spring la revisa primero; si la ruta no encaja, prueba la siguiente.
     */
    @Bean
    @Order(1)
    public SecurityFilterChain loginFilterChain(HttpSecurity http) throws Exception {

        http.securityMatcher("/api/auth/**");
        http.authorizeHttpRequests(configurer -> configurer.anyRequest().authenticated());
        http.httpBasic(Customizer.withDefaults());
        http.csrf(csrf -> csrf.disable());
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    /**
     * CADENA 2 - todo lo demas (el torniquete donde se valida el boleto).
     *
     * Fijate en las reglas de autorizacion: son EXACTAMENTE las mismas del proyecto 01.
     * No cambia quien puede hacer que; cambia de donde salen los roles.
     */
    @Bean
    @Order(2)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(configurer -> configurer
                .requestMatchers(HttpMethod.GET,    "/api/employees").hasRole("EMPLOYEE")
                .requestMatchers(HttpMethod.GET,    "/api/employees/**").hasRole("EMPLOYEE")
                .requestMatchers(HttpMethod.POST,   "/api/employees").hasRole("MANAGER")
                .requestMatchers(HttpMethod.PUT,    "/api/employees").hasRole("MANAGER")
                .requestMatchers(HttpMethod.PATCH,  "/api/employees/**").hasRole("MANAGER")
                .requestMatchers(HttpMethod.DELETE, "/api/employees/**").hasRole("ADMIN")
                .anyRequest().authenticated());

        // Aqui esta el cambio de fondo: ya no se acepta HTTP Basic.
        // Esta cadena solo entiende "Authorization: Bearer <token>".
        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(
                jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));

        http.csrf(csrf -> csrf.disable());
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    /**
     * FIRMAR tokens. Usa la llave PRIVADA: solo este servidor puede emitir tokens validos.
     */
    @Bean
    public JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(publicKey).privateKey(privateKey).build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

    /**
     * VALIDAR tokens. Usa la llave PUBLICA. Cualquiera podria validar; nadie mas puede firmar.
     * Esa asimetria es todo el truco de la criptografia de llave publica.
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

    /**
     * Traduce los claims del token a roles de Spring.
     *
     * Por defecto Spring lee el claim "scope" y le pega el prefijo "SCOPE_", con lo que
     * hasRole("EMPLOYEE") dejaria de funcionar. Como nuestro token trae un claim "roles"
     * que YA dice "ROLE_EMPLOYEE", le decimos: lee "roles" y no le agregues nada.
     * Gracias a esto las reglas de la cadena 2 son identicas a las del proyecto 01.
     */
    private JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthoritiesClaimName("roles");
        authoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);

        return converter;
    }
}
```

### `02-security-jwt/src/main/java/com/luv2code/springboot/cruddemo/rest/AuthController.java`

```java
package com.luv2code.springboot.cruddemo.rest;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * La taquilla: cambia usuario y contrasena por un token.
 *
 * Este es el UNICO sitio de la aplicacion donde todavia viaja la contrasena.
 * A partir de aqui, el cliente solo manda el token.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /** Lo que devuelve el login. Un record: clase inmutable en una linea. */
    public record TokenResponse(String accessToken, String tokenType, long expiresIn, String user) {
    }

    private final JwtEncoder jwtEncoder;

    private final long ttlSeconds;

    public AuthController(JwtEncoder theJwtEncoder,
            @Value("${jwt.ttl-seconds}") long theTtlSeconds) {
        jwtEncoder = theJwtEncoder;
        ttlSeconds = theTtlSeconds;
    }

    /**
     * POST /api/auth/login
     *
     * No recibe un @RequestBody con usuario y contrasena: para cuando este metodo se
     * ejecuta, la cadena de filtros 1 YA valido el HTTP Basic contra la base de datos.
     * Spring nos inyecta el resultado en el parametro Authentication.
     * Si las credenciales fueran malas, nunca llegariamos hasta aqui (401).
     */
    @PostMapping("/login")
    public TokenResponse login(Authentication authentication) {

        Instant ahora = Instant.now();

        // Los roles que salieron de la tabla "roles": ROLE_EMPLOYEE, ROLE_MANAGER...
        //
        // El filtro startsWith("ROLE_") NO es decorativo. Spring Security 7 agrega por su
        // cuenta autoridades que describen COMO te autenticaste (FACTOR_PASSWORD, y otras
        // FACTOR_* si usas multifactor). Son utiles dentro del servidor, pero no tienen
        // nada que hacer dentro de un token que viaja al cliente. Sin este filtro, el
        // token saldria con "roles":["ROLE_EMPLOYEE","FACTOR_PASSWORD"].
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith("ROLE_"))
                .collect(Collectors.toList());

        // El PAYLOAD del token. Todo esto viaja en claro dentro del token:
        // va FIRMADO, no cifrado. Nunca pongas aqui datos secretos.
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("security-jwt")                        // quien lo emitio
                .issuedAt(ahora)                               // cuando  (claim "iat")
                .expiresAt(ahora.plusSeconds(ttlSeconds))      // hasta cuando (claim "exp")
                .subject(authentication.getName())             // de quien es (claim "sub")
                .claim("roles", roles)                         // que puede hacer
                .build();

        // firmar con RS256 = RSA + SHA-256, usando la llave privada
        JwsHeader header = JwsHeader.with(org.springframework.security.oauth2.jose.jws.SignatureAlgorithm.RS256).build();

        String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();

        return new TokenResponse(token, "Bearer", ttlSeconds, authentication.getName());
    }
}
```

### `02-security-jwt/src/main/resources/application.properties`

```properties
#
# JDBC properties (mismas tablas members/roles del proyecto 01)
#
spring.datasource.url=jdbc:mysql://localhost:3306/employee_directory
spring.datasource.username=springstudent
spring.datasource.password=springstudent

#
# Puerto de esta etapa (01 = 8071, 02 = 8072, 03 = 8073)
#
server.port=8072

#
# Las llaves RSA con las que se FIRMAN y se VALIDAN los tokens.
# La privada firma (solo el servidor la tiene). La publica valida (puede ser publica).
#
rsa.private-key=classpath:certs/private.pem
rsa.public-key=classpath:certs/public.pem

#
# Cuanto dura un token, en segundos. 3600 = 1 hora.
# Ponlo en 30 para ver caducar un token en vivo durante la clase.
#
jwt.ttl-seconds=3600

#logging.level.org.springframework.security=DEBUG
```

### `02-security-jwt/pom.xml`

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
	<artifactId>security-jwt</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>security-jwt</name>
	<description>Seguridad REST con JWT - Spring Security 7</description>
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
		<!-- Trae el soporte de JWT (firmar, validar, leer claims). En Spring Boot 4
		     este starter se llama ...-security-oauth2-...; en Boot 3 era sin "security". -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-security-oauth2-resource-server</artifactId>
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

### Llaves RSA

El proyecto incluye un par de llaves en `src/main/resources/certs/` (private.pem y
public.pem) para que la clase funcione sin pasos extra. No se reproducen aquí: no
aportan nada a la comprensión y una llave privada no debe circular. Se generan con:

```bash
openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -out private.pem
openssl rsa -pubout -in private.pem -out public.pem
```
