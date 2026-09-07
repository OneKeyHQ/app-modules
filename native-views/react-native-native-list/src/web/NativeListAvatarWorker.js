/* global indexedDB, OffscreenCanvas, createImageBitmap, postMessage, onmessage: writable */
// OneKey patch: avatar generation and PNG bytes stay in this external worker.
// Algorithm ported from ethereum-blockies-base64 1.0.2 by MyCrypto (MIT):
// https://github.com/MyCryptoHQ/ethereum-blockies-base64
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

const PREFIX = 'onekey-avatar://blockie/v1/';
const DATABASE = 'onekey-native-list-avatar-v1';
const MAX_DISK_BYTES = 32 * 1024 * 1024;
const MAX_DISK_ENTRIES = 2048;
const MAX_CONCURRENT_LOADS = 2;
const requests = new Map();
const images = new Map();
const jobs = new Map();
const queue = [];
let activeLoads = 0;
let databasePromise;
// OneKey patch: persistence never occupies either display-load slot. Pending bytes
// bridge the last-lease/reacquire window until a bounded background write finishes.
const MAX_PENDING_WRITES = 32;
const MAX_PENDING_BYTES = 4 * 1024 * 1024;
const WRITE_DEADLINE_MS = 32;
const READ_BUDGET_MS = 8;
const MAX_PENDING_READS = 2;
const DISK_IDLE_MS = 100;
const pendingBlobs = new Map();
const diskQueue = [];
const touches = new Map();
const priorities = new Map();
let pendingBytes = 0;
let writing = false;
let diskTimer;
let lastAcquire = 0;
let pendingReads = 0;

function diskIsIdle() {
  return (
    !activeLoads &&
    !queue.length &&
    !pendingReads &&
    Date.now() - lastAcquire >= DISK_IDLE_MS
  );
}

function scheduleDiskWork() {
  if (
    writing ||
    diskTimer !== undefined ||
    activeLoads ||
    queue.length ||
    pendingReads ||
    (!diskQueue.length && !touches.size)
  )
    return;
  const delay = Math.max(0, DISK_IDLE_MS - (Date.now() - lastAcquire));
  diskTimer = setTimeout(() => {
    diskTimer = undefined;
    void drainDiskQueue();
  }, delay);
}

function persistLater(uri, blob) {
  if (pendingBlobs.has(uri) || blob.size > MAX_PENDING_BYTES) return;
  // OneKey patch: a long scroll retains the most recent bounded write window,
  // rather than filling it once and dropping every later result. Never evict I/O in flight.
  // if (pendingBlobs.size >= MAX_PENDING_WRITES || pendingBytes + blob.size > MAX_PENDING_BYTES) return;
  while (
    pendingBlobs.size >= MAX_PENDING_WRITES ||
    pendingBytes + blob.size > MAX_PENDING_BYTES
  ) {
    const oldest = diskQueue.shift();
    if (!oldest) return;
    if (pendingBlobs.get(oldest.uri) === oldest.blob)
      pendingBlobs.delete(oldest.uri);
    pendingBytes -= oldest.blob.size;
  }
  pendingBlobs.set(uri, blob);
  pendingBytes += blob.size;
  diskQueue.push({ uri, blob });
  scheduleDiskWork();
}

async function drainDiskQueue() {
  // OneKey patch: a momentary empty load queue is not a scroll-idle window.
  // if (writing || activeLoads || queue.length) return;
  if (writing) return;
  if (!diskIsIdle()) {
    scheduleDiskWork();
    return;
  }
  writing = true;
  try {
    if (diskQueue.length) {
      const { uri, blob } = diskQueue.shift();
      let result;
      try {
        result = await writeDisk(uri, blob);
      } catch {
        /* Persistence remains best-effort. */
      }
      if (result === 'deferred') {
        diskQueue.unshift({ uri, blob });
      } else {
        if (pendingBlobs.get(uri) === blob) pendingBlobs.delete(uri);
        pendingBytes -= blob.size;
        const image = images.get(uri);
        if (result === true && image?.blob === blob) image.persisted = true;
      }
    } else if (touches.size) {
      await flushTouches();
    }
  } finally {
    writing = false;
    scheduleDiskWork();
  }
}

