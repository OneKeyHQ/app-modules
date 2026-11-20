import assert from 'node:assert/strict';
import { execFileSync } from 'node:child_process';
import { EventEmitter } from 'node:events';
import { createHash } from 'node:crypto';
import fs, {
  cpSync,
  existsSync,
  mkdirSync,
  mkdtempSync,
  readFileSync,
  rmSync,
  truncateSync,
  writeFileSync,
} from 'node:fs';
import https from 'node:https';
import { createRequire } from 'node:module';
import { tmpdir } from 'node:os';
import { dirname, join } from 'node:path';
import { Readable } from 'node:stream';
import { test } from 'node:test';
import { setImmediate as nextTurn } from 'node:timers/promises';
import { fileURLToPath } from 'node:url';

import AdmZip from 'adm-zip';
import {
  createCleartextMessage,
  generateKey,
  readPrivateKey,
  sign,
} from 'openpgp';
import {
  BundleUpdateNode,
  DIFF_DOWNLOAD_DIR_NAME,
  DIFF_STAGING_DIR_NAME,
  downloadNodeFile,
} from '@onekeyfe/react-native-bundle-update/node';

const appVersion = '6.8.0';
const bundleVersion = '42';
const downloadUrl = 'https://fixtures.invalid/full.zip';
const diffDownloadUrl = 'https://fixtures.invalid/diff.zip';
const outputName = `${appVersion}-${bundleVersion}`;
const keys = generateKey({
  type: 'ecc',
  curve: 'p256',
  userIDs: [{ name: 'OTA Test Fixture' }],
});

function hash(algorithm, value) {
  return createHash(algorithm).update(value).digest('hex');
}

function zip(files) {
  const archive = new AdmZip();
  for (const [name, data] of Object.entries(files)) {
    archive.addFile(name, Buffer.from(data));
  }
  return archive.toBuffer();
}

async function signDigest(fileName, data) {
  const { privateKey } = await keys;
  return sign({
    message: await createCleartextMessage({
      text: JSON.stringify({
        fileName,
        appType: 'electron',
        appVersion,
        bundleVersion,
        sha256: hash('sha256', data),
      }),
    }),
    signingKeys: await readPrivateKey({ armoredKey: privateKey }),
  });
}

async function fixture(
  t,
  {
    base = true,
    transport,
    diffStatus = 200,
    logger,
    nativeVersion = appVersion,
  } = {}
) {
  const root = mkdtempSync(join(tmpdir(), 'bundle-update-node-'));
  t.after(() => rmSync(root, { recursive: true, force: true }));
  const builtInBuildDir = join(root, 'built-in', 'build');
  const bundleDir = join(root, 'onekey-bundle', outputName);
  const diffDir = join(root, 'onekey-bundle-download', DIFF_DOWNLOAD_DIR_NAME);
  const metadataPath = join(bundleDir, 'metadata.json');

  const html = '<html>version 42</html>';
  const asset = 'unchanged asset';
  const nestedMetadata = JSON.stringify({ language: 'en' });
  const metadata = JSON.stringify({
    'index.html': hash('sha512', html),
    'assets/metadata.json': hash('sha512', nestedMetadata),
    'assets/reused.txt': hash('sha512', asset),
  });
  const manifest = JSON.stringify({
    schemaVersion: 1,
    appType: 'electron',
    appVersion,
    baseAppVersion: appVersion,
    targetBundleVersion: bundleVersion,
    hashAlgorithm: 'sha512',
    targetMetadataSha256: hash('sha256', metadata),
    changedFiles: {
      'index.html': hash('sha512', html),
      'assets/metadata.json': hash('sha512', nestedMetadata),
    },
    reusedFiles: { 'assets/reused.txt': hash('sha512', asset) },
    deletedFiles: [],
  });

  const full = zip({
    'metadata.json': metadata,
    'build/index.html': html,
    'build/assets/metadata.json': nestedMetadata,
    'build/assets/reused.txt': asset,
  });
  const diff = zip({
    'diff-manifest.json': manifest,
    'metadata.json': metadata,
    'build/index.html': html,
    'build/assets/metadata.json': nestedMetadata,
  });
  if (base) {
    const baseAsset = join(builtInBuildDir, 'assets/reused.txt');
    mkdirSync(dirname(baseAsset), { recursive: true });
    writeFileSync(baseAsset, asset);
  }

  const calls = [];
  const progress = [];
  let installed;
  let fallbacks = [];
  let restarts = 0;
  const { publicKey } = await keys;
  const options = {
    userDataDir: () => root,
    appVersion: () => nativeVersion,
    buildNumber: () => '1',
    builtInBuildDir: () => builtInBuildDir,
    publicKey,
    logger,
    restart: () => {
      restarts += 1;
    },
    store: {
      getUpdateBundleData: () => installed,
      getFallbackUpdateBundleData: () => fallbacks,
      setUpdateBundleData: (data) => {
        installed = data;
      },
      setFallbackUpdateBundleData: (data) => {
        fallbacks = data;
      },
      setNativeBuildNumber: () => {},
      setNativeVersion: () => {},
      clearUpdateBundleData: () => {
        installed = undefined;
      },
    },
    onProgress: (event) => progress.push(event),
    download: (request) =>
      downloadNodeFile({
        ...request,
        transport: async (url, headers, signal) => {
          calls.push(url);
          const data = url === downloadUrl ? full : diff;
          if (transport) return transport(url, headers, signal, data);
          return fixtureResponse(
            url,
            url === diffDownloadUrl ? diffStatus : 200,
            data
          );
        },
      }),
  };

  const params = {
    latestVersion: appVersion,
    bundleVersion,
    downloadUrl,
    sha256: hash('sha256', full),
    fileSize: full.length,
    signature: await signDigest('metadata.json', metadata),
    diffDownloadUrl,
    diffSha256: hash('sha256', diff).toUpperCase(),
    diffFileSize: diff.length,
    diffSignature: await signDigest('diff-manifest.json', manifest),
  };

  return {
    root,
    bundleDir,
    diffDir,
    metadataPath,
    metadata,
    asset,
    nestedMetadata,
    calls,
    progress,
    params,
    store: options.store,
    makeClient: () => new BundleUpdateNode(options),
    getInstalled: () => installed,
    getRestarts: () => restarts,
  };
}

