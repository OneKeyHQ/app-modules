import { spawnSync } from "node:child_process";
import { copyFileSync } from "node:fs";
import { fileURLToPath } from "node:url";

const root = fileURLToPath(new URL("../", import.meta.url));
const target =
  process.argv[2] ??
  (process.arch === "arm64"
    ? "aarch64-pc-windows-msvc"
    : "x86_64-pc-windows-msvc");
const architectures = {
  "x86_64-pc-windows-msvc": "x64",
  "aarch64-pc-windows-msvc": "arm64",
};
if (!architectures[target]) throw new Error(`Unsupported target: ${target}`);
const build = spawnSync(
  "cargo",
  ["build", "--release", "--locked", "--target", target],
  {
    cwd: root,
    stdio: "inherit",
  },
);
if (build.error) throw build.error;
if (build.status !== 0) process.exit(build.status ?? 1);
copyFileSync(
  new URL(
    `../target/${target}/release/electron_windows_hello.dll`,
    import.meta.url,
  ),
  new URL(
    `../electron-windows-hello.win32-${architectures[target]}-msvc.node`,
    import.meta.url,
  ),
);
