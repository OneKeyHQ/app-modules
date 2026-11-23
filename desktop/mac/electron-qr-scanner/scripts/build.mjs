import { spawnSync } from "node:child_process";
import { copyFileSync } from "node:fs";
import { fileURLToPath } from "node:url";

const target =
  process.argv[2] ??
  (process.arch === "arm64" ? "aarch64-apple-darwin" : "x86_64-apple-darwin");
const architectures = {
  "aarch64-apple-darwin": "arm64",
  "x86_64-apple-darwin": "x64",
};
if (!architectures[target]) throw new Error(`Unsupported target: ${target}`);

function run(command, args, options = {}) {
  const result = spawnSync(command, args, { stdio: "inherit", ...options });
  if (result.error) throw result.error;
  if (result.status !== 0) process.exit(result.status ?? 1);
  return result;
}

run("cargo", ["build", "--release", "--locked", "--target", target], {
  cwd: new URL("../", import.meta.url),
});
const output = fileURLToPath(
  new URL(`../electron-qr-scanner.darwin-${architectures[target]}.node`, import.meta.url),
);
copyFileSync(
  new URL(`../target/${target}/release/libelectron_qr_scanner.dylib`, import.meta.url),
  output,
);
// Strip local symbols with Xcode's strip (see Cargo.toml) and make sure the
// result is loadable: dyld on macOS 27 rejects a misaligned string table.
run("xcrun", ["strip", "-x", output]);
const loadCommands = spawnSync("xcrun", ["otool", "-l", output], { encoding: "utf8" }).stdout;
const stroff = Number(/cmd LC_SYMTAB[\s\S]*?stroff (\d+)/.exec(loadCommands)?.[1]);
if (!Number.isInteger(stroff) || stroff % 8 !== 0) {
  throw new Error(`${output}: LC_SYMTAB string table offset ${stroff} is not 8-byte aligned`);
}
