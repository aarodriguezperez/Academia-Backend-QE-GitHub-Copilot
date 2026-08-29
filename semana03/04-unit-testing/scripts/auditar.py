#!/usr/bin/env python3
"""
Auditoria de coherencia del tema de testing.

Comprueba que las guias y el codigo sigan contandose la misma historia. Se
escribio despues de que el mismo hecho -- la latencia del proyecto Mono -- se
desincronizara DOS VECES el mismo dia entre el codigo, un script y una guia.
La leccion no fue "hay que fijarse mas": fue que un dato repetido a mano en
cuatro sitios se desincroniza solo, y que hace falta algo que lo compruebe.

    ./scripts/auditar.py            comprobaciones estaticas (rapido)
    ./scripts/auditar.py --tests    ademas corre las suites y compara cifras

Devuelve 0 si todo cuadra, 1 si algo no.

--------------------------------------------------------------------------
LO QUE **NO** COMPRUEBA, dicho en voz alta
--------------------------------------------------------------------------
Un verificador que calla lo que no mira es un fail-open con buena letra: el
verde se lee como "todo bien" cuando solo significa "todo lo que mire".
Esto NO se comprueba aqui:

  - Que las paginas RENDERICEN bien. El HTML puede estar equilibrado y verse
    roto. Eso se mira con un navegador.
  - Que el boton "Copiar" funcione. Depende del permiso del portapapeles.
  - Que los comandos que las guias mandan escribir realmente corran. Eso solo
    se sabe ejecutandolos.
  - Que la PROSA sea correcta. Si una guia explica mal un concepto, aqui sale
    verde. Ninguna herramienta sustituye leerlo.
  - Que los numeros SUELTOS del texto ("~2 s", "300 ms") cuadren con el codigo.
    Solo se comprueban los que aparecen en sitios reconocibles (etiquetas de
    la cabecera y bloques <version>). Ese es justo el hueco por el que se colo
    el incidente de la latencia: la unica defensa de verdad es que el dato
    viva en UN solo sitio y los demas lo lean, como hace ahora comparar.sh.
"""

import os
import re
import subprocess
import sys
import xml.etree.ElementTree as ET
from html.parser import HTMLParser

RAIZ = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
GUIAS = os.path.join(RAIZ, "guias")
PROYECTOS = os.path.join(RAIZ, "proyectos")

# --------------------------------------------------------------------------
# Mapa explicito guia -> URL publicada y proyecto que la acompana.
#
# Es una ALLOWLIST a proposito. Si aparece una guia nueva en disco que no este
# aqui, o falta una que si esta, el script FALLA pidiendo que lo actualices.
# Lo contrario -- ignorar lo que no reconoce -- convertiria esta herramienta
# en un sello de goma.
# --------------------------------------------------------------------------
GUIAS_CONOCIDAS = {
    "guia-00-por-que-se-prueba.html":    ("dc6ceb37-bac9-4a8c-bbd0-89f91dbc7def", None,                   ["prueba"]),
    "guia-01-anatomia-de-un-test.html":  ("6755acb6-a6c0-42ac-95dd-e2ffb84a3a71", "01-junit-fundamentos", ["anatom"]),
    "guia-02-el-catalogo.html":          ("b02ccd10-cb0b-4545-a130-a0dc29e6a26c", "02-junit-catalogo",    ["cat"]),
    "guia-03-un-test-muchos-datos.html": ("dec3fbf5-39f0-411c-8daa-02bb3dec3eb0", "03-junit-datos",       ["dato"]),
    "guia-04-dobles-de-prueba.html":     ("33074df8-6377-44cd-a3c6-68f766ff4b78", "04-mockito-dobles",    ["doble"]),
    "anexo-junit5-vs-junit6.html":       ("53f58e3c-f8ea-4c4c-a6a9-ae93e2b9ca9a", None,                   ["anexo", "5 contra", "junit 5", "migra"]),
}

VOID = {"meta", "link", "br", "hr", "img", "input", "source", "col",
        "area", "base", "embed", "param", "track", "wbr"}

fallos = []
avisos = []


def falla(msg):
    fallos.append(msg)


def titulo(t):
    print(f"\n\033[1;36m== {t} ==\033[0m")


def ok(msg):
    print(f"  \033[1;32mOK  \033[0m {msg}")


def mal(msg):
    print(f"  \033[1;31mMAL \033[0m {msg}")
    falla(msg)


def nota(msg):
    print(f"  \033[1;33m--  \033[0m {msg}")
    avisos.append(msg)


