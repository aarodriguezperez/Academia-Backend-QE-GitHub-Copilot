#!/usr/bin/env bash
# ---------------------------------------------------------------------------
#  Un mock es una mentira que tu controlas.
#  El riesgo no es que falle: es que te la creas.
#
#  Este script rompe la regla del cupo dentro de Curso -- un solo operador,
#  '== 0' pasa a '< 0' -- y corre DOS clases de test que prueban lo mismo:
#
#    ServicioInscripcionTest  usa un Curso REAL         -> deberia caer
#    SobreMockeoTest          mockea el Curso           -> deberia seguir verde
#
#  El codigo se restaura pase lo que pase.
# ---------------------------------------------------------------------------
set -uo pipefail
cd "$(dirname "$0")/.."

FUENTE="src/main/java/com/academymty/academia/Curso.java"
RESPALDO="$(mktemp)"
MVN="./mvnw"; [ -x "$MVN" ] || MVN="mvn"

titulo() { printf '\n\033[1;36m== %s ==\033[0m\n' "$1"; }
rojo()   { printf '\033[1;31m%s\033[0m\n' "$1"; }
verde()  { printf '\033[1;32m%s\033[0m\n' "$1"; }

restaurar() { cp "$RESPALDO" "$FUENTE"; rm -f "$RESPALDO"; }
trap restaurar EXIT INT TERM

# Guarda: si una corrida anterior murio sin restaurar, el archivo ya trae el
# bug y esta corrida lo tomaria como "el original", reportando que todo va
# bien con el codigo roto. La leccion la aprendimos en el proyecto 01.
if grep -q "BUG INYECTADO" "$FUENTE"; then
  rojo "   $FUENTE todavia tiene el bug de una corrida anterior."
  echo "   Cambia el '< 0' por '== 0' en estaLleno(), o recuperalo con git."
  exit 1
fi

cp "$FUENTE" "$RESPALDO"
HUELLA_ORIGINAL="$(shasum -a 256 "$FUENTE" | cut -d' ' -f1)"

resultado() {  # $1 = clase, $2 = que esperamos
  local salida resumen
  salida="$($MVN -B test -Dtest="$1" 2>&1)"
  resumen="$(echo "$salida" | grep -E "^\[(INFO|ERROR)\] Tests run: .*Skipped" | tail -1 | sed 's/^\[[A-Z]*\] //')"
  if echo "$salida" | grep -q "BUILD SUCCESS"; then
    printf '   \033[1;32m%-26s VERDE\033[0m  %s\n' "$1" "$resumen"
  else
    printf '   \033[1;31m%-26s ROJO \033[0m  %s\n' "$1" "$resumen"
    echo "$salida" | grep -E "^\[ERROR\]   [A-Za-z]" | sed 's/^/        /' | head -4
  fi
}

titulo "1. Las dos clases, con el codigo sano"
resultado ServicioInscripcionTest
resultado SobreMockeoTest
echo "   Las dos en verde. Prueban lo mismo y las dos parecen razonables."

titulo "2. Rompemos la regla del cupo: 'lugaresDisponibles() == 0' -> '< 0'"
sed -i.bak 's|return lugaresDisponibles() == 0;|return lugaresDisponibles() < 0;   // BUG INYECTADO|' "$FUENTE"
rm -f "$FUENTE.bak"
grep -n "lugaresDisponibles() < 0" "$FUENTE" | sed 's/^/   /'
echo "   Ahora un curso lleno dice que NO lo esta. Se cuelan alumnos de mas."

titulo "3. Las mismas dos clases, con el bug dentro"
resultado ServicioInscripcionTest
resultado SobreMockeoTest

titulo "4. Ahi esta la mentira"
cat <<'TXT'
   SobreMockeoTest sigue en VERDE con el bug dentro.

   No es mala suerte ni un descuido: es la consecuencia exacta de haber
   escrito @Mock sobre Curso. Al mockearlo, estaLleno() ya no ejecuta la
   regla -- devuelve lo que el propio test escribio en el when(). El test
   no comprueba "un curso lleno se rechaza": comprueba "si yo digo que
   esta lleno, se rechaza". Aprueba su propia respuesta.

   ServicioInscripcionTest lo caza porque construye el Curso con new. La
   diferencia entre las dos clases es UNA LINEA.

   La regla, entonces:

       MOCKEA lo que DUELE      lento, externo, no repetible, no determinista.
                                Los repositorios y el notificador: bien.

       NO MOCKEES lo que DECIDE si tiene dentro la regla que estas probando,
                                va real. Curso: mal.

   Y el corolario incomodo, que es el mismo de la guia 01: la cobertura
   no distingue estos dos casos. Las dos clases "cubren" ServicioInscripcion
   igual de bien. Solo una lo protege.
TXT

titulo "5. Devolviendo el codigo a su sitio"
restaurar
trap - EXIT INT TERM
HUELLA_FINAL="$(shasum -a 256 "$FUENTE" | cut -d' ' -f1)"
if [ "$HUELLA_FINAL" = "$HUELLA_ORIGINAL" ]; then
  verde "   $FUENTE identico al original (sha256 ${HUELLA_ORIGINAL:0:12}). Todo en orden."
else
  rojo  "   *** $FUENTE NO coincide con el original ***"
  rojo  "   esperado ${HUELLA_ORIGINAL:0:12}, hay ${HUELLA_FINAL:0:12}"
  exit 1
fi