function fixtureResponse(url, statusCode, data) {
  const response = Readable.from([data]);
  response.statusCode = statusCode;
  response.headers = { 'content-length': String(data.length) };
  return { url, response };
}

function hasCode(code, step) {
  return (error) => {
    assert.equal(error.data?.updateError?.code, code);
    assert.equal(error.data?.updateError?.step, step);
    return true;
  };
}

test('ESM and CommonJS can load the Node updater without a React Native runtime', () => {
  const require = createRequire(import.meta.url);
  const commonjs = require('@onekeyfe/react-native-bundle-update/node');
  assert.equal(typeof BundleUpdateNode, 'function');
  assert.equal(typeof commonjs.BundleUpdateNode, 'function');
  assert.equal(commonjs.DIFF_STAGING_DIR_NAME, DIFF_STAGING_DIR_NAME);
});

test('downloads, verifies and installs the signed full archive', async (t) => {
  const f = await fixture(t);
  const client = f.makeClient();
  const { diffDownloadUrl: _diffUrl, ...fullParams } = f.params;
  const result = await client.downloadBundle(fullParams);
  await client.verifyBundle(result);
  await client.downloadBundleASC(result);
  await client.verifyBundleASC(result);
  await client.installBundle(result);

  assert.equal(readFileSync(f.metadataPath, 'utf8'), f.metadata);
  assert.equal(
    readFileSync(join(f.bundleDir, 'build/assets/metadata.json'), 'utf8'),
    f.nestedMetadata
  );
  assert.equal(f.getInstalled().bundleVersion, bundleVersion);
  assert.equal(f.getRestarts(), 1);
  assert.ok(
    f.progress.some(
      (event) => event.bundleVersion === bundleVersion && event.percent === 100
    )
  );
  assert.ok(f.calls.every((url) => url === downloadUrl));
});

test('reconstructs from built-in assets and verifies the persisted trust anchor after restart', async (t) => {
  const f = await fixture(t);
  const client = f.makeClient();
  const result = await client.downloadBundle(f.params);
  await client.verifyBundle(result);
  const abandoned = join(
    f.root,
    'onekey-bundle',
    DIFF_STAGING_DIR_NAME,
    'abandoned'
  );
  mkdirSync(abandoned, { recursive: true });

  await f.makeClient().verifyBundleASC(result);
  assert.equal(existsSync(abandoned), false);
  assert.equal(
    readFileSync(join(f.bundleDir, 'build/assets/reused.txt'), 'utf8'),
    f.asset
  );
  await f.makeClient().verifyBundle({
    latestVersion: appVersion,
    bundleVersion,
    sha256: f.params.sha256,
  });
  assert.ok(f.calls.every((url) => url === diffDownloadUrl));
  assert.equal(
    readFileSync(join(f.bundleDir, 'build/assets/metadata.json'), 'utf8'),
    f.nestedMetadata
  );

  writeFileSync(join(f.bundleDir, 'build/assets/reused.txt'), 'tampered');
  await assert.rejects(
    client.installBundle(result),
    hasCode('INTEGRITY_FAILED', 'install')
  );
  assert.equal(f.getInstalled(), undefined);
});

test('persists a failed full fallback and resumes it after restart', async (t) => {
  let canDownloadFull = false;
  const f = await fixture(t, {
    base: false,
    transport: async (url, _headers, _signal, data) => {
      if (url === downloadUrl && !canDownloadFull) {
        return fixtureResponse(url, 403, data);
      }
      return fixtureResponse(url, 200, data);
    },
  });

  const client = f.makeClient();
  const result = await client.downloadBundle(f.params);
  await assert.rejects(
    client.verifyBundleASC(result),
    hasCode('HTTP_ERROR', 'verifyASC')
  );
  const descriptor = join(f.diffDir, `${outputName}.diff.zip.meta.json`);
  assert.equal(
    JSON.parse(readFileSync(descriptor, 'utf8')).fallbackRequired,
    true
  );
  assert.equal(existsSync(join(f.diffDir, `${outputName}.diff.zip`)), false);

  canDownloadFull = true;
  await f.makeClient().verifyBundleASC({
    latestVersion: appVersion,
    bundleVersion,
    sha256: f.params.sha256,
    signature: f.params.signature,
  });
  assert.equal(
    readFileSync(join(f.bundleDir, 'build/assets/reused.txt'), 'utf8'),
    f.asset
  );
});

