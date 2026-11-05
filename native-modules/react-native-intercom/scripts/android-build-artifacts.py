#!/usr/bin/env python3
"""Build the recovered native SDK from source and publish two namespace-preserving AARs into this package only."""
import argparse
import hashlib
import json
import os
import pathlib
import shutil
import subprocess
import sys
import xml.etree.ElementTree as ET
import zipfile

ROOT = pathlib.Path(__file__).resolve().parent.parent
BUILD = ROOT / '.build/android-artifacts'
GROUP = 'com.onekey.intercom.source'
VERSION = '18.10.0-source.1'


def sha(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()


def run(command, log, env=None):
    with log.open('w') as stream:
        code = subprocess.run(list(map(str, command)), stdout=stream, stderr=subprocess.STDOUT, env=env).returncode
    if code:
        raise RuntimeError(f'exit {code}: {log}')


def dependencies(name):
    return sorted({r['group'] + ':' + r['artifact'] + ':' + r['version']
                   for r in json.loads((ROOT / 'source/android' / name / 'external-dependencies.json').read_text())
                   if r.get('scope', 'external-runtime') == 'external-runtime'})


def manifest(name, target, push=False):
    tree = ET.parse(ROOT / 'source/android' / name / 'AndroidManifest.xml')
    root = tree.getroot()
    root.attrib.pop('package', None)
    if push:
        fcm = ET.parse(ROOT / 'source/android/intercom-sdk-push/AndroidManifest.xml').getroot()
        for child in fcm.find('application'):
            root.find('application').append(child)
    ET.register_namespace('android', 'http://schemas.android.com/apk/res/android')
    tree.write(target, encoding='unicode', xml_declaration=True)


def source_artifact(name, required):
    receipt_path = ROOT / '.build' / name / 'receipt.json'
    receipt = json.loads(receipt_path.read_text())
    if receipt['status'] != required:
        raise RuntimeError('Incomplete source closure: ' + name)
    for row in receipt['input_hashes']:
        if sha(ROOT / row['path']) != row['sha256']:
            raise RuntimeError('Stale source input: ' + row['path'])
    artifact = pathlib.Path(receipt['artifact']['path'])
    if sha(artifact) != receipt['artifact']['sha256']:
        raise RuntimeError('Changed source artifact: ' + str(artifact))
    return artifact, dict(receipt=str(receipt_path.relative_to(ROOT)), receipt_sha256=sha(receipt_path), artifact_sha256=sha(artifact))


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--offline', action='store_true')
    parser.add_argument('--gradle', type=pathlib.Path, default=shutil.which('gradle'), help='Gradle 8.13 executable for the independent SDK build')
    args = parser.parse_args()
    if not args.gradle or not args.gradle.is_file():
        parser.error('Pass an existing Gradle 8.13 executable with --gradle')
    BUILD.mkdir(parents=True, exist_ok=True)
    receipt = dict(status='FAIL', vendor_sdk_inputs=False, scope='Source-built native AARs; no SDK session/account/runtime acceptance')
    try:
        project = BUILD / 'project'
        project.mkdir(exist_ok=True)
        repository = ROOT / 'android/maven'
        env = dict(os.environ)
        java17 = env.get('JAVA17_HOME', '/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home')
        env['JAVA_HOME'] = java17
        (project / 'settings.gradle').write_text("pluginManagement { repositories { google(); mavenCentral(); gradlePluginPortal() } }\ndependencyResolutionManagement { repositories { google(); mavenCentral() } }\nrootProject.name='intercom-source-artifacts'\ninclude ':base', ':ui', ':resources'\n")
        (project / 'build.gradle').write_text("plugins { id 'com.android.library' version '8.13.2' apply false; id 'com.android.application' version '8.13.2' apply false }\n")
        (project / 'gradle.properties').write_text('android.useAndroidX=true\nandroid.enableJetifier=false\nandroid.nonTransitiveRClass=true\nandroid.nonFinalResIds=true\norg.gradle.jvmargs=-Xmx3072m\n')
        sdk = os.environ.get('ANDROID_HOME', str(pathlib.Path.home() / 'Library/Android/sdk'))
        (project / 'local.properties').write_text('sdk.dir=' + sdk + '\n')
        inputs = BUILD / 'inputs'
        inputs.mkdir(exist_ok=True)
        source_deps = {'ui': dependencies('intercom-sdk-ui'), 'base': sorted(set(dependencies('intercom-sdk-base') + dependencies('intercom-sdk-push')))}
        # Resource preparation must precede source compilation. The same real AGP graph later packages the artifacts.
        for part in ['base', 'ui']:
            folder = project / part
            folder.mkdir(exist_ok=True)
            manifest('intercom-sdk-' + part, folder / 'AndroidManifest.xml', part == 'base')
            source = ROOT / 'source/android' / ('intercom-sdk-' + part)
            proguard = [source / 'proguard.txt']
            if part == 'base':
                proguard += [source / 'nexus-proguard.txt', ROOT / 'source/android/intercom-sdk-push/proguard.txt']
            dependencies_text = '\n'.join("api '" + dep + "'" for dep in source_deps[part])
            if part == 'base':
                dependencies_text += "\napi project(':ui')"
            (folder / 'build.gradle').write_text("plugins { id 'com.android.library'; id 'maven-publish' }\ngroup='" + GROUP + "'\nversion='" + VERSION + "'\nandroid { namespace 'io.intercom.android.sdk" + ('.ui' if part == 'ui' else '') + "'; compileSdk 36; defaultConfig { minSdk 23; consumerProguardFiles " + ', '.join(repr(str(p)) for p in proguard) + " }; buildFeatures { buildConfig false }; sourceSets.main { java.srcDirs=[]; res.srcDirs=[" + repr(str(source / 'res')) + "]; manifest.srcFile 'AndroidManifest.xml' }; publishing { singleVariant('release') } }\ndependencies {\n" + dependencies_text + "\nfileTree(dir:" + repr(str(inputs / part)) + ", include:['*.jar']).each { api files(it) }\n}\nafterEvaluate { publishing { publications { release(MavenPublication) { from components.release; artifactId='intercom-" + part + "' } }; repositories { maven { name='PackageSource'; url=uri(" + repr(str(repository)) + ") } } } }\n")
        resources = project / 'resources'
        resources.mkdir(exist_ok=True)
        (resources / 'AndroidManifest.xml').write_text('<manifest xmlns:android="http://schemas.android.com/apk/res/android"><application /></manifest>')
        (resources / 'build.gradle').write_text("plugins { id 'com.android.application' }\nandroid { namespace 'com.onekey.intercom.source.resources'; compileSdk 36; defaultConfig { applicationId 'com.onekey.intercom.source.resources'; minSdk 23; targetSdk 36 }; sourceSets.main.manifest.srcFile 'AndroidManifest.xml' }\ndependencies { implementation project(':base') }\n")
        command = [args.gradle.resolve(), '-p', project, '--console=plain', '--no-daemon'] + (['--offline'] if args.offline else [])
        run(command + [':resources:processDebugResources', ':base:generateDebugRFile'], BUILD / 'resources.log', env)
        resource_header = resources / 'build/intermediates/compile_and_runtime_not_namespaced_r_class_jar/debug/processDebugResources/R.jar'
        if not resource_header.is_file():
            raise RuntimeError('Missing real linked resource header')
        source_commands = [
            ['android-build-source.py'], ['android-build-realtime-source.py'], ['android-build-transition-source.py'],
            ['android-build-icon-source.py', '--resource-header', str(project / 'base/build/intermediates/compile_r_class_jar/debug/generateDebugRFile/R.jar')],
            ['android-build-base-source.py', '--resource-header', str(resource_header)], ['android-build-push-source.py']]
        for arguments in source_commands:
            run([sys.executable, ROOT / 'scripts' / arguments[0], *arguments[1:], *(['--offline'] if args.offline else [])], BUILD / (arguments[0] + '.log'))
        receipt['sources'] = []
        expected = {}
        for part, name, status in [('ui', 'android-ui-source', 'PASS_SOURCE_AND_ABI_SYMBOLS'), ('base', 'android-base-source', 'PASS_SOURCE_ABI_AND_ANDROID_CLASS_GATES'), ('base', 'android-push-source', 'PASS_SOURCE_AND_ABI_SYMBOLS')]:
            jar, proof = source_artifact(name, status)
            folder = inputs / part
            folder.mkdir(exist_ok=True)
            if part not in expected:
                for old in folder.glob('*.jar'):
                    old.unlink()
                expected[part] = {}
            shutil.copy2(jar, folder / (sha(jar) + '.jar'))
            receipt['sources'].append(proof)
            with zipfile.ZipFile(jar) as archive:
                for filename in archive.namelist():
                    if filename.endswith(('.class', '.kotlin_module')):
                        if filename in expected[part]:
                            raise RuntimeError('Duplicate source class/module: ' + filename)
                        expected[part][filename] = hashlib.sha256(archive.read(filename)).hexdigest()
        run(command + [':base:publishReleasePublicationToPackageSourceRepository', ':ui:publishReleasePublicationToPackageSourceRepository'], BUILD / 'artifacts.log', env)
        receipt['artifacts'] = []
        for part in ['base', 'ui']:
            aar = repository / GROUP.replace('.', '/') / ('intercom-' + part) / VERSION / ('intercom-' + part + '-' + VERSION + '.aar')
            actual = {}
            with zipfile.ZipFile(aar) as archive:
                import io
                for filename in archive.namelist():
                    if filename.endswith('.jar'):
                        with zipfile.ZipFile(io.BytesIO(archive.read(filename))) as jar:
                            for entry in jar.namelist():
                                if entry.endswith(('.class', '.kotlin_module')):
                                    if entry in actual:
                                        raise RuntimeError('Duplicate packaged entry: ' + entry)
                                    if entry.endswith('/R.class') or '/R$' in entry:
                                        raise RuntimeError('Compile-only R packaged: ' + entry)
                                    actual[entry] = hashlib.sha256(jar.read(entry)).hexdigest()
                if actual != expected[part]:
                    raise RuntimeError('AAR class/module bytes differ from verified source artifacts')
                if 'proguard.txt' not in archive.namelist():
                    raise RuntimeError('Missing original consumer keep rules')
            pom = aar.with_suffix('.pom')
            if 'io.intercom.android' in pom.read_text():
                raise RuntimeError('Vendor dependency in own-source POM')
            receipt['artifacts'].append(dict(path=str(aar.relative_to(ROOT)), sha256=sha(aar), classes=sum(n.endswith('.class') for n in actual), kotlin_modules=sum(n.endswith('.kotlin_module') for n in actual)))
        receipt['resource_header'] = dict(sha256=sha(resource_header), path=str(resource_header.relative_to(ROOT)))
        receipt['status'] = 'PASS_SOURCE_AAR_PACKAGING'
        proof_folder = repository / 'source-provenance'
        proof_folder.mkdir(exist_ok=True)
        for row in receipt['sources']:
            origin = ROOT / row['receipt']
            shutil.copy2(origin, proof_folder / (origin.parent.name + '-receipt.json'))
        receipt['packaging_inputs'] = [dict(path=str(path.relative_to(ROOT)), sha256=sha(path)) for path in sorted(
            [pathlib.Path(__file__).resolve()] + [p for folder in ['intercom-sdk-base', 'intercom-sdk-ui', 'intercom-sdk-push'] for p in (ROOT / 'source/android' / folder).rglob('*') if p.is_file() and (p.name in ['AndroidManifest.xml', 'proguard.txt', 'nexus-proguard.txt', 'external-dependencies.json'] or 'res' in p.relative_to(ROOT / 'source/android' / folder).parts)])]
        (proof_folder / 'artifacts-receipt.json').write_text(json.dumps(receipt, indent=2) + '\n')
    except Exception as failure:
        receipt['error'] = str(failure)
        print(failure, file=sys.stderr)
    (BUILD / 'receipt.json').write_text(json.dumps(receipt, indent=2) + '\n')
    print(json.dumps(receipt, indent=2))
    return 0 if receipt['status'] == 'PASS_SOURCE_AAR_PACKAGING' else 1


if __name__ == '__main__':
    sys.exit(main())
