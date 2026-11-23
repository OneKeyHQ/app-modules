// Regenerates test/fixtures/animated-ur.mp4 and animated-ur.json: a
// deterministic 2,003-byte crypto-psbt UR as a looping animated QR at 8 fps.
//
// Needs ffmpeg and the `qrcode` package, which this repository does not
// install; pass a node_modules directory that has it:
//   node scripts/make-ur-fixture.mjs /path/to/node_modules
import { spawnSync } from "node:child_process";
import { mkdtempSync, rmSync, writeFileSync } from "node:fs";
import { createRequire } from "node:module";
import { tmpdir } from "node:os";
import { join } from "node:path";
import { fileURLToPath } from "node:url";

const nodeModules = process.argv[2];
if (!nodeModules) throw new Error("usage: make-ur-fixture.mjs <node_modules with qrcode>");
const QR = createRequire(join(nodeModules, "noop.js"))("qrcode");
const { UR, UREncoder } = createRequire(import.meta.url)("@ngraveio/bc-ur");

const length = 2000;
const body = Buffer.alloc(length);
let seed = 7;
for (let i = 0; i < length; i += 1) {
  seed = (Math.imul(seed, 1103515245) + 12345) >>> 0;
  body[i] = seed >>> 24;
}
const cbor = Buffer.concat([Buffer.from([0x59, length >> 8, length & 0xff]), body]);
let fnv = 0x811c9dc5;
for (const byte of cbor) fnv = Math.imul(fnv ^ byte, 0x01000193) >>> 0;

const encoder = new UREncoder(new UR(cbor, "crypto-psbt"), 120);
const fragments = encoder.fragmentsLength;
// Three passes: originals plus fountain mixes, like a device loop.
const parts = Array.from({ length: fragments * 3 }, () => encoder.nextPart().toUpperCase());

const frames = mkdtempSync(join(tmpdir(), "ur-frames-"));
try {
  for (const [index, part] of parts.entries()) {
    await QR.toFile(join(frames, `f${String(index).padStart(3, "0")}.png`), part, {
      errorCorrectionLevel: "L",
      margin: 4,
      width: 720,
    });
  }
  const out = fileURLToPath(new URL("../test/fixtures/", import.meta.url));
  const ffmpeg = spawnSync(
    "ffmpeg",
    [
      "-y", "-loglevel", "error",
      "-framerate", "8",
      "-i", join(frames, "f%03d.png"),
      "-c:v", "libx264", "-pix_fmt", "yuv420p", "-crf", "18",
      join(out, "animated-ur.mp4"),
    ],
    { stdio: "inherit" },
  );
  if (ffmpeg.status !== 0) throw new Error("ffmpeg failed");
  writeFileSync(
    join(out, "animated-ur.json"),
    `${JSON.stringify({ type: "crypto-psbt", bytes: cbor.length, fnv1a: fnv.toString(16).padStart(8, "0"), fragments, frames: parts.length, fps: 8 }, null, 2)}\n`,
  );
} finally {
  rmSync(frames, { recursive: true, force: true });
}
