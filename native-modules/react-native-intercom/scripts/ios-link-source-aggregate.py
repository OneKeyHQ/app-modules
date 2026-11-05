#!/usr/bin/env python3
"""Probe all recovered native sources without vendor, fixtures or undefined fallback."""
import hashlib
import json
import pathlib
import re
import subprocess

package = pathlib.Path(__file__).resolve().parents[1]
output = package / "node_modules/.cache/ios-source-aggregate"
output.mkdir(parents=True, exist_ok=True)
directories = [package / "ios" / name for name in ["Models", "Identity", "Session"]]
objc = sorted(p for directory in directories for p in directory.rglob("*.m"))
swift = sorted(p for directory in directories for p in directory.rglob("*.swift"))
implementation_fragments = sorted((package / "ios/Session").rglob("*.inc"))
headers = sorted((package / "ios").rglob("*.h"))
bridge = package / "ios/Session/ICMSourceAggregateImports.h"
frameworks = output / "frameworks"
framework = frameworks / "Intercom.framework"
for directory in ["Headers", "PrivateHeaders", "Modules"]:
    (framework / directory).mkdir(parents=True, exist_ok=True)
public_headers = {"IntercomSourceImports.h", "Intercom.h", "Models/ICMUserAttributes.h", "Models/ICMCompany.h", "Models/ICMHelpCenterCollection.h", "Models/ICMHelpCenterCollectionContent.h", "Models/ICMHelpCenterArticleSearchResult.h", "Models/ICMHelpCenterDataError.h", "Models/ICMHelpCenterArticle.h", "Models/ICMHelpCenterArticleAuthor.h", "Models/IntercomContent.h"}
for header in headers:
    relative = header.relative_to(package / "ios")
    staged = framework / ("Headers" if str(relative) in public_headers else "PrivateHeaders") / relative
    staged.parent.mkdir(parents=True, exist_ok=True)
    if not staged.exists():
        staged.symlink_to(header)
umbrella = framework / "Headers/ReactNativeIntercom-umbrella.h"
umbrella.write_text('#import <UIKit/UIKit.h>\n' + ''.join('#import <Intercom/' + name + '>\n' for name in sorted(public_headers)))
(framework / "Modules/module.modulemap").write_text('framework module Intercom {\n  umbrella header "ReactNativeIntercom-umbrella.h"\n  export *\n  module * { export * }\n}\n')
selector_inventory = package / "docs/IOS_EXPECTED_SELECTORS.json"
commands, results = [], []
receipt = {"status": "RUNNING", "fullSDKStatus": "NOT_RESTORED", "objcSourceUnits": len(objc), "swiftSourceUnits": len(swift), "fixtureImplementationsUsed": False, "originalSDKCompileOrLinkInput": False, "scaffoldIncluded": False, "runtimeBehaviorVerified": False, "sessionsCreatedByValidation": 0, "missingSelectorsWarning": "Selected own-selector presence is checked separately; ABI, protocol metadata, all other selectors and resources remain unverified", "inputs": {str(p.relative_to(package)): hashlib.sha256(p.read_bytes()).hexdigest() for p in [*objc, *swift, *headers, *implementation_fragments]}, "targets": results, "commands": commands}
receipt["selectorInventory"] = {"path": str(selector_inventory.relative_to(package)), "sha256": hashlib.sha256(selector_inventory.read_bytes()).hexdigest()}
def save():
    (output / "receipt.json").write_text(json.dumps(receipt, indent=2) + "\n")
def run(command):
    commands.append(command)
    return subprocess.run(command, text=True, capture_output=True)
def checked(command, log):
    result = run(command)
    log.write_text(result.stdout + result.stderr)
    return result.returncode == 0
