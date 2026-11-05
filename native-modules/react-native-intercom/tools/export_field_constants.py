#!/usr/bin/env python3
"""Read JVM field metadata only; preserve ConstantValue presence and exact bits."""
import argparse,json,struct
from pathlib import Path
def mutf8(raw):
    units=[];i=0
    while i<len(raw):
        a=raw[i];i+=1
        if 1<=a<=127: units.append(a)
        elif a&224==192:
            assert i<len(raw) and raw[i]&192==128
            b=raw[i];i+=1;v=((a&31)<<6)|(b&63)
            assert v>=128 or (a,b)==(192,128)
            units.append(v)
        elif a&240==224:
            assert i+1<len(raw) and raw[i]&192==128 and raw[i+1]&192==128
            v=((a&15)<<12)|((raw[i]&63)<<6)|(raw[i+1]&63);i+=2;assert v>=2048
            units.append(v)
        else: raise ValueError('Invalid modified UTF8')
    return b''.join(struct.pack('>H',u) for u in units).decode('utf-16-be','surrogatepass'), units
class Reader:
    def __init__(self,b):self.b=b;self.o=0
    def take(self,n):v=self.b[self.o:self.o+n];assert len(v)==n;self.o+=n;return v
    def u1(self):return self.take(1)[0]
    def u2(self):return struct.unpack('>H',self.take(2))[0]
    def u4(self):return struct.unpack('>I',self.take(4))[0]
def fields(data):
    r=Reader(data);assert r.u4()==0xcafebabe;r.take(4);cp=[None]*r.u2();i=1
    while i<len(cp):
        tag=r.u1()
        if tag==1:
            raw=r.take(r.u2());value,units=mutf8(raw);v={'text':value,'utf16_units':units,'mutf8_hex':raw.hex()}
        elif tag in(3,4):v=r.take(4)
        elif tag in(5,6):v=r.take(8)
        elif tag in(7,8,16,19,20):v=r.u2()
        elif tag in(9,10,11,12,17,18):v=(r.u2(),r.u2())
        elif tag==15:v=(r.u1(),r.u2())
        else:raise ValueError(tag)
        cp[i]=(tag,v);i+=2 if tag in(5,6)else 1
    utf=lambda n:cp[n][1]['text']
    r.u2();owner=utf(cp[r.u2()][1]);r.u2();r.take(r.u2()*2);result=[]
    for _ in range(r.u2()):
        flags=r.u2();name=utf(r.u2());desc=utf(r.u2());constant=None
        for _ in range(r.u2()):
            attr=utf(r.u2());payload=r.take(r.u4())
            if attr!='ConstantValue':continue
            assert constant is None and len(payload)==2
            tag,v=cp[struct.unpack('>H',payload)[0]]
            if tag==8:
                assert desc=='Ljava/lang/String;';string=cp[v][1]
                constant={'cp_tag':8,'kind':'String','value':string['text'],'utf16_units':string['utf16_units'],'mutf8_hex':string['mutf8_hex']}
            else:
                fmt={3:'i',4:'f',5:'q',6:'d'}[tag]
                assert (tag==3 and desc in('B','C','I','S','Z')) or {4:'F',5:'J',6:'D'}.get(tag)==desc
                constant={'cp_tag':tag,'kind':{3:'Integer',4:'Float',5:'Long',6:'Double'}[tag],'bits_hex':v.hex(),'value_decimal':str(struct.unpack('>'+fmt,v)[0])}
        result.append({'owner':owner,'name':name,'descriptor':desc,'access_flags':flags,'constant_value':constant})
    return owner,result

if __name__ == '__main__':
    parser=argparse.ArgumentParser(description=__doc__)
    parser.add_argument('classes', type=Path)
    parser.add_argument('output', type=Path)
    args=parser.parse_args()
    records=[]; owners=[]
    for path in sorted(args.classes.rglob('*.class')):
        owner, rows=fields(path.read_bytes()); owners.append(owner); records.extend(rows)
    args.output.write_text(json.dumps({'schema':'jvm-field-constant-value-v1','class_count':len(owners),'owners':owners,'fields':records},ensure_ascii=True,indent=2)+'\n')
    print('Class metadata read='+str(len(owners))+'; fields='+str(len(records))+'; method Code is not parsed')
