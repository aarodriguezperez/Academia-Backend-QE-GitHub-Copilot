# 2026-08-22 · Etapa 03 — OAuth2 y OIDC

Proyecto 03-security-oauth2, puerto 8073.

Tercera y última etapa. La API se convierte en Resource Server puro: deja de emitir
tokens, deja de tener llaves y deja de tener tabla de usuarios. Los tokens los emite
Keycloak (puerto 8090) y la API solo los valida vía JWKS.
Stack: Spring Boot 4.1.0, Spring Security 7.1.0, Java 21, Keycloak 26.4.

---

## GUÍA DE LABORATORIO
Seguridad 03 — OAuth2

-

-

-

Spring Boot 4.1 · Spring Security 7.1 · Guía de laboratorio · 03 de 03

# Delegar: OAuth2 y OIDC

En esta etapa tu API *pierde* cosas: se le va el controlador de login, las llaves RSA y hasta la tabla de usuarios. Y queda más segura que nunca. Los tokens los emite Keycloak; tu API se limita a validarlos, y esa línea divisoria es todo el tema.

- ~75 min

- Java 21

- Keycloak 26 en Docker

- localhost:8073

- Keycloak en :8090

## 00 Poner en marcha

> ATENCION: **Aquí el orden importa, y es la causa número uno de que la clase se atore.** Esta API descarga la configuración de Keycloak *al arrancar*. Si Keycloak no está listo todavía, la aplicación **no arranca** — no es que falle una petición: no levanta. Primero Keycloak, después la API.

- [ ]**Java 21 y Docker** `java -version` debe decir `21` o más, y `docker ps` debe responder sin errores.

- [ ]**Aquí no hacen falta las tablas de usuarios** — y esa ausencia es justo el tema de esta etapa.

```
# 1. Keycloak PRIMERO (solo la primera vez; tarda ~15 s en estar listo)
$ docker run --name keycloak-academy -p 8090:8080 \
    -e KC_BOOTSTRAP_ADMIN_USERNAME=admin \
    -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin \
    -d quay.io/keycloak/keycloak:26.4 start-dev

# las siguientes veces basta con:  docker start keycloak-academy

# 2. crear el realm, el client, los roles y los usuarios (solo la primera vez)
$ ./scripts/keycloak-setup.sh

# 3. comprobar que Keycloak responde ANTES de seguir
$ curl -s http://localhost:8090/realms/academy/.well-known/openid-configuration | head -c 80
{"issuer":"http://localhost:8090/realms/academy",...

# 4. la base de datos de empleados
$ docker start mysql-9.7

# 5. ahora sí, arrancar la API — escucha en el 8073
$ cd 03-security-oauth2
$ ./mvnw spring-boot:run
```

> NOTA: Keycloak trae consola web en http://localhost:8090 (usuario `admin`, contraseña `admin`). Vale la pena abrirla y pasearse: ahí vas a ver los usuarios, los roles y el client que creó el script del paso 2. Todo el detalle está en `17-seguridad-autenticacion/instalacion.txt`.

> ATENCION: **Si trabajas en Windows.** Los comandos de arriba son de macOS y Linux. En PowerShell: usa `mvnw.cmd spring-boot:run` en lugar de `./mvnw`, y si un comando ocupa varias líneas, la barra `\` del final se cambia por acento grave `` ` ``. Los scripts `.sh` de la carpeta `scripts/` necesitan **Git Bash** o **WSL**; en `instalacion.txt` están las versiones para PowerShell.

## 01 Lo que arrastra JWT propio

El proyecto 02 quedó bien: tokens firmados, caducidad, roles dentro del token. Pero tu API seguía siendo la dueña de algo incómodo: **las contraseñas de todo el mundo**. Y eso trae problemas que no se arreglan con código:

- **Cada API con su propia tabla de usuarios.** Cinco servicios, cinco tablas, cinco sitios donde se puede filtrar una contraseña, cinco procesos de “olvidé mi clave”.

- **No puedes delegar sin dar la llave completa.** Si una app de terceros quiere leer tus empleados, la única forma es darle usuario y contraseña. Con eso puede leer… y borrar.

- **El SSO es imposible.** Entrar una vez y quedar dentro de todas las aplicaciones requiere que alguien centralice la identidad.

