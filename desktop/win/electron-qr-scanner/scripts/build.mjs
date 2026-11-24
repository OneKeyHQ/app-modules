import { spawnSync } from "node:child_process";
import { copyFileSync, existsSync } from "node:fs";
import { tmpdir } from "node:os";
import { delimiter, join } from "node:path";
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

// Ensure VS-bundled CMake and rustup cargo bin are discoverable when invoked
// from a plain shell.
const extraPathDirs = [
  process.env.USERPROFILE && join(process.env.USERPROFILE, ".cargo", "bin"),
  "C:\\Program Files\\CMake\\bin",
  "C:\\Program Files\\Microsoft Visual Studio\\2022\\Enterprise\\Common7\\IDE\\CommonExtensions\\Microsoft\\CMake\\CMake\\bin",
  "C:\\Program Files\\Microsoft Visual Studio\\2022\\Professional\\Common7\\IDE\\CommonExtensions\\Microsoft\\CMake\\CMake\\bin",
  "C:\\Program Files\\Microsoft Visual Studio\\2022\\Community\\Common7\\IDE\\CommonExtensions\\Microsoft\\CMake\\CMake\\bin",
  "C:\\Program Files (x86)\\Microsoft Visual Studio\\2022\\BuildTools\\Common7\\IDE\\CommonExtensions\\Microsoft\\CMake\\CMake\\bin",
].filter((dir) => dir && existsSync(dir));

const env = { ...process.env };
const currentPath = env.PATH ?? env.Path ?? "";
const missing = extraPathDirs.filter(
  (dir) => !currentPath.toLowerCase().includes(dir.toLowerCase()),
);
if (missing.length > 0) {
  env.PATH = [...missing, currentPath].filter(Boolean).join(delimiter);
}

// MSBuild FileTracker hits Windows' 260-char MAX_PATH when CMake builds
// zxing-cpp inside deeply nested worktree paths; shorten CARGO_TARGET_DIR
// automatically when needed.
const defaultTargetDir = join(root, "target");
const targetDir =
  env.CARGO_TARGET_DIR ||
  (defaultTargetDir.length > 70 ? join(tmpdir(), "oqs-win-target") : defaultTargetDir);
env.CARGO_TARGET_DIR = targetDir;

const args = ["build", "--release", "--target", target];
if (existsSync(join(root, "Cargo.lock"))) {
  args.push("--locked");
}
const build = spawnSync("cargo", args, {
  cwd: root,
  env,
  stdio: "inherit",
});
if (build.error) throw build.error;
if (build.status !== 0) process.exit(build.status ?? 1);

copyFileSync(
  join(targetDir, target, "release", "electron_qr_scanner.dll"),
  new URL(
    `../electron-qr-scanner.win32-${architectures[target]}-msvc.node`,
    import.meta.url,
  ),
);
