#!/bin/bash
# Regenera los 4 documentos .md que se suben como fuentes a Gemini Notebook.
# Se ejecuta desde cualquier sitio; trabaja sobre la carpeta del tema.
#
#   ./generar.sh
#
# Despues hay que volver a subirlos al notebook (ver README.md).

set -eu
AQUI="$(cd "$(dirname "$0")" && pwd)"
TEMA="$(dirname "$AQUI")"
cd "$TEMA"

# Gemini Notebook toma el nombre de la fuente de la PRIMERA LINEA del texto
# pegado, y la barra lateral lo corta a 50 caracteres sin tooltip. Por eso la
# fecha va delante: es lo unico que distingue una version de otra de un vistazo,
# y cualquier sufijo al final seria invisible.
# Efecto colateral buscado: regenerar en otro dia cambia el H1, asi que el
# documento deja constancia de cuando se construyo.
HOY="$(date +%F)"

md() { python3 "$AQUI/html2md.py" "$1"; }
# printf garantiza el salto final: el pom.xml no lo trae y la marca de cierre
# se pegaba a </project>, dejando el bloque sin cerrar.
vuelca() { echo; echo "### \`$1\`"; echo; echo '```'"${2:-}"; printf '%s\n' "$(cat "$1")"; echo '```'; }

