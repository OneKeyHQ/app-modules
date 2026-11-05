#!/usr/bin/env python3
"""Apply only AST/descriptor/group/producer-proved functional local casts from the current compile baseline."""
import argparse,hashlib,json,pathlib,re,shutil
ROOT=pathlib.Path(__file__).resolve().parent.parent
STAGE=ROOT/'.recovery/remember-functional-casts';BASE=ROOT/'source/android/intercom-sdk-base'
def sha(path):return hashlib.sha256(path.read_bytes()).hexdigest()
def main():
 global STAGE
 parser=argparse.ArgumentParser(description=__doc__);parser.add_argument('--stage',type=pathlib.Path);parser.add_argument('--checked-copies',action='store_true');args=parser.parse_args()
 if args.stage:STAGE=args.stage.resolve()
 baseline=ROOT/'.recovery/android-base/receipt.json';receipt=json.loads(baseline.read_text());expected={row['path']:row['sha256']for row in receipt['source_hashes']}
 files={};methods=[]
 for line in(STAGE/'source-edits.tsv').read_text().splitlines():
  row=line.split('\t');path=pathlib.Path(row[0]);relative=path.relative_to(ROOT).as_posix()
  if sha(path)!=expected[relative]:raise RuntimeError('Source differs from the compiler baseline: '+relative)
  start,end=map(int,row[1:3]);files.setdefault(path,[]).append((start,end,row[3]));site=dict(file=relative,start=start,end=end,replacement=row[3],owner=row[4],method=row[5],descriptor=row[6],status='SOURCE_CAST_RESTORED_NOT_COMPILED')
  if args.checked_copies:site.update(function_type=row[7],source_line=int(row[8]),checked_value_proofs=row[9].split(';'))
  else:site.update(composer_group_key=int(row[7]),function_type=row[8],original_checkcast_bci=int(row[9]),original_store_slot=int(row[10]),source_line=int(row[11]))
  methods.append(site)
 changes=[]
 for path,edits in files.items():
  original=path.read_text();assert not any(ord(character)>65535 for character in original);text=original;previous=len(text)+1
  for start,end,replacement in sorted(edits,reverse=True):
   assert end<=previous and re.fullmatch(r'[\w$]+',text[start:end]);text=text[:start]+replacement+text[end:];previous=start
  backup=STAGE/'before'/path.relative_to(BASE/'java');backup.parent.mkdir(parents=True,exist_ok=True)
  if not backup.exists():shutil.copy2(path,backup)
  path.write_text(text);changes.append(dict(file=path.relative_to(ROOT).as_posix(),before_sha256=hashlib.sha256(original.encode()).hexdigest(),after_sha256=sha(path)))
 inventory=ROOT/'.recovery/remember-functional-casts/original-joins.tsv' if args.checked_copies else STAGE/'original-joins.tsv'
 scope='Only current-diagnostic typed local joins with exact original Composer group/CHECKCAST/store/end proof and compatible reaching producers; Object slots and invocation arguments preserved'
 if args.checked_copies:scope='Only current-diagnostic typed-local RHS copies of all-path-proved checked/parameter/subtype FunctionN values or null; no Object declaration narrowing, argument overload changes or earlier source CHECKCAST movement'
 report=dict(status='SOURCE_CASTS_NOT_COMPILED',scope=scope,baseline_compile_log_sha256=sha(ROOT/'.recovery/android-base/compile.log'),baseline_receipt_sha256=sha(baseline),source_edits=len(methods),changed_files=changes,sites=methods,original_join_inventory_sha256=sha(inventory),reference_offset_coordinate='Actual normalized-reference offsets, not universally raw-vendor coordinates',tool_sha256=sha(ROOT/'tools/RememberFunctionalCasts.java'),rejected=(STAGE/'source-edits.tsv.rejected.tsv').read_text().splitlines())
 (BASE/('checked-functional-copy-recovery.json' if args.checked_copies else 'remember-functional-recovery.json')).write_text(json.dumps(report,indent=2)+'\n');print('Applied proved source casts',len(methods),'files',len(changes))
if __name__=='__main__':main()
