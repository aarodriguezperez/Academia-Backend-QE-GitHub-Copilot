# Originales de los pósters de la guía 00

Masters a resolución completa de las cuatro ilustraciones de
`guia-00-las-cuatro-ideas.html`. Los `.webp` de `../` derivan de estos.

| Original (aquí) | Derivado (`../`) |
|---|---|
| `fig-00-N-*.png` · 2048×1152 · ~1,6 MB | `fig-00-N-*.webp` · 1500×843 · ~60 KB |

Los PNG están recomprimidos sin pérdida (`optimize=True`): **los píxeles son
idénticos** a lo que devolvió el generador; solo pesan un 25 % menos.

## Cómo se generaron

- **Herramienta**: MCP `claude.ai higgsfield` → `generate_image_batch`
- **Modelo**: `z_image` (Z Image, de Tongyi-MAI), 16:9, salida 2048×1152 PNG

Dos limitaciones de ese modelo que explican el resultado:

1. **No rotula texto.** La primera prueba pidió bocadillos con «Zzz» y salieron
   vacíos. Por eso los prompts finales piden explícitamente *no letters, no
   words*, y los rótulos los ponen los pies de figura del HTML.
2. **No acepta paleta.** La paleta va solo descrita en el prompt, así que estos
   pósters no son idénticos en color a los de `17-seguridad-autenticacion`.

**Si algún día hay plan de pago en Higgsfield**, el modelo correcto es
`recraft_v4_1` con `model_type: "vector"`: hace ilustración vectorial, rotula
tipografía y acepta `colors` + `background_color`. Con él estas cuatro escenas
se regeneran con los rótulos dentro y con la paleta exacta de la serie de
seguridad: `#1B365D #F5F0E4 #F5A623 #D0342C #4A7C59 #A8BACB`.

Ojo también con el **rate limit**: enviando las cuatro de golpe, tres fallaron
con `429 rate_limit_reached`. Hay que mandarlas de una en una.

## Post-proceso (para regenerar los .webp)

```python
from PIL import Image
im = Image.open("fig-00-1-meseros-dormidos.png").convert("RGB")
im = im.resize((1500, int(im.height * 1500 / im.width)), Image.LANCZOS)
im.save("../fig-00-1-meseros-dormidos.webp", "WEBP", quality=84, method=6)
```

Después se incrustan en el HTML como data URI, para que la guía funcione como
archivo suelto sin depender de esta carpeta.

## Los prompts

Preámbulo común a los cuatro:

> Flat vector educational infographic, children's textbook style. Cream
> off-white background. Thick navy blue outlines, completely flat colors, no
> gradients, no shading. Palette: navy blue, cream, mustard yellow, terracotta,
> sage green[, brick red].

Y el cierre común:

> No speech bubbles. No letters, no words, no writing anywhere in the image.
> Clean flat cartoon vector, thick uniform outlines, simple geometric shapes.

**1 · meseros dormidos**
> A cartoon restaurant. On the RIGHT half, a kitchen counter with a chef behind
> it. Standing in a tight row directly in front of the counter, facing it: SIX
> identical cartoon waiters in navy uniforms with bow ties, each holding an
> empty round tray, all with CLOSED EYES, standing perfectly still, asleep on
> their feet. On the LEFT half, THREE small round tables, each with two cartoon
> customers sitting with arms raised waving for attention, nobody serving them.
> A large mustard yellow wall clock hangs top left.

**2 · el aviso**
> A cartoon restaurant working smoothly. On the RIGHT, a kitchen counter with a
> chef, and mounted above it a large mustard yellow HAND BELL tilted as if
> ringing, with three curved yellow arc lines radiating outward to show sound.
> In the CENTER, ONE single cartoon waiter in a navy uniform, eyes wide open and
> smiling, placing a plate of food onto a table for a seated customer, his head
> turned back over his shoulder toward the ringing bell. Around him, THREE other
> tables with relaxed, already-served cartoon customers eating happily.

**3 · se caen**
> A cartoon restaurant in chaos. On the RIGHT, a kitchen counter pushing out a
> fast stream of round plates of food in a horizontal line, each plate trailed
> by short straight speed lines. In the CENTER, ONE single cartoon waiter in a
> navy uniform with a panicked face and flying sweat drops, arms outstretched,
> balancing a very tall leaning stack of eight plates that wobbles sideways. On
> the FLOOR beneath him, FIVE plates lie SHATTERED into broken triangular
> shards, with small red star-burst lines around the breakages.

**4 · la alarma**
> A cartoon restaurant kitchen emergency. On the RIGHT, the kitchen counter with
> stylised flat red and orange FLAME shapes rising up behind it, and a round red
> alarm bell on the wall with red curved arc lines radiating to show it ringing.
> Walking away from the counter toward the LEFT, ONE cartoon waiter in a navy
> uniform holds up a flat serving tray, and standing upright on the tray is a
> single bright red rectangular CARD bearing one large white exclamation mark.
> On the far LEFT, a seated cartoon customer looks up at the approaching red
> card and raises one hand. No plates of food anywhere in the scene.

(En el 4 el cierre permite una excepción: *no letters or words anywhere except
the single exclamation mark on the red card*.)
