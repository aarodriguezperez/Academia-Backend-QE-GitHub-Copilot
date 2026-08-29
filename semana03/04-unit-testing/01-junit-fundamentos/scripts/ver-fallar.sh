#!/usr/bin/env bash
# ---------------------------------------------------------------------------
#  Un test que nunca has visto fallar no sabes si funciona.
#
#  Este script rompe UN caracter de Boleta.aprobado() -- el >= pasa a ser > --
#  corre la suite entera, te ensena el rojo, y deja el codigo como estaba.
#
#  Lo que vas a aprender: casi todos los tests siguen en verde con el bug
#  vivo. Solo caen los que se pararon justo encima del limite.
# ---------------------------------------------------------------------------
set -uo pipefail
cd "$(dirname "$0")/.."

FUENTE="src/main/java/com/academymty/academia/Boleta.java"
RESPALDO="$(mktemp)"
MVN="./mvnw"; [ -x "$MVN" ] || MVN="mvn"

titulo() { printf '\n\033[1;36m== %s ==\033[0m\n' "$1"; }
rojo()   { printf '\033[1;31m%s\033[0m\n' "$1"; }
verde()  { printf '\033[1;32m%s\033[0m\n' "$1"; }

restaurar() {
  cp "$RESPALDO" "$FUENTE"
  rm -f "$RESPALDO"
}
trap restaurar EXIT INT TERM     # pase lo que pase, el codigo queda intacto

# Guarda: si una corrida anterior murio sin restaurar (kill -9, disco lleno),
# el archivo todavia tiene el bug dentro. Sin esta comprobacion, esta corrida
# lo tomaria como "el original" y reportaria que todo esta bien -- dejando el
# codigo roto para siempre y con el script diciendo que no pasa nada.
if grep -q "BUG INYECTADO" "$FUENTE"; then
  rojo "   $FUENTE todavia tiene el bug de una corrida anterior."
  echo "   Arreglalo antes de seguir: cambia el '>' por '>=' en aprobado(),"
  echo "   o recuperalo con  git checkout -- $FUENTE"
  exit 1
fi

cp "$FUENTE" "$RESPALDO"
HUELLA_ORIGINAL="$(shasum -a 256 "$FUENTE" | cut -d' ' -f1)"

titulo "1. La suite tal y como esta en el repositorio"
$MVN -q test 2>/dev/null >/dev/null && verde "   BUILD SUCCESS -- los 22 tests en verde." \
                                    || rojo  "   Algo ya venia roto antes de empezar."

titulo "2. Metemos el bug: 'promedio() >= 70' pasa a 'promedio() > 70'"
sed -i.bak 's/return promedio() >= MINIMA_APROBATORIA;/return promedio() > MINIMA_APROBATORIA;   \/\/ BUG INYECTADO/' "$FUENTE"
rm -f "$FUENTE.bak"
grep -n "MINIMA_APROBATORIA;" "$FUENTE" | sed 's/^/   /'
echo "   Un caracter. El alumno que saca exactamente 70 ahora reprueba."

titulo "3. La suite con el bug dentro"
SALIDA="$($MVN test 2>&1)"
echo "$SALIDA" | grep -E "^\[ERROR\]   [A-Za-z]" | sed 's/^/   /'
RESUMEN="$(echo "$SALIDA" | grep -E "Tests run: .*Failures" | tail -1)"
echo
rojo "   $RESUMEN"

titulo "4. Lee bien ese numero"
cat <<'TXT'
   20 de 22 tests SIGUIERON EN VERDE con el bug dentro.

   Los que probaban 90 aprueba y 50 reprueba no se enteraron de nada:
   estaban lejos de la frontera, y el bug solo vive EN la frontera.

   Los dos que cayeron son los que se pararon justo encima del 70.
   Esos son los que valen. Esa es la leccion del proyecto 01:

       prueba el limite, el limite menos uno, el limite mas uno,
       el vacio y el nulo.

   Cobertura alta no es lo mismo que estar protegido.
TXT

titulo "5. Devolviendo el codigo a su sitio"
restaurar
trap - EXIT INT TERM

# Se compara el CONTENIDO, no lo que diga git.
#
# La version anterior de este script preguntaba 'git diff --quiet'. Parecia
# razonable y era un fail-open: si el archivo esta sin trackear -- y lo esta,
# hasta que alguien haga el primer commit -- git responde "sin cambios" pase
# lo que pase, incluso si el archivo quedo destrozado. Una comprobacion que
# no puede fallar nunca no comprueba nada.
HUELLA_FINAL="$(shasum -a 256 "$FUENTE" | cut -d' ' -f1)"
if [ "$HUELLA_FINAL" = "$HUELLA_ORIGINAL" ]; then
  verde "   $FUENTE identico al original (sha256 ${HUELLA_ORIGINAL:0:12}). Todo en orden."
else
  rojo  "   *** $FUENTE NO coincide con el original ***"
  rojo  "   esperado ${HUELLA_ORIGINAL:0:12}, hay ${HUELLA_FINAL:0:12}"
  rojo  "   Recupera el archivo antes de seguir."
  exit 1
fi