- **Nada de multifactor.** ¿Vas a implementar tú los códigos por SMS, las apps de autenticación y las llaves de seguridad?

> NOTA: **La idea de OAuth2 en una frase:** saca la identidad de tu aplicación y ponla en un servicio dedicado. Tu API deja de preguntar “¿cuál es tu contraseña?” y pasa a preguntar “¿quién te avala?”. Es exactamente lo que ya haces cuando entras a un sitio con “Continuar con Google”: ese sitio nunca ve tu contraseña de Google.

## 02 Los cuatro actores

OAuth2 tiene un vocabulario propio que suena abstracto hasta que lo aterrizas. Piensa en un hotel:

| En el hotel | En OAuth2 | En este proyecto |
|---|---|---|
| El huésped | Resource Owner el dueño de los datos | john, mary, susan |
| La recepción | Authorization Server comprueba identidad y emite | Keycloak (:8090) |
| La tarjeta-llave | Access token la prueba, temporal y acotada | el JWT de Keycloak |
| La puerta de la habitación | Resource Server valida y deja pasar | tu API (:8073) |
| Quien quiere entrar | Client la app que pide en tu nombre | curl, o una app web |

Y ahora el detalle que hace clic: **la puerta de tu habitación no llama a recepción cada vez que metes la tarjeta.** La puerta sabe reconocer una tarjeta legítima por sí sola. Eso es exactamente lo que hace tu API con la firma del token — y es la razón por la que un Resource Server escala sin convertir al Authorization Server en un cuello de botella.

> ATENCION: **Fíjate en quién NO aparece nunca.** En todo el flujo, la contraseña de john jamás toca tu API. Solo la ve Keycloak. Si mañana te hackean la API, no hay contraseñas que robar — porque no las tienes.

> **Imagen:** Dos edificios separados. El de la izquierda, rotulado KEYCLOAK, es una recepción de hotel: john entrega por el mostrador una tarjeta con un candado y una contraseña oculta, y una flecha roja muestra que entra y se queda ahí. Detrás de la recepcionista hay una estantería grande llena de cajas de fichas: ahí viven las identidades. De la recepción sale una tarjeta llave con el nombre john, el rol EMPLOYEE y un sello dorado. El de la derecha, rotulado YOUR API, es una habitación: john acerca la tarjeta a un lector y una flecha verde sube desde el lector hasta un tablón público colgado en la pared que muestra ese mismo sello dorado, así que la puerta decide sola. Detrás de la puerta abierta solo hay una caja fuerte verde con fichas de empleados: ninguna estantería de personas. Abajo a la derecha, en un recuadro aparte, una tarjeta roja a nombre de maria con un sello distinto es rechazada por el lector con una equis.

Fig. 1 — La contraseña entra en Keycloak y no sale de ahí; las fichas de personas están en el edificio izquierdo y en el derecho no hay ninguna. La puerta valida contra el sello publicado en el tablón (el JWKS), sin preguntarle nada a recepción. Fíjate en maria: **mismo rol que john** y aun así no entra — lo que falla no es el permiso, es el emisor.

## 03 El flujo de verdad

El flujo estándar se llama **Authorization Code + PKCE**. Es el que ocurre cuando pulsas “Continuar con Google”:

> **Diagrama:** Client (la app) · Keycloak · tu API :8073 · 1 · te mando al login (con un reto PKCE) · 2 · john teclea su contraseña AQUÍ · 3 · vuelve un código de un solo uso · 4 · cambio el código por el token · 5 · access token (+ refresh token) · 6 · Authorization: Bearer  · 7 · valida la firma con el JWKS · sin preguntarle nada a Keycloak

Fig. 2 — Los siete pasos del flujo real. La contraseña solo aparece en el paso 2, y solo dentro de Keycloak.

**¿Por qué un código intermedio y no el token directo?** Porque el paso 3 viaja por el navegador (queda en el historial, en los logs, en la barra de direcciones). Un código de un solo uso y vida de segundos no sirve de nada si se filtra. El token va por el paso 4, que es una llamada directa de servidor a servidor. **PKCE** añade que quien canjea el código demuestre ser el mismo que lo pidió.

