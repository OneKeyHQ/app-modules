#!/usr/bin/env python3
"""Strict-link recovered native original Blocks/renderers and real image/card dependencies."""
import hashlib
import json
import pathlib
import subprocess

package = pathlib.Path(__file__).resolve().parents[1]
aggregate = package / "node_modules/.cache/ios-source-aggregate"
output = package / "node_modules/.cache/ios-native-engine-blocks-component"
output.mkdir(parents=True, exist_ok=True)
prior = json.loads((aggregate / "receipt.json").read_text())
drift = [name for name, digest in prior["inputs"].items()
         if hashlib.sha256((package / name).read_bytes()).hexdigest() != digest]
if drift or not all(target["sourceCompiled"] for target in prior["targets"]):
    raise SystemExit("Run ios-link-source-aggregate.py against the current sources first")
names = ["ITBBlock", "ITBBlockView", "ITBTextBlock", "ITBHTMLParser", "ITBInlineCitation",
         "ITBInlineCitationParser", "ITBHyperlinkTextView", "ITBTextBlockView", "ITBHeadingBlock",
         "ITBListBlock", "ITBCodeBlock", "ITBButtonBlock", "ITBButtonBlockView", "ITBSocialButtonBlocks",
         "ITBAttachmentBlock", "ITBAttachmentBlockView", "ITBBlockViewsReuseManager",
         "ITBImageLoader", "ITBAnimatedImageView", "ITBImageUtils", "ITBImagePreviewMetadata",
         "ITBUpload", "ITBImageFileLoader", "ITBUploadProgressButton", "ICMEngine",
         "ITBLinkBlock", "ITBLinkBlockModels", "ITBLinkListBlock", "ITBLinkBlockView",
         "ITBLinkListBlockView", "ITBLinkBlockAuthorView", "ITBLinkListChildViews", "ITBFooterContainerView",
         "ITBTypingBlock", "ITBNotificationCardBlock", "ITBNotificationCardBlockView",
         "ICPCardFallbackModel", "ITBMessengerCardFallbackBlock", "ITBMessengerCardFallbackView",
         "ICPTextInputPayload", "ICPCardFallbackJSBuilder", "ICPLoadingView", "ICPMessengerCardFallbackView"]
image_output = package / "node_modules/.cache/ios-image-engine-component"
image_receipt = json.loads((image_output / "receipt.json").read_text())
if (image_receipt["aggregateReceiptSHA256"] != hashlib.sha256((aggregate / "receipt.json").read_bytes()).hexdigest()
        or not all(target["linked"] for target in image_receipt["targets"])):
    raise SystemExit("Run ios-link-image-component.py against the current aggregate first")
receipt = {"status": "RUNNING", "scope": "Original native text/buttons/attachments/links/typing/notification/messenger-card and genuine image/upload/shimmer and actual generic Engine/multipart dependencies; excludes incomplete factory, ImageBlockView/LoadingView and full SDK or normal consumer",
           "fullSDKStatus": "NOT_RESTORED", "runtimeBehaviorVerified": False,
           "fixtureImplementationsUsed": False, "originalSDKCompileOrLinkInput": False,
           "objcSourceUnits": names, "swiftSourceUnits": image_receipt["swiftSourceUnits"],
           "imageComponentReceiptSHA256": hashlib.sha256((image_output / "receipt.json").read_bytes()).hexdigest(), "aggregateReceiptSHA256": hashlib.sha256((aggregate / "receipt.json").read_bytes()).hexdigest(),
           "inputs": prior["inputs"], "sourceHashDrift": drift, "targets": []}
for sdk, arch, target in [("iphoneos", "arm64", "arm64-apple-ios15.0"),
                          ("iphonesimulator", "arm64", "arm64-apple-ios15.0-simulator"),
                          ("iphonesimulator", "x86_64", "x86_64-apple-ios15.0-simulator")]:
    sdk_path = subprocess.check_output(["xcrun", "--sdk", sdk, "--show-sdk-path"], text=True).strip()
    objects = [aggregate / f"{sdk}-{arch}-{name}.o" for name in names] + [image_output / f"{sdk}-{arch}-swift.o"]
    artifact = output / f"{sdk}-{arch}-NativeBlocksAndImages.dylib"
    command = ["xcrun", "--sdk", sdk, "swiftc", "-target", target, "-sdk", sdk_path,
               "-emit-library", *map(str, objects), "-framework", "Foundation", "-framework", "UIKit",
               "-framework", "AVFoundation", "-framework", "UniformTypeIdentifiers", "-framework", "WebKit", "-lxml2", "-o", str(artifact)]
    result = subprocess.run(command, capture_output=True, text=True)
    (output / f"{sdk}-{arch}-link.log").write_text(result.stdout + result.stderr)
    images = subprocess.check_output(["otool", "-L", str(artifact)], text=True) if result.returncode == 0 else ""
    receipt["targets"].append({"target": target, "linked": result.returncode == 0, "command": command,
                               "objectSHA256": {str(p): hashlib.sha256(p.read_bytes()).hexdigest() for p in objects},
                               "artifactSHA256": hashlib.sha256(artifact.read_bytes()).hexdigest() if result.returncode == 0 else None,
                               "linkedImages": images})
receipt["status"] = "SOURCE_COMPONENT_LINKED_RUNTIME_UNVERIFIED" if all(t["linked"] for t in receipt["targets"]) else "SOURCE_COMPONENT_UNLINKED"
(output / "receipt.json").write_text(json.dumps(receipt, indent=2) + "\n")
print(receipt["status"])
print(output / "receipt.json")
if not all(t["linked"] for t in receipt["targets"]):
    raise SystemExit(1)