test('rejects a replayed metadata signature as the diff signature without full fallback', async (t) => {
  const f = await fixture(t);
  const client = f.makeClient();
  const result = await client.downloadBundle({
    ...f.params,
    diffSignature: f.params.signature,
  });

  await assert.rejects(
    client.verifyBundleASC(result),
    hasCode('INTEGRITY_FAILED', 'verifyASC')
  );
  assert.equal(existsSync(f.bundleDir), false);
  assert.ok(f.calls.every((url) => url === diffDownloadUrl));
});

test('clearDownload cancels a live transport before removing the download directory', async (t) => {
  let begin;
  const transportEntered = new Promise((resolve) => {
    begin = resolve;
  });
  const f = await fixture(t, {
    transport: async (_url, _headers, signal) =>
      new Promise((_resolve, reject) => {
        signal.addEventListener(
          'abort',
          () => reject(new DOMException('Cancelled', 'AbortError')),
          { once: true }
        );
        begin();
      }),
  });
  const client = f.makeClient();
  const result = client.downloadBundle(f.params);
  await transportEntered;
  const verification = assert.rejects(result, hasCode('CANCELLED', 'download'));
  await client.clearDownload();
  await verification;
  assert.equal(client.isDownloading, false);
  assert.equal(existsSync(join(f.root, 'onekey-bundle-download')), false);
});

test('shipped declarations typecheck in strict ESM and CommonJS NodeNext consumers', (t) => {
  const require = createRequire(import.meta.url);
  const packageDir = fileURLToPath(new URL('../../', import.meta.url));
  const temp = mkdtempSync(join(tmpdir(), 'bundle-update-nodenext-'));
  t.after(() => rmSync(temp, { recursive: true, force: true }));

  const installed = join(
    temp,
    'node_modules/@onekeyfe/react-native-bundle-update'
  );
  mkdirSync(installed, { recursive: true });
  cpSync(join(packageDir, 'package.json'), join(installed, 'package.json'));
  cpSync(join(packageDir, 'lib'), join(installed, 'lib'), { recursive: true });
  // Keep unrelated dependencies from ancestor workspaces out of module resolution.
  const nodeTypes = dirname(require.resolve('@types/node/package.json'));
  const undiciTypes = dirname(
    require.resolve('undici-types/package.json', { paths: [nodeTypes] })
  );
  cpSync(nodeTypes, join(temp, 'node_modules/@types/node'), {
    recursive: true,
  });
  cpSync(undiciTypes, join(temp, 'node_modules/undici-types'), {
    recursive: true,
  });

  const esm = join(temp, 'consumer.mts');
  const commonjs = join(temp, 'consumer.cts');
  const usage = `
    declare const options: IBundleUpdateNodeOptions;
    const updater = new BundleUpdateNode(options);
    const progress: boolean = updater.isDownloading;
    const download: typeof downloadNodeFile = options.download ?? downloadNodeFile;
    const verify: Promise<void> = updater.verifyBundle(undefined);
  `;
  writeFileSync(
    esm,
    `import {
      BundleUpdateNode,
      downloadNodeFile,
      type IBundleUpdateNodeOptions,
    } from '@onekeyfe/react-native-bundle-update/node';\n${usage}`
  );
  writeFileSync(
    commonjs,
    `import api = require('@onekeyfe/react-native-bundle-update/node');
    const { BundleUpdateNode, downloadNodeFile } = api;
    type IBundleUpdateNodeOptions = api.IBundleUpdateNodeOptions;
    ${usage}`
  );

  execFileSync(
    process.execPath,
    [
      require.resolve('typescript/bin/tsc'),
      '--module',
      'NodeNext',
      '--lib',
      'ES2022',
      '--types',
      'node',
      '--strict',
      '--skipLibCheck',
      'false',
      '--verbatimModuleSyntax',
      '--noEmit',
      esm,
      commonjs,
    ],
    { cwd: temp, encoding: 'utf8', stdio: ['ignore', 'inherit', 'inherit'] }
  );
});

test('verifyBundle checks the full ZIP while install checks the extracted manifest', async (t) => {
  const f = await fixture(t);
  const client = f.makeClient();
  const result = await client.downloadBundle({
    ...f.params,
    diffDownloadUrl: undefined,
  });
  await client.verifyBundleASC(result);
  rmSync(join(f.bundleDir, 'build/assets/metadata.json'));

  await client.verifyBundle(result);
  await assert.rejects(
    client.installBundle(result),
    hasCode('INTEGRITY_FAILED', 'install')
  );
  assert.equal(f.getInstalled(), undefined);
});

test(
  'verifyASC and ordinary download join the same full fallback transfer',
  { timeout: 5000 },
  async (t) => {
    let notifyStarted;
    let releaseFull;
    const fullStarted = new Promise((resolve) => {
      notifyStarted = resolve;
    });
    const fullCanComplete = new Promise((resolve) => {
      releaseFull = resolve;
    });
    const f = await fixture(t, {
      base: false,
      transport: async (url, _headers, _signal, data) => {
        if (url === downloadUrl) {
          notifyStarted();
          await fullCanComplete;
        }
        return fixtureResponse(url, 200, data);
      },
    });
    const client = f.makeClient();
    const diff = await client.downloadBundle(f.params);
    const verify = client.verifyBundleASC(diff);
    await fullStarted;
    const download = client.downloadBundle(f.params);
    const duplicateVerify = client.verifyBundleASC(diff);
    releaseFull();

    const [result] = await Promise.all([download, verify, duplicateVerify]);
    assert.equal(result.diffSha256, undefined);
    assert.equal(f.calls.filter((url) => url === downloadUrl).length, 2);
    assert.equal(readFileSync(f.metadataPath, 'utf8'), f.metadata);
  }
);

