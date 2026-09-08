#!/usr/bin/env python3
"""Reconstruct pinned crates and their reviewed patches before Cargo runs."""

import argparse
import hashlib
import json
import os
from pathlib import Path, PurePosixPath
import subprocess
import tarfile
import tempfile
import urllib.request


ROOT = Path(__file__).resolve().parent.parent


def file_hashes(directory):
    return {
        str(path.relative_to(directory)): hashlib.sha256(path.read_bytes()).hexdigest()
        for path in directory.rglob("*")
        if path.is_file()
    }


def extract_crate(archive, temporary, identity):
    # Accept only ordinary files/directories under the expected crate root.
    # This also works on macOS Python versions without tarfile's data filter.
    with tarfile.open(archive) as contents:
        for member in contents:
            path = PurePosixPath(member.name)
            if path.is_absolute() or ".." in path.parts or not path.parts or path.parts[0] != identity:
                raise RuntimeError(f"Invalid crate archive path: {member.name}")
            target = Path(temporary).joinpath(*path.parts)
            if member.isdir():
                target.mkdir(parents=True, exist_ok=True)
            elif member.isfile():
                target.parent.mkdir(parents=True, exist_ok=True)
                with contents.extractfile(member) as source:
                    target.write_bytes(source.read())
            else:
                raise RuntimeError(f"Unsupported crate archive entry: {member.name}")


def prepare(offline):
    vendor = ROOT / "vendor"
    archives = vendor / ".archives"
    archives.mkdir(parents=True, exist_ok=True)
    cargo_home = Path(os.environ.get("CARGO_HOME", Path.home() / ".cargo"))
    manifest = json.loads((ROOT / "patches/manifest.json").read_text())
    for entry in manifest:
        identity = f"{entry['name']}-{entry['version']}"
        archive = archives / f"{identity}.crate"
        if not archive.exists():
            cached = list((cargo_home / "registry/cache").glob(f"*/{identity}.crate"))
            if cached:
                data = cached[0].read_bytes()
            elif offline:
                raise RuntimeError(f"Missing cached archive for {identity}; run without --offline once")
            else:
                request = urllib.request.Request(
                    f"https://static.crates.io/crates/{entry['name']}/{identity}.crate",
                    headers={"User-Agent": "OneKey app-modules Zcash build"},
                )
                with urllib.request.urlopen(request, timeout=60) as response:
                    data = response.read()
            if hashlib.sha256(data).hexdigest() != entry["sha256"]:
                raise RuntimeError(f"Archive checksum mismatch: {identity}")
            archive.write_bytes(data)
        if hashlib.sha256(archive.read_bytes()).hexdigest() != entry["sha256"]:
            raise RuntimeError(f"Cached archive checksum mismatch: {identity}")

        with tempfile.TemporaryDirectory(prefix="prepare-", dir=vendor) as temporary:
            extract_crate(archive, temporary, identity)
            source = Path(temporary) / identity
            subprocess.run(
                ["patch", "--batch", "--forward", "-F", "0", "-p1", "-i", str(ROOT / "patches" / entry["patch"])],
                cwd=source,
                check=True,
            )
            destination = vendor / identity
            if destination.exists():
                if file_hashes(source) != file_hashes(destination):
                    raise RuntimeError(f"Existing vendor files differ from the pinned source and patch: {identity}")
            else:
                source.rename(destination)
        print(f"Verified {identity}")


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--offline", action="store_true", help="Use cached crate archives only")
    prepare(parser.parse_args().offline)
