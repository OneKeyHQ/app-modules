import assert from 'node:assert/strict';
import { createHash } from 'node:crypto';
import {
  existsSync,
  mkdirSync,
  mkdtempSync,
  rmSync,
  symlinkSync,
  utimesSync,
  writeFileSync,
} from 'node:fs';
import fsPromises from 'node:fs/promises';
import { syncBuiltinESMExports } from 'node:module';
import { tmpdir } from 'node:os';
import { join } from 'node:path';
import { test } from 'node:test';

import { FirmwareArtifactNode } from '@onekeyfe/react-native-range-downloader/node';

const expired = new Date('2000-01-01T00:00:00Z');
const input = {
  taskId: 'firmware-download',
  transactionId: 'transaction',
  artifactId: 'firmware',
  url: 'https://common.onekey-asset.com/widget.bin',
  route: { routeType: 'domain' },
  overallDeadlineSeconds: 5,
  maxBytes: 1024,
};
const firmware = Buffer.from('fixture firmware');
const zipFixtures = {
  empty: 'UEsFBgAAAAAAAAAAAAAAAAAAAAAAAA==',
  directoryOnly:
    'UEsDBBQAAAAAAAAAIQAAAAAAAAAAAAAAAAAJAAAAZmlybXdhcmUvUEsBAhQDFAAAAAAAAAAhAAAAAAAAAAAAAAAAAAkAAAAAAAAAAAAAAO1BAAAAAGZpcm13YXJlL1BLBQYAAAAAAQABADcAAAAnAAAAAAA=',
  firmware:
    'UEsDBBQAAAAAAAAAIQAAAAAAAAAAAAAAAAAJAAAAZmlybXdhcmUvUEsDBBQAAAAAAAAAIQCsFJnXEAAAABAAAAATAAAAZmlybXdhcmUvd2lkZ2V0LmJpbmFyY2hpdmUgZmlybXdhcmVQSwECFAMUAAAAAAAAACEAAAAAAAAAAAAAAAAACQAAAAAAAAAAAAAA7UEAAAAAZmlybXdhcmUvUEsBAhQDFAAAAAAAAAAhAKwUmdcQAAAAEAAAABMAAAAAAAAAAAAAAKSBJwAAAGZpcm13YXJlL3dpZGdldC5iaW5QSwUGAAAAAAIAAgB4AAAAaAAAAAAA',
};

function digest(data) {
  return createHash('sha256').update(data).digest('hex');
}

function deferred() {
  let resolve;
  const promise = new Promise((complete) => {
    resolve = complete;
  });
  return { promise, resolve };
}

function createStore(t, fetch) {
  const rootPath = mkdtempSync(join(tmpdir(), 'onekey-firmware-node-'));
  t.after(() => rmSync(rootPath, { recursive: true, force: true }));
  return { rootPath, store: new FirmwareArtifactNode({ rootPath, fetch }) };
}

function storeFile(root, data) {
  const sha256 = digest(data);
  const filePath = join(root, `${sha256}.bin`);
  writeFileSync(filePath, data);
  return { artifactRef: `fw:${sha256}`, filePath, sha256 };
}

function storePartial(root, data) {
  const expectedSha256 = digest(firmware);
  const transactionToken = digest(input.transactionId).slice(0, 16);
  const partialPath = join(
    root,
    `${expectedSha256}.${input.taskId}.${transactionToken}.partial`
  );
  writeFileSync(partialPath, data);
  return { partialPath, expectedSha256 };
}

function age(filePath) {
  utimesSync(filePath, expired, expired);
}

async function assertReadable(store, artifactRef, expected) {
  const { readerId, size } = await store.open(artifactRef);
  try {
    const bytes = await store.read({ readerId, offset: 0, length: size });
    assert.deepEqual(Buffer.from(bytes), expected);
  } finally {
    await store.close(readerId);
  }
}

test('coalesced calls validate their own limits and retain each successful lease', async (t) => {
  const fetchStarted = deferred();
  const finishFetch = deferred();
  let fetchCount = 0;
  const { store, rootPath } = createStore(t, async () => {
    fetchCount += 1;
    fetchStarted.resolve();
    await finishFetch.promise;
    return new Response(firmware);
  });
  const first = await store.createLease(input.transactionId);
  const second = await store.createLease(input.transactionId);
  const limit = await store.createLease(input.transactionId);
  const size = await store.createLease(input.transactionId);
  const firstDownload = store.download({ ...input, ...first });
  await fetchStarted.promise;
  const outcomes = Promise.allSettled([
    firstDownload,
    store.download({ ...input, ...second, expectedSize: firmware.length }),
    store.download({ ...input, ...limit, maxBytes: firmware.length - 1 }),
    store.download({ ...input, ...size, expectedSize: firmware.length + 1 }),
  ]);
  finishFetch.resolve();
  const [firstResult, secondResult, limitedResult, sizedResult] =
    await outcomes;
  assert.equal(fetchCount, 1);
  const sha256 = digest(firmware);
  const receipt = {
    artifactRef: `fw:${sha256}`,
    size: firmware.length,
    sha256,
    expectedSha256Verified: false,
  };
  assert.deepEqual(firstResult, { status: 'fulfilled', value: receipt });
  assert.deepEqual(secondResult, { status: 'fulfilled', value: receipt });
  assert.notEqual(firstResult.value, secondResult.value);
  for (const rejected of [limitedResult, sizedResult]) {
    assert.equal(rejected.status, 'rejected');
    assert.equal(rejected.reason.code, 'ARTIFACT_INTEGRITY_FAILED');
  }

  age(join(rootPath, `${sha256}.bin`));
  await store.releaseLease({ ...first, disposition: 'completed' });
  assert.deepEqual(await store.sweepOrphans(), { deletedFiles: 0, deletedBytes: 0 });
  await assertReadable(store, receipt.artifactRef, firmware);
  await store.releaseLease({ ...second, disposition: 'completed' });
  assert.deepEqual(await store.sweepOrphans(), {
    deletedFiles: 1,
    deletedBytes: firmware.length,
  });
});

