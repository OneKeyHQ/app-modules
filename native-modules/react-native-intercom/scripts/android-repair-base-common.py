#!/usr/bin/env python3
"""Apply conservative typed JVM-to-Java source repairs; leave ambiguous lifetimes unresolved."""
import hashlib,json,pathlib,re
ROOT=pathlib.Path(__file__).resolve().parent.parent
BASE=ROOT/'source/android/intercom-sdk-base';JAVA=BASE/'java'
class_alias={r[1]:r[2] for line in (BASE/'identifier-aliases.tsv').read_text().splitlines() if (r:=line.split('\t'))[0]=='C'}
supers={};initializers=set()
for line in (BASE/'original-abi.tsv').read_text().splitlines():
 row=line.split('\t');owner=class_alias.get(row[1],row[1])
 if row[0]=='C':supers[owner]=row[4]
 if row[0]=='M' and row[2]=='<clinit>':initializers.add(owner)
records=[]
for path in sorted(JAVA.rglob('*.java')):
 before=path.read_text();text=before;actions=[];owner=path.relative_to(JAVA).as_posix()[:-5]
 text,n=re.subn(r'\bvar (var\d+) = new ([A-Za-z_$][\w$\.]*)(?=[(<\[])',lambda m:m[2]+' '+m[1]+' = new '+m[2],text)
 if n:actions.append(f'{n} NEW expressions retain their explicit constructed class type')
 text,n=re.subn(r'\bvar (var\d+) = \(([A-Za-z_$][\w$\.\[\]]*)\)',lambda m:m[2]+' '+m[1]+' = ('+m[2]+')',text)
 if n:actions.append(f'{n} explicitly cast expressions retain their cast type')
 text,n=re.subn(r'\(boolean\)\s*([01])\b',lambda m:'true' if m[1]=='1' else 'false',text)
 if n:actions.append(f'{n} literal JVM boolean 0/1 representations made Java booleans')
 if supers.get(owner)=='java/lang/Object':
  text,n=re.subn(r'(\{\n)((?:      Intrinsics\.checkNotNullParameter\([^\n]+\);\n)+)(      super\(\);\n)',r'\1\3\2',text)
  if n:actions.append(f'{n} empty Object super calls moved before retained validation')
 if re.search(r'\benum '+re.escape(path.stem)+r'\b',text):
  text,n=re.subn(r'\bpublic '+re.escape(path.stem)+r'\(', 'private '+path.stem+'(',text)
  if n:actions.append(f'{n} enum constructors expressed with required Java source visibility')
 if owner in initializers:
  def stability(m):
   value=m[1]
   return 'public static int $stable;\n\n   static {}' if value is None else 'public static final int $stable;\n\n   static { $stable = '+value+'; }'
  text,n=re.subn(r'public static final int \$stable(?: = (\d+))?;',stability,text)
  if n:actions.append('retain original initialization boundary without Java constant folding')
 if text!=before:
  path.write_text(text);records.append({'file':str(path.relative_to(ROOT)),'before_sha256':hashlib.sha256(before.encode()).hexdigest(),'after_sha256':hashlib.sha256(text.encode()).hexdigest(),'actions':actions,'status':'SOURCE_RESTORED_NOT_COMPILED'})
(BASE/'common-repairs.json').write_text(json.dumps(records,indent=2)+'\n');print('Typed/common source repairs',len(records),'files')
