# 2026-08-22 · Setup y referencia

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

```
================================================================
 SEGURIDAD Y AUTENTICACION - preparacion del entorno
 Tres proyectos: 01 Basic (8071) · 02 JWT (8072) · 03 OAuth2 (8073)
================================================================

----------------------------------------------------------------
 PARA LOS TRES PROYECTOS: MySQL
----------------------------------------------------------------
Los empleados viven en MySQL en los tres proyectos.

  docker start mysql-9.7          (si ya lo tienes del proyecto 15/16)

  # o desde cero:
  docker run --name mysql-9.7 -p 3306:3306 \
    -e MYSQL_ROOT_PASSWORD=tu_password -d mysql:9.7

Si empiezas de cero de verdad (contenedor recien creado), antes que nada
necesitas la base employee_directory, el usuario springstudent y la tabla
employee. Todo eso esta en:

  ../15-spring-boot-rest-crud-employee-rest-controller-delete-employee/instructions.txt

Ese archivo trae los dos scripts SQL: el que crea el usuario springstudent
y el que crea la base y la tabla employee. Ejecuta esos DOS primero; el de
aqui abajo solo agrega las tablas de usuarios de seguridad.

OJO: si tienes tambien el contenedor bd-mysql, los dos usan el 3306
y NO pueden correr a la vez. Arranca solo uno.

----------------------------------------------------------------
 PROYECTOS 01 y 02: las tablas de usuarios
----------------------------------------------------------------
Crea las tablas members y roles con los 3 usuarios de practica
(john, mary, susan - todos con password test123):

  docker exec -i mysql-9.7 mysql -uroot -pTU_PASSWORD employee_directory \
    < sql-scripts/01-security-tables.sql

----------------------------------------------------------------
 PROYECTO 02: el par de llaves RSA
----------------------------------------------------------------
El proyecto ya trae unas llaves listas para que la clase funcione.
Para generar las tuyas (y entender de donde salen):

  cd 02-security-jwt
  mkdir -p src/main/resources/certs
  openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 \
    -out src/main/resources/certs/private.pem
  openssl rsa -pubout -in src/main/resources/certs/private.pem \
    -out src/main/resources/certs/public.pem

La privada debe empezar con "-----BEGIN PRIVATE KEY-----" (formato PKCS#8).
Si dice "BEGIN RSA PRIVATE KEY" es PKCS#1 y Spring no la va a leer.

En un proyecto real la llave privada NUNCA se sube a git.

----------------------------------------------------------------
 PROYECTO 03: Keycloak
----------------------------------------------------------------
Puerto 8090. NO uses el 8081: en Windows ese puerto lo ocupa
mongo-express del proyecto 16.

  docker run --name keycloak-academy -p 8090:8080 \
    -e KC_BOOTSTRAP_ADMIN_USERNAME=admin \
    -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin \
    -d quay.io/keycloak/keycloak:26.4 start-dev

Para Windows (PowerShell), con acento grave al final de cada linea:

  docker run --name keycloak-academy -p 8090:8080 `
    -e KC_BOOTSTRAP_ADMIN_USERNAME=admin `
    -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin `
    -d quay.io/keycloak/keycloak:26.4 start-dev

Tarda unos 15 segundos en arrancar. Luego crea el realm, el client,
los roles y los usuarios de una sola vez:

  ./03-security-oauth2/scripts/keycloak-setup.sh

Consola web:  http://localhost:8090     (admin / admin)
Comprobacion: curl http://localhost:8090/realms/academy/.well-known/openid-configuration

Las siguientes veces basta con:  docker start keycloak-academy

----------------------------------------------------------------
 MAPA DE PUERTOS
----------------------------------------------------------------
  3306   MySQL
  8070   proyecto 16 con MySQL (sin seguridad)
  8071   01-security-basic
  8072   02-security-jwt
  8073   03-security-oauth2
  8081   proyecto 16 con MongoDB
  8082   mongo-express (del proyecto 16 MongoDB)
  8090   Keycloak
  27017  MongoDB

