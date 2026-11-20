import assert from 'node:assert/strict';
import { setMaxListeners } from 'node:events';
import { createRequire } from 'node:module';
import { test } from 'node:test';

test('the second Node entry point recognizes a shared admission error', async () => {
  const require = createRequire(import.meta.url);
  const commonjs = require('@onekeyfe/react-native-sni-connect/node');
  const client = new commonjs.SniNodeClient();
  const limiter = client.getRequestLimiter();
  const esm = await import('@onekeyfe/react-native-sni-connect/node');
  const esmClient = new esm.SniNodeClient();
  const hostname = 'example.com';
  const ip = '93.184.216.34';
  const releases = await Promise.all(
    Array.from({ length: 16 }, () => limiter.acquire(hostname, ip))
  );
  const controller = new AbortController();
  setMaxListeners(0, controller.signal);
  const queued = Promise.allSettled(
    Array.from({ length: 256 }, () =>
      limiter.acquire(hostname, ip, { signal: controller.signal })
    )
  );

  try {
    assert.deepEqual(await esmClient.getDebugSnapshot({ hostname, ip }), {
      activeRequests: 16,
      activeRequestsForPair: 16,
      pendingRequests: 256,
      pendingRequestsForPair: 256,
    });
    await assert.rejects(
      esmClient.request({
        hostname,
        ip,
        path: '/',
        method: 'GET',
        body: null,
        headers: {},
        timeout: 1000,
      }),
      (error) => {
        assert.ok(error instanceof esm.SniRequestError);
        assert.ok(error instanceof commonjs.SniRequestError);
        assert.equal(error.code, 'SNI_RESOURCE_LIMIT');
        assert.equal(error.failClosed, true);
        return true;
      }
    );
  } finally {
    controller.abort();
    releases.forEach((release) => release());
    await queued;
  }
});