def sin_etiquetas(s):
    """Texto plano, quitando antes los data: URI para no arrastrar base64.

    No es cosmetico: un WebP incrustado hizo que un grep de esta misma
    auditoria diera un falso positivo enorme mientras se escribia.
    """
    s = re.sub(r'src="data:[^"]*"', 'src="[IMG]"', s)
    return re.sub(r"<[^>]+>", " ", s)


class Equilibrio(HTMLParser):
    def __init__(self):
        super().__init__(convert_charrefs=True)
        self.pila = []
        self.err = []

    def handle_starttag(self, t, a):
        if t not in VOID:
            self.pila.append((t, self.getpos()[0]))

    def handle_endtag(self, t):
        if t in VOID:
            return
        if not self.pila:
            self.err.append(f"cierre sobrante </{t}> linea {self.getpos()[0]}")
            return
        n, l = self.pila.pop()
        if n != t:
            self.err.append(f"linea {self.getpos()[0]}: </{t}> cierra <{n}> abierto en {l}")


def leer(f):
    with open(os.path.join(GUIAS, f), encoding="utf-8") as fh:
        return fh.read()


# ==========================================================================
def inventario():
    titulo("00 Inventario de guias")
    en_disco = {f for f in os.listdir(GUIAS) if f.endswith(".html")}
    conocidas = set(GUIAS_CONOCIDAS)
    for f in sorted(en_disco - conocidas):
        mal(f"{f}: existe en disco y NO esta en GUIAS_CONOCIDAS. Anadela al mapa "
            f"del script (con su URL publicada) o borrala.")
    for f in sorted(conocidas - en_disco):
        mal(f"{f}: esta en el mapa del script y NO existe en disco.")
    if en_disco == conocidas:
        ok(f"{len(en_disco)} guias, todas reconocidas")
    return sorted(en_disco & conocidas)


def html_equilibrado(guias):
    titulo("01 HTML equilibrado")
    for f in guias:
        v = Equilibrio()
        v.feed(leer(f))
        for n, l in v.pila:
            v.err.append(f"sin cerrar <{n}> abierto en linea {l}")
        if v.err:
            for e in v.err:
                mal(f"{f}: {e}")
        else:
            ok(f"{f}")


def anclas(guias):
    titulo("02 Anclas internas")
    for f in guias:
        s = leer(f)
        ids = set(re.findall(r'\bid="([^"]+)"', s))
        rotas = set(re.findall(r'href="#([^"]+)"', s)) - ids
        if rotas:
            mal(f"{f}: anclas sin destino -> {sorted(rotas)}")
        else:
            ok(f"{f}: {len(ids)} anclas, ninguna rota")


def enlaces(guias):
    titulo("03 Enlaces entre guias")
    uuid_de = {f: GUIAS_CONOCIDAS[f][0] for f in guias}
    for f in guias:
        s = leer(f)
        problemas = []

        # OJO AL SENTIDO DE ESTA COMPROBACION.
        #
        # Durante un tiempo fue justo al reves: se exigian URL de artifact y se
        # daban por rotos los relativos. Estaba mal, y de la peor manera: el
        # canal que de verdad usan los alumnos es el archivo HTML abierto con
        # doble clic, y ahi una URL de artifact los saca del material en el
        # primer clic -- a una pagina privada que ademas no pueden ver.
        #
        # Ahora: en el HTML van RELATIVOS, y un bloque de JS los reescribe a su
        # URL solo cuando la pagina corre publicada. Funciona en los dos sitios.
        citados = set(re.findall(r'href="((?:guia|anexo)-[^"]+\.html)"', s))

        fantasmas = sorted(d for d in citados if not os.path.exists(os.path.join(GUIAS, d)))
        if fantasmas:
            problemas.append(f"enlaza a archivos que no existen {fantasmas}")

        en_href = re.findall(r'href="https://claude\.ai/code/artifact/', s)
        if en_href:
            problemas.append(f"{len(en_href)} enlace(s) con URL de artifact en el href "
                             f"(rompen al abrir el HTML local: deben ser relativos)")

        if f in citados:
            problemas.append("se enlaza a si misma")
        faltan = {g for g in guias if g != f} - citados
        if faltan:
            problemas.append(f"no enlaza a {sorted(faltan)}")

        # el remapeo para cuando se publica tiene que estar, y cubrir a todas
        if "PUBLICADAS" not in s:
            problemas.append("le falta el bloque JS que reescribe los enlaces al publicarse")
        else:
            mapeadas = set(re.findall(r"'((?:guia|anexo)-[^']+\.html)':", s))
            sin_mapear = sorted(set(guias) - mapeadas)
            if sin_mapear:
                problemas.append(f"el remapeo no cubre {sin_mapear}")

        # el TEXTO del enlace debe corresponder con su destino
        for destino, texto in re.findall(
                r'href="((?:guia|anexo)-[^"]+\.html)"[^>]*>(.*?)</a>', s, re.S):
            if destino not in GUIAS_CONOCIDAS:
                continue
            plano = re.sub(r"<[^>]+>", "", texto).strip().lower()
            # Un enlace puede referirse a su destino de dos formas legitimas:
            #
            #   por REFERENCIA   "guia 02", "seccion 00 de la guia 04"  -> basta
            #   por NOMBRE       "El catalogo"                          -> tiene que cuadrar
            #
            # La primera version aceptaba el numero suelto como palabra clave, y
            # por eso dejaba pasar "02 Zanahorias". Ahora: si al quitar numeros y
            # muletillas queda algo que parece un NOMBRE, ese nombre tiene que
            # corresponder con el destino.
            MULETILLAS = r"gu[ií]a|secci[oó]n|anexo\b|de|la|el|los|en|y|ver|ir|del"
            resto = re.sub(MULETILLAS, "", plano)
            resto = re.sub(r"[^a-záéíóúñ]", "", resto)
            if resto and not any(k in plano for k in GUIAS_CONOCIDAS[destino][2]):
                problemas.append(f"el enlace '{plano[:40]}' apunta a {destino}, "
                                 f"pero su texto no lo nombra")

        if problemas:
            for p in problemas:
                mal(f"{f}: {p}")
        else:
            ok(f"{f}: {len(citados)} enlaces relativos + remapeo al publicar")