save()
for sdk, arch, target in [("iphoneos", "arm64", "arm64-apple-ios15.0"), ("iphonesimulator", "arm64", "arm64-apple-ios15.0-simulator"), ("iphonesimulator", "x86_64", "x86_64-apple-ios15.0-simulator")]:
    sdk_path = run(["xcrun", "--sdk", sdk, "--show-sdk-path"]).stdout.strip()
    entry = {"target": target, "sourceCompiled": False, "linked": False}
    results.append(entry)
    objects = []
    for source in objc:
        obj = output / f"{sdk}-{arch}-{source.stem}.o"
        log = output / f"{sdk}-{arch}-{source.stem}-compile.log"
        if not checked(["xcrun", "--sdk", sdk, "clang", "-target", target, "-isysroot", sdk_path, "-I" + sdk_path + "/usr/include/libxml2", "-DINTERCOM_SOURCE_BUILD=1", "-fobjc-arc", "-fmodules", f"-fmodules-cache-path={output / 'modules'}", "-Wall", "-Wextra", "-Werror", "-c", str(source), "-o", str(obj)], log):
            entry.update({"failedSource": str(source), "compileLog": str(log)})
            break
        objects.append(obj)
    else:
        symbols = run(["xcrun", "nm", *map(str, objects)]).stdout
        implemented = {}
        for kind, name, selector in re.findall(r' [tT] ([+-])\[([^\s(]+)(?:\([^)]*\))? ([^\]]+)\]', symbols):
            implemented.setdefault(name, set()).add(kind + selector)
        expected = json.loads(selector_inventory.read_text())["classes"]
        entry["selectedOwnSelectorCoverage"] = {
            name: {"expected": len(selectors), "present": len(set(selectors) & implemented.get(name, set())),
                   "missing": sorted(set(selectors) - implemented.get(name, set()))}
            for name, selectors in expected.items()
        }
        swift_obj = output / f"{sdk}-{arch}-swift.o"
        log = output / f"{sdk}-{arch}-swift-compile.log"
        if checked(["xcrun", "--sdk", sdk, "swiftc", "-module-name", "Intercom", "-swift-version", "5", "-parse-as-library", "-warnings-as-errors", "-target", target, "-sdk", sdk_path, "-whole-module-optimization", "-F", str(frameworks), "-import-underlying-module", "-Xcc", "-DINTERCOM_SOURCE_BUILD=1", "-Xcc", "-I" + str(package / "ios"), "-Xcc", "-I" + sdk_path + "/usr/include/libxml2", "-emit-object", *map(str, swift), "-o", str(swift_obj)], log):
            objects.append(swift_obj)
            entry["sourceCompiled"] = True
            log = output / f"{sdk}-{arch}-link.log"
            entry["linked"] = checked(["xcrun", "--sdk", sdk, "swiftc", "-target", target, "-sdk", sdk_path, "-emit-library", *map(str, objects), "-framework", "Foundation", "-framework", "UIKit", "-framework", "Network", "-framework", "Security", "-framework", "UserNotifications", "-framework", "AVFoundation", "-framework", "Photos", "-framework", "CoreLocation", "-framework", "AudioToolbox", "-framework", "AVKit", "-framework", "UniformTypeIdentifiers", "-framework", "WebKit", "-lxml2", "-o", str(output / f"{sdk}-{arch}-Recovered.dylib")], log)
            entry["linkLog"] = str(log)
            entry["undefinedObjCClasses"] = sorted(set(re.findall(r'"_OBJC_CLASS_\$_([^\"]+)"', log.read_text())))
        else:
            entry.update({"failedSource": "aggregate Swift implementation", "compileLog": str(log)})
    save()
missing_selected_selectors = any(coverage["missing"] for result in results for coverage in result.get("selectedOwnSelectorCoverage", {}).values())
receipt["status"] = "SOURCE_COMPILE_FAILED" if not all(r["sourceCompiled"] for r in results) else "SOURCE_AGGREGATE_LINKED_SELECTORS_INCOMPLETE" if all(r["linked"] for r in results) and missing_selected_selectors else "SOURCE_AGGREGATE_LINKED" if all(r["linked"] for r in results) else "SOURCE_AGGREGATE_UNLINKED"
current_files = sorted({*headers, *objc, *swift, *implementation_fragments})
current_hashes = {str(path.relative_to(package)): hashlib.sha256(path.read_bytes()).hexdigest() for path in current_files if path.exists()}
current_tree = sorted([*(package / "ios").rglob("*.h"), *(path for directory in directories for path in directory.rglob("*.m")), *(path for directory in directories for path in directory.rglob("*.swift")), *(package / "ios/Session").rglob("*.inc")])
receipt["inputHashDrift"] = [name for name, digest in receipt["inputs"].items() if current_hashes.get(name) != digest]
receipt["sourceFilesAddedDuringBuild"] = [str(path.relative_to(package)) for path in current_tree if str(path.relative_to(package)) not in receipt["inputs"]]
if receipt["inputHashDrift"] or receipt["sourceFilesAddedDuringBuild"]:
    receipt["compileStatusForCapturedInputs"] = receipt["status"]
    receipt["status"] = "SOURCE_INPUTS_CHANGED_DURING_BUILD"
save()
print(receipt["status"])
for result in results:
    print(result["target"], "compiled=" + str(result["sourceCompiled"]), "linked=" + str(result["linked"]), result.get("undefinedObjCClasses", result.get("compileLog")))
print(output / "receipt.json")
