#!/usr/bin/env python3
"""Restore original enum constant bodies and explicit constructor source expressions."""
import hashlib,json,pathlib,re,shutil
ROOT=pathlib.Path(__file__).resolve().parent.parent
BASE=ROOT/'source/android/intercom-sdk-base';JAVA=BASE/'java'
AUDIT=pathlib.Path('/Volumes/T7Shield/Project/intercom-native-audit-20261104/android')
records=[]
def sha(path):return hashlib.sha256(path.read_bytes()).hexdigest()
# These Java enums decompile correctly with JADX's constant-body handling. All overrides remain.
for artifact,name in [('nexus-client-android','io/intercom/android/nexus/NexusEventType'),('intercom-sdk-base','io/intercom/android/sdk/blocks/lib/BlockType'),('intercom-sdk-base','io/intercom/android/sdk/blocks/lib/BlockAlignment')]:
 source=AUDIT/'jadx'/artifact/'sources'/(name+'.java');target=JAVA/(name+'.java')
 before=sha(target);shutil.copy2(source,target)
 children=sorted(target.parent.glob(target.stem+'$[0-9]*.java'))
 # Body code is now inside real enum constants; javac regenerates the corresponding anonymous classes.
 for child in children:child.unlink()
 records.append({'file':str(target.relative_to(ROOT)),'before_sha256':before,'after_sha256':sha(target),'basis':str(source),'anonymous_constant_classes_inlined':len(children),'status':'SOURCE_RESTORED_NOT_COMPILED'})
# JVM permits allocation followed by separate invokespecial. Java needs a complete constructor expression.
# Preserve every argument computation/branch and record each site for later behavior comparison.
allocation=re.compile(r'(?m)^(?P<indent> *)(?:(?P<decl>[A-Za-z_$][\w$\.<>\[\]]*) )?(?P<variable>var\d+) = new (?P<type>[A-Za-z_$][\w$\.]*)[;]$')
for path in sorted(JAVA.rglob('*.java')):
 text=path.read_text();before=sha(path);sites=[]
 while True:
  match=allocation.search(text)
  if not match:break
  variable=match['variable'];tail=text[match.end():]
  call=re.search(re.escape(variable)+r'\./\* \$VF: Unable to resugar constructor \*/<init>\(',tail)
  if call is None:raise RuntimeError('No constructor invocation for allocation '+str(path)+':'+variable)
  call_start=match.end()+call.start();call_end=match.end()+call.end()
  between=text[match.end():call_start]
  if re.search(r'\b'+re.escape(variable)+r'\b',between):raise RuntimeError('Uninitialized reference is used before constructor: '+str(path)+':'+variable)
  declaration=match['indent']+(match['decl']+' '+variable+';' if match['decl'] else ';')
  text=text[:call_start]+variable+' = new '+match['type']+'('+text[call_end:]
  text=text[:match.start()]+declaration+text[match.end():]
  sites.append({'variable':variable,'constructor_type':match['type'],'comparison_status':'ARGUMENT_BRANCHES_RETAINED_ALLOCATION_TIMING_NOT_RUNTIME_VERIFIED'})
 if sites:
  path.write_text(text);records.append({'file':str(path.relative_to(ROOT)),'before_sha256':before,'after_sha256':sha(path),'constructor_sites':sites,'status':'SOURCE_RESTORED_NOT_COMPILED'})
(BASE/'syntax-repairs.json').write_text(json.dumps(records,indent=2)+'\n')
print('Repaired',len(records),'files;',sum(len(r.get('constructor_sites',[])) for r in records),'constructor sites')
