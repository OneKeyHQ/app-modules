import { createHash } from "node:crypto";
import { readFileSync } from "node:fs";
import { join } from "node:path";
import { fileURLToPath } from "node:url";

const root = fileURLToPath(new URL("../", import.meta.url));
const sha256 = (bytes) => createHash("sha256").update(bytes).digest("hex");
const fixed = {
  schema: "onekey-fault-only-capture-build-v1",
  crashpadRevision: "ad1827ddbc03f1c214030b31b380243ded660b95",
  crashpadArchiveSha256: "1a6d74bc7f4ca57fe6a4fc1ae47486477179d71d1b1b556e8f919a5149b0565d",
  depsSha256: "e0c861f45590a190bfdb1281e40632ca0b088c8d33df946b0b62d160f2ae4bcc",
  toolSha256: {
    gn: "fa111680baf435088415ebedf4cdd41d07db8651c482d944c841e0f7d46ca580",
    clang: "b79edbeef7a88a63ae8b76957baabf95250fdc4b646fa1a8ae4ab12d7ecdd0b6",
  },
  dependencyTreeSha256: {
    miniChromium: "8b15dfbba38479eab934db856192cb64b4b3aa58310bad840cc3f1c1c87f22a7",
    zlib: "8be7493a713bdddd690b48906212ad6d8c8ae4eff377d0fcc980949340b7515a",
    clang: "16f227a10626977ff66d66658a4df92b26b7686d1a7a6f02a12f286b6bdfd8fe",
    buildtools: "96df3d27e93e577704734d4fd396d00b8507f0a7d3c6836c68948597570a1dde",
  },
  buildArgs: 'target_cpu="x64" is_debug=false; root-target=//onekey_wrapper:all; ninja=onekey_capture_test',
};

export function verifyCaptureReceipt(capturePath) {
  if (capturePath instanceof URL) capturePath = fileURLToPath(capturePath);
  const capture = readFileSync(capturePath);
  const receiptPath = `${capturePath}.receipt.json`;
  const receipt = JSON.parse(readFileSync(receiptPath, "utf8"));
  for (const name of ["schema", "crashpadRevision", "crashpadArchiveSha256", "depsSha256", "buildArgs"]) {
    if (receipt[name] !== fixed[name]) throw new Error(`Unexpected capture receipt ${name}`);
  }
  for (const name of ["gn", "clang"]) {
    if (receipt.toolSha256?.[name] !== fixed.toolSha256[name]) {
      throw new Error(`Unexpected capture tool ${name}`);
    }
  }
  for (const [name, expected] of Object.entries(fixed.dependencyTreeSha256)) {
    if (receipt.dependencyTreeSha256?.[name] !== expected) {
      throw new Error(`Unexpected capture dependency tree: ${name}`);
    }
  }
  for (const name of ["ninja", "python", "clang"]) {
    if (typeof receipt.toolVersion?.[name] !== "string" || !receipt.toolVersion[name]) {
      throw new Error(`Missing capture tool version: ${name}`);
    }
  }
  for (const name of ["fault-only-crashpad-ad1827d.patch", "BUILD.gn", "capture.cc", "capture.h"]) {
    const current = sha256(readFileSync(join(root, "src", "crashpad_wrapper", name)));
    const recorded = name.endsWith(".patch") ? receipt.patchSha256 : receipt.wrapperSha256?.[name];
    if (current !== recorded) throw new Error(`Capture source changed: ${name}`);
  }
  if (sha256(capture) !== receipt.captureSha256) throw new Error("Capture DLL does not match its build receipt");
  if (!/^[0-9a-f]{64}$/.test(receipt.capturePdbSha256)) throw new Error("Capture PDB identity missing");
  const header = capture.readUInt32LE(0x3c);
  if (capture.readUInt16LE(0) !== 0x5a4d || capture.readUInt32LE(header) !== 0x4550 ||
      capture.readUInt16LE(header + 4) !== 0x8664) {
    throw new Error("Crashpad prototype DLL must be Windows x64");
  }
  return { receiptPath, receipt };
}
