#!/usr/bin/env python3
"""Build independent source SDK archives and force-load their complete consumers."""

import hashlib
import json
from pathlib import Path
import re
import shutil
import subprocess
import sys

from restore_ios_metadata import restore


ROOT = Path(__file__).resolve().parent.parent
BUILD = ROOT / ".build" / "ios"
TARGETS = (
    ("ios-arm64", "iphoneos", "arm64-apple-ios15.0"),
    ("ios-simulator-arm64", "iphonesimulator", "arm64-apple-ios15.0-simulator"),
    ("ios-simulator-x86_64", "iphonesimulator", "x86_64-apple-ios15.0-simulator"),
)
FRAMEWORKS = ("Foundation", "CoreFoundation", "UIKit", "CoreLocation", "UserNotifications", "SystemConfiguration", "Security", "CoreTelephony", "CFNetwork", "Network", "MobileCoreServices", "WebKit", "CoreGraphics")


def source_hashes():
    return {str(p.relative_to(ROOT)): hashlib.sha256(p.read_bytes()).hexdigest()
            for p in sorted((ROOT / "source/ios").rglob("*")) if p.is_file()}


def run(command, log):
    with log.open("w") as stream:
        return subprocess.run(command, stdout=stream, stderr=subprocess.STDOUT).returncode


def build(receipt):
    sources = sorted((ROOT / "source/ios").glob("j*/*.m"))
    if not sources:
        raise RuntimeError("No iOS Objective-C sources found")
    before = source_hashes()
    receipt.update({"source_files": len(sources), "source_sha256": before, "targets": []})
    failed_links = []
    for label, sdk, target in TARGETS:
        directory = BUILD / label
        if directory.exists():
            shutil.rmtree(directory)
        directory.mkdir(parents=True)
        sdkpath = subprocess.check_output(["xcrun", "--sdk", sdk, "--show-sdk-path"], text=True).strip()
        compiler = ["xcrun", "clang", "-isysroot", sdkpath, "-target", target, "-fobjc-arc"]
        result = {"target": label, "compile_status": "FAIL", "link_status": "NOT_RUN", "compilations": []}
        receipt["targets"].append(result)
        objects = []
        for source in sources:
            objectfile = directory / f"{source.parent.name}-{source.stem}.o"
            log = directory / f"compile-{source.stem}.log"
            command = compiler + ["-Wall", "-Wextra", "-Werror", "-Wno-unused-parameter", "-I", str(ROOT / "source/ios/include"), "-c", str(source), "-o", str(objectfile)]
            code = run(command, log)
            compilation = {"source": str(source.relative_to(ROOT)), "exit_code": code, "log": str(log.relative_to(ROOT))}
            result["compilations"].append(compilation)
            if code:
                raise RuntimeError(f"Source compilation failed; see {log}")
            compilation["metadata_restoration"] = restore(objectfile)
            objects.append(objectfile)
        result["compile_status"] = "PASS"
        archives = {}
        result["archives"] = archives
        for sdk_name, archive_name in (("jcore", "libJCore.a"), ("jpush", "libJPush.a")):
            archive = directory / archive_name
            owned_objects = [p for p in objects if p.name.startswith(sdk_name + "-")]
            archive_log = directory / (sdk_name + "-archive.log")
            if run(["xcrun", "libtool", "-static", "-o", str(archive)] + [str(p) for p in owned_objects], archive_log):
                raise RuntimeError(f"Archive creation failed; see {archive_log}")
            archives[sdk_name] = {"path": str(archive.relative_to(ROOT)), "sha256": hashlib.sha256(archive.read_bytes()).hexdigest(), "objects": [p.name for p in owned_objects]}
        # Every JCore member must link on its own; JPush consumes JCore as a separate archive.
        # The independent consumer includes public headers from the package and never executes.
        result["consumers"] = []
        undefined = set()
        for consumer_name, sdk_names in (("jcore", ("jcore",)), ("jpush", ("jcore", "jpush"))):
            consumer = directory / (consumer_name + "-consumer.m")
            body = '#import "JGCOREAPI.h"\n'
            if consumer_name == "jpush":
                body += '#import "JPUSHService.h"\nint main(void) { @autoreleasepool { [JGCOREAPI ci:NO]; return [JPUSHService registrationID] != nil; } }\n'
            else:
                body += 'int main(void) { @autoreleasepool { [JGCOREAPI ci:NO]; return 0; } }\n'
            consumer.write_text(body)
            command = compiler + ["-Wall", "-Wextra", "-Werror", "-I", str(ROOT / "source/ios/include"), str(consumer), "-Wl,-undefined,error", "-lresolv", "-lz"]
            for sdk_name in sdk_names:
                command += ["-Wl,-force_load," + str(ROOT / archives[sdk_name]["path"])]
            for framework in FRAMEWORKS:
                command += ["-framework", framework]
            command += ["-o", str(directory / (consumer_name + "-consumer"))]
            log = directory / (consumer_name + "-consumer.log")
            code = run(command, log)
            result["consumers"].append({"sdk": consumer_name, "status": "PASS" if code == 0 else "FAIL", "command": command, "exit_code": code, "log": str(log.relative_to(ROOT)), "executed": False})
            if code:
                undefined.update(re.findall(r'^\s+"([^"]+)", referenced from:', log.read_text(), re.MULTILINE))
                failed_links.append(label + "/" + consumer_name)
        result["undefined_symbols"] = sorted(undefined)
        result["link_status"] = "PASS" if all(c["status"] == "PASS" for c in result["consumers"]) else "FAIL"
    if source_hashes() != before:
        raise RuntimeError("Build inputs changed during compilation; results are not a stable source build")
    receipt["source_inputs_unchanged"] = True
    if failed_links:
        raise RuntimeError(f"Strict force-load consumer link failed for {', '.join(failed_links)}; SDK closure is incomplete")
    receipt["status"] = "PASS"


def main():
    BUILD.mkdir(parents=True, exist_ok=True)
    receipt = {
        "status": "FAIL",
        "original_objects_linked": False,
        "scope": "Independent JCore/JPush source archives with strict force-load public-header consumer links. Runtime equivalence and application/device integration remain separate checks.",
    }
    try:
        build(receipt)
    except (OSError, RuntimeError, subprocess.SubprocessError) as error:
        receipt["error"] = str(error)
    (BUILD / "build.json").write_text(json.dumps(receipt, indent=2) + "\n")
    print(json.dumps({key: value for key, value in receipt.items() if key not in ("source_sha256", "targets")}, indent=2))
    for target in receipt.get("targets", []):
        print(f"{target['target']}: compile={target['compile_status']}, strict-link={target['link_status']}")
        for symbol in target.get("undefined_symbols", []):
            print(f"  unresolved: {symbol}")
    return 0 if receipt["status"] == "PASS" else 1


if __name__ == "__main__":
    sys.exit(main())
