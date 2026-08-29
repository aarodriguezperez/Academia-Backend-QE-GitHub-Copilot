"""Extrae el texto de las guias HTML conservando la estructura y el codigo."""
import re, html as H

def convertir(path):
    s = open(path, encoding='utf-8').read()
    s = re.sub(r'<button class="copy".*?</button>', '', s, flags=re.S)
    s = re.sub(r'<style.*?</style>', '', s, flags=re.S)
    s = re.sub(r'<script.*?</script>', '', s, flags=re.S)
    s = re.sub(r'<nav class="toc".*?</nav>', '', s, flags=re.S)
    # los diagramas SVG: conservar solo el texto de sus etiquetas
    def svg(m):
        t = re.findall(r'<text[^>]*>(.*?)</text>', m.group(0), flags=re.S)
        t = [H.unescape(re.sub(r'<[^>]+>', '', x)).strip() for x in t]
        t = [x for x in t if x]
        return '\n\n> **Diagrama:** ' + ' · '.join(t) + '\n\n' if t else ''
    s = re.sub(r'<svg.*?</svg>', svg, s, flags=re.S)

    # las imagenes se pierden al indexar; su texto alternativo NO: es la descripcion
    def img(m):
        alt = re.search(r'alt="([^"]*)"', m.group(0), flags=re.S)
        if not alt: return ''
        t = H.unescape(re.sub(r'\s+', ' ', alt.group(1))).strip()
        return f'\n\n> **Imagen:** {t}\n\n'
    s2 = re.sub(r'<img[^>]*>', img, s)
    s = s2
    s = re.sub(r'<figcaption[^>]*>', '\n\n', s)

    # bloques de terminal / codigo -> bloque markdown
    def term(m):
        cuerpo = m.group(1)
        cuerpo = re.sub(r'<[^>]+>', '', cuerpo)
        # NO se desescapa aqui: si &lt;String&gt; se convierte ahora en <String>,
        # el barrido global de etiquetas de mas abajo se lo come y los genericos
        # de Java desaparecen (Map<String, Object> -> Map). Se deja escapado y
        # lo resuelve el H.unescape() final, que corre DESPUES del barrido.
        return '\n```\n' + cuerpo.strip() + '\n```\n'
    s = re.sub(r'<pre><code>(.*?)</code></pre>', term, s, flags=re.S)

    # tablas -> markdown
    def tabla(m):
        filas = re.findall(r'<tr>(.*?)</tr>', m.group(0), flags=re.S)
        out, primera = [], True
        for f in filas:
            celdas = re.findall(r'<t[hd][^>]*>(.*?)</t[hd]>', f, flags=re.S)
            celdas = [H.unescape(re.sub(r'<[^>]+>', ' ', c)).strip() for c in celdas]
            celdas = [re.sub(r'\s+', ' ', c) for c in celdas]
            if not celdas: continue
            out.append('| ' + ' | '.join(celdas) + ' |')
            if primera:
                out.append('|' + '---|' * len(celdas)); primera = False
        return '\n' + '\n'.join(out) + '\n'
    s = re.sub(r'<table>.*?</table>', tabla, s, flags=re.S)

    # titulos
    s = re.sub(r'<h1[^>]*>(.*?)</h1>', lambda m: '\n# ' + re.sub(r'<[^>]+>','',m.group(1)).strip() + '\n', s, flags=re.S)
    s = re.sub(r'<h2 class="sec"[^>]*>(.*?)</h2>', lambda m: '\n## ' + re.sub(r'\s+',' ',re.sub(r'<[^>]+>',' ',m.group(1))).strip() + '\n', s, flags=re.S)
    s = re.sub(r'<h2[^>]*>(.*?)</h2>', lambda m: '\n## ' + re.sub(r'<[^>]+>','',m.group(1)).strip() + '\n', s, flags=re.S)
    s = re.sub(r'<h3[^>]*>(.*?)</h3>', lambda m: '\n### ' + re.sub(r'<[^>]+>','',m.group(1)).strip() + '\n', s, flags=re.S)
    s = re.sub(r'<li[^>]*>', '\n- ', s)
    s = re.sub(r'<p class="note warn"[^>]*>', '\n> ATENCION: ', s)
    s = re.sub(r'<p class="note"[^>]*>', '\n> NOTA: ', s)
    s = re.sub(r'<p class="rubric"[^>]*>.*?</p>', '', s, flags=re.S)
    s = re.sub(r'<p[^>]*>', '\n\n', s)
    def code_inline(m):
        c = m.group(1)
        # si el propio codigo lleva un acento grave, delimitarlo con uno solo
        # produce ``` y Markdown lo lee como apertura de bloque. Se usan dos.
        return '`` ' + c + ' ``' if '`' in c else '`' + c + '`'
    s = re.sub(r'<code>(.*?)</code>', code_inline, s, flags=re.S)
    s = re.sub(r'<(strong|b)>(.*?)</\1>', r'**\2**', s, flags=re.S)
    s = re.sub(r'<(em|i)>(.*?)</\1>', r'*\2*', s, flags=re.S)
    s = re.sub(r'<[^>]+>', '', s)
    s = H.unescape(s)
    # el HTML viene indentado; 4 espacios al inicio de linea significan "bloque de
    # codigo" en Markdown. Se limpian fuera de los bloques ``` (dentro, la sangria
    # del Java es significativa y no se toca).
    dentro, salida = False, []
    for l in s.split('\n'):
        if l.strip().startswith('```'):
            salida.append(l.strip())        # la marca, siempre sola en su linea
            dentro = not dentro
        elif dentro:
            salida.append(l)                # dentro del bloque la sangria es codigo
        else:
            salida.append(l.lstrip())
    s = '\n'.join(salida)

    s = re.sub(r'\n{3,}', '\n\n', s)
    s = re.sub(r'\n\s*\n(```)', r'\n\n\1', s)
    s = re.sub(r'[ \t]+\n', '\n', s)
    return s.strip()

if __name__ == '__main__':
    import sys; print(convertir(sys.argv[1]))