test(
  'clearBundle waits for joined full fallback callers before removing managed files',
  { timeout: 5000 },
  async (t) => {
    let notifyStarted;
    let notifyAborted;
    let releaseTransport;
    const fullStarted = new Promise((resolve) => {
      notifyStarted = resolve;
    });
    const fullAborted = new Promise((resolve) => {
      notifyAborted = resolve;
    });
    const afterAbort = new Promise((resolve) => {
      releaseTransport = resolve;
    });
    const f = await fixture(t, {
      base: false,
      transport: async (url, _headers, signal, data) => {
        if (url === downloadUrl) {
          signal.addEventListener('abort', notifyAborted, { once: true });
          notifyStarted();
          await afterAbort;
          throw new DOMException('Cancelled', 'AbortError');
        }
        return fixtureResponse(url, 200, data);
      },
    });
    const client = f.makeClient();
    const diff = await client.downloadBundle(f.params);
    const verify = assert.rejects(
      client.verifyBundleASC(diff),
      hasCode('CANCELLED', 'verifyASC')
    );
    await fullStarted;
    const download = assert.rejects(
      client.downloadBundle(f.params),
      hasCode('CANCELLED', 'download')
    );
    const clearing = client.clearBundle();
    await fullAborted;
    assert.equal(existsSync(f.diffDir), true);
    assert.equal(client.isDownloading, true);
    releaseTransport();

    await Promise.all([verify, download, clearing]);
    assert.equal(f.calls.filter((url) => url === downloadUrl).length, 1);
    assert.equal(client.isDownloading, false);
    assert.equal(existsSync(join(f.root, 'onekey-bundle-download')), false);
    assert.equal(existsSync(join(f.root, 'onekey-bundle')), false);
  }
);

test('the default HTTPS transport classifies a malformed redirect location', async (t) => {
  const f = await fixture(t);
  t.mock.method(https, 'get', (_url, _options, callback) => {
    const response = Readable.from([]);
    response.statusCode = 302;
    response.headers = { location: 'https://fixtures.invalid:invalid-port/' };
    const request = new EventEmitter();
    request.setTimeout = () => request;
    queueMicrotask(() => callback(response));
    return request;
  });
  await assert.rejects(
    downloadNodeFile({
      url: downloadUrl,
      targetPath: join(f.root, 'archive.zip'),
      identity: 'redirect-fixture',
      expectedSha256: f.params.sha256,
    }),
    hasCode('SECURITY_REJECTED', 'download')
  );
});

test(
  'verifyASC persists retry state when joining an ordinary full download',
  { timeout: 5000 },
  async (t) => {
    let notifyStarted;
    let notifyFallback;
    let releaseFull;
    const fullStarted = new Promise((resolve) => {
      notifyStarted = resolve;
    });
    const fallbackEntered = new Promise((resolve) => {
      notifyFallback = resolve;
    });
    const fullCanFinish = new Promise((resolve) => {
      releaseFull = resolve;
    });
    let fullStatus = 403;
    const f = await fixture(t, {
      base: false,
      logger: {
        info: () => {},
        error: () => {},
        warn: (source) => {
          if (source === 'bundle-verifyASC') notifyFallback();
        },
      },
      transport: async (url, _headers, _signal, data) => {
        if (url === diffDownloadUrl) return fixtureResponse(url, 200, data);
        notifyStarted();
        await fullCanFinish;
        return fixtureResponse(url, fullStatus, data);
      },
    });
    const client = f.makeClient();
    const diff = await client.downloadBundle(f.params);
    const download = assert.rejects(
      client.downloadBundle({ ...f.params, diffDownloadUrl: undefined }),
      hasCode('HTTP_ERROR', 'download')
    );
    await fullStarted;
    const verify = assert.rejects(
      client.verifyBundleASC(diff),
      hasCode('HTTP_ERROR', 'verifyASC')
    );
    await fallbackEntered;

    const descriptor = join(f.diffDir, `${outputName}.diff.zip.meta.json`);
    assert.equal(
      JSON.parse(readFileSync(descriptor, 'utf8')).fallbackRequired,
      true
    );
    releaseFull();
    await Promise.all([download, verify]);
    assert.equal(f.calls.filter((url) => url === downloadUrl).length, 1);

    fullStatus = 200;
    await f.makeClient().verifyBundleASC({
      latestVersion: appVersion,
      bundleVersion,
      sha256: f.params.sha256,
      signature: f.params.signature,
    });
    assert.equal(readFileSync(f.metadataPath, 'utf8'), f.metadata);
  }
);

