'use strict';
const fs = require('node:fs');
const path = require('node:path');
const crypto = require('node:crypto');
const assert = require('node:assert/strict');
const { spawn } = require('node:child_process');

const base = __dirname;
const moduleRoot = path.resolve(base, '../..');
const consumerRootArgument = process.argv[4];
assert.ok(consumerRootArgument && path.isAbsolute(consumerRootArgument), 'usage: node run.cjs CASE EXPECTED_CANDIDATE_MANIFEST.json ABSOLUTE_CONSUMER_ROOT');
const root = path.resolve(consumerRootArgument);
assert.equal(fs.realpathSync.native(root).toLowerCase(), root.toLowerCase(), 'consumer root resolves elsewhere');
const testCase = process.argv[2] || 'normal';
const fault=/^(kill|hang|suspend)-(pre_handshake|pre_capture|read|unwind|scrub|write)$/.exec(testCase);
assert.ok(fault||['normal','knownchain','scrub','dump','unwind','disabled','startfail','helper-kill-healthy','create-gate'].includes(testCase));
const manifestPath=process.argv[3];
assert.ok(manifestPath,'usage: node run.cjs CASE EXPECTED_CANDIDATE_MANIFEST.json ABSOLUTE_CONSUMER_ROOT');
const expected=JSON.parse(fs.readFileSync(path.resolve(manifestPath),'utf8').replace(/^\uFEFF/,''));
const fixedCandidate=path.join(moduleRoot, '.tmp/p1-phase-a/candidate');
assert.equal(typeof expected.candidateDirectory,'string','candidateDirectory missing from manifest');
assert.ok(path.isAbsolute(expected.candidateDirectory),'candidateDirectory must be absolute');
const candidateDir=path.resolve(expected.candidateDirectory);
assert.equal(candidateDir.toLowerCase(),fixedCandidate.toLowerCase(),'candidateDirectory differs from isolated P1 package');
assert.equal(fs.realpathSync.native(candidateDir).toLowerCase(),candidateDir.toLowerCase(),'candidateDirectory resolves outside isolated P1 package');
const candidateFiles={
  native:['electron-windows-process-hardening.win32-x64-msvc.node','nativeSha256'],
  helper:['onekey-crash-prototype.win32-x64-msvc.exe','helperSha256'],
  capture:['onekey-crashpad-capture.win32-x64.dll','captureSha256'],
  index:['index.cjs','indexSha256'],
  types:['index.d.ts','typesSha256'],
  package:['package.json','packageSha256'],
};
for(const key of ['electronSha256','mainBundleSha256',...Object.values(candidateFiles).map(([,hashKey])=>hashKey)])assert.match(expected[key],/^[0-9a-f]{64}$/i,`candidate manifest missing ${key}`);
const sha = file => crypto.createHash('sha256').update(fs.readFileSync(file)).digest('hex');
const recordBinary = file => fs.existsSync(file) ? { path: file, bytes: fs.statSync(file).size, sha256: sha(file) } : { path: file, missing: true };
const candidateBinaries={};
for(const [name,[filename,hashKey]] of Object.entries(candidateFiles)){
  const file=path.join(candidateDir,filename);
  assert.ok(fs.existsSync(file),`candidate runtime file missing: ${filename}`);
  assert.equal(path.dirname(fs.realpathSync.native(file)).toLowerCase(),candidateDir.toLowerCase(),`candidate runtime file escapes package: ${filename}`);
  const actual=recordBinary(file);
  assert.equal(actual.sha256.toLowerCase(),expected[hashKey].toLowerCase(),`candidate runtime hash mismatch: ${filename}`);
  candidateBinaries[name]=actual;
}
const run = path.join(moduleRoot, '.tmp/p1-phase-a/harness-runs', `${Date.now()}-${testCase}`);
fs.mkdirSync(run, { recursive: true });
const exe = path.join(root, '.tmp/process-hardening-runtime/node_modules/electron/dist/OneKey.exe');
assert.ok(fs.existsSync(exe), `missing official OneKey.exe: ${exe}`);
const mainBundle=path.join(root,'apps/desktop/app/dist/app.js');
assert.ok(fs.existsSync(mainBundle),'production main bundle missing');
assert.equal(sha(mainBundle).toLowerCase(),expected.mainBundleSha256.toLowerCase(),'production main bundle differs from reviewed candidate');
const env = { ...process.env,
  ONEKEY_P1_RUN_DIR: run,
  ONEKEY_P1_CASE: testCase,
  ONEKEY_LOCAL_HANDLER_OUTPUT: run,
  ONEKEY_TEST_CRASH_FAIL_STAGE: ['scrub','dump','unwind'].includes(testCase) ? testCase : '',
  ONEKEY_LOCAL_HANDLER_DISABLED: testCase === 'disabled' ? '1' : '0',
  ONEKEY_LOCAL_HANDLER_START_FAIL: testCase === 'startfail' ? '1' : '0',
  ONEKEY_TEST_HELPER_CREATE_GATE: testCase === 'create-gate' ? '1' : '0',
  ONEKEY_TEST_CRASH_HANG_STAGE: fault ? fault[2] : '',
  ONEKEY_TEST_CRASH_SUSPEND_HELPER: fault&&fault[1]==='suspend' ? '1' : '0',
  ONEKEY_P1_CANDIDATE_DIR: candidateDir,
  ONEKEY_P1_CONSUMER_ROOT: root,
};
delete env.ELECTRON_RUN_AS_NODE;
const summary = { run, testCase, exe, mainBundle:recordBinary(mainBundle), candidateDirectory:candidateDir, candidateBinaries, expectedManifest:path.resolve(manifestPath), expectedHashes:expected, startedUtc: new Date().toISOString(), validation: 'OneKey production main bundle + real renderer + official Electron executable', inventoryStatus:'not_sampled', handleInventories:[] };
const active = new Set();
const sleep = ms => new Promise(resolve => setTimeout(resolve, ms));
const failAfter = (ms,message) => new Promise((_,reject)=>{setTimeout(()=>reject(new Error(message)),ms).unref();});
const read = file => { try { return JSON.parse(fs.readFileSync(file, 'utf8').replace(/^\uFEFF/, '')); } catch { return undefined; } };
const save = () => fs.writeFileSync(path.join(run, 'run.json'), JSON.stringify(summary, null, 2));
function inventory(folder, rootName, relative = '') {
  if (!fs.existsSync(folder)) return [];
  return fs.readdirSync(folder, { withFileTypes:true }).flatMap(item => {
    const full = path.join(folder,item.name), name = path.join(relative,item.name);
    if (item.isDirectory()) return inventory(full,rootName,name);
    const first=Buffer.alloc(4),fd=fs.openSync(full,'r');
    try { fs.readSync(fd,first,0,4,0); } finally { fs.closeSync(fd); }
    return [{root:rootName,name,bytes:fs.statSync(full).size,sha256:sha(full),mdmp:first.toString('ascii')==='MDMP'}];
  });
}
async function until(check, ms, message) {
  const end=Date.now()+ms;
  while(Date.now()<end){const value=check();if(value)return value;await sleep(50);}
  throw new Error(message);
}
function launch(restart) {
  const label=restart?'restart':'crash';
  const out=fs.openSync(path.join(run,`${label}-stdout.txt`),'w');
  const err=fs.openSync(path.join(run,`${label}-stderr.txt`),'w');
  const child=spawn(exe,[path.join(base,'fixture'),...(restart?['--restart']:[])],{env,windowsHide:true,stdio:['ignore',out,err]});
  active.add(child);
  const completion=new Promise((resolve,reject)=>{child.once('error',reject);child.once('exit',(code,signal)=>{const exitedUtc=new Date().toISOString();active.delete(child);fs.closeSync(out);fs.closeSync(err);resolve({pid:child.pid,code,signal,exitedUtc,stdioClosedUtc:new Date().toISOString()});});});
  return {child,completion};
}
async function command(file,args,label) {
  return new Promise((resolve,reject)=>{
    let stdout='',stderr='';
    const child=spawn(file,args,{windowsHide:true,stdio:['ignore','pipe','pipe']});active.add(child);
    child.stdout.on('data',d=>stdout+=d);child.stderr.on('data',d=>stderr+=d);
    child.once('error',reject);child.once('exit',code=>{active.delete(child);fs.writeFileSync(path.join(run,`${label}-stdout.txt`),stdout);fs.writeFileSync(path.join(run,`${label}-stderr.txt`),stderr);resolve({code,stdout,stderr});});
  });
}
async function snapshotSuspension(pid,label){
  const file=path.join(run,`${label}.json`);
  const result=await command('powershell.exe',['-NoProfile','-NonInteractive','-File',path.join(base,'snapshot-helper-threads.ps1'),'-TargetProcessId',String(pid),'-OutputFile',file],label);
  const report=read(file);
  return {code:result.code,stderr:result.stderr,report,confirmed:result.code===0&&report?.confirmed===true&&report.snapshots?.filter(s=>s.allSuspended).length>=2};
}
const processAllowed=new Set(['PROCESS_QUERY_LIMITED_INFORMATION','SYNCHRONIZE','PROCESS_TERMINATE']);
const threadDangerous=new Set(['THREAD_TERMINATE','THREAD_GET_CONTEXT','THREAD_SET_CONTEXT','THREAD_SUSPEND_RESUME','THREAD_SET_INFORMATION','THREAD_IMPERSONATE','THREAD_DIRECT_IMPERSONATION','THREAD_SET_THREAD_TOKEN','WRITE_DAC','WRITE_OWNER']);
const tokenDangerous=new Set(['TOKEN_ADJUST_DEFAULT','WRITE_DAC','WRITE_OWNER','TOKEN_DUPLICATE','TOKEN_ASSIGN_PRIMARY']);
async function probe(pid,label,mode,tid=0) {
  const file=path.join(run,`${label}.json`);
  const args=['-NoProfile','-NonInteractive','-File',path.join(base,'probe-access.ps1'),'-TargetProcessId',String(pid),'-OutputFile',file];
  if(tid)args.push('-ExpectedThreadId',String(tid));
  const result=await command('powershell.exe',args,label);
  assert.equal(result.code,0,`${label}: ${result.stderr}`);
  const p=read(file);assert.equal(p?.callerElevated,false);
  assert.equal(p.targetProcessId,pid);
  assert.ok(p.threads.length>0,`${label}: no threads enumerated`);
  if(mode!=='record')for(const row of p.processRights){const allowed=mode==='initial'?['SYNCHRONIZE','PROCESS_TERMINATE'].includes(row.right):processAllowed.has(row.right);assert.equal(row.success,allowed,`${label}: ${row.right}`);assert.equal(row.win32Error,allowed?0:5,`${label}: ${row.right} error`);}
  if(mode==='initial'){
    assert.equal(p.tokenReachable,false,`${label}: helper token reachable before parent DACL changes`);
    assert.equal(p.tokenOpenProcessError,5,`${label}: QLI denial error`);
    assert.equal(p.tokenRights.length,0,`${label}: token rights listed despite unreachable token`);
    assert.equal(p.tokenDefaultDaclSha256,null,`${label}: token DACL hash exposed without QLI`);
  }else if(mode==='helper'){
    assert.equal(p.tokenReachable,true,`${label}: helper token not reachable via allowed QLI`);
    assert.equal(p.tokenRights.length,6,`${label}: token rights matrix incomplete`);
    for(const row of p.tokenRights){const allowed=row.right==='TOKEN_QUERY';assert.equal(row.success,allowed,`${label}: token ${row.right}`);assert.equal(row.win32Error,allowed?0:5,`${label}: token ${row.right} error`);}
    assert.match(p.tokenDefaultDaclSha256,/^[0-9a-f]{64}$/,`${label}: helper default thread DACL hash missing`);
  }
  if(mode==='initial'||mode==='helper'){
    for(const t of p.threads)for(const row of t.rights){
      if(['THREAD_QUERY_LIMITED_INFORMATION','SYNCHRONIZE'].includes(row.right)){assert.equal(row.success,true,`${label}: thread ${t.threadId} ${row.right} allowed control`);assert.equal(row.win32Error,0);}
      if(threadDangerous.has(row.right)){assert.equal(row.success,false,`${label}: thread ${t.threadId} ${row.right}`);assert.equal(row.win32Error,5,`${label}: thread ${t.threadId} ${row.right} error`);}
    }
  }
  return {file,mode,processRights:p.processRights,threads:p.threads,tokenReachable:p.tokenReachable,tokenOpenProcessError:p.tokenOpenProcessError,tokenRights:p.tokenRights,tokenDefaultDaclSha256:p.tokenDefaultDaclSha256,tokenRisk:p.tokenRights.filter(row=>tokenDangerous.has(row.right)&&row.success).map(row=>row.right)};
}
async function watch(mainPid,helperPid) {
  const child=spawn('powershell.exe',['-NoProfile','-NonInteractive','-File',path.join(base,'watch-strict-access.ps1'),'-MainPid',String(mainPid),'-HandlerPid',String(helperPid),'-OutputDirectory',run],{windowsHide:true,stdio:['ignore','pipe','pipe']});
  active.add(child);let stderr='';child.stderr.on('data',d=>stderr+=d);
  const completion=new Promise((resolve,reject)=>{child.once('error',reject);child.once('exit',code=>{active.delete(child);resolve({code,stderr});});});
  await until(()=>fs.existsSync(path.join(run,'watch-ready')),10000,'continuous watcher did not start');
  return {completion};
}
async function inventoryHandles(mainPid,helperPid,label,stage,children) {
  const file=path.join(run,`${label}.json`);
  const result=await command('powershell.exe',['-NoProfile','-NonInteractive','-File',path.join(base,'inventory-handles.ps1'),'-TargetProcessId',String(mainPid),'-HelperProcessId',String(helperPid),'-OutputFile',file],label);
  const snapshot=read(file);
  const item={label,stage,file,exitCode:result.code,snapshot};
  summary.handleInventories.push(item);
  if(result.code!==0||snapshot?.verified!==true){
    summary.inventoryStatus='unverified';item.reason=snapshot?.error||result.stderr||'handle snapshot unavailable';save();return item;
  }
  assert.equal(snapshot.targetProcessId,mainPid,`${label}: wrong target PID`);
  assert.equal(snapshot.helperProcessId,helperPid,`${label}: wrong helper PID`);
  assert.equal(snapshot.targetAnchorValidated,true,`${label}: no target process anchor`);
  assert.equal(snapshot.scope,'target_process_object_point_in_time');
  assert.ok(snapshot.targetHandles.length>0,`${label}: empty anchored process handle list`);
  const helperTarget=snapshot.targetHandles.filter(row=>row.holderPid===helperPid);
  item.helperTargetHandles=helperTarget;
  if(stage==='initial'||stage==='final'||stage==='pre_handshake'){
    assert.equal(helperTarget.length,0,`${label}: helper inherited a main process handle before first code`);
    if(stage!=='pre_handshake')assert.equal(snapshot.helperHandles.length,3,`${label}: suspended helper initial handle list is not the three stdio handles`);
  }else{
    assert.equal(helperTarget.length,1,`${label}: helper must hold exactly one target handle`);
    assert.equal(helperTarget[0].grantedAccess.toLowerCase(),'0x00120410',`${label}: helper target mask differs from fixed capability`);
    assert.equal(helperTarget[0].attributes.toLowerCase(),'0x00000000',`${label}: helper target handle is inheritable or otherwise attributed`);
  }
  const knownChildren=new Set([children?.rendererPid,...(children?.metrics||[]).map(row=>row.pid)].filter(pid=>Number.isInteger(pid)&&pid>0&&pid!==mainPid&&pid!==helperPid));
  item.knownChildPids=[...knownChildren];
  item.unknownDangerousHolders=[];
  for(const row of snapshot.targetHandles){
    const access=Number.parseInt(row.grantedAccess,16),extra=access&~0x101001;
    if(!extra||row.holderPid===helperPid||row.holderPid===mainPid||row.holderPid===snapshot.observerPid)continue;
    if(knownChildren.has(row.holderPid))assert.fail(`${label}: Electron child ${row.holderPid} holds dangerous main process rights ${row.grantedAccess}`);
    item.unknownDangerousHolders.push(row);
  }
  if(item.unknownDangerousHolders.length>0)summary.inventoryStatus='unverified';
  else if(summary.inventoryStatus==='not_sampled')summary.inventoryStatus='verified';
  save();return item;
}
function finishBehavior() {
  summary.behaviorCompleted=true;
  summary.acceptanceComplete=['verified','not_applicable'].includes(summary.inventoryStatus);
  summary.completed=true;summary.finishedUtc=new Date().toISOString();save();
  if(!summary.acceptanceComplete){
    process.exitCode=2;
    console.error(`Behavior complete; inherited-handle inventory ${summary.inventoryStatus}: ${run}`);
  }else console.log('P1_PHASE_A_COMPLETE='+run);
}
function verifyReport(report,reportFile,kind) {
  assert.ok(report,'missing safe report');
  assert.deepEqual(Object.keys(report).sort(),['schema','createdAtUnixMs','appVersion','exceptionCode','moduleId','moduleRva','captureBackend','daclUnchanged','threads'].sort(),'unexpected diagnostic field');
  assert.ok([1,2].includes(report.schema));
  assert.equal(report.exceptionCode,'0xc0000005');
  assert.equal(report.captureBackend,'crashpad-snapshot');
  assert.equal(report.daclUnchanged,true);
  assert.ok(Array.isArray(report.threads)&&report.threads.length===1,'stage A must emit only the fault thread');
  const fault=report.threads[0];
  assert.deepEqual(Object.keys(fault).sort(),['threadId','frames'].sort(),'unexpected thread field');
  assert.ok(Number.isSafeInteger(fault.threadId)&&fault.threadId>0);
  assert.ok(Array.isArray(fault.frames));
  const frames=fault.frames;
  assert.ok(frames.length>=3,'fewer than three native frames');
  for(const frame of frames){assert.deepEqual(Object.keys(frame).sort(),['moduleId','moduleRva','trust'].sort(),'unexpected frame field');assert.ok(['context','stackwalk','leaf_stack'].includes(frame.trust));assert.ok(Number.isSafeInteger(frame.moduleRva)&&frame.moduleRva>=0);assert.ok(typeof frame.moduleId==='string'&&frame.moduleId.length>0);}
  if(kind==='knownchain')assert.ok(frames.slice(0,3).every(f=>f.moduleId==='process-hardening.node'),'knownchain fault thread lacks three native frames');
  const text=fs.readFileSync(reportFile,'utf8');
  assert.ok(!text.includes('ONEKEY_SYNTHETIC_CRASH_SECRET_7c4d52e1'));
  assert.ok(!/[A-Za-z]:\\/.test(text),'absolute path in safe report');
  return frames.length;
}
(async()=>{
  console.log('P1_PHASE_A_RUN='+run);save();
  summary.binaries={electron:recordBinary(exe)};save();
  assert.equal(summary.binaries.electron.sha256.toLowerCase(),expected.electronSha256.toLowerCase(),'OneKey.exe does not match candidate manifest');
  const first=launch(false);summary.pid=first.child.pid;save();
  if(fault&&fault[2]==='pre_handshake'){
    const marker=await until(()=>read(path.join(run,'empty-user-data/local-crash-prototype/fault-stage.json')),15000,'pre_handshake marker not published');
    assert.equal(marker.stage,'pre_handshake');
    assert.ok(Number.isInteger(marker.helperPid)&&marker.helperPid>0);
    assert.equal(marker.mode,fault[1]==='suspend'?'suspend':'sleep');
    summary.faultMarker=marker;save();
    // Observe the helper first, within the unchanged five-second startup deadline.
    const observations=await Promise.all([
      probe(marker.helperPid,'pre-handshake-helper','helper'),
      fault[1]==='suspend'?snapshotSuspension(marker.helperPid,'suspension'):Promise.resolve(undefined),
    ]);
    summary.faultProbe={helper:observations[0]};
    if(observations[1])summary.suspension=observations[1];
    save();
    if(fault[1]==='kill'){
      const nativePath=await until(()=>read(path.join(run,'main.json'))?.nativeLoads?.[0]?.path,5000,'native path missing before pre_handshake kill');
      const helperPath=path.join(path.dirname(nativePath),'onekey-crash-prototype.win32-x64-msvc.exe');
      const killed=await command('powershell.exe',['-NoProfile','-NonInteractive','-File',path.join(base,'terminate-helper.ps1'),'-TargetProcessId',String(marker.helperPid),'-ExpectedPath',helperPath,'-OutputFile',path.join(run,'helper-kill.json')],'terminate-helper');
      assert.equal(killed.code,0,killed.stderr);summary.helperKill=read(path.join(run,'helper-kill.json'));save();
    }
    summary.faultProbe.main=await probe(first.child.pid,'pre-handshake-main','record');save();
    await inventoryHandles(first.child.pid,marker.helperPid,'pre-handshake-handles','pre_handshake');
  }
  if(testCase==='create-gate'){
    const gateFolder=path.join(run,'empty-user-data/local-crash-prototype');
    summary.createGate={};
    for(const [stage,prefix,mode] of [['initial','create-gate-initial','initial'],['final','create-gate','helper']]){
      const gate=await until(()=>read(path.join(gateFolder,`${prefix}-ready.json`)),15000,`${stage} CREATE_SUSPENDED gate not published`);
      assert.ok(Number.isInteger(gate.helperPid)&&gate.helperPid>0);
      assert.ok(Number.isInteger(gate.initialThreadId)&&gate.initialThreadId>0);
      assert.ok(typeof gate.nonce==='string'&&gate.nonce.length>0&&gate.nonce.length<256);
      if(stage==='final'){
        assert.equal(gate.helperPid,summary.createGate.initial.ready.helperPid,'helper PID changed between creation gates');
        assert.equal(gate.initialThreadId,summary.createGate.initial.ready.initialThreadId,'initial TID changed between creation gates');
      }
      const gateMain=read(path.join(run,'main.json'));
      assert.equal(gateMain?.hardenCurrentProcess?.dacl.status,'applied',`${stage}: main DACL was not applied before helper creation`);
      assert.equal(gateMain?.mainBundle?.sha256?.toLowerCase(),expected.mainBundleSha256.toLowerCase(),`${stage}: main bundle identity missing`);
      summary.createGate[stage]={ready:gate,mainProbe:await probe(first.child.pid,`${stage}-gate-main`,'main'),helperProbe:await probe(gate.helperPid,`${stage}-gate-helper`,mode,gate.initialThreadId)};
      await inventoryHandles(first.child.pid,gate.helperPid,`${stage}-gate-handles`,stage);
      assert.match(summary.createGate[stage].mainProbe.tokenDefaultDaclSha256,/^[0-9a-f]{64}$/,`${stage}: main default DACL hash missing`);
      if(stage==='final')assert.equal(summary.createGate.final.mainProbe.tokenDefaultDaclSha256,summary.createGate.initial.mainProbe.tokenDefaultDaclSha256,'parent token default DACL changed during helper creation');
      save();
      const release=JSON.stringify({nonce:gate.nonce});
      assert.ok(Buffer.byteLength(release)<=128,`${stage} gate release exceeds native bound`);
      const releaseTmp=path.join(gateFolder,`${prefix}-release.tmp`);
      fs.writeFileSync(releaseTmp,release,{flag:'wx'});
      fs.renameSync(releaseTmp,path.join(gateFolder,`${prefix}-release.json`));
      summary.createGate[stage].releasedUtc=new Date().toISOString();save();
    }
  }
  const main=await until(()=>{const r=read(path.join(run,'main.json'));if(first.child.exitCode!==null&&!r?.emptyWalletWelcome)throw new Error('main exited before welcome: '+JSON.stringify(r));return r?.emptyWalletWelcome&&r;},100000,'welcome not ready');
  summary.main=main;
  assert.equal(main.reporterCalls,0);
  assert.ok(Number.isInteger(main.childProcesses?.rendererPid)&&main.childProcesses.rendererPid>0,'fixture did not record renderer PID');
  assert.equal(main.mainBundle?.sha256?.toLowerCase(),expected.mainBundleSha256.toLowerCase(),'running main bundle hash differs from candidate');
  assert.ok(main.candidateLoads?.length>0,'candidate package was not requested');
  for(const load of main.candidateLoads){assert.equal(path.resolve(load.path).toLowerCase(),path.join(candidateDir,'index.cjs').toLowerCase(),'package load escaped candidate');assert.equal(load.sha256.toLowerCase(),expected.indexSha256.toLowerCase(),'candidate index changed during load');}
  assert.ok(main.nativeLoads?.length>0,'candidate native binding was not loaded');
  const native=path.join(candidateDir,candidateFiles.native[0]);
  for(const load of main.nativeLoads){assert.equal(path.resolve(load.path).toLowerCase(),native.toLowerCase(),'loaded native binding outside candidate package');assert.equal(load.sha256.toLowerCase(),expected.nativeSha256.toLowerCase(),'loaded native binding hash mismatch');}
  summary.binaries.native=recordBinary(native);
  summary.binaries.helper=recordBinary(path.join(candidateDir,candidateFiles.helper[0]));
  summary.binaries.capture=recordBinary(path.join(candidateDir,candidateFiles.capture[0]));
  for(const [name,key] of [['native','nativeSha256'],['helper','helperSha256'],['capture','captureSha256']])assert.equal(summary.binaries[name]?.sha256?.toLowerCase(),expected[key].toLowerCase(),`${name} consumer binary differs from candidate manifest`);
  save();
  if(['disabled','startfail'].includes(testCase)||(fault&&fault[2]==='pre_handshake')){
    summary.exit=await first.completion;assert.equal(summary.exit.code,0);
    if(testCase==='disabled')assert.equal(main.startLocalCrashHandler,undefined);
    else assert.equal(main.startLocalCrashHandler?.status,'failed');
    if(fault)assert.equal(summary.faultMarker.helperPid>0,true);
    if(fault){
      const live=await command('powershell.exe',['-NoProfile','-NonInteractive','-Command',`[bool](Get-Process -Id ${summary.faultMarker.helperPid} -ErrorAction SilentlyContinue)`],'helper-liveness');
      assert.equal(live.code,0);assert.equal(live.stdout.trim(),'False','startup failure left orphan helper');
      assert.deepEqual(read(path.join(run,'empty-user-data/local-crash-prototype/fault-stage.json')),summary.faultMarker,'parent could not reopen startup fault marker after helper exit');
    }
    summary.files=[...inventory(path.join(run,'empty-user-data'),'profile'),...inventory(path.join(run,'crash-dumps'),'crashpad')];
    assert.ok(summary.files.every(x=>!x.mdmp&&!x.name.toLowerCase().endsWith('.dmp')));
    summary.candidateFilesAfter={};for(const [name,[filename]] of Object.entries(candidateFiles)){summary.candidateFilesAfter[name]=recordBinary(path.join(candidateDir,filename));assert.equal(summary.candidateFilesAfter[name].sha256,candidateBinaries[name].sha256,`${filename} changed during run`);}
    summary.electronAfter=recordBinary(exe);assert.equal(summary.electronAfter.sha256,summary.binaries.electron.sha256,'OneKey.exe changed during run');
    summary.mainBundleAfter=recordBinary(mainBundle);assert.equal(summary.mainBundleAfter.sha256,summary.mainBundle.sha256,'main bundle changed during run');
    if(fault?.[1]==='suspend')assert.equal(summary.suspension?.confirmed,true,'actual helper suspension was not observed');
    if(!fault)summary.inventoryStatus='not_applicable';
    finishBehavior();return;
  }
  const ready=main.startLocalCrashHandler;
  assert.ok(ready?.handlerPid,`handler did not report ready PID: ${JSON.stringify(ready)}`);
  if(testCase==='create-gate')assert.equal(ready.handlerPid,summary.createGate.final.ready.helperPid,'gate PID differs from ready helper');
  summary.before={main:await probe(first.child.pid,'before-main','main'),helper:await probe(ready.handlerPid,'before-helper','helper')};save();
  if(testCase==='create-gate')assert.equal(summary.before.main.tokenDefaultDaclSha256,summary.createGate.initial.mainProbe.tokenDefaultDaclSha256,'parent token default DACL changed by welcome');
  const watching=await watch(first.child.pid,ready.handlerPid);
  await inventoryHandles(first.child.pid,ready.handlerPid,'welcome-handles','ready',main.childProcesses);
  if(testCase==='helper-kill-healthy'){
    const kill=await command('powershell.exe',['-NoProfile','-NonInteractive','-File',path.join(base,'terminate-helper.ps1'),'-TargetProcessId',String(ready.handlerPid),'-ExpectedPath',summary.binaries.helper.path,'-OutputFile',path.join(run,'helper-kill.json')],'terminate-helper');
    assert.equal(kill.code,0,kill.stderr);
    summary.helperKill=read(path.join(run,'helper-kill.json'));
    assert.ok(summary.helperKill);
    await sleep(36000);
    assert.equal(first.child.exitCode,null,'healthy main exited after helper death');
    summary.mainSurvivedHelperDeathMs=36000;save();
    fs.writeFileSync(path.join(run,'trigger-quit'),'test only');
    summary.exit=await Promise.race([first.completion,failAfter(15000,'healthy main did not quit')]);
    assert.equal(summary.exit.code,0,'healthy main failed after helper death');
    fs.writeFileSync(path.join(run,'stop-watch'),'stop');
    summary.watcherExit=await Promise.race([watching.completion,failAfter(10000,'watcher did not stop')]);
    assert.equal(summary.watcherExit.code,0,summary.watcherExit.stderr);
    summary.watch=read(path.join(run,'watch.json'));
    assert.equal(summary.watch?.violations,0);
    summary.files=[...inventory(path.join(run,'empty-user-data'),'profile'),...inventory(path.join(run,'crash-dumps'),'crashpad')];
    assert.ok(summary.files.every(x=>!x.mdmp&&!x.name.toLowerCase().endsWith('.dmp')));
    assert.equal(fs.existsSync(path.join(run,'empty-user-data/local-crash-prototype/report.json')),false);
    summary.candidateFilesAfter={};for(const [name,[filename]] of Object.entries(candidateFiles)){summary.candidateFilesAfter[name]=recordBinary(path.join(candidateDir,filename));assert.equal(summary.candidateFilesAfter[name].sha256,candidateBinaries[name].sha256,`${filename} changed during run`);}
    summary.electronAfter=recordBinary(exe);assert.equal(summary.electronAfter.sha256,summary.binaries.electron.sha256,'OneKey.exe changed during run');
    summary.mainBundleAfter=recordBinary(mainBundle);assert.equal(summary.mainBundleAfter.sha256,summary.mainBundle.sha256,'main bundle changed during run');
    finishBehavior();return;
  }
  const trigger=path.join(run,'trigger-crash');fs.writeFileSync(trigger,'test only');
  const marked=await until(()=>read(path.join(run,'main.json'))?.crashTriggeredUtc&&read(path.join(run,'main.json')),10000,'crash trigger timestamp missing');
  summary.triggeredUtc=marked.crashTriggeredUtc;save();
  assert.ok(Number.isInteger(marked.childProcessesAtTrigger?.rendererPid)&&marked.childProcessesAtTrigger.rendererPid>0,'fixture did not record renderer PID at crash trigger');
  if(fault){
    const marker=await until(()=>read(path.join(run,'empty-user-data/local-crash-prototype/fault-stage.json')),15000,`${fault[2]} marker not published`);
    assert.equal(marker.stage,fault[2]);assert.equal(marker.helperPid,ready.handlerPid);
    assert.equal(marker.mode,fault[1]==='suspend'?'suspend':'sleep');
    summary.faultMarker=marker;save();
    if(fault[1]==='suspend'){summary.suspension=await snapshotSuspension(marker.helperPid,'suspension');save();}
    summary.faultProbe={main:await probe(first.child.pid,'fault-main','main'),helper:await probe(marker.helperPid,'fault-helper','helper')};save();
    await inventoryHandles(first.child.pid,marker.helperPid,'fault-handles','capture',marked.childProcessesAtTrigger);
    if(fault[1]==='kill'){
      const killed=await command('powershell.exe',['-NoProfile','-NonInteractive','-File',path.join(base,'terminate-helper.ps1'),'-TargetProcessId',String(ready.handlerPid),'-ExpectedPath',summary.binaries.helper.path,'-OutputFile',path.join(run,'helper-kill.json')],'terminate-helper');
      assert.equal(killed.code,0,killed.stderr);summary.helperKill=read(path.join(run,'helper-kill.json'));save();
    }
  }
  const exit=await Promise.race([first.completion,failAfter(40000,'crashed main remained alive past 40s')]);
  summary.exit=exit;summary.nodeExitElapsedMs=Date.parse(exit.exitedUtc)-Date.parse(marked.crashTriggeredUtc);save();
  assert.notEqual(exit.code,0,'crash unexpectedly exited cleanly');
  fs.writeFileSync(path.join(run,'stop-watch'),'stop');
  summary.watcherExit=await Promise.race([watching.completion,failAfter(10000,'watcher did not stop')]);
  assert.equal(summary.watcherExit.code,0,summary.watcherExit.stderr);
  summary.watch=read(path.join(run,'watch.json'));
  assert.equal(summary.watch?.callerElevated,false);
  assert.ok(summary.watch.attempts>0);
  assert.equal(summary.watch.violations,0,'dangerous process access became available');
  assert.equal(summary.watch.mainHandleAccess,'0x00101000','exit observer requested extra process rights');
  assert.ok(Number.isFinite(Date.parse(summary.watch.mainExitUtc)),'kernel process exit time missing');
  summary.elapsedMs=Date.parse(summary.watch.mainExitUtc)-Date.parse(marked.crashTriggeredUtc);
  summary.nodeExitNotificationDelayMs=Date.parse(exit.exitedUtc)-Date.parse(summary.watch.mainExitUtc);
  summary.stdioCloseDelayMs=Date.parse(exit.stdioClosedUtc)-Date.parse(exit.exitedUtc);
  summary.mainSignalObservedElapsedMs=Date.parse(summary.watch.mainExitObservedUtc)-Date.parse(marked.crashTriggeredUtc);
  summary.elapsedSource='GetProcessTimes kernel exit time minus fixture trigger UTC';save();
  assert.ok(summary.elapsedMs>=0&&summary.elapsedMs<=35000,'kernel crash exit '+summary.elapsedMs+'ms exceeds 35s');
  assert.ok(summary.mainSignalObservedElapsedMs>=0&&summary.mainSignalObservedElapsedMs<=35000,'main process signal observed after '+summary.mainSignalObservedElapsedMs+'ms exceeds 35s');
  const statusFile=path.join(run,'empty-user-data/local-crash-prototype/status.json');
  const reportFile=path.join(run,'empty-user-data/local-crash-prototype/report.json');
  const pendingFile=path.join(run,'empty-user-data/local-crash-prototype/report.pending');
  if(fs.existsSync(pendingFile)){
    const bytes=fs.readFileSync(pendingFile);
    summary.pending={bytes:bytes.length,sha256:sha(pendingFile),validJson:!!read(pendingFile)};
    assert.ok(bytes.length<=256*1024,'pending report exceeds safe bound');
    assert.ok(!bytes.includes(Buffer.from('ONEKEY_SYNTHETIC_CRASH_SECRET_')));
    assert.ok(!bytes.includes(Buffer.from('ONEKEY_SYNTHETIC_CRASH_SECRET_','utf16le')));
    assert.ok(!/[A-Za-z]:\\/.test(bytes.toString('utf8')),'absolute path in pending report');
    assert.notEqual(bytes.subarray(0,4).toString('ascii'),'MDMP','raw dump in pending report');
  }
  summary.status=read(statusFile);
  if(fault){
    // A killed or blocked helper cannot publish its final status; the synced fault marker is required.
    assert.ok(summary.faultMarker,'fault injection left no readable stage marker');
    summary.statusUnavailableAfterForcedHelperStop=!summary.status;
    assert.equal(fs.existsSync(reportFile),false,'fault injection emitted report');
  }else{
    assert.ok(summary.status,'missing handler status');
    assert.equal(summary.status.targetPid,first.child.pid);
    assert.equal(summary.status.handlerPid,ready.handlerPid);
    assert.equal(summary.status.daclUnchanged,true);
  }
  if(!fault&&['scrub','dump','unwind'].includes(testCase)){
    assert.equal(summary.status.stage,testCase==='dump'?'dump_failed':'scrub_failed');
    assert.equal(fs.existsSync(reportFile),false,'failure emitted report');
  }else if(!fault){
    assert.equal(summary.status.stage,'complete');
    summary.report=read(reportFile);
    summary.frameCount=verifyReport(summary.report,reportFile,testCase);
  }
  summary.files=await until(()=>{try{return [...inventory(path.join(run,'empty-user-data'),'profile'),...inventory(path.join(run,'crash-dumps'),'crashpad')];}catch(e){if(['EBUSY','EPERM'].includes(e.code))return undefined;throw e;}},15000,'profile locked after crash');
  assert.ok(summary.files.every(x=>!x.mdmp&&!x.name.toLowerCase().endsWith('.dmp')),'raw dump on disk');
  summary.binariesAfter={electron:recordBinary(exe),native:recordBinary(summary.binaries.native.path),helper:recordBinary(summary.binaries.helper.path),capture:recordBinary(summary.binaries.capture.path)};
  for(const name of Object.keys(summary.binariesAfter))assert.equal(summary.binariesAfter[name].sha256,summary.binaries[name].sha256,`${name} changed during run`);
  assert.equal(sha(mainBundle).toLowerCase(),expected.mainBundleSha256.toLowerCase(),'main bundle changed during run');
  summary.candidateFilesAfter={};
  for(const [name,[filename]] of Object.entries(candidateFiles)){
    summary.candidateFilesAfter[name]=recordBinary(path.join(candidateDir,filename));
    assert.equal(summary.candidateFilesAfter[name].sha256,candidateBinaries[name].sha256,`${filename} changed during run`);
  }
  summary.helperAliveAfterExit=await command('powershell.exe',['-NoProfile','-NonInteractive','-Command',`[bool](Get-Process -Id ${ready.handlerPid} -ErrorAction SilentlyContinue)`],'helper-liveness');
  assert.equal(summary.helperAliveAfterExit.code,0);
  assert.equal(summary.helperAliveAfterExit.stdout.trim(),'False','orphan helper after main exit');
  assert.deepEqual(read(statusFile),summary.status,'parent could not reopen status after helper exit');
  if(fault)assert.deepEqual(read(path.join(run,'empty-user-data/local-crash-prototype/fault-stage.json')),summary.faultMarker,'parent could not reopen fault marker after helper exit');
  else if(!['scrub','dump','unwind'].includes(testCase)){
    assert.deepEqual(read(reportFile),summary.report,'parent could not reopen report after helper exit');
    assert.equal(fs.existsSync(pendingFile),false,'successful report left pending file');
  }
  save();
  const second=launch(true);
  summary.restart=await Promise.race([second.completion,failAfter(100000,'restart timeout')]);
  assert.equal(summary.restart.code,0);
  const restart=read(path.join(run,'restart.json'));
  assert.equal(restart?.emptyWalletWelcome,true);
  assert.equal(restart?.mainBundle?.sha256?.toLowerCase(),expected.mainBundleSha256.toLowerCase(),'restart main bundle differs from candidate');
  assert.ok(restart.candidateLoads?.length>0,'restart did not load candidate package');
  assert.ok(restart.nativeLoads?.every(load=>path.resolve(load.path).toLowerCase()===native.toLowerCase()),'restart loaded noncandidate native binding');
  assert.equal(fs.existsSync(reportFile),!fault&&!['scrub','dump','unwind'].includes(testCase));
  if(!fault&&!['scrub','dump','unwind'].includes(testCase))assert.deepEqual(read(reportFile),summary.report,'restart profile could not reopen safe report');
  if(fault?.[1]==='suspend')assert.equal(summary.suspension?.confirmed,true,'actual helper suspension was not observed');
  finishBehavior();
})().catch(error=>{
  summary.error=String(error.stack);summary.finishedUtc=new Date().toISOString();save();
  try{fs.writeFileSync(path.join(run,'stop-watch'),'stop');}catch{}
  for(const child of active)child.kill();
  console.error(error);process.exitCode=1;
});
