#!/usr/bin/env python3
"""Compile and execute the recovered model closure without any vendor SDK input."""
import hashlib
import json
import pathlib
import subprocess
import sys

package = pathlib.Path(__file__).resolve().parents[1]
output = package / "node_modules/.cache/ios-source-models"
output.mkdir(parents=True, exist_ok=True)
models = package / "ios/Models"
sources = sorted(models.glob("*.m"))
common = ["-fobjc-arc", "-fmodules", f"-fmodules-cache-path={output / 'modules'}", "-Wall", "-Wextra", "-Werror", "-I", str(models)]
commands = []

def run(command):
    commands.append(command)
    return subprocess.run(command, check=True, text=True, capture_output=True)

host = output / "models-test"
run(["xcrun", "clang", *common, "-framework", "Foundation", *map(str, sources), str(package / "scripts/ios-model-tests.m"), "-o", str(host)])
result = run([str(host)])
(output / "host-tests.json").write_text(result.stdout)
if result.stderr:
    (output / "expected-validation.log").write_text(result.stderr)
for sdk, arch, target in [("iphoneos", "arm64", "arm64-apple-ios15.0"), ("iphonesimulator", "arm64", "arm64-apple-ios15.0-simulator"), ("iphonesimulator", "x86_64", "x86_64-apple-ios15.0-simulator")]:
    sdk_path = run(["xcrun", "--sdk", sdk, "--show-sdk-path"]).stdout.strip()
    for source in sources:
        run(["xcrun", "--sdk", sdk, "clang", *common, "-target", target, "-isysroot", sdk_path, "-c", str(source), "-o", str(output / f"{sdk}-{arch}-{source.stem}.o")])
receipt = {
    "status": "passed",
    "scope": "Foundation model closure only; not complete SDK or original runtime differential",
    "hostBehavior": json.loads(result.stdout),
    "targets": ["macOS host executable", "iOS 15.0 arm64 source objects", "iOS 15.0 simulator arm64 source objects", "iOS 15.0 simulator x86_64 source objects"],
    "originalSDKCompileOrLinkInput": False,
    "inputs": {str(p.relative_to(package)): hashlib.sha256(p.read_bytes()).hexdigest() for p in [*sorted(models.glob("*.[hm]")), package / "scripts/ios-model-tests.m"]},
    "commands": commands,
}
(output / "receipt.json").write_text(json.dumps(receipt, indent=2) + "\n")
print(f"PASS: {receipt['hostBehavior']['testCount']} behavior cases; 3 iOS target compilations")
print(output / "receipt.json")
