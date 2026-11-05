#!/usr/bin/env python3
"""Build recovered UI from checked-in Java/resources/metadata only, without vendor inputs."""
import argparse,hashlib,json,os,pathlib,shutil,subprocess,sys,urllib.request,zipfile
ROOT=pathlib.Path(__file__).resolve().parent.parent
BUILD=ROOT/'.build/android-ui-source'
SOURCE=ROOT/'source/android/intercom-sdk-ui'
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def run(command,log):
 with log.open('w') as stream:result=subprocess.run([str(v) for v in command],stdout=stream,stderr=subprocess.STDOUT)
 if result.returncode:raise RuntimeError(f'exit {result.returncode}: {log}')
def fetch(row,folder,offline):
 target=folder/(row['artifact']+'-'+row['version']+row['extension'])
 if target.exists() and sha(target)==row['sha256']:return target
 cache=pathlib.Path.home()/'.gradle/caches/modules-2/files-2.1'/row['group']/row['artifact']/row['version']
 for candidate in cache.glob('*/*'):
  if candidate.suffix==row['extension'] and sha(candidate)==row['sha256']:shutil.copy2(candidate,target);return target
 if offline:raise RuntimeError('Offline dependency missing: '+str(row))
 data=urllib.request.urlopen(row['url'],timeout=90).read();target.write_bytes(data)
 if sha(target)!=row['sha256']:raise RuntimeError('Dependency SHA mismatch: '+str(target))
 return target

