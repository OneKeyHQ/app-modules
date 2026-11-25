'use strict';
const fs = require('node:fs');
const path = require('node:path');
const crypto = require('node:crypto');
const Module = require('node:module');
const assert = require('node:assert/strict');
const { app, BrowserWindow, crashReporter } = require('electron');

const root = path.resolve(process.env.ONEKEY_P1_CONSUMER_ROOT || '');
assert.ok(process.env.ONEKEY_P1_CONSUMER_ROOT && path.isAbsolute(process.env.ONEKEY_P1_CONSUMER_ROOT), 'consumer root missing');
const output = path.resolve(process.env.ONEKEY_P1_RUN_DIR || '');
assert.ok(output.startsWith(path.join(path.resolve(__dirname, '../..'), '.tmp/p1-phase-a/harness-runs') + path.sep));
const fixedCandidate=path.join(path.resolve(__dirname, '../..'), '.tmp/p1-phase-a/candidate');
const candidateDir=path.resolve(process.env.ONEKEY_P1_CANDIDATE_DIR || '');
assert.equal(candidateDir.toLowerCase(),fixedCandidate.toLowerCase(),'candidate path differs from isolated P1 package');
const candidateIndex=path.join(candidateDir,'index.cjs');
const candidateNative=path.join(candidateDir,'electron-windows-process-hardening.win32-x64-msvc.node');
assert.ok(fs.existsSync(candidateIndex)&&fs.existsSync(candidateNative),'candidate package incomplete');
const testCase = process.env.ONEKEY_P1_CASE;
const restart = process.argv.includes('--restart');
const disabled = testCase === 'disabled';
const startFail = testCase === 'startfail';
const profile = path.join(output, 'empty-user-data');
fs.mkdirSync(profile, { recursive: true });
if (startFail && !restart) fs.mkdirSync(path.join(profile, 'local-crash-prototype'));
app.setPath('userData', profile);
app.setPath('crashDumps', path.join(output, 'crash-dumps'));
app.getAppPath = () => path.join(root, 'apps/desktop/app');
app.setAsDefaultProtocolClient = () => false;
Object.assign(process.env, {
  ELECTRON_IS_DEV: '0',
  DESKTOP_E2E_MODE: 'true',
  DESKTOP_E2E_USER_DATA_DIR: profile,
  ONEKEY_TEST_LOCAL_CRASH_HANDLER: disabled || restart ? '0' : '1',
});
delete process.env.PERF_CI_MODE;
delete process.env.ONEKEY_DISABLE_PROCESS_HARDENING;