// OneKey patch: abort is a request, not a bounded browser store-lock release.
// Display reads have their own budget below, even if this transaction is committing.
function cacheWriteDeadline(transaction, resolve) {
  let timer;
  const check = () => {
    if (
      !activeLoads &&
      !queue.length &&
      Date.now() - lastAcquire >= DISK_IDLE_MS
    ) {
      timer = setTimeout(check, WRITE_DEADLINE_MS);
      return;
    }
    try {
      transaction.abort();
    } catch {
      /* The browser already started committing. */
    }
  };
  timer = setTimeout(check, WRITE_DEADLINE_MS);
  const finish = (success) => {
    clearTimeout(timer);
    resolve(success);
  };
  transaction.oncomplete = () => finish(true);
  transaction.onabort = () => finish(false);
}

function touchLater(uri) {
  touches.delete(uri);
  touches.set(uri, Date.now());
  if (touches.size > 128) touches.delete(touches.keys().next().value);
  // OneKey patch: touches share the same idle gate and writer as persistence.
  // if (touchTimer === undefined) touchTimer = setTimeout(flushTouches, 250);
  scheduleDiskWork();
}

async function flushTouches() {
  const database = await openDatabase();
  // Opening the database can yield across a new acquire; recheck before starting I/O.
  if (!diskIsIdle()) return;
  const batch = [...touches];
  touches.clear();
  if (database && batch.length)
    await new Promise((resolve) => {
      try {
        const transaction = database.transaction('images', 'readwrite');
        cacheWriteDeadline(transaction, resolve);
        const store = transaction.objectStore('images');
        batch.forEach(([uri, accessed]) => {
          const request = store.get(uri);
          request.onsuccess = () => {
            if (request.result)
              store.put({
                ...request.result,
                accessed: Math.max(request.result.accessed || 0, accessed),
              });
          };
        });
      } catch {
        resolve(false);
      }
    });
}

function jobPriority(job) {
  let priority = 2;
  job.ids.forEach((id) => {
    priority = Math.min(priority, priorities.get(id) ?? 2);
  });
  return priority;
}

function openDatabase() {
  if (!databasePromise) {
    databasePromise = new Promise((resolve, reject) => {
      const request = indexedDB.open(DATABASE, 1);
      request.onupgradeneeded = () => {
        const store = request.result.createObjectStore('images', {
          keyPath: 'uri',
        });
        store.createIndex('accessed', 'accessed');
        request.result.createObjectStore('metadata');
      };
      let blocked = false;
      request.onsuccess = () => {
        const database = request.result;
        if (blocked) {
          database.close();
          return;
        }
        database.onversionchange = () => {
          database.close();
          databasePromise = undefined;
        };
        resolve(database);
      };
      request.onerror = () => reject(request.error);
      request.onblocked = () => {
        blocked = true;
        reject(new Error('Avatar cache database is blocked'));
      };
    }).catch(() => undefined);
  }
  return databasePromise;
}

async function readDisk(uri) {
  // OneKey patch: the cache is optional. A slow read must not retain a display
  // load slot while abort/commit waits on browser I/O. Keep unfinished reads
  // separately bounded, including an unresolved shared database open.
  if (pendingReads >= MAX_PENDING_READS) return undefined;
  pendingReads += 1;
  return new Promise((resolve) => {
    let transaction;
    let blob;
    let settled = false;
    let finished = false;
    let abortRequested = false;
    const finish = (success) => {
      if (finished) return;
      finished = true;
      pendingReads -= 1;
      clearTimeout(timer);
      if (!settled) {
        settled = true;
        if (success && blob) touchLater(uri);
        resolve(success ? blob : undefined);
      }
      scheduleDiskWork();
    };
    const timeout = () => {
      if (finished) return;
      if (!settled) {
        settled = true;
        resolve(undefined);
      }
      // Do not release pendingReads here: an aborted IDB transaction can still
      // hold its store lock for hundreds of milliseconds before onabort arrives.
      if (transaction && !abortRequested) {
        abortRequested = true;
        try {
          transaction.abort();
        } catch {
          /* Already committing; ignore late callbacks. */
        }
      }
    };
    const timer = setTimeout(timeout, READ_BUDGET_MS);
    Promise.resolve()
      .then(openDatabase)
      .then((database) => {
        if (settled || !database) {
          finish(false);
          return;
        }
        try {
          transaction = database.transaction('images', 'readonly');
          transaction.oncomplete = () => finish(true);
          transaction.onabort = () => finish(false);
          transaction.onerror = timeout;
          const request = transaction.objectStore('images').get(uri);
          request.onsuccess = () => {
            if (settled) return;
            const record = request.result;
            if (
              record?.blob instanceof Blob &&
              record.blob.type === 'image/png' &&
              record.blob.size <= MAX_DISK_BYTES
            )
              blob = record.blob;
            // A completed readonly get can serve display before the transaction ends.
            // Its physical permit still belongs to oncomplete/onabort.
            settled = true;
            if (blob) touchLater(uri);
            resolve(blob);
          };
        } catch {
          if (transaction) timeout();
          else finish(false);
        }
      })
      .catch(() => finish(false));
  });
}

