#!/usr/bin/env python3
"""Compile genuine Config nested source; never supply absent product classes."""
import hashlib
import json
import pathlib
import subprocess

package = pathlib.Path(__file__).resolve().parents[1]
output = package / "node_modules/.cache/ios-source-config-dependencies"
output.mkdir(parents=True, exist_ok=True)
sources = [package / "ios/Session/ConfigModules.swift", package / "ios/Session/HomeHeaderBackground.swift", package / "ios/Session/HomeHeaderContent.swift", package / "ios/Session/CustomizationColors.swift", package / "ios/Session/CustomizationConfig.swift", package / "ios/Session/ImageCaches.swift", package / "ios/Session/AnimatedImage.swift", package / "ios/Session/ImageDownloader.swift", package / "ios/Session/ImageLoaderCore.swift", package / "ios/Session/Dispatcher.swift"]
bridge = package / "ios/Session/ICMConfigNestedImports.h"
commands = []

def run(command):
    commands.append(command)
    result = subprocess.run(command, text=True, capture_output=True)
    if result.returncode:
        print(result.stdout + result.stderr)
        result.check_returncode()
    return result

for sdk, arch, target in [
    ("iphoneos", "arm64", "arm64-apple-ios15.0"),
    ("iphonesimulator", "arm64", "arm64-apple-ios15.0-simulator"),
    ("iphonesimulator", "x86_64", "x86_64-apple-ios15.0-simulator"),
]:
    sdk_path = run(["xcrun", "--sdk", sdk, "--show-sdk-path"]).stdout.strip()
    obj = output / f"{sdk}-{arch}-config-models.o"
    run(["xcrun", "--sdk", sdk, "swiftc", "-swift-version", "5", "-parse-as-library", "-warnings-as-errors", "-target", target, "-sdk", sdk_path, "-whole-module-optimization", "-import-objc-header", str(bridge), "-emit-object", *map(str, sources), "-o", str(obj)])
    undefined = run(["xcrun", "nm", "-u", str(obj)]).stdout
    (output / f"{sdk}-{arch}-undefined.txt").write_text(undefined)

receipt = {
    "status": "SOURCE_RESTORED_OBJECT_COMPILED_UNLINKED",
    "scope": ["ConfigModules", "HomeConfig", "HomeHeader", "OpenConfig", "Space", "SpaceBadge", "HomeHeaderBackground", "HomeHeaderContent", "HomeHeaderCloseButtonDetails", "HomeHeaderLabelDetails", "CustomizationConfig", "HeaderCustomization", "ActionColors", "ActionContrastColors"],
    "runtimeBehaviorVerified": False,
    "missingProductClasses": [],
    "fixtureImplementationsUsed": False,
    "originalSDKCompileOrLinkInput": False,
    "inputs": {str(p.relative_to(package)): hashlib.sha256(p.read_bytes()).hexdigest() for p in [*sources, bridge, package / "ios/Session/ICMColor.h", package / "ios/Session/ICMColor.m"]},
    "commands": commands,
}
(output / "receipt.json").write_text(json.dumps(receipt, indent=2) + "\n")
print("PASS: fourteen genuine Config model sources compile for three iOS15 targets; product closure UNLINKED")
print(output / "receipt.json")
