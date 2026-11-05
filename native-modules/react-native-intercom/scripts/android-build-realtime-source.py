#!/usr/bin/env python3
"""Build Nexus 6.4.0 + Twig 1.3.0 from checked-in source and metadata; no original SDK inputs."""
import argparse,hashlib,json,os,pathlib,runpy,shutil,subprocess,sys,zipfile
ROOT=pathlib.Path(__file__).resolve().parent.parent
SOURCE=ROOT/'source/android/intercom-sdk-base';BUILD=ROOT/'.build/android-realtime-source'
fetch=runpy.run_path(str(ROOT/'scripts/android-build-source.py'))['fetch']
def sha(path):return hashlib.sha256(path.read_bytes()).hexdigest()
def run(command,log):
 with log.open('w')as stream:result=subprocess.run([str(value)for value in command],stdout=stream,stderr=subprocess.STDOUT)
 if result.returncode:raise RuntimeError(f'exit {result.returncode}: {log}')
def main():
 parser=argparse.ArgumentParser(description=__doc__);parser.add_argument('--offline',action='store_true');args=parser.parse_args()
 BUILD.mkdir(parents=True,exist_ok=True);receipt=dict(status='FAIL',scope='Nexus/Twig source-only compilation + original ABI symbols; not full SDK or runtime acceptance',reads_original_sdk=False,vendor_sdk_on_classpath=False,vendor_sdk_packaged=False,runtime_verified=False)
 try:
  for name in ['classes','restored','tools']:
   path=BUILD/name
   if path.exists():shutil.rmtree(path)
   path.mkdir()
  dependencies=BUILD/'dependencies';dependencies.mkdir(exist_ok=True)
  dependency_file=SOURCE/'realtime-external-dependencies.json';rows=json.loads(dependency_file.read_text());jars=[fetch(row,dependencies,args.offline)for row in rows]
  for jar in jars:
   with zipfile.ZipFile(jar)as archive:
    if any(name.startswith(('io/intercom/android/','com/intercom/twig/'))and name.endswith('.class')for name in archive.namelist()):raise RuntimeError('Original SDK payload in external dependency')
  tools=json.loads((ROOT/'source/android/tool-dependencies.json').read_text());asm=[fetch(row,dependencies,args.offline)for row in tools]
  sdk=pathlib.Path(os.environ.get('ANDROID_HOME',str(pathlib.Path.home()/'Library/Android/sdk')));android=sdk/'platforms/android-35/android.jar';jars.append(android)
  jvm=pathlib.Path('/opt/homebrew/opt/openjdk@17/bin');java=jvm/'java';javac=jvm/'javac';toolcp=os.pathsep.join(map(str,asm));tool=ROOT/'tools/RestoreBuildMetadata.java'
  run([javac,'-cp',toolcp,'-d',BUILD/'tools',tool],BUILD/'compile-tools.log')
  sources=sorted((SOURCE/'java/io/intercom/android/nexus').glob('*.java'))+sorted((SOURCE/'java/com/intercom/twig').glob('*.java'))
  if len(sources)!=22:raise RuntimeError('Unexpected realtime source count')
  run([sys.executable,ROOT/'tools/check_recovered_source.py','--report',BUILD/'no-stub-gate.json',*sources],BUILD/'no-stub-gate.log')
  arguments=BUILD/'sources.txt';arguments.write_text(''.join('"'+str(source)+'"\n'for source in sources))
  # These Java modules use unchanged external dependencies, including checked exception declarations.
  run([javac,'--release','8','-proc:none','-Xmaxerrs','1000','-cp',os.pathsep.join(map(str,jars)),'-d',BUILD/'classes','@'+str(arguments)],BUILD/'compile.log')
  abi=SOURCE/'realtime-original-abi.tsv';aliases=SOURCE/'realtime-identifier-aliases.tsv'
  run([java,'-cp',str(BUILD/'tools')+os.pathsep+toolcp,'RestoreBuildMetadata',BUILD/'classes',BUILD/'restored',aliases,abi],BUILD/'restore-metadata.log')
  classes=sorted((BUILD/'restored').rglob('*.class'))
  if len(classes)!=45:raise RuntimeError('Unexpected restored class count')
  artifact=BUILD/'nexus-6.4.0-twig-1.3.0-source.jar'
  with zipfile.ZipFile(artifact,'w',compression=zipfile.ZIP_DEFLATED)as archive:
   for path in classes:
    info=zipfile.ZipInfo(path.relative_to(BUILD/'restored').as_posix(),date_time=(2026,11,4,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;archive.writestr(info,path.read_bytes())
  inputs=[*sources,abi,aliases,dependency_file,ROOT/'source/android/tool-dependencies.json',tool,ROOT/'tools/check_recovered_source.py',ROOT/'scripts/android-build-source.py',pathlib.Path(__file__).resolve()]
  receipt.update(status='PASS_SOURCE_AND_ABI_SYMBOLS',classes=45,source_files=22,missing_abi_records=0,unexpected_extra_abi_records=0,artifact=dict(path=str(artifact),sha256=sha(artifact)),input_hashes=[dict(path=str(path.relative_to(ROOT)),sha256=sha(path))for path in sorted(set(inputs))],dependencies=rows,build_environment=dict(android_jar_sha256=sha(android),javac_sha256=sha(javac),java_sha256=sha(java)))
 except(OSError,RuntimeError,subprocess.SubprocessError,zipfile.BadZipFile)as error:receipt['error']=str(error)
 (BUILD/'receipt.json').write_text(json.dumps(receipt,indent=2)+'\n');print(json.dumps({key:value for key,value in receipt.items()if key not in ['input_hashes','dependencies']},indent=2));return 0 if receipt['status']=='PASS_SOURCE_AND_ABI_SYMBOLS'else 1
if __name__=='__main__':sys.exit(main())