async function writeDisk(uri, blob) {
  const database = await openDatabase();
  if (!database || blob.size > MAX_DISK_BYTES) return false;
  if (!diskIsIdle()) return 'deferred';
  return new Promise((resolve) => {
    try {
      const transaction = database.transaction(
        ['images', 'metadata'],
        'readwrite'
      );
      cacheWriteDeadline(transaction, resolve);
      const store = transaction.objectStore('images');
      const metadata = transaction.objectStore('metadata');
      const previous = store.get(uri);
      previous.onsuccess = () => {
        const counter = metadata.get('size');
        counter.onsuccess = () => {
          const size = counter.result || { bytes: 0, count: 0 };
          size.bytes += blob.size - (previous.result?.blob?.size || 0);
          size.count += previous.result ? 0 : 1;
          store.put({ uri, blob, accessed: Date.now() });
          const save = () => metadata.put(size, 'size');
          if (size.bytes <= MAX_DISK_BYTES && size.count <= MAX_DISK_ENTRIES) {
            save();
            return;
          }
          const cursor = store.index('accessed').openCursor();
          cursor.onsuccess = () => {
            const item = cursor.result;
            if (
              !item ||
              (size.bytes <= MAX_DISK_BYTES && size.count <= MAX_DISK_ENTRIES)
            ) {
              save();
              return;
            }
            if (item.value.uri !== uri) {
              size.bytes -= item.value.blob.size;
              size.count -= 1;
              item.delete();
            }
            item.continue();
          };
        };
      };
      // OneKey patch: completion/abort clears the deadline before releasing pending bytes.
      // transaction.oncomplete = transaction.onerror = transaction.onabort = () => resolve();
    } catch {
      resolve();
    }
  });
}

// PRNG and HSL conversion adapted from ethereum-blockies-base64 1.0.2 (MIT), MyCrypto.
// https://github.com/MyCryptoHQ/ethereum-blockies-base64/blob/master/src/main.js
// https://github.com/MyCryptoHQ/ethereum-blockies-base64/blob/master/src/hsl2rgb.js
// Preserve signed shifts, color order, and RGB rounding to match V1's decoded pixels.
async function generateBlob(seed) {
  const state = [0, 0, 0, 0];
  for (let i = 0; i < seed.length; i += 1) {
    state[i % 4] = (state[i % 4] << 5) - state[i % 4] + seed.charCodeAt(i);
  }
  const rand = () => {
    const t = state[0] ^ (state[0] << 11);
    state[0] = state[1];
    state[1] = state[2];
    state[2] = state[3];
    state[3] = state[3] ^ (state[3] >> 19) ^ t ^ (t >> 8);
    return (state[3] >>> 0) / ((1 << 31) >>> 0);
  };
  const hue = (p, q, value) => {
    let t = value;
    if (t < 0) t += 1;
    if (t > 1) t -= 1;
    if (t < 1 / 6) return p + (q - p) * 6 * t;
    if (t < 1 / 2) return q;
    if (t < 2 / 3) return p + (q - p) * (2 / 3 - t) * 6;
    return p;
  };
  const color = () => {
    const h = Math.floor(rand() * 360) / 360;
    const s = (rand() * 60 + 40) / 100;
    const l = ((rand() + rand() + rand() + rand()) * 25) / 100;
    const q = l < 0.5 ? l * (1 + s) : l + s - l * s;
    const p = 2 * l - q;
    const rgb =
      s === 0
        ? [l, l, l]
        : [hue(p, q, h + 1 / 3), hue(p, q, h), hue(p, q, h - 1 / 3)];
    return `rgb(${rgb.map((value) => Math.round(value * 255)).join(',')})`;
  };
  const foreground = color();
  const background = color();
  const spot = color();
  const canvas = new OffscreenCanvas(128, 128);
  const context = canvas.getContext('2d');
  if (!context) throw new Error('Avatar canvas is unavailable');
  context.fillStyle = background;
  context.fillRect(0, 0, 128, 128);
  for (let row = 0; row < 8; row += 1) {
    for (let column = 0; column < 4; column += 1) {
      const value = Math.floor(rand() * 2.3);
      if (value === 0) continue;
      context.fillStyle = value === 1 ? foreground : spot;
      context.fillRect(column * 16, row * 16, 16, 16);
      context.fillRect((7 - column) * 16, row * 16, 16, 16);
    }
  }
  return canvas.convertToBlob({ type: 'image/png' });
}

