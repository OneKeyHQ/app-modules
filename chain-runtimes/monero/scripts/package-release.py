#!/usr/bin/env python3
"""Pack the Monero runtime npm archive from a wasm-pack build.

Usage: python3 scripts/package-release.py --version 0.1.0-alpha.1 --tag next
"""

import argparse
import json
import re
import shutil
import subprocess
import tomllib
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
NAME = "onekey-monero-runtime"


def workspace_version(root=ROOT):
    with open(root / "Cargo.toml", "rb") as f:
        return tomllib.load(f)["workspace"]["package"]["version"]


def check_version(value, expected, tag):
    match = re.fullmatch(r"\d+\.\d+\.\d+(?:-(alpha|rc)\.\d+)?", value)
    if value != expected or not match:
        raise ValueError("Release version must exactly match the workspace version")
    required_tag = {"alpha": "next", "rc": "rc"}.get(match.group(1), "latest")
    if tag != required_tag:
        raise ValueError(f"Version {value} requires npm dist-tag {required_tag}")


def prepare(root=ROOT):
    pkg = root / "pkg"
    path = pkg / "package.json"
    package = json.loads(path.read_text())
    if package["name"] != NAME or package["version"] != workspace_version(root):
        raise ValueError("Rebuild the wasm package: pkg version mismatch")
    module = NAME.replace("-", "_")
    package["files"] = [f"{module}.js", f"{module}.d.ts", f"{module}_bg.wasm", "LICENSE"]
    if (pkg / "snippets").exists():
        package["files"].append("snippets")
    package["repository"] = {
        "type": "git",
        "url": "git+https://github.com/OneKeyHQ/app-modules.git",
        "directory": "chain-runtimes/monero/crates/monero-runtime",
    }
    path.write_text(json.dumps(package, indent=2) + "\n")
    shutil.copyfile(root / "LICENSE", pkg / "LICENSE")


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--version", required=True)
    parser.add_argument("--tag", required=True, choices=["next", "rc", "latest"])
    args = parser.parse_args()
    check_version(args.version, workspace_version(), args.tag)
    prepare()
    dist = ROOT / "dist"
    dist.mkdir(exist_ok=True)
    subprocess.run(
        ["npm", "pack", "--pack-destination", str(dist)], cwd=ROOT / "pkg", check=True
    )
    archive = dist / f"{NAME}-{args.version}.tgz"
    if not archive.exists():
        raise SystemExit(f"missing {archive}")
    print(archive)


if __name__ == "__main__":
    main()