# ---------------------------------------------------------------- 00 referencia
{
echo "# $HOY · Setup y referencia"
cat <<'EOF'

Seguridad y autenticación en Spring Boot. Documento de referencia del tema "Seguridad y autenticación" del curso Academy MTY.
Cubre la instalación del entorno, el código común a las tres etapas, los scripts de
prueba y la comparativa final entre HTTP Basic, JWT y OAuth2.

**Stack:** Spring Boot 4.1.0 · Spring Security 7.1.0 · Java 21 · MySQL 9.7 · Keycloak 26.4

## El tema en una frase

La misma API REST de empleados, protegida de tres formas distintas. Las reglas de
autorización son IDÉNTICAS en las tres etapas; lo único que cambia es cómo se autentica
y quién emite la prueba de identidad.

| | 01 · Basic | 02 · JWT | 03 · OAuth2 |
|---|---|---|---|
| Puerto | 8071 | 8072 | 8073 |
| Qué viaja | usuario:contraseña en cada petición | token propio | token de un tercero |
| Quién emite | nadie | la propia API | Keycloak (8090) |
| Dónde viven los usuarios | tabla members | tabla members | Keycloak |
| Consulta a la BD por petición | sí (+ BCrypt) | no | no |
| Caduca | nunca | exp del token (3600 s) | 300 s + refresh |
| Se puede revocar | cambiando la contraseña | no, hasta que caduque | sí, en Keycloak |
| Multifactor | no | lo escribes tú | configuración |
| Sirve para SSO | no | no | sí |
| Cuándo usarlo | scripts internos, APIs pequeñas, siempre con HTTPS | una API con su propio frontend | varias apps, usuarios externos, empresa |

## Los tres usuarios de práctica

Los tres comparten la contraseña `test123`. En las etapas 01 y 02 viven en MySQL
(tablas `members` y `roles`); en la etapa 03 viven en Keycloak.

| Usuario | Roles | Puede |
|---|---|---|
| john | EMPLOYEE | leer |
| mary | EMPLOYEE, MANAGER | leer, crear, modificar |
| susan | EMPLOYEE, MANAGER, ADMIN | todo, incluido borrar |

## Matriz de autorización (idéntica en las tres etapas)

| Operación | Rol necesario | john | mary | susan |
|---|---|---|---|---|
| GET /api/employees | EMPLOYEE | 200 | 200 | 200 |
| POST /api/employees | MANAGER | 403 | 200 | 200 |
| PUT /api/employees | MANAGER | 403 | 200 | 200 |
| PATCH /api/employees/{id} | MANAGER | 403 | 200 | 200 |
| DELETE /api/employees/{id} | ADMIN | 403 | 403 | 200 |

`401` = no sé quién eres (fallo de autenticación). `403` = sé quién eres y no te toca
(fallo de autorización).

## Seis comportamientos de Spring Boot 4 / Security 7 verificados en este proyecto

Ninguno aparece en los tutoriales, porque todos son de Boot 3 / Security 6:

1. **FACTOR_PASSWORD**: Security 7 agrega autoridades que describen cómo te autenticaste
   (soporte multifactor). Sin filtrar por `startsWith("ROLE_")` se cuelan dentro del JWT
   emitido: `"roles":["ROLE_EMPLOYEE","FACTOR_PASSWORD"]`.
2. **Tolerancia de reloj de 60 segundos**: `JwtTimestampValidator` acepta 60 s de desfase
   por defecto. Un token con `ttl=3s` sigue entrando a los 10 s y solo falla a los 65 s.
3. **El último carácter base64 de una firma RS256 tiene 4 bits sobrantes**: la firma son
   2048 bits en 342 caracteres, que dan para 2052. Resultado medido: 15 de los 63
   caracteres alternativos producen la MISMA firma (~24%). La demo de "cambia un carácter
   y falla" hay que hacerla en un carácter del medio.
4. **Con CSRF encendido, POST devuelve 401 con cuerpo vacío, no 403**: el CsrfFilter corre
   antes de autenticar, así que el rechazo sale por el AuthenticationEntryPoint.
5. **Keycloak**: `set-password` sin `--temporary=false` deja la cuenta con una acción
   pendiente y el login por curl falla con `invalid_grant: Account is not fully set up`.
6. **zsh**: `"$var:test123"` aplica el modificador de historia `:t` y rompe `curl -u`.
   Hay que escribir `"${var}:test123"`.

## Diferencias entre Spring Boot 3 y Spring Boot 4 / Security 7

| Lo que dicen los tutoriales | Lo que va en este curso | Desde |
|---|---|---|
| extends WebSecurityConfigurerAdapter | un @Bean SecurityFilterChain | Security 5.7 |
| .authorizeRequests() | .authorizeHttpRequests() | Security 5.8 |
| .antMatchers(...) | .requestMatchers(...) | Security 6.0 |
| cadenas con .and() | lambdas: http.csrf(c -> c.disable()) | Security 6.1 |
| spring-boot-starter-web | spring-boot-starter-webmvc | Boot 4.0 |
| spring-boot-starter-oauth2-resource-server | spring-boot-starter-security-oauth2-resource-server | Boot 4.0 |

## Instalación del entorno
EOF
echo; echo '```'; cat instalacion.txt; echo '```'
echo; echo "## Script SQL de las tablas de usuarios (etapas 01 y 02)"
vuelca sql-scripts/01-security-tables.sql sql
cat <<'EOF'

## El CRUD base, común a las tres etapas

Estas seis clases son BYTE POR BYTE IDÉNTICAS en los tres proyectos: vienen del proyecto
16 y no se tocaron al añadir seguridad. Ese es justamente el argumento pedagógico del
tema: la seguridad entra por la cadena de filtros, no modificando el controlador.
Lo único que cambia entre etapas es `SecurityConfig` (y `AuthController` en la etapa 02).
EOF
for f in CruddemoApplication entity/Employee dao/EmployeeRepository service/EmployeeService service/EmployeeServiceImpl rest/EmployeeRestController; do
  vuelca "01-security-basic/src/main/java/com/luv2code/springboot/cruddemo/$f.java" java
done
echo; echo "## Scripts de prueba"
echo; echo "Cada etapa trae su matriz automatizada. Resultados verificados: 6 + 11 + 8 = 25 comprobaciones en verde."
vuelca 01-security-basic/scripts/test-endpoints.sh bash
vuelca 02-security-jwt/scripts/test-endpoints.sh bash
vuelca 03-security-oauth2/scripts/test-endpoints.sh bash
} > "$AQUI/00-setup-y-referencia.md"

# ---------------------------------------------------------------------- 01 basic
{
echo "# $HOY · Etapa 01 — HTTP Basic"
echo
echo "Proyecto 01-security-basic, puerto 8071."
echo
echo "Primera de las tres etapas del tema de seguridad. Protege con HTTP Basic la API REST"
echo "de empleados del proyecto 16, con usuarios en MySQL y contraseñas cifradas con BCrypt."
echo "Stack: Spring Boot 4.1.0, Spring Security 7.1.0, Java 21, MySQL 9.7."
echo; echo "---"; echo; echo "## GUÍA DE LABORATORIO"
md guias/guia-01-basic.html
echo; echo "---"; echo; echo "## CÓDIGO DISTINTIVO DE ESTA ETAPA"; echo
echo "El CRUD (Employee, EmployeeRestController, EmployeeService, EmployeeRepository) es"
echo "idéntico al de las otras dos etapas y está en el documento de setup y referencia."
echo "Aquí va solo lo que hace distinta a esta etapa."
vuelca 01-security-basic/src/main/java/com/luv2code/springboot/cruddemo/security/SecurityConfig.java java
vuelca 01-security-basic/src/main/resources/application.properties properties
vuelca 01-security-basic/pom.xml xml
} > "$AQUI/01-basic.md"

