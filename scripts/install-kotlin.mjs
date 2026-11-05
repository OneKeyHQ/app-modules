import { createHash } from "node:crypto";
import { createReadStream } from "node:fs";
import { mkdir, mkdtemp, rename, rm } from "node:fs/promises";
import { join } from "node:path";
import { promisify } from "node:util";
import { execFile } from "node:child_process";
import { setTimeout as pause } from "node:timers/promises";
import { pathToFileURL } from "node:url";

// Published by JetBrains at the matching release's .zip.sha256 URL.
export const kotlinCompilerPin = {
  version: "2.3.21",
  sha256: "a8cfc1d62cd4d0de4d04f42575e40135bd620588c17d568a20eb9c7c259af14f",
  url: "https://github.com/JetBrains/kotlin/releases/download/v2.3.21/kotlin-compiler-2.3.21.zip",
};
const runCommand = promisify(execFile);

async function matchesChecksum(path, checksum) {
  try {
    const hash = createHash("sha256");
    for await (const chunk of createReadStream(path)) hash.update(chunk);
    return hash.digest("hex") === checksum;
  } catch {
    return false;
  }
}

export async function downloadKotlin(url, output, { run = runCommand, sleep = pause } = {}) {
  for (let attempt = 0; attempt < 3; attempt++) {
    try {
      await run("curl", [
        "--disable", "--fail", "--silent", "--show-error", "--location",
        "--proto", "=https", "--proto-redir", "=https",
        "--connect-timeout", "15", "--max-time", "90", "--output", output, url,
      ], { timeout: 100_000 });
      return;
    } catch {
      if (attempt < 2) await sleep(1000 * (attempt + 1));
    }
  }
  throw new Error("kotlin_download_failed");
}

export async function installKotlin(
  { cacheDirectory, installDirectory, pin = kotlinCompilerPin },
  { run = runCommand, sleep = pause } = {}
) {
  await mkdir(cacheDirectory, { recursive: true });
  const archive = join(cacheDirectory, `kotlin-compiler-${pin.version}.zip`);
  const cacheHit = await matchesChecksum(archive, pin.sha256);
  if (!cacheHit) {
    await rm(archive, { force: true });
    const temporary = await mkdtemp(join(cacheDirectory, "download-"));
    try {
      const downloaded = join(temporary, "compiler.zip");
      await downloadKotlin(pin.url, downloaded, { run, sleep });
      if (!(await matchesChecksum(downloaded, pin.sha256))) throw new Error("kotlin_checksum_mismatch");
      await rename(downloaded, archive);
    } finally {
      await rm(temporary, { recursive: true, force: true });
    }
  }

  await mkdir(installDirectory);
  try {
    await run("unzip", ["-q", archive, "-d", installDirectory], { timeout: 60_000 });
  } catch {
    throw new Error("kotlin_extract_failed");
  }
  let version;
  try {
    version = await run(join(installDirectory, "kotlinc/bin/kotlinc"), ["-version"], { timeout: 30_000 });
  } catch {
    throw new Error("kotlin_version_failed");
  }
  const escaped = pin.version.replaceAll(".", "\\.");
  if (!new RegExp(`\\bkotlinc-jvm ${escaped}(?:\\s|$)`).test(`${version.stdout}\n${version.stderr}`)) {
    throw new Error("kotlin_version_mismatch");
  }
  return { cacheHit, archive, binDirectory: join(installDirectory, "kotlinc/bin") };
}

if (process.argv[1] && import.meta.url === pathToFileURL(process.argv[1]).href) {
  if (process.argv[2] === "pin") {
    console.log(`version=${kotlinCompilerPin.version}\nsha256=${kotlinCompilerPin.sha256}`);
  } else {
    try {
      if (process.argv.length !== 4) throw new Error("kotlin_arguments_invalid");
      await installKotlin({ cacheDirectory: process.argv[2], installDirectory: process.argv[3] });
      console.log(`Verified Kotlin ${kotlinCompilerPin.version}`);
    } catch (error) {
      const allowed = new Set([
        "kotlin_arguments_invalid", "kotlin_download_failed", "kotlin_checksum_mismatch",
        "kotlin_extract_failed", "kotlin_version_failed", "kotlin_version_mismatch",
      ]);
      console.error(allowed.has(error.message) ? error.message : "kotlin_install_failed");
      process.exitCode = 1;
    }
  }
}