Keycloak va en el 8090 a proposito: el 8080 lo usa el contenedor tomcat9 y
el 8081/8082 son del proyecto 16 con MongoDB.
```

## Script SQL de las tablas de usuarios (etapas 01 y 02)

### `sql-scripts/01-security-tables.sql`

```sql
--
-- Tablas de seguridad para los proyectos 01-security-basic y 02-security-jwt
--
-- Uso:
--   docker exec -i mysql-9.7 mysql -uroot -pTU_PASSWORD employee_directory < 01-security-tables.sql
--
-- Los 3 usuarios comparten la MISMA contrasena: test123
-- ...pero fijate que los 3 hashes son DISTINTOS. Eso es el "salt" de BCrypt:
-- misma contrasena + salt aleatorio = hash distinto cada vez.
--

USE employee_directory;

DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS members;

--
-- Tabla de usuarios.
-- pw es char(68) porque Spring guarda: {bcrypt} (8) + hash BCrypt (60) = 68
--
CREATE TABLE members (
    user_id varchar(50) NOT NULL,
    pw char(68) NOT NULL,
    active tinyint NOT NULL,
    PRIMARY KEY (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO members (user_id, pw, active) VALUES
('john',  '{bcrypt}$2y$10$q5C89SItU5ZKPZlTspXrZOOcm7njHEeRF7dys6b.Bgo7NhKWbMGfG', 1),
('mary',  '{bcrypt}$2y$10$y0UvRlnLKlOh7nBfH8sNvuXUIVhvwOMYaz1ysyJoPYvwY8tCg.K/i', 1),
('susan', '{bcrypt}$2y$10$6eOesXl7A1E7kaE7UYulPu4h5o5r6Yqd/F/dPFMWx2kDTZA64qU1W', 1);

--
-- Tabla de roles. Un usuario puede tener varios.
-- OJO: aqui se guarda "ROLE_EMPLOYEE" con el prefijo ROLE_,
-- pero en Java se escribe hasRole("EMPLOYEE") SIN el prefijo. Spring lo agrega solo.
--
CREATE TABLE roles (
    user_id varchar(50) NOT NULL,
    role varchar(50) NOT NULL,
    UNIQUE KEY authorities_idx_1 (user_id, role),
    CONSTRAINT authorities_ibfk_1
        FOREIGN KEY (user_id) REFERENCES members (user_id)
        ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO roles (user_id, role) VALUES
('john',  'ROLE_EMPLOYEE'),
('mary',  'ROLE_EMPLOYEE'),
('mary',  'ROLE_MANAGER'),
('susan', 'ROLE_EMPLOYEE'),
('susan', 'ROLE_MANAGER'),
('susan', 'ROLE_ADMIN');

SELECT m.user_id, m.active, r.role FROM members m JOIN roles r ON m.user_id = r.user_id ORDER BY m.user_id, r.role;
```

## El CRUD base, común a las tres etapas

Estas seis clases son BYTE POR BYTE IDÉNTICAS en los tres proyectos: vienen del proyecto
16 y no se tocaron al añadir seguridad. Ese es justamente el argumento pedagógico del
tema: la seguridad entra por la cadena de filtros, no modificando el controlador.
Lo único que cambia entre etapas es `SecurityConfig` (y `AuthController` en la etapa 02).

### `01-security-basic/src/main/java/com/luv2code/springboot/cruddemo/CruddemoApplication.java`

```java
package com.luv2code.springboot.cruddemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CruddemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(CruddemoApplication.class, args);
	}

}
```

### `01-security-basic/src/main/java/com/luv2code/springboot/cruddemo/entity/Employee.java`

```java
package com.luv2code.springboot.cruddemo.entity;

import jakarta.persistence.*;

@Entity
@Table(name="employee")
public class Employee {

    // define fields
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="first_name")
    private String firstName;

    @Column(name="last_name")
    private String lastName;

    @Column(name="email")
    private String email;


    // define constructors
    public Employee() {

    }

    public Employee(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    // define getter/setter

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // define toString
    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
```

### `01-security-basic/src/main/java/com/luv2code/springboot/cruddemo/dao/EmployeeRepository.java`

```java
package com.luv2code.springboot.cruddemo.dao;

import com.luv2code.springboot.cruddemo.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    // that's it ... no need to write any code LOL!

}
```

### `01-security-basic/src/main/java/com/luv2code/springboot/cruddemo/service/EmployeeService.java`

```java
package com.luv2code.springboot.cruddemo.service;

import com.luv2code.springboot.cruddemo.entity.Employee;

import java.util.List;

public interface EmployeeService {

    List<Employee> findAll();

    Employee findById(int theId);

    Employee save(Employee theEmployee);

    void deleteById(int theId);

}
```

### `01-security-basic/src/main/java/com/luv2code/springboot/cruddemo/service/EmployeeServiceImpl.java`

```java
package com.luv2code.springboot.cruddemo.service;

import com.luv2code.springboot.cruddemo.dao.EmployeeRepository;
import com.luv2code.springboot.cruddemo.entity.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository theEmployeeRepository) {
        employeeRepository = theEmployeeRepository;
    }

    @Override
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee findById(int theId) {
        Optional<Employee> result = employeeRepository.findById(theId);

        Employee theEmployee = null;

        if (result.isPresent()) {
            theEmployee = result.get();
        }
        else {
            // we didn't find the employee
            throw new RuntimeException("Did not find employee id - " + theId);
        }

        return theEmployee;
    }

    @Override
    public Employee save(Employee theEmployee) {
        return employeeRepository.save(theEmployee);
    }

    @Override
    public void deleteById(int theId) {
        employeeRepository.deleteById(theId);
    }
}
```

### `01-security-basic/src/main/java/com/luv2code/springboot/cruddemo/rest/EmployeeRestController.java`

```java
package com.luv2code.springboot.cruddemo.rest;

