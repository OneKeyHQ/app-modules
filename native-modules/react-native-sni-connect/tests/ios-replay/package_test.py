#!/usr/bin/env python3
"""Validate one packed SDK and run its native replay cases without an RN app install."""
import argparse
import hashlib
import json
import os
from pathlib import Path
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
    parser.add_argument("--ci-prepared-device", action="store_true")
    args = parser.parse_args()
    if args.ci_prepared_device:
        assert os.environ.get("GITHUB_ACTIONS") == "true" and os.environ.get("RUNNER_ENVIRONMENT") == "github-hosted", "Preinstalled simulator reuse is restricted to GitHub-hosted CI"
    if args.ci_prepared_device:
        assert (args.output_dir / "native/device.json").is_file(), "CI simulator preparation record is missing"
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
    for relative in [*(f"ios/SniConnect{name}.swift" for name in ("Client", "PinnedTransport", "Validation", "Core", "Log")), "SniConnect.podspec"]:
        identities[relative] = digest(sdk / relative)
        assert identities[relative] == digest(PACKAGE / relative), f"Packed source mismatch: {relative}"
    for removed in ("ios/apply_emascurl_no_replay.rb", "ios/patches/emascurl-1.5.5-no-replay.patch"):
        assert not (sdk / removed).exists(), f"Removed dependency patch is still packaged: {removed}"
    script = "require 'cocoapods'; require ARGV.shift; require 'json'; puts Pod::Specification.from_file(ARGV.shift).to_pretty_json"
    evaluated = run("bundle", "exec", "ruby", "-e", script, ROOT / "node_modules/react-native/scripts/react_native_pods.rb",
                    sdk / "SniConnect.podspec", env=env, capture_output=True)
    spec = json.loads(evaluated.stdout)
    assert not spec.get("script_phases"), "Removed replay-policy build phase is still installed"
    provenance = {"archive": str(archive), "archiveSha256": digest(archive), "sdkRoot": str(sdk),
                  "sourceSha256": identities, "removedPatchArtifactsAbsent": True,
                  "unmodifiedDependency": True, "emascurlVersion": "1.5.5", "protocolSha256": ORIGINAL_SHA,
                  "preparationSeconds": round(time.monotonic() - started, 3)}
    (output / "package-summary.json").write_text(json.dumps(provenance, indent=2))
    print(json.dumps(provenance), flush=True)
    run("python3", HERE / "run.py", "--sdk-root", sdk, "--emascurl-root", clean,
        "--output-dir", output / "native",
        *(["--ci-prepared-device"] if args.ci_prepared_device else []))
    summary = json.loads((output / "native/summary.json").read_text())
    assert summary["passed"] and summary["totalCases"] == summary["passedCases"] == 15
    assert digest(original) == ORIGINAL_SHA, "Pristine dependency changed"
    acceptance = {"packageAcceptancePassed": True, "unmodifiedDependency": True,
                  "nativePassed": 15, "nativeSkipped": 0, "totalSeconds": round(time.monotonic() - started, 3)}
    (output / "acceptance-summary.json").write_text(json.dumps(acceptance, indent=2))
    print(json.dumps(acceptance), flush=True)


if __name__ == "__main__":
    main()
