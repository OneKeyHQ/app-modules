#!/usr/bin/env python3
"""Restore unambiguous static lambda sites from reviewed bootstrap/capture descriptors."""
import hashlib,json,pathlib,re
ROOT=pathlib.Path(__file__).resolve().parent.parent
BASE=ROOT/'source/android/intercom-sdk-base';JAVA=BASE/'java'
owned={p.relative_to(JAVA).as_posix()[:-5] for p in JAVA.rglob('*.java')}
def jtype(desc):
 if desc.startswith('['):return jtype(desc[1:])+'[]'
 if desc.startswith('L'):
  name=desc[1:-1];return name.replace('/','.') if name in owned else name.replace('/','.').replace('$','.')
 return dict(V='void',Z='boolean',B='byte',C='char',S='short',I='int',J='long',F='float',D='double')[desc]
def params(desc):
 text=desc[1:desc.index(')')];out=[]
 while text:
  n=0
  while text[n]=='[':n+=1
  end=text.index(';',n)+1 if text[n]=='L' else n+1;out.append(text[:end]);text=text[end:]
 return out
def adapt(desc,arg):
 if desc.startswith(('L','[')):return '('+jtype(desc)+')'+arg
 box,method={'I':('Integer','intValue'),'J':('Long','longValue'),'F':('Float','floatValue'),'D':('Double','doubleValue'),'S':('Short','shortValue'),'B':('Byte','byteValue'),'C':('Character','charValue'),'Z':('Boolean','booleanValue')}[desc]
 return '((java.lang.'+box+')'+arg+').'+method+'()'
aliases=[line.split('\t') for line in (BASE/'identifier-aliases.tsv').read_text().splitlines()]
classes={r[1]:r[2] for r in aliases if r[0]=='C'};method_names={(r[1],r[2],r[3]):r[4] for r in aliases if r[0]=='M'}
def mapped_descriptor(value):return re.sub(r'L([^;]+);',lambda m:'L'+classes.get(m[1],m[1])+';',value)
access={}
for line in (BASE/'original-abi.tsv').read_text().splitlines():
 r=line.split('\t')
 if r[0]=='M':access[(classes.get(r[1],r[1]),method_names.get((r[1],r[2],r[3]),r[2]),mapped_descriptor(r[3]))]=int(r[4])
rows=[line.split('\t') for line in (BASE/'lambda-sites.tsv').read_text().splitlines()];by_file={};ledger=[]
for index,row in enumerate(rows):by_file.setdefault(JAVA/(row[0]+'.java'),[]).append((index,row))
for path,sites in by_file.items():
 if not path.exists():
  ledger += [{'site':i,'status':'UNRESTORED','reason':'owner source is nested or absent'} for i,_ in sites];continue
 original=path.read_text();text=original;replacements=[]
 for index,row in sites:
  owner,enclosing,encdesc,offset,target,name,impldesc,tag,sam,instantiated,interface,captures=row
  site={'site':index,'owner':owner,'method':enclosing,'target':target+'.'+name,'status':'UNRESTORED'}
  if tag!='6':site['reason']='bound/interface/special receiver needs separate reconstruction';ledger.append(site);continue
  if 'opcode' in captures or '+' in captures:site['reason']='non-local capture producer';ledger.append(site);continue
  ref=target.rsplit('/',1)[-1]+'::'+name;position=text.find(ref)
  if position<0:site['reason']='reference expression not uniquely located';ledger.append(site);continue
  if text.find(ref,position+len(ref))>=0:site['reason']='multiple source references require method-scoped matching';ledger.append(site);continue
  captured=[part.split('=',1) for part in captures.split(',') if part]
  line_start=text.rfind('\n',0,position)+1;line_end=text.find('\n',position);line=text[line_start:line_end if line_end>=0 else len(text)]
  if captured:
   prefix=text[line_start:position].strip()
   # Do not inject declarations inside an argument list/conditional expression.
   valid=bool(re.fullmatch(r'(?:[\w$\.<>\[\]]+ )?var\d+ = ',prefix+' ')) or bool(re.fullmatch(r'var\d+\.updateScope\(',prefix)) or prefix=='return'
   if not valid or not line.rstrip().endswith(';'):site['reason']='multiline/non-statement capture site requires expression recovery';ledger.append(site);continue
  is_static=bool(access[(owner,enclosing,encdesc)]&8)
  declarations=[];arguments=[]
  for j,(desc,producer) in enumerate(captured):
   if not re.fullmatch(r'var\d+',producer):raise RuntimeError('Unreviewed capture '+producer)
   variable='this' if producer=='var0' and not is_static else producer
   identifier=f'recoveredCapture_{index}_{j}';declarations.append('final '+jtype(desc)+' '+identifier+' = '+variable+';');arguments.append(identifier)
  tail=params(impldesc)[len(captured):];samargs=[f'recoveredArgument_{index}_{j}' for j in range(len(params(sam)))]
  if len(tail)!=len(samargs):site['reason']='SAM adaptation arity mismatch';ledger.append(site);continue
  arguments += [adapt(desc,arg) for desc,arg in zip(tail,samargs)]
  expression='(('+interface.replace('/','.')+') ('+', '.join(samargs)+') -> '+target.rsplit('/',1)[-1]+'.'+name+'('+', '.join(arguments)+'))'
  replacements.append((position,position+len(ref),expression))
  if declarations:
   indent=re.match(r' *',text[line_start:])[0];replacements.append((line_start,line_start,''.join(indent+s+'\n' for s in declarations)))
  site.update(status='SOURCE_RESTORED_NOT_COMPILED',captures=len(captured),sam_arguments=len(tail));ledger.append(site)
 for start,end,value in sorted(replacements,reverse=True):text=text[:start]+value+text[end:]
 if text!=original:path.write_text(text)
(BASE/'lambda-recovery.json').write_text(json.dumps(ledger,indent=2)+'\n')
print('Lambda sites restored',sum(r['status']=='SOURCE_RESTORED_NOT_COMPILED' for r in ledger),'remaining',sum(r['status']=='UNRESTORED' for r in ledger))
