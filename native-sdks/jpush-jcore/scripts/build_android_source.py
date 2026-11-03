#!/usr/bin/env python3
"""Build recovered Android SDK JARs from Java sources and ABI metadata only."""

import argparse
import hashlib
import json
import os
from pathlib import Path
import re
import shutil
import subprocess
import sys
import urllib.request
import zipfile


ROOT = Path(__file__).resolve().parent.parent
BUILD = ROOT / ".build" / "android"
ASM_VERSION = "9.7.1"
ASM_HASHES = {
    "asm": "8cadd43ac5eb6d09de05faecca38b917a040bb9139c7edeb4cc81c740b713281",
    "asm-commons": "9a579b54d292ad9be171d4313fd4739c635592c2b5ac3a459bbd1049cddec6a0",
    "asm-tree": "9929881f59eb6b840e86d54570c77b59ce721d104e6dfd7a40978991c2d3b41f",
}
REPORTS = (
    "abi-missing.tsv",
    "abi-extra.tsv",
    "abi-metadata-adjustments.tsv",
    "compiler-artifact-restorations.tsv",
    "anonymous-class-identities.tsv",
)
ALLOWED_EXTRA_ABI = {
    "M\tcn/jiguang/api/SdkType\t$values$()[Lcn/jiguang/api/SdkType;",
    "M\tcn/jiguang/ba/b$a\t$values()[Lcn/jiguang/ba/b$a;",
    "M\tcn/jiguang/ba/i$a\t$values()[Lcn/jiguang/ba/i$a;",
}


