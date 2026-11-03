#!/usr/bin/env python3
from __future__ import annotations

import argparse
from pathlib import Path
import shutil
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parents[1]
SOURCE_ROOT = ROOT / "source" / "ios"
INCLUDE_DIR = SOURCE_ROOT / "include"
JCORE_DIR = SOURCE_ROOT / "jcore"
JPUSH_DIR = SOURCE_ROOT / "jpush"

DEFAULT_OUTPUT_DIR = ROOT / "build" / "ios"
JCORE_ARCHIVE_NAME = "jcore-ios-5.2.0.a"
JPUSH_ARCHIVE_NAME = "jpush-ios-6.0.0.a"


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


def compile_slice(
    sources: list[Path],
    sdk_name: str,
    arch: str,
    min_version_flag: str,
    slice_dir: Path,
    archive_name: str,
    extra_flags: list[str] | None = None,
) -> Path:
    slice_dir.mkdir(parents=True, exist_ok=True)
    sysroot = sdk_path(sdk_name)
    objects: list[Path] = []

    for src in sources:
        obj = slice_dir / f"{src.stem}.o"
        cmd = [
            "xcrun",
            "--sdk",
            sdk_name,
            "clang",
            "-x",
            "objective-c",
            "-arch",
            arch,
            "-isysroot",
            sysroot,
            min_version_flag,
            *(extra_flags or []),
            "-fobjc-arc",
            "-fPIC",
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
            "-c",
            str(src),
            "-o",
            str(obj),
        ]
        run(cmd)
        objects.append(obj)

    output_archive = slice_dir / archive_name
    run(["xcrun", "libtool", "-static", "-o", str(output_archive), *[str(o) for o in objects]])
    return output_archive


def build_library(
    sources: list[Path],
    archive_name: str,
    output_dir: Path,
    temp_dir: Path,
    target: str = "universal",
) -> Path:
    output_dir.mkdir(parents=True, exist_ok=True)
    universal_archive = output_dir / archive_name

    if target == "macabi":
        mac_sdk = sdk_path("macosx")
        macabi_slice = compile_slice(
            sources=sources,
            sdk_name="macosx",
            arch="arm64",
            min_version_flag="-target",
            slice_dir=temp_dir / archive_name / "macabi-arm64",
            archive_name=archive_name,
            extra_flags=[
                "arm64-apple-ios14.0-macabi",
                "-iframework",
                f"{mac_sdk}/System/iOSSupport/System/Library/Frameworks",
            ],
        )
        shutil.copy2(macabi_slice, universal_archive)
        return universal_archive

    ios_arm64 = compile_slice(
        sources=sources,
        sdk_name="iphoneos",
        arch="arm64",
        min_version_flag="-miphoneos-version-min=13.0",
        slice_dir=temp_dir / archive_name / "iphoneos-arm64",
        archive_name=archive_name,
    )
    sim_x86_64 = compile_slice(
        sources=sources,
        sdk_name="iphonesimulator",
        arch="x86_64",
        min_version_flag="-mios-simulator-version-min=13.0",
        slice_dir=temp_dir / archive_name / "iphonesimulator-x86_64",
        archive_name=archive_name,
    )

    run(["xcrun", "lipo", "-create", str(ios_arm64), str(sim_x86_64), "-output", str(universal_archive)])
    return universal_archive


def main() -> int:
    parser = argparse.ArgumentParser(description="Build clean JCore and JPush iOS static libraries from source.")
    parser.add_argument(
        "--output-dir",
        type=Path,
        default=DEFAULT_OUTPUT_DIR,
        help="Output directory for static libraries and public headers.",
    )
    parser.add_argument(
        "--target",
        choices=("universal", "macabi"),
        default="universal",
        help="Build target: 'universal' (iphoneos-arm64 + iphonesimulator-x86_64) or 'macabi' (arm64-apple-ios14.0-macabi).",
    )
    args = parser.parse_args()

    jcore_sources = sorted(JCORE_DIR.glob("*.m"))
    jpush_sources = sorted(JPUSH_DIR.glob("*.m"))
    if not jcore_sources:
        raise RuntimeError(f"No JCore sources found in {JCORE_DIR}")
    if not jpush_sources:
        raise RuntimeError(f"No JPush sources found in {JPUSH_DIR}")

    output_dir = args.output_dir.resolve()
    if output_dir.exists():
        shutil.rmtree(output_dir)
    output_dir.mkdir(parents=True, exist_ok=True)

    with tempfile.TemporaryDirectory(prefix="jpush-jcore-ios-") as tmp:
        temp_dir = Path(tmp)
        jcore_lib = build_library(jcore_sources, JCORE_ARCHIVE_NAME, output_dir, temp_dir, target=args.target)
        jpush_lib = build_library(jpush_sources, JPUSH_ARCHIVE_NAME, output_dir, temp_dir, target=args.target)

    headers_out = output_dir / "include"
    headers_out.mkdir(parents=True, exist_ok=True)
    for header in sorted(INCLUDE_DIR.glob("*.h")):
        shutil.copy2(header, headers_out / header.name)

    print(f"Built {jcore_lib} ({jcore_lib.stat().st_size} bytes)")
    print(f"Built {jpush_lib} ({jpush_lib.stat().st_size} bytes)")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
