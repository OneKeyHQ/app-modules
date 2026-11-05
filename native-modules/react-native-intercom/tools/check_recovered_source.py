#!/usr/bin/env python3
"""Reject known decompiler failure placeholders without rejecting legitimate SDK errors."""
import argparse,json,pathlib,re,sys
FAILURE=re.compile(r'(?i)(?:\$VF:\s*Couldn.t be decompiled|JADX ERROR:|Method not decompiled:|<unrepresentable>|Decompilation failed|An error occurred while decompiling this method|Could not decompile|Unable to decompile)')
DECLARATION=re.compile(r'^\s*(?:public|protected|private|static)\b[^;=]*?\b([A-Za-z_$][\w$]*)\s*\(')
def scan(paths):
 findings=[]
 for source in paths:
  hint=None;header=[];header_line=None
  for number,line in enumerate(source.read_text().splitlines(),1):
   match=DECLARATION.search(line)
   if match:hint={'name':match[1],'declaration_line':number,'declaration':line.strip()}
   elif re.match(r'^\s*static\s*\{',line):hint={'name':'<clinit>','declaration_line':number,'declaration':line.strip()}
   if FAILURE.search(line):findings.append({'source':str(source),'line':number,'method':hint,'marker':line.strip()[:500]})
 return findings
if __name__=='__main__':
 parser=argparse.ArgumentParser(description=__doc__);parser.add_argument('--report',required=True,type=pathlib.Path);parser.add_argument('sources',nargs='+',type=pathlib.Path);args=parser.parse_args()
 sources=sorted(set(p for root in args.sources for p in (root.rglob('*.java') if root.is_dir() else [root])))
 findings=scan(sources);args.report.parent.mkdir(parents=True,exist_ok=True);args.report.write_text(json.dumps({'status':'FAIL' if findings else 'PASS','source_files':len(sources),'findings':findings},indent=2)+'\n')
 print('Known decompiler failure markers:',len(findings));sys.exit(1 if findings else 0)
