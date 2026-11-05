#!/usr/bin/env python3
"""Verify actual push status core; compile real iOS defaults without OS notification queries."""
import hashlib
import json
import pathlib
import subprocess
import sys
package = pathlib.Path(__file__).resolve().parents[1]
output = package / "node_modules/.cache/ios-source-push-status"
output.mkdir(parents=True, exist_ok=True)
swift = package / "ios/Session/PushNotificationStatus.swift"
objc = package / "ios/Session/ICMPushNotificationManager.m"
imports = package / "ios/Session/ICMPushNotificationManager.h"
fixture = package / "scripts/ios-push-status-tests.swift"
commands = []
def run(command):
    commands.append(command)
    result = subprocess.run(command, text=True, capture_output=True)
    if result.returncode:
        print(result.stdout + result.stderr, file=sys.stderr)
        result.check_returncode()
    return result
host = output / "push-status-test"
run(["xcrun", "swiftc", "-swift-version", "5", "-parse-as-library", "-warnings-as-errors", str(swift), str(fixture), "-o", str(host)])
result = run([str(host)])
(output / "host-tests.json").write_text(result.stdout)
for sdk, arch, target in [("iphoneos", "arm64", "arm64-apple-ios15.0"), ("iphonesimulator", "arm64", "arm64-apple-ios15.0-simulator"), ("iphonesimulator", "x86_64", "x86_64-apple-ios15.0-simulator")]:
    sdk_path = run(["xcrun", "--sdk", sdk, "--show-sdk-path"]).stdout.strip()
    run(["xcrun", "--sdk", sdk, "clang", "-target", target, "-isysroot", sdk_path, "-fobjc-arc", "-fmodules", f"-fmodules-cache-path={output / 'modules'}", "-Wall", "-Wextra", "-Werror", "-c", str(objc), "-o", str(output / f"{sdk}-{arch}-manager.o")])
    run(["xcrun", "--sdk", sdk, "swiftc", "-swift-version", "5", "-parse-as-library", "-warnings-as-errors", "-target", target, "-sdk", sdk_path, "-import-objc-header", str(imports), "-whole-module-optimization", "-emit-object", str(swift), "-o", str(output / f"{sdk}-{arch}-status.o")])
receipt = {"status":"SOURCE_COMPILED_INJECTED_CORE_TESTED", "hostBehavior":json.loads(result.stdout), "originalSDKCompileOrLinkInput":False, "realOSNotificationQueries":0, "iOSDefaultAndObserverRuntime":"PENDING", "inputs":{str(p.relative_to(package)):hashlib.sha256(p.read_bytes()).hexdigest() for p in [swift, objc, imports, fixture]}, "commands":commands}
(output / "receipt.json").write_text(json.dumps(receipt,indent=2)+"\n")
print(f"PASS: {receipt['hostBehavior']['testCount']} push status injected assertions; actual default iOS objects for 3 targets")
print(output / "receipt.json")
