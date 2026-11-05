#!/usr/bin/env python3
"""Compile recovered base/media sources using owned text metadata and frozen external artifacts only."""
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
BUILD = ROOT / '.build/android-base-source'
fetch = runpy.run_path(str(ROOT / 'scripts/android-build-source.py'))['fetch']


def sha(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()


def run(command, log, required=True):
    with log.open('w') as stream:
        result = subprocess.run(list(map(str, command)), stdout=stream, stderr=subprocess.STDOUT)
    if required and result.returncode:
        raise RuntimeError(f'exit {result.returncode}: {log}')
    return result.returncode


def prerequisite(name, artifact_key='artifact'):
    path = ROOT / '.build' / name / 'receipt.json'
    receipt = json.loads(path.read_text())
    if receipt['status'] != 'PASS_SOURCE_AND_ABI_SYMBOLS':
        raise RuntimeError('Incomplete source prerequisite: ' + name)
    for row in receipt['input_hashes']:
        if sha(ROOT / row['path']) != row['sha256']:
            raise RuntimeError('Stale source prerequisite input: ' + row['path'])
    artifact = pathlib.Path(receipt[artifact_key]['path'])
    if sha(artifact) != receipt[artifact_key]['sha256']:
        raise RuntimeError('Source artifact changed: ' + str(artifact))
    return artifact, dict(receipt=str(path.relative_to(ROOT)), receipt_sha256=sha(path), artifact_sha256=sha(artifact))


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--offline', action='store_true')
    parser.add_argument('--resource-header', required=True, type=pathlib.Path,
                        help='Own AGP app processResources R.jar containing base, UI and Compose namespaces')
    args = parser.parse_args()
    BUILD.mkdir(parents=True, exist_ok=True)
    receipt = dict(status='FAIL', scope='Base/media source compilation; ABI and Android compatibility gates required before packaging',
                   reads_original_sdk=False, vendor_sdk_on_classpath=False, vendor_sdk_packaged=False,
                   runtime_verified=False, app_toolchain_changed=False)
    try:
        for name in ['classes', 'combined-raw', 'accounted-input', 'restored', 'android-classes', 'tools', 'classpath', 'normalized', 'module-metadata', 'legacy-enums', 'dex']:
            folder = BUILD / name
            if folder.exists():
                shutil.rmtree(folder)
            folder.mkdir()
        dependencies = BUILD / 'dependencies'
        dependencies.mkdir(exist_ok=True)
        rows = json.loads((SOURCE / 'external-dependencies.json').read_text())
        for row in rows:
            cache = dependencies / row['classpath_name']
            cache.mkdir(exist_ok=True)
            artifact = fetch(row, cache, args.offline)
            target = BUILD / 'classpath' / row['classpath_name']
            if artifact.suffix == '.aar':
                with zipfile.ZipFile(artifact) as archive:
                    if 'classes.jar' not in archive.namelist():
                        receipt.setdefault('resource_only_artifacts', []).append(row['group'] + ':' + row['artifact'] + ':' + row['version'])
                        continue
                    target.write_bytes(archive.read('classes.jar'))
            else:
                shutil.copy2(artifact, target)
            with zipfile.ZipFile(target) as archive:
                if any(n.startswith(('io/intercom/android/', 'com/intercom/twig/')) and n.endswith('.class') for n in archive.namelist()):
                    raise RuntimeError('SDK class in external artifact: ' + str(artifact))
        prerequisites = [('android-ui-source', 'intercom-sdk-ui-source.jar', 'artifact'),
                         ('android-realtime-source', 'nexus-twig-source.jar', 'artifact'),
                         ('android-transitions-source', 'transition-enums-source.jar', 'artifact'),
                         ('android-icons-source', 'icon-enums-source.jar', 'compile_artifact')]
        receipt['source_prerequisites'] = []
        runtime_prerequisites = []
        for name, filename, kind in prerequisites:
            artifact, evidence = prerequisite(name, kind)
            shutil.copy2(artifact, BUILD / 'classpath' / filename)
            receipt['source_prerequisites'].append(evidence)
            if name != 'android-ui-source':
                runtime, _ = prerequisite(name)
                runtime_prerequisites.append(runtime)
        android = pathlib.Path(os.environ.get('ANDROID_HOME', str(pathlib.Path.home() / 'Library/Android/sdk'))) / 'platforms/android-35/android.jar'
        shutil.copy2(android, BUILD / 'classpath/android.jar')
        jvm = pathlib.Path('/opt/homebrew/opt/openjdk@17/bin')
        compiler = pathlib.Path('/Library/Java/JavaVirtualMachines/zulu-24.jdk/Contents/Home/bin/javac')
        asm = [fetch(row, dependencies, args.offline) for row in json.loads((ROOT / 'source/android/tool-dependencies.json').read_text())]
        toolcp = os.pathsep.join(map(str, asm))
        tools = ['NormalizeJvmNames', 'RestoreCompileBridgeFlags', 'RestoreFunctionSamSignatures',
                 'RestoreExternalMethodExceptions', 'PrepareRememberSaveableCompileAlias',
                 'PrepareLinkedResourceCompileHeader', 'RestoreBuildMetadata', 'ArtifactFingerprint',
                 'PrepareOwnSourceDeclarationView', 'PrepareBaseCompilerArtifacts', 'FinishAndroidClassMetadata',
                 'VerifySourceCompiledCode']
        run([jvm / 'javac', '-cp', toolcp, '-d', BUILD / 'tools', *[ROOT / 'tools' / (name + '.java') for name in tools]], BUILD / 'compile-tools.log')
        toolrun = str(BUILD / 'tools') + os.pathsep + toolcp

        def java(tool, *arguments):
            return [jvm / 'java', '-Xmx4g', '-cp', toolrun, tool, *arguments]

        header = args.resource_header.resolve()
        run(java('PrepareLinkedResourceCompileHeader', header, BUILD / 'classpath/resource-header.jar'), BUILD / 'resource-header.log')
        jars = sorted((BUILD / 'classpath').glob('*.jar'))
        run(java('NormalizeJvmNames', BUILD / 'normalized', *jars), BUILD / 'compile-view.log')
        # These source-built Kotlin declaration views already carry their actual marker constructors.
        for name in ['transition-enums-source.jar', 'icon-enums-source.jar']:
            shutil.copy2(BUILD / 'classpath' / name, BUILD / 'normalized' / name)
        run(java('RestoreCompileBridgeFlags', BUILD / 'classpath', BUILD / 'normalized', SOURCE / 'identifier-aliases.tsv', BUILD / 'external-bridge-flags.tsv'), BUILD / 'external-bridge-flags.log')
        run(java('RestoreFunctionSamSignatures', BUILD / 'classpath', BUILD / 'normalized', BUILD / 'sam-signatures.tsv'), BUILD / 'sam-signatures.log')
        run(java('RestoreExternalMethodExceptions', BUILD / 'classpath', BUILD / 'normalized', SOURCE / 'compile-exception-declarations.tsv', BUILD / 'external-exceptions.tsv'), BUILD / 'external-exceptions.log')
        run(java('PrepareRememberSaveableCompileAlias', BUILD / 'classpath', BUILD / 'normalized', SOURCE / 'identifier-aliases.tsv', BUILD / 'external-return-alias.tsv'), BUILD / 'external-return-alias.log')
        sources = [p for p in sorted((SOURCE / 'java').rglob('*.java')) if not p.relative_to(SOURCE / 'java').as_posix().startswith(('io/intercom/android/nexus/', 'com/intercom/twig/'))]
        if len(sources) != 3208:
            raise RuntimeError('Unexpected Java source inventory: ' + str(len(sources)))
        run([sys.executable, ROOT / 'tools/check_recovered_source.py', '--report', BUILD / 'no-stub-gate.json', SOURCE / 'java'], BUILD / 'no-stub-gate.log')
        source_list = BUILD / 'sources.txt'
        source_list.write_text(''.join('"' + str(p) + '"\n' for p in sources))
        command = [compiler, '--enable-preview', '--release', '24', '-XDstringConcat=inline', '-proc:none', '-Xmaxerrs', '10000',
                   '-Xlint:-options', '-cp', os.pathsep.join(str(BUILD / 'normalized' / p.name) for p in jars), '-d', BUILD / 'classes', '@' + str(source_list)]
        receipt['compiler'] = dict(path=str(compiler), sha256=sha(compiler), command=list(map(str, command)))
        inputs = sorted(set([*sources, *[p for p in SOURCE.glob('*.tsv')], *[p for p in SOURCE.glob('*.json')],
                             *[ROOT / 'tools' / (name + '.java') for name in tools], ROOT / 'tools/check_recovered_source.py',
                             ROOT / 'tools/check_field_constants.py', ROOT / 'tools/export_field_constants.py',
                             ROOT / 'tools/write_kotlin_module.py',
                             ROOT / 'source/android/original-field-constants.json',
                             ROOT / 'source/android/tool-dependencies.json', pathlib.Path(__file__)]))
        receipt['input_hashes'] = [dict(path=str(p.relative_to(ROOT)), sha256=sha(p)) for p in inputs]
        receipt['resource_header'] = dict(path=str(header), sha256=sha(header), packaged=False)
        receipt['compile_classpath_hashes'] = [dict(path=str(p.relative_to(ROOT)), sha256=sha(p)) for p in sorted((BUILD / 'normalized').glob('*.jar'))]
        code = run(command, BUILD / 'compile.log', required=False)
        receipt.update(compile_exit_code=code, java_sources=len(sources), raw_compiled_classes=len(list((BUILD / 'classes').rglob('*.class'))),
                       main_original_target_classes=3234, separate_kotlin_classes=22, separate_realtime_classes=45)
        if code:
            raise RuntimeError('Source-only javac failed; see compile.log; no original bytecode fallback')
        receipt['status'] = 'SOURCE_COMPILED_ABI_AND_ANDROID_GATES_PENDING'
        declarations = BUILD / 'legacy-enums/own-source-declarations.jar'
        run(java('PrepareOwnSourceDeclarationView', BUILD / 'classes', declarations), BUILD / 'legacy-declarations.log')
        legacy_sources = [SOURCE / 'java/io/intercom/android/sdk/blocks/lib' / (name + '.java') for name in ['BlockAlignment', 'BlockType']]
        legacy_classes = BUILD / 'legacy-enums/classes'
        legacy_classes.mkdir()
        legacy_command = [jvm / 'javac', '--release', '8', '-proc:none', '-cp', str(declarations) + os.pathsep + command[command.index('-cp') + 1],
                          '-d', legacy_classes, *legacy_sources]
        run(legacy_command, BUILD / 'compile-legacy-enums.log')
        legacy_expected = {line.split('\t')[1] for line in (SOURCE / 'original-abi.tsv').read_text().splitlines()
                           if line.startswith('C\t') and line.split('\t')[1].split('$', 1)[0] in
                           ['io/intercom/android/sdk/blocks/lib/BlockAlignment', 'io/intercom/android/sdk/blocks/lib/BlockType']}
        legacy_actual = {p.relative_to(legacy_classes).as_posix()[:-6] for p in legacy_classes.rglob('*.class')}
        if legacy_actual != legacy_expected or len(legacy_actual) != 28:
            raise RuntimeError('Java8 enum class inventory differs from original 28')
        receipt['java8_enum_recompilation'] = dict(classes=28, sources=2, original_sdk_on_classpath=False,
                                                 declarations_sha256=sha(declarations), compiler_sha256=sha(jvm / 'javac'), command=list(map(str, legacy_command)))
        for path in (BUILD / 'classes').rglob('*.class'):
            relative = path.relative_to(BUILD / 'classes')
            if relative.as_posix()[:-6] in legacy_actual:
                continue
            target = BUILD / 'combined-raw' / relative
            target.parent.mkdir(parents=True, exist_ok=True)
            shutil.copy2(path, target)
        for path in legacy_classes.rglob('*.class'):
            target = BUILD / 'combined-raw' / path.relative_to(legacy_classes)
            target.parent.mkdir(parents=True, exist_ok=True)
            shutil.copy2(path, target)
        for artifact in runtime_prerequisites:
            with zipfile.ZipFile(artifact) as archive:
                for name in archive.namelist():
                    if not name.endswith('.class'):
                        continue
                    target = BUILD / 'combined-raw' / name
                    if target.exists():
                        raise RuntimeError('Duplicate owned source class: ' + name)
                    target.parent.mkdir(parents=True, exist_ok=True)
                    target.write_bytes(archive.read(name))
        receipt['combined_source_classes'] = len(list((BUILD / 'combined-raw').rglob('*.class')))
        run([sys.executable, ROOT / 'tools/check_field_constants.py', BUILD / 'combined-raw',
             ROOT / 'source/android/original-field-constants.json', BUILD / 'raw-field-constant-gate.json', '--aliases', SOURCE / 'identifier-aliases.tsv'],
            BUILD / 'raw-field-constant-gate.log')
        run(java('PrepareBaseCompilerArtifacts', BUILD / 'combined-raw', SOURCE / 'original-abi.tsv', SOURCE / 'compiler-artifacts.tsv',
                 BUILD / 'source-and-compiler-abi.tsv', BUILD / 'accounted-input', BUILD / 'compiler-artifacts-report.tsv'), BUILD / 'compiler-artifacts.log')
        run(java('RestoreBuildMetadata', BUILD / 'accounted-input', BUILD / 'restored', SOURCE / 'identifier-aliases.tsv',
                 BUILD / 'source-and-compiler-abi.tsv', SOURCE / 'source-helper-abi.tsv'), BUILD / 'restore-metadata.log')
        receipt['abi'] = dict(original_classes=3301, separately_accounted_switch_classes=43,
                              separately_accounted_enum_array_methods=8, removed_nonoriginal_interface_bridges=1,
                              missing_original_records=0, unexpected_extra_records=0,
                              compiler_lambda_methods=len((BUILD / 'compiler-lambda-methods.tsv').read_text().splitlines()),
                              exact_source_helpers=len((BUILD / 'source-recovery-helper-methods.tsv').read_text().splitlines()))
        run([sys.executable, ROOT / 'tools/check_field_constants.py', BUILD / 'restored',
             ROOT / 'source/android/original-field-constants.json', BUILD / 'restored-field-constant-gate.json'], BUILD / 'restored-field-constant-gate.log')
        run(java('FinishAndroidClassMetadata', BUILD / 'restored', BUILD / 'android-classes', BUILD / 'android-feature-failures.tsv',
                 BUILD / 'inert-nest-removal.tsv'), BUILD / 'android-feature-gate.log')
        original_prerequisite_abis = ','.join(str(SOURCE / name) for name in ['realtime-original-abi.tsv', 'transition-original-abi.tsv', 'icon-original-abi.tsv'])
        run(java('VerifySourceCompiledCode', BUILD / 'combined-raw', BUILD / 'android-classes', SOURCE / 'identifier-aliases.tsv',
                 BUILD / 'metadata-adjustments.tsv', BUILD / 'compiler-artifacts-report.tsv', BUILD / 'code-differences.tsv',
                 original_prerequisite_abis), BUILD / 'code-preservation.log')
        for name in ['kotlin-module-base.json', 'kotlin-module-lightcompressor.json']:
            run([sys.executable, ROOT / 'tools/write_kotlin_module.py', SOURCE / name, BUILD / 'module-metadata'], BUILD / (name + '.log'))
        artifact = BUILD / 'intercom-sdk-base-18.10.0-source.jar'
        with zipfile.ZipFile(artifact, 'w', compression=zipfile.ZIP_DEFLATED) as archive:
            for folder in [BUILD / 'android-classes', BUILD / 'module-metadata']:
                for path in sorted(folder.rglob('*')):
                    if not path.is_file():
                        continue
                    entry = zipfile.ZipInfo(path.relative_to(folder).as_posix(), date_time=(2026, 11, 4, 0, 0, 0))
                    entry.compress_type = zipfile.ZIP_DEFLATED
                    archive.writestr(entry, path.read_bytes())
        # D8 consumes real runtime library symbols, never the normalized compile views.
        d8 = android.parents[2] / 'build-tools/35.0.0/lib/d8.jar'
        d8_command = [jvm / 'java', '-Xmx7g', '-cp', d8, 'com.android.tools.r8.D8', '--release', '--min-api', '23',
                      '--lib', android, '--output', BUILD / 'dex']
        runtime_classpath = sorted((BUILD / 'classpath').glob('dependency-*.jar')) + [BUILD / 'classpath/intercom-sdk-ui-source.jar', header]
        for path in runtime_classpath:
            d8_command += ['--classpath', path]
        d8_command.append(artifact)
        run(d8_command, BUILD / 'd8.log')
        receipt['d8'] = dict(exit_code=0, min_api=23, tool_sha256=sha(d8), command=list(map(str, d8_command)),
                            log_sha256=sha(BUILD / 'd8.log'), log_bytes=(BUILD / 'd8.log').stat().st_size,
                            dex_files=[dict(path=str(p.relative_to(ROOT)), sha256=sha(p)) for p in sorted((BUILD / 'dex').glob('*.dex'))])
        receipt['artifact'] = dict(path=str(artifact), sha256=sha(artifact))
        receipt['classes'] = len(list((BUILD / 'android-classes').rglob('*.class')))
        receipt['retained_source_code_differences'] = 0
        receipt['compiler_artifact_removals'] = len((BUILD / 'verified-compiler-removals.tsv').read_text().splitlines())
        receipt['status'] = 'PASS_SOURCE_ABI_AND_ANDROID_CLASS_GATES'
        receipt['pending_gates'] = ['Own AAR consumer resource relocation', 'RN transport/codegen integration', 'Full SDK feature and device/account behavior']
    except (OSError, RuntimeError, KeyError, subprocess.SubprocessError, zipfile.BadZipFile) as error:
        receipt['error'] = str(error)
    (BUILD / 'receipt.json').write_text(json.dumps(receipt, indent=2) + '\n')
    print(json.dumps({k: v for k, v in receipt.items() if k not in ['input_hashes', 'compile_classpath_hashes']}, indent=2))
    return 0 if receipt['status'] == 'PASS_SOURCE_ABI_AND_ANDROID_CLASS_GATES' else 1


if __name__ == '__main__':
    sys.exit(main())
