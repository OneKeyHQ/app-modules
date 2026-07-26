#!/usr/bin/env node

'use strict';

const crypto = require('node:crypto');
const fs = require('node:fs');
const https = require('node:https');
const path = require('node:path');

const SMALL_BYTES = 4 * 1024 * 1024;
const LARGE_BYTES = 83_854_948;
const CHUNK_BYTES = 64 * 1024;
const MAX_LOGS = 2_000;
const scriptDirectory = __dirname;
const certificateDirectory =
  process.env.FIRMWARE_CERT_DIR || path.join(scriptDirectory, '.certs');
const port = Number(process.env.FIRMWARE_TEST_PORT || 9443);
const wrongCertificatePort = Number(
  process.env.FIRMWARE_WRONG_CERT_PORT || 9444
);

const profiles = Object.freeze({
  small: Object.freeze({ size: SMALL_BYTES }),
  large: Object.freeze({ size: LARGE_BYTES }),
});

const digests = Object.fromEntries(
  Object.entries(profiles).map(([name, profile]) => [
    name,
    calculateDigest(profile.size),
  ])
);

let activeScenario = 'normal';
let activeProfile = 'small';
let generation = 1;
const counters = new Map();
const requestLogs = [];

function payloadByte(offset) {
  return (offset * 31 + Math.floor(offset / 251) * 17 + 0x5a) & 0xff;
}

function payloadChunk(offset, length) {
  const chunk = Buffer.allocUnsafe(length);
  for (let index = 0; index < length; index += 1) {
    chunk[index] = payloadByte(offset + index);
  }
  return chunk;
}

function calculateDigest(size) {
  const hash = crypto.createHash('sha256');
  for (let offset = 0; offset < size; offset += CHUNK_BYTES) {
    hash.update(payloadChunk(offset, Math.min(CHUNK_BYTES, size - offset)));
  }
  return hash.digest('hex');
}

function parseRange(value, size) {
  const match = /^bytes=(\d+)-(\d+)$/.exec(value || '');
  if (!match) {
    return null;
  }
  const start = Number(match[1]);
  const end = Number(match[2]);
  if (
    !Number.isSafeInteger(start) ||
    !Number.isSafeInteger(end) ||
    start < 0 ||
    end < start ||
    end >= size
  ) {
    return null;
  }
  return { start, end };
}

function scenarioCounter(scenario, profile, rangeHeader) {
  const key = `${scenario}|${profile}|${rangeHeader || 'full'}`;
  const count = (counters.get(key) || 0) + 1;
  counters.set(key, count);
  return count;
}

function recordRequest(entry) {
  requestLogs.push({
    at: new Date().toISOString(),
    ...entry,
  });
  if (requestLogs.length > MAX_LOGS) {
    requestLogs.splice(0, requestLogs.length - MAX_LOGS);
  }
}

function readJsonBody(request) {
  return new Promise((resolve, reject) => {
    const chunks = [];
    let length = 0;
    request.on('data', (chunk) => {
      length += chunk.length;
      if (length > 64 * 1024) {
        reject(new Error('request body exceeds 64KiB'));
        request.destroy();
        return;
      }
      chunks.push(chunk);
    });
    request.on('end', () => {
      try {
        resolve(
          chunks.length === 0
            ? {}
            : JSON.parse(Buffer.concat(chunks).toString('utf8'))
        );
      } catch (error) {
        reject(error);
      }
    });
    request.on('error', reject);
  });
}

function sendJson(response, statusCode, value) {
  const body = Buffer.from(`${JSON.stringify(value, null, 2)}\n`);
  response.writeHead(statusCode, {
    'Cache-Control': 'no-store',
    'Content-Length': body.length,
    'Content-Type': 'application/json',
  });
  response.end(body);
}

function wait(milliseconds) {
  return new Promise((resolve) => setTimeout(resolve, milliseconds));
}