> ATENCION: **Y entonces, ¿por qué en clase usamos `grant_type=password`?** Porque el flujo de arriba necesita un navegador y no se puede hacer con `curl` en una línea. Ese atajo (*Direct Access Grant*) está **desaconsejado en OAuth 2.1** justamente porque devuelve la contraseña al client — lo que OAuth vino a evitar. Lo habilitamos solo para practicar desde la terminal. En una aplicación real: Authorization Code + PKCE, siempre.

## 04 OAuth2 no es OIDC

Esta es la confusión número uno del tema, y se resuelve con una frase: **OAuth2 es autorización; OpenID Connect es autenticación**.

|  | OAuth2 | OpenID Connect (OIDC) |
|---|---|---|
| Responde | ¿Qué puede hacer el portador? | ¿Quién es esta persona? |
| Te da | access_token | además un id_token |
| Para | llamar a una API | saber el nombre, el correo, la foto |
| Analogía | la tarjeta-llave | la credencial con tu foto |

OIDC es una *capa encima* de OAuth2, no un competidor. Keycloak habla los dos, y por eso la URL de descubrimiento se llama `/.well-known/**openid-configuration**`. Nuestra API solo usa la parte OAuth2: recibe un `access_token` y comprueba permisos. El `id_token` lo usaría el frontend para pintar “Hola, john”.

> NOTA: **Regla para no equivocarse:** si vas a mandar el token a una API, es un `access_token` y estás haciendo OAuth2. Si lo abres para leer quién es el usuario y saludarlo, es un `id_token` y estás haciendo OIDC. Mandar un `id_token` a una API es un error clásico.

## 05 El código que desaparece

Compara el `SecurityConfig` del proyecto 02 con el de este. La lista interesante es la de lo que **ya no está**:

| En el proyecto 02 | Aquí | Por qué |
|---|---|---|
| UserDetailsService | eliminado | los usuarios viven en Keycloak |
| JwtEncoder | eliminado | esta API ya no emite tokens |
| JwtDecoder | eliminado | lo construye Spring desde el issuer-uri |
| private.pem / public.pem | eliminados | la llave se descarga del JWKS |
| AuthController | eliminado | el login pasa en Keycloak |
| tablas members y roles | sin usar | la identidad ya no es asunto de esta API |

Y lo que entra a cambio es **una línea de configuración**:

```
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8090/realms/academy
```

Con eso, al arrancar, Spring va solo a esa URL, lee `/.well-known/openid-configuration`, saca de ahí la dirección del `jwks_uri`, y de ahí descarga la llave pública. Cero criptografía escrita a mano:

```
$ curl -s http://localhost:8090/realms/academy/.well-known/openid-configuration
  issuer:         http://localhost:8090/realms/academy
  token_endpoint: http://localhost:8090/realms/academy/protocol/openid-connect/token
  jwks_uri:       http://localhost:8090/realms/academy/protocol/openid-connect/certs

$ curl -s http://localhost:8090/realms/academy/protocol/openid-connect/certs
  kid=USoiHqQNllGxYhCC... alg=RS256 use=sig kty=RSA
```

> NOTA: **El `kid` es la pieza que faltaba.** El header del token de Keycloak trae `"kid"` (key id); el JWKS publica varias llaves, cada una con el suyo. Así el emisor puede *rotar* sus llaves sin romper nada: publica la nueva, los tokens nuevos apuntan a ella, y los viejos siguen validándose con la anterior hasta caducar. Tu API se entera sola. Ese problema, en el proyecto 02, lo tenías que resolver a mano parando el servidor.

### La única traducción necesaria

Keycloak no pone los roles donde Spring los busca. Los mete anidados y sin prefijo:

```
"realm_access": { "roles": ["default-roles-academy", "EMPLOYEE", "offline_access", "uma_authorization"] }
```

```
private static Collection<GrantedAuthority> extraerRoles(Jwt jwt) {
    Map<String, Object> realmAccess = jwt.getClaim("realm_access");
    if (realmAccess == null || realmAccess.get("roles") == null) return List.of();
    List<String> roles = (List<String>) realmAccess.get("roles");
    return roles.stream()
            .map(role -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + role))
            .toList();
}
```

Baja dos niveles y añade el prefijo `ROLE_`. Gracias a esas seis líneas, las reglas de autorización del proyecto siguen siendo **exactamente las mismas de los proyectos 01 y 02**: `hasRole("EMPLOYEE")`, `hasRole("ADMIN")`. Tres formas distintas de autenticar, un único control de acceso.

