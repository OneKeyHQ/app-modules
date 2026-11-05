#!/usr/bin/env python3
"""Apply one diagnostic no-late-merge source result after exact original-site/SSA/lexical capture checks."""
import argparse,collections,hashlib,json,pathlib,re,shutil,subprocess
ROOT=pathlib.Path(__file__).resolve().parent.parent
BASE=ROOT/'source/android/intercom-sdk-base';STAGE=ROOT/'.recovery/android-conversation-no-late-merge'
OWNER='io/intercom/android/sdk/m5/conversation/ui/ConversationScreenKt'
owned={path.relative_to(BASE/'java').as_posix()[:-5]for path in(BASE/'java').rglob('*.java')}
def jtype(descriptor):
 if descriptor.startswith('['):return jtype(descriptor[1:])+'[]'
 if descriptor.startswith('L'):
  name=descriptor[1:-1];return name.replace('/','.')if name in owned else name.replace('/','.').replace('$','.')
 return dict(V='void',Z='boolean',B='byte',C='char',S='short',I='int',J='long',F='float',D='double')[descriptor]
def parameters(descriptor):
 text=descriptor[1:descriptor.index(')')];out=[]
 while text:
  dimensions=0
  while text[dimensions]=='[':dimensions+=1
  end=text.index(';',dimensions)+1 if text[dimensions]=='L' else dimensions+1;out.append(text[:end]);text=text[end:]
 return out
def adapt(descriptor,name):
 if descriptor.startswith(('L','[')):return '('+jtype(descriptor)+')'+name
 boxed,method={'I':('Integer','intValue'),'J':('Long','longValue'),'F':('Float','floatValue'),'D':('Double','doubleValue'),'S':('Short','shortValue'),'B':('Byte','byteValue'),'C':('Character','charValue'),'Z':('Boolean','booleanValue')}[descriptor]
 return '((java.lang.'+boxed+')'+name+').'+method+'()'
