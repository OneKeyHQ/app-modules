import assert from 'node:assert/strict';
import { execFileSync } from 'node:child_process';
import { createHash } from 'node:crypto';
import { cpSync, mkdirSync, mkdtempSync, readFileSync, rmSync, writeFileSync } from 'node:fs';
import { createRequire } from 'node:module';
import { tmpdir } from 'node:os';
import { dirname, join } from 'node:path';
import { Readable } from 'node:stream';
import { test } from 'node:test';

import {
  FirmwareArtifactNode,
  FirmwareArtifactNodeError,
  NodeRangeDownloadError,
  downloadNodeFile,
} from '@onekeyfe/react-native-range-downloader/node';

const packageName = '@onekeyfe/react-native-range-downloader';
const require = createRequire(import.meta.url);
const binaryUrl = 'https://downloads.example.test/file.bin';
const firmwareUrl = 'https://common.onekey-asset.com/test-firmware.bin';

function scratch(t) {
  const root = mkdtempSync(join(tmpdir(), 'onekey-range-node-'));
  t.after(() => rmSync(root, { recursive: true, force: true }));
  return root;
}

function sha256(bytes) {
  return createHash('sha256').update(bytes).digest('hex');
}

function response(url, status, body, headers = {}) {
  return {
    url,
    response: Object.assign(Readable.from([body]), {
      statusCode: status,
      headers: { 'content-length': String(body.length), ...headers },
    }),
  };
}

function rangeResponse(url, data, start, end) {
  return response(url, 206, data.subarray(start, end + 1), {
    'content-range': `bytes ${start}-${end}/${data.length}`,
    etag: '"fixture"',
  });
}

test('the CommonJS entry exposes the same Node services as ESM', () => {
  const commonjs = require(`${packageName}/node`);
  assert.equal(typeof commonjs.downloadNodeFile, 'function');
  assert.equal(typeof commonjs.FirmwareArtifactNode, 'function');
  assert.equal(typeof commonjs.NodeRangeDownloadError, 'function');
});

test('shipped declarations resolve through both strict NodeNext conditions', (t) => {
  const root = scratch(t);
  const manifest = require.resolve(`${packageName}/package.json`);
  const installed = join(root, 'node_modules', packageName);
  mkdirSync(installed, { recursive: true });
  cpSync(manifest, join(installed, 'package.json'));
  cpSync(join(dirname(manifest), 'lib'), join(installed, 'lib'), {
    recursive: true,
  });

  const nodeTypes = dirname(require.resolve('@types/node/package.json'));
  const undiciTypes = dirname(
    require.resolve('undici-types/package.json', { paths: [nodeTypes] })
  );
  cpSync(nodeTypes, join(root, 'node_modules/@types/node'), { recursive: true });
  cpSync(undiciTypes, join(root, 'node_modules/undici-types'), { recursive: true });

  const usage = `
    const options: INodeDownloadOptions = {
      url: 'https://example.test/download',
      targetPath: 'example.bin',
      identity: 'example',
    };
    const download = downloadNodeFile(options);
    const store: IFirmwareArtifactAdapter = new FirmwareArtifactNode({ rootPath: '.' });
  `;
  const esm = join(root, 'consumer.mts');
  const cjs = join(root, 'consumer.cts');
  writeFileSync(esm, `
    import {
      downloadNodeFile, FirmwareArtifactNode,
      type INodeDownloadOptions, type IFirmwareArtifactAdapter,
    } from '${packageName}/node';
    ${usage}
  `);
  writeFileSync(cjs, `
    import api = require('${packageName}/node');
    const { downloadNodeFile, FirmwareArtifactNode } = api;
    type INodeDownloadOptions = api.INodeDownloadOptions;
    type IFirmwareArtifactAdapter = api.IFirmwareArtifactAdapter;
    ${usage}
  `);

  execFileSync(process.execPath, [
    require.resolve('typescript/bin/tsc'),
    '--module', 'NodeNext', '--lib', 'ES2022', '--types', 'node',
    '--strict', '--verbatimModuleSyntax', '--noEmit', esm, cjs,
  ], { cwd: root, stdio: 'pipe' });
});

