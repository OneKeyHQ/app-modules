#!/usr/bin/env python3
"""Verify partial Oreo request helpers without networking or vendor dependencies."""
import hashlib
import json
import pathlib
import subprocess
import sys

package = pathlib.Path(__file__).resolve().parents[1]
output = package / "node_modules/.cache/ios-source-oreo-request"
output.mkdir(parents=True, exist_ok=True)
source = package / "ios/Session/ICMOreoEngine.m"
fixture = package / "scripts/ios-oreo-request-tests.m"
swift = package / "ios/Session/MultipartTaskBuilder.swift"
dependencies = [package / "ios/Session" / (name + ".m") for name in ["ICMAppInstallationFingerprint", "ICMOreoShutdownState"]]
dependencies += [package / "ios/Identity" / (name + ".m") for name in ["ICMUtils", "ICMPersistenceProxy"]]
commands = []
common = ["-fobjc-arc", "-fmodules", f"-fmodules-cache-path={output / 'modules'}", "-Wall", "-Wextra", "-Werror", "-I", str(source.parent)]

def run(command):
    commands.append(command)
    result = subprocess.run(command, text=True, capture_output=True)
    if result.returncode:
        print(result.stdout + result.stderr, file=sys.stderr)
        result.check_returncode()
    return result

host = output / "oreo-request-test"
objects = []
for unit in [source, *dependencies, fixture]:
    obj = output / (unit.stem + ".o")
    run(["xcrun", "clang", *common, "-c", str(unit), "-o", str(obj)])
    objects.append(obj)
swift_object = output / "multipart.o"
run(["xcrun", "swiftc", "-parse-as-library", "-warnings-as-errors", "-emit-object", str(swift), "-o", str(swift_object)])
run(["xcrun", "swiftc", *map(str, objects), str(swift_object), "-o", str(host)])
result = run([str(host)])
(output / "host-tests.json").write_text(result.stdout)
for sdk, arch, target in [("iphoneos", "arm64", "arm64-apple-ios15.0"), ("iphonesimulator", "arm64", "arm64-apple-ios15.0-simulator"), ("iphonesimulator", "x86_64", "x86_64-apple-ios15.0-simulator")]:
    sdk_path = run(["xcrun", "--sdk", sdk, "--show-sdk-path"]).stdout.strip()
    run(["xcrun", "--sdk", sdk, "swiftc", "-parse-as-library", "-warnings-as-errors", "-target", target, "-sdk", sdk_path, "-emit-object", str(swift), "-o", str(output / f"{sdk}-{arch}-multipart.o")])
    run(["xcrun", "--sdk", sdk, "clang", *common, "-target", target, "-isysroot", sdk_path, "-c", str(source), "-o", str(output / f"{sdk}-{arch}-oreo-request.o")])
receipt = {"status": "PARTIAL_CLASS_SOURCE_COMPILED_TESTED", "scope": "Oreo request helpers only; link-only fixture identity dependency; product session remains unlinked", "hostBehavior": json.loads(result.stdout), "originalSDKCompileOrLinkInput": False,
           "inputs": {str(p.relative_to(package)): hashlib.sha256(p.read_bytes()).hexdigest() for p in [source, source.with_suffix('.h'), fixture, swift, source.parent / "ICMMultipartTaskBuilder.h", *dependencies]}, "commands": commands}
(output / "receipt.json").write_text(json.dumps(receipt, indent=2) + "\n")
print(f"PASS: {receipt['hostBehavior']['testCount']} Oreo request assertions; source compiled for 3 iOS targets")
print(output / "receipt.json")
