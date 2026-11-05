#!/usr/bin/env python3
"""Apply explicit source repairs for JVM/Java expression differences in the UI module."""
import hashlib,json,pathlib,re
ROOT=pathlib.Path(__file__).resolve().parent.parent
SOURCE=ROOT/'source/android/intercom-sdk-ui'
JAVA=SOURCE/'java'
records=[]
def digest(t):return hashlib.sha256(t.encode()).hexdigest()
def jtype(desc):
 if desc.startswith('['):return jtype(desc[1:])+'[]'
 if desc.startswith('L'):
  n=desc[1:-1].replace('/','.')
  return n if desc.startswith('Lio/intercom/android/sdk/ui/') else n.replace('$','.')
 return dict(V='void',Z='boolean',B='byte',C='char',S='short',I='int',J='long',F='float',D='double')[desc]
def params(desc):
 content=desc[1:desc.index(')')];out=[]
 while content:
  count=0
  while content[count]=='[':count+=1
  end=content.index(';',count)+1 if content[count]=='L' else count+1
  out.append(content[:end]);content=content[end:]
 return out

def adapt(desc,arg):
 if desc.startswith(('L','[')):return '('+jtype(desc)+')'+arg
 box,method={'I':('Integer','intValue'),'J':('Long','longValue'),'F':('Float','floatValue'),'D':('Double','doubleValue'),'S':('Short','shortValue'),'B':('Byte','byteValue'),'C':('Character','charValue'),'Z':('Boolean','booleanValue')}[desc]
 return '((java.lang.'+box+')'+arg+').'+method+'()'

sites=[]
for row in (SOURCE/'lambda-sites.tsv').read_text().splitlines():
 columns=row.split('\t');sites.append(columns)