import tools.jackson.databind.json.JsonMapper;
import com.luv2code.springboot.cruddemo.entity.Employee;
import com.luv2code.springboot.cruddemo.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class EmployeeRestController {

    private EmployeeService employeeService;

    private JsonMapper jsonMapper;

    @Autowired
    public EmployeeRestController(EmployeeService theEmployeeService, JsonMapper theJsonMapper) {
        employeeService = theEmployeeService;
        jsonMapper = theJsonMapper;
    }

    // expose "/employees" and return a list of employees
    @GetMapping("/employees")
    public List<Employee> findAll() {
        return employeeService.findAll();
    }

    // add mapping for GET /employees/{employeeId}

    @GetMapping("/employees/{employeeId}")
    public Employee getEmployee(@PathVariable int employeeId) {

        Employee theEmployee = employeeService.findById(employeeId);

        if (theEmployee == null) {
            throw new RuntimeException("Employee id not found - " + employeeId);
        }

        return theEmployee;
    }

    // add mapping for POST /employees - add new employee

    @PostMapping("/employees")
    public Employee addEmployee(@RequestBody Employee theEmployee) {

        // also just in case they pass an id in JSON ... set id to 0
        // this is to force a save of new item ... instead of update

        theEmployee.setId(0);

        Employee dbEmployee = employeeService.save(theEmployee);

        return dbEmployee;
    }

    // add mapping for PUT /employees - update existing employee

    @PutMapping("/employees")
    public Employee updateEmployee(@RequestBody Employee theEmployee) {

        Employee dbEmployee = employeeService.save(theEmployee);

        return dbEmployee;
    }

    // add mapping for PATCH /employees/{employeeId} - patch employee ... partial
    // update

    @PatchMapping("/employees/{employeeId}")
    public Employee patchEmployee(@PathVariable int employeeId,
            @RequestBody Map<String, Object> patchPayload) {

        // Step 1: Retrieve the existing employee from database
        Employee tempEmployee = employeeService.findById(employeeId);

        if (tempEmployee == null) {
            throw new RuntimeException("Employee id not found - " + employeeId);
        }

        // Step 2: Security check - prevent ID modifications
        // The ID should never change, so reject any attempts to modify it
        if (patchPayload.containsKey("id")) {
            throw new RuntimeException(
                    "Employee id cannot be modified. Remove 'id' from request body.");
        }

        // Step 3: Apply the partial update
        // This creates a NEW employee object with the updates applied
        Employee patchedEmployee = jsonMapper.updateValue(tempEmployee, patchPayload);

        // Step 4: Save the updated employee to database and return it
        Employee dbEmployee = employeeService.save(patchedEmployee);

        return dbEmployee;
    }

    // add mapping for DELETE /employees/{employeeId} - delete employee

    @DeleteMapping("/employees/{employeeId}")
    public String deleteEmployee(@PathVariable int employeeId) {

        Employee tempEmployee = employeeService.findById(employeeId);

        // throw exception if null

        if (tempEmployee == null) {
            throw new RuntimeException("Employee id not found - " + employeeId);
        }

        employeeService.deleteById(employeeId);

        return "Deleted employee id - " + employeeId;
    }

}
```

## Scripts de prueba

Cada etapa trae su matriz automatizada. Resultados verificados: 6 + 11 + 8 = 25 comprobaciones en verde.

### `01-security-basic/scripts/test-endpoints.sh`

```bash
#!/bin/bash
# Matriz de seguridad de 01-security-basic (HTTP Basic + roles)
# Puerto 8071. Los 3 usuarios tienen la misma password: test123
#
#   john   ROLE_EMPLOYEE                          -> solo puede LEER
#   mary   ROLE_EMPLOYEE + ROLE_MANAGER           -> lee, crea y modifica
#   susan  ROLE_EMPLOYEE + ROLE_MANAGER + ADMIN   -> ademas puede BORRAR
#
# El script crea un empleado temporal y lo borra al final: tus datos quedan igual.

