#!/usr/bin/env python3
"""Recover base/media/realtime source and attempt the complete vendor-free base compilation closure."""
import datetime,argparse,hashlib,json,os,pathlib,re,shutil,subprocess,sys,zipfile,xml.etree.ElementTree as ET
ROOT=pathlib.Path(__file__).resolve().parent.parent
BUILD=ROOT/'.recovery/android-base'
AUDIT=pathlib.Path('/Volumes/T7Shield/Project/intercom-native-audit-20261104/android')
REFERENCE_TOOLS=pathlib.Path('/Volumes/T7Shield/Project/jpush-jcore-sdk-recovery-20261103/tools')
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def run(command,log,fail=True):
 with log.open('w') as stream:done=subprocess.run([str(v) for v in command],stdout=stream,stderr=subprocess.STDOUT)
 if fail and done.returncode:raise RuntimeError(f'exit {done.returncode}: {log}')
 return done.returncode

def main():
 parser=argparse.ArgumentParser(description=__doc__);parser.add_argument('--compile-only',action='store_true');parser.add_argument('--refresh-decompiled',action='store_true');parser.add_argument('--java24-diagnostic',action='store_true',help='Diagnostic only: retain original pre-super source with the installed Java24 preview compiler; normal App/source module builders unchanged');args=parser.parse_args()
 BUILD.mkdir(parents=True,exist_ok=True)
 previous=BUILD/'attempts'/datetime.datetime.now(datetime.timezone.utc).strftime('%Y%m%dT%H%M%S%fZ');previous.mkdir(parents=True,exist_ok=True)
 for path in [*BUILD.glob('*.log'),*BUILD.glob('*.json'),*BUILD.glob('restored-*.tsv')]:shutil.copy2(path,previous/path.name)
 receipt={'status':'FAIL','scope':'Full base/lightcompressor/Nexus/Twig source compilation experiment; SDK integration incomplete','original_sdk_on_compile_classpath':False,'original_sdk_bytecode_packaged':False}
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
   (deps/'settings.gradle').write_text("rootProject.name = 'intercom-source-base-dependencies'\n")
   manifest=json.loads((AUDIT/'manifest.json').read_text());ui=next(i for i in manifest if i['artifact']=='intercom-sdk-base')
   lines=["plugins { id 'java-library' }","repositories { google(); mavenCentral() }","abstract class JvmCompatibility implements AttributeCompatibilityRule<String> { void execute(CompatibilityCheckDetails<String> details) { if (details.consumerValue == 'androidJvm' && details.producerValue == 'jvm') details.compatible() } }","dependencies { attributesSchema { attribute(Attribute.of('org.jetbrains.kotlin.platform.type', String)) { compatibilityRules.add(JvmCompatibility) } } }","configurations { recovery { canBeResolved = true; canBeConsumed = false; attributes { attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage, 'java-api')); attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category, 'library')); attribute(TargetJvmEnvironment.TARGET_JVM_ENVIRONMENT_ATTRIBUTE, objects.named(TargetJvmEnvironment, 'android')); attribute(Attribute.of('org.jetbrains.kotlin.platform.type', String), 'androidJvm') } } }","dependencies {"]
   bom=next((pathlib.Path.home()/'.gradle/caches/modules-2/files-2.1/androidx.compose/compose-bom/2026.06.01').glob('*/*.pom'))
   ns={'m':'http://maven.apache.org/POM/4.0.0'};tree=ET.parse(bom)
   versions={(d.findtext('m:groupId',namespaces=ns),d.findtext('m:artifactId',namespaces=ns)):d.findtext('m:version',namespaces=ns) for d in tree.findall('.//m:dependencyManagement/m:dependencies/m:dependency',ns)}
   for d in ui['dependencies']:
    if d['groupId'] in ['io.intercom.android','com.intercom']:continue
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
   reference_jar=reference/'intercom-sdk-base-reference.jar';shutil.copy2(AUDIT/'extracted/intercom-sdk-base/classes.jar',reference_jar)
   source_deps=BUILD/'source-dependencies';source_deps.mkdir(exist_ok=True)
   pinned_receipt=source_deps/'ui-source-build-receipt.json'
   if pinned_receipt.exists():
    ui_receipt=json.loads(pinned_receipt.read_text())
   else:
    ui_build=ROOT/'.build/android-ui-source';ui_receipt=json.loads((ui_build/'receipt.json').read_text())
   if ui_receipt['status']!='PASS_SOURCE_AND_ABI_SYMBOLS' or ui_receipt['classes']!=83:raise RuntimeError('Source-built UI prerequisite incomplete')
   ui_hash=ui_receipt['artifact']['sha256'];ui_jar=source_deps/(ui_hash+'.jar')
   if not ui_jar.exists():
    original_source_build=pathlib.Path(ui_receipt['artifact']['path'])
    if sha(original_source_build)!=ui_hash:raise RuntimeError('Source-built UI hash mismatch')
    temporary=source_deps/(ui_hash+'.copying');shutil.copy2(original_source_build,temporary)
    if sha(temporary)!=ui_hash:raise RuntimeError('Source-built UI changed during copy')
    temporary.replace(ui_jar);pinned_receipt.write_text(json.dumps(ui_receipt,indent=2)+'\n')
   if sha(ui_jar)!=ui_hash:raise RuntimeError('Pinned source-built UI dependency was modified')
   for item in ui_receipt['source_hashes']:
    if sha(ROOT/item['path'])!=item['sha256']:raise RuntimeError('Pinned UI source is stale: '+item['path'])
   target=classpath/'intercom-sdk-ui-source.jar';shutil.copy2(ui_jar,target);jars.append(target)
   receipt['source_built_ui']={'path':str(ui_jar),'sha256':ui_hash,'source_build_receipt':str(pinned_receipt)}
   toolclasses=BUILD/'tools';toolclasses.mkdir(exist_ok=True);toolcp=os.pathsep.join(str(p) for p in asm)
   run([javac,'-cp',toolcp,'-d',toolclasses,*[ROOT/'tools'/f for f in ['NormalizeJvmNames.java','ExportAbi.java','ArtifactFingerprint.java']]],BUILD/'compile-tools.log')
   toolrun=os.pathsep.join([str(toolclasses),toolcp])
   source=ROOT/'source/android/intercom-sdk-base';source.mkdir(parents=True,exist_ok=True)
   references=[reference_jar]
   for name in ['intercom-sdk-lightcompressor','nexus-client-android','twig']:
    target=reference/(name+'-reference.jar');shutil.copy2(AUDIT/'extracted'/name/'classes.jar',target);references.append(target)
   run([java,'-cp',toolrun,'ExportAbi',source/'original-abi.tsv',*references],BUILD/'export-abi.log')
   ui['class_count']=3301
   normalized=BUILD/'normalized'
   run([java,'-Xmx4g','-cp',toolrun,'NormalizeJvmNames',normalized,*references,*jars],BUILD/'normalize.log')
   shutil.copy2(normalized/'identifier-aliases.tsv',source/'identifier-aliases.tsv')
   abi=(source/'original-abi.tsv').read_text().splitlines();(source/'synthetic-abi.tsv').write_text('\n'.join(line for line in abi if (p:=line.split('\t'))[0]=='M' and int(p[4]) & (0x1000|0x40))+'\n')
   decompiled=BUILD/'decompiled';decompiled.mkdir(exist_ok=True)
   external=[f'-e={normalized/p.name}' for p in jars]
   run([java,'-Xmx4g','-jar',vf,'--folder','--thread-count=4','--kt-enable=false','--decompile-inner=false','--use-lvt-names=false','--use-method-parameters=false','--remove-synthetic=false','--remove-bridge=false','--decompile-generics=false','--lambda-to-anonymous-class=false',*external,*[normalized/p.name for p in references],decompiled],BUILD/'decompile.log')
   output=source/'java'
   if output.exists() and list(output.rglob('*.java')):
    if not args.refresh_decompiled:raise RuntimeError('Source already exists; use --compile-only, or explicit --refresh-decompiled to replace decompiler output')
    shutil.rmtree(output)
   output.mkdir(exist_ok=True)
   for p in decompiled.rglob('*.java'):
    target=output/p.relative_to(decompiled);target.parent.mkdir(parents=True,exist_ok=True);shutil.copy2(p,target)

  else:
   source=ROOT/'source/android/intercom-sdk-base';output=source/'java';normalized=BUILD/'normalized';dependency_rows=json.loads((BUILD/'dependencies/artifacts.json').read_text());jars=sorted((BUILD/'classpath').glob('*.jar'));android=pathlib.Path.home()/'Library/Android/sdk/platforms/android-35/android.jar';javac=pathlib.Path('/opt/homebrew/opt/openjdk@17/bin/javac');ui={'class_count':3301}
  realtime_receipt_path=BUILD/'source-dependencies/realtime-source-build-receipt.json'
  if realtime_receipt_path.exists():
   realtime=json.loads(realtime_receipt_path.read_text())
   if realtime['status']!='PASS_SOURCE_AND_ABI_SYMBOLS' or realtime['classes']!=45:raise RuntimeError('Incomplete source-built realtime prerequisite')
   for item in realtime['input_hashes']:
    if sha(ROOT/item['path'])!=item['sha256']:raise RuntimeError('Source-built realtime input stale: '+item['path'])
   realtime_jar=BUILD/'source-dependencies'/(realtime['artifact']['sha256']+'.jar')
   if sha(realtime_jar)!=realtime['artifact']['sha256']:raise RuntimeError('Pinned source-built realtime JAR changed')
   compile_realtime=BUILD/'classpath/nexus-twig-source.jar'
   if not compile_realtime.exists() or sha(compile_realtime)!=realtime['artifact']['sha256']:raise RuntimeError('Realtime compile dependency differs from source receipt')
   if compile_realtime not in jars:jars.append(compile_realtime)
   receipt['source_built_realtime']={'classes':45,'source_files':22,'path':str(realtime_jar),'sha256':realtime['artifact']['sha256'],'source_build_receipt':str(realtime_receipt_path)}
   ui['class_count']=3256
  transitions_receipt_path=BUILD/'source-dependencies/transitions-source-build-receipt.json'
  if transitions_receipt_path.exists():
   transitions=json.loads(transitions_receipt_path.read_text())
   if transitions['status']!='PASS_SOURCE_AND_ABI_SYMBOLS' or transitions['classes']!=13:raise RuntimeError('Incomplete source-built transition enums')
   for item in transitions['input_hashes']:
    if sha(ROOT/item['path'])!=item['sha256']:raise RuntimeError('Source-built transition input stale: '+item['path'])
   transition_jar=BUILD/'source-dependencies'/(transitions['artifact']['sha256']+'.jar')
   if sha(transition_jar)!=transitions['artifact']['sha256']:raise RuntimeError('Pinned source-built transition JAR changed')
   transition_compile=BUILD/'classpath/transition-enums-source.jar'
   shutil.copy2(transition_jar,transition_compile)
   # The Kotlin closure already uses original legal symbols and complete enum metadata.
   # Keep its raw source-built declaration view, including marker constructors.
   shutil.copy2(transition_jar,normalized/transition_compile.name)
   if transition_compile not in jars:jars.append(transition_compile)
   receipt['source_built_transition_enums']={'classes':13,'source_files':1,'language':'Kotlin','path':str(transition_jar),'sha256':transitions['artifact']['sha256'],'source_build_receipt':str(transitions_receipt_path)}
   ui['class_count']-=13
  icons_receipt_path=BUILD/'source-dependencies/icons-source-build-receipt.json'
  if icons_receipt_path.exists():
   icons=json.loads(icons_receipt_path.read_text())
   if icons['status']!='PASS_SOURCE_AND_ABI_SYMBOLS' or icons['classes']!=9:raise RuntimeError('Incomplete source-built icon enums')
   for item in icons['input_hashes']:
    if sha(ROOT/item['path'])!=item['sha256']:raise RuntimeError('Source-built icon input stale: '+item['path'])
   icon_artifacts={}
   for kind in ['artifact','compile_artifact']:
    row=icons[kind];path=BUILD/'source-dependencies'/(row['sha256']+'.jar')
    if sha(path)!=row['sha256']:raise RuntimeError('Pinned source-built icon JAR changed')
    icon_artifacts[kind]={'path':str(path),'sha256':row['sha256']}
   icon_compile=BUILD/'classpath/icon-enums-source.jar'
   # Raw Kotlin source output is the flat Java declaration view. The final SDK
   # artifact separately retains original nested class/annotation metadata.
   shutil.copy2(icon_artifacts['compile_artifact']['path'],icon_compile)
   shutil.copy2(icon_compile,normalized/icon_compile.name)
   if icon_compile not in jars:jars.append(icon_compile)
   receipt['source_built_icon_enums']={'classes':9,'source_files':2,'language':'Kotlin','artifacts':icon_artifacts,'source_build_receipt':str(icons_receipt_path),'resource_runtime_verified':False}
   ui['class_count']-=9
  resource_receipt=BUILD/'source-dependencies/resource-build-receipt.json'
  if resource_receipt.exists():
   resource_evidence=json.loads(resource_receipt.read_text());resource_status=0;receipt['resource_closure']=resource_evidence
   receipt['resource_link_exit_code']=0;receipt['resource_runtime_verified']=False;receipt['compile_only_resource_symbols']=False
   for item in resource_evidence['generated_resource_jars']:
    if sha(pathlib.Path(item['path']))!=item['sha256']:raise RuntimeError('Generated resource dependency changed')
   generated=BUILD/'resources-java'
   if generated.exists():shutil.rmtree(generated)
   generated.mkdir(exist_ok=True)
  else:
   aapt=pathlib.Path.home()/'Library/Android/sdk/build-tools/35.0.0/aapt2';reszip=BUILD/'resources.zip'
   run([aapt,'compile','--dir',source/'res','-o',reszip],BUILD/'compile-resources.log')
   generated=BUILD/'resources-java';generated.mkdir(exist_ok=True)
   resource_status=run([aapt,'link','--static-lib','--manifest',source/'AndroidManifest.xml','-I',android,'-o',BUILD/'resources.apk','--java',generated,'-R',reszip,'--auto-add-overlay'],BUILD/'link-resources.log',fail=False)
   receipt['resource_link_exit_code']=resource_status
   receipt['resource_runtime_verified']=False
   if resource_status:
    # Compile-only R declarations expose symbol ABI while retaining the failed resource gate.
    # They are never packaged or counted as recovered implementation classes.
    receipt['compile_only_resource_symbols']=True
    for symbols,namespace in [(source/'R.txt','io.intercom.android.sdk'),(ROOT/'source/android/intercom-sdk-ui/R.txt','io.intercom.android.sdk.ui')]:
     groups={}
     for line in symbols.read_text().splitlines():
      kind,group,name,value=line.split(' ',3);groups.setdefault(group,[]).append('public static '+kind+' '+name+' = '+('new int[] '+value if kind=='int[]' else value)+';')
     text='package '+namespace+';\npublic final class R {\n'+''.join('public static final class '+group+' {\n'+'\n'.join(fields)+'\n}\n' for group,fields in groups.items())+'}\n'
     target=generated/namespace.replace('.','/')/'R.java';target.parent.mkdir(parents=True,exist_ok=True);target.write_text(text)

  # Synthetic Kotlin defaults are visible in the compile view, but ordinary
  # covariant bridges must retain their original hidden Java declaration flags.
  bridge_tools=BUILD/'compile-view-tools';bridge_tools.mkdir(exist_ok=True)
  bridge_asm=sorted((REFERENCE_TOOLS/'asm').glob('*.jar'))
  bridge_cp=os.pathsep.join(str(path) for path in bridge_asm)
  bridge_jvm=pathlib.Path('/opt/homebrew/opt/openjdk@17/bin')
  run([bridge_jvm/'javac','-cp',bridge_cp,'-d',bridge_tools,ROOT/'tools/RestoreCompileBridgeFlags.java',ROOT/'tools/RestoreFunctionSamSignatures.java',ROOT/'tools/RestoreExternalMethodExceptions.java',ROOT/'tools/PrepareRememberSaveableCompileAlias.java'],BUILD/'compile-bridge-tools.log')
  bridge_ledger=BUILD/'restored-external-bridge-flags.tsv'
  run([bridge_jvm/'java','-cp',str(bridge_tools)+os.pathsep+bridge_cp,'RestoreCompileBridgeFlags',BUILD/'classpath',normalized,source/'identifier-aliases.tsv',bridge_ledger],BUILD/'restore-external-bridge-flags.log')
  receipt['external_bridge_visibility']={'scope':'Original third-party ACC_BRIDGE flags plus exact deprecated SupervisorJob regular/default overloads returning Job; other Kotlin defaults remain visible; runtime dependencies unchanged','ledger':str(bridge_ledger),'restored_this_run':len(bridge_ledger.read_text().splitlines()),'tool_sha256':sha(ROOT/'tools/RestoreCompileBridgeFlags.java')}
  sam_ledger=BUILD/'restored-function-sam-signatures.tsv'
  run([bridge_jvm/'java','-cp',str(bridge_tools)+os.pathsep+bridge_cp,'RestoreFunctionSamSignatures',BUILD/'classpath',normalized,sam_ledger],BUILD/'restore-function-sam-signatures.log')
  receipt['typed_function_sam_signatures']={'scope':'Only frozen kotlin.Function/Function1, ActivityResultCallback and android ValueCallback class/SAM method signatures; no Code/runtime changes','ledger':str(sam_ledger),'changed_attributes_this_run':len(sam_ledger.read_text().splitlines()),'tool_sha256':sha(ROOT/'tools/RestoreFunctionSamSignatures.java')}
  exceptions_ledger=BUILD/'restored-external-exceptions.tsv'
  run([bridge_jvm/'java','-cp',str(bridge_tools)+os.pathsep+bridge_cp,'RestoreExternalMethodExceptions',BUILD/'classpath',normalized,source/'compile-exception-declarations.tsv',exceptions_ledger],BUILD/'restore-external-exceptions.log')
  receipt['external_exception_declarations']={'scope':'Finite exact original external/platform method Exceptions listed in compile-exception-declarations.tsv; original runtime dependencies and source catch bodies unchanged','ledger':str(exceptions_ledger),'changed_attributes_this_run':len(exceptions_ledger.read_text().splitlines()),'input_sha256':sha(source/'compile-exception-declarations.tsv')}
  return_alias_ledger=BUILD/'restored-external-return-aliases.tsv'
  run([bridge_jvm/'java','-cp',str(bridge_tools)+os.pathsep+bridge_cp,'PrepareRememberSaveableCompileAlias',BUILD/'classpath',normalized,source/'identifier-aliases.tsv',return_alias_ledger],BUILD/'prepare-remember-saveable-alias.log')
  receipt['external_return_only_alias']={'scope':'Exact rememberSaveable MutableState-return compile declaration alias; Object-return stays visible; source output restores original invokestatic owner/name/full descriptor','ledger':str(return_alias_ledger),'changed_this_run':len(return_alias_ledger.read_text().splitlines()),'tool_sha256':sha(ROOT/'tools/PrepareRememberSaveableCompileAlias.java')}
  recovered_sources=sorted(output.rglob('*.java'))
  if 'source_built_realtime' in receipt:
   recovered_sources=[path for path in recovered_sources if not path.relative_to(output).as_posix().startswith(('io/intercom/android/nexus/','com/intercom/twig/'))]
  if 'source_built_transition_enums' in receipt:
   transition_owners={row[1] for line in (source/'transition-original-abi.tsv').read_text().splitlines() if (row:=line.split('\t'))[0]=='C'}
   duplicates=[path for path in recovered_sources if path.relative_to(output).as_posix()[:-5] in transition_owners]
   if duplicates:raise RuntimeError('Duplicate Java declarations for source-built Kotlin transition enums')
  if 'source_built_icon_enums' in receipt:
   icon_owners={row[1] for line in (source/'icon-original-abi.tsv').read_text().splitlines() if (row:=line.split('\t'))[0]=='C'}
   if any(path.relative_to(output).as_posix()[:-5] in icon_owners for path in recovered_sources):raise RuntimeError('Duplicate Java declarations for source-built Kotlin icon enums')
  if not recovered_sources:raise RuntimeError('No recovered Java sources; generated resource R.java alone is not SDK recovery')
  run([sys.executable,ROOT/'tools/check_recovered_source.py','--report',BUILD/'no-stub-gate.json',output],BUILD/'no-stub-gate.log')
  receipt['known_decompiler_placeholders']=json.loads((BUILD/'no-stub-gate.json').read_text())
  javafiles=recovered_sources+sorted(generated.rglob('*.java'));argsfile=BUILD/'sources.txt';argsfile.write_text(''.join('"'+str(p)+'"\n' for p in javafiles))
  classes=BUILD/'classes'
  if classes.exists():shutil.rmtree(classes)
  classes.mkdir(exist_ok=True)
  cp=os.pathsep.join(str(normalized/p.name) for p in jars)
  language=['--release','8']
  if args.java24_diagnostic:
   javac=pathlib.Path('/Library/Java/JavaVirtualMachines/zulu-24.jdk/Contents/Home/bin/javac')
   language=['--enable-preview','--release','24','-XDstringConcat=inline']
   receipt['diagnostic_only']=True
   receipt['normal_source_build_adopts_java24']=False
   receipt['java24_compatibility_gate']='NOT_RUN: full output must pass attribute/bootstrap/API/private-access + original ABI and Android gates before adopting a source-only build'
  receipt['compiler']={'path':str(javac),'sha256':sha(javac),'language_flags':language,'version':subprocess.run([str(javac),'-version'],capture_output=True,text=True,check=True).stdout.strip()}
  command=[javac,*language,'-proc:none','-Xmaxerrs','10000','-Xlint:-options','-cp',cp,'-d',classes,'@'+str(argsfile)]
  receipt['compile_classpath_hashes']=[{'path':str(normalized/path.name),'sha256':sha(normalized/path.name)} for path in jars]
  receipt['source_hashes']=[{'path':str(path.relative_to(ROOT)),'sha256':sha(path)} for path in recovered_sources]
  receipt['source_tool_hashes']=[{'path':str(path.relative_to(ROOT)),'sha256':sha(path)} for path in sorted((ROOT/'tools').glob('*.java'))]+[{'path':str(pathlib.Path(__file__).relative_to(ROOT)),'sha256':sha(pathlib.Path(__file__))}]
  status=run(command,BUILD/'compile.log',fail=False)
  receipt.update(source_files=len(javafiles),expected_original_classes=ui['class_count'],compiled_classes=len(list(classes.rglob('*.class'))),compile_command=[str(v) for v in command],compile_exit_code=status,dependencies=[{**r,'sha256':sha(pathlib.Path(r['path']))} for r in dependency_rows])
  diagnostics=(BUILD/'compile.log').read_text()
  errors=re.findall(r'(?m)^(\d+) errors?$',diagnostics)
  shown=len(re.findall(r'(?m)^.*?:\d+: error:',diagnostics))
  receipt['compiler_diagnostics']={'total_errors':int(errors[-1]) if errors else 0,'printed_errors':shown,'maximum_errors':10000,'truncated':bool(errors and shown<int(errors[-1])),'log':str(BUILD/'compile.log')}
  if status:raise RuntimeError('Recovered base/media/realtime Java does not compile; see compile.log. No original bytecode fallback was attempted.')
  if resource_status:raise RuntimeError('Source compilation passed but native resource closure failed; source SDK is not complete')
  receipt['status']='SOURCE_COMPILED_ABI_NOT_VALIDATED'
 except (OSError,RuntimeError,KeyError,subprocess.SubprocessError,zipfile.BadZipFile) as error:receipt['error']=str(error)
 (BUILD/'receipt.json').write_text(json.dumps(receipt,indent=2)+'\n')
 print(json.dumps({k:v for k,v in receipt.items() if k not in ['compile_command','dependencies','tools','source_hashes','source_tool_hashes']},indent=2))
 return 0 if receipt['status']!='FAIL' else 1
if __name__=='__main__':sys.exit(main())
