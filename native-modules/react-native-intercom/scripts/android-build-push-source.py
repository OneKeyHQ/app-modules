#!/usr/bin/env python3
"""Build the original four FCM classes and SDK marker from owned Java source, without vendor SDK artifacts."""
import argparse
import hashlib
import json
import os
import pathlib
import runpy
import shutil
import subprocess
import sys
import zipfile

ROOT = pathlib.Path(__file__).resolve().parent.parent
SOURCE = ROOT / 'source/android/intercom-sdk-push'
BUILD = ROOT / '.build/android-push-source'
shared = runpy.run_path(str(ROOT / 'scripts/android-build-source.py'))
fetch = shared['fetch']
java_bin = shared['java_bin']
android_sdk = shared['android_sdk']


def sha(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()


def run(command, log):
    with log.open('w') as stream:
        code = subprocess.run(list(map(str, command)), stdout=stream, stderr=subprocess.STDOUT).returncode
    if code:
        raise RuntimeError(f'exit {code}: {log}')


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--offline', action='store_true')
    args = parser.parse_args()
    BUILD.mkdir(parents=True, exist_ok=True)
    receipt = dict(status='FAIL', scope='FCM4 + SDK marker1 source closure; no push service/token/account execution',
                   reads_original_sdk=False, vendor_sdk_on_classpath=False, vendor_sdk_packaged=False, runtime_verified=False)
    try:
        for name in ['classes', 'restored', 'tools', 'classpath']:
            p = BUILD / name
            if p.exists():
                shutil.rmtree(p)
            p.mkdir()
        deps = BUILD / 'dependencies'
        deps.mkdir(exist_ok=True)
        rows = json.loads((SOURCE / 'external-dependencies.json').read_text())
        jars = []
        for row in rows:
            folder = deps / row['classpath_name']
            folder.mkdir(exist_ok=True)
            original = fetch(row, folder, args.offline)
            target = BUILD / 'classpath' / row['classpath_name']
            if original.suffix == '.aar':
                with zipfile.ZipFile(original) as z:
                    if 'classes.jar' not in z.namelist():
                        continue
                    target.write_bytes(z.read('classes.jar'))
            else:
                shutil.copy2(original, target)
            with zipfile.ZipFile(target) as z:
                if any(n.startswith(('io/intercom/android/', 'com/intercom/twig/')) and n.endswith('.class') for n in z.namelist()):
                    raise RuntimeError('Vendor SDK namespace in external dependency')
            jars.append(target)
        for name, required in [('android-base-source', 'PASS_SOURCE_ABI_AND_ANDROID_CLASS_GATES'), ('android-ui-source', 'PASS_SOURCE_AND_ABI_SYMBOLS')]:
            path = ROOT / '.build' / name / 'receipt.json'
            prerequisite = json.loads(path.read_text())
            if prerequisite['status'] != required:
                raise RuntimeError('Incomplete source prerequisite: ' + name)
            for row in prerequisite['input_hashes']:
                if sha(ROOT / row['path']) != row['sha256']:
                    raise RuntimeError('Stale source prerequisite input: ' + row['path'])
            artifact = pathlib.Path(prerequisite['artifact']['path'])
            if sha(artifact) != prerequisite['artifact']['sha256']:
                raise RuntimeError('Changed source prerequisite artifact')
            target = BUILD / 'classpath' / (name + '.jar')
            shutil.copy2(artifact, target)
            jars.append(target)
            receipt.setdefault('source_prerequisites', []).append(dict(receipt=str(path.relative_to(ROOT)), receipt_sha256=sha(path), artifact_sha256=sha(artifact)))
        android = android_sdk() / 'platforms/android-35/android.jar'
        jars.append(android)
        jvm = java_bin(17)
        asm = [fetch(row, deps, args.offline) for row in json.loads((ROOT / 'source/android/tool-dependencies.json').read_text())]
        toolcp = os.pathsep.join(map(str, asm))
        tool = ROOT / 'tools/RestoreBuildMetadata.java'
        run([jvm / 'javac', '-cp', toolcp, '-d', BUILD / 'tools', tool], BUILD / 'compile-tools.log')
        sources = sorted((SOURCE / 'java').rglob('*.java'))
        if len(sources) != 3:
            raise RuntimeError('Expected exactly three original Java source files')
        run([sys.executable, ROOT / 'tools/check_recovered_source.py', '--report', BUILD / 'no-stub-gate.json', SOURCE / 'java'], BUILD / 'no-stub-gate.log')
        command = [jvm / 'javac', '--release', '8', '-Xpkginfo:always', '-proc:none', '-cp', os.pathsep.join(map(str, jars)), '-d', BUILD / 'classes', *sources]
        run(command, BUILD / 'compile.log')
        run([jvm / 'java', '-cp', str(BUILD / 'tools') + os.pathsep + toolcp, 'RestoreBuildMetadata', BUILD / 'classes', BUILD / 'restored',
             SOURCE / 'identifier-aliases.tsv', SOURCE / 'original-abi.tsv'], BUILD / 'restore-metadata.log')
        run([sys.executable, ROOT / 'tools/check_field_constants.py', BUILD / 'classes', ROOT / 'source/android/original-field-constants.json', BUILD / 'raw-field-constant-gate.json'], BUILD / 'raw-field-constant-gate.log')
        run([sys.executable, ROOT / 'tools/check_field_constants.py', BUILD / 'restored', ROOT / 'source/android/original-field-constants.json', BUILD / 'restored-field-constant-gate.json'], BUILD / 'restored-field-constant-gate.log')
        classes = sorted((BUILD / 'restored').rglob('*.class'))
        if len(classes) != 5:
            raise RuntimeError('Original FCM/SDK marker class inventory incomplete')
        artifact = BUILD / 'intercom-fcm-marker-18.10.0-source.jar'
        with zipfile.ZipFile(artifact, 'w', compression=zipfile.ZIP_DEFLATED) as output:
            for path in classes:
                entry = zipfile.ZipInfo(path.relative_to(BUILD / 'restored').as_posix(), date_time=(2026, 11, 4, 0, 0, 0))
                entry.compress_type = zipfile.ZIP_DEFLATED
                output.writestr(entry, path.read_bytes())
        inputs = [*sources, SOURCE / 'external-dependencies.json', SOURCE / 'original-abi.tsv', SOURCE / 'identifier-aliases.tsv', SOURCE / 'AndroidManifest.xml',
                  SOURCE / 'proguard.txt', tool, ROOT / 'tools/check_field_constants.py', ROOT / 'tools/export_field_constants.py', ROOT / 'tools/check_recovered_source.py',
                  ROOT / 'source/android/original-field-constants.json', ROOT / 'source/android/tool-dependencies.json', pathlib.Path(__file__)]
        receipt.update(status='PASS_SOURCE_AND_ABI_SYMBOLS', source_files=3, classes=5, missing_abi_records=0, unexpected_extra_abi_records=0,
                       compile_command=list(map(str, command)), artifact=dict(path=str(artifact), sha256=sha(artifact)),
                       input_hashes=[dict(path=str(p.relative_to(ROOT)), sha256=sha(p)) for p in inputs])
    except (OSError, RuntimeError, KeyError, subprocess.SubprocessError, zipfile.BadZipFile) as error:
        receipt['error'] = str(error)
    (BUILD / 'receipt.json').write_text(json.dumps(receipt, indent=2) + '\n')
    print(json.dumps({k: v for k, v in receipt.items() if k not in ['input_hashes', 'compile_command']}, indent=2))
    return 0 if receipt['status'] == 'PASS_SOURCE_AND_ABI_SYMBOLS' else 1


if __name__ == '__main__':
    sys.exit(main())