test('a lease acquired while a sweep is listing the cache is honored', async (t) => {
  const { store, rootPath } = createStore(t);
  const { artifactRef, filePath } = storeFile(rootPath, firmware);
  age(filePath);
  const listStarted = deferred();
  const finishList = deferred();
  const readdir = fsPromises.readdir;
  const mock = t.mock.method(fsPromises, 'readdir', async (directory, options) => {
    if (directory === rootPath) {
      listStarted.resolve();
      await finishList.promise;
    }
    return readdir(directory, options);
  });
  syncBuiltinESMExports();
  t.after(() => {
    mock.mock.restore();
    syncBuiltinESMExports();
  });

  const sweep = store.sweepOrphans();
  await listStarted.promise;
  try {
    const lease = await store.createLease(input.transactionId);
    await store.retain({ ...lease, artifactRef });
  } finally {
    finishList.resolve();
  }
  assert.deepEqual(await sweep, { deletedFiles: 0, deletedBytes: 0 });
  await assertReadable(store, artifactRef, firmware);
});

test('releasing a lease preserves cancellation while other leases or downloads remain', async (t) => {
  const fetchStarted = deferred();
  const finishFetch = deferred();
  const { store } = createStore(t, async () => {
    fetchStarted.resolve();
    await finishFetch.promise;
    return new Response(firmware);
  });
  const first = await store.createLease(input.transactionId);
  const second = await store.createLease(input.transactionId);
  const downloads = Promise.allSettled([
    store.download({ ...input, ...first }),
    store.download({ ...input, ...second }),
  ]);
  const cancelled = { code: 'ARTIFACT_CANCELLED' };
  await fetchStarted.promise;
  try {
    await store.cancelDownloads(input.transactionId);
    await store.releaseLease({ ...first, disposition: 'safeCancelled' });
    await assert.rejects(store.download({ ...input, ...second }), cancelled);
    await store.releaseLease({ ...second, disposition: 'safeCancelled' });

    const duringDownload = await store.createLease(input.transactionId);
    await assert.rejects(store.download({ ...input, ...duringDownload }), cancelled);
    await store.releaseLease({ ...duringDownload, disposition: 'safeCancelled' });
  } finally {
    finishFetch.resolve();
  }
  for (const result of await downloads) {
    assert.equal(result.status, 'rejected');
    assert.equal(result.reason.code, cancelled.code);
  }
  const afterDownload = await store.createLease(input.transactionId);
  const receipt = await store.download({ ...input, ...afterDownload });
  await assertReadable(store, receipt.artifactRef, firmware);
});

test('retain and cache reuse wait for a sweep whose unlink has already started', async (t) => {
  let fetchCount = 0;
  const { store, rootPath } = createStore(t, async () => {
    fetchCount += 1;
    return new Response(firmware);
  });
  const { artifactRef, filePath, sha256 } = storeFile(rootPath, firmware);
  age(filePath);
  const resolvePath = store.resolveArtifactPath.bind(store);
  const readBeforeUnlink = t.mock.method(store, 'resolveArtifactPath', resolvePath);
  const unlinkStarted = deferred();
  const finishUnlink = deferred();
  const rm = fsPromises.rm;
  const mock = t.mock.method(fsPromises, 'rm', async (target, options) => {
    if (target === filePath) {
      unlinkStarted.resolve();
      await finishUnlink.promise;
    }
    return rm(target, options);
  });
  syncBuiltinESMExports();
  t.after(() => {
    mock.mock.restore();
    syncBuiltinESMExports();
  });

  const sweep = store.sweepOrphans();
  await unlinkStarted.promise;
  const explicitLease = await store.createLease(input.transactionId);
  const downloadLease = await store.createLease(input.transactionId);
  const retain = assert.rejects(store.retain({ ...explicitLease, artifactRef }), {
    code: 'ARTIFACT_INTEGRITY_FAILED',
  });
  const download = store.download({ ...input, ...downloadLease, expectedSha256: sha256 });
  const outcomes = Promise.all([sweep, retain, download]);
  try {
    assert.equal(readBeforeUnlink.mock.callCount(), 0);
  } finally {
    finishUnlink.resolve();
  }
  const [swept, , receipt] = await outcomes;
  assert.deepEqual(swept, { deletedFiles: 1, deletedBytes: firmware.length });
  assert.equal(fetchCount, 1);
  assert.equal(receipt.artifactRef, artifactRef);
  await assertReadable(store, artifactRef, firmware);
});