## 06 Probarlo

```
# 1. el token se le pide A KEYCLOAK, no a tu API
$ TOKEN=$(curl -s -X POST http://localhost:8090/realms/academy/protocol/openid-connect/token \
        -d grant_type=password -d client_id=employee-api \
        -d username=john -d password=test123 \
        | python3 -c "import json,sys;print(json.load(sys.stdin)['access_token'])")

# 2. y se usa contra tu API, que nunca vio esa contraseña
$ curl -H "Authorization: Bearer $TOKEN" http://localhost:8073/api/employees
[{"firstName":"Patrobas","lastName":"Filologo", ...}]
```

El token de Keycloak medido en este proyecto: **1367 caracteres**, frente a los ~490 del proyecto 02. Y hay diferencias de fondo en el payload:

| Claim | Proyecto 02 | Keycloak |
|---|---|---|
| iss | security-jwt | http://localhost:8090/realms/academy |
| sub | john | 19c4dfd4-ac82-... (un UUID) |
| usuario legible | el propio sub | preferred_username |
| roles | roles (plano) | realm_access.roles (anidado) |
| vida | 3600 s | 300 s + refresh de 1800 s |
| total de claims | 5 | 20 |

> ATENCION: **El `sub` es un UUID, no el nombre.** Es a propósito: john puede cambiar de nombre de usuario o de correo, pero su identificador interno no cambia nunca. Si guardas en tu base de datos “de quién es este registro”, guarda el `sub` — nunca el `preferred_username`.

### La matriz completa

El script `scripts/test-endpoints.sh` corre las ocho comprobaciones, incluida la del token del emisor equivocado:

```
$ ./scripts/test-endpoints.sh
OK  token de john (EMPLOYEE) GET                     -> HTTP 200  (esperado 200)
OK  token de john POST                               -> HTTP 403  (esperado 403)
OK  curl -u susan:test123                            -> HTTP 401  (esperado 401)
OK  DELETE con token de mary (MANAGER)               -> HTTP 403  (esperado 403)
OK  DELETE con token de susan (ADMIN)                -> HTTP 200  (esperado 200)
OK  token emitido por el proyecto 02                 -> HTTP 401  (esperado 401)
```

## 07 Dónde vive la confianza

Arranca el proyecto 02 en el 8072 y el 03 en el 8073 a la vez. Pide un token al 02 — es un JWT impecable: bien formado, firmado con RSA, sin caducar, con `ROLE_EMPLOYEE` dentro. Mándalo al 03:

```
$ curl -H "Authorization: Bearer $TOKEN_DEL_02" http://localhost:8073/api/employees
HTTP 401
WWW-Authenticate: Bearer error="invalid_token",
  error_description="Signed JWT rejected: Invalid signature"
```

Rechazado. Y aquí está la idea con la que hay que cerrar el tema:

> NOTA: **La confianza no está en “es un JWT”. Está en *quién lo firmó*.** Tu API no confía en tokens: confía en **un emisor**, el del `issuer-uri`, y en la llave pública que ese emisor publica. Cualquier token de cualquier otro origen es basura para ella, por perfecto que sea su formato. Ese es el motivo por el que un atacante no gana nada montando su propio servidor de tokens.

### Lo que ganaste, en concreto

- **Tu API no guarda contraseñas.** Si te la comprometen, no hay credenciales que filtrar.

- **El multifactor es gratis.** Se activa en Keycloak; tu código no cambia ni una línea.

- **SSO real.** Diez APIs apuntando al mismo `issuer-uri`: un solo login para todas.

- **Rotación de llaves automática.** Keycloak publica la nueva, tus APIs se enteran solas por el JWKS.

- **Revocación de verdad.** Desactiva a john en Keycloak y su *refresh* deja de funcionar; su access token muere en 5 minutos, no en una hora.

> ATENCION: **Y lo que cuesta:** una pieza más de infraestructura que hay que levantar, actualizar y respaldar; y un punto único de fallo — si Keycloak se cae, nadie entra a ninguna aplicación. OAuth2 no es “la versión buena” de las otras dos etapas: es la que resuelve el problema de *varias* aplicaciones. Para una API interna con tres usuarios, Basic con HTTPS sigue siendo una respuesta correcta.

