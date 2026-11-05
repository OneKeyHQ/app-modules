#!/usr/bin/env python3
"""Strict-link the real image pipeline/upload units without the incomplete UI SDK."""
import hashlib
import json
import pathlib
import subprocess

package = pathlib.Path(__file__).resolve().parents[1]
aggregate = package / "node_modules/.cache/ios-source-aggregate"
output = package / "node_modules/.cache/ios-image-engine-component"
output.mkdir(parents=True, exist_ok=True)
prior = json.loads((aggregate / "receipt.json").read_text())
drift = [name for name, digest in prior["inputs"].items()
         if hashlib.sha256((package / name).read_bytes()).hexdigest() != digest]
if drift or not all(target["sourceCompiled"] for target in prior["targets"]):
    raise SystemExit("Run ios-link-source-aggregate.py against the current sources first")
objc_names = ["ITBImageLoader", "ITBAnimatedImageView", "ITBImageUtils", "ITBImagePreviewMetadata",
              "ITBUpload", "ITBImageFileLoader", "ITBUploadProgressButton", "ICMEngine"]
swift_sources = [package / "ios/Session" / f"{name}.swift" for name in
                 ["ImageCaches", "AnimatedImage", "ImageDownloader", "ImageLoaderCore", "Dispatcher", "ShimmeringView", "MultipartTaskBuilder"]]
receipt = {"status": "RUNNING", "scope": "Real image caches/download/decoder/animated UIView, upload model/file JPEG conversion/progress button, shimmer and actual generic Engine/SessionDelegate/multipart transport; excludes ImageBlockView/LoadingView requiring the still-incomplete Blocks factory and full SDK controllers",
           "fullSDKStatus": "NOT_RESTORED", "runtimeBehaviorVerified": False,
           "fixtureImplementationsUsed": False, "originalSDKCompileOrLinkInput": False,
           "objcSourceUnits": objc_names, "swiftSourceUnits": [str(p.relative_to(package)) for p in swift_sources],
           "aggregateReceiptSHA256": hashlib.sha256((aggregate / "receipt.json").read_bytes()).hexdigest(),
           "inputs": prior["inputs"], "sourceHashDrift": drift, "targets": []}
for sdk, arch, target in [("iphoneos", "arm64", "arm64-apple-ios15.0"),
                          ("iphonesimulator", "arm64", "arm64-apple-ios15.0-simulator"),
                          ("iphonesimulator", "x86_64", "x86_64-apple-ios15.0-simulator")]:
    sdk_path = subprocess.check_output(["xcrun", "--sdk", sdk, "--show-sdk-path"], text=True).strip()
    swift_object = output / f"{sdk}-{arch}-swift.o"
    compile_command = ["xcrun", "--sdk", sdk, "swiftc", "-swift-version", "5", "-module-name", "Intercom",
                       "-parse-as-library", "-warnings-as-errors", "-whole-module-optimization", "-target", target,
                       "-sdk", sdk_path, "-emit-object", *map(str, swift_sources), "-o", str(swift_object)]
    compiled = subprocess.run(compile_command, capture_output=True, text=True)
    (output / f"{sdk}-{arch}-swift.log").write_text(compiled.stdout + compiled.stderr)
    objects = [aggregate / f"{sdk}-{arch}-{name}.o" for name in objc_names] + [swift_object]
    artifact = output / f"{sdk}-{arch}-ImagePipelineAndUpload.dylib"
    command = ["xcrun", "--sdk", sdk, "swiftc", "-target", target, "-sdk", sdk_path, "-emit-library", *map(str, objects),
               "-framework", "Foundation", "-framework", "UIKit", "-framework", "AVFoundation", "-framework", "UniformTypeIdentifiers", "-o", str(artifact)]
    result = subprocess.run(command, capture_output=True, text=True) if compiled.returncode == 0 else compiled
    (output / f"{sdk}-{arch}-link.log").write_text(result.stdout + result.stderr)
    images = subprocess.check_output(["otool", "-L", str(artifact)], text=True) if result.returncode == 0 else ""
    receipt["targets"].append({"target": target, "sourceCompiled": compiled.returncode == 0, "linked": result.returncode == 0,
                               "compileCommand": compile_command, "linkCommand": command,
                               "objectSHA256": {str(p): hashlib.sha256(p.read_bytes()).hexdigest() for p in objects if p.exists()},
                               "artifactSHA256": hashlib.sha256(artifact.read_bytes()).hexdigest() if result.returncode == 0 else None,
                               "linkedImages": images})
receipt["status"] = "SOURCE_COMPONENT_LINKED_RUNTIME_UNVERIFIED" if all(t["linked"] for t in receipt["targets"]) else "SOURCE_COMPONENT_UNLINKED"
(output / "receipt.json").write_text(json.dumps(receipt, indent=2) + "\n")
print(receipt["status"])
print(output / "receipt.json")
if not all(t["linked"] for t in receipt["targets"]):
    raise SystemExit(1)
