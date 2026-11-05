#!/usr/bin/env python3
"""Exercise Auth HTTP mapping and multipart identity fields with diagnostic identity/engine/defaults."""
import hashlib
import json
import os
import pathlib
import subprocess
import sys
package = pathlib.Path(__file__).resolve().parents[1]
output = package / "node_modules/.cache/ios-source-identity-requests"
output.mkdir(parents=True, exist_ok=True)
sources = [package / "ios/Session/ICMHTTPClient.m", package / "ios/Session/ICMHTTPClient+IdentityRequests.m", package / "ios/Identity/ICMDeviceData.m"]
swift = package / "ios/Identity/ErrorProcessor.swift"
fixture = package / "scripts/ios-identity-request-tests.m"
sources += [package / "ios/Identity/ICMUtils.m", package / "ios/Identity/ICMPersistenceProxy.m"]
commands = []
def run(command, **kwargs):
    commands.append(command)
    result = subprocess.run(command, text=True, capture_output=True, **kwargs)
    if result.returncode:
        print(result.stdout + result.stderr, file=sys.stderr)
        result.check_returncode()
    return result
common = ["-fobjc-arc", "-fmodules", f"-fmodules-cache-path={output / 'modules'}", "-Wall", "-Wextra", "-Werror", "-I", str(package / "ios/Session"), "-I", str(package / "ios/Identity")]
host = output / "identity-request-test"
objects = []
for unit in [*sources, fixture]:
    obj = output / (unit.stem + ".o")
    run(["xcrun", "clang", *common, "-c", str(unit), "-o", str(obj)])
    objects.append(obj)
swift_object = output / "errors.o"
run(["xcrun", "swiftc", "-parse-as-library", "-warnings-as-errors", "-emit-object", str(swift), "-o", str(swift_object)])
run(["xcrun", "swiftc", *map(str, objects), str(swift_object), "-o", str(host)])
env = dict(os.environ, ICM_TEST_DIRECTORY=str(output / "fixture-files"))
result = run([str(host)], env=env)
(output / "host-tests.json").write_text(result.stdout)
for sdk, arch, target in [("iphoneos", "arm64", "arm64-apple-ios15.0"), ("iphonesimulator", "arm64", "arm64-apple-ios15.0-simulator"), ("iphonesimulator", "x86_64", "x86_64-apple-ios15.0-simulator")]:
    sdk_path = run(["xcrun", "--sdk", sdk, "--show-sdk-path"]).stdout.strip()
    for source in sources:
        run(["xcrun", "--sdk", sdk, "clang", *common, "-target", target, "-isysroot", sdk_path, "-c", str(source), "-o", str(output / f"{sdk}-{arch}-{source.stem}.o")])
receipt = {"status":"SOURCE_COMPILED_FIXTURE_TESTED_PRODUCT_UNLINKED", "hostBehavior":json.loads(result.stdout), "originalSDKCompileOrLinkInput":False, "productIdentitySingletonRestored":False, "testOnlyIdentitySubstitute":True, "testOnlyEngineSink":True, "hostDefaultPreferencesAccessed":False, "fixtureDirectory":env["ICM_TEST_DIRECTORY"], "inputs":{str(p.relative_to(package)):hashlib.sha256(p.read_bytes()).hexdigest() for p in [*sources, fixture, swift, *sorted((package / "ios/Session").glob("*.h"))]}, "commands":commands}
(output / "receipt.json").write_text(json.dumps(receipt, indent=2) + "\n")
print(f"PASS: {receipt['hostBehavior']['testCount']} Auth mapping/multipart identity assertions; 3 iOS targets; product session UNLINKED")
print(output / "receipt.json")