test('an existing stale full ZIP is replaced before download reports success', async (t) => {
  const f = await fixture(t);
  const client = f.makeClient();
  const fullArchive = join(client.getDownloadDir(), `${outputName}.zip`);
  writeFileSync(fullArchive, zip({ 'build/index.html': 'Previous release' }));

  await assert.rejects(
    client.verifyBundle(f.params),
    hasCode('INTEGRITY_FAILED', 'verifyPackage')
  );

  const result = await client.downloadBundle(f.params);
  assert.equal(result.diffSha256, undefined);
  assert.ok(f.calls.length > 0);
  assert.ok(f.calls.every((url) => url === downloadUrl));
  await client.verifyBundle(result);
  await client.verifyBundleASC(result);
  assert.equal(readFileSync(f.metadataPath, 'utf8'), f.metadata);

  const networkCalls = f.calls.length;
  await client.downloadBundle(f.params);
  assert.equal(f.calls.length, networkCalls);
});

test('diff package verification requires its archive and checks an existing target tree', async (t) => {
  const f = await fixture(t);
  const client = f.makeClient();
  const result = await client.downloadBundle(f.params);
  await client.verifyBundle(result);

  mkdirSync(f.bundleDir);
  await assert.rejects(
    client.verifyBundle(result),
    hasCode('PACKAGE_MISSING', 'verifyPackage')
  );
  rmSync(f.bundleDir, { recursive: true });

  await client.verifyBundleASC(result);
  await client.verifyBundle(result);
  rmSync(join(f.diffDir, `${outputName}.diff.zip`));
  await assert.rejects(
    client.verifyBundle(result),
    hasCode('PACKAGE_MISSING', 'verifyPackage')
  );
});

test('lists and prunes using the exact native version prefix', async (t) => {
  const nativeVersion = '6.8.0-beta.2';
  const f = await fixture(t, { nativeVersion });
  const client = f.makeClient();
  const current = `${nativeVersion}-41-diff`;
  const stale = '6.7.0-beta.2-41-diff';
  const extractRoot = dirname(f.bundleDir);
  const downloadRoot = client.getDownloadDir();
  for (const name of [current, stale]) {
    mkdirSync(join(extractRoot, name), { recursive: true });
  }
  mkdirSync(f.diffDir, { recursive: true });
  const artifacts = (prefix) => [
    join(downloadRoot, `${prefix}.zip`),
    join(downloadRoot, `${prefix}.zip.partial.progress.json`),
    join(f.diffDir, `${prefix}.diff.zip`),
    join(f.diffDir, `${prefix}.diff.zip.meta.json`),
  ];
  for (const artifact of [...artifacts(current), ...artifacts(stale)]) {
    writeFileSync(artifact, '');
  }
  assert.deepEqual(await client.listLocalBundles(), [
    { appVersion: nativeVersion, bundleVersion: '41-diff' },
  ]);
  assert.equal(await client.pruneStaleAppVersionBundles(), 1);
  assert.equal(existsSync(join(extractRoot, current)), true);
  for (const artifact of artifacts(current))
    assert.equal(existsSync(artifact), true);
  for (const artifact of artifacts(stale))
    assert.equal(existsSync(artifact), false);
});

test('retryable HTTP errors preserve the partial diff for the next download', async (t) => {
  let interrupted = false;
  let recovered = false;
  let resumed = false;
  const f = await fixture(t, {
    transport: async (url, headers, _signal, data) => {
      assert.equal(url, diffDownloadUrl);
      if (!headers.Range || headers.Range === 'bytes=0-0') {
        if (headers.Range || recovered) return fixtureResponse(url, 200, data);
        interrupted = true;
        const result = fixtureResponse(url, 200, data);
        result.response = Readable.from(
          (async function* () {
            yield data.subarray(0, 10);
            await nextTurn();
            throw Object.assign(new Error('Fixture transfer interrupted'), {
              code: 'ECONNRESET',
            });
          })()
        );
        result.response.statusCode = 200;
        result.response.headers = { 'content-length': String(data.length) };
        return result;
      }
      assert.equal(headers.Range, 'bytes=10-');
      if (!recovered) {
        const result = fixtureResponse(url, 503, Buffer.alloc(0));
        result.response.headers['retry-after'] = '0';
        return result;
      }
      resumed = true;
      const result = fixtureResponse(url, 206, data.subarray(10));
      result.response.headers['content-range'] =
        `bytes 10-${data.length - 1}/${data.length}`;
      return result;
    },
  });
  const client = f.makeClient();
  await assert.rejects(client.downloadBundle(f.params), (error) => {
    hasCode('HTTP_ERROR', 'download')(error);
    assert.equal(error.data.updateError.httpStatus, 503);
    return true;
  });
  assert.equal(interrupted, true);
  const partial = join(f.diffDir, `${outputName}.diff.zip.partial`);
  assert.equal(readFileSync(partial).length, 10);
  assert.equal(existsSync(`${partial}.progress.json`), true);
  recovered = true;
  const result = await f.makeClient().downloadBundle(f.params);
  assert.equal(resumed, true);
  assert.equal(result.diffDownloadUrl, diffDownloadUrl);
});