BASE="http://localhost:8071/api/employees"

paso() { echo; echo "════════ $1 ════════"; }

# probar USUARIO:PASS METODO URL [JSON] [esperado]
probar() {
  local cred=$1 m=$2 u=$3 d=$4 esp=$5
  local salida code
  if [ -n "$d" ]; then
    salida=$(curl -s -w "\n%{http_code}" -u "$cred" -X "$m" "$u" -H "Content-Type: application/json" -d "$d")
  else
    salida=$(curl -s -w "\n%{http_code}" -u "$cred" -X "$m" "$u")
  fi
  code=$(echo "$salida" | tail -1)
  local marca="  "
  [ -n "$esp" ] && { [ "$code" = "$esp" ] && marca="OK" || marca="!!"; }
  printf "%s  %-22s %-6s -> HTTP %s   (esperado %s)\n" "$marca" "${cred%%:*}" "$m" "$code" "${esp:-?}"
  echo "$salida" | sed '$d' | head -c 160; echo
}

paso "1. Sin credenciales: la API ya no esta abierta"
echo "→ curl -i $BASE   (fijate en la cabecera WWW-Authenticate)"
curl -s -D - -o /dev/null "$BASE" | head -2

paso "2. Credenciales invalidas -> 401 (no se quien eres)"
probar "john:PASSWORD_MALA" GET "$BASE" "" 401

paso "3. john = ROLE_EMPLOYEE: puede LEER"
probar "john:test123" GET "$BASE" "" 200

paso "4. john intenta CREAR -> 403 (se quien eres, pero no te toca)"
probar "john:test123" POST "$BASE" '{"firstName":"X","lastName":"Y","email":"x@y.com"}' 403

paso "5. john intenta BORRAR -> 403"
probar "john:test123" DELETE "$BASE/1" "" 403

# Red de seguridad: si el script se interrumpe (Ctrl-C, o la salida se corta con
# head/less), borramos igual el empleado temporal para no dejar basura en la tabla.
ID=""
limpiar() { [ -n "$ID" ] && curl -s -o /dev/null -u susan:test123 -X DELETE "$BASE/$ID"; }
trap limpiar EXIT

paso "6. mary = ROLE_MANAGER: si puede CREAR"
NUEVO=$(curl -s -u mary:test123 -X POST "$BASE" -H "Content-Type: application/json" \
  -d '{"firstName":"Temp","lastName":"Borrame","email":"temp@test.com"}')
echo "$NUEVO"
ID=$(echo "$NUEVO" | sed -n 's/.*"id":\([0-9]*\).*/\1/p')
echo "  ➜ empleado temporal creado con id: $ID"

paso "7. mary intenta BORRAR -> 403 (crear no implica borrar)"
probar "mary:test123" DELETE "$BASE/$ID" "" 403