def main():
 parser=argparse.ArgumentParser(description=__doc__);parser.add_argument('--offline',action='store_true');args=parser.parse_args()
 BUILD.mkdir(parents=True,exist_ok=True)
 receipt={'status':'FAIL','scope':'intercom-sdk-ui 18.10.0 source-only module, not full Intercom SDK','reads_original_sdk':False,'vendor_sdk_on_classpath':False,'vendor_sdk_packaged':False,'runtime_verified':False}
 try:
  for name in ['classes','restored','tools','classpath','normalized','resources-java']:
   folder=BUILD/name
   if folder.exists():shutil.rmtree(folder)
   folder.mkdir()
  dependencies=BUILD/'dependencies';dependencies.mkdir(exist_ok=True)
  rows=json.loads((SOURCE/'external-dependencies.json').read_text());tools=json.loads((ROOT/'source/android/tool-dependencies.json').read_text())
  fetched=[fetch(row,dependencies,args.offline) for row in rows];asm=[fetch(row,dependencies,args.offline) for row in tools]
  jars=[]
  for index,artifact in enumerate(fetched):
   target=BUILD/'classpath'/f'external-{index:03d}.jar'
   if artifact.suffix=='.aar':
    with zipfile.ZipFile(artifact) as z:
     if 'classes.jar' not in z.namelist():continue
     target.write_bytes(z.read('classes.jar'))
   else:shutil.copy2(artifact,target)
   with zipfile.ZipFile(target) as z:
    if any(n.startswith(('io/intercom/android/','com/intercom/twig/')) and n.endswith('.class') for n in z.namelist()):raise RuntimeError('Vendor SDK class in external dependency: '+str(artifact))
   jars.append(target)
  sdk=pathlib.Path(os.environ.get('ANDROID_HOME',str(pathlib.Path.home()/'Library/Android/sdk')))
  android=sdk/'platforms/android-35/android.jar';aapt=sdk/'build-tools/35.0.0/aapt2';jvm=pathlib.Path('/opt/homebrew/opt/openjdk@17/bin');javac=jvm/'javac';java=jvm/'java'
  target=BUILD/'classpath/android.jar';shutil.copy2(android,target);jars.append(target)
  toolcp=os.pathsep.join(str(v) for v in asm)
  run([javac,'-cp',toolcp,'-d',BUILD/'tools',ROOT/'tools/NormalizeJvmNames.java',ROOT/'tools/RestoreBuildMetadata.java'],BUILD/'compile-tools.log')
  toolrun=os.pathsep.join([str(BUILD/'tools'),toolcp])
  run([java,'-Xmx4g','-cp',toolrun,'NormalizeJvmNames',BUILD/'normalized',*jars],BUILD/'compile-view.log')
  # The compile-only view contains only pinned third-party dependencies and the Android API.
  # Runtime artifacts below are created solely from recovered Java compiler output.
  run([aapt,'compile','--dir',SOURCE/'res','-o',BUILD/'resources.zip'],BUILD/'compile-resources.log')
  run([aapt,'link','--static-lib','--manifest',SOURCE/'AndroidManifest.xml','-I',android,'-o',BUILD/'resources.apk','--java',BUILD/'resources-java','-R',BUILD/'resources.zip','--auto-add-overlay'],BUILD/'link-resources.log')
  sources=sorted((SOURCE/'java').rglob('*.java'))+sorted((BUILD/'resources-java').rglob('*.java'))
  if len(sources)!=84:raise RuntimeError('Unexpected UI source inventory: '+str(len(sources)))
  run([sys.executable,ROOT/'tools/check_recovered_source.py','--report',BUILD/'no-stub-gate.json',SOURCE/'java'],BUILD/'no-stub-gate.log')
  argsfile=BUILD/'sources.txt';argsfile.write_text(''.join('"'+str(v)+'"\n' for v in sources))
  cp=os.pathsep.join(str(BUILD/'normalized'/v.name) for v in jars)
  run([javac,'--release','8','-proc:none','-Xmaxerrs','3000','-Xlint:-options','-cp',cp,'-d',BUILD/'classes','@'+str(argsfile)],BUILD/'compile.log')
  run([java,'-cp',toolrun,'RestoreBuildMetadata',BUILD/'classes',BUILD/'restored',SOURCE/'identifier-aliases.tsv',SOURCE/'original-abi.tsv'],BUILD/'restore-metadata.log')
  classes=sorted((BUILD/'restored').rglob('*.class'))
  if len(classes)!=83:raise RuntimeError('Unexpected restored class count')
  run([sys.executable,ROOT/'tools/write_kotlin_module.py',SOURCE/'kotlin-module.json',BUILD/'module-metadata'],BUILD/'write-kotlin-module.log')
  artifact=BUILD/'intercom-sdk-ui-18.10.0-source.jar'
  with zipfile.ZipFile(artifact,'w',compression=zipfile.ZIP_DEFLATED) as output:
   for compiled in classes:
    info=zipfile.ZipInfo(compiled.relative_to(BUILD/'restored').as_posix(),date_time=(2026,11,4,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;output.writestr(info,compiled.read_bytes())
   for metadata in sorted((BUILD/'module-metadata').rglob('*')):
    if metadata.is_file():
     info=zipfile.ZipInfo(metadata.relative_to(BUILD/'module-metadata').as_posix(),date_time=(2026,11,4,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;output.writestr(info,metadata.read_bytes())
  input_files=sorted(set([*list((SOURCE/'java').rglob('*.java')),*[p for p in (SOURCE/'res').rglob('*') if p.is_file()],SOURCE/'AndroidManifest.xml',SOURCE/'original-abi.tsv',SOURCE/'identifier-aliases.tsv',SOURCE/'external-dependencies.json',SOURCE/'kotlin-module.json',ROOT/'source/android/tool-dependencies.json',ROOT/'tools/NormalizeJvmNames.java',ROOT/'tools/RestoreBuildMetadata.java',ROOT/'tools/write_kotlin_module.py',ROOT/'tools/check_recovered_source.py',pathlib.Path(__file__).resolve()]))
  receipt['input_hashes']=[{'path':str(p.relative_to(ROOT)),'sha256':sha(p)} for p in input_files]
  receipt['build_environment']={'android_jar_sha256':sha(android),'aapt2_sha256':sha(aapt),'javac_sha256':sha(javac),'java_sha256':sha(java)}
  receipt.update(status='PASS_SOURCE_AND_ABI_SYMBOLS',source_files=len(sources),classes=len(classes),missing_abi_records=0,unexpected_extra_abi_records=0,private_compiler_lambda_helpers=len((BUILD/'compiler-lambda-methods.tsv').read_text().splitlines()),artifact={'path':str(artifact),'sha256':sha(artifact)},source_hashes=[{'path':str(v.relative_to(ROOT)),'sha256':sha(v)} for v in sorted((SOURCE/'java').rglob('*.java'))],dependencies=rows,metadata_scope='Names, descriptors, declaration flags, generic signatures, inner/enclosing metadata; ConstantValue/annotations/behavior not fully compared')
 except (OSError,RuntimeError,subprocess.SubprocessError,zipfile.BadZipFile) as error:receipt['error']=str(error)
 (BUILD/'receipt.json').write_text(json.dumps(receipt,indent=2)+'\n');print(json.dumps({k:v for k,v in receipt.items() if k not in ['source_hashes','dependencies','input_hashes']},indent=2));return 0 if receipt['status']=='PASS_SOURCE_AND_ABI_SYMBOLS' else 1
if __name__=='__main__':sys.exit(main())