def main():
 global OWNER,STAGE
 parser=argparse.ArgumentParser(description=__doc__);parser.add_argument('--apply',action='store_true');parser.add_argument('--owner',default=OWNER);parser.add_argument('--stage',type=pathlib.Path);parser.add_argument('--input-source',type=pathlib.Path);parser.add_argument('--capture-map',type=pathlib.Path);parser.add_argument('--decompile-command',type=pathlib.Path);args=parser.parse_args()
 OWNER=args.owner
 if args.stage:STAGE=args.stage.resolve()
 STAGE.mkdir(parents=True,exist_ok=True)
 raw=args.input_source or STAGE/'scoped-source'/(OWNER+'.java');text=raw.read_text();original=text
 # Keep only existing explicit NEW/cast types and literal JVM boolean representations.
 text=re.sub(r'\bvar (var\d+) = new ([A-Za-z_$][\w$\.]*)(?=[(<])',lambda match:match[2]+' '+match[1]+' = new '+match[2],text)
 text=re.sub(r'\bvar (var\d+) = \(([A-Za-z_$][\w$\.\[\]]*)\)',lambda match:match[2]+' '+match[1]+' = ('+match[2]+')',text)
 text=re.sub(r'\(boolean\)\s*([01])\b',lambda match:'true'if match[1]=='1'else'false',text)
 maps=collections.defaultdict(list)
 allrows=[line.split('\t')for line in(ROOT/'.recovery/android-base-mapping/lambda-sites-with-bci.tsv').read_text().splitlines()]
 map_path=args.capture_map or STAGE/'scoped-capture-map.tsv'
 if not map_path.exists():
  assert not any(row[0]==OWNER for row in allrows), 'Capture map missing for owner with original lambda sites'
  map_path.write_text('')
 for line in map_path.read_text().splitlines():
  row=line.split('\t');maps[tuple(row[1:7])].append(row)
 replacements=[];ledger=[];requests=[]
 repaired=STAGE/'repaired'/(OWNER+'.java');repaired.parent.mkdir(parents=True,exist_ok=True)
 for index,row in enumerate(allrows):
  if row[0]!=OWNER:continue
  matches=[mapped for mapped in maps[tuple(row[:3]+row[4:7])]if int(row[12])in{int(value)for value in mapped[7].strip('{}').split(', ')if value}]
  assert len(matches)==2 and {match[0]for match in matches}=={'AT_END','LATE_PROCESSING'}and matches[0][8:]==matches[1][8:]
  captured=[value.split('=')for value in row[11].split(',')if value];ssa=[value.split('|')for value in matches[0][8:]]
  assert row[7]=='6'and len(captured)==len(ssa)
  for (descriptor,producer),variable in zip(captured,ssa):assert variable[0]=='LOCAL'and producer=='var'+variable[1]and descriptor==variable[-1]
  reference=row[4].rsplit('/',1)[-1]+'::'+row[5];assert text.count(reference)==1
  position=text.index(reference);start=text.rfind('\n',0,position)+1;end=text.find('\n',position);line=text[start:end];prefix=text[start:position].strip()
  if captured:
   safe=bool(re.fullmatch(r'(?:[\w$\.<>\[\]]+ )?[\w$]+ = ',prefix+' '))or bool(re.fullmatch(r'[\w$]+\.updateScope\(',prefix))or prefix=='return'
   # A first invocation argument has no earlier operand expression to evaluate.
   # Captures here are already restricted to direct local loads, so snapshots
   # retain factory-time values without moving any field read or invocation.
   safe = safe or bool(re.fullmatch(r'(?:[\w$\.<>\[\]]+ [\w$]+ = )?[\w$]+\.[\w$]+\(',prefix))
   assert safe and line.rstrip().endswith(';'),(index,prefix)
  declarations=[];arguments=[];details=[]
  for slot,((descriptor,producer),variable)in enumerate(zip(captured,ssa)):
   name=f'recoveredCapture_{index}_{slot}';declarations.append('final '+jtype(descriptor)+' '+name+' = '+variable[4]+';');arguments.append(name);requests.append('\t'.join((str(repaired),name,variable[4])));details.append(dict(identifier=name,original_slot=variable[1],ssa_index=variable[2],ssa_version=variable[3],final_name=variable[4],descriptor=descriptor))
  tail=parameters(row[6])[len(captured):];samnames=[f'recoveredArgument_{index}_{slot}'for slot in range(len(parameters(row[8])))];assert len(tail)==len(samnames)
  arguments += [adapt(descriptor,name)for descriptor,name in zip(tail,samnames)]
  expression='(('+row[10].replace('/','.')+') ('+', '.join(samnames)+') -> '+row[4].rsplit('/',1)[-1]+'.'+row[5]+'('+', '.join(arguments)+'))'
  replacements.append((position,position+len(reference),expression))
  if declarations:
   indent=re.match(r' *',text[start:])[0];replacements.append((start,start,''.join(indent+declaration+'\n'for declaration in declarations)))
  ledger.append(dict(site=index,enclosing=row[1],enclosing_descriptor=row[2],target=row[4:7],bci=int(row[12]),captures=details,status='SOURCE_RESTORED_NOT_COMPILED'))
 assert len(ledger)==sum(row[0]==OWNER for row in allrows)
 for start,end,replacement in sorted(replacements,reverse=True):text=text[:start]+replacement+text[end:]
 repaired.write_text(text);(STAGE/'capture-requests.tsv').write_text('\n'.join(requests)+'\n');tools=STAGE/'binding-tools';tools.mkdir(exist_ok=True)
 if requests:
  subprocess.run(['/opt/homebrew/opt/openjdk@17/bin/javac','-d',str(tools),str(ROOT/'tools/SourceCaptureBindings.java')],check=True)
  subprocess.run(['/opt/homebrew/opt/openjdk@17/bin/java','-cp',str(tools),'SourceCaptureBindings',str(STAGE/'capture-requests.tsv'),str(STAGE/'lexical-bindings.tsv')],check=True)
  bindings=[line.split('\t')for line in(STAGE/'lexical-bindings.tsv').read_text().splitlines()];assert len(bindings)==len(requests)and all(binding[3]in('LOCAL','THIS')for binding in bindings)
 else:
  bindings=[]
  (STAGE/'lexical-bindings.tsv').write_text('')
 product=BASE/'java'/(OWNER+'.java');receipt=dict(status='CAPTURES_VERIFIED_SOURCE_NOT_COMPILED',scope='Only '+OWNER+' source selected from class-scoped no-late-merge diagnostic; child source outputs untouched',source_before_sha256=hashlib.sha256(product.read_bytes()).hexdigest(),raw_decompile_sha256=hashlib.sha256(original.encode()).hexdigest(),source_after_sha256=hashlib.sha256(text.encode()).hexdigest(),source_applied=args.apply,sites=ledger,capture_bindings=len(bindings),normal_source_build_uses_decompiler=False,command=str(args.decompile_command or STAGE/'scoped-command.json'))
 if args.apply:
  backup=STAGE/'before'/(OWNER.rsplit('/',1)[-1]+'.java');backup.parent.mkdir(exist_ok=True)
  if not backup.exists():shutil.copy2(product,backup)
  product.write_text(text)
 (BASE/(('conversation-no-late-merge-recovery' if OWNER.endswith('/ConversationScreenKt') else OWNER.rsplit('/',1)[-1]+'-no-late-merge-recovery')+'.json')).write_text(json.dumps(receipt,indent=2)+'\n');print('Verified sites',len(ledger),'captures',len(bindings),'applied',args.apply)
if __name__=='__main__':main()
