// OneKey patch: exercise actual worker/broker code with controlled I/O completion.
const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const vm = require('node:vm');
const { test } = require('node:test');
const ts = require('typescript');
const { IDBFactory } = require('fake-indexeddb');
const prefix = 'onekey-avatar://blockie/v1/';
const sourceRoot = path.resolve(__dirname, '..');
const tick = () => new Promise((resolve) => setTimeout(resolve, 1));
async function until(predicate) { for (let i = 0; i < 3000; i++) { if (predicate()) return; await tick(); } throw new Error('Timed out'); }
function worker({ db = new IDBFactory(), size = 100 } = {}) {
  const messages = [], generated = [], urls = new Map(), revoked = [];
  const context = {
    indexedDB: db, Blob, setTimeout, clearTimeout,
    createImageBitmap: async () => ({ width: 128, height: 128, close() {} }),
    URL: { createObjectURL(blob) { const url = `blob:${messages.length}:${urls.size}`; urls.set(url, blob); return url; }, revokeObjectURL(url) { revoked.push(url); urls.delete(url); } },
    postMessage(message) { messages.push(message); }, onmessage: undefined,
    generate: async (seed) => { generated.push(seed); return new Blob([new Uint8Array(size)], { type: 'image/png' }); },
  };
  vm.createContext(context);
  vm.runInContext(fs.readFileSync(path.join(sourceRoot, 'web/NativeListAvatarWorker.js'), 'utf8'), context);
  vm.runInContext('generateBlob = generate', context);
  const read = (expression) => vm.runInContext(expression, context);
  const send = (type, id, seed = String(id), priority = 2) => context.onmessage({ data: { type, id, uri: prefix + seed, priority } });
  return { context, messages, generated, urls, revoked, read, send, db };
}
function loadTypeScript(name, globals = {}) {
  const source = fs.readFileSync(path.join(sourceRoot, name), 'utf8').replaceAll('import.meta.url', "'https://unit.test/module.js'");
  const result = ts.transpileModule(source, { compilerOptions: { target: ts.ScriptTarget.ES2022, module: ts.ModuleKind.CommonJS } });
  const exports = {};
  const context = vm.createContext({ exports, setTimeout, clearTimeout, ...globals });
  vm.runInContext(result.outputText, context);
  return exports;
}
test('display and subsequent load slots do not await disk; pending bytes deduplicate reacquire', async () => {
  const w = worker();
  let finishWrite;
  w.context.blockedWrite = () => new Promise((resolve) => { finishWrite = resolve; });
  w.read('readDisk = async () => undefined; writeDisk = blockedWrite');
  w.send('acquire', 1, 'a');
  await until(() => w.messages.length === 1);
  assert.equal(w.read('activeLoads'), 0);
  assert.equal(w.read('pendingBlobs.size'), 1);
  w.send('release', 1, 'a');
  w.send('acquire', 2, 'a');
  await until(() => w.messages.length === 2);
  assert.deepEqual(w.generated, ['a']);
  w.send('acquire', 3, 'b');
  await until(() => w.messages.length === 3);
  assert.deepEqual(w.generated, ['a', 'b']);
  // OneKey patch: background persistence starts after the shared quiet window.
  await until(() => finishWrite);
  // Quota/write failure remains best-effort, never a late display error.
  w.read('writeDisk = async () => { throw new Error("quota"); }');
  finishWrite();
  await until(() => w.read('pendingBlobs.size') === 0);
  assert(w.messages.every((message) => message.type === 'resolved'));
});
test('background persistence is bounded without blocking visible results', async () => {
  const w = worker({ size: 200_000 });
  let release;
  w.context.blockedWrite = () => new Promise((resolve) => { release = resolve; });
  w.read('readDisk = async () => undefined; writeDisk = blockedWrite');
  for (let index = 0; index < 80; index++) {
    w.send('acquire', index);
    await until(() => w.messages.length === index + 1);
    w.send('release', index);
  }
  assert(w.read('pendingBlobs.size') <= 32);
  assert(w.read('pendingBytes') <= 4 * 1024 * 1024);
  assert.equal(w.read('activeLoads'), 0);
  assert.equal(w.urls.size, 0);
  await until(() => release);
  w.read('writeDisk = async () => {}');
  release();
  await until(() => w.read('pendingBytes') === 0);
});
test('queued visible promotion wins over FIFO prefetch without duplicate generation', async () => {
  const w = worker();
  const reads = [];
  w.context.blockedRead = () => new Promise((resolve) => reads.push(resolve));
  w.read('readDisk = blockedRead; writeDisk = async () => {}');
  w.send('acquire', 1, 'active-a'); w.send('acquire', 2, 'active-b');
  w.send('acquire', 3, 'earlier-prefetch'); w.send('acquire', 4, 'now-visible');
  w.send('priority', 4, 'now-visible', 0);
  reads[0]();
  await until(() => reads.length === 3);
  assert.equal(w.read('jobs.get("' + prefix + 'now-visible").active'), true);
  assert.equal(w.read('jobs.get("' + prefix + 'earlier-prefetch").active'), false);
  w.send('release', 3); // Drop stale opposite-direction work before it starts.
  reads[1](); reads[2]();
  await until(() => w.read('activeLoads') === 0);
  assert(!w.generated.includes('earlier-prefetch'));
  assert.equal(w.generated.filter((seed) => seed === 'now-visible').length, 1);
});
test('last-lease cancellation during generation never publishes or leaks a Blob URL', async () => {
  const w = worker(); let finish;
  w.context.slowGenerate = () => new Promise((resolve) => { finish = resolve; });
  w.read('readDisk = async () => undefined; generateBlob = slowGenerate; writeDisk = async () => {}');
  w.send('acquire', 1); await until(() => finish);
  w.send('release', 1); finish(new Blob(['png'], { type: 'image/png' }));
  await until(() => w.read('activeLoads') === 0);
  assert.equal(w.messages.length, 0); assert.equal(w.urls.size, 0);
});
test('disk reopen avoids generation; readonly hits defer touch; corrupt cache regenerates', async () => {
  const w = worker(); w.send('acquire', 1, 'cached');
  await until(() => w.messages.length === 1 && w.read('pendingBlobs.size') === 0);
  const reopened = worker({ db: w.db }); reopened.send('acquire', 1, 'cached');
  await until(() => reopened.messages.length === 1);
  assert.equal(reopened.generated.length, 0);
  assert.equal(reopened.read('touches.size'), 1);
  const corrupt = worker({ db: w.db });
  corrupt.context.createImageBitmap = async () => { throw new Error('corrupt'); };
  corrupt.send('acquire', 1, 'cached');
  await until(() => corrupt.messages.length === 1);
  assert.deepEqual(corrupt.generated, ['cached']);
  const denied = worker({ db: { open() { throw new Error('denied'); } } });
  denied.send('acquire', 1); await until(() => denied.messages.length === 1);
  assert.equal(denied.generated.length, 1);
});
test('broker promotes a shared queued URI and release restores remaining priority', () => {
  let instance;
  class FakeWorker {
    constructor() { instance = this; this.messages = []; }
    addEventListener() {}
    postMessage(message) { this.messages.push(message); }
  }
  const api = loadTypeScript('web/NativeListWebAvatarCache.ts', { Worker: FakeWorker, URL, queueMicrotask });
  const document = {};
  const prefetch = api.acquireNativeListAvatar(document, prefix + 'a', () => {}, () => {}, 2);
  const visible = api.acquireNativeListAvatar(document, prefix + 'a', () => {}, () => {}, 0);
  assert.equal(instance.messages.filter((m) => m.type === 'acquire').length, 1);
  assert.equal(instance.messages.at(-1).priority, 0);
  visible(); assert.equal(instance.messages.at(-1).priority, 2);
  prefetch.setPriority(1); assert.equal(instance.messages.at(-1).priority, 1);
  prefetch(); assert(instance.messages.some((m) => m.type === 'release'));
});
test('window prioritizes visibility, reverses ahead work and bounds large groups', () => {
  const { avatarPrefetchWindow } = loadTypeScript('avatarPrefetch.ts');
  const row = (i) => ({ type: 'identity', key: String(i), title: String(i), leading: { kind: 'account', image: { uri: prefix + i, width: 32, height: 32 } } });
  const rows = Array.from({ length: 1000 }, (_, i) => row(i));
  const forward = avatarPrefetchWindow(rows, 30, 39, 1);
  assert.equal(forward[0].source.uri, prefix + '30');
  assert.equal(forward.find((item) => item.priority === 1).source.uri, prefix + '40');
  const reverse = avatarPrefetchWindow(rows, 30, 39, -1);
  assert.equal(reverse.find((item) => item.priority === 1).source.uri, prefix + '29');
  assert(forward.length <= 64); assert(reverse.length <= 64);
  const large = avatarPrefetchWindow([{ type: 'walletGroup', key: '0', parent: row(0), children: rows.slice(1) }], 0, 0, 1);
  assert(large.length <= 64);
  assert.equal(avatarPrefetchWindow(rows, -1, -1, 1).length, 0);
});
test('native short batches replace stale pending direction, deduplicate and stop on unmount', async () => {
  const { NativeAvatarPrefetchQueue } = loadTypeScript('avatarPrefetch.ts');
  const started = [], finish = [];
  const queue = new NativeAvatarPrefetchQueue((source) => { started.push(source.uri); return new Promise((resolve) => finish.push(resolve)); });
  const candidates = (...values) => values.map((value) => ({ source: { uri: prefix + value, width: 32, height: 32 }, priority: 1 }));
  queue.update(candidates('a', 'b', 'c')); await until(() => started.length === 1);
  queue.update(candidates('x', 'y')); finish[0](true);
  await until(() => started.length === 2);
  assert.deepEqual(started, [prefix + 'a', prefix + 'x']);
  queue.update(candidates('a', 'x', 'z')); queue.dispose(); finish[1](true);
  await new Promise((resolve) => setTimeout(resolve, 25));
  assert.equal(started.length, 2);
});
test('over-budget actual IDB write rolls back and unblocks a subsequent display miss', async () => {
  const w = worker();
  w.send('acquire', 1, 'prime');
  await until(() => w.messages.length === 1 && w.read('pendingBlobs.size') === 0);
  const database = await w.read('openDatabase()');
  const originalTransaction = database.transaction.bind(database);
  let blocked = false, aborted = false;
  database.transaction = (...args) => {
    const transaction = originalTransaction(...args);
    if (args[1] === 'readwrite' && Array.isArray(args[0]) && !blocked) {
      blocked = true;
      // Keep the real readwrite transaction alive until the production deadline
      // aborts it. A later readonly transaction really shares this store lock.
      let alive = true;
      transaction.addEventListener('abort', () => { alive = false; aborted = true; });
      const store = transaction.objectStore('images');
      const keepAlive = () => {
        if (!alive) return;
        try { const request = store.get('lock'); request.onsuccess = keepAlive; } catch {}
      };
      keepAlive();
    }
    return transaction;
  };
  w.send('acquire', 2, 'blocked-write');
  await until(() => blocked && w.messages.length === 2);
  w.send('acquire', 3, 'subsequent-miss');
  await until(() => w.messages.length === 3 && aborted);
  assert.equal(w.messages[2].type, 'resolved');
  assert(w.generated.includes('subsequent-miss'));
  await until(() => w.read('pendingBlobs.size') === 0);
  // The aborted transaction must not leave a half-written row/metadata counter.
  const check = originalTransaction(['images', 'metadata'], 'readonly');
  const failed = check.objectStore('images').get(prefix + 'blocked-write');
  const counter = check.objectStore('metadata').get('size');
  await new Promise((resolve) => { check.oncomplete = resolve; });
  assert.equal(failed.result, undefined);
  assert.equal(counter.result.count, 2);
});
