import { readFileSync } from "node:fs";

for (const [arch, machine] of [
  ["x64", 0x8664],
  ["arm64", 0xaa64],
]) {
  const binary = readFileSync(
    new URL(
      `../electron-windows-hello.win32-${arch}-msvc.node`,
      import.meta.url,
    ),
  );
  if (binary.readUInt16LE(0) !== 0x5a4d)
    throw new Error(`Invalid ${arch} Windows binary`);
  const header = binary.readUInt32LE(0x3c);
  if (
    binary.readUInt32LE(header) !== 0x4550 ||
    binary.readUInt16LE(header + 4) !== machine
  ) {
    throw new Error(`Wrong architecture for ${arch} binary`);
  }
}
for (const file of ["index.cjs", "index.d.ts", "README.md", "LICENSE"]) {
  readFileSync(new URL(`../${file}`, import.meta.url));
}