async function writePayload(
  response,
  start,
  end,
  { disconnectAfterBytes = null, stallAfterBytes = null, stallMs = 0 } = {}
) {
  let offset = start;
  let emitted = 0;
  while (offset <= end && !response.destroyed) {
    const length = Math.min(CHUNK_BYTES, end - offset + 1);
    const chunk = payloadChunk(offset, length);
    const remainingBeforeDisconnect =
      disconnectAfterBytes == null ? length : disconnectAfterBytes - emitted;
    if (remainingBeforeDisconnect <= 0) {
      response.socket?.destroy();
      return;
    }
    const writeLength = Math.min(length, remainingBeforeDisconnect);
    if (!response.write(chunk.subarray(0, writeLength))) {
      await new Promise((resolve) => response.once('drain', resolve));
    }
    offset += writeLength;
    emitted += writeLength;
    if (
      stallAfterBytes != null &&
      emitted >= stallAfterBytes &&
      stallMs > 0
    ) {
      await wait(stallMs);
      stallAfterBytes = null;
    }
    if (disconnectAfterBytes != null && emitted >= disconnectAfterBytes) {
      response.socket?.destroy();
      return;
    }
  }
  if (!response.destroyed) {
    response.end();
  }
}

function selectScenario(url, request) {
  return (
    url.searchParams.get('scenario') ||
    request.headers['x-firmware-test-scenario'] ||
    activeScenario
  );
}

function selectProfile(url) {
  const requested = url.searchParams.get('profile') || activeProfile;
  return profiles[requested] ? requested : null;
}

function currentETag(scenario, isProbe, count) {
  if (scenario === 'etag-change' && (!isProbe || count > 1)) {
    return '"firmware-v2"';
  }
  return `"firmware-v${generation}"`;
}

async function handleArtifact(request, response, url, wrongCertificateServer) {
  const scenario = selectScenario(url, request);
  const profileName = selectProfile(url);
  if (!profileName) {
    sendJson(response, 400, { error: 'unknown profile' });
    return;
  }
  const { size } = profiles[profileName];
  const rangeHeader = request.headers.range;
  const parsedRange = parseRange(rangeHeader, size);
  const count = scenarioCounter(scenario, profileName, rangeHeader);
  const isProbe = parsedRange?.start === 0 && parsedRange?.end === 0;
  const etag = currentETag(scenario, isProbe, count);
  recordRequest({
    count,
    ifRange: request.headers['if-range'] || null,
    method: request.method,
    profile: profileName,
    range: rangeHeader || null,
    scenario,
    server: wrongCertificateServer ? 'wrong-certificate' : 'canonical',
  });

  if (scenario === 'cross-host-redirect') {
    response.writeHead(302, {
      Location: `https://wrong.test:${wrongCertificatePort}/artifact?profile=${profileName}`,
    });
    response.end();
    return;
  }
  if (scenario === '429-once' && !isProbe && count === 1) {
    response.writeHead(429, { 'Retry-After': '1' });
    response.end();
    return;
  }
  if (scenario === '503-once' && !isProbe && count === 1) {
    response.writeHead(503, { 'Retry-After': '1' });
    response.end();
    return;
  }
  if (scenario === '416-once' && !isProbe && count === 1) {
    response.writeHead(416, {
      'Content-Range': `bytes */${size}`,
      ETag: etag,
    });
    response.end();
    return;
  }
  if (rangeHeader && !parsedRange) {
    response.writeHead(416, {
      'Content-Range': `bytes */${size}`,
      ETag: etag,
    });
    response.end();
    return;
  }

  const forceWholeBody =
    (scenario === 'range-200' && !isProbe) ||
    (scenario === 'etag-change' &&
      request.headers['if-range'] &&
      request.headers['if-range'] !== etag);
  const responseRange = parsedRange && !forceWholeBody ? parsedRange : null;
  const start = responseRange?.start || 0;
  const end = responseRange?.end ?? size - 1;
  const length = end - start + 1;
  const headers = {
    'Accept-Ranges': 'bytes',
    'Cache-Control': 'no-store',
    'Content-Length': length,
    'Content-Type': 'application/octet-stream',
    ETag: etag,
    'Last-Modified': 'Mon, 01 Jan 2024 00:00:00 GMT',
  };
  if (responseRange) {
    headers['Content-Range'] = `bytes ${start}-${end}/${size}`;
  }
  response.writeHead(responseRange ? 206 : 200, headers);

  const options = {};
  if (scenario === 'disconnect-once' && !isProbe && count === 1) {
    options.disconnectAfterBytes = Math.min(64 * 1024, length);
  }
  if (scenario === 'stall-once' && !isProbe && count === 1) {
    options.stallAfterBytes = Math.min(64 * 1024, length);
    options.stallMs = Number(url.searchParams.get('stallMs') || 35_000);
  }
  await writePayload(response, start, end, options);
}

