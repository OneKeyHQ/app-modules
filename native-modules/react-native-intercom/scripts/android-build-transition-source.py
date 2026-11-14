#!/usr/bin/env python3
"""Build the two original transition enums from Kotlin source and frozen text metadata."""
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
SOURCE = ROOT / 'source/android/intercom-sdk-base'
BUILD = ROOT / '.build/android-transitions-source'
shared = runpy.run_path(str(ROOT / 'scripts/android-build-source.py'))
fetch = shared['fetch']
java_bin = shared['java_bin']
android_sdk = shared['android_sdk']


def sha(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()


def run(command, log):
    with log.open('w') as stream:
        result = subprocess.run([str(value) for value in command], stdout=stream, stderr=subprocess.STDOUT)
    if result.returncode:
        raise RuntimeError(f'exit {result.returncode}: {log}')


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--offline', action='store_true')
    args = parser.parse_args()
    BUILD.mkdir(parents=True, exist_ok=True)
    receipt = dict(status='FAIL', scope='Two transition enums/13 classes; source compilation and original ABI symbols, not whole SDK or runtime acceptance',
                   reads_original_sdk=False, vendor_sdk_on_classpath=False, vendor_sdk_packaged=False, runtime_verified=False)
    try:
        for name in ['classes', 'restored', 'tools', 'classpath']:
            path = BUILD / name
            if path.exists():
                shutil.rmtree(path)
            path.mkdir()
        dependencies = BUILD / 'dependencies'
        dependencies.mkdir(exist_ok=True)
        manifest_path = SOURCE / 'transition-build-inputs.json'
        manifest = json.loads(manifest_path.read_text())
        compiler = manifest['compiler']
        compiler_jars = [fetch(row, dependencies, args.offline) for row in compiler['dependencies']]
        libraries = []
        for index, row in enumerate(manifest['libraries']):
            artifact = fetch(row, dependencies, args.offline)
            output = BUILD / 'classpath' / f'external-{index:03d}.jar'
            if artifact.suffix == '.aar':
                with zipfile.ZipFile(artifact) as archive:
                    output.write_bytes(archive.read('classes.jar'))
            else:
                shutil.copy2(artifact, output)
            with zipfile.ZipFile(output) as archive:
                if any(name.startswith(('io/intercom/android/', 'com/intercom/twig/')) and name.endswith('.class') for name in archive.namelist()):
                    raise RuntimeError('Original SDK payload in external library')
            libraries.append(output)
        source = SOURCE / 'kotlin/io/intercom/android/sdk/m5/navigation/transitions/TransitionStyle.kt'
        jvm = java_bin(17)
        command = [jvm / 'java', '-cp', os.pathsep.join(map(str, compiler_jars)), 'org.jetbrains.kotlin.cli.jvm.K2JVMCompiler',
                   '-no-stdlib', '-no-reflect', '-language-version', compiler['language_version'], '-api-version', compiler['api_version'],
                   '-jvm-target', compiler['jvm_target'], '-Xlambdas=' + compiler['lambdas'], '-module-name', compiler['module_name'],
                   '-classpath', os.pathsep.join(map(str, libraries)), source, '-d', BUILD / 'classes']
        run(command, BUILD / 'compile.log')
        asm = [fetch(row, dependencies, args.offline) for row in json.loads((ROOT / 'source/android/tool-dependencies.json').read_text())]
        toolcp = os.pathsep.join(map(str, asm))
        restore = ROOT / 'tools/RestoreBuildMetadata.java'
        run([jvm / 'javac', '-cp', toolcp, '-d', BUILD / 'tools', restore], BUILD / 'compile-tools.log')
        abi = SOURCE / 'transition-original-abi.tsv'
        aliases = SOURCE / 'transition-identifier-aliases.tsv'
        run([jvm / 'java', '-cp', str(BUILD / 'tools') + os.pathsep + toolcp, 'RestoreBuildMetadata',
             BUILD / 'classes', BUILD / 'restored', aliases, abi], BUILD / 'restore-metadata.log')
        classes = sorted((BUILD / 'restored').rglob('*.class'))
        if len(classes) != 13:
            raise RuntimeError('Unexpected transition class count')
        artifact = BUILD / 'intercom-transition-enums-18.10.0-source.jar'
        module = BUILD / 'classes/META-INF' / (compiler['module_name'] + '.kotlin_module')
        with zipfile.ZipFile(artifact, 'w', compression=zipfile.ZIP_DEFLATED) as archive:
            for path, relative in [(path, path.relative_to(BUILD / 'restored').as_posix()) for path in classes] + [(module, module.relative_to(BUILD / 'classes').as_posix())]:
                info = zipfile.ZipInfo(relative, date_time=(2026, 11, 4, 0, 0, 0))
                info.compress_type = zipfile.ZIP_DEFLATED
                archive.writestr(info, path.read_bytes())
        inputs = [source, abi, aliases, manifest_path, restore, ROOT / 'source/android/tool-dependencies.json',
                  ROOT / 'scripts/android-build-source.py', pathlib.Path(__file__).resolve()]
        receipt.update(status='PASS_SOURCE_AND_ABI_SYMBOLS', source_files=1, classes=13, missing_abi_records=0, unexpected_extra_abi_records=0,
                       artifact=dict(path=str(artifact), sha256=sha(artifact)), compiler=compiler, library_dependencies=manifest['libraries'],
                       compiler_command=list(map(str, command)), java_sha256=sha(jvm / 'java'), javac_sha256=sha(jvm / 'javac'),
                       input_hashes=[dict(path=str(path.relative_to(ROOT)), sha256=sha(path)) for path in sorted(set(inputs))],
                       partial_kotlin_module=True)
    except (OSError, RuntimeError, subprocess.SubprocessError, zipfile.BadZipFile) as error:
        receipt['error'] = str(error)
    (BUILD / 'receipt.json').write_text(json.dumps(receipt, indent=2) + '\n')
    print(json.dumps({key: receipt[key] for key in ['status', 'scope', 'classes', 'artifact', 'error'] if key in receipt}, indent=2))
    return 0 if receipt['status'] == 'PASS_SOURCE_AND_ABI_SYMBOLS' else 1


if __name__ == '__main__':
    sys.exit(main())
