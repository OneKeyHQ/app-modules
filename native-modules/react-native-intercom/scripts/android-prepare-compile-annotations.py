#!/usr/bin/env python3
"""Provide original annotation namespaces as pinned compile-only dependencies."""
import hashlib,json,os,pathlib,shutil,subprocess,urllib.request
ROOT=pathlib.Path(__file__).resolve().parent.parent
BUILD=ROOT/'.recovery/android-base';SOURCE=ROOT/'source/android/intercom-sdk-base'
coordinates=[('com.google.auto.value','auto-value-annotations','1.10.4'),('com.google.code.findbugs','jsr305','3.0.2'),('com.android.support','support-annotations','28.0.0')]
rows=[];jars=[]
for group,name,version in coordinates:
 directory=pathlib.Path.home()/'.gradle/caches/modules-2/files-2.1'/group/name/version
 cached=next(directory.glob('*/*.jar'),None)
 target=BUILD/'classpath'/f'compile-only-{name}-{version}.jar';target.parent.mkdir(parents=True,exist_ok=True)
 url=('https://dl.google.com/dl/android/maven2' if group=='com.android.support' else 'https://repo.maven.apache.org/maven2')+'/'+group.replace('.','/')+'/'+name+'/'+version+'/'+name+'-'+version+'.jar'
 if cached:shutil.copy2(cached,target)
 elif not target.exists():target.write_bytes(urllib.request.urlopen(url,timeout=60).read())
 rows.append({'group':group,'artifact':name,'version':version,'url':url,'sha256':hashlib.sha256(target.read_bytes()).hexdigest(),'scope':'compile-only','reason':'Retain original annotation namespaces; no annotation processor; never package'})
 jars.append(target)
(SOURCE/'compile-only-dependencies.json').write_text(json.dumps(rows,indent=2)+'\n')
asm=list(pathlib.Path('/Volumes/T7Shield/Project/jpush-jcore-sdk-recovery-20261103/tools/asm').glob('*.jar'))
cp=os.pathsep.join([str(BUILD/'tools'),*[str(p) for p in asm]])
with (BUILD/'normalize-compile-annotations.log').open('w') as stream:subprocess.run(['java','-cp',cp,'NormalizeJvmNames',str(BUILD/'normalized'),*[str(p) for p in jars]],stdout=stream,stderr=subprocess.STDOUT,check=True)
print('Pinned compile-only annotation artifacts',len(jars))
