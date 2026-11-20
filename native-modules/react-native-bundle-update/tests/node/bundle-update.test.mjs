import assert from 'node:assert/strict';
import { createHash } from 'node:crypto';
import {
  existsSync,
  mkdirSync,
  mkdtempSync,
  readFileSync,
  rmSync,
  writeFileSync,
} from 'node:fs';
import { createRequire } from 'node:module';
import { tmpdir } from 'node:os';
import { dirname, join } from 'node:path';
import { Readable } from 'node:stream';
import { test } from 'node:test';

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

async function fixture(t, { base = true, transport, diffStatus = 200 } = {}) {
  const root = mkdtempSync(join(tmpdir(), 'bundle-update-node-'));
  t.after(() => rmSync(root, { recursive: true, force: true }));
  const builtInBuildDir = join(root, 'built-in', 'build');
  const bundleDir = join(root, 'onekey-bundle', outputName);
  const diffDir = join(root, 'onekey-bundle-download', DIFF_DOWNLOAD_DIR_NAME);
  const metadataPath = join(bundleDir, 'metadata.json');

  const html = '<html>version 42</html>';
  const asset = 'unchanged asset';
  const metadata = JSON.stringify({
    'index.html': hash('sha512', html),
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
    changedFiles: { 'index.html': hash('sha512', html) },
    reusedFiles: { 'assets/reused.txt': hash('sha512', asset) },
    deletedFiles: [],
  });

  const full = zip({
    'metadata.json': metadata,
    'build/index.html': html,
    'build/assets/reused.txt': asset,
  });
  const diff = zip({
    'diff-manifest.json': manifest,
    'metadata.json': metadata,
    'build/index.html': html,
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
    appVersion: () => appVersion,
    buildNumber: () => '1',
    builtInBuildDir: () => builtInBuildDir,
    publicKey,
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
    calls,
    progress,
    params,
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
