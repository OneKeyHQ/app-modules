#!/usr/bin/env python3
"""Restore property metadata that current clang marks dynamic; never change compiled instructions."""
from pathlib import Path
import struct,json,hashlib,argparse,re,subprocess
ROOT=Path(__file__).resolve().parent.parent

def sections(data):
 assert struct.unpack_from('<I',data)[0]==0xfeedfacf
 offset=32
 for _ in range(struct.unpack_from('<I',data,16)[0]):
  command,size=struct.unpack_from('<II',data,offset)
  if command==0x19:
   count=struct.unpack_from('<I',data,offset+64)[0]
   for i in range(count):
    at=offset+72+80*i
    name=data[at:at+16].split(b'\0')[0].decode();segment=data[at+16:at+32].split(b'\0')[0].decode()
    length=struct.unpack_from('<Q',data,at+40)[0];fileoff=struct.unpack_from('<I',data,at+48)[0]
    yield segment,name,fileoff,length
  offset+=size

def property_plan(path, original, allowed, preserve):
 """Protect real dynamic properties by owner, not by a shared attribute string."""
 ranges=[];defined=set();symbol_table=None;offset=32
 for _ in range(struct.unpack_from('<I',original,16)[0]):
  command,size=struct.unpack_from('<II',original,offset)
  if command==0x19:
   for i in range(struct.unpack_from('<I',original,offset+64)[0]):
    at=offset+72+80*i
    name=original[at:at+16].split(b'\0')[0].decode()
    address,length,fileoff=struct.unpack_from('<QQI',original,at+32)
    ranges.append((name,address,length,fileoff))
  elif command==2:symbol_table=struct.unpack_from('<IIII',original,offset+8)
  offset+=size
 if symbol_table:
  symoff,count,stroff,_=symbol_table
  for i in range(count):
   index,kind,section,_,_=struct.unpack_from('<IBBHQ',original,symoff+16*i)
   if kind&0xe0 or kind&0x0e!=0x0e or not section:continue
   end=original.find(b'\0',stroff+index)
   name=original[stroff+index:end].decode()
   for prefix,owner_kind in [('_OBJC_CLASS_$_','class'),('__OBJC_PROTOCOL_$_','protocol')]:
    if name.startswith(prefix):defined.add((owner_kind,name[len(prefix):]))
 required={key:value for key,value in preserve.items() if key[:2] in defined}
 if not required:return {},[]

 def string_offset(address, rendered):
  for name,start,length,fileoff in ranges:
   if name in ['__cstring','__objc_methname'] and start<=address<start+length:
    at=fileoff+address-start;end=original.find(b'\0',at,fileoff+length)
    if end<0 or original[at:end]!=rendered.encode():
     raise RuntimeError(f'Property string mismatch at VM address {address:#x}: {rendered}')
    return at
  raise RuntimeError(f'Property string outside supported string sections: {address:#x}')

 output=subprocess.check_output(['xcrun','otool','-ov',str(path)],text=True,errors='replace')
 lines=output.splitlines();references={};seen=set();preserved=[]
 for index,line in enumerate(lines):
  match=re.match(r'\s*(baseProperties|instanceProperties)\s+0x[0-9a-f]+\s+_+OBJC_\$_PROP_LIST_(\S+)\s*$',line)
  if not match:continue
  owner_kind='class' if match[1]=='baseProperties' else 'protocol';owner=match[2]
  if index+2>=len(lines) or not re.fullmatch(r'\s*entsize\s+16\s*',lines[index+1]):
   raise RuntimeError(f'Unsupported property list for {owner_kind} {owner}')
  count_match=re.fullmatch(r'\s*count\s+(\d+)\s*',lines[index+2])
  if not count_match:raise RuntimeError(f'Missing property count for {owner_kind} {owner}')
  for entry in range(int(count_match[1])):
   at=index+3+entry*2
   if at+1>=len(lines):raise RuntimeError(f'Truncated properties for {owner_kind} {owner}')
   name_match=re.fullmatch(r'\s*name\s+0x([0-9a-f]+)(?:\s+\S+)?\s+(\S+)\s*',lines[at])
   attr_match=re.fullmatch(r'\s*attributes\s+0x([0-9a-f]+)(?:\s+\S+)?\s+(T.*)',lines[at+1])
   if not name_match or not attr_match:raise RuntimeError(f'Unrecognized property entry for {owner_kind} {owner}: {lines[at:at+2]}')
   name=name_match[2];raw=attr_match[2]
   string_offset(int(name_match[1],16),name)
   fileoff=string_offset(int(attr_match[1],16),raw)
   key=(owner_kind,owner,name)
   if key in required:
    if raw!=required[key]:raise RuntimeError(f'Protected property mismatch for {owner_kind} {owner}.{name}: expected {required[key]}, found {raw}')
    if key not in seen:preserved.append({'kind':owner_kind,'owner':owner,'name':name,'attributes':raw,'offset':fileoff})
    seen.add(key);expected=raw
   else:
    stripped=','.join(part for part in raw.split(',') if part not in ['D','?'])
    expected=stripped if stripped in allowed else raw
   reference={'kind':owner_kind,'owner':owner,'name':name,'before':raw,'expected':expected}
   if reference not in references.setdefault(fileoff,[]):references[fileoff].append(reference)
 missing=set(required)-seen
 if missing:raise RuntimeError(f'Missing protected properties in {path}: {sorted(missing)}')
 planned={}
 for fileoff,items in references.items():
  expected={item['expected'] for item in items}
  if len(expected)!=1:raise RuntimeError(f'Conflicting property requirements at file offset {fileoff:#x}: {items}')
  planned[fileoff]={'expected':expected.pop(),'references':items}
 return planned,preserved

