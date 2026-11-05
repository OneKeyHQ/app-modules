#!/usr/bin/env python3
"""Verify the identity closure without reading/writing the host Keychain."""
import hashlib
import json
import os
import pathlib
import subprocess
import sys

package = pathlib.Path(__file__).resolve().parents[1]
output = package / "node_modules/.cache/ios-source-identity"
output.mkdir(parents=True, exist_ok=True)
identity = package / "ios/Identity"
sources = sorted(identity.glob("*.m"))
swift = identity / "ICMKeychain.swift"
fixture = package / "scripts/ios-identity-tests.m"
common = ["-fobjc-arc", "-fmodules", f"-fmodules-cache-path={output / 'modules'}", "-Wall", "-Wextra", "-Werror", "-I", str(identity)]
commands = []

def run(command):
    commands.append(command)
    environment = dict(os.environ, ICM_TEST_DIRECTORY=str(output / "files"))
    result = subprocess.run(command, text=True, capture_output=True, env=environment)
    if result.returncode:
        print(result.stdout + result.stderr, file=sys.stderr)
        result.check_returncode()
    return result

objects = []
for source in [*sources, fixture]:
    obj = output / f"host-{source.stem}.o"
    run(["xcrun", "clang", *common, "-c", str(source), "-o", str(obj)])
    objects.append(obj)
swift_object = output / "host-keychain.o"
run(["xcrun", "swiftc", "-parse-as-library", "-warnings-as-errors", "-emit-object", str(swift), "-o", str(swift_object)])
host = output / "identity-test"
run(["xcrun", "swiftc", *map(str, objects), str(swift_object), "-framework", "Foundation", "-framework", "Security", "-o", str(host)])
result = run([str(host)])
(output / "host-tests.json").write_text(result.stdout)
if result.stderr: (output / "host-tests.log").write_text(result.stderr)
for sdk, arch, target in [("iphoneos", "arm64", "arm64-apple-ios15.0"), ("iphonesimulator", "arm64", "arm64-apple-ios15.0-simulator"), ("iphonesimulator", "x86_64", "x86_64-apple-ios15.0-simulator")]:
    sdk_path = run(["xcrun", "--sdk", sdk, "--show-sdk-path"]).stdout.strip()
    for source in sources:
        run(["xcrun", "--sdk", sdk, "clang", *common, "-target", target, "-isysroot", sdk_path, "-c", str(source), "-o", str(output / f"{sdk}-{arch}-{source.stem}.o")])
    run(["xcrun", "--sdk", sdk, "swiftc", "-parse-as-library", "-warnings-as-errors", "-target", target, "-sdk", sdk_path, "-emit-object", str(swift), "-o", str(output / f"{sdk}-{arch}-keychain.o")])
receipt = {
    "status": "passed", "scope": "Identity state/secure-coding and injected Security query/status behavior only; no real host persistence",
    "hostBehavior": json.loads(result.stdout), "targets": ["macOS host behavior", "iOS15 arm64", "iOS15 simulator arm64", "iOS15 simulator x86_64"],
    "originalSDKCompileOrLinkInput": False,
    "inputs": {str(p.relative_to(package)): hashlib.sha256(p.read_bytes()).hexdigest() for p in [*sorted(identity.glob('*.[hm]')), swift, fixture]},
    "commands": commands,
}
(output / "receipt.json").write_text(json.dumps(receipt, indent=2) + "\n")
print(f"PASS: {receipt['hostBehavior']['testCount']} identity assertions; Swift/ObjC compiled for 3 iOS targets")
print(output / "receipt.json")