const report = {
  pid: process.pid, restart, testCase, profile, candidateDirectory:candidateDir, candidateLoads:[], startedUtc: new Date().toISOString(),
  electron: process.versions.electron, node: process.versions.node,
  reporterCalls: 0, nativeLoads: [], errors: [],
};
const save = () => fs.writeFileSync(path.join(output, restart ? 'restart.json' : 'main.json'), JSON.stringify(report, null, 2));
process.on('uncaughtException', error => { report.errors.push(String(error.stack)); save(); app.exit(12); });
crashReporter.start = () => { report.reporterCalls++; save(); throw new Error('Crashpad upload forbidden in this test'); };
const originalLoad = Module._load;
Module._load = function(request, parent, main) {
  const packageRequest=request==='@onekeyfe/electron-windows-process-hardening';
  const effectiveRequest=packageRequest?candidateIndex:request;
  const value = Reflect.apply(originalLoad, this, [effectiveRequest, parent, main]);
  if (request.endsWith('.node') && request.includes('electron-windows-process-hardening')) {
    const binary = Module._resolveFilename(request, parent);
    assert.equal(path.resolve(binary).toLowerCase(),candidateNative.toLowerCase(),'native binding loaded outside candidate package');
    report.nativeLoads.push({ path: binary, sha256: crypto.createHash('sha256').update(fs.readFileSync(binary)).digest('hex') });
    save();
  }
  if (request === '@sentry/electron/main') return new Proxy(value, { get(target, key) {
    if (key === 'init') return options => target.init({ ...options, dsn: undefined, transport: () => ({ send: async () => ({}), flush: async () => true }) });
    return Reflect.get(target, key);
  }});
  if (packageRequest){
    report.candidateLoads.push({request,path:candidateIndex,sha256:crypto.createHash('sha256').update(fs.readFileSync(candidateIndex)).digest('hex')});save();
    return new Proxy(value, { get(target, key) {
    const fn = Reflect.get(target, key);
    if (['startLocalCrashHandler', 'hardenCurrentProcess'].includes(key)) return (...args) => {
      const result = fn(...args); report[key] = result; save(); return result;
    };
    return fn;
  }});
  }
  return value;
};
globalThis.__crashPrivacyCanary = [Buffer.from('ONEKEY_SYNTHETIC_CRASH_SECRET_7c4d52e1'), Buffer.from('ONEKEY_SYNTHETIC_CRASH_SECRET_7c4d52e1', 'utf16le')];
const watchdog = setTimeout(() => { report.errors.push('renderer watchdog'); save(); app.exit(9); }, 120000);
const mainBundle = path.join(root, 'apps/desktop/app/dist/app.js');
report.mainBundle = { path: mainBundle, sha256: crypto.createHash('sha256').update(fs.readFileSync(mainBundle)).digest('hex') };
save();
require(mainBundle);
const childSnapshot = wc => ({
  rendererPid: wc.getOSProcessId(),
  metrics: app.getAppMetrics().filter(row => Number.isInteger(row.pid) && row.pid > 0)
    .map(row => ({ pid: row.pid, type: row.type, serviceName: row.serviceName || null })),
});
let busy = false;
app.whenReady().then(() => {
  const timer = setInterval(async () => {
    if (busy) return;
    const wc = BrowserWindow.getAllWindows().map(w => w.webContents).find(w => w.getTitle() === 'OneKey');
    if (!wc) return;
    busy = true;
    try {
      const page = await wc.executeJavaScript('({ready:document.readyState,text:document.body.innerText,bridge:typeof globalThis.desktopApiBridge?.call})');
      if (page.ready !== 'complete' || page.bridge !== 'function' || !/创建(?:新)?钱包|连接硬件钱包|Create (?:a new )?wallet|Connect hardware wallet/i.test(page.text)) { busy = false; return; }
      clearInterval(timer); clearTimeout(watchdog);
      assert.equal(report.hardenCurrentProcess?.dacl.status, 'applied');
      assert.equal(report.hardenCurrentProcess?.extensionPoint.status, 'applied');
      assert.equal(report.reporterCalls, 0);
      report.childProcesses = childSnapshot(wc);
      report.emptyWalletWelcome = true; report.readyUtc = new Date().toISOString(); save();
      if (restart || disabled || startFail || testCase.endsWith('-pre_handshake')) { report.cleanExit = true; save(); app.quit(); return; }
      if (testCase === 'helper-kill-healthy') {
        const quitGate=path.join(output,'trigger-quit');
        const quitWaiter=setInterval(()=>{
          if(!fs.existsSync(quitGate))return;
          clearInterval(quitWaiter);report.cleanExit=true;report.quitUtc=new Date().toISOString();save();app.quit();
        },100);
        return;
      }
      const gate = path.join(output, 'trigger-crash');
      const waiter = setInterval(() => {
        if (!fs.existsSync(gate)) return;
        clearInterval(waiter);
        report.crashTriggeredUtc = new Date().toISOString();
        report.childProcessesAtTrigger = childSnapshot(wc);
        report.trigger = testCase === 'knownchain' ? 'native three-level AV' : 'Electron process.crash()';
        save();
        if (testCase === 'knownchain') Module.createRequire(path.join(root, 'apps/desktop/app/package.json'))('@onekeyfe/electron-windows-process-hardening').triggerLocalCrashForTest();
        else process.crash();
      }, 50);
      setTimeout(() => { report.errors.push('trigger watchdog'); save(); app.exit(10); }, 60000).unref();
    } catch (error) { report.errors.push(String(error.stack)); save(); app.exit(2); }
  }, 500);
});
save();
