#!/usr/bin/env python3
"""Verify the recovered JWT refresher core with injected clocks/storage; iOS source objects."""
import hashlib
import json
import os
import pathlib
import subprocess
import sys

package = pathlib.Path(__file__).resolve().parents[1]
output = package / "node_modules/.cache/ios-source-refresher"
output.mkdir(parents=True, exist_ok=True)
identity = package / "ios/Identity"
sources = [identity / (name + ".m") for name in ["ICMLogger", "ICMUtils", "ICMPersistenceProxy"]]
swift = package / "ios/Session/UserJwtRefresher.swift"
jwt = identity / "JWTExpiry.swift"
dispatcher = package / "ios/Session/Dispatcher.swift"
imports = package / "ios/Session/ICMUserJwtRefresherImports.h"
fixture = package / "scripts/ios-refresher-tests.swift"
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
for source in sources:
    obj = output / f"host-{source.stem}.o"
    run(["xcrun", "clang", *common, "-c", str(source), "-o", str(obj)])
    objects.append(obj)
host = output / "refresher-test"
run(["xcrun", "swiftc", "-swift-version", "5", "-parse-as-library", "-warnings-as-errors", "-import-objc-header", str(imports), str(jwt), str(dispatcher), str(swift), str(fixture), *map(str, objects), "-o", str(host)])
result = run([str(host)])
(output / "host-tests.json").write_text(result.stdout)
if result.stderr: (output / "host-tests.log").write_text(result.stderr)
for sdk, arch, target in [("iphoneos", "arm64", "arm64-apple-ios15.0"), ("iphonesimulator", "arm64", "arm64-apple-ios15.0-simulator"), ("iphonesimulator", "x86_64", "x86_64-apple-ios15.0-simulator")]:
    sdk_path = run(["xcrun", "--sdk", sdk, "--show-sdk-path"]).stdout.strip()
    for source in sources:
        run(["xcrun", "--sdk", sdk, "clang", *common, "-target", target, "-isysroot", sdk_path, "-c", str(source), "-o", str(output / f"{sdk}-{arch}-{source.stem}.o")])
    run(["xcrun", "--sdk", sdk, "swiftc", "-swift-version", "5", "-parse-as-library", "-warnings-as-errors", "-target", target, "-sdk", sdk_path, "-import-objc-header", str(imports), "-whole-module-optimization", "-emit-object", str(jwt), str(dispatcher), str(swift), "-o", str(output / f"{sdk}-{arch}-refresher.o")])
receipt = {
    "status": "SOURCE_COMPILED_FIXTURE_TESTED_PRODUCT_UNLINKED", "scope": "Injected core state machine; iOS default initializer object-compiles; real Bridge/full session unlinked",
    "hostBehavior": json.loads(result.stdout), "targets": ["macOS host behavior", "iOS15 arm64", "iOS15 simulator arm64", "iOS15 simulator x86_64"],
    "originalSDKCompileOrLinkInput": False,
    "inputs": {str(p.relative_to(package)): hashlib.sha256(p.read_bytes()).hexdigest() for p in [*sorted(identity.glob('*.[hm]')), swift, jwt, dispatcher, imports, fixture, *sorted((package / "ios/Session").glob("*.h"))]},
    "commands": commands,
}
(output / "receipt.json").write_text(json.dumps(receipt, indent=2) + "\n")
print(f"PASS: {receipt['hostBehavior']['testCount']} refresher assertions; Swift/ObjC compiled for 3 iOS targets")
print(output / "receipt.json")
