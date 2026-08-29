#!/usr/bin/env bash
# ---------------------------------------------------------------------------
#  Una suite que nadie corre no protege nada.
#
#  Corre la misma suite tres veces con filtros distintos y mide, de cada
#  corrida, cuanto tardaron LOS TESTS -- no el reloj de pared.
#
#  La distincion importa: en un proyecto de juguete Maven tarda mas en
#  arrancar que en correr todo. Lo que se compara aqui es el tiempo que
#  suma Surefire por clase, que es lo unico que crece con tu suite.
# ---------------------------------------------------------------------------
set -uo pipefail
cd "$(dirname "$0")/.."

MVN="./mvnw"; [ -x "$MVN" ] || MVN="mvn"

titulo() { printf '\n\033[1;36m== %s ==\033[0m\n' "$1"; }

# Suma los "Time elapsed: N s" que Surefire imprime por clase de test.
corrida() {
  local etiqueta="$1"; shift
  local salida resumen segundos
  salida="$($MVN test "$@" 2>&1)"
  resumen="$(echo "$salida" | grep -E "^\[(INFO|WARNING)\] Tests run: .*Skipped" | tail -1 | sed 's/^\[[A-Z]*\] //')"
  segundos="$(echo "$salida" \
      | grep -oE "Time elapsed: [0-9.]+ s -- in" \
      | grep -oE "[0-9.]+" \
      | awk '{s += $1} END {printf "%.3f", s}')"
  printf '   \033[1m%-26s\033[0m %-52s \033[1;33m%ss de tests\033[0m\n' \
         "$etiqueta" "$resumen" "$segundos"
}

titulo "La misma suite, tres filtros"
corrida "mvn test"
corrida "-DexcludedGroups=lento" -DexcludedGroups=lento
corrida "-Dgroups=rapido"        -Dgroups=rapido

titulo "Lo que acabas de ver"
cat <<'TXT'
   Los @Tag("lento") son 3 de 37 tests -- un 8 % de la suite -- y se
   llevan mas de la MITAD del tiempo de ejecucion (compara la primera
   linea con la segunda). Cada uno genera un acta a 40 ms por alumno.

   Y el filtro mas agresivo, -Dgroups=rapido, deja la suite en una
   fraccion: de ~1.1 s a ~0.02 s. Sesenta veces menos.

   Ese desequilibrio es la regla, no la excepcion: en cualquier
   proyecto real un punado de tests que tocan disco, red o base de
   datos acaba pesando mas que los cientos que no tocan nada.

   De ahi el reparto practico:

       antes de cada commit     mvn test -Dgroups=rapido
       antes de subir la rama   mvn test -DexcludedGroups=lento
       en el servidor de CI     mvn test

   AVISO SOBRE EL RELOJ: si cronometras estas tres corridas con un
   reloj de pared vas a ver casi lo mismo (~2 s cada una), porque en
   un proyecto de este tamano Maven tarda mas en arrancar que en
   correr nada. Lo que se compara arriba es el tiempo de los tests.
   Es lo unico que crece cuando tu suite crece.

   Y fijate en el "Skipped: 4" de la corrida completa: son los tests
   con assumptions y con @Disabled de CondicionesTest. NO fallaron,
   ni siquiera corrieron. Un build verde con tests abortados dentro
   sigue siendo un build que no probo esas rutas.
TXT

titulo "La suite con nombre (@Suite)"
echo "   mvn test -Dtest=SuiteDelCurso"
$MVN test -Dtest=SuiteDelCurso 2>&1 | grep -E "^\[INFO\] Tests run: .*Skipped" | tail -1 | sed 's/^\[INFO\]/  /'
echo "   -> 15 tests de humo, sin los lentos. Definido en SuiteDelCurso.java."
