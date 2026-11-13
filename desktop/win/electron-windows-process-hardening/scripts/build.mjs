import { spawnSync } from "node:child_process";
import { createHash } from "node:crypto";
import { copyFileSync, existsSync, mkdirSync, readFileSync, writeFileSync } from "node:fs";
import { join, resolve } from "node:path";
import { fileURLToPath } from "node:url";
import { verifyCaptureReceipt } from "./capture-receipt.mjs";

const root = fileURLToPath(new URL("../", import.meta.url));
const target = process.argv[2] ??
  (process.arch === "arm64" ? "aarch64-pc-windows-msvc" : "x86_64-pc-windows-msvc");
const architectures = {
  "x86_64-pc-windows-msvc": "x64",
  "aarch64-pc-windows-msvc": "arm64",
};
if (!architectures[target]) throw new Error(`Unsupported target: ${target}`);
const buildEnv = { ...process.env };
const captureSource = process.env.ONEKEY_CRASHPAD_CAPTURE_DLL;
let captureReceiptPath;
let verifiedHeadersSha256;
if (captureSource) {
  if (target !== "x86_64-pc-windows-msvc") {
    throw new Error("The local Crashpad prototype supports Windows x64 only");
  }
  captureReceiptPath = verifyCaptureReceipt(resolve(captureSource)).receiptPath;
  const tools = join(root, ".build-tools", "electron-44.5.1");
  mkdirSync(tools, { recursive: true });
  const archive = join(tools, "node-v44.5.1-headers.tar.gz");
  if (!existsSync(archive)) {
    const response = await fetch(
      "https://artifacts.electronjs.org/headers/dist/v44.5.1/node-v44.5.1-headers.tar.gz",
      { signal: AbortSignal.timeout(60000) },
    );
    if (!response.ok) throw new Error("Electron headers download failed: " + response.status);
    writeFileSync(archive, Buffer.from(await response.arrayBuffer()));
  }
  const archiveDigest = createHash("sha256").update(readFileSync(archive)).digest("hex");
  if (archiveDigest !== "ba56f6db1f43baeda19c82d3f72beeb00e20c8ba87e787a763db60f44d212977") {
    throw new Error("Electron 44.5.1 headers archive checksum mismatch");
  }
  verifiedHeadersSha256 = archiveDigest;
  const extract = spawnSync("tar", ["-xzf", archive, "-C", tools], { stdio: "inherit" });
  if (extract.error) throw extract.error;
  if (extract.status !== 0) throw new Error("Electron headers extraction failed");
  buildEnv.ELECTRON_44_HEADERS_DIR = join(tools, "node_headers", "include", "node");
}
const build = spawnSync("cargo", ["build", "--release", "--locked", "--target", target], {
  cwd: root,
  env: buildEnv,
  stdio: "inherit",
});
if (build.error) throw build.error;
if (build.status !== 0) process.exit(build.status ?? 1);
const cargoTarget = resolve(root, buildEnv.CARGO_TARGET_DIR ?? "target");
if (captureSource) {
  copyFileSync(resolve(captureSource), join(root, "onekey-crashpad-capture.win32-x64.dll"));
  copyFileSync(captureReceiptPath, join(root, "onekey-crashpad-capture.win32-x64.dll.receipt.json"));
}
copyFileSync(
  join(cargoTarget, target, "release", "electron_windows_process_hardening.dll"),
  new URL(`../electron-windows-process-hardening.win32-${architectures[target]}-msvc.node`, import.meta.url),
);
copyFileSync(
  join(cargoTarget, target, "release", "onekey-crash-prototype.exe"),
  new URL(`../onekey-crash-prototype.win32-${architectures[target]}-msvc.exe`, import.meta.url),
);
if (target === "x86_64-pc-windows-msvc") {
  const hashFile = (name) => createHash("sha256").update(readFileSync(join(root, name))).digest("hex");
  const receipt = {
    schema: "onekey-private-x64-candidate-build-v1",
    crashCapable: Boolean(captureSource),
    target,
    electronHeadersArchiveSha256: verifiedHeadersSha256 ?? null,
    nodeSha256: hashFile("electron-windows-process-hardening.win32-x64-msvc.node"),
    helperSha256: hashFile("onekey-crash-prototype.win32-x64-msvc.exe"),
    captureSha256: captureSource ? hashFile("onekey-crashpad-capture.win32-x64.dll") : null,
    captureReceiptSha256: captureSource ? hashFile("onekey-crashpad-capture.win32-x64.dll.receipt.json") : null,
  };
  writeFileSync(join(root, "candidate-build.receipt.json"), JSON.stringify(receipt, null, 2) + "\n");
}
