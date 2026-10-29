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
# wasm-bindgen module name, and the npm package name apps install.
MODULE = "onekey_monero_runtime"
NAME = "@onekeyfe/monero-runtime"


def workspace_version(root=ROOT):
    with open(root / "Cargo.toml", "rb") as f:
        return tomllib.load(f)["workspace"]["package"]["version"]


def check_version(value, expected, tag):
    match = re.fullmatch(r"\d+\.\d+\.\d+(?:-(alpha|rc)\.\d+)?", value)
    if value != expected or not match:
        raise ValueError("Release version must exactly match the workspace version")
    required_tag = "next" if match.group(1) else "latest"
    if tag != required_tag:
        raise ValueError(f"Version {value} requires npm dist-tag {required_tag}")


def prepare(root=ROOT):
    pkg = root / "pkg"
    path = pkg / "package.json"
    package = json.loads(path.read_text())
    # wasm-pack names the package after the crate.
    crate = MODULE.replace("_", "-")
    if package["name"] != crate or package["version"] != workspace_version(root):
        raise ValueError("Rebuild the wasm package: pkg version mismatch")
    package["name"] = NAME
    package["files"] = [f"{MODULE}.js", f"{MODULE}.d.ts", f"{MODULE}_bg.wasm", "LICENSE"]
    if (pkg / "snippets").exists():
        package["files"].append("snippets")
    package["repository"] = {
        "type": "git",
        "url": "git+https://github.com/OneKeyHQ/app-modules.git",
        "directory": "chain-runtimes/monero/crates/monero-runtime",
    }
    # A scoped package is private unless published public.
    package["publishConfig"] = {"access": "public", "registry": "https://registry.npmjs.org/"}
    path.write_text(json.dumps(package, indent=2) + "\n")
    shutil.copyfile(root / "LICENSE", pkg / "LICENSE")


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--version", required=True)
    parser.add_argument("--tag", required=True, choices=["next", "latest"])
    args = parser.parse_args()
    check_version(args.version, workspace_version(), args.tag)
    prepare()
    dist = ROOT / "dist"
    dist.mkdir(exist_ok=True)
    subprocess.run(
        ["npm", "pack", "--pack-destination", str(dist)], cwd=ROOT / "pkg", check=True
    )
    # npm pack names @scope/pkg archives scope-pkg-<version>.tgz.
    archive = dist / f"{NAME.removeprefix('@').replace('/', '-')}-{args.version}.tgz"
    if not archive.exists():
        raise SystemExit(f"missing {archive}")
    print(archive)


if __name__ == "__main__":
    main()
