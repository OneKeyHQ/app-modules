"""Combine the iOS, Android and Web gallery renders into one comparison image.

Usage: python3 compose.py <dir with ios.png, android.png, web-base.png> <out.png>
Each input is 2x (736 px wide); every row is 72 pt tall. Requires Pillow.
"""

import json
from pathlib import Path
import sys

from PIL import Image, ImageDraw, ImageFont

src = Path(sys.argv[1])
out = Path(sys.argv[2])
spec = json.loads((Path(__file__).parent / "scenarios.json").read_text())
scale = 2
row_h = spec["rowHeight"] * scale

web = Image.open(src / "web-base.png").convert("RGBA")
pressed = json.loads((src / "web-pressed.json").read_text())["pressed"]
for item in pressed:
    web.paste(Image.open(item["file"]).convert("RGBA"), (0, item["index"] * row_h))

columns = [
    ("iOS", Image.open(src / "ios.png").convert("RGBA")),
    ("Android", Image.open(src / "android.png").convert("RGBA")),
    ("Web", web),
]
col_w = columns[0][1].width
rows = len(spec["rows"])
label_w = 420
header_h = 72
gap = 16
width = label_w + (col_w + gap) * len(columns)
height = header_h + rows * row_h
canvas = Image.new("RGBA", (width, height), "#F2F2F2")
draw = ImageDraw.Draw(canvas)


def font(size):
    for name in ["/System/Library/Fonts/Helvetica.ttc", "/System/Library/Fonts/SFNS.ttf"]:
        try:
            return ImageFont.truetype(name, size)
        except OSError:
            pass
    return ImageFont.load_default()


head, body = font(32), font(26)
for i, (name, image) in enumerate(columns):
    x = label_w + i * (col_w + gap)
    draw.text((x + col_w / 2, header_h / 2), name, fill="#111111", font=head, anchor="mm")
    canvas.paste(image.crop((0, 0, col_w, rows * row_h)), (x, header_h))
for r, row in enumerate(spec["rows"]):
    y = header_h + r * row_h + spec["sliderTop"] * scale + 24
    draw.text((24, y), row["label"], fill="#333333", font=body, anchor="lm")
canvas.convert("RGB").save(out)
print(f"wrote {out} ({width}x{height})")
