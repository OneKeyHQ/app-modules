#!/usr/bin/env python3
"""Prepare and check the two npm packages without publishing or fetching code."""

import argparse
import importlib.util
import json
from pathlib import Path
import re
import shutil
import subprocess
import tarfile
import tomllib


ROOT = Path(__file__).resolve().parent.parent
# Build directory -> (wasm-bindgen module name, npm package name).
PACKAGES = {
    "pkg": ("onekey_zcash_runtime", "@onekeyfe/zcash-runtime"),
    "pkg-keys": ("onekey_zcash_keys", "@onekeyfe/zcash-keys"),
}


def archive_name(name, release_version):
    # npm pack names @scope/pkg archives scope-pkg-<version>.tgz.
    return f"{name.removeprefix('@').replace('/', '-')}-{release_version}.tgz"


def version(root):
    return tomllib.loads((root / "Cargo.toml").read_text())["workspace"]["package"]["version"]


def check_version(value, expected, tag):
    match = re.fullmatch(r"\d+\.\d+\.\d+(?:-(alpha|rc)\.\d+)?", value)
    if value != expected or not match:
        raise ValueError("Release version must exactly match the workspace version")
    required_tag = "next" if match.group(1) else "latest"
    if tag != required_tag:
        raise ValueError(f"Version {value} requires npm dist-tag {required_tag}")


def prepare(root):
    release_version = version(root)
    for directory, (module, name) in PACKAGES.items():
        target = root / directory
        path = target / "package.json"
        package = json.loads(path.read_text())
        # wasm-pack names the package after the crate.
        if package["name"] != module.replace("_", "-") or package["version"] != release_version:
            raise ValueError(f"Rebuild both WASM packages: {directory} version mismatch")
        package["name"] = name
        package["files"] = [f"{module}.js", f"{module}.d.ts", f"{module}_bg.wasm", "LICENSE"]
        if (target / "snippets").exists():
            package["files"].append("snippets")
        package["repository"] = {
            "type": "git", "url": "git+https://github.com/OneKeyHQ/app-modules.git",
            "directory": f"chain-runtimes/zcash/crates/{'zcash-runtime' if directory == 'pkg' else 'zcash-keys-runtime'}",
        }
        package["publishConfig"] = {"access": "public", "registry": "https://registry.npmjs.org/"}
        shutil.copyfile(root / "LICENSE", target / "LICENSE")
        path.write_text(json.dumps(package, indent=2) + "\n")


def verify_tarball(archive, source, module, name, release_version):
    with tarfile.open(archive, "r:gz") as tar:
        files = {}
        for member in tar.getmembers():
            if not member.isfile() or not member.name.startswith("package/"):
                raise ValueError(f"Unexpected npm archive member: {member.name}")
            relative = member.name.removeprefix("package/")
            if Path(relative).is_absolute() or ".." in Path(relative).parts or relative in files:
                raise ValueError("Unsafe or duplicate npm archive member")
            files[relative] = tar.extractfile(member).read()
        package = json.loads(files["package.json"])
        if package["name"] != name or package["version"] != release_version:
            raise ValueError("Packed package identity mismatch")
        required = {"package.json", "LICENSE"}
        required.update({f"{module}.js", f"{module}.d.ts", f"{module}_bg.wasm"})
        required.update(p.relative_to(source).as_posix() for p in (source / "snippets").rglob("*")
                        if p.is_file() and p.name != ".gitignore")
        if not required.issubset(files):
            raise ValueError(f"npm archive is incomplete: {sorted(required - files.keys())}")
        for relative, content in files.items():
            if content != (source / relative).read_bytes():
                raise ValueError(f"npm archive differs from verified artifact: {relative}")


def pack(root, expected, tag, revision, allow_dirty=False):
    check_version(version(root), expected, tag)
    spec = importlib.util.spec_from_file_location("artifact_manifest", ROOT / "scripts/artifact-manifest.py")
    artifact = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(artifact)
    artifact.verify(root, revision, allow_dirty)
    output = root / "dist"
    output.mkdir(exist_ok=True)
    for directory, (module, name) in PACKAGES.items():
        result = subprocess.check_output([
            "npm", "pack", "--ignore-scripts", "--json", "--pack-destination", str(output.resolve()),
        ], cwd=root / directory, text=True)
        filename = json.loads(result)[0]["filename"]
        if filename != archive_name(name, expected):
            raise ValueError("Unexpected npm archive filename")
        verify_tarball(output / filename, root / directory, module, name, expected)
        print(f"Verified {filename}")
    shutil.copyfile(root / "zcash-wasm-manifest.json", output / "zcash-wasm-manifest.json")


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("operation", choices=("prepare", "pack"))
    parser.add_argument("--version")
    parser.add_argument("--tag", choices=("next", "latest"))
    parser.add_argument("--revision")
    parser.add_argument("--allow-dirty", action="store_true", help="Local pack checks only; never publish these archives")
    args = parser.parse_args()
    if args.operation == "prepare":
        prepare(ROOT)
    elif not all((args.version, args.tag, args.revision)):
        parser.error("pack requires --version, --tag and --revision")
    else:
        pack(ROOT, args.version, args.tag, args.revision, args.allow_dirty)