def digest(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()


def run(command, log):
    with log.open("w") as stream:
        result = subprocess.run(command, stdout=stream, stderr=subprocess.STDOUT)
    if result.returncode:
        raise RuntimeError(f"Command failed ({result.returncode}); see {log}")


def argument_file(path, sources):
    # javac argument files need quoting when a checkout path contains spaces.
    path.write_text("".join('"' + str(p).replace('\\', '\\\\').replace('"', '\\"') + '"\n' for p in sources))
    return "@" + str(path)


def dependencies(cache):
    directory = ROOT / ".build" / "deps"
    directory.mkdir(parents=True, exist_ok=True)
    paths = []
    for artifact, expected in ASM_HASHES.items():
        filename = f"{artifact}-{ASM_VERSION}.jar"
        target = directory / filename
        if target.exists() and digest(target) != expected:
            raise RuntimeError(f"SHA-256 mismatch: {target}")
        if not target.exists():
            cached = cache / filename if cache else None
            temporary = target.with_suffix(".download")
            try:
                if cached and cached.is_file():
                    shutil.copyfile(cached, temporary)
                elif cache:
                    raise RuntimeError(f"Offline ASM cache is missing {filename}: {cache}")
                else:
                    url = f"https://repo.maven.apache.org/maven2/org/ow2/asm/{artifact}/{ASM_VERSION}/{filename}"
                    with urllib.request.urlopen(url, timeout=60) as response, temporary.open("wb") as stream:
                        shutil.copyfileobj(response, stream)
                if digest(temporary) != expected:
                    raise RuntimeError(f"SHA-256 mismatch: {filename}")
                temporary.replace(target)
            finally:
                temporary.unlink(missing_ok=True)
        paths.append(target)
    return paths


def build(args, receipt):
    java_home = args.java_home or os.environ.get("JAVA_HOME")
    javac = str(Path(java_home) / "bin" / "javac") if java_home else shutil.which("javac")
    java = str(Path(java_home) / "bin" / "java") if java_home else shutil.which("java")
    if not javac or not java:
        raise RuntimeError("JDK 24.0.1 is required; set JAVA_HOME or --java-home")
    for executable in (javac, java):
        version = subprocess.run([executable, "-version"], capture_output=True, text=True, check=True)
        output = version.stdout + version.stderr
        if not re.search(r'(?:javac\s+|version\s+")24\.0\.1(?:\s|\")', output):
            raise RuntimeError(f"JDK 24.0.1 is required: {output.strip()}")
    sdk = Path(os.environ.get("ANDROID_HOME") or os.environ.get("ANDROID_SDK_ROOT") or Path.home() / "Library/Android/sdk")
    android = Path(args.android_jar).resolve() if args.android_jar else sdk / "platforms/android-35/android.jar"
    if not android.is_file():
        raise RuntimeError(f"Android API 35 android.jar is required: {android}")
    with zipfile.ZipFile(android) as platform:
        entries = platform.namelist()
        if "android/os/Build$VERSION_CODES.class" not in entries:
            raise RuntimeError(f"Not an Android platform JAR: {android}")
        sdk_classes = [name for name in entries if name.endswith(".class") and name.startswith(("cn/jiguang/", "cn/jpush/", "cn/jj/", "cn/asus/", "cn/android/service/"))]
        if sdk_classes:
            raise RuntimeError(f"Android platform JAR contains SDK classes: {sdk_classes[:5]}")

    asm = dependencies(Path(args.asm_cache).resolve() if args.asm_cache else None)
    for name in ("classes", "provided", "restored", "tools", "artifacts"):
        directory = BUILD / name
        if directory.exists():
            shutil.rmtree(directory)
        directory.mkdir(parents=True)
    for name in REPORTS:
        (BUILD / name).unlink(missing_ok=True)

    source = ROOT / "source" / "android"
    paths = sorted((source / "java").rglob("*.java"))
    provided = sorted((source / "compile-only").rglob("*.java"))
    if not paths:
        raise RuntimeError("No SDK Java sources found")
    # These identities are required inputs, even when the restorer treats them as optional.
    for name in ("identifier-aliases.tsv", "original-abi.tsv", "compiler-identities.tsv", "compiler-fields.tsv", "artifact-owners.tsv"):
        if not (source / name).is_file():
            raise RuntimeError(f"Missing build metadata: {name}")
    receipt.update({
        "source_files": len(paths),
        "jdk": str(javac),
        "android_platform_jar": str(android),
        "android_platform_sha256": digest(android),
        "compile_only_sources": [str(p.relative_to(ROOT)) for p in provided],
        "original_sdk_on_compile_classpath": False,
        "source_build_uses_original_sdk_bytecode": False,
        "dependencies": [{"file": p.name, "sha256": digest(p)} for p in asm],
    })
    if provided:
        run([javac, "--release", "8", "-proc:none", "-Xlint:-options", "-cp", str(android), "-d", str(BUILD / "provided"), argument_file(BUILD / "provided-sources.txt", provided)], BUILD / "compile-provided.log")
    compile_classpath = os.pathsep.join([str(android), str(BUILD / "provided")])
    command = [javac, "--release", "8", "-proc:none", "-Xlint:-options", "-Xmaxerrs", "3000", "-cp", compile_classpath, "-d", str(BUILD / "classes"), argument_file(BUILD / "sources.txt", paths)]
    receipt["compile_command"] = command
    run(command, BUILD / "compile.log")
    tool_classpath = os.pathsep.join(str(p) for p in asm)
    run([javac, "-proc:none", "-cp", tool_classpath, "-d", str(BUILD / "tools"), str(ROOT / "tools/ArtifactFingerprint.java"), str(ROOT / "tools/RestoreAbi.java")], BUILD / "compile-tools.log")
    run([java, "-cp", os.pathsep.join([str(BUILD / "tools"), tool_classpath]), "RestoreAbi", str(BUILD / "classes"), str(BUILD / "restored"), str(source / "identifier-aliases.tsv"), str(source / "original-abi.tsv")], BUILD / "restore-abi.log")

    missing = (BUILD / "abi-missing.tsv").read_text().splitlines()
    if missing:
        raise RuntimeError(f"Missing {len(missing)} ABI records; see {BUILD / 'abi-missing.tsv'}")
    extra = set((BUILD / "abi-extra.tsv").read_text().splitlines())
    if extra - ALLOWED_EXTRA_ABI:
        raise RuntimeError(f"Unexpected extra ABI records: {sorted(extra - ALLOWED_EXTRA_ABI)}")
    expected = {p[1] for line in (source / "original-abi.tsv").read_text().splitlines() if (p := line.split("\t"))[0] == "C"}
    classes = sorted((BUILD / "restored").rglob("*.class"))
    actual = {p.relative_to(BUILD / "restored").as_posix()[:-6] for p in classes}
    if actual != expected:
        raise RuntimeError(f"Class set mismatch: missing={sorted(expected - actual)}, extra={sorted(actual - expected)}")
    owners = dict(line.split("\t") for line in (source / "artifact-owners.tsv").read_text().splitlines())
    if set(owners) != expected or set(owners.values()) != {"jpush", "jcore"}:
        raise RuntimeError("Artifact ownership must cover exactly all SDK classes with jpush/jcore owners")
    provided_classes = {p.relative_to(BUILD / "provided").as_posix()[:-6] for p in (BUILD / "provided").rglob("*.class")}
    if actual & provided_classes:
        raise RuntimeError("Compile-only classes must not appear in runtime artifacts")

    outputs = []
    for owner, filename in (("jpush", "jpush-android-5.9.0-recovered.jar"), ("jcore", "jcore-android-5.2.0-recovered.jar"), ("combined", "jpush-jcore-recovered.jar")):
        target = BUILD / "artifacts" / filename
        count = 0
        with zipfile.ZipFile(target, "w", compression=zipfile.ZIP_DEFLATED) as output:
            for path in classes:
                name = path.relative_to(BUILD / "restored").as_posix()[:-6]
                if owner != "combined" and owners[name] != owner:
                    continue
                info = zipfile.ZipInfo(name + ".class", date_time=(2026, 11, 3, 0, 0, 0))
                info.compress_type = zipfile.ZIP_DEFLATED
                output.writestr(info, path.read_bytes())
                count += 1
        outputs.append({"path": str(target.relative_to(ROOT)), "classes": count, "sha256": digest(target)})
    receipt.update({
        "status": "PASS",
        "classes": len(actual),
        "missing_abi_records": 0,
        "extra_abi_records": len(extra),
        "allowed_extra_abi_records": sorted(extra),
        "exact_class_set": True,
        "compile_only_classes_packaged": False,
        "artifacts": outputs,
        "remaining_scope": "External native implementations and dynamic providers are not supplied or validated by this build; runtime/provider closure is not established.",
    })


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--java-home", help="JDK 24.0.1 home (defaults to JAVA_HOME, then PATH)")
    parser.add_argument("--android-jar", help="Android API 35 platform android.jar")
    parser.add_argument("--asm-cache", help="Optional offline directory containing the three pinned ASM 9.7.1 JARs")
    args = parser.parse_args()
    BUILD.mkdir(parents=True, exist_ok=True)
    receipt = {"status": "FAIL"}
    try:
        build(args, receipt)
    except (OSError, RuntimeError, subprocess.SubprocessError, zipfile.BadZipFile) as error:
        receipt["error"] = str(error)
    (BUILD / "build.json").write_text(json.dumps(receipt, indent=2) + "\n")
    print(json.dumps(receipt, indent=2))
    return 0 if receipt["status"] == "PASS" else 1


if __name__ == "__main__":
    sys.exit(main())
