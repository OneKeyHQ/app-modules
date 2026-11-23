#!/usr/bin/env python3
from __future__ import annotations

import subprocess
import sys
import tempfile
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
BUILD_SCRIPT = ROOT / "scripts" / "build_ios_source.py"
SOURCE_ROOT = ROOT / "ios"
INCLUDE_DIR = SOURCE_ROOT / "include"
JCORE_DIR = SOURCE_ROOT / "jcore"
JPUSH_DIR = SOURCE_ROOT / "jpush"
BRIDGE_DIR = SOURCE_ROOT / "bridge"

JCORE_RN_IOS_DIR = BRIDGE_DIR
JPUSH_RN_IOS_DIR = BRIDGE_DIR

FORBIDDEN_SYMBOLS = (
    "UIPasteboard",
    "ASIdentifierManager",
    "CNCopyCurrentNetworkInfo",
    "evaluateJavaScript",
    "CLLocationManager",
    "kCFStreamSSLValidatesCertificateChain",
)

REQUIRED_CLASSES = (
    "_OBJC_CLASS_$_JGInforCollectionAuth",
    "_OBJC_CLASS_$_JGInforCollectionAuthItems",
    "_OBJC_CLASS_$_JGCOREAPI",
    "_OBJC_CLASS_$_JCOREPersistence",
    "_OBJC_CLASS_$_JCORETcpClient",
    "_OBJC_CLASS_$_JPUSHRegisterEntity",
    "_OBJC_CLASS_$_JPushNotificationIdentifier",
    "_OBJC_CLASS_$_JPushNotificationSound",
    "_OBJC_CLASS_$_JPushNotificationContent",
    "_OBJC_CLASS_$_JPushNotificationTrigger",
    "_OBJC_CLASS_$_JPushNotificationRequest",
    "_OBJC_CLASS_$_JPushInAppMessage",
    "_OBJC_CLASS_$_JPushCollectControl",
    "_OBJC_CLASS_$_JPUSHService",
)

REQUIRED_HEADERS = (
    "JGCOREAPI.h",
    "JGInforCollectionAuth.h",
    "JPUSHService.h",
    "JCOREService.h",
    "JGInforCollectionAuthItems.h",
    "JGPushNotificationTrigger.h",
)


def run(cmd: list[str]) -> str:
    proc = subprocess.run(cmd, text=True, capture_output=True)
    if proc.returncode != 0:
        raise RuntimeError(
            f"Command failed ({proc.returncode}): {' '.join(cmd)}\n"
            f"STDOUT:\n{proc.stdout}\nSTDERR:\n{proc.stderr}"
        )
    return proc.stdout


def sdk_path(sdk_name: str) -> str:
    return run(["xcrun", "--sdk", sdk_name, "--show-sdk-path"]).strip()