test('explicit extract cleanup reports a storage failure before clearing the active bundle', async (t) => {
  const f = await fixture(t);
  const client = f.makeClient();
  const active = { appVersion, bundleVersion, signature: f.params.signature };
  f.store.setUpdateBundleData(active);
  mkdirSync(f.bundleDir, { recursive: true });
  const originalRemove = fs.rmSync;
  const rm = t.mock.method(fs, 'rmSync', (target, options) => {
    if (target === dirname(f.bundleDir)) {
      throw Object.assign(new Error('Fixture directory busy'), {
        code: 'EBUSY',
      });
    }
    return originalRemove(target, options);
  });
  const storageFailure = hasCode('STORAGE_ERROR', 'cleanup');
  await assert.rejects(client.clearBundleExtract(), storageFailure);
  await assert.rejects(client.clearBundle(), storageFailure);
  await assert.rejects(client.clearAllJSBundleData(), storageFailure);
  assert.deepEqual(f.getInstalled(), active);
  assert.equal(existsSync(f.bundleDir), true);
  rm.mock.restore();
  await client.clearAllJSBundleData();
  assert.equal(existsSync(f.bundleDir), false);
  assert.equal(f.getInstalled(), undefined);
});

test('installs and commits the fallback list when evicted-directory cleanup fails', async (t) => {
  const f = await fixture(t);
  const client = f.makeClient();
  const result = await client.downloadBundle({
    ...f.params,
    diffDownloadUrl: undefined,
  });
  await client.verifyBundleASC(result);
  const previous = ['38', '39', '40', '41'].map((version) => ({
    appVersion,
    bundleVersion: version,
    signature: f.params.signature,
  }));
  f.store.setUpdateBundleData(previous[3]);
  f.store.setFallbackUpdateBundleData(previous.slice(0, 3));
  const evicted = join(dirname(f.bundleDir), `${appVersion}-38`);
  mkdirSync(evicted);
  const originalRemove = fs.rmSync;
  let stateAtEviction;
  t.mock.method(fs, 'rmSync', (target, options) => {
    if (target === evicted) {
      stateAtEviction = [
        f.getInstalled(),
        f.store.getFallbackUpdateBundleData(),
      ];
      throw Object.assign(new Error('Fixture directory busy'), {
        code: 'EBUSY',
      });
    }
    return originalRemove(target, options);
  });
  await client.installBundle(result);
  assert.equal(f.getInstalled()?.bundleVersion, bundleVersion);
  assert.deepEqual(f.store.getFallbackUpdateBundleData(), previous.slice(1));
  assert.deepEqual(stateAtEviction, [f.getInstalled(), previous.slice(1)]);
  assert.equal(f.getRestarts(), 1);
  assert.equal(existsSync(evicted), true);
});

test('failed full ASC verification preserves the prior cached bundle', async (t) => {
  const f = await fixture(t);
  const client = f.makeClient();
  const result = await client.downloadBundle({
    ...f.params,
    diffDownloadUrl: undefined,
  });
  await client.verifyBundleASC(result);
  const originalHtml = readFileSync(
    join(f.bundleDir, 'build/index.html'),
    'utf8'
  );
  const invalid = zip({
    'metadata.json': f.metadata,
    'build/index.html': 'HTML has a different digest',
    'build/assets/metadata.json': f.nestedMetadata,
    'build/assets/reused.txt': f.asset,
  });
  writeFileSync(join(client.getDownloadDir(), `${outputName}.zip`), invalid);
  await assert.rejects(
    client.verifyBundleASC({
      ...result,
      sha256: hash('sha256', invalid),
    }),
    hasCode('INTEGRITY_FAILED', 'verifyASC')
  );
  assert.equal(
    readFileSync(join(f.bundleDir, 'build/index.html'), 'utf8'),
    originalHtml
  );
  assert.deepEqual(
    fs.readdirSync(join(dirname(f.bundleDir), DIFF_STAGING_DIR_NAME)),
    []
  );
});

test('both archive verification operations reject oversize ZIPs and can be cancelled while hashing', async (t) => {
  for (const [method, step] of [
    ['verifyBundle', 'verifyPackage'],
    ['verifyBundleASC', 'verifyASC'],
  ]) {
    await t.test(method, async (subtest) => {
      const f = await fixture(subtest);
      const client = f.makeClient();
      const archive = join(client.getDownloadDir(), `${outputName}.zip`);
      writeFileSync(archive, 'archive');
      truncateSync(archive, 2 * 1024 * 1024 * 1024 + 1);
      await assert.rejects(
        client[method](f.params),
        hasCode('SECURITY_REJECTED', step)
      );
      truncateSync(archive, 1);

      let announceHash;
      const hashing = new Promise((resolve) => {
        announceHash = resolve;
      });
      const originalRead = fs.createReadStream;
      const streamSpy = subtest.mock.method(
        fs,
        'createReadStream',
        (filePath, options) => {
          if (filePath !== archive) return originalRead(filePath, options);
          assert.equal(options.highWaterMark, 64 * 1024);
          const stream = new Readable({ read() {}, signal: options.signal });
          announceHash(stream);
          return stream;
        }
      );
      const verification = assert.rejects(
        client[method](f.params),
        hasCode('CANCELLED', step)
      );
      const stream = await hashing;
      await client.clearDownload();
      await verification;
      assert.equal(stream.destroyed, true);
      assert.equal(existsSync(archive), false);
      assert.equal(existsSync(f.bundleDir), false);
      streamSpy.mock.restore();
    });
  }
});

