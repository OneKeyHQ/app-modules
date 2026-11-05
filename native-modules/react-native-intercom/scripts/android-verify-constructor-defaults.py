#!/usr/bin/env python3
"""Focused semantic check for the independent constructor-default transformation."""
import hashlib,json,pathlib,subprocess
ROOT=pathlib.Path(__file__).resolve().parent.parent
STAGE=ROOT/'.recovery/constructor-default-fixture';STAGE.mkdir(parents=True,exist_ok=True)
fixture='''public class DefaultsFixture {
 static String order;
 final int first, second;
 DefaultsFixture(int var1,int var2){first=var1;second=var2;}
 DefaultsFixture(int var1,int var2,int var3,Object var4){
  if((var3 & 1)!=0){var1=firstDefault();}
  if((var3 & 2)!=0){var2=secondDefault();}
  this(var1,var2);
 }
 static int firstDefault(){order+="A";return 11;}
 static int secondDefault(){order+="B";return 22;}
 public static void main(String[] args){
  for(int mask=0;mask<4;mask++){
   order="";DefaultsFixture value=new DefaultsFixture(3,5,mask,null);
   String expected=((mask&1)!=0?"A":"")+((mask&2)!=0?"B":"");
   if(!order.equals(expected)||value.first!=((mask&1)!=0?11:3)||value.second!=((mask&2)!=0?22:5))throw new AssertionError(mask+":"+order);
  }
  System.out.println("PASS all four masks, default call order, argument values");
 }
}
'''
dependent='''class DependentFixture {
 DependentFixture(int var1,int var2){}
 DependentFixture(int var1,int var2,int var3,Object var4){
  if((var3 & 1)!=0){var1=11;}
  if((var3 & 2)!=0){var2=var1+1;}
  this(var1,var2);
 }
}
'''
nullable='''public class NullableFixture {
 final Boolean first;final Integer second;
 NullableFixture(Boolean var1,Integer var2){first=var1;second=var2;}
 NullableFixture(Boolean var1,Integer var2,int var3,Object var4){
  if((var3 & 1)!=0){var1=true;}
  if((var3 & 2)!=0){var2=7;}
  this(var1,var2);
 }
 public static void main(String[] args){
  for(int mask=0;mask<4;mask++){
   NullableFixture value=new NullableFixture(null,null,mask,null);
   if((mask&1)==0?value.first!=null:!Boolean.TRUE.equals(value.first))throw new AssertionError("Boolean:"+mask);
   if((mask&2)==0?value.second!=null:!Integer.valueOf(7).equals(value.second))throw new AssertionError("Integer:"+mask);
  }
  Integer identity=Integer.valueOf(1000);NullableFixture supplied=new NullableFixture(Boolean.FALSE,identity,0,null);
  if(supplied.first!=Boolean.FALSE||supplied.second!=identity)throw new AssertionError("Supplied wrapper identity");
  System.out.println("PASS nullable Boolean/Integer four masks and supplied identity");
 }
}
'''
(STAGE/'DefaultsFixture.java').write_text(fixture);(STAGE/'DependentFixture.java').write_text(dependent);(STAGE/'NullableFixture.java').write_text(nullable)
java='/opt/homebrew/opt/openjdk@17/bin/java';javac='/opt/homebrew/opt/openjdk@17/bin/javac'
commands=[[javac,'-d',str(STAGE),str(ROOT/'tools/ConstructorDefaults.java')],[java,'-cp',str(STAGE),'ConstructorDefaults',str(STAGE),str(STAGE/'changes.tsv')]]
log=[]
for command in commands:log.append(subprocess.run(command,check=True,capture_output=True,text=True).stdout)
rows=(STAGE/'changes.tsv').read_text().splitlines();assert len(rows)==2
for line in rows:
 row=line.split('\t');path=pathlib.Path(row[0]);assert path.name in ['DefaultsFixture.java','NullableFixture.java']
 text=path.read_text();start,end=map(int,row[1:3]);path.write_text(text[:start]+row[3]+text[end:])
commands += [[javac,'-d',str(STAGE),str(STAGE/'DefaultsFixture.java'),str(STAGE/'NullableFixture.java')],[java,'-cp',str(STAGE),'DefaultsFixture'],[java,'-cp',str(STAGE),'NullableFixture']]
for command in commands[2:]:log.append(subprocess.run(command,check=True,capture_output=True,text=True).stdout)
(STAGE/'test.log').write_text(''.join(log));(STAGE/'receipt.json').write_text(json.dumps(dict(status='PASS',scope='Constructor-default transformation fixture only, not SDK behavior',dependent_defaults_rejected=True,nullable_wrapper_masks_and_identity=True,commands=commands,tool_sha256=hashlib.sha256((ROOT/'tools/ConstructorDefaults.java').read_bytes()).hexdigest()),indent=2)+'\n');print(''.join(log).strip())
