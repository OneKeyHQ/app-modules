#!/usr/bin/env python3
"""Verify Actual Config conversation-sync model, dictionary and coding behavior."""
import hashlib
import json
import pathlib
import subprocess
import sys
package = pathlib.Path(__file__).resolve().parents[1]
output = package / "node_modules/.cache/ios-source-sync-settings"
output.mkdir(parents=True, exist_ok=True)
swift = package / "ios/Session/ConversationStateSyncSettings.swift"
fixture = package / "scripts/ios-sync-settings-tests.swift"
commands = []
def run(command):
    commands.append(command)
    result = subprocess.run(command, text=True, capture_output=True)
    if result.returncode:
        print(result.stdout + result.stderr, file=sys.stderr)
        result.check_returncode()
    return result
host = output / "sync-settings-test"
run(["xcrun", "swiftc", "-swift-version", "5", "-parse-as-library", "-warnings-as-errors", str(swift), str(fixture), "-o", str(host)])
result = run([str(host)])
(output / "host-tests.json").write_text(result.stdout)
for sdk, arch, target in [("iphoneos", "arm64", "arm64-apple-ios15.0"), ("iphonesimulator", "arm64", "arm64-apple-ios15.0-simulator"), ("iphonesimulator", "x86_64", "x86_64-apple-ios15.0-simulator")]:
    sdk_path = run(["xcrun", "--sdk", sdk, "--show-sdk-path"]).stdout.strip()
    run(["xcrun", "--sdk", sdk, "swiftc", "-swift-version", "5", "-parse-as-library", "-warnings-as-errors", "-target", target, "-sdk", sdk_path, "-whole-module-optimization", "-emit-object", str(swift), "-o", str(output / f"{sdk}-{arch}-status.o")])
receipt = {"status":"SOURCE_COMPILED_TESTED", "hostBehavior":json.loads(result.stdout), "originalSDKCompileOrLinkInput":False, "originalDifferential":"PENDING", "inputs":{str(p.relative_to(package)):hashlib.sha256(p.read_bytes()).hexdigest() for p in [swift, fixture]}, "commands":commands}
(output / "receipt.json").write_text(json.dumps(receipt,indent=2)+"\n")
print(f"PASS: {receipt['hostBehavior']['testCount']} sync settings assertions; actual model objects for 3 targets")
print(output / "receipt.json")
