#!/usr/bin/env python3
"""Repair reviewed snapshot names using bytecode-site, SSA capture, and lexical-scope evidence."""
import collections,hashlib,json,pathlib,re,subprocess
ROOT=pathlib.Path(__file__).resolve().parent.parent
BASE=ROOT/'source/android/intercom-sdk-base';STAGE=ROOT/'.recovery/android-base-mapping'
rows=[line.split('\t') for line in (STAGE/'lambda-sites-with-bci.tsv').read_text().splitlines()]
assert [row[:12] for row in rows]==[line.split('\t') for line in (BASE/'lambda-sites.tsv').read_text().splitlines()]
mappings=collections.defaultdict(list)
for line in (STAGE/'capture-map.tsv').read_text().splitlines():
 row=line.split('\t');mappings[tuple(row[1:7])].append(row)
restored={item['site'] for item in json.loads((BASE/'lambda-recovery.json').read_text()) if item['status']=='SOURCE_RESTORED_NOT_COMPILED'}
candidates=[];requests=[]
for index,row in enumerate(rows):
 matches=[item for item in mappings[tuple(row[:3]+row[4:7])] if int(row[12]) in {int(value) for value in item[7].strip('{}').split(', ') if value}]
 assert len(matches)==2 and {item[0] for item in matches}=={'AT_END','LATE_PROCESSING'} and matches[0][8:]==matches[1][8:]
 captures=[value.split('=') for value in row[11].split(',') if value];parameters=[value.split('|') for value in matches[0][8:]]
 assert len(captures)==len(parameters)
 if index not in restored:continue
 assert row[7]=='6' and all(part[0]=='LOCAL' and part[1]==producer[3:] and part[-1]==descriptor for (descriptor,producer),part in zip(captures,parameters))
 for capture,part in enumerate(parameters):
  file=BASE/'java'/(row[0]+'.java');identifier=f'recoveredCapture_{index}_{capture}'
  requests.append('\t'.join((str(file),identifier,part[4])))
  candidates.append(dict(site=index,owner=row[0],method=row[1],enclosing_descriptor=row[2],target=row[4:7],bci=int(row[12]),capture=capture,original_slot=part[1],ssa_index=part[2],ssa_version=part[3],final_name=part[4],descriptor=part[5],identifier=identifier,file=str(file)))
(STAGE/'capture-name-candidates.tsv').write_text('\n'.join(requests)+'\n')
commands=[['/opt/homebrew/opt/openjdk@17/bin/javac','-d',str(STAGE/'tools'),str(ROOT/'tools/SourceCaptureBindings.java')],['/opt/homebrew/opt/openjdk@17/bin/java','-cp',str(STAGE/'tools'),'SourceCaptureBindings',str(STAGE/'capture-name-candidates.tsv'),str(STAGE/'lexical-bindings.tsv')]]
for command in commands:subprocess.run(command,check=True)
bindings={}
for line in (STAGE/'lexical-bindings.tsv').read_text().splitlines():
 item=line.split('\t');key=(item[0],item[1]);assert key not in bindings;bindings[key]=item
assert len(bindings)==len(candidates)
files={};ledger=[]
for item in candidates:
 file=pathlib.Path(item['file']);text=files.get(file,file.read_text());binding=bindings[(str(file),item['identifier'])];assert binding[2]==item['final_name'] and binding[3] in ('LOCAL','THIS')
 expression=re.compile(r'(\b'+re.escape(item['identifier'])+r'\s*=\s*)(this|var\d+)(;)')
 matches=list(expression.finditer(text));assert len(matches)==1,(file,item['identifier'])
 match=matches[0];item['previous_name']=match[2];item['lexical_binding']=binding[4];item['declaration_line']=int(binding[5]);item['status']='SOURCE_NAME_VERIFIED_NOT_COMPILED'
 if match[2]!=item['final_name']:text=text[:match.start(2)]+item['final_name']+text[match.end(2):]
 files[file]=text;ledger.append(item)
changed=[]
for file,text in files.items():
 original=file.read_text()
 if original!=text:
  file.write_text(text);changed.append(dict(file=str(file.relative_to(ROOT)),before_sha256=hashlib.sha256(original.encode()).hexdigest(),after_sha256=hashlib.sha256(text.encode()).hexdigest()))
report=dict(status='SOURCE_NAMES_VERIFIED_NOT_COMPILED',joined_sites=len(rows),all_phase_pairs_equal=True,capture_bindings=len(ledger),changed_captures=sum(item['previous_name']!=item['final_name'] for item in ledger),changed_files=changed,captures=ledger,commands=commands)
(BASE/'lambda-name-recovery.json').write_text(json.dumps(report,indent=2)+'\n')
print('Verified snapshots',len(ledger),'changed',report['changed_captures'],'files',len(changed))
