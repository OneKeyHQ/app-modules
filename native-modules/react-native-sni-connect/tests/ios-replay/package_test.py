#!/usr/bin/env python3
"""Validate one packed SDK and run its native replay cases without an RN app install."""
import argparse
import hashlib
import json
import os
from pathlib import Path
import shutil
import subprocess
import time

HERE = Path(__file__).resolve().parent
PACKAGE = HERE.parents[1]
ROOT = HERE.parents[3]
ORIGINAL_SHA = "56969066a34ec9ed8e3cc7510a69ed3f209bac8a8b16713e8c3300185f367258"


def digest(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()


def run(*args, **kwargs):
    try:
        return subprocess.run([str(arg) for arg in args], check=True, text=True, **kwargs)
    except subprocess.CalledProcessError as error:
        if error.stdout:
            print(error.stdout, flush=True)
        if error.stderr:
            print(error.stderr, flush=True)
        raise


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--output-dir", type=Path, required=True)
    parser.add_argument("--emascurl-root", type=Path)
    args = parser.parse_args()
    started = time.monotonic()
    output = args.output_dir.resolve()
    output.mkdir(parents=True, exist_ok=True)
    env = {**os.environ, "BUNDLE_GEMFILE": str(ROOT / "example/react-native/Gemfile")}
    if args.emascurl_root:
        clean = args.emascurl_root.resolve()
    else:
        project = output / "dependency-install"
        project.mkdir(exist_ok=True)
        (project / "Podfile").write_text("source 'https://cdn.cocoapods.org/'\nsource 'https://github.com/aliyun/aliyun-specs.git'\nplatform :ios, '15.0'\ninstall! 'cocoapods', :integrate_targets => false\ntarget 'SniReplay' do\n  pod 'EMASCurl', '1.5.5'\nend\n")
        with (output / "pod-install.log").open("w") as log:
            run("bundle", "exec", "pod", "install", cwd=project, env=env, stdout=log, stderr=subprocess.STDOUT, timeout=300)
        clean = project / "Pods/EMASCurl"
    original = clean / "EMASCurl/EMASCurlProtocol.m"
    assert digest(original) == ORIGINAL_SHA, "Expected pristine EMASCurl 1.5.5"
    packed = run("npm", "pack", "--ignore-scripts", "--json", "--pack-destination", output,
                 cwd=PACKAGE, capture_output=True)
    files = json.loads(packed.stdout)
    assert len(files) == 1 and Path(files[0]["filename"]).name == files[0]["filename"]
    archive = output / files[0]["filename"]
    consumer = output / "consumer SDK with spaces"
    assert not consumer.exists(), "Use a fresh output directory for package acceptance"
    consumer.mkdir()
    run("tar", "-xzf", archive, "-C", consumer)
    sdk = consumer / "package"
    identities = {}
    for relative in [*(f"ios/SniConnect{name}.swift" for name in ("Client", "PinnedTransport", "Validation", "Core", "Log")),
                     "ios/apply_emascurl_no_replay.rb", "ios/patches/emascurl-1.5.5-no-replay.patch", "SniConnect.podspec"]:
        identities[relative] = digest(sdk / relative)
        assert identities[relative] == digest(PACKAGE / relative), f"Packed source mismatch: {relative}"
    helper = sdk / "ios/apply_emascurl_no_replay.rb"
    checks = run("ruby", HERE / "patch_test.rb", helper, clean, capture_output=True)
    (output / "helper-tests.log").write_text(checks.stdout + checks.stderr)
    assert len([line for line in checks.stdout.splitlines() if line.startswith("PASS ")]) == 6
    # Evaluate the actual packed podspec, rather than duplicating its build command.
    script = "require 'cocoapods'; require ARGV.shift; require 'json'; puts Pod::Specification.from_file(ARGV.shift).to_pretty_json"
    evaluated = run("bundle", "exec", "ruby", "-e", script, ROOT / "node_modules/react-native/scripts/react_native_pods.rb",
                    sdk / "SniConnect.podspec", env=env, capture_output=True)
    spec = json.loads(evaluated.stdout)
    phases = spec["script_phases"]
    assert len(phases) == 1 and phases[0]["execution_position"] == "before_compile"
    phase = phases[0]
    assert "--verify-only" in phase["script"]
    expected_inputs = ["$(PODS_TARGET_SRCROOT)/ios/apply_emascurl_no_replay.rb",
                       "$(PODS_TARGET_SRCROOT)/ios/patches/emascurl-1.5.5-no-replay.patch",
                       "$(PODS_ROOT)/EMASCurl/EMASCurl/EMASCurlProtocol.m",
                       "$(PODS_ROOT)/EMASCurl/EMASCurl/EMASCurlProtocol.h", "$(PODS_ROOT)/Manifest.lock"]
    assert phase["input_files"] == expected_inputs
    pods = output / "Pods with spaces"
    pods.mkdir()
    dependency = pods / "EMASCurl"
    shutil.copytree(clean, dependency)
    manifest = pods / "Manifest.lock"
    manifest.write_text("PODS:\n  - EMASCurl (1.5.5)\n")
    for value in expected_inputs:
        assert Path(value.replace("$(PODS_TARGET_SRCROOT)", str(sdk)).replace("$(PODS_ROOT)", str(pods))).is_file()
    source = dependency / "EMASCurl/EMASCurlProtocol.m"
    phase_env = {**os.environ, "PATH": "/usr/bin:/bin", "PODS_TARGET_SRCROOT": str(sdk), "PODS_ROOT": str(pods)}
    before = (source.read_bytes(), source.stat().st_mtime_ns)
    rejected = subprocess.run(["/bin/sh", "-c", phase["script"]], env=phase_env, capture_output=True, text=True)
    assert rejected.returncode != 0, "Unpatched build phase must reject"
    assert before == (source.read_bytes(), source.stat().st_mtime_ns), "Verify phase wrote the source"
    run("ruby", helper, "--pod-root", dependency, "--manifest", manifest)
    before = (source.read_bytes(), source.stat().st_mtime_ns)
    verified = run("/bin/sh", "-c", phase["script"], env=phase_env, capture_output=True)
    assert before == (source.read_bytes(), source.stat().st_mtime_ns), "Patched verify phase wrote the source"
    (output / "build-phase.log").write_text(rejected.stdout + rejected.stderr + verified.stdout + verified.stderr)
    provenance = {"archive": str(archive), "archiveSha256": digest(archive), "sdkRoot": str(sdk),
                  "sourceSha256": identities, "helperPassed": 6, "phaseRejectedUnpatched": True,
                  "phaseVerifiedPatchedReadOnly": True, "phaseInputFiles": expected_inputs,
                  "originalProtocolSha256": ORIGINAL_SHA, "patchedProtocolSha256": digest(source),
                  "preparationSeconds": round(time.monotonic() - started, 3)}
    (output / "package-summary.json").write_text(json.dumps(provenance, indent=2))
    print(json.dumps(provenance), flush=True)
    run("python3", HERE / "run.py", "--sdk-root", sdk, "--emascurl-root", dependency,
        "--prepared-dependency", "--output-dir", output / "native")
    summary = json.loads((output / "native/summary.json").read_text())
    assert summary["passed"] and summary["totalCases"] == summary["passedCases"] == 15
    assert digest(original) == ORIGINAL_SHA, "Pristine dependency changed"
    acceptance = {"packageAcceptancePassed": True, "helperPassed": 6, "helperSkipped": 0,
                  "nativePassed": 15, "nativeSkipped": 0, "totalSeconds": round(time.monotonic() - started, 3)}
    (output / "acceptance-summary.json").write_text(json.dumps(acceptance, indent=2))
    print(json.dumps(acceptance), flush=True)


if __name__ == "__main__":
    main()
