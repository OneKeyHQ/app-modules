#!/usr/bin/env python3
"""Exercise the real Nexus configuration Foundation parser."""
import hashlib
import json
import os
import pathlib
import subprocess
import sys
package = pathlib.Path(__file__).resolve().parents[1]
output = package / "node_modules/.cache/ios-source-nexus"
output.mkdir(parents=True, exist_ok=True)
sources = [package / "ios/Identity/IntercomNexusConfiguration.m"]
fixture = package / "scripts/ios-nexus-tests.m"
commands = []
def run(command, **kwargs):
    commands.append(command)
    result = subprocess.run(command, text=True, capture_output=True, **kwargs)
    if result.returncode:
        print(result.stdout + result.stderr, file=sys.stderr)
        result.check_returncode()
    return result
common = ["-fobjc-arc", "-fmodules", f"-fmodules-cache-path={output / 'modules'}", "-Wall", "-Wextra", "-Werror", "-I", str(package / "ios/Identity")]
host = output / "nexus-test"
run(["xcrun", "clang", *common, "-framework", "Foundation", *map(str, sources), str(fixture), "-o", str(host)])
env = dict(os.environ, ICM_TEST_DIRECTORY=str(output / "fixture-files"))
result = run([str(host)], env=env)
(output / "host-tests.json").write_text(result.stdout)
for sdk, arch, target in [("iphoneos", "arm64", "arm64-apple-ios15.0"), ("iphonesimulator", "arm64", "arm64-apple-ios15.0-simulator"), ("iphonesimulator", "x86_64", "x86_64-apple-ios15.0-simulator")]:
    sdk_path = run(["xcrun", "--sdk", sdk, "--show-sdk-path"]).stdout.strip()
    for source in sources:
        run(["xcrun", "--sdk", sdk, "clang", *common, "-target", target, "-isysroot", sdk_path, "-c", str(source), "-o", str(output / f"{sdk}-{arch}-{source.stem}.o")])
receipt = {"status":"SOURCE_COMPILED_TESTED", "hostBehavior":json.loads(result.stdout), "originalSDKCompileOrLinkInput":False, "fixtureDirectory":env["ICM_TEST_DIRECTORY"], "inputs":{str(p.relative_to(package)):hashlib.sha256(p.read_bytes()).hexdigest() for p in [*sources, fixture]}, "commands":commands}
(output / "receipt.json").write_text(json.dumps(receipt, indent=2) + "\n")
print(f"PASS: {receipt['hostBehavior']['testCount']} Nexus assertions; 3 iOS targets; helpers only")
print(output / "receipt.json")
