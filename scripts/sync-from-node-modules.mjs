#!/usr/bin/env node
// Copies source edits made inside a consumer's node_modules/@onekeyfe/* back
// into this repository, so debugging in the app never leaves the only copy of
// a change in node_modules.
//
// Usage:
//   node scripts/sync-from-node-modules.mjs --from <consumer-root> <package-name>... [--dry-run] [--force]
//
// A file counts as edited only when it differs from the installed version's
// pristine tarball (the consumer's .yarn/cache zip), so an older install never
// rolls this repository back. Only published source entries (`files` in the
// package's package.json, minus its `!` exclusions) are considered; build
// output (`lib`), docs and native build folders are skipped. Nothing is
// deleted.
//
// Per edited file:
//   - repository already has the same content: skipped
//   - repository still matches the pristine file (or lacks it): copied
//   - repository changed it too: conflict, left alone unless --force

import { spawnSync } from "node:child_process";
import fs from "node:fs";
import os from "node:os";
import path from "node:path";
import { fileURLToPath } from "node:url";

const REPO_ROOT = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");
const PACKAGE_ROOTS = ["native-modules", "native-views"];
const SKIPPED_ENTRIES = new Set([
  "lib",
  "docs",
  "README.md",
  "LICENSE",
  "package.json",
]);
const SKIPPED_SEGMENTS = new Set([
  "build",
  ".gradle",
  ".cxx",
  "node_modules",
  ".DS_Store",
]);

export function findPackageDirs(repoRoot = REPO_ROOT) {
  const dirs = new Map();
  for (const root of PACKAGE_ROOTS) {
    const base = path.join(repoRoot, root);
    if (!fs.existsSync(base)) continue;
    for (const entry of fs.readdirSync(base)) {
      const manifest = path.join(base, entry, "package.json");
      if (!fs.existsSync(manifest)) continue;
      const { name } = JSON.parse(fs.readFileSync(manifest, "utf8"));
      if (name) dirs.set(name, path.join(base, entry));
    }
  }
  return dirs;
}