def numeracion(guias):
    titulo("04 Numeracion de secciones")
    for f in guias:
        nums = [int(n) for n in re.findall(
            r'<h2 class="sec"><span class="num">(\d+)</span>', leer(f))]
        if not nums:
            nota(f"{f}: sin secciones numeradas (no comprobado)")
            continue
        # correlativa desde SU propio inicio: el anexo empieza en 01, no en 00
        esperado = list(range(nums[0], nums[0] + len(nums)))
        if nums != esperado:
            mal(f"{f}: numeracion {nums}, se esperaba {esperado}")
        else:
            ok(f"{f}: {nums[0]:02d}-{nums[-1]:02d} correlativas")


def archivos_citados(guias):
    titulo("05 Los archivos que las guias nombran existen")
    reales = set()
    for base, _, ficheros in os.walk(PROYECTOS):
        if "target" in base.split(os.sep):
            continue
        reales |= set(ficheros)
    # inventados a proposito o de otros proyectos: no deben buscarse aqui
    EXENTOS = {"UsuarioServicio.java", "UsuarioRepositorio.java", "UsuarioDTO.java",
               "salida.txt", "evidencia.txt", "DatosDePrueba.java",
               "RepositorioAlumnosFake.java", "Aviso.java"}
    for f in guias:
        plano = sin_etiquetas(leer(f))
        citados = set(re.findall(
            r"\b([A-Z][A-Za-z0-9_]*\.java|[a-z][a-z0-9-]*\.(?:sh|csv|properties))\b", plano))
        faltan = sorted(citados - reales - EXENTOS)
        if faltan:
            mal(f"{f}: nombra archivos que no existen -> {faltan}")
        else:
            ok(f"{f}: {len(citados)} archivos citados, todos existen")


def versiones(guias):
    titulo("06 Versiones que muestran las guias contra los poms")
    reales = set()
    for p in sorted(os.listdir(PROYECTOS)):
        pom = os.path.join(PROYECTOS, p, "pom.xml")
        if os.path.exists(pom):
            reales |= set(re.findall(r"<[a-z-]+\.version>([^<]+)<", open(pom, encoding="utf-8").read()))
    if not reales:
        mal("no pude leer ninguna version de los poms")
        return
    print(f"       versiones declaradas en los poms: {sorted(reales)}")
    for f in guias:
        plano = sin_etiquetas(leer(f))
        mostradas = set(re.findall(r"&lt;version&gt;\s*([0-9][^&\s]*)\s*&lt;/version&gt;", plano))
        malas = sorted(mostradas - reales)
        if malas:
            mal(f"{f}: muestra en un <version> algo que ningun pom usa -> {malas}")
        elif mostradas:
            ok(f"{f}: {sorted(mostradas)}")
        else:
            ok(f"{f}: no muestra ningun <version>")