paso "8. susan = ROLE_ADMIN: si puede BORRAR"
probar "susan:test123" DELETE "$BASE/$ID" "" 200
ID=""   # ya lo borramos: la limpieza automatica no tiene nada que hacer

paso "9. Lo que curl -u manda en realidad"
echo "→ curl -v -u john:test123 $BASE  | grep Authorization"
curl -s -o /dev/null -v -u john:test123 "$BASE" 2>&1 | grep -i "^> authorization:"
echo
echo "Eso NO esta cifrado, solo empaquetado en base64. Se revierte en un comando:"
echo "→ echo -n 'am9objp0ZXN0MTIz' | base64 -d"
echo -n 'am9objp0ZXN0MTIz' | base64 -d; echo
echo
echo "Moraleja: HTTP Basic SIN HTTPS = mandar la password en texto plano."

paso "10. Estado final (debe ser igual al inicial)"
curl -s -u susan:test123 "$BASE"; echo

echo
echo "════════ RESUMEN ════════"
echo "  401 = no se quien eres      (fallo de AUTENTICACION)"
echo "  403 = se quien eres, pero no puedes  (fallo de AUTORIZACION)"
```

### `02-security-jwt/scripts/test-endpoints.sh`

```bash
#!/bin/bash
# Matriz de seguridad de 02-security-jwt (JWT + roles)
# Puerto 8072. Los 3 usuarios tienen la misma password: test123
#
# La diferencia con el proyecto 01: la contrasena solo viaja UNA vez, al /login.
# Todo lo demas va con "Authorization: Bearer <token>".

BASE="http://localhost:8072/api/employees"
LOGIN="http://localhost:8072/api/auth/login"

paso() { echo; echo "════════ $1 ════════"; }

# OJO con el quoting: en zsh, "$1:test123" activa un modificador de historia
# y te deja sin credenciales. Hay que escribir "${1}:test123".
token() {
  curl -s -u "${1}:test123" -X POST "$LOGIN" \
    | python3 -c "import json,sys;print(json.load(sys.stdin)['accessToken'])" 2>/dev/null
}

payload() {  # imprime el contenido del token, sin ninguna llave: solo base64
  echo "$1" | cut -d. -f2 | python3 -c "
import sys,base64,json
s=sys.stdin.read().strip(); s+='='*(-len(s)%4)
print(json.dumps(json.loads(base64.urlsafe_b64decode(s)), indent=2))"
}

probar() {  # probar DESCRIPCION ESPERADO curl-args...
  local desc=$1 esp=$2; shift 2
  local code=$(curl -s -o /dev/null -w '%{http_code}' "$@")
  local marca="!!"; [ "$code" = "$esp" ] && marca="OK"
  printf "%s  %-48s -> HTTP %s  (esperado %s)\n" "$marca" "$desc" "$code" "$esp"
}

ID=""
limpiar() { [ -n "$ID" ] && curl -s -o /dev/null -H "Authorization: Bearer $TS" -X DELETE "$BASE/$ID"; }
trap limpiar EXIT

paso "1. Login: cambiar credenciales por un token"
probar "login con password mala"   401 -u john:MALA -X POST "$LOGIN"
probar "login sin credenciales"    401 -X POST "$LOGIN"
TJ=$(token john); TM=$(token mary); TS=$(token susan)
echo "→ curl -u john:test123 -X POST $LOGIN"
echo "$TJ" | head -c 80; echo "..."

paso "2. Lo que ese token lleva dentro"
echo "Nadie necesita una llave para leer esto. Va FIRMADO, no cifrado:"
payload "$TJ"
echo "Por eso NUNCA se ponen datos secretos en un JWT."

paso "3. Usar el token"
probar "GET sin token"                      401 "$BASE"
probar "GET con token de john"              200 -H "Authorization: Bearer $TJ" "$BASE"
probar "POST con token de john (EMPLOYEE)"  403 -H "Authorization: Bearer $TJ" -X POST -H 'Content-Type: application/json' -d '{"firstName":"X","lastName":"Y","email":"x@y.com"}' "$BASE"
probar "DELETE con token de john"           403 -H "Authorization: Bearer $TJ" -X DELETE "$BASE/1"

