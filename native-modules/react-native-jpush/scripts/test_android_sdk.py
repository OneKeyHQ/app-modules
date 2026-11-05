#!/usr/bin/env python3
from __future__ import annotations

from pathlib import Path
import re
import sys
import subprocess
import tempfile
import zipfile

ROOT = Path(__file__).resolve().parents[1]
BUILD_ANDROID_SCRIPT = ROOT / "scripts" / "build_android_source.py"
JAVA_SOURCE_DIR = ROOT / "android" / "src" / "main" / "java"
PLUGINS_DIR = JAVA_SOURCE_DIR / "cn" / "jiguang" / "plugins"
JCORE_RN_JAVA_DIR = PLUGINS_DIR / "core"
JPUSH_RN_JAVA_DIR = PLUGINS_DIR / "push"
JCORE_SERVICE_JAVA_DIR = PLUGINS_DIR / "service"

BANNED_PATTERNS = re.compile(
    r"(RecoverType|recoverMethod_|recoverField_|Member\d+|JavascriptInterface|HostJsScope)"
)


def run(cmd: list[str]) -> str:
    proc = subprocess.run(cmd, text=True, capture_output=True)
    if proc.returncode != 0:
        raise RuntimeError(
            f"Command failed ({proc.returncode}): {' '.join(cmd)}\n"
            f"STDOUT:\n{proc.stdout}\nSTDERR:\n{proc.stderr}"
        )
    return proc.stdout


def junit_classpath() -> str:
    import os

    cache = Path(os.environ.get("GRADLE_USER_HOME", Path.home() / ".gradle")) / "caches/modules-2/files-2.1"
    jars = []
    for group, artifact, version in (("junit", "junit", "4.13.2"), ("org.hamcrest", "hamcrest-core", "1.3")):
        matches = sorted((cache / group / artifact / version).glob(f"*/{artifact}-{version}.jar"))
        if not matches:
            raise RuntimeError(f"Missing {artifact}:{version}; run the example Gradle JPush testDebugUnitTest task to resolve test dependencies first")
        jars.append(str(matches[0]))
    return os.pathsep.join(jars)


def verify_clean_sources_and_jars(jcore_jar: Path, jpush_jar: Path) -> None:
    manifest_path = ROOT / "android" / "src" / "main" / "AndroidManifest.xml"
    manifest_text = manifest_path.read_text(encoding="utf-8")
    if 'android:exported="true"' in manifest_text:
        raise RuntimeError(f"Found android:exported=\"true\" in {manifest_path}")
    for activity_cls in (
        "cn.jpush.android.ui.PopWinActivity",
        "cn.jpush.android.ui.PushActivity",
        "cn.jpush.android.service.JNotifyActivity",
        "cn.android.service.JTransitActivity",
        "cn.jpush.android.service.DActivity",
    ):
        if activity_cls not in manifest_text:
            raise RuntimeError(f"Missing activity {activity_cls} in {manifest_path}")

    for java_file in sorted(JAVA_SOURCE_DIR.rglob("*.java")):
        text = java_file.read_text(encoding="utf-8")
        match = BANNED_PATTERNS.search(text)
        if match:
            raise RuntimeError(f"Banned identifier '{match.group(0)}' found in {java_file}")

    for jar_path in (jcore_jar, jpush_jar):
        with zipfile.ZipFile(jar_path, "r") as zf:
            names = zf.namelist()
            if not names:
                raise RuntimeError(f"JAR is empty: {jar_path}")
            for name in names:
                match = BANNED_PATTERNS.search(name)
                if match:
                    raise RuntimeError(f"Banned class entry '{name}' in {jar_path}")


