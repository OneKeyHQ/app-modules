#!/usr/bin/env python3
"""Encode reviewed Kotlin package mapping text; no class or instruction payloads."""
import hashlib,json,pathlib,struct,sys

def varint(number):
 if number<0:raise ValueError('Negative protobuf length/tag')
 result=bytearray()
 while number>=128:result.append((number&127)|128);number>>=7
 result.append(number);return bytes(result)
def field(number,data):return varint((number<<3)|2)+varint(len(data))+data

def encode(mapping):
 unsupported=['metadata_parts','jvm_package_names','annotations','optional_annotation_classes','unknown_fields']
 if any(mapping.get(name) for name in unsupported):raise ValueError('Unrestored module metadata fields')
 if mapping['string_table_hex'] or mapping['qualified_name_table_hex']:raise ValueError('Unrestored module name tables')
 data=b''
 for package in mapping['package_parts']:
  if any(v for k,v in package.items() if k not in ['package_fq_name','short_class_names']):raise ValueError('Unrestored package mapping fields')
  parts=field(1,package['package_fq_name'].encode())
  for name in package['short_class_names']:parts+=field(2,name.encode())
  data+=field(1,parts)
 # The original mapping explicitly contains empty StringTable and QualifiedNameTable messages.
 data+=field(4,b'')+field(5,b'')
 values=[len(mapping['version']),*mapping['version'],mapping['flags']]
 return b''.join(struct.pack('>i',value) for value in values)+data
if __name__=='__main__':
 mapping=json.loads(pathlib.Path(sys.argv[1]).read_text());data=encode(mapping)
 actual=hashlib.sha256(data).hexdigest()
 if actual!=mapping['original_sha256']:raise ValueError('Reviewed text does not reproduce original package mapping SHA: '+actual)
 target=pathlib.Path(sys.argv[2])/mapping['entry'];target.parent.mkdir(parents=True,exist_ok=True);target.write_bytes(data)
 print(json.dumps({'entry':mapping['entry'],'sha256':actual,'bytes':len(data),'packages':len(mapping['package_parts']),'facades':sum(len(p['short_class_names']) for p in mapping['package_parts'])}))