## Las tres etapas, una al lado de la otra

|  | 01 · Basic | 02 · JWT | 03 · OAuth2 |
|---|---|---|---|
| Qué viaja | usuario:contraseña en cada petición | token propio | token de un tercero |
| Quién emite | nadie | tu API | Keycloak |
| Dónde viven los usuarios | tabla members | tabla members | Keycloak |
| Consulta a la BD por petición | sí (+ BCrypt) | no | no |
| Caduca | nunca | exp del token | 300 s + refresh |
| Se puede revocar | cambiando la contraseña | no, hasta que caduque | sí, en Keycloak |
| Multifactor | no | lo escribes tú | configuración |
| Sirve para SSO | no | no | sí |
| Complejidad | 1 clase | 2 clases + llaves | 1 clase + Keycloak |
| Cuándo usarlo | scripts internos, APIs pequeñas, siempre con HTTPS | una API con su propio frontend | varias apps, usuarios externos, empresa |

> NOTA: **Lo que no cambió en ninguna de las tres:** las reglas de autorización. `hasRole("ADMIN")` para borrar, `hasRole("MANAGER")` para crear. Abre los tres `SecurityConfig` uno al lado del otro y compruébalo — esa es la moraleja del tema completo. **Autenticar y autorizar son problemas separados, y solo el primero cambió tres veces.**

## Trampas de Keycloak

### 1. Account is not fully set up

Creas el usuario, le pones contraseña, pides el token y Keycloak responde `invalid_grant` con ese mensaje. La causa: la contraseña quedó marcada como *temporal*, así que Keycloak exige cambiarla en el primer login — algo imposible por `curl`. La solución está en el script:

```
kcadm.sh set-password -r academy --username john --new-password test123 --temporary=false
```

### 2. Roles que tú no pusiste

El token de john trae cuatro roles, y tú solo le diste uno:

```
"roles": ["default-roles-academy", "EMPLOYEE", "offline_access", "uma_authorization"]
```

Los otros tres se los pone Keycloak. Se convierten en `ROLE_offline_access` y compañía, y ahí se quedan sin estorbar — pero explican por qué la lista de autoridades es más larga de lo que esperas al depurar.

### 3. El issuer-uri tiene que coincidir exactamente

Spring compara el claim `iss` del token contra tu `issuer-uri` carácter por carácter. `localhost` y `127.0.0.1` son cosas distintas; una barra final de más, también. Si no coinciden: `401`.

### 4. Keycloak tiene que estar arriba cuando arranca tu API

Spring descarga la configuración del emisor *al arrancar*. Si Keycloak no responde todavía, la aplicación falla al iniciar. Arranca primero el contenedor y espera esos ~15 segundos.

## Errores comunes

| Síntoma | Causa | Arreglo |
|---|---|---|
| La API no arranca: error conectando al issuer-uri | Keycloak apagado o todavía arrancando. | docker start keycloak-academy y espera ~15 s. |
| invalid_grant : Account is not fully set up | Contraseña marcada como temporal. | set-password ... --temporary=false |
| 401 Invalid signature | El token lo emitió otro servidor (o borraste y recreaste el realm, que regenera las llaves). | Pide un token nuevo al emisor correcto. |
| 401 The iss claim is not valid | El issuer-uri no coincide exacto con el claim iss . | Compara los dos textos carácter por carácter, barra final incluida. |
| 403 en todo con un token válido | Los roles no se extrajeron: falta el converter de realm_access , o al usuario no le asignaste el rol. | Decodifica el payload y mira qué hay en realm_access.roles . |
| 401 a los 5 minutos, sin haber tocado nada | El token de Keycloak dura 300 s por defecto. | Pide otro, o usa el refresh_token . Es el comportamiento correcto. |
| unauthorized_client al pedir el token | El client no tiene habilitado Direct Access Grants . | Actívalo (lo hace keycloak-setup.sh ) o usa el flujo con navegador. |

## Ejercicios

- [ ]**1. Sigue el rastro de la llave** Partiendo solo del `issuer-uri`, llega con `curl` hasta la llave pública. Después compara el `kid` del header de un token con los del JWKS: ¿cuál de las dos llaves lo validó, y cómo lo sabes?

