#!/usr/bin/env python3
"""Compile recovered session source; deliberately do not claim a linked SDK."""
import hashlib
import json
import pathlib
import subprocess
import sys

package = pathlib.Path(__file__).resolve().parents[1]
output = package / "node_modules/.cache/ios-source-session"
output.mkdir(parents=True, exist_ok=True)
sources = sorted((package / "ios/Session").glob("*.m"))
commands, objects = [], []

def run(command):
    commands.append(command)
    result = subprocess.run(command, text=True, capture_output=True)
    if result.returncode:
        print(result.stdout + result.stderr, file=sys.stderr)
        result.check_returncode()
    return result

for sdk, arch, target in [("iphoneos", "arm64", "arm64-apple-ios15.0"), ("iphonesimulator", "arm64", "arm64-apple-ios15.0-simulator"), ("iphonesimulator", "x86_64", "x86_64-apple-ios15.0-simulator")]:
    sdk_path = run(["xcrun", "--sdk", sdk, "--show-sdk-path"]).stdout.strip()
    for source in sources:
        obj = output / f"{sdk}-{arch}-{source.stem}.o"
        run(["xcrun", "--sdk", sdk, "clang", "-target", target, "-isysroot", sdk_path, "-fobjc-arc", "-fmodules", f"-fmodules-cache-path={output / 'modules'}", "-Wall", "-Wextra", "-Werror", "-c", str(source), "-o", str(obj)])
        objects.append(obj)
undefined = run(["xcrun", "nm", "-u", *map(str, objects)]).stdout
(output / "undefined-symbols.txt").write_text(undefined)
receipt = {"status": "SOURCE_COMPILED_UNLINKED", "scope": "Recovered Session Objective-C objects, including real DataManager/Nexus/HTTP response application; no linked complete session or SDK",
           "missingProductImplementations": ["ICMHTTPBridge remaining original selectors", "ICMBridge complete implementation", "UnreadConversations/UnreadTickets real DataManager/Conversation dependencies and Carousel/Survey response models", "Presentation/Carousel/Survey services, Conversation/Open/related models and aggregate Store/Bridge integration"], "originalSDKCompileOrLinkInput": False,
           "inputs": {str(p.relative_to(package)): hashlib.sha256(p.read_bytes()).hexdigest() for p in sorted([*(package / 'ios/Session').glob('*.[hm]'), *(package / 'ios/Session').glob('*.inc')])}, "commands": commands}
(output / "receipt.json").write_text(json.dumps(receipt, indent=2) + "\n")
print(f"SOURCE_COMPILED_UNLINKED: {len(sources)} source units compiled for 3 iOS targets; product dependencies remain missing")
print(output / "receipt.json")