test('a complete partial with only a trusted SHA-256 is promoted without fetching', async (t) => {
  const { store, rootPath } = createStore(t, async () => {
    assert.fail('a matching complete partial should require no fetch');
  });
  const lease = await store.createLease(input.transactionId);
  const { partialPath, expectedSha256 } = storePartial(rootPath, firmware);
  const receipt = await store.download({ ...input, ...lease, expectedSha256 });
  assert.equal(receipt.expectedSha256Verified, true);
  assert.equal(receipt.size, firmware.length);
  assert.equal(existsSync(partialPath), false);
  await assertReadable(store, receipt.artifactRef, firmware);
});

for (const retryStatus of [200, 416]) {
  test(`a resumed HTTP 416 restarts once on the original deadline (HTTP ${retryStatus})`, async (t) => {
    const requests = [];
    const { store, rootPath } = createStore(t, async (_url, { headers, signal }) => {
      requests.push({ range: headers.Range, signal });
      const status = requests.length === 1 ? 416 : retryStatus;
      return new Response(status === 200 ? firmware : null, { status });
    });
    const lease = await store.createLease(input.transactionId);
    const { partialPath, expectedSha256 } = storePartial(
      rootPath,
      firmware.subarray(0, 4)
    );
    const download = store.download({ ...input, ...lease, expectedSha256 });
    if (retryStatus === 200) {
      const receipt = await download;
      assert.equal(receipt.expectedSha256Verified, true);
      await assertReadable(store, receipt.artifactRef, firmware);
    } else {
      await assert.rejects(download, { code: 'ARTIFACT_HTTP_416' });
    }
    assert.deepEqual(requests.map(({ range }) => range), ['bytes=4-', undefined]);
    assert.equal(requests[0].signal, requests[1].signal);
    assert.equal(existsSync(partialPath), false);
  });
}

test('legacy materialization requires a firmware file and accepts a normal directory entry', async (t) => {
  const { store, rootPath } = createStore(t);
  const lease = await store.createLease(input.transactionId);
  for (const fixture of [zipFixtures.empty, zipFixtures.directoryOnly]) {
    const { artifactRef } = storeFile(rootPath, Buffer.from(fixture, 'base64'));
    await assert.rejects(
      store.materialize({ ...lease, archiveArtifactRef: artifactRef }),
      { code: 'ARTIFACT_ARCHIVE_INVALID', message: /no materializable files/ }
    );
  }
  const { artifactRef } = storeFile(rootPath, Buffer.from(zipFixtures.firmware, 'base64'));
  const [entry] = await store.materialize({ ...lease, archiveArtifactRef: artifactRef });
  assert.equal(entry.entryName, 'firmware/widget.bin');
  assert.equal(entry.receipt.expectedSha256Verified, false);
  await assertReadable(store, entry.receipt.artifactRef, Buffer.from('archive firmware'));
});

test('orphan sweep removes only aged managed scratch and preserves active materialization', async (t) => {
  const { store, rootPath } = createStore(t);
  const active = deferred();
  const continueExtraction = deferred();
  const extractArchive = store.extractArchive.bind(store);
  t.mock.method(store, 'extractArchive', async (archive, scratchPath, requirements) => {
    age(scratchPath);
    active.resolve(scratchPath);
    await continueExtraction.promise;
    return extractArchive(archive, scratchPath, requirements);
  });
  const newScratch = (suffix) =>
    join(rootPath, `archive-00000000-0000-4000-8000-00000000000${suffix}`);
  const orphan = newScratch('1');
  const fresh = newScratch('2');
  const malformed = newScratch('3.extra');
  const regularFile = newScratch('4');
  const symbolicLink = newScratch('5');
  for (const directory of [orphan, fresh, malformed]) mkdirSync(directory);
  writeFileSync(join(orphan, '0.entry'), firmware);
  writeFileSync(regularFile, firmware);
  symlinkSync(malformed, symbolicLink, 'dir');
  for (const filePath of [orphan, malformed, regularFile]) age(filePath);

  const lease = await store.createLease(input.transactionId);
  const { artifactRef, filePath } = storeFile(rootPath, Buffer.from(zipFixtures.firmware, 'base64'));
  age(filePath);
  const materialization = store.materialize({ ...lease, archiveArtifactRef: artifactRef });
  const activePath = await active.promise;
  try {
    assert.deepEqual(await store.sweepOrphans(), {
      deletedFiles: 1,
      deletedBytes: firmware.length,
    });
    assert.equal(existsSync(orphan), false);
    for (const entry of [fresh, malformed, regularFile, symbolicLink, activePath, filePath]) {
      assert.equal(existsSync(entry), true, entry);
    }
  } finally {
    continueExtraction.resolve();
  }
  await materialization;
  assert.equal(existsSync(activePath), false);
});