# ------------------------------------------------------------------------ 02 jwt
{
echo "# $HOY · Etapa 02 — JWT"
echo
echo "Proyecto 02-security-jwt, puerto 8072."
echo
echo "Segunda etapa. La contraseña deja de viajar en cada petición: se cambia una sola vez"
echo "por un token JWT firmado con RSA (RS256) y con fecha de caducidad. Usa las MISMAS"
echo "tablas members y roles de la etapa 01."
echo "Stack: Spring Boot 4.1.0, Spring Security 7.1.0, Java 21, MySQL 9.7, RSA 2048."
echo; echo "---"; echo; echo "## GUÍA DE LABORATORIO"
md guias/guia-02-jwt.html
echo; echo "---"; echo; echo "## CÓDIGO DISTINTIVO DE ESTA ETAPA"; echo
echo "El CRUD es idéntico al de las otras etapas (ver documento de setup y referencia)."
echo "Lo nuevo aquí: dos cadenas de filtros, el encoder/decoder RSA y el controlador de login."
vuelca 02-security-jwt/src/main/java/com/luv2code/springboot/cruddemo/security/SecurityConfig.java java
vuelca 02-security-jwt/src/main/java/com/luv2code/springboot/cruddemo/rest/AuthController.java java
vuelca 02-security-jwt/src/main/resources/application.properties properties
vuelca 02-security-jwt/pom.xml xml
echo; echo "### Llaves RSA"; echo
echo "El proyecto incluye un par de llaves en \`src/main/resources/certs/\` (private.pem y"
echo "public.pem) para que la clase funcione sin pasos extra. No se reproducen aquí: no"
echo "aportan nada a la comprensión y una llave privada no debe circular. Se generan con:"
echo; echo '```bash'
echo "openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -out private.pem"
echo "openssl rsa -pubout -in private.pem -out public.pem"
echo '```'
} > "$AQUI/02-jwt.md"

# --------------------------------------------------------------------- 03 oauth2
{
echo "# $HOY · Etapa 03 — OAuth2 y OIDC"
echo
echo "Proyecto 03-security-oauth2, puerto 8073."
echo
echo "Tercera y última etapa. La API se convierte en Resource Server puro: deja de emitir"
echo "tokens, deja de tener llaves y deja de tener tabla de usuarios. Los tokens los emite"
echo "Keycloak (puerto 8090) y la API solo los valida vía JWKS."
echo "Stack: Spring Boot 4.1.0, Spring Security 7.1.0, Java 21, Keycloak 26.4."
echo; echo "---"; echo; echo "## GUÍA DE LABORATORIO"
md guias/guia-03-oauth2.html
echo; echo "---"; echo; echo "## CÓDIGO DISTINTIVO DE ESTA ETAPA"; echo
echo "El CRUD es idéntico al de las otras etapas (ver documento de setup y referencia)."
echo "Lo característico aquí es lo que DESAPARECIÓ respecto a la etapa 02: no hay"
echo "UserDetailsService, ni JwtEncoder, ni JwtDecoder, ni llaves, ni AuthController."
vuelca 03-security-oauth2/src/main/java/com/luv2code/springboot/cruddemo/security/SecurityConfig.java java
vuelca 03-security-oauth2/src/main/resources/application.properties properties
vuelca 03-security-oauth2/pom.xml xml
echo; echo "## CONFIGURACIÓN DE KEYCLOAK"; echo
echo "Script re-ejecutable que crea el realm academy, el client employee-api, los roles"
echo "EMPLOYEE/MANAGER/ADMIN y los tres usuarios. Verificado desde cero y en segunda pasada."
vuelca 03-security-oauth2/scripts/keycloak-setup.sh bash
} > "$AQUI/03-oauth2.md"

echo "Regenerados en $AQUI:"
for f in "$AQUI"/0*.md; do printf "  %-28s %5s KB\n" "$(basename "$f")" "$(( $(wc -c < "$f") / 1024 ))"; done
