#!/usr/bin/env python3
"""Check genuine unread state; keep absent response dependencies unlinked."""
import hashlib
import json
import pathlib
import subprocess

package = pathlib.Path(__file__).resolve().parents[1]
output = package / "node_modules/.cache/ios-source-unread"
output.mkdir(parents=True, exist_ok=True)
swift = package / "ios/Session/UnreadManager.swift"
fixture = package / "scripts/ios-unread-manager-tests.swift"
objc = [package / "ios/Session" / f"{name}.m" for name in ["ICMUnreadConversationsResponse", "ICMUnreadTicketsResponse"]]
commands = []
def run(command):
    commands.append(command)
    result = subprocess.run(command, text=True, capture_output=True)
    if result.returncode:
        print(result.stdout + result.stderr)
        result.check_returncode()
    return result
host = output / "unread-test"
run(["xcrun", "swiftc", "-swift-version", "5", "-parse-as-library", str(swift), str(fixture), "-o", str(host)])
result = run([str(host)])
(output / "host-tests.json").write_text(result.stdout)
for sdk, arch, target in [("iphoneos", "arm64", "arm64-apple-ios15.0"), ("iphonesimulator", "arm64", "arm64-apple-ios15.0-simulator"), ("iphonesimulator", "x86_64", "x86_64-apple-ios15.0-simulator")]:
    sdk_path = run(["xcrun", "--sdk", sdk, "--show-sdk-path"]).stdout.strip()
    run(["xcrun", "--sdk", sdk, "swiftc", "-swift-version", "5", "-parse-as-library", "-warnings-as-errors", "-target", target, "-sdk", sdk_path, "-emit-library", str(swift), "-o", str(output / f"{sdk}-{arch}-UnreadManager.dylib")])
    for source in objc:
        run(["xcrun", "--sdk", sdk, "clang", "-target", target, "-isysroot", sdk_path, "-fobjc-arc", "-fmodules", f"-fmodules-cache-path={output / 'modules'}", "-Wall", "-Wextra", "-Werror", "-c", str(source), "-o", str(output / f"{sdk}-{arch}-{source.stem}.o")])
receipt = {"status": "UNREAD_MANAGER_SOURCE_LINKED_HOST_TESTED_RESPONSES_UNLINKED", "hostBehavior": json.loads(result.stdout), "originalSDKCompileOrLinkInput": False, "fixtureProductDependenciesUsed": False, "responseMissingProductDependencies": ["ICMDataManager", "ICMConversation"], "iOSRuntimeBehaviorVerified": False, "inputs": {str(p.relative_to(package)): hashlib.sha256(p.read_bytes()).hexdigest() for p in [swift, fixture, *objc, package / "ios/Session/ICMDataManager.h", package / "ios/Session/ICMUnreadResponseDependencies.h"]}, "commands": commands}
(output / "receipt.json").write_text(json.dumps(receipt, indent=2) + "\n")
print(f"PASS: {receipt['hostBehavior']['testCount']} unread assertions; real manager linked and two responses compiled for three iOS15 targets")
print(output / "receipt.json")