paso "4. Lo que YA NO funciona (esta es la leccion del proyecto)"
probar "HTTP Basic contra /api/employees"   401 -u susan:test123 "$BASE"
echo "La cadena 2 solo entiende Bearer. La contrasena ya no abre esta puerta."

paso "5. Manipular el token"
# IMPORTANTE: se cambia un caracter del MEDIO de la firma, no el ultimo.
# Al ultimo caracter de la firma le sobran 4 bits, asi que 15 de 63 sustituciones
# decodifican a la MISMA firma y el token seguiria siendo valido (~24% de las veces).
SIG=$(echo "$TJ" | cut -d. -f3)
MID=$(python3 -c "
s='$SIG'; c='A' if s[10]!='A' else 'B'; print(s[:10]+c+s[11:])")
TBAD="$(echo "$TJ" | cut -d. -f1).$(echo "$TJ" | cut -d. -f2).$MID"
probar "firma alterada (1 caracter del medio)" 401 -H "Authorization: Bearer $TBAD" "$BASE"

FALSO="$(echo "$TJ" | cut -d. -f1).$(echo -n '{"iss":"security-jwt","sub":"john","exp":9999999999,"roles":["ROLE_ADMIN"]}' | base64 | tr -d '=' | tr '/+' '_-').$(echo "$TJ" | cut -d. -f3)"
probar "payload reescrito a ROLE_ADMIN"        401 -X DELETE -H "Authorization: Bearer $FALSO" "$BASE/1"
echo "Puedes reescribir el payload todo lo que quieras: sin la llave privada"
echo "no puedes recalcular la firma, y el servidor lo detecta."

paso "6. Los roles siguen mandando (van dentro del token)"
NUEVO=$(curl -s -H "Authorization: Bearer $TM" -X POST "$BASE" -H "Content-Type: application/json" \
  -d '{"firstName":"Temp","lastName":"Jwt","email":"temp@jwt.com"}')
echo "$NUEVO"
ID=$(echo "$NUEVO" | sed -n 's/.*"id":\([0-9]*\).*/\1/p')
echo "  ➜ empleado temporal creado con id: $ID"
probar "DELETE con token de mary (MANAGER)"  403 -H "Authorization: Bearer $TM" -X DELETE "$BASE/$ID"
probar "DELETE con token de susan (ADMIN)"   200 -H "Authorization: Bearer $TS" -X DELETE "$BASE/$ID"
ID=""

paso "7. Estado final (debe ser igual al inicial)"
curl -s -H "Authorization: Bearer $TS" "$BASE"; echo

echo
echo "════════ SOBRE LA CADUCIDAD ════════"
echo "  Para verla en vivo, arranca con un TTL corto:"
echo "    java -jar target/security-jwt-0.0.1-SNAPSHOT.jar --jwt.ttl-seconds=3"
echo "  ...y NO esperes 3 segundos: espera 65."
echo "  Spring tolera 60 segundos de desfase de reloj por defecto"
echo "  (JwtTimestampValidator), pensando en servidores con relojes distintos."
```

### `03-security-oauth2/scripts/test-endpoints.sh`

```bash
#!/bin/bash
# Matriz de seguridad de 03-security-oauth2 (OAuth2 + Keycloak)
# API en 8073. Keycloak en 8090. Usuarios john/mary/susan, password test123.
#
# Diferencia clave con los proyectos 01 y 02: esta API NO tiene usuarios.
# El token lo pides a Keycloak; la API solo lo valida.

BASE="http://localhost:8073/api/employees"
KC="http://localhost:8090/realms/academy/protocol/openid-connect"

paso() { echo; echo "════════ $1 ════════"; }

token() {
  curl -s -X POST "$KC/token" \
    -d grant_type=password -d client_id=employee-api \
    -d username="$1" -d password=test123 \
    | python3 -c "import json,sys;print(json.load(sys.stdin)['access_token'])" 2>/dev/null
}

probar() {  # probar DESCRIPCION ESPERADO curl-args...
  local desc=$1 esp=$2; shift 2
  local code=$(curl -s -o /dev/null -w '%{http_code}' "$@")
  local marca="!!"; [ "$code" = "$esp" ] && marca="OK"
  printf "%s  %-48s -> HTTP %s  (esperado %s)\n" "$marca" "$desc" "$code" "$esp"
}

ID=""
limpiar() { [ -n "$ID" ] && curl -s -o /dev/null -H "Authorization: Bearer $TS" -X DELETE "$BASE/$ID"; }
trap limpiar EXIT

paso "1. Lo que la API descarga sola del emisor"
echo "→ curl $KC/../.well-known/openid-configuration"
curl -s "http://localhost:8090/realms/academy/.well-known/openid-configuration" \
  | python3 -c "
import sys,json; d=json.load(sys.stdin)
for k in ('issuer','token_endpoint','jwks_uri'): print(f'  {k}: {d[k]}')"
echo "  Esa jwks_uri es la llave publica. La API la baja sola: no hay .pem en el proyecto."

paso "2. Pedir el token A KEYCLOAK (no a la API)"
TJ=$(token john); TM=$(token mary); TS=$(token susan)
echo "→ curl -X POST $KC/token -d grant_type=password -d client_id=employee-api ..."
echo "  token de john: ${#TJ} caracteres (el del proyecto 02 medía ~490)"
echo "$TJ" | cut -d. -f2 | python3 -c "
import sys,base64,json
s=sys.stdin.read().strip(); s+='='*(-len(s)%4)
p=json.loads(base64.urlsafe_b64decode(s))
print('  iss:', p['iss'])
print('  sub:', p['sub'], ' <- un UUID, no \"john\"')
print('  preferred_username:', p.get('preferred_username'))
print('  realm_access.roles:', p['realm_access']['roles'])"

paso "3. Usar el token contra la API"
probar "sin token"                          401 "$BASE"
probar "token de john (EMPLOYEE) GET"       200 -H "Authorization: Bearer $TJ" "$BASE"
probar "token de john POST"                 403 -H "Authorization: Bearer $TJ" -X POST -H 'Content-Type: application/json' -d '{"firstName":"X","lastName":"Y","email":"x@y.com"}' "$BASE"
probar "token de john DELETE"               403 -H "Authorization: Bearer $TJ" -X DELETE "$BASE/1"

paso "4. HTTP Basic ya no existe en esta API"
probar "curl -u susan:test123"              401 -u susan:test123 "$BASE"
echo "Y no podria existir: esta API no tiene tabla de usuarios ni contrasenas."

paso "5. Los roles siguen mandando (ahora vienen de Keycloak)"
NUEVO=$(curl -s -H "Authorization: Bearer $TM" -X POST "$BASE" -H "Content-Type: application/json" \
  -d '{"firstName":"Temp","lastName":"Oauth","email":"temp@oauth.com"}')
echo "$NUEVO"
ID=$(echo "$NUEVO" | sed -n 's/.*"id":\([0-9]*\).*/\1/p')
echo "  ➜ empleado temporal creado con id: $ID"
probar "DELETE con token de mary (MANAGER)" 403 -H "Authorization: Bearer $TM" -X DELETE "$BASE/$ID"
probar "DELETE con token de susan (ADMIN)"  200 -H "Authorization: Bearer $TS" -X DELETE "$BASE/$ID"
ID=""

paso "6. Un token VALIDO, pero del emisor equivocado"
echo "Si tienes el proyecto 02 corriendo en el 8072, su token es un JWT perfecto..."
T02=$(curl -s -u "john:test123" -X POST http://localhost:8072/api/auth/login \
      | python3 -c "import json,sys;print(json.load(sys.stdin)['accessToken'])" 2>/dev/null)
if [ -n "$T02" ]; then
  probar "token emitido por el proyecto 02"  401 -H "Authorization: Bearer $T02" "$BASE"
  curl -s -D - -o /dev/null -H "Authorization: Bearer $T02" "$BASE" | grep -i www-authenticate
  echo "...pero lo firmo OTRA llave privada. La confianza no es en 'un JWT': es en QUIEN lo firmo."
else
  echo "  (el proyecto 02 no esta corriendo; arrancalo en el 8072 para ver esta prueba)"
fi

paso "7. Estado final (debe ser igual al inicial)"
curl -s -H "Authorization: Bearer $TS" "$BASE"; echo
```
