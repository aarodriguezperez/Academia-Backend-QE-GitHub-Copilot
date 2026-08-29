# Fuentes para Gemini Notebook (NotebookLM)

Los cuatro `.md` de esta carpeta son el material del tema empaquetado para subirlo
como fuentes a Gemini Notebook (`notebook.google.com`).

## Por qué existen

Gemini Notebook **no acepta** `.java`, `.xml`, `.sh`, `.sql` ni `.properties`: solo
PDF, texto plano, Markdown, Google Docs, URLs y audio. Y aunque los aceptara, subir
45 archivos sueltos fragmenta el tema y empeora las respuestas.

Así que cada documento reúne una unidad con sentido propio — la guía y el código que
la implementa, juntos:

| Documento | Contenido |
|---|---|
| `00-setup-y-referencia.md` | instalación, SQL, mapa de puertos, el CRUD base, los 3 scripts de prueba, tabla comparativa de las tres etapas |
| `01-basic.md` | guía 01 + SecurityConfig + pom + properties |
| `02-jwt.md` | guía 02 + SecurityConfig + AuthController + pom + properties |
| `03-oauth2.md` | guía 03 + SecurityConfig + keycloak-setup.sh |

Dos decisiones de empaquetado, ambas medidas:

- Las guías HTML son **63% CSS y SVG**. Gemini Notebook solo indexa texto, así que se
  convierten a Markdown y se descarta el resto.
- **6 de las 7 clases Java son byte por byte idénticas** en los tres proyectos (vienen
  del proyecto 16 y no se tocaron). Van una sola vez, en el documento de referencia.
- Las llaves `.pem` **no se incluyen**: no aportan nada al índice y una llave privada
  no debe circular.

## El nombre de la fuente: por que empieza por la fecha

Gemini Notebook **no deja elegir el nombre al subir**: lo toma de la primera linea
del texto pegado. Y la barra lateral lo **corta a 50 caracteres**, sin tooltip que
muestre el resto. Medido, no supuesto.

Por eso el H1 de cada documento empieza por la fecha:

    # 2026-08-22 · Etapa 01 — HTTP Basic

Consecuencias practicas:

- Cualquier sufijo del tipo `(v2)` al final **seria invisible**. La version va delante
  o no va.
- Al subir una version nueva junto a la vieja, las dos se distinguen de un vistazo y
  ordenadas alfabeticamente quedan agrupadas por dia.
- Se usa la fecha y no `v2` porque `v2` obliga a alguien a llevar la cuenta. La fecha
  se pone sola y ademas dice si el material lleva meses sin actualizarse.
- El detalle del proyecto y el puerto bajo al primer parrafo del cuerpo: ahi no
  estorba y Gemini Notebook lo indexa igual.

Si necesitas cambiar el nombre de una fuente **ya subida**, se puede desde la UI:
menu de tres puntos de la fuente → *Rename source*. Eso solo cambia la etiqueta; no
toca el contenido ni los Audio Overviews que cuelguen de ella.

**Ojo con la reproducibilidad:** por el sello de fecha, regenerar en un dia distinto
cambia el H1 aunque no haya cambiado nada del material. Es intencionado — el documento
deja constancia de cuando se construyo — pero significa que `generar.sh` solo produce
salida identica byte a byte **dentro del mismo dia**.

## SON ARCHIVOS DERIVADOS

No los edites a mano. Si cambias una guía, un `SecurityConfig` o el `instalacion.txt`,
regenéralos:

    ./generar.sh

Y vuelve a subirlos al notebook.

## Cómo subirlos

Gemini Notebook usa el selector de archivos nativo del sistema, así que la subida no se
puede automatizar desde el navegador. La vía que funciona es pegar el contenido:

1. `pbcopy < 01-basic.md`   (macOS; en Linux: `xclip -sel clip < 01-basic.md`)
2. En el notebook: **Add sources → Copied text**
3. Cmd+V (o Ctrl+V) en el cuadro de texto → **Insert**
4. Repetir con cada documento

El notebook se pone el título solo a partir del primer documento.