def verify_universal_archives_and_rn_consumer(universal_dir: Path, temp_root: Path) -> None:
    print("==> Step 1: Building universal iOS static archives (iphoneos-arm64 + iphonesimulator-x86_64)...")
    run([sys.executable, str(BUILD_SCRIPT), "--output-dir", str(universal_dir), "--target", "universal"])

    jcore_a = universal_dir / "jcore-ios-5.2.0.a"
    jpush_a = universal_dir / "jpush-ios-6.0.0.a"
    for archive in (jcore_a, jpush_a):
        if not archive.exists():
            raise RuntimeError(f"Missing expected archive: {archive}")
        lipo_info = run(["xcrun", "lipo", "-info", str(archive)])
        if "arm64" not in lipo_info or "x86_64" not in lipo_info:
            raise RuntimeError(f"Expected arm64 + x86_64 in {archive.name}, got: {lipo_info}")

    for hdr in REQUIRED_HEADERS:
        hdr_path = universal_dir / "include" / hdr
        if not hdr_path.exists():
            raise RuntimeError(f"Missing required public header: {hdr_path}")

    nm_out = run(["xcrun", "nm", "-g", str(jcore_a), str(jpush_a)])
    for cls in REQUIRED_CLASSES:
        if cls not in nm_out:
            raise RuntimeError(f"Missing exported ObjC class {cls} in static archives")

    strings_out = run(["strings", str(jcore_a), str(jpush_a)])
    for forbidden in FORBIDDEN_SYMBOLS:
        if forbidden in nm_out or forbidden in strings_out:
            raise RuntimeError(f"Forbidden symbol/string '{forbidden}' found in built archives")

    # Compile actual jcore-react-native and jpush-react-native iOS sources against built headers
    rn_stubs_dir = ROOT / "tests/ios/react-stubs"

    rn_sources = [
        JCORE_RN_IOS_DIR / "RCTJCoreModule.m",
        JPUSH_RN_IOS_DIR / "RCTJPushEventQueue.m",
        JPUSH_RN_IOS_DIR / "RCTJPushModule.m",
    ]
    sim_sdk = sdk_path("iphonesimulator")
    for src in rn_sources:
        if not src.exists():
            raise RuntimeError(f"Missing RN iOS source file: {src}")
        obj_out = temp_root / f"{src.stem}.o"
        run([
            "xcrun",
            "--sdk",
            "iphonesimulator",
            "clang",
            "-x",
            "objective-c",
            "-arch",
            "x86_64",
            "-isysroot",
            sim_sdk,
            "-mios-simulator-version-min=13.0",
            "-fobjc-arc",
            "-Wno-int-conversion",
            "-Wno-incompatible-pointer-types",
            "-Wno-nullability-completeness",
            "-Wno-deprecated-declarations",
            "-I",
            str(universal_dir / "include"),
            "-I",
            str(rn_stubs_dir),
            "-I",
            str(JCORE_RN_IOS_DIR),
            "-I",
            str(JPUSH_RN_IOS_DIR),
            "-c",
            str(src),
            "-o",
            str(obj_out),
        ])

    print("[PASS] Universal static archives built, headers & symbol hygiene verified, and RCTJCoreModule + RCTJPushModule compiled cleanly")


def verify_macabi_e2e_runtime(macabi_dir: Path, temp_root: Path) -> None:
    print("==> Step 2: Building Mac Catalyst (arm64-apple-ios14.0-macabi) static archives and running in-process E2E test binary...")
    run([sys.executable, str(BUILD_SCRIPT), "--output-dir", str(macabi_dir), "--target", "macabi"])

    jcore_a = macabi_dir / "jcore-ios-5.2.0.a"
    jpush_a = macabi_dir / "jpush-ios-6.0.0.a"
    runner_src = ROOT / "tests/ios/JPushSdkHostTests.m"
    runner_bin = temp_root / "ios_e2e_test_runner"

    rn_stubs_dir = ROOT / "tests/ios/react-stubs"

    mac_sdk = sdk_path("macosx")
    compile_cmd = [
        "xcrun",
        "--sdk",
        "macosx",
        "clang",
        "-target",
        "arm64-apple-ios14.0-macabi",
        "-isysroot",
        mac_sdk,
        "-iframework",
        f"{mac_sdk}/System/iOSSupport/System/Library/Frameworks",
        "-fobjc-arc",
        "-O2",
        "-Wall",
        "-Wextra",
        "-Wno-unused-parameter",
        "-Wno-deprecated-declarations",
        "-I",
        str(INCLUDE_DIR),
        "-I",
        str(JCORE_DIR),
        "-I",
        str(JPUSH_DIR),
        "-I",
        str(BRIDGE_DIR),
        "-I",
        str(rn_stubs_dir),
        str(runner_src),
        str(BRIDGE_DIR / "RCTJPushEventQueue.m"),
        str(BRIDGE_DIR / "RCTJPushModule.m"),
        str(jpush_a),
        str(jcore_a),
        "-framework",
        "Foundation",
        "-framework",
        "UIKit",
        "-framework",
        "UserNotifications",
        "-framework",
        "CoreLocation",
        "-framework",
        "Security",
        "-framework",
        "Network",
        "-lz",
        "-o",
        str(runner_bin),
    ]
    run(compile_cmd)

    output = run([str(runner_bin)])
    for line in output.strip().splitlines():
        print(f"    {line}")


def main() -> int:
    with tempfile.TemporaryDirectory(prefix="jpush-jcore-ios-test-") as tmp:
        temp_root = Path(tmp)
        verify_universal_archives_and_rn_consumer(temp_root / "universal", temp_root)
        verify_macabi_e2e_runtime(temp_root / "macabi", temp_root)

    print("ALL_IOS_SDK_TESTS_PASSED")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
