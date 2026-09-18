#!/usr/bin/env python3
"""Bind the complete Zcash WASM artifact to its source revision and file hashes."""

import argparse
import hashlib
import json
from pathlib import Path
import subprocess


ROOT = Path(__file__).resolve().parent.parent
MANIFEST = "zcash-wasm-manifest.json"
PACKAGES = {
    "pkg": "onekey_zcash_runtime",
    "pkg-keys": "onekey_zcash_keys",
    "pkg/storage-benchmark": "onekey_zcash_storage_benchmark",
}


def digest(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()


def artifact_files(root):
    files = {}
    for directory in ("pkg", "pkg-keys"):
        if (root / directory).is_symlink():
            raise ValueError(f"Artifact package is a symlink: {directory}")
        for path in sorted((root / directory).rglob("*")):
            if path.is_symlink():
                raise ValueError(f"Artifact symlink: {path.relative_to(root)}")
            if path.is_file() and path.name != ".gitignore":
                files[path.relative_to(root).as_posix()] = digest(path)
    for directory, module in PACKAGES.items():
        for name in ("package.json", f"{module}.js", f"{module}.d.ts", f"{module}_bg.wasm"):
            if f"{directory}/{name}" not in files:
                raise ValueError(f"Incomplete artifact: {directory}/{name}")
    return files


def write(root):
    revision = subprocess.check_output(["git", "rev-parse", "HEAD"], cwd=ROOT, text=True).strip()
    dirty = bool(subprocess.check_output([
        "git", "-c", "core.fsmonitor=false", "status", "--porcelain", "--untracked-files=normal", "--", ".",
    ], cwd=ROOT, text=True).strip())
    manifest = {
        "formatVersion": 1,
        "sourceRepository": "OneKeyHQ/app-modules",
        "sourceRevision": revision,
        "sourceDirty": dirty,
        "cargoLockSha256": digest(ROOT / "Cargo.lock"),
        "files": artifact_files(root),
    }
    (root / MANIFEST).write_text(json.dumps(manifest, indent=2, sort_keys=True) + "\n")
    print(f"Wrote {MANIFEST}: {len(manifest['files'])} files, dirty={dirty}")


def verify(root, revision, allow_dirty=False):
    manifest = json.loads((root / MANIFEST).read_text())
    if manifest.get("formatVersion") != 1 or manifest.get("sourceRepository") != "OneKeyHQ/app-modules":
        raise ValueError("Unsupported artifact provenance")
    if manifest.get("sourceRevision") != revision:
        raise ValueError("Artifact source revision mismatch")
    if manifest.get("sourceDirty") is not False and not allow_dirty:
        raise ValueError("CI artifacts must originate from a clean source checkout")
    if artifact_files(root) != manifest.get("files"):
        raise ValueError("Artifact file inventory or checksum mismatch")
    print(f"Verified complete Zcash artifact for {revision}")


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("operation", choices=("write", "verify"))
    parser.add_argument("--root", type=Path, default=ROOT)
    parser.add_argument("--expected-revision")
    parser.add_argument("--allow-dirty", action="store_true", help="Local development verification only")
    args = parser.parse_args()
    if args.operation == "write":
        write(args.root)
    elif not args.expected_revision:
        parser.error("verify requires --expected-revision")
    else:
        verify(args.root, args.expected_revision, args.allow_dirty)