test('a fresh 200 replaces a partial with the new response headers after If-Range', async (t) => {
  const f = await fixture(t);
  const targetPath = join(f.root, 'archive.zip');
  const contents = Buffer.from(
    'New response with the expected release payload'
  );
  const partial = `${targetPath}.partial`;
  const identity = 'fresh-if-range';
  const expectedSha256 = hash('sha256', contents);
  writeFileSync(partial, 'old bytes from a different response');
  writeFileSync(
    `${partial}.progress.json`,
    JSON.stringify({
      identity,
      url: downloadUrl,
      size: contents.length,
      etag: '"old"',
      checksum: `:${expectedSha256}`,
      mode: 'single',
      parts: [{ start: 0, end: contents.length - 1, done: 10 }],
    })
  );

  let requests = 0;
  await downloadNodeFile({
    url: downloadUrl,
    targetPath,
    identity,
    expectedSha256,
    expectedBytes: contents.length,
    transport: async (url, headers) => {
      requests += 1;
      if (requests === 1) {
        const result = fixtureResponse(url, 200, contents);
        assert.equal(headers.Range, 'bytes=0-0');
        result.response.headers.etag = '"old"';
        return result;
      }
      if (requests === 2) {
        assert.equal(headers.Range, 'bytes=10-');
        assert.equal(headers['If-Range'], '"old"');
        const response = Readable.from(
          (async function* () {
            yield contents.subarray(0, 6);
            await nextTurn();
            throw Object.assign(new Error('Fixture transfer interrupted'), {
              code: 'ECONNRESET',
            });
          })()
        );
        response.statusCode = 200;
        response.headers = {
          'content-length': String(contents.length),
          'etag': '"new"',
        };
        return { url, response };
      }
      assert.equal(headers.Range, 'bytes=6-');
      assert.equal(headers['If-Range'], '"new"');
      const result = fixtureResponse(url, 206, contents.subarray(6));
      result.response.headers.etag = '"new"';
      result.response.headers['content-range'] =
        `bytes 6-${contents.length - 1}/${contents.length}`;
      return result;
    },
  });
  assert.equal(requests, 3);
  assert.deepEqual(readFileSync(targetPath), contents);
});

test('probe and single-stream retries honor the server delay and the 60-second cap', async (t) => {
  const f = await fixture(t);
  const now = new Date('2026-11-20T12:00:00Z');
  t.mock.timers.enable({ apis: ['setTimeout', 'Date'], now });
  const contents = Buffer.from('Retried release payload');
  const statuses = [];
  const download = downloadNodeFile({
    url: downloadUrl,
    targetPath: join(f.root, 'archive.zip'),
    identity: 'retry-after',
    expectedSha256: hash('sha256', contents),
    transport: async (url, headers) => {
      const probe = headers.Range === 'bytes=0-0';
      statuses.push(probe ? 'probe' : 'single');
      if (statuses.length === 1) {
        const result = fixtureResponse(url, 429, Buffer.alloc(0));
        result.response.headers['retry-after'] = '500';
        return result;
      }
      if (statuses.length === 3) {
        const result = fixtureResponse(url, 503, Buffer.alloc(0));
        result.response.headers['retry-after'] = new Date(
          Date.now() + 4_000
        ).toUTCString();
        return result;
      }
      return fixtureResponse(url, 200, contents);
    },
  });
  await nextTurn();
  t.mock.timers.tick(59_999);
  await nextTurn();
  assert.deepEqual(statuses, ['probe']);
  t.mock.timers.tick(1);
  await nextTurn();
  assert.deepEqual(statuses, ['probe', 'probe', 'single']);
  t.mock.timers.tick(3_999);
  await nextTurn();
  assert.equal(statuses.length, 3);
  t.mock.timers.tick(1);
  await download;
  assert.deepEqual(statuses, ['probe', 'probe', 'single', 'single']);
});

test('the last transient probe honors Retry-After before a full-stream request', async (t) => {
  const f = await fixture(t);
  t.mock.timers.enable({ apis: ['setTimeout'] });
  const contents = Buffer.from('Full response after probe throttling');
  let probes = 0;
  let singles = 0;
  const download = downloadNodeFile({
    url: downloadUrl,
    targetPath: join(f.root, 'archive.zip'),
    identity: 'probe-fallback-delay',
    expectedSha256: hash('sha256', contents),
    transport: async (url, headers) => {
      if (headers.Range === 'bytes=0-0') {
        probes += 1;
        const result = fixtureResponse(url, 503, Buffer.alloc(0));
        result.response.headers['retry-after'] = probes === 4 ? '5' : '0';
        return result;
      }
      singles += 1;
      return fixtureResponse(url, 200, contents);
    },
  });
  for (let retry = 0; retry < 3; retry += 1) {
    await nextTurn();
    t.mock.timers.tick(0);
  }
  await nextTurn();
  assert.equal(probes, 4);
  t.mock.timers.tick(4_999);
  await nextTurn();
  assert.equal(singles, 0);
  t.mock.timers.tick(1);
  await download;
  assert.equal(singles, 1);
});

function failDescriptorRename(t, descriptor) {
  const originalRename = fs.renameSync;
  return t.mock.method(fs, 'renameSync', (from, target) => {
    if (target === descriptor) {
      throw Object.assign(new Error('Fixture descriptor busy'), {
        code: 'EBUSY',
      });
    }
    return originalRename(from, target);
  });
}

