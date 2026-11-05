#!/usr/bin/env python3
"""Attempt the genuine aggregate Config link, without fixture or vendor fallback."""
import hashlib
import json
import pathlib
import subprocess

package = pathlib.Path(__file__).resolve().parents[1]
output = package / "node_modules/.cache/ios-source-config-link"
output.mkdir(parents=True, exist_ok=True)
objc = [package / "ios/Identity" / f"{name}.m" for name in ["ICMSafeNetworkModel", "ICMPersistenceProxy", "ICMUtils", "ICMLogger", "IntercomNexusConfiguration"]]
objc += [package / "ios/Session" / f"{name}.m" for name in ["ICMConfig", "ICMConfig+ImagesAndUI", "ITBImageLoader", "ICMColor", "ICMDataManager", "IntercomNexusClient", "IntercomNexusConnection", "IntercomNexusSocket", "IntercomNexusEvent"]]
swift = [package / "ios/Session" / f"{name}.swift" for name in ["AttachmentSettings", "ConversationStateSyncSettings", "ConfigModules", "HomeHeaderBackground", "HomeHeaderContent", "CustomizationColors", "CustomizationConfig", "ImageCaches", "AnimatedImage", "ImageDownloader", "ImageLoaderCore", "Dispatcher", "UnreadManager"]]
bridge = package / "ios/Session/ICMConfigSourceImports.h"
commands, results, image_results = [], [], []

def run(command, *, allow_failure=False):
    commands.append(command)
    result = subprocess.run(command, text=True, capture_output=True)
    if result.returncode and not allow_failure:
        print(result.stdout + result.stderr)
        result.check_returncode()
    return result

for sdk, arch, target in [
    ("iphoneos", "arm64", "arm64-apple-ios15.0"),
    ("iphonesimulator", "arm64", "arm64-apple-ios15.0-simulator"),
    ("iphonesimulator", "x86_64", "x86_64-apple-ios15.0-simulator"),
]:
    sdk_path = run(["xcrun", "--sdk", sdk, "--show-sdk-path"]).stdout.strip()
    objects = []
    for source in objc:
        obj = output / f"{sdk}-{arch}-{source.stem}.o"
        run(["xcrun", "--sdk", sdk, "clang", "-target", target, "-isysroot", sdk_path, "-fobjc-arc", "-fmodules", f"-fmodules-cache-path={output / 'modules'}", "-Wall", "-Wextra", "-Werror", "-c", str(source), "-o", str(obj)])
        objects.append(obj)
    swift_obj = output / f"{sdk}-{arch}-swift.o"
    run(["xcrun", "--sdk", sdk, "swiftc", "-swift-version", "5", "-parse-as-library", "-warnings-as-errors", "-target", target, "-sdk", sdk_path, "-whole-module-optimization", "-import-objc-header", str(bridge), "-emit-object", *map(str, swift), "-o", str(swift_obj)])
    objects.append(swift_obj)
    run_result = run(["xcrun", "--sdk", sdk, "swiftc", "-target", target, "-sdk", sdk_path, "-emit-library", *map(str, objects), "-framework", "UIKit", "-framework", "Foundation", "-framework", "Network", "-o", str(output / f"{sdk}-{arch}-Config.dylib")], allow_failure=True)
    log = output / f"{sdk}-{arch}-link.log"
    log.write_text(run_result.stdout + run_result.stderr)
    results.append({"target": target, "linked": run_result.returncode == 0, "exitCode": run_result.returncode, "log": str(log)})
    image_sources = [package / "ios/Session" / f"{name}.swift" for name in ["ImageCaches", "AnimatedImage", "ImageDownloader", "ImageLoaderCore", "Dispatcher"]]
    image_library = output / f"{sdk}-{arch}-ImagePipeline.dylib"
    image_result = run(["xcrun", "--sdk", sdk, "swiftc", "-swift-version", "5", "-parse-as-library", "-warnings-as-errors", "-whole-module-optimization", "-target", target, "-sdk", sdk_path, "-emit-library", *map(str, image_sources), "-o", str(image_library)])
    (output / f"{sdk}-{arch}-image-link.log").write_text(image_result.stdout + image_result.stderr)
    (output / f"{sdk}-{arch}-image-dependencies.txt").write_text(run(["xcrun", "otool", "-L", str(image_library)]).stdout)
    image_results.append({"target": target, "linked": True, "library": str(image_library)})

receipt = {
    "status": "AGGREGATE_SOURCE_LINKED" if all(r["linked"] for r in results) else "AGGREGATE_SOURCE_UNLINKED",
    "scope": "Actual Config, fourteen nested models, two Settings, genuine image pipeline/adapter/color methods, true primary DataManager/UnreadManager and real persistence/support; not complete SDK",
    "fixtureImplementationsUsed": False,
    "originalSDKCompileOrLinkInput": False,
    "targets": results,
    "imagePipelineSourceLink": image_results,
    "runtimeBehaviorVerified": False,
    "inputs": {str(p.relative_to(package)): hashlib.sha256(p.read_bytes()).hexdigest() for p in [*objc, *swift, bridge, *sorted((package / "ios/Session").glob("ICMDataManager+*.inc"))]},
    "commands": commands,
}
(output / "receipt.json").write_text(json.dumps(receipt, indent=2) + "\n")
print(receipt["status"])
for result in results:
    if not result["linked"]:
        print(pathlib.Path(result["log"]).read_text())
print(output / "receipt.json")
