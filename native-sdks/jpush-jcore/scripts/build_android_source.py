#!/usr/bin/env python3
from __future__ import annotations

import argparse
import os
from pathlib import Path
import shutil
import subprocess
import tempfile
import zipfile

ROOT = Path(__file__).resolve().parents[1]
JAVA_SOURCE_DIR = ROOT / "source" / "android" / "java"
DEFAULT_OUTPUT_DIR = ROOT / "build" / "android"

JCORE_JAR_NAME = "jcore-android-5.2.0.jar"
JPUSH_JAR_NAME = "jpush-android-5.9.0.jar"

JPUSH_OWNED_PREFIXES = (
    "cn/jpush/android/api/",
    "cn/jpush/android/data/",
    "cn/jpush/android/helper/",
    "cn/jpush/android/local/",
    "cn/jpush/android/service/JPushMessageReceiver",
    "cn/jpush/android/ui/",
    "cn/jpush/android/ups/",
)


def run(cmd: list[str]) -> str:
    proc = subprocess.run(cmd, text=True, capture_output=True)
    if proc.returncode != 0:
        raise RuntimeError(
            f"Command failed ({proc.returncode}): {' '.join(cmd)}\n"
            f"STDOUT:\n{proc.stdout}\nSTDERR:\n{proc.stderr}"
        )
    return proc.stdout


def find_android_jar(explicit_jar: Path | None) -> Path:
    if explicit_jar is not None:
        resolved = explicit_jar.expanduser().resolve()
        if not resolved.is_file():
            raise RuntimeError(f"android.jar not found at {resolved}")
        return resolved

    candidates: list[Path] = []
    for env_key in ("ANDROID_HOME", "ANDROID_SDK_ROOT"):
        val = os.environ.get(env_key)
        if val:
            candidates.append(Path(val))
    candidates.extend([
        Path.home() / "Library" / "Android" / "sdk",
        Path("/opt/homebrew/share/android-commandlinetools"),
    ])

    for sdk_root in candidates:
        platforms_dir = sdk_root / "platforms"
        if not platforms_dir.is_dir():
            continue
        platform_jars = sorted(platforms_dir.glob("android-*/android.jar"), reverse=True)
        if platform_jars:
            return platform_jars[0]

    raise RuntimeError(
        "Could not locate android.jar. Pass --android-jar or set ANDROID_SDK_ROOT."
    )


def find_javac() -> str:
    java_home = os.environ.get("JAVA_HOME")
    if java_home:
        candidate = Path(java_home) / "bin" / "javac"
        if candidate.is_file():
            return str(candidate)
    which_javac = shutil.which("javac")
    if which_javac:
        return which_javac
    raise RuntimeError("Could not locate javac.")


def is_jpush_class(rel_path: str) -> bool:
    return any(rel_path.startswith(prefix) for prefix in JPUSH_OWNED_PREFIXES)


def write_deterministic_jar(classes_dir: Path, predicate, out_jar: Path) -> None:
    out_jar.parent.mkdir(parents=True, exist_ok=True)
    class_files = sorted(classes_dir.rglob("*.class"))
    with zipfile.ZipFile(out_jar, "w", compression=zipfile.ZIP_DEFLATED) as zf:
        for file_path in class_files:
            rel = file_path.relative_to(classes_dir).as_posix()
            if not predicate(rel):
                continue
            info = zipfile.ZipInfo(rel, date_time=(2025, 1, 1, 0, 0, 0))
            info.compress_type = zipfile.ZIP_DEFLATED
            info.external_attr = 0o644 << 16
            zf.writestr(info, file_path.read_bytes())


def main() -> int:
    parser = argparse.ArgumentParser(description="Build clean JCore and JPush Android JARs from source.")
    parser.add_argument(
        "--output-dir",
        type=Path,
        default=DEFAULT_OUTPUT_DIR,
        help="Output directory for built JARs.",
    )
    parser.add_argument(
        "--android-jar",
        type=Path,
        default=None,
        help="Explicit path to android.jar.",
    )
    args = parser.parse_args()

    android_jar = find_android_jar(args.android_jar)
    javac = find_javac()
    java_sources = sorted(JAVA_SOURCE_DIR.rglob("*.java"))
    if not java_sources:
        raise RuntimeError(f"No Java sources found in {JAVA_SOURCE_DIR}")

    output_dir = args.output_dir.resolve()
    if output_dir.exists():
        shutil.rmtree(output_dir)
    output_dir.mkdir(parents=True, exist_ok=True)

    with tempfile.TemporaryDirectory(prefix="jpush-jcore-android-") as tmp:
        temp_dir = Path(tmp)
        classes_dir = temp_dir / "classes"
        classes_dir.mkdir(parents=True, exist_ok=True)
        arg_file = temp_dir / "sources.txt"
        arg_file.write_text("\n".join(str(p) for p in java_sources), encoding="utf-8")

        run([
            javac,
            "--release",
            "8",
            "-encoding",
            "UTF-8",
            "-g",
            "-Xlint:-deprecation,-options",
            "-classpath",
            str(android_jar),
            "-d",
            str(classes_dir),
            f"@{arg_file}",
        ])

        jcore_jar = output_dir / JCORE_JAR_NAME
        jpush_jar = output_dir / JPUSH_JAR_NAME
        write_deterministic_jar(classes_dir, lambda rel: not is_jpush_class(rel), jcore_jar)
        write_deterministic_jar(classes_dir, is_jpush_class, jpush_jar)

    print(f"Built {jcore_jar} ({jcore_jar.stat().st_size} bytes)")
    print(f"Built {jpush_jar} ({jpush_jar.stat().st_size} bytes)")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
