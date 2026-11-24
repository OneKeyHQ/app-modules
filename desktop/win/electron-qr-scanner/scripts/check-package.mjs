import { existsSync, readFileSync } from "node:fs";

const requireAll = process.argv.includes("--all");
const targets = [
  ["x64", 0x8664],
  ["arm64", 0xaa64],
];

let checkedBinaries = 0;
for (const [arch, machine] of targets) {
  const url = new URL(
    `../electron-qr-scanner.win32-${arch}-msvc.node`,
    import.meta.url,
  );
  if (!existsSync(url)) {
    if (requireAll || arch === process.arch) {
      throw new Error(`Missing ${arch} Windows binary`);
    }
    continue;
  }
  const binary = readFileSync(url);
  if (binary.readUInt16LE(0) !== 0x5a4d) {
    throw new Error(`Invalid ${arch} Windows binary`);
  }
  const header = binary.readUInt32LE(0x3c);
  if (
    binary.readUInt32LE(header) !== 0x4550 ||
    binary.readUInt16LE(header + 4) !== machine
  ) {
    throw new Error(`Wrong architecture for ${arch} binary`);
  }
  checkedBinaries += 1;
}
if (checkedBinaries === 0) {
  throw new Error("No Windows binary found to validate");
}

for (const file of ["index.cjs", "index.d.ts", "README.md", "LICENSE", "SPEC.md"]) {
  readFileSync(new URL(`../${file}`, import.meta.url));
}