def main() -> int:
    from build_android_source import find_android_jar, find_javac

    android_jar = find_android_jar(None)
    javac = find_javac()
    java_bin = str(Path(javac).with_name("java"))
    junit_cp = junit_classpath()
    fixtures = ROOT / "tests" / "android"

    with tempfile.TemporaryDirectory(prefix="test-jpush-android-sdk-") as tmp:
        temp_dir = Path(tmp)
        jars_dir = temp_dir / "jars"

        # 1. Build jcore-android-5.2.0.jar and jpush-android-5.9.0.jar from clean Java source
        run([sys.executable, str(BUILD_ANDROID_SCRIPT), "--output-dir", str(jars_dir)])
        jcore_jar = jars_dir / "jcore-android-5.2.0.jar"
        jpush_jar = jars_dir / "jpush-android-5.9.0.jar"
        verify_clean_sources_and_jars(jcore_jar, jpush_jar)

        # 2. Compile the ACTUAL jcore-react-native and jpush-react-native Java sources against the built JARs + android.jar
        rn_classes_dir = temp_dir / "rn_classes"
        rn_classes_dir.mkdir(parents=True, exist_ok=True)

        stub_files = sorted((fixtures / "bridge-stubs").rglob("*.java"))
        jcore_rn_files = sorted(JCORE_RN_JAVA_DIR.rglob("*.java")) + sorted(
            JCORE_SERVICE_JAVA_DIR.rglob("*.java")
        )
        jpush_rn_files = sorted(JPUSH_RN_JAVA_DIR.rglob("*.java"))
        if not jcore_rn_files or not jpush_rn_files:
            raise RuntimeError("Could not locate jcore-react-native or jpush-react-native Java files")

        rn_compile_sources = stub_files + jcore_rn_files + jpush_rn_files
        rn_arg_file = temp_dir / "rn_sources.txt"
        rn_arg_file.write_text("\n".join(str(p) for p in rn_compile_sources), encoding="utf-8")

        compile_cp = f"{android_jar}:{jcore_jar}:{jpush_jar}"
        run([
            javac,
            "--release",
            "8",
            "-encoding",
            "UTF-8",
            "-Xlint:-deprecation,-options",
            "-classpath",
            compile_cp,
            "-d",
            str(rn_classes_dir),
            f"@{rn_arg_file}",
        ])

        # 3. Compile and run the JVM end-to-end test runner
        test_classes_dir = temp_dir / "test_classes"
        test_classes_dir.mkdir(parents=True, exist_ok=True)

        shim_files = sorted((fixtures / "runtime-shims").rglob("*.java"))
        runner_file = fixtures / "test" / "SdkHostRegressionTest.java"
        junit_test_files = sorted((ROOT / "android/src/test/java").rglob("*.java"))
        test_arg_file = temp_dir / "test_sources.txt"
        test_arg_file.write_text(
            "\n".join(str(p) for p in (shim_files + junit_test_files + [runner_file])),
            encoding="utf-8",
        )

        test_compile_cp = f"{rn_classes_dir}:{jcore_jar}:{jpush_jar}:{android_jar}:{junit_cp}"
        run([
            javac,
            "--release",
            "8",
            "-encoding",
            "UTF-8",
            "-Xlint:-deprecation,-options",
            "-classpath",
            test_compile_cp,
            "-d",
            str(test_classes_dir),
            f"@{test_arg_file}",
        ])

        runtime_cp = f"{test_classes_dir}:{rn_classes_dir}:{jcore_jar}:{jpush_jar}:{android_jar}:{junit_cp}"
        output = run([
            java_bin, "-cp", runtime_cp, "org.junit.runner.JUnitCore",
            "com.margelo.nitro.reactnativejpush.ReactNativeJpushProtocolTest",
            "com.margelo.nitro.reactnativejpush.JPushCloudHandshakeTest",
            "com.margelo.nitro.reactnativejpush.JPushHandshakeFailureTest",
            "test.SdkHostRegressionTest",
        ])
        if not re.search(r"OK \(\d+ tests?\)", output):
            raise RuntimeError(f"JUnit did not execute the native host suites:\n{output}")

        print(f"Built and verified: {jcore_jar.name} ({jcore_jar.stat().st_size} bytes), {jpush_jar.name} ({jpush_jar.stat().st_size} bytes)")
        print(f"Compiled {len(jcore_rn_files)} jcore-react-native and {len(jpush_rn_files)} jpush-react-native Java files with 0 errors.")
        print(output.strip())
        print("ALL_ANDROID_SDK_TESTS_PASSED")

    return 0


if __name__ == "__main__":
    raise SystemExit(main())