def cifras(guias, correr_tests):
    titulo("07 Cifras de la cabecera contra lo que reporta Surefire")
    for f in guias:
        proyecto = GUIAS_CONOCIDAS[f][1]
        if proyecto is None:
            continue
        s = leer(f)
        i = s.find('class="facts"')
        chips = re.findall(r"<li>([^<]+)</li>", s[i:s.find("</ul>", i)]) if i >= 0 else []
        dice_tests = next((int(m.group(1)) for c in chips
                           if (m := re.search(r"(\d+)\s*tests", c))), None)
        dice_clases = next((int(m.group(1)) for c in chips
                            if (m := re.search(r"(\d+)\s*clases", c))), None)
        dice_metodos = next((int(m.group(1)) for c in chips
                             if (m := re.search(r"(\d+)\s*m[eé]todos", c))), None)

        dir_p = os.path.join(PROYECTOS, proyecto)
        reportes = os.path.join(dir_p, "target", "surefire-reports")
        if correr_tests:
            subprocess.run(["./mvnw", "-B", "-q", "test"], cwd=dir_p,
                           stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
        if not os.path.isdir(reportes):
            nota(f"{f}: NO COMPROBADO -- no hay {proyecto}/target/surefire-reports. "
                 f"Corre `./mvnw test` en ese proyecto, o lanza esto con --tests.")
            continue

        # Se cuenta con los XML, no con los .txt.
        #
        # Y esto costo un susto: la primera version sumaba el "Tests run: N" de
        # cada .txt y daba 29 donde la guia decia 37. Parecia que la guia mentia.
        # Mentia el contador: el .txt de una clase con @Nested reporta 0, porque
        # solo cuenta los tests del nivel exterior. Los anidados solo salen en el
        # XML, que trae el total en el atributo tests= de <testsuite>.
        #
        # Moraleja, y va en serio: el verificador tambien hay que verificarlo.
        xmls = [x for x in os.listdir(reportes)
                if x.startswith("TEST-") and x.endswith(".xml")]
        total = 0
        for x in xmls:
            try:
                raiz = ET.parse(os.path.join(reportes, x)).getroot()
                total += int(raiz.get("tests", 0))
            except ET.ParseError:
                mal(f"{proyecto}: no pude leer {x}")
                return
        txts = xmls   # una clase de nivel superior por XML

        if dice_tests is not None:
            (ok if dice_tests == total else mal)(
                f"{f}: dice {dice_tests} tests, Surefire reporta {total}")
        if dice_clases is not None:
            (ok if dice_clases == len(txts) else mal)(
                f"{f}: dice {dice_clases} clases, Surefire reporta {len(txts)}")
        if dice_metodos is not None:
            src = os.path.join(dir_p, "src", "test", "java")
            n = 0
            for base, _, ficheros in os.walk(src):
                for x in ficheros:
                    if x.endswith(".java"):
                        n += len(re.findall(
                            r"^\s*@(?:Test|ParameterizedTest|RepeatedTest|TestFactory)\b",
                            open(os.path.join(base, x), encoding="utf-8").read(), re.M))
            (ok if dice_metodos == n else mal)(
                f"{f}: dice {dice_metodos} metodos, en el codigo hay {n}")


def scripts_ejecutables():
    titulo("08 Los .sh y los mvnw son ejecutables")
    for base, _, ficheros in os.walk(PROYECTOS):
        if "target" in base.split(os.sep):
            continue
        for x in sorted(ficheros):
            if x.endswith(".sh") or x == "mvnw":
                ruta = os.path.join(base, x)
                rel = os.path.relpath(ruta, RAIZ)
                if os.access(ruta, os.X_OK):
                    ok(rel)
                else:
                    mal(f"{rel}: sin permiso de ejecucion (chmod +x)")


# ==========================================================================
def main():
    correr_tests = "--tests" in sys.argv
    print(f"\033[1mAuditoria del tema de testing\033[0m   ({RAIZ})")
    if not correr_tests:
        print("  modo estatico. Con --tests ademas corre las suites.")

    guias = inventario()
    html_equilibrado(guias)
    anclas(guias)
    enlaces(guias)
    numeracion(guias)
    archivos_citados(guias)
    versiones(guias)
    cifras(guias, correr_tests)
    scripts_ejecutables()

    titulo("Resumen")
    if avisos:
        print(f"  \033[1;33m{len(avisos)} cosa(s) NO comprobadas:\033[0m")
        for a in avisos:
            print(f"     - {a}")
    if fallos:
        print(f"  \033[1;31m{len(fallos)} FALLO(S)\033[0m")
        return 1
    print("  \033[1;32mTodo cuadra.\033[0m")
    print("  Recuerda lo que esto NO mira: el renderizado, el boton Copiar, que los")
    print("  comandos corran, y si la prosa dice la verdad. Eso se lee, no se audita.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