- [ ]**2. Asciende a john** Dale `MANAGER` en la consola de Keycloak (`http://localhost:8090`). Prueba un POST con su token *viejo*: ¿funciona? Pide un token nuevo y vuelve a probar. Explica la diferencia.

- [ ]**3. Desactívalo** Desactiva a susan en Keycloak y usa su token ya emitido. ¿Sigue entrando? Explícalo con lo que aprendiste en el proyecto 02 sobre la revocación.

- [ ]**4. El flujo de verdad** Abre en el navegador la URL de `authorization_endpoint` con `client_id=employee-api`, `response_type=code` y `redirect_uri=http://localhost:8073/`. Entra como john y mira la barra de direcciones: ahí está el `code` del paso 3 de la Fig. 2.

- [ ]**5. Rompe el issuer** Cambia `localhost` por `127.0.0.1` en el `issuer-uri`, reinicia y prueba. Anota el mensaje exacto del error: vas a reconocerlo el resto de tu carrera.

- [ ]**6. Cierra el tema** Abre los tres `SecurityConfig` a la vez. En una tabla de dos columnas escribe qué cambió y qué no. Después responde en tres renglones: para una API interna del equipo, con cinco usuarios, ¿cuál de las tres etapas elegirías y por qué?

Academia MTY · Seguridad y autenticación 03/03
Spring Boot 4.1.0 · Spring Security 7.1.0 · Keycloak 26.4

---

## CÓDIGO DISTINTIVO DE ESTA ETAPA

El CRUD es idéntico al de las otras etapas (ver documento de setup y referencia).
Lo característico aquí es lo que DESAPARECIÓ respecto a la etapa 02: no hay
UserDetailsService, ni JwtEncoder, ni JwtDecoder, ni llaves, ni AuthController.

### `03-security-oauth2/src/main/java/com/luv2code/springboot/cruddemo/security/SecurityConfig.java`

```java
package com.luv2code.springboot.cruddemo.security;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuracion de seguridad con OAuth2. Compara este archivo con el del proyecto 02
 * y fijate en todo lo que DESAPARECIO:
 *
 *   - no hay UserDetailsService  -> los usuarios ya no viven aqui, viven en Keycloak
 *   - no hay JwtEncoder          -> esta API ya no emite tokens
 *   - no hay JwtDecoder          -> la llave publica se descarga sola del emisor
 *   - no hay llaves RSA          -> no hay nada que guardar ni que rotar
 *   - no hay AuthController      -> el login pasa en Keycloak, no aqui
 *
 * Esta API se volvio un RESOURCE SERVER puro: solo sabe validar tokens ajenos.
 */
@Configuration
public class SecurityConfig {

    /**
     * Las reglas de autorizacion: IDENTICAS a las de los proyectos 01 y 02.
     * Tres formas distintas de autenticar, exactamente el mismo control de acceso.
     */
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

        // Toda la validacion de tokens cabe aqui. La llave publica la descarga Spring
        // solo, desde el issuer-uri de application.properties.
        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(
                jwt -> jwt.jwtAuthenticationConverter(keycloakConverter())));

        http.csrf(csrf -> csrf.disable());
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    /**
     * Keycloak no pone los roles donde Spring los busca por defecto.
     *
     * Spring espera un claim plano (por defecto "scope"). Keycloak los mete ANIDADOS,
     * dentro de "realm_access", y sin el prefijo ROLE_:
     *
     *   "realm_access": { "roles": ["EMPLOYEE", "MANAGER", "default-roles-academy"] }
     *
     * Asi que hay que bajar dos niveles y agregar el prefijo nosotros. Este metodo es
     * la unica "traduccion" que necesita la aplicacion, y es la razon por la que las
     * reglas de arriba pudieron quedarse idenricas a las del proyecto 01.
     */
    private Converter<Jwt, AbstractAuthenticationToken> keycloakConverter() {

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(SecurityConfig::extraerRoles);

        return converter;
    }

    @SuppressWarnings("unchecked")
    private static Collection<GrantedAuthority> extraerRoles(Jwt jwt) {

        Map<String, Object> realmAccess = jwt.getClaim("realm_access");

        if (realmAccess == null || realmAccess.get("roles") == null) {
            return List.of();
        }

        List<String> roles = (List<String>) realmAccess.get("roles");

        return roles.stream()
                .map(role -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + role))
                .toList();
    }
}
```

