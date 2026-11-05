#!/usr/bin/env python3
"""Recover the full UI module into Java, then attempt a vendor-free source build."""
import datetime,argparse,hashlib,json,os,pathlib,shutil,subprocess,sys,zipfile,xml.etree.ElementTree as ET
ROOT=pathlib.Path(__file__).resolve().parent.parent
BUILD=ROOT/'.recovery/android-ui'
AUDIT=pathlib.Path('/Volumes/T7Shield/Project/intercom-native-audit-20261104/android')
REFERENCE_TOOLS=pathlib.Path('/Volumes/T7Shield/Project/jpush-jcore-sdk-recovery-20261103/tools')
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def run(command,log,fail=True):
 with log.open('w') as stream:done=subprocess.run([str(v) for v in command],stdout=stream,stderr=subprocess.STDOUT)
 if fail and done.returncode:raise RuntimeError(f'exit {done.returncode}: {log}')
 return done.returncode

def main():
 parser=argparse.ArgumentParser(description=__doc__);parser.add_argument('--compile-only',action='store_true');parser.add_argument('--refresh-decompiled',action='store_true');args=parser.parse_args()
 BUILD.mkdir(parents=True,exist_ok=True)
 previous=BUILD/'attempts'/datetime.datetime.now(datetime.timezone.utc).strftime('%Y%m%dT%H%M%S%fZ');previous.mkdir(parents=True,exist_ok=True)
 for path in [*BUILD.glob('*.log'),*BUILD.glob('*.json')]:shutil.copy2(path,previous/path.name)
 receipt={'status':'FAIL','scope':'Full 83-class intercom-sdk-ui compilation experiment; not the complete SDK','original_sdk_on_compile_classpath':False,'original_sdk_bytecode_packaged':False}
 try:
  if not args.compile_only:
   for name in ['classpath','tools','normalized','decompiled','resources-java']:
    path=BUILD/name
    if path.exists():shutil.rmtree(path)
   asm=sorted((REFERENCE_TOOLS/'asm').glob('*.jar'));vf=REFERENCE_TOOLS/'vineflower-1.12.0.jar'
   if not asm or not vf.exists():raise RuntimeError('Reference compiler tools missing')
   receipt['tools']=[{'path':str(p),'sha256':sha(p)} for p in [*asm,vf]]
   java=pathlib.Path('/opt/homebrew/opt/openjdk@17/bin/java');javac=java.with_name('javac')
   deps=BUILD/'dependencies';deps.mkdir(exist_ok=True)
   (deps/'settings.gradle').write_text("rootProject.name = 'intercom-source-ui-dependencies'\n")
   manifest=json.loads((AUDIT/'manifest.json').read_text());ui=next(i for i in manifest if i['artifact']=='intercom-sdk-ui')
   lines=["plugins { id 'java-library' }","repositories { google(); mavenCentral() }","abstract class JvmCompatibility implements AttributeCompatibilityRule<String> { void execute(CompatibilityCheckDetails<String> details) { if (details.consumerValue == 'androidJvm' && details.producerValue == 'jvm') details.compatible() } }","dependencies { attributesSchema { attribute(Attribute.of('org.jetbrains.kotlin.platform.type', String)) { compatibilityRules.add(JvmCompatibility) } } }","configurations { recovery { canBeResolved = true; canBeConsumed = false; attributes { attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage, 'java-api')); attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category, 'library')); attribute(TargetJvmEnvironment.TARGET_JVM_ENVIRONMENT_ATTRIBUTE, objects.named(TargetJvmEnvironment, 'android')); attribute(Attribute.of('org.jetbrains.kotlin.platform.type', String), 'androidJvm') } } }","dependencies {"]
   bom=next((pathlib.Path.home()/'.gradle/caches/modules-2/files-2.1/androidx.compose/compose-bom/2026.06.01').glob('*/*.pom'))
   ns={'m':'http://maven.apache.org/POM/4.0.0'};tree=ET.parse(bom)
   versions={(d.findtext('m:groupId',namespaces=ns),d.findtext('m:artifactId',namespaces=ns)):d.findtext('m:version',namespaces=ns) for d in tree.findall('.//m:dependencyManagement/m:dependencies/m:dependency',ns)}
   for d in ui['dependencies']:
    version=d['version'] or versions.get((d['groupId'],d['artifactId']))
    if not version:raise RuntimeError('No BOM version for '+str(d))
    coordinate=d['groupId']+':'+d['artifactId']+':'+version
    lines.append(f"  recovery platform('{coordinate}')" if d['scope']=='import' else f"  recovery '{coordinate}'")
   lines.extend(["}","tasks.register('recordArtifacts') { doLast { def rows = configurations.recovery.resolvedConfiguration.resolvedArtifacts.collect { [id: it.moduleVersion.id.toString(), path: it.file.absolutePath] }; file('artifacts.json').text = groovy.json.JsonOutput.prettyPrint(groovy.json.JsonOutput.toJson(rows)) } }"])
   (deps/'build.gradle').write_text('\n'.join(lines)+'\n')
   gradle=next((pathlib.Path.home()/'.gradle/wrapper/dists').glob('gradle-9.4.1-bin/*/gradle-9.4.1/bin/gradle'))
   run([gradle,'--no-daemon','--console=plain','-p',deps,'recordArtifacts'],BUILD/'dependencies.log')
   dependency_rows=json.loads((deps/'artifacts.json').read_text())
   classpath=BUILD/'classpath';classpath.mkdir(exist_ok=True)
   jars=[];owned={n[:-6] for i in manifest for n in (AUDIT/'artifacts'/i['artifact']/'classes.txt').read_text().splitlines()}
   for i,d in enumerate(dependency_rows):
    source=pathlib.Path(d['path']);target=classpath/f'dependency-{i:03d}.jar'
    if source.suffix=='.aar':
     with zipfile.ZipFile(source) as z:
      if 'classes.jar' not in z.namelist():
       receipt.setdefault('resource_only_dependencies',[]).append(d);continue
      target.write_bytes(z.read('classes.jar'))
    elif source.suffix=='.jar':shutil.copy2(source,target)
    else:continue
    with zipfile.ZipFile(target) as z:
     overlap=owned.intersection(n[:-6] for n in z.namelist() if n.endswith('.class'))
     if overlap:raise RuntimeError(f'Original SDK classes present in compilation dependency {source}: {sorted(overlap)[:5]}')
    jars.append(target)
   android=pathlib.Path.home()/'Library/Android/sdk/platforms/android-35/android.jar';shutil.copy2(android,classpath/'android.jar');jars.append(classpath/'android.jar')
   reference=BUILD/'reference';reference.mkdir(exist_ok=True)
   reference_jar=reference/'intercom-sdk-ui-reference.jar';shutil.copy2(AUDIT/'extracted/intercom-sdk-ui/classes.jar',reference_jar)
   toolclasses=BUILD/'tools';toolclasses.mkdir(exist_ok=True);toolcp=os.pathsep.join(str(p) for p in asm)
   run([javac,'-cp',toolcp,'-d',toolclasses,*[ROOT/'tools'/f for f in ['NormalizeJvmNames.java','ExportAbi.java','ArtifactFingerprint.java']]],BUILD/'compile-tools.log')
   toolrun=os.pathsep.join([str(toolclasses),toolcp])
   source=ROOT/'source/android/intercom-sdk-ui';source.mkdir(parents=True,exist_ok=True)
   run([java,'-cp',toolrun,'ExportAbi',source/'original-abi.tsv',reference_jar],BUILD/'export-abi.log')
   normalized=BUILD/'normalized'
   run([java,'-Xmx4g','-cp',toolrun,'NormalizeJvmNames',normalized,reference_jar,*jars],BUILD/'normalize.log')
   shutil.copy2(normalized/'identifier-aliases.tsv',source/'identifier-aliases.tsv')
   abi=(source/'original-abi.tsv').read_text().splitlines();(source/'synthetic-abi.tsv').write_text('\n'.join(line for line in abi if (p:=line.split('\t'))[0]=='M' and int(p[4]) & (0x1000|0x40))+'\n')
   decompiled=BUILD/'decompiled';decompiled.mkdir(exist_ok=True)
   external=[f'-e={normalized/p.name}' for p in jars]
   run([java,'-Xmx4g','-jar',vf,'--folder','--thread-count=4','--kt-enable=false','--decompile-inner=false','--use-lvt-names=false','--use-method-parameters=false','--remove-synthetic=false','--remove-bridge=false','--decompile-generics=false','--lambda-to-anonymous-class=false',*external,normalized/reference_jar.name,decompiled],BUILD/'decompile.log')
   output=source/'java'
   if output.exists() and list(output.rglob('*.java')):
    if not args.refresh_decompiled:raise RuntimeError('Source already exists; use --compile-only, or explicit --refresh-decompiled to replace decompiler output')
    shutil.rmtree(output)
   output.mkdir(exist_ok=True)
   for p in decompiled.rglob('*.java'):
    target=output/p.relative_to(decompiled);target.parent.mkdir(parents=True,exist_ok=True);shutil.copy2(p,target)

  else:
   source=ROOT/'source/android/intercom-sdk-ui';output=source/'java';normalized=BUILD/'normalized';dependency_rows=json.loads((BUILD/'dependencies/artifacts.json').read_text());jars=sorted((BUILD/'classpath').glob('*.jar'));android=pathlib.Path.home()/'Library/Android/sdk/platforms/android-35/android.jar';javac=pathlib.Path('/opt/homebrew/opt/openjdk@17/bin/javac');ui={'class_count':83}
  aapt=pathlib.Path.home()/'Library/Android/sdk/build-tools/35.0.0/aapt2';reszip=BUILD/'resources.zip'
  run([aapt,'compile','--dir',source/'res','-o',reszip],BUILD/'compile-resources.log')
  generated=BUILD/'resources-java';generated.mkdir(exist_ok=True)
  run([aapt,'link','--static-lib','--manifest',source/'AndroidManifest.xml','-I',android,'-o',BUILD/'resources.apk','--java',generated,'-R',reszip,'--auto-add-overlay'],BUILD/'link-resources.log')
  recovered_sources=sorted(output.rglob('*.java'))
  if not recovered_sources:raise RuntimeError('No recovered Java sources; generated resource R.java alone is not SDK recovery')
  javafiles=recovered_sources+sorted(generated.rglob('*.java'));argsfile=BUILD/'sources.txt';argsfile.write_text(''.join('"'+str(p)+'"\n' for p in javafiles))
  classes=BUILD/'classes'
  if classes.exists():shutil.rmtree(classes)
  classes.mkdir(exist_ok=True)
  cp=os.pathsep.join(str(normalized/p.name) for p in jars)
  command=[javac,'--release','8','-proc:none','-Xmaxerrs','3000','-Xlint:-options','-cp',cp,'-d',classes,'@'+str(argsfile)]
  status=run(command,BUILD/'compile.log',fail=False)
  receipt.update(source_files=len(javafiles),expected_original_classes=ui['class_count'],compiled_classes=len(list(classes.rglob('*.class'))),compile_command=[str(v) for v in command],compile_exit_code=status,dependencies=[{**r,'sha256':sha(pathlib.Path(r['path']))} for r in dependency_rows])
  if status:raise RuntimeError('Recovered UI Java does not compile; see compile.log. No original bytecode fallback was attempted.')
  receipt['status']='SOURCE_COMPILED_ABI_NOT_VALIDATED'
 except (OSError,RuntimeError,KeyError,subprocess.SubprocessError,zipfile.BadZipFile) as error:receipt['error']=str(error)
 (BUILD/'receipt.json').write_text(json.dumps(receipt,indent=2)+'\n')
 print(json.dumps({k:v for k,v in receipt.items() if k not in ['compile_command','dependencies','tools']},indent=2))
 return 0 if receipt['status']!='FAIL' else 1
if __name__=='__main__':sys.exit(main())
