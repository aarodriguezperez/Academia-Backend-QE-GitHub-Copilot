#!/usr/bin/env bash
# ---------------------------------------------------------------------------
#  32 metodos escritos a mano -> 211 tests ejecutados.
#
#  Ese es el tema entero del proyecto 03, en un numero. Este script lo
#  cuenta delante de ti: primero los metodos que hay en el codigo fuente,
#  despues los tests que de verdad corrieron.
# ---------------------------------------------------------------------------
set -uo pipefail
cd "$(dirname "$0")/.."

MVN="./mvnw"; [ -x "$MVN" ] || MVN="mvn"
FUENTES="src/test/java/com/academymty/academia"

titulo() { printf '\n\033[1;36m== %s ==\033[0m\n' "$1"; }

contar() { grep -h -cE "^\s*@$1(\(|\s*$)" $FUENTES/*.java | awk '{s += $1} END {print s}'; }

titulo "1. Lo que hay ESCRITO en src/test/java"
T=$(contar "Test"); P=$(contar "ParameterizedTest"); R=$(contar "RepeatedTest"); F=$(contar "TestFactory")
printf '   %-22s %3s\n' "@Test"              "$T"
printf '   %-22s %3s\n' "@ParameterizedTest" "$P"
printf '   %-22s %3s\n' "@RepeatedTest"      "$R"
printf '   %-22s %3s\n' "@TestFactory"       "$F"
printf '   \033[1m%-22s %3s metodos\033[0m\n' "TOTAL" "$((T + P + R + F))"

titulo "2. Lo que de verdad SE EJECUTA"
SALIDA="$($MVN test 2>&1)"
echo "$SALIDA" | grep -E "^\[INFO\] Tests run: .*Skipped" | tail -1 | sed 's/^\[INFO\]/  /'

titulo "3. Y clase por clase"
echo "$SALIDA" | grep -E "Tests run: .*-- in com" \
  | sed -E 's/.*Tests run: ([0-9]+),.*-- in com\.academymty\.academia\.(.*)/   \2|\1/' \
  | awk -F'|' '{printf "   %-22s %3s tests\n", $1, $2}'

titulo "Lo que significa"
cat <<'TXT'
   Un metodo por cada FORMA de probar algo.
   Un test por cada DATO con el que se prueba.

   Escribir el caso 212 no cuesta un metodo nuevo: cuesta una linea en
   una tabla. Y ahi esta el verdadero cambio -- no es que escribas menos
   codigo, es que anadir un caso deja de dar pereza. Los casos limite se
   prueban cuando probarlos es barato.

   Si algo falla, el reporte no dice "curpsInvalidas fallo": dice cual de
   los diez datos fallo, con su nombre. Por eso vale la pena el atributo
   name = "..." en cada @ParameterizedTest.
TXT
