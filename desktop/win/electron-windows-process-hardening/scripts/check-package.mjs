import { readFileSync } from "node:fs";
import { createHash } from "node:crypto";
import { verifyCaptureReceipt } from "./capture-receipt.mjs";

const manifest = JSON.parse(readFileSync(new URL("../package.json", import.meta.url), "utf8"));
const runtimeFiles = [
  "index.cjs",
  "index.d.ts",
  "electron-windows-process-hardening.win32-x64-msvc.node",
  "electron-windows-process-hardening.win32-arm64-msvc.node",
  "onekey-crash-prototype.win32-x64-msvc.exe",
  "onekey-crash-prototype.win32-arm64-msvc.exe",
  "onekey-crashpad-capture.win32-x64.dll",
  "third-party-licenses",
];
if (manifest.private !== true || manifest.dependencies || manifest.optionalDependencies || manifest.install || manifest.scripts?.install) {
  throw new Error("Candidate must stay private, runtime dependency-free and install-script-free");
}
if (JSON.stringify(manifest.files) !== JSON.stringify(runtimeFiles)) {
  throw new Error("Unexpected runtime file boundary");
}
for (const [arch, machine] of [["x64", 0x8664], ["arm64", 0xaa64]]) {
  for (const name of [`electron-windows-process-hardening.win32-${arch}-msvc.node`, `onekey-crash-prototype.win32-${arch}-msvc.exe`]) {
    const binary = readFileSync(new URL(`../${name}`, import.meta.url));
    if (binary.readUInt16LE(0) !== 0x5a4d) throw new Error(`Invalid ${arch} Windows binary`);
    const header = binary.readUInt32LE(0x3c);
    if (binary.readUInt32LE(header) !== 0x4550 || binary.readUInt16LE(header + 4) !== machine) {
      throw new Error(`Wrong architecture for ${arch} binary`);
    }
  }
}
verifyCaptureReceipt(new URL("../onekey-crashpad-capture.win32-x64.dll", import.meta.url));
const buildReceipt = JSON.parse(readFileSync(new URL("../candidate-build.receipt.json", import.meta.url), "utf8"));
if (buildReceipt.schema !== "onekey-private-x64-candidate-build-v1" ||
    buildReceipt.crashCapable !== true || buildReceipt.target !== "x86_64-pc-windows-msvc" ||
    buildReceipt.electronHeadersArchiveSha256 !== "ba56f6db1f43baeda19c82d3f72beeb00e20c8ba87e787a763db60f44d212977") {
  throw new Error("Package has no verified crash-capable x64 build");
}
for (const [field, name] of [
  ["nodeSha256", "electron-windows-process-hardening.win32-x64-msvc.node"],
  ["helperSha256", "onekey-crash-prototype.win32-x64-msvc.exe"],
  ["captureSha256", "onekey-crashpad-capture.win32-x64.dll"],
  ["captureReceiptSha256", "onekey-crashpad-capture.win32-x64.dll.receipt.json"],
]) {
  const actual = createHash("sha256").update(readFileSync(new URL(`../${name}`, import.meta.url))).digest("hex");
  if (buildReceipt[field] !== actual) throw new Error(`Package binary changed after x64 build: ${name}`);
}
for (const file of ["NOTICE.txt", "Crashpad-LICENSE.txt", "mini_chromium-LICENSE.txt",
  "mini_chromium-icu-LICENSE.txt", "zlib-LICENSE.txt", "getopt-LICENSE.txt"]) {
  readFileSync(new URL("../third-party-licenses/" + file, import.meta.url));
}
for (const file of ["index.cjs", "index.d.ts", "README.md", "LICENSE", "SPEC.md"]) {
  readFileSync(new URL(`../${file}`, import.meta.url));
}