### `03-security-oauth2/src/main/resources/application.properties`

```properties
#
# JDBC properties
#
# Fijate en lo que YA NO esta aqui: esta base de datos solo guarda EMPLEADOS.
# Las tablas members y roles ya no se usan: los usuarios viven en Keycloak.
#
spring.datasource.url=jdbc:mysql://localhost:3306/employee_directory
spring.datasource.username=springstudent
spring.datasource.password=springstudent

#
# Puerto de esta etapa (01 = 8071, 02 = 8072, 03 = 8073)
#
server.port=8073

#
# LA LINEA MAS IMPORTANTE DE TODO EL PROYECTO.
#
# Con esto le decimos a Spring: "los tokens los emite ese servidor de alla".
# Spring va solo a esa URL, descarga la configuracion (/.well-known/openid-configuration),
# de ahi saca la direccion del JWKS y de ahi baja la llave publica.
# No hay que escribir NADA de criptografia: ni llaves, ni encoder, ni decoder.
#
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8090/realms/academy

#logging.level.org.springframework.security=DEBUG
```

### `03-security-oauth2/pom.xml`

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
	<artifactId>security-oauth2</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>security-oauth2</name>
	<description>Seguridad REST con OAuth2 y Keycloak - Spring Security 7</description>
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

## CONFIGURACIÓN DE KEYCLOAK

Script re-ejecutable que crea el realm academy, el client employee-api, los roles
EMPLOYEE/MANAGER/ADMIN y los tres usuarios. Verificado desde cero y en segunda pasada.

### `03-security-oauth2/scripts/keycloak-setup.sh`

```bash
#!/bin/bash
# Configura desde cero el realm "academy" en Keycloak.
# Es re-ejecutable: si algo ya existe, Keycloak lo dira y el script sigue.
#
# Requisito: el contenedor keycloak-academy corriendo (ver instalacion.txt).

set -u
kc() { docker exec keycloak-academy /opt/keycloak/bin/kcadm.sh "$@"; }

echo "→ esperando a que Keycloak responda..."
for i in $(seq 1 90); do
  [ "$(curl -s -o /dev/null -w '%{http_code}' -m 2 http://localhost:8090/realms/master)" = "200" ] && break
  sleep 1
done

echo "→ autenticando como admin"
kc config credentials --server http://localhost:8080 --realm master --user admin --password admin

echo "→ realm academy"
kc create realms -s realm=academy -s enabled=true

echo "→ client employee-api"
# publicClient           = sin secreto (es un cliente que no puede guardar secretos)
# directAccessGrantsEnabled = permite pedir el token con usuario y password desde curl.
#                          Se habilita SOLO para poder practicar en la terminal.
#                          El flujo de verdad es Authorization Code + PKCE, con navegador.
kc create clients -r academy -s clientId=employee-api -s enabled=true \
  -s publicClient=true -s directAccessGrantsEnabled=true -s standardFlowEnabled=true \
  -s 'redirectUris=["http://localhost:8073/*"]'

echo "→ roles"
for r in EMPLOYEE MANAGER ADMIN; do kc create roles -r academy -s name=$r; done

echo "→ usuarios"
for u in john mary susan; do
  kc create users -r academy -s username=$u -s enabled=true \
     -s "email=${u}@academy.mx" -s "firstName=${u}" -s lastName=Demo -s emailVerified=true
  # --temporary=false es OBLIGATORIO. Sin eso Keycloak marca la password como
  # "hay que cambiarla al entrar" y el login por curl falla con un mensaje
  # que no ayuda nada: "Account is not fully set up".
  kc set-password -r academy --username $u --new-password test123 --temporary=false
done

echo "→ asignando roles"
kc add-roles -r academy --uusername john  --rolename EMPLOYEE
kc add-roles -r academy --uusername mary  --rolename EMPLOYEE --rolename MANAGER
kc add-roles -r academy --uusername susan --rolename EMPLOYEE --rolename MANAGER --rolename ADMIN

echo
echo "Listo. Comprueba:"
echo "  curl -s http://localhost:8090/realms/academy/.well-known/openid-configuration | head -c 200"
echo "  Consola web: http://localhost:8090  (admin / admin)"
```