test('diff ASC keeps the staged bundle private when the verified descriptor cannot be written', async (t) => {
  const f = await fixture(t);
  const client = f.makeClient();
  const result = await client.downloadBundle(f.params);
  const descriptor = join(f.diffDir, `${outputName}.diff.zip.meta.json`);
  const rename = failDescriptorRename(t, descriptor);
  await assert.rejects(
    client.verifyBundleASC(result),
    hasCode('STORAGE_ERROR', 'verifyASC')
  );
  assert.equal(existsSync(f.bundleDir), false);
  assert.equal(existsSync(join(f.diffDir, `${outputName}.diff.zip`)), true);
  assert.equal(f.calls.includes(downloadUrl), false);
  assert.equal(
    JSON.parse(readFileSync(descriptor, 'utf8')).signature,
    undefined
  );
  rename.mock.restore();
  await client.verifyBundleASC(result);
  assert.equal(
    JSON.parse(readFileSync(descriptor, 'utf8')).signature,
    result.signature
  );
  assert.equal(readFileSync(f.metadataPath, 'utf8'), f.metadata);
});

test('a diff download requires its descriptor and retains the archive on write failure', async (t) => {
  const f = await fixture(t);
  const client = f.makeClient();
  const archive = join(f.diffDir, `${outputName}.diff.zip`);
  const descriptor = `${archive}.meta.json`;
  const rename = failDescriptorRename(t, descriptor);
  await assert.rejects(
    client.downloadBundle(f.params),
    hasCode('STORAGE_ERROR', 'download')
  );
  assert.equal(
    hash('sha256', readFileSync(archive)),
    f.params.diffSha256.toLowerCase()
  );
  assert.equal(existsSync(descriptor), false);
  assert.equal(f.calls.includes(downloadUrl), false);
  const transfers = f.calls.length;
  rename.mock.restore();
  const result = await client.downloadBundle(f.params);
  assert.equal(result.diffDownloadUrl, diffDownloadUrl);
  assert.equal(f.calls.length, transfers);
  assert.equal(existsSync(descriptor), true);
});

test('full fallback preserves the diff until the fallback descriptor is committed', async (t) => {
  const f = await fixture(t, { base: false });
  const client = f.makeClient();
  const result = await client.downloadBundle(f.params);
  const archive = join(f.diffDir, `${outputName}.diff.zip`);
  const descriptor = `${archive}.meta.json`;
  const rename = failDescriptorRename(t, descriptor);
  await assert.rejects(
    client.verifyBundleASC(result),
    hasCode('STORAGE_ERROR', 'verifyASC')
  );
  assert.equal(existsSync(archive), true);
  assert.equal(
    JSON.parse(readFileSync(descriptor, 'utf8')).fallbackRequired,
    false
  );
  assert.equal(f.calls.includes(downloadUrl), false);
  rename.mock.restore();
  const diff = await client.downloadBundle(f.params);
  assert.equal(diff.diffDownloadUrl, diffDownloadUrl);
  await client.verifyBundleASC(diff);
  assert.equal(
    JSON.parse(readFileSync(descriptor, 'utf8')).fallbackRequired,
    true
  );
  assert.equal(existsSync(archive), false);
  assert.equal(readFileSync(f.metadataPath, 'utf8'), f.metadata);
});

test('download deadline covers stalled transport, response body and final digest', async (t) => {
  for (const phase of [
    'transport',
    'body',
    'body iterator',
    'digest',
    'caller abort',
  ]) {
    await t.test(phase, { timeout: 5000 }, async (subtest) => {
      const f = await fixture(subtest);
      subtest.mock.timers.enable({ apis: ['setTimeout'] });
      const controller = new AbortController();
      const contents = Buffer.from('A release with a valid hash');
      const targetPath = join(f.root, 'archive.zip');
      let entered;
      const waiting = new Promise((resolve) => {
        entered = resolve;
      });
      let readStream;
      if (phase === 'digest') {
        subtest.mock.method(fs, 'createReadStream', (_path, options) => {
          readStream = new Readable({ read() {}, signal: options.signal });
          entered();
          return readStream;
        });
      }
      const verification = assert.rejects(
        downloadNodeFile({
          url: downloadUrl,
          targetPath,
          identity: 'deadline',
          signal: controller.signal,
          expectedSha256: hash('sha256', contents),
          transport: async (url, headers) => {
            if (phase === 'transport' || phase === 'caller abort') {
              entered();
              return new Promise(() => {});
            }
            if (phase === 'body' && !headers.Range) {
              readStream = new Readable({
                read() {
                  entered();
                },
              });
              readStream.statusCode = 200;
              readStream.headers = {
                'content-length': String(contents.length),
              };
              return { url, response: readStream };
            }
            if (phase === 'body iterator' && !headers.Range) {
              readStream = {
                statusCode: 200,
                headers: { 'content-length': String(contents.length) },
                destroy() {
                  this.destroyed = true;
                },
                [Symbol.asyncIterator]: () => ({
                  next: () => {
                    entered();
                    return new Promise(() => {});
                  },
                }),
              };
              return { url, response: readStream };
            }
            return fixtureResponse(url, 200, contents);
          },
        }),
        hasCode(phase === 'caller abort' ? 'CANCELLED' : 'TIMEOUT', 'download')
      );
      await waiting;
      if (phase === 'caller abort') controller.abort();
      else subtest.mock.timers.tick(15 * 60_000);
      await verification;
      assert.equal(existsSync(targetPath), false);
      if (readStream) assert.equal(readStream.destroyed, true);
    });
  }
});
