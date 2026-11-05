#!/usr/bin/env python3
"""Compile actual mainline Swift dependencies without diagnostic class bodies."""
import hashlib
import json
import pathlib
import subprocess

package = pathlib.Path(__file__).resolve().parents[1]
output = package / "node_modules/.cache/ios-source-mainline-swift"
output.mkdir(parents=True, exist_ok=True)
source = package / "ios/Session/ProactiveContentVisibility.swift"
bridge = package / "ios/Session/ICMVisibilityImports.h"
commands = []
def run(command):
    commands.append(command)
    result = subprocess.run(command, text=True, capture_output=True)
    if result.returncode:
        print(result.stdout + result.stderr)
        result.check_returncode()
    return result
for sdk, arch, target in [("iphoneos", "arm64", "arm64-apple-ios15.0"), ("iphonesimulator", "arm64", "arm64-apple-ios15.0-simulator"), ("iphonesimulator", "x86_64", "x86_64-apple-ios15.0-simulator")]:
    sdk_path = run(["xcrun", "--sdk", sdk, "--show-sdk-path"]).stdout.strip()
    obj = output / f"{sdk}-{arch}-visibility.o"
    run(["xcrun", "--sdk", sdk, "swiftc", "-swift-version", "5", "-parse-as-library", "-warnings-as-errors", "-target", target, "-sdk", sdk_path, "-import-objc-header", str(bridge), "-whole-module-optimization", "-emit-object", str(source), "-o", str(obj)])
    (output / f"{sdk}-{arch}-undefined.txt").write_text(run(["xcrun", "nm", "-u", str(obj)]).stdout)
inputs = [source, *sorted((package / "ios/Session").glob("*.h")), *sorted((package / "ios/Identity").glob("*.h"))]
receipt = {"status": "GENUINE_VISIBILITY_SOURCE_COMPILED_UNLINKED", "originalSDKCompileOrLinkInput": False, "fixtureImplementationsUsed": False, "runtimeBehaviorVerified": False, "missingProductClosure": "IdentityStore/HTTPBridge updateUser actual product chain", "inputs": {str(p.relative_to(package)): hashlib.sha256(p.read_bytes()).hexdigest() for p in inputs}, "commands": commands}
(output / "receipt.json").write_text(json.dumps(receipt, indent=2) + "\n")
print("PASS: genuine proactive visibility Swift source compiled for three iOS15 targets; product closure remains unlinked")
print(output / "receipt.json")