test('the Node downloader assembles eight ranges and reuses the verified file', async (t) => {
  const data = Buffer.alloc(3 * 1024 * 1024);
  for (let i = 0; i < data.length; i += 1) data[i] = i % 251;
  const targetPath = join(scratch(t), 'artifact.bin');
  const requests = [];
  const options = {
    url: binaryUrl,
    targetPath,
    identity: 'eight-ranges',
    expectedSha256: sha256(data),
    expectedBytes: data.length,
    async transport(url, headers) {
      requests.push(headers.Range);
      const match = /^bytes=(\d+)-(\d+)$/.exec(headers.Range);
      assert.ok(match);
      return rangeResponse(url, data, Number(match[1]), Number(match[2]));
    },
  };
  assert.deepEqual(await downloadNodeFile(options), { filePath: targetPath, totalBytes: data.length });
  assert.equal(requests.length, 9);
  assert.equal(requests[0], 'bytes=0-0');
  assert.deepEqual(readFileSync(targetPath), data);

  await downloadNodeFile(options);
  assert.equal(requests.length, 9);
});

test('cancelling a single stream retains the bytes for a later Range request', async (t) => {
  const data = Buffer.from('resume at the last verified offset');
  const targetPath = join(scratch(t), 'resumable.bin');
  const options = {
    url: binaryUrl,
    targetPath,
    identity: 'single-resume',
    expectedSha256: sha256(data),
    expectedBytes: data.length,
  };
  const controller = new AbortController();
  const prefixLength = 8;
  await assert.rejects(
    downloadNodeFile({
      ...options,
      signal: controller.signal,
      onProgress: ({ transferred }) => {
        if (transferred === prefixLength) controller.abort();
      },
      async transport(url, headers) {
        if (headers.Range === 'bytes=0-0') return rangeResponse(url, data, 0, 0);
        return response(url, 200, data.subarray(0, prefixLength), {
          'content-length': String(data.length),
          etag: '"fixture"',
        });
      },
    }),
    (error) => error instanceof NodeRangeDownloadError && error.code === 'CANCELLED'
  );
  assert.deepEqual(readFileSync(`${targetPath}.partial`), data.subarray(0, prefixLength));

  await downloadNodeFile({
    ...options,
    async transport(url, headers) {
      if (headers.Range === 'bytes=0-0') return rangeResponse(url, data, 0, 0);
      assert.equal(headers.Range, `bytes=${prefixLength}-`);
      assert.equal(headers['If-Range'], '"fixture"');
      return rangeResponse(url, data, prefixLength, data.length - 1);
    },
  });
  assert.deepEqual(readFileSync(targetPath), data);
});

test('the firmware store retains its download constraints and reader/cache lifecycle', async (t) => {
  const data = Buffer.from('firmware fixture');
  const requests = [];
  const store = new FirmwareArtifactNode({
    rootPath: join(scratch(t), 'firmware-artifacts'),
    async fetch(url, options) {
      requests.push({ url, options });
      return new Response(data, { status: 200 });
    },
  });
  const transactionId = 'firmware-test';
  const { leaseRef } = await store.createLease(transactionId);
  const input = {
    taskId: 'download-1',
    transactionId,
    leaseRef,
    artifactId: 'firmware-1',
    url: firmwareUrl,
    route: { routeType: 'domain' },
    overallDeadlineSeconds: 5,
    maxBytes: data.length,
    expectedSize: data.length,
    expectedSha256: sha256(data),
  };

  const received = await store.download(input);
  assert.deepEqual(received, {
    artifactRef: `fw:${sha256(data)}`,
    size: data.length,
    sha256: sha256(data),
    expectedSha256Verified: true,
  });
  assert.equal(requests.length, 1);
  assert.equal(requests[0].url, firmwareUrl);
  assert.equal(requests[0].options.redirect, 'error');
  assert.equal(requests[0].options.credentials, 'omit');
  assert.equal(requests[0].options.headers['Accept-Encoding'], 'identity');

  const { readerId, size } = await store.open(received.artifactRef);
  assert.deepEqual(Buffer.from(await store.read({ readerId, offset: 0, length: size })), data);
  await store.close(readerId);
  await store.download(input);
  assert.equal(requests.length, 1);
  await store.releaseLease({ leaseRef, disposition: 'completed' });
  await assert.rejects(
    store.download({ ...input, url: 'https://unlisted.example.test/file.bin' }),
    (error) => error instanceof FirmwareArtifactNodeError && error.code === 'ARTIFACT_INVALID_INPUT'
  );
});
