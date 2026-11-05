#!/usr/bin/env python3
"""Compile and exercise the recovered JWT expiry helper from Swift source."""
import hashlib
import json
import pathlib
import subprocess
import sys

package = pathlib.Path(__file__).resolve().parents[1]
output = package / "node_modules/.cache/ios-source-jwt-expiry"
output.mkdir(parents=True, exist_ok=True)
source = package / "ios/Identity/JWTExpiry.swift"
fixture = package / "scripts/ios-jwt-expiry-tests.swift"
commands = []

def run(command):
    commands.append(command)
    result = subprocess.run(command, text=True, capture_output=True)
    if result.returncode:
        print(result.stdout + result.stderr, file=sys.stderr)
        result.check_returncode()
    return result

host = output / "jwt-expiry-test"
run(["xcrun", "swiftc", "-warnings-as-errors", "-parse-as-library", str(source), str(fixture), "-o", str(host)])
result = run([str(host)])
(output / "host-tests.json").write_text(result.stdout)
for sdk, arch, target in [("iphoneos", "arm64", "arm64-apple-ios15.0"), ("iphonesimulator", "arm64", "arm64-apple-ios15.0-simulator"), ("iphonesimulator", "x86_64", "x86_64-apple-ios15.0-simulator")]:
    sdk_path = run(["xcrun", "--sdk", sdk, "--show-sdk-path"]).stdout.strip()
    run(["xcrun", "--sdk", sdk, "swiftc", "-parse-as-library", "-warnings-as-errors", "-target", target, "-sdk", sdk_path, "-emit-object", str(source), "-o", str(output / f"{sdk}-{arch}-jwt-expiry.o")])
receipt = {"status": "passed", "scope": "JWTExpiry helper only; original comparison recorded separately in docs/IOS_CONFORMANCE.md", "hostBehavior": json.loads(result.stdout), "originalSDKCompileOrLinkInput": False,
           "inputs": {str(p.relative_to(package)): hashlib.sha256(p.read_bytes()).hexdigest() for p in [source, fixture]}, "commands": commands}
(output / "receipt.json").write_text(json.dumps(receipt, indent=2) + "\n")
print(f"PASS: {receipt['hostBehavior']['testCount']} JWT expiry assertions; source compiled for 3 iOS targets")
print(output / "receipt.json")