// Converts a `files` glob to a RegExp over "/"-separated relative paths:
// `**` spans directories, `*` and `?` stay within one segment, and a pattern
// without a "/" matches at any depth (npm `files` semantics).
export function globToRegExp(glob) {
  const pattern = glob.replace(/^\.?\//, "").replace(/\/$/, "");
  let source = "";
  for (let index = 0; index < pattern.length; index += 1) {
    const char = pattern[index];
    if (char === "*" && pattern[index + 1] === "*") {
      const slash = pattern[index + 2] === "/";
      source += slash ? "(?:.*/)?" : ".*";
      index += slash ? 2 : 1;
    } else if (char === "*") {
      source += "[^/]*";
    } else if (char === "?") {
      source += "[^/]";
    } else {
      source += char.replace(/[\\^$.|+()[\]{}]/g, "\\$&");
    }
  }
  const anyDepth = pattern.includes("/") ? "" : "(?:.*/)?";
  return new RegExp(`^${anyDepth}${source}$`);
}

// Source entries to consider: the positive `files` entries minus build
// output and docs. Glob entries (`*.podspec`) resolve against `dir`.
export function selectSyncEntries(files, dir) {
  const entries = [];
  for (const file of files ?? []) {
    if (file.startsWith("!") || SKIPPED_ENTRIES.has(file)) continue;
    if (file.startsWith("docs/")) continue;
    if (file.includes("*")) {
      const pattern = globToRegExp(file);
      for (const name of fs.readdirSync(dir)) {
        if (pattern.test(name)) entries.push(name);
      }
      continue;
    }
    entries.push(file);
  }
  return entries;
}

// The negative `files` entries (`!**/__tests__`): a path is excluded when the
// pattern matches it or one of its parent directories.
export function createExcludeMatcher(files) {
  const patterns = (files ?? [])
    .filter((file) => file.startsWith("!"))
    .map((file) => globToRegExp(file.slice(1)));
  return (relativePath) => {
    const segments = relativePath.split(/[\\/]/);
    for (let length = 1; length <= segments.length; length += 1) {
      const candidate = segments.slice(0, length).join("/");
      if (patterns.some((pattern) => pattern.test(candidate))) return true;
    }
    return false;
  };
}

function listFiles(root, entry) {
  const start = path.join(root, entry);
  if (!fs.existsSync(start)) return [];
  if (!fs.statSync(start).isDirectory()) return [entry];
  const files = [];
  const walk = (relative) => {
    for (const name of fs.readdirSync(path.join(root, relative))) {
      if (SKIPPED_SEGMENTS.has(name)) continue;
      const child = path.join(relative, name);
      if (fs.statSync(path.join(root, child)).isDirectory()) walk(child);
      else files.push(child);
    }
  };
  walk(entry);
  return files;
}

function readOrNull(file) {
  return fs.existsSync(file) ? fs.readFileSync(file) : null;
}

function sameContent(a, b) {
  return a !== null && b !== null && a.equals(b);
}

// Decides what to do with one file given its three versions.
export function classifyFile({ edited, pristine, repo }) {
  if (sameContent(edited, pristine)) return "untouched";
  if (sameContent(edited, repo)) return "in-sync";
  if (repo === null || sameContent(repo, pristine)) return "copy";
  return "conflict";
}

function extractPristine(consumerRoot, name, version) {
  const cacheDir = path.join(consumerRoot, ".yarn", "cache");
  const prefix = `${name.replace("/", "-")}-npm-${version}-`;
  const zip = fs.existsSync(cacheDir)
    ? fs
        .readdirSync(cacheDir)
        .find((file) => file.startsWith(prefix) && file.endsWith(".zip"))
    : undefined;
  if (!zip) {
    throw new Error(
      `No pristine ${name}@${version} in ${cacheDir}; run yarn install in the consumer first`,
    );
  }
  const out = fs.mkdtempSync(path.join(os.tmpdir(), "onekeyfe-pristine-"));
  const result = spawnSync("unzip", ["-q", path.join(cacheDir, zip), "-d", out]);
  if (result.status !== 0) throw new Error(`Failed to unzip ${zip}`);
  return path.join(out, "node_modules", name);
}

function syncPackage(name, consumerRoot, packageDir, { dryRun, force }) {
  const edited = path.join(consumerRoot, "node_modules", name);
  if (!fs.existsSync(edited)) {
    throw new Error(`${name} is not installed under ${consumerRoot}/node_modules`);
  }
  const { version } = JSON.parse(
    fs.readFileSync(path.join(edited, "package.json"), "utf8"),
  );
  const pristine = extractPristine(consumerRoot, name, version);
  const { files } = JSON.parse(
    fs.readFileSync(path.join(packageDir, "package.json"), "utf8"),
  );
  const isExcluded = createExcludeMatcher(files);
  let conflicts = 0;
  for (const entry of selectSyncEntries(files, edited)) {
    for (const file of listFiles(edited, entry)) {
      if (isExcluded(file)) continue;
      const editedContent = fs.readFileSync(path.join(edited, file));
      const target = path.join(packageDir, file);
      const action = classifyFile({
        edited: editedContent,
        pristine: readOrNull(path.join(pristine, file)),
        repo: readOrNull(target),
      });
      if (action === "untouched" || action === "in-sync") continue;
      if (action === "conflict" && !force) {
        conflicts += 1;
        console.log(`conflict ${name}/${file} (changed in both; rerun with --force to take node_modules)`);
        continue;
      }
      console.log(`${dryRun ? "would copy" : "copy"} ${name}/${file}`);
      if (!dryRun) {
        fs.mkdirSync(path.dirname(target), { recursive: true });
        fs.writeFileSync(target, editedContent);
      }
    }
  }
  fs.rmSync(path.dirname(path.dirname(pristine)), { recursive: true, force: true });
  return conflicts;
}

function main(argv) {
  const dryRun = argv.includes("--dry-run");
  const force = argv.includes("--force");
  const fromIndex = argv.indexOf("--from");
  const consumerRoot = fromIndex >= 0 ? argv[fromIndex + 1] : undefined;
  const names = argv.filter(
    (arg, index) =>
      !arg.startsWith("--") && !(fromIndex >= 0 && index === fromIndex + 1),
  );
  if (!consumerRoot || names.length === 0) {
    console.error(
      "Usage: node scripts/sync-from-node-modules.mjs --from <consumer-root> <package-name>... [--dry-run] [--force]",
    );
    process.exit(1);
  }
  const dirs = findPackageDirs();
  let conflicts = 0;
  for (const name of names) {
    const packageDir = dirs.get(name);
    if (!packageDir) throw new Error(`${name} is not a package in this repository`);
    conflicts += syncPackage(name, path.resolve(consumerRoot), packageDir, {
      dryRun,
      force,
    });
  }
  if (conflicts > 0) process.exit(2);
}

if (process.argv[1] === fileURLToPath(import.meta.url)) {
  main(process.argv.slice(2));
}
