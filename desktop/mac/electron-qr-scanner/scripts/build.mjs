import { spawnSync } from "node:child_process";
import { copyFileSync } from "node:fs";

const target =
  process.argv[2] ??
  (process.arch === "arm64" ? "aarch64-apple-darwin" : "x86_64-apple-darwin");
const architectures = {
  "aarch64-apple-darwin": "arm64",
  "x86_64-apple-darwin": "x64",
};
if (!architectures[target]) throw new Error(`Unsupported target: ${target}`);
const build = spawnSync(
  "cargo",
  ["build", "--release", "--locked", "--target", target],
  { cwd: new URL("../", import.meta.url), stdio: "inherit" },
);
if (build.error) throw build.error;
if (build.status !== 0) process.exit(build.status ?? 1);
copyFileSync(
  new URL(`../target/${target}/release/libelectron_qr_scanner.dylib`, import.meta.url),
  new URL(`../electron-qr-scanner.darwin-${architectures[target]}.node`, import.meta.url),
);