def restore(path):
 original=path.read_bytes();data=bytearray(original)
 allowed=set();preserve={}
 for metadata_path in (ROOT/'source/ios/abi').glob('*.json'):
  for item in json.loads(metadata_path.read_text()):
   allowed.update(p['attributes'] for p in item.get('properties',[]))
   for prop in item.get('preserve_properties',[]):
    owner_kind='class' if 'class' in item else 'protocol'
    owner=item.get(owner_kind)
    if not owner:raise RuntimeError(f'Missing preserve-property owner in {metadata_path}')
    key=(owner_kind,owner,prop['name'])
    if key in preserve and preserve[key]!=prop['attributes']:raise RuntimeError(f'Conflicting preserve-property configuration: {key}')
    preserve[key]=prop['attributes']
 planned,preserved=property_plan(path,original,allowed,preserve)
 patches=[];code_before=[];code_after=[]
 for segment,name,offset,length in sections(original):
  if name=='__text':code_before.append(original[offset:offset+length])
  if name not in ['__cstring','__objc_methname']:continue
  end=offset+length;at=offset
  while at<end:
   terminator=original.find(b'\0',at,end)
   if terminator<0:break
   value=original[at:terminator]
   try:text=value.decode('utf-8')
   except UnicodeDecodeError:at=terminator+1;continue
   parts=text.split(',');restored=','.join(p for p in parts if p not in ['D','?'])
   if at in planned:restored=planned[at]['expected']
   if restored!=text and (at in planned or (any(p in ['D','?'] for p in parts) and restored in allowed)):
    encoded=restored.encode();assert len(encoded)<len(value)
    data[at:terminator]=encoded+b'\0'*(len(value)-len(encoded))
    patches.append({'offset':at,'before':text,'after':restored})
   at=terminator+1
 for fileoff,plan in planned.items():
  end=data.find(b'\0',fileoff)
  if end<0 or bytes(data[fileoff:end])!=plan['expected'].encode():
   raise RuntimeError(f'Property metadata alias could not be restored safely at {fileoff:#x}: {plan["references"]}')
 for segment,name,offset,length in sections(data):
  if name=='__text':code_after.append(bytes(data[offset:offset+length]))
 assert code_before==code_after,'Instruction change rejected'
 path.write_bytes(data)
 return {'object':str(path.relative_to(ROOT)),'changes':patches,'preserved_properties':preserved,'instructions_identical':True,'before_sha256':hashlib.sha256(original).hexdigest(),'after_sha256':hashlib.sha256(data).hexdigest()}
if __name__=='__main__':
 parser=argparse.ArgumentParser();parser.add_argument('object');args=parser.parse_args();print(json.dumps(restore(Path(args.object).resolve()),indent=2))
