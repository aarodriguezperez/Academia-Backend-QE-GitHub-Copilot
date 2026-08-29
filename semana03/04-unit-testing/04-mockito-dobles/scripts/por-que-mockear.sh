#!/usr/bin/env bash
# ---------------------------------------------------------------------------
#  Por que existe Mockito, medido en vez de afirmado.
#
#  La misma clase bajo prueba, los mismos caminos, dos suites:
#
#    ConRepositorioRealTest   usa RepositorioAlumnosLento -> 300 ms por consulta
#    las demas                usan un @Mock               -> microsegundos
#
#  Se mide el tiempo que suma Surefire por clase, no el reloj de pared:
#  en un proyecto de este tamano Maven tarda mas en arrancar que en correr
#  nada, y eso taparia justo lo que queremos ver.
# ---------------------------------------------------------------------------
set -uo pipefail
cd "$(dirname "$0")/.."

MVN="./mvnw"; [ -x "$MVN" ] || MVN="mvn"

titulo() { printf '\n\033[1;36m== %s ==\033[0m\n' "$1"; }

corrida() {
  local etiqueta="$1"; shift
  local salida resumen segundos
  salida="$($MVN -B test "$@" 2>&1)"
  resumen="$(echo "$salida" | grep -E "^\[(INFO|WARNING)\] Tests run: .*Skipped" | tail -1 | sed 's/^\[[A-Z]*\] //')"
  segundos="$(echo "$salida" | grep -oE "Time elapsed: [0-9.]+ s -- in" | grep -oE "[0-9.]+" \
              | awk '{s += $1} END {printf "%.3f", s}')"
  printf '   \033[1m%-34s\033[0m %-46s \033[1;33m%ss\033[0m\n' "$etiqueta" "$resumen" "$segundos"
}

titulo "La misma prueba, con y sin dobles"
corrida "con el repositorio REAL (300 ms)" -Dgroups=lento
corrida "con dobles de Mockito"            -DexcludedGroups=lento

titulo "Lee bien esos dos numeros"
cat <<'TXT'
   Cinco tests contra el repositorio real tardan MAS que los cuarenta
   que usan dobles. Y la latencia simulada es modesta: 300 ms. Una
   consulta real por red, con la base en otro servidor, no es mas rapida.

   Multiplica: doscientos tests que consulten dos veces cada uno, a
   300 ms, son dos minutos de espera. Y no es solo el reloj -- es que
   a los dos minutos la gente deja de correr la suite antes de subir
   codigo, y una suite que nadie corre no protege nada (guia 02, §05).

   Esa es la primera razon para usar un doble: VELOCIDAD.
   Pero no es la mas fuerte. Las otras dos son:

     CONTROL   con un mock provocas en una linea lo que con la pieza
               real cuesta un escenario entero: la base caida, el curso
               lleno, el timeout. Ver ServicioInscripcionTest.

     SEGURIDAD el Notificador real manda correos DE VERDAD. Un test que
               lo use manda correos a personas cada vez que alguien
               ejecuta `mvn test`. Eso no se puede deshacer.

   Ojo: esto NO dice que ConRepositorioRealTest sobre. Es el unico que
   comprueba que el repositorio de verdad funciona. Lo que no puede
   pasar es que TODA la suite sea asi.
TXT