function release(id) {
  const uri = requests.get(id);
  requests.delete(id);
  priorities.delete(id);
  const image = images.get(uri);
  image?.references.delete(id);
  if (image && image.references.size === 0) {
    URL.revokeObjectURL(image.url);
    images.delete(uri);
  }
  const job = jobs.get(uri);
  job?.ids.delete(id);
  if (job && job.ids.size === 0 && !job.active) {
    jobs.delete(uri);
    const index = queue.indexOf(job);
    if (index !== -1) queue.splice(index, 1);
  }
}

async function runJob(job) {
  // OneKey patch: reuse generated bytes even if the last UI lease was released
  // while persistence is still in flight; these bytes already passed generation.
  const pending = pendingBlobs.get(job.uri);
  let blob = pending ?? (await readDisk(job.uri));
  let persisted = !!blob && !pending;
  if (blob && !pending) {
    try {
      const bitmap = await createImageBitmap(blob);
      const valid = bitmap.width === 128 && bitmap.height === 128;
      bitmap.close();
      if (!valid) blob = undefined;
    } catch {
      blob = undefined;
    }
  }
  if (!blob && job.ids.size) {
    persisted = false;
    blob = await generateBlob(job.seed);
    // OneKey patch: notify clients and free the load slot without awaiting I/O.
    // await writeDisk(job.uri, blob);
    persistLater(job.uri, blob);
  }
  if (!blob || !job.ids.size) return;
  const image = {
    url: URL.createObjectURL(blob),
    blob,
    persisted,
    references: new Set(),
  };
  images.set(job.uri, image);
  job.ids.forEach((id) => {
    if (requests.get(id) !== job.uri) return;
    image.references.add(id);
    postMessage({ type: 'resolved', id, url: image.url });
  });
  if (!image.references.size) {
    URL.revokeObjectURL(image.url);
    images.delete(job.uri);
  }
}

function pump() {
  while (activeLoads < MAX_CONCURRENT_LOADS && queue.length) {
    // OneKey patch: queued leases can be promoted without restarting their load.
    // const job = queue.shift();
    let next = 0;
    for (let index = 1; index < queue.length; index += 1) {
      if (jobPriority(queue[index]) < jobPriority(queue[next])) next = index;
    }
    const [job] = queue.splice(next, 1);
    if (jobs.get(job.uri) !== job || !job.ids.size) continue;
    job.active = true;
    activeLoads += 1;
    runJob(job)
      .catch(() => {
        job.ids.forEach((id) => {
          if (requests.get(id) === job.uri) postMessage({ type: 'error', id });
        });
      })
      .finally(() => {
        if (jobs.get(job.uri) === job) jobs.delete(job.uri);
        activeLoads -= 1;
        pump();
        void drainDiskQueue();
      });
  }
}

onmessage = ({ data }) => {
  if (!data || !Number.isSafeInteger(data.id)) return;
  if (data.type === 'release') {
    release(data.id);
    return;
  }
  if (data.type === 'priority') {
    if (requests.has(data.id))
      priorities.set(
        data.id,
        data.priority === 0 ? 0 : data.priority === 1 ? 1 : 2
      );
    return;
  }
  if (data.type !== 'acquire') return;
  release(data.id);
  lastAcquire = Date.now();
  scheduleDiskWork();
  try {
    if (typeof data.uri !== 'string' || !data.uri.startsWith(PREFIX))
      throw new Error('Invalid avatar URI');
    const seed = decodeURIComponent(
      data.uri.slice(PREFIX.length)
    ).toLowerCase();
    if (!seed) throw new Error('Empty avatar seed');
    const uri = PREFIX + encodeURIComponent(seed);
    requests.set(data.id, uri);
    priorities.set(
      data.id,
      data.priority === 0 ? 0 : data.priority === 1 ? 1 : 2
    );
    const image = images.get(uri);
    if (image) {
      // Reacquiring a live Blob can retry best-effort persistence after queue pressure.
      if (!image.persisted) persistLater(uri, image.blob);
      image.references.add(data.id);
      postMessage({ type: 'resolved', id: data.id, url: image.url });
      return;
    }
    let job = jobs.get(uri);
    if (!job) {
      job = { uri, seed, ids: new Set(), active: false };
      jobs.set(uri, job);
      queue.push(job);
    }
    job.ids.add(data.id);
    pump();
  } catch {
    postMessage({ type: 'error', id: data.id });
  }
};