function createHandler(wrongCertificateServer) {
  return async (request, response) => {
    const url = new URL(request.url, 'https://firmware.test');
    try {
      if (request.method === 'GET' && url.pathname === '/health') {
        sendJson(response, 200, {
          activeProfile,
          activeScenario,
          generation,
          profiles: Object.fromEntries(
            Object.entries(profiles).map(([name, profile]) => [
              name,
              { ...profile, sha256: digests[name] },
            ])
          ),
          server: wrongCertificateServer ? 'wrong-certificate' : 'canonical',
        });
        return;
      }
      if (request.method === 'GET' && url.pathname === '/manifest') {
        sendJson(response, 200, {
          profiles: Object.fromEntries(
            Object.entries(profiles).map(([name, profile]) => [
              name,
              {
                sha256: digests[name],
                size: profile.size,
                url: `https://firmware.test:${port}/artifact?profile=${name}`,
              },
            ])
          ),
        });
        return;
      }
      if (request.method === 'GET' && url.pathname === '/logs') {
        sendJson(response, 200, { requests: requestLogs });
        return;
      }
      if (request.method === 'POST' && url.pathname === '/reset') {
        counters.clear();
        requestLogs.length = 0;
        generation += 1;
        activeScenario = 'normal';
        activeProfile = 'small';
        sendJson(response, 200, { generation, ok: true });
        return;
      }
      if (request.method === 'POST' && url.pathname === '/scenario') {
        const body = await readJsonBody(request);
        if (typeof body.scenario !== 'string' || body.scenario.length > 64) {
          sendJson(response, 400, { error: 'scenario is required' });
          return;
        }
        if (body.profile != null && !profiles[body.profile]) {
          sendJson(response, 400, { error: 'unknown profile' });
          return;
        }
        activeScenario = body.scenario;
        activeProfile = body.profile || activeProfile;
        counters.clear();
        requestLogs.length = 0;
        sendJson(response, 200, {
          activeProfile,
          activeScenario,
          generation,
        });
        return;
      }
      if (
        (request.method === 'GET' || request.method === 'HEAD') &&
        url.pathname === '/artifact'
      ) {
        if (request.method === 'HEAD') {
          sendJson(response, 405, { error: 'use GET range probe' });
          return;
        }
        await handleArtifact(
          request,
          response,
          url,
          wrongCertificateServer
        );
        return;
      }
      sendJson(response, 404, { error: 'not found' });
    } catch (error) {
      if (!response.headersSent) {
        sendJson(response, 500, { error: error.message });
      } else {
        response.destroy(error);
      }
    }
  };
}

function readTLSOptions(prefix) {
  return {
    cert: fs.readFileSync(path.join(certificateDirectory, `${prefix}.crt`)),
    key: fs.readFileSync(path.join(certificateDirectory, `${prefix}.key`)),
    minVersion: 'TLSv1.2',
  };
}

function start() {
  const canonical = https.createServer(
    readTLSOptions('server'),
    createHandler(false)
  );
  const wrongCertificate = https.createServer(
    readTLSOptions('wrong-server'),
    createHandler(true)
  );
  canonical.listen(port, '0.0.0.0', () => {
    process.stdout.write(
      `firmware conformance server https://firmware.test:${port}\n`
    );
  });
  wrongCertificate.listen(wrongCertificatePort, '0.0.0.0', () => {
    process.stdout.write(
      `wrong-certificate server https://wrong.test:${wrongCertificatePort}\n`
    );
  });
}

if (require.main === module) {
  start();
}

module.exports = {
  LARGE_BYTES,
  SMALL_BYTES,
  calculateDigest,
  parseRange,
  payloadChunk,
};