by_file={}
for index,row in enumerate(sites):by_file.setdefault(JAVA/(row[0]+'.java'),[]).append((index,row))
for path in sorted(JAVA.rglob('*.java')):
 before=path.read_text();text=before;actions=[]
 # Preserve zero-initialized storage without Java constant folding; the ABI pass restores FINAL.
 if 'public static final int $stable;' in text:
  text=text.replace('public static final int $stable;','public static int $stable;');actions.append('default-zero static $stable: defer FINAL flag to ABI restoration')
 # Object and the recovered StringProvider super constructors have no side effects.
 # Java requires the call before Kotlin parameter checks; parameter checks remain intact.
 text,count=re.subn(r'(\{\n)((?:      Intrinsics\.checkNotNullParameter\([^\n]+\);\n)+)(      super\((?:null)?\);\n)',r'\1\3\2',text)
 if count:actions.append(f'move {count} empty-super calls before preserved null checks')
 # Kotlin default constructor prologues are pure bit-mask selection. Express them as this arguments.
 ctor=re.compile(r'(public '+re.escape(path.stem)+r'\([^{}]+\) \{\n)((?:      if \([^{}]+\) \{\n         var\d+ = [^\n]+;\n      \}\n\n)+)      this\(([^;]+)\);\n   \}')
 def rewrite_ctor(m):
  selections={}
  for choice in re.finditer(r'if \((.+)\) \{\n         (var\d+) = ([^\n]+);',m[2]):selections[choice[2]]='('+choice[1]+' ? '+choice[3]+' : '+choice[2]+')'
  args=[selections.get(v.strip(),v.strip()) for v in m[3].split(',')]
  return m[1]+'      this('+', '.join(args)+');\n   }'
 text,count=ctor.subn(rewrite_ctor,text)
 if count:actions.append(f'express {count} default constructor prologues as pure argument selections')
 if path.name=='PdfDecoder.java':
  text=text.replace('      Continuation var5;','      PdfDecoder$decode$1 var5;')
  actions.append('retain concrete continuation state-machine type')
 if path.name=='PulsatingBoxKt.java':
  text=text.replace('Function2 var22 = var32;', 'Function2 var22 = (Function2)var32;')
  actions.append('restore checked Function2 cast at erased merge')
 if path.name=='PdfDecoderKt.java':
  text=text.replace('switch (var1)', 'switch (PdfDecoderKt$WhenMappings.$EnumSwitchMapping$0[var1.ordinal()])').replace('case FILL:', 'case 1:').replace('case FIT:', 'case 2:')
  actions.append('retain original Kotlin integer switch-map class')
 if path.name=='JumpToBottomKt.java':
  text=text.replace('switch ((ThemeMode)IntercomColorsKt.getCurrentThemeMode().getValue())', 'switch (JumpToBottomKt$WhenMappings.$EnumSwitchMapping$0[((ThemeMode)IntercomColorsKt.getCurrentThemeMode().getValue()).ordinal()])').replace('case DARK:', 'case 1:').replace('case LIGHT:', 'case 2:').replace('case SYSTEM:', 'case 3:')
  actions.append('retain original Kotlin integer switch-map class')
 if path.name=='ThemeMode.java':
  text=text.replace('EnumEntriesKt.enumEntries($VALUES)', 'EnumEntriesKt.enumEntries(ThemeMode.values())')
  actions.append('initialize EnumEntries through source-accessible enum values()')
 replacements=[]
 for index,row in by_file.get(path,[]):
  owner,enclosing,encdesc,offset,target,name,impldesc,tag,sam,instantiated,interface,captures=row
  ref=target.rsplit('/',1)[-1]+'::'+name
  position=text.find(ref)
  if position<0:
   if 'recoveredCapture_'+str(index)+'_' in text:continue
   # Previously repaired capture-free sites have no capture declaration.
   if name+'(' in text and '->' in text:continue
   raise RuntimeError(f'Cannot locate lambda reference {path}:{ref}')
  if tag!='6':raise RuntimeError('Unsupported bound/constructor lambda handle '+str(row))
  captured=[part.split('=',1) for part in captures.split(',') if part]
  declarations=[];arguments=[]
  for j,(desc,producer) in enumerate(captured):
   if not re.fullmatch(r'var\d+',producer):raise RuntimeError('Non-local capture requires review '+producer)
   variable='this' if producer=='var0' and owner.endswith('/PdfDecoder') and enclosing=='decode' else producer
   identifier=f'recoveredCapture_{index}_{j}'
   declarations.append('final '+jtype(desc)+' '+identifier+' = '+variable+';')
   arguments.append(identifier)
  tail=params(impldesc)[len(captured):]
  samargs=[f'recoveredArgument_{index}_{j}' for j in range(len(params(sam)))]
  if len(tail)!=len(samargs):raise RuntimeError('Lambda adaptation arity mismatch '+str(row))
  arguments += [adapt(desc,arg) for desc,arg in zip(tail,samargs)]
  expression='(('+interface.replace('/','.')+') ('+', '.join(samargs)+') -> '+target.rsplit('/',1)[-1]+'.'+name+'('+', '.join(arguments)+'))'
  line_start=text.rfind('\n',0,position)+1
  if captured and path.name=='TransitionsKt.java':line_start=text.rfind('      return ',0,position)
  indent=re.match(r' *',text[line_start:])[0]
  insertion=''.join(indent+declaration+'\n' for declaration in declarations)
  replacements.append((position,position+len(ref),expression))
  if insertion:replacements.append((line_start,line_start,insertion))
  actions.append(f'lambda site {index}: explicit {len(captured)} captures and {len(tail)} SAM adaptations from bootstrap descriptors')
 for start,end,replacement in sorted(replacements,reverse=True):text=text[:start]+replacement+text[end:]
 if text!=before:
  path.write_text(text)
  records.append({'file':str(path.relative_to(ROOT)),'before_sha256':digest(before),'after_sha256':digest(text),'actions':actions})
if records:(SOURCE/'source-repairs.json').write_text(json.dumps(records,indent=2)+'\n')
print('Repaired',len(records),'source files; retained original method bodies and validation')
