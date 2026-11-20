import { EventEmitter } from 'events';
import * as fs from 'fs';
import type { IncomingMessage } from 'http';
import https, { type RequestOptions } from 'https';
import { Socket } from 'net';
import * as os from 'os';
import * as path from 'path';
import { pathToFileURL } from 'url';

import SniNodeClient, {
  buildSniRequestOptions,
  classifyTransportError,
  headersToMaps,
  isProxyRouteActive,
  isSniFailClosedError,
  redactIpLiterals,
  SniRequestError,
  SniRequestLimiter,
  type SniNodeLogSink,
  type SniNodeRequestConfig,
  type SniNodeUploadFileConfig,
  validateSniRequestConfig,
} from '../node';

const baseConfig = (): SniNodeRequestConfig => ({
  ip: '93.184.216.34',
  hostname: 'example.com',
  path: '/',
  headers: {},
  method: 'GET',
  body: null,
  timeout: 30_000,
});

class FakeClientRequest extends EventEmitter {
  options: RequestOptions;

  destroyedWith: Error | undefined;

  write = jest.fn();

  end = jest.fn();

  destroy = jest.fn((error?: Error) => {
    this.destroyedWith = error;
    if (error) {
      this.emit('error', error);
    }
    return this as never;
  });

  constructor(options: RequestOptions) {
    super();
    this.options = options;
  }
}

class FakeUploadRequest extends EventEmitter {
  options: RequestOptions;

  source: fs.ReadStream | undefined;

  chunks: Buffer[] = [];

  writableEnded = false;

  destroyedWith: Error | undefined;

  autoFlushWrites = true;

  pendingWriteCallbacks: Array<(error?: Error | null) => void> = [];

  write = jest.fn(
    (chunk: Buffer | string, callback?: (error?: Error | null) => void) => {
      this.chunks.push(Buffer.isBuffer(chunk) ? chunk : Buffer.from(chunk));
      if (callback) {
        if (this.autoFlushWrites) {
          setImmediate(() => callback());
        } else {
          this.pendingWriteCallbacks.push(callback);
        }
      }
      return true;
    }
  );

  end = jest.fn((chunk?: Buffer | string) => {
    if (chunk) this.write(chunk);
    this.writableEnded = true;
    this.emit('finish');
    return this as never;
  });

  destroy = jest.fn((error?: Error) => {
    this.destroyedWith = error;
    if (error) this.emit('error', error);
    this.emit('close');
    return this as never;
  });

  constructor(options: RequestOptions) {
    super();
    this.options = options;
    this.on('pipe', (source: fs.ReadStream) => {
      this.source = source;
    });
  }

  get body(): Buffer {
    return Buffer.concat(this.chunks);
  }
}

async function flushMicrotasks(): Promise<void> {
  await Promise.resolve();
  await Promise.resolve();
}

function observeRejection<T>(promise: Promise<T>): Promise<T> {
  promise.catch(() => undefined);
  return promise;
}

function createMockLogger(): SniNodeLogSink & {
  info: jest.Mock;
  warn: jest.Mock;
  error: jest.Mock;
} {
  return {
    info: jest.fn(),
    warn: jest.fn(),
    error: jest.fn(),
  };
}

describe('SniNodeClient OSCS validation and request lifecycle', () => {
  const expectInvalid = (config: Partial<SniNodeRequestConfig>) => {
    expect(() =>
      validateSniRequestConfig({ ...baseConfig(), ...config })
    ).toThrow(/SNI_INVALID_CONFIG/);
  };

  test('accepts valid request boundary values and normalizes method/path/body', () => {
    const normalized = validateSniRequestConfig({
      ...baseConfig(),
      requestId: 'req-1',
      method: ' post ',
      path: 'v1?q=1',
      body: 'a'.repeat(1024 * 1024),
      timeout: 120_000,
    });

    expect(normalized.method).toBe('POST');
    expect(normalized.path).toBe('/v1?q=1');
    expect(normalized.body).toHaveLength(1024 * 1024);
  });

  test('requires explicit bodies for write methods and rejects bodies for GET and HEAD', () => {
    for (const method of ['GET', 'HEAD']) {
      expectInvalid({ method, body: '' });
    }
    for (const method of ['POST', 'PUT', 'PATCH']) {
      expectInvalid({ method });
      expect(
        validateSniRequestConfig({ ...baseConfig(), method, body: '' }).body
      ).toBe('');
    }
  });

  test('redacts bracketed and unbracketed IPv6 including an IPv4 tail', () => {
    expect(redactIpLiterals('dial [2001:db8::1]:443 or 2001:db8::2')).toBe(
      'dial <ip6>:443 or <ip6>'
    );
    expect(
      redactIpLiterals('[abcd:abcd:abcd:abcd:abcd:abcd:192.168.128.100]')
    ).toBe('<ip6>');
  });

  test('rejects unsafe IP destinations and hostnames that are IP literals', () => {
    [
      'example.com',
      '93.184.216.34:443',
      ' 93.184.216.34',
      '093.184.216.034',
      '10.0.0.1',
      '127.0.0.1',
      '100.64.0.1',
      '169.254.169.254',
      '172.16.0.1',
      '192.168.1.1',
      '192.0.2.1',
      '198.18.0.1',
      '198.51.100.1',
      '203.0.113.1',
      '224.0.0.1',
      '255.255.255.255',
      '::',
      '::1',
      'fe80::1',
      'fc00::1',
      'ff00::1',
      '100::1',
      '2001::1',
      '2001:2::1',
      '2001:db8::1',
      '2002:0a00:0001::1',
      '::ffff:10.0.0.1',
      '64:ff9b::10.0.0.1',
      '64:ff9b:1::1',
      '2001:4860:4860::8888%en0',
      '[2001:4860:4860::8888]',
    ].forEach((ip) => expectInvalid({ ip }));

    ['93.184.216.34', '2001:4860:4860::8888'].forEach((hostname) =>
      expectInvalid({ hostname })
    );
  });

  test('rejects malformed hostnames, methods, paths, request ids, timeouts and body sizes', () => {
    [
      '',
      '-example.com',
      'example-.com',
      'example..com',
      'bad_host.example',
      'https://example.com',
      'example.com:443',
      `${'a'.repeat(64)}.example.com`,
      `${'a'.repeat(250)}.com`,
    ].forEach((hostname) => expectInvalid({ hostname }));

    ['TRACE', 'CONNECT', '', 'GET\n'].forEach((method) =>
      expectInvalid({ method })
    );

    [
      'https://example.com',
      'http://example.com',
      '//example.com/path',
      ['java', 'script:alert(1)'].join(''),
      '/path\nInjected: yes',
      `/${'a'.repeat(8192)}`,
    ].forEach((requestPath) => expectInvalid({ path: requestPath }));

    expectInvalid({ requestId: '' });
    expectInvalid({ requestId: 'x'.repeat(129) });
    expectInvalid({ requestId: 'req\n1' });
    expectInvalid({ timeout: 0 });
    expectInvalid({ timeout: 120_001 });
    expectInvalid({ timeout: Number.NaN });
    expectInvalid({ body: 'a'.repeat(1024 * 1024 + 1) });
    expectInvalid({ port: 8443 });
  });

  test('logs the destination IP while redacting validation error details', async () => {
    const logger = createMockLogger();
    const client = new SniNodeClient({ logger });

    await expect(
      client.request({ ...baseConfig(), ip: '10.0.0.5' })
    ).rejects.toThrow(/Forbidden IP: 10\.0\.0\.5/);

    const errorLogs = logger.error.mock.calls
      .map(([message]) => String(message))
      .join('\n');
    expect(errorLogs).toContain(
      'errorMessage=SNI_INVALID_CONFIG:_Forbidden_IP:_<ip>'
    );
    expect(errorLogs).toContain('ip=10.0.0.5');
    expect(errorLogs).not.toContain('ipHash=');
  });

  test('filters module-owned headers and rejects unsafe headers', () => {
    const normalized = validateSniRequestConfig({
      ...baseConfig(),
      method: 'post',
      headers: {
        'Host': 'evil.example',
        'Content-Length': '9999',
        'Accept-Encoding': 'gzip',
        'X-Test': 'ok',
      },
      body: 'abc',
    });
    const options = buildSniRequestOptions(normalized, {} as never);

    expect(options.port).toBe(443);
    expect(options.host).toBe('93.184.216.34');
    expect(options.servername).toBe('example.com');
    expect(options.method).toBe('POST');
    expect(options.headers).toMatchObject({
      'Host': 'example.com',
      'Accept-Encoding': 'identity',
      'Content-Length': '3',
      'X-Test': 'ok',
    });

    [
      { Connection: 'close' },
      { 'Proxy-Authorization': 'secret' },
      { 'Transfer-Encoding': 'chunked' },
      { Expect: '100-continue' },
      { ':authority': 'evil.example' },
      { 'Bad Header': 'x' },
      { 'X-Test': 'line\nbreak' },
      { 'X-Test': 'x'.repeat(8 * 1024 + 1) },
      Object.fromEntries(
        Array.from({ length: 65 }, (_, index) => [`X-${index}`, 'v'])
      ),
      Object.fromEntries(
        Array.from({ length: 5 }, (_, index) => [
          `X-${index}`,
          'x'.repeat(7 * 1024),
        ])
      ),
    ].forEach((headers) => expectInvalid({ headers }));
  });

  test('preserves repeated response headers separately from last-value map', () => {
    const maps = headersToMaps([
      'Set-Cookie',
      'a=1',
      'set-cookie',
      'b=2',
      'X-Test',
      'one',
      'Constructor',
      'two',
      '__proto__',
      'three',
      '__proto__',
      'four',
    ]);

    expect(maps.headers).toEqual({
      'set-cookie': 'b=2',
      'x-test': 'one',
      'constructor': 'two',
      ['__proto__']: 'four',
    });
    expect(maps.multiValueHeaders).toEqual({
      'set-cookie': ['a=1', 'b=2'],
      'x-test': ['one'],
      'constructor': ['two'],
      ['__proto__']: ['three', 'four'],
    });
  });

  test('treats fail-closed SNI errors as non-fallback failures', () => {
    expect(isSniFailClosedError(new Error('SNI_RESOURCE_LIMIT'))).toBe(true);
    expect(isSniFailClosedError(new Error('SNI_CANCELLED'))).toBe(true);
    expect(isSniFailClosedError(new Error('SNI_TLS_FAILED'))).toBe(true);
    expect(
      isSniFailClosedError(new Error('connect ssl.example.com failed'))
    ).toBe(false);
    expect(isSniFailClosedError(new Error('ECONNRESET'))).toBe(false);
  });

  test('uses one limiter bucket and HTTPS agent identity for equivalent IPv6 and mapped IPv4 forms', async () => {
    const limiter = new SniRequestLimiter(2, 1);
    const releaseActive = await limiter.acquire(
      'Example.com',
      '2001:4860:4860:0:0:0:0:8888'
    );
    let queuedStarted = false;
    const queuedReleasePromise = limiter
      .acquire('example.com', '2001:4860:4860::8888')
      .then((release) => {
        queuedStarted = true;
        return release;
      });

    await flushMicrotasks();
    expect(queuedStarted).toBe(false);
    expect(limiter.snapshot('example.com', '2001:4860:4860::8888')).toEqual({
      activeRequests: 1,
      activeRequestsForPair: 1,
      pendingRequests: 1,
      pendingRequestsForPair: 1,
    });

    releaseActive();
    const queuedRelease = await queuedReleasePromise;
    expect(queuedStarted).toBe(true);
    queuedRelease();

    const client = new SniNodeClient();
    const agent = client.getAgentState().agent as https.Agent & {
      getName(options: RequestOptions): string;
    };
    expect(
      agent.getName({
        host: '::ffff:93.184.216.34',
        servername: 'example.com',
        port: 443,
      })
    ).toBe(
      agent.getName({
        host: '93.184.216.34',
        servername: 'example.com',
        port: 443,
      })
    );
  });

  test('classifies desktop TLS and certificate errors as fail-closed', () => {
    const certError = Object.assign(
      new Error("Hostname/IP does not match certificate's altnames"),
      { code: 'ERR_TLS_CERT_ALTNAME_INVALID' }
    );
    expect(classifyTransportError(certError)).toMatchObject({
      code: 'SNI_CERT_FAILED',
      failClosed: true,
    });

    const tlsError = Object.assign(new Error('SSL handshake failed'), {
      code: 'EPROTO',
    });
    expect(classifyTransportError(tlsError)).toMatchObject({
      code: 'SNI_TLS_FAILED',
      failClosed: true,
    });
  });

  test('detects active proxy routes from resolveProxy output', async () => {
    const resolveProxy = jest.fn<Promise<string>, [string]>();
    const client = new SniNodeClient({ resolveProxy });

    expect(isProxyRouteActive('DIRECT')).toBe(false);
    expect(isProxyRouteActive('DIRECT; direct')).toBe(false);
    expect(isProxyRouteActive('PROXY 127.0.0.1:7890; DIRECT')).toBe(true);
    expect(isProxyRouteActive('SOCKS5 127.0.0.1:7890')).toBe(true);

    resolveProxy.mockResolvedValueOnce('DIRECT');
    await expect(
      client.isProxyActiveForUrl('https://example.com/')
    ).resolves.toBe(false);

    resolveProxy.mockResolvedValueOnce('PROXY 127.0.0.1:7890; DIRECT');
    await expect(
      client.isProxyActiveForUrl('https://example.com/')
    ).resolves.toBe(true);
  });

  test('reports unavailable proxy preflight when there is no route to inspect', async () => {
    for (const client of [
      new SniNodeClient(),
      new SniNodeClient({ resolveProxy: async () => ' ; ' }),
    ]) {
      await expect(
        client.isProxyActiveForUrl('https://example.com/')
      ).rejects.toMatchObject({
        code: 'SNI_UNSUPPORTED',
      });
    }
  });

  test('shares per-destination admission across clients', async () => {
    const firstClient = new SniNodeClient();
    const secondClient = new SniNodeClient();
    const requestSpy = jest
      .spyOn(https, 'request')
      .mockImplementation(
        ((options: RequestOptions) => new FakeClientRequest(options)) as never
      );
    const active = Array.from({ length: 16 }, () =>
      observeRejection(firstClient.request(baseConfig()))
    );
    const queued = observeRejection(secondClient.request(baseConfig()));

    try {
      await flushMicrotasks();
      expect(requestSpy).toHaveBeenCalledTimes(16);
      await expect(
        secondClient.getDebugSnapshot(baseConfig())
      ).resolves.toMatchObject({
        activeRequests: 16,
        activeRequestsForPair: 16,
        pendingRequests: 1,
        pendingRequestsForPair: 1,
      });
      await firstClient.cancelAllRequests();
      await flushMicrotasks();
      expect(requestSpy).toHaveBeenCalledTimes(17);
      await expect(
        secondClient.getDebugSnapshot(baseConfig())
      ).resolves.toMatchObject({
        activeRequests: 1,
        pendingRequests: 0,
      });
    } finally {
      await firstClient.cancelAllRequests();
      await secondClient.cancelAllRequests();
      await Promise.allSettled([...active, queued]);
      requestSpy.mockRestore();
    }
  });

  test.each([
    ['unsupported encoding', 'SNI_RESPONSE_FAILED'],
    ['upgrade', 'SNI_SECURITY_POLICY_FAILED'],
    ['close', 'SNI_REQUEST_FAILED'],
  ])('cleans up the transport and admission after %s', async (event, code) => {
    const client = new SniNodeClient();
    const socket = new Socket();
    const destroySocket = jest.spyOn(socket, 'destroy');
    const request = new FakeClientRequest({});
    const response = Object.assign(new EventEmitter(), {
      headers: { 'content-encoding': 'gzip' },
      resume: jest.fn(),
    }) as unknown as IncomingMessage;
    const requestSpy = jest.spyOn(https, 'request').mockImplementation(((
      _options: RequestOptions,
      callback: (message: IncomingMessage) => void
    ) => {
      request.once('response', callback);
      return request;
    }) as never);

    const pending = observeRejection(
      client.request({ ...baseConfig(), requestId: 'request' })
    );
    try {
      await flushMicrotasks();
      if (event === 'unsupported encoding') {
        request.emit('response', response);
        expect(response.resume).not.toHaveBeenCalled();
      } else if (event === 'upgrade') {
        request.emit('upgrade', response, socket);
        expect(destroySocket).toHaveBeenCalled();
      } else {
        request.emit('close');
      }
      await expect(pending).rejects.toMatchObject({ code });
      expect(request.destroy).toHaveBeenCalledTimes(1);
      await expect(client.cancelRequest('request')).resolves.toEqual({
        success: false,
      });
      await expect(
        client.getDebugSnapshot(baseConfig())
      ).resolves.toMatchObject({
        activeRequests: 0,
        pendingRequests: 0,
      });
    } finally {
      await client.cancelAllRequests();
      socket.destroy();
      requestSpy.mockRestore();
    }
  });

  test('queues the 17th per-pair and 65th global request, enforces 256 pending limit, and preserves FIFO', async () => {
    const pairLimiter = new SniRequestLimiter();
    const pairReleases = await Promise.all(
      Array.from({ length: 16 }, () =>
        pairLimiter.acquire('Example.com', '93.184.216.34')
      )
    );
    let pairQueuedStarted = false;
    const pairQueuedPromise = pairLimiter
      .acquire('example.com', '93.184.216.34')
      .then((release) => {
        pairQueuedStarted = true;
        return release;
      });
    await flushMicrotasks();
    expect(pairQueuedStarted).toBe(false);
    pairReleases[0]?.();
    const pairQueuedRelease = await pairQueuedPromise;
    expect(pairQueuedStarted).toBe(true);
    pairReleases.forEach((release) => release());
    pairQueuedRelease();

    const boundedLimiter = new SniRequestLimiter(1, 1, 2);
    const activeRelease = await boundedLimiter.acquire('a.example.com', 'ip-a');
    const firstPending = boundedLimiter.acquire('a.example.com', 'ip-a');
    const secondPending = boundedLimiter.acquire('b.example.com', 'ip-b');
    await expect(
      boundedLimiter.acquire('c.example.com', 'ip-c')
    ).rejects.toMatchObject({
      code: 'SNI_RESOURCE_LIMIT',
      failClosed: true,
    });
    activeRelease();
    (await firstPending)();
    (await secondPending)();

    const fifoLimiter = new SniRequestLimiter(2, 1);
    const releaseA = await fifoLimiter.acquire('a.example.com', 'ip-a');
    const releaseBlocker = await fifoLimiter.acquire(
      'blocker.example.com',
      'ip-b'
    );
    const order: string[] = [];
    const queuedA = fifoLimiter
      .acquire('a.example.com', 'ip-a')
      .then((release) => {
        order.push('a');
        return release;
      });
    const queuedRunnable = fifoLimiter
      .acquire('runnable.example.com', 'ip-c')
      .then((release) => {
        order.push('runnable');
        return release;
      });
    releaseBlocker();
    const releaseRunnable = await queuedRunnable;
    expect(order).toEqual(['runnable']);
    releaseA();
    const releaseQueuedA = await queuedA;
    expect(order).toEqual(['runnable', 'a']);
    releaseRunnable();
    releaseQueuedA();
  });

  test('handles duplicate requestId, queued timeout, remaining timeout, and slot release on sync throw', async () => {
    const requests: FakeClientRequest[] = [];
    const requestSpy = jest.spyOn(https, 'request').mockImplementation(((
      options: RequestOptions
    ) => {
      const request = new FakeClientRequest(options);
      requests.push(request);
      return request as never;
    }) as never);

    try {
      const client = new SniNodeClient();
      client.setRequestLimiter(new SniRequestLimiter(1, 1));
      const blocker = observeRejection(
        client.request({ ...baseConfig(), requestId: 'blocker' })
      );
      await flushMicrotasks();
      const firstPending = observeRejection(
        client.request({ ...baseConfig(), requestId: 'same' })
      );
      await flushMicrotasks();
      const replacementPending = observeRejection(
        client.request({ ...baseConfig(), requestId: 'same' })
      );

      await expect(firstPending).rejects.toMatchObject({
        code: 'SNI_CANCELLED',
      });
      await client.cancelAllRequests();
      await expect(replacementPending).rejects.toMatchObject({
        code: 'SNI_CANCELLED',
      });
      await expect(blocker).rejects.toMatchObject({ code: 'SNI_CANCELLED' });

      requestSpy.mockImplementationOnce(() => {
        throw new SniRequestError(
          'SNI_REQUEST_FAILED',
          'synchronous transport failure'
        );
      });
      await expect(client.request(baseConfig())).rejects.toThrow(
        'synchronous transport failure'
      );
      expect(client.getRequestLimiter().snapshot()).toEqual({
        activeRequests: 0,
        activeRequestsForPair: 0,
        pendingRequests: 0,
        pendingRequestsForPair: 0,
      });
    } finally {
      requestSpy.mockRestore();
    }
  });

  test('cancels queued and active requests and rotates agent on clearDNSCache', async () => {
    const requests: FakeClientRequest[] = [];
    const requestSpy = jest.spyOn(https, 'request').mockImplementation(((
      options: RequestOptions
    ) => {
      const request = new FakeClientRequest(options);
      requests.push(request);
      return request as never;
    }) as never);

    try {
      const client = new SniNodeClient();
      client.setRequestLimiter(new SniRequestLimiter(1, 1));
      const activePromise = observeRejection(
        client.request({ ...baseConfig(), requestId: 'active' })
      );
      await flushMicrotasks();
      const queuedPromise = observeRejection(
        client.request({ ...baseConfig(), requestId: 'queued' })
      );
      await flushMicrotasks();

      await expect(client.cancelRequest('queued')).resolves.toEqual({
        success: true,
      });
      await expect(queuedPromise).rejects.toMatchObject({
        code: 'SNI_CANCELLED',
        failClosed: true,
      });
      expect(requests).toHaveLength(1);

      const firstAgent = requests[0]?.options.agent as https.Agent;
      const firstAgentDestroySpy = jest.spyOn(firstAgent, 'destroy');
      const idleSocket = new Socket();
      const destroyIdleSocket = jest.spyOn(idleSocket, 'destroy');
      Object.assign(firstAgent.freeSockets, {
        'idle.example.com:8.8.8.8:443': [idleSocket],
      });
      await client.clearDNSCache();
      expect(destroyIdleSocket).toHaveBeenCalledTimes(1);
      expect(firstAgent.keepSocketAlive(idleSocket)).toBe(false);
      expect(firstAgentDestroySpy).not.toHaveBeenCalled();
      expect(requests[0]?.destroy).not.toHaveBeenCalled();

      await client.cancelAllRequests();
      await expect(activePromise).rejects.toMatchObject({
        code: 'SNI_CANCELLED',
      });
      expect(firstAgentDestroySpy).toHaveBeenCalledTimes(1);
    } finally {
      requestSpy.mockRestore();
    }
  });
});

describe('SniNodeClient.uploadFile and getUploadProgress', () => {
  const TMP_ROOT = fs.realpathSync(
    fs.mkdtempSync(path.join(os.tmpdir(), 'onekey-sni-node-upload-'))
  );
  const USER_DATA_DIR = path.join(TMP_ROOT, 'userData');
  const LOGS_DIR = path.join(TMP_ROOT, 'Logs', 'OneKey');
  const ARCHIVE_DIR = path.join(TMP_ROOT, 'Logs', 'logs_zip');
  const APP_TEMP_DIR = path.join(TMP_ROOT, 'temp');
  const OUTSIDE_DIR = path.join(TMP_ROOT, 'outside');
  const ARCHIVE_FILE = path.join(ARCHIVE_DIR, 'OneKeyLogs.zip');
  const ARCHIVE_BYTES = Buffer.from(
    // eslint-disable-next-line no-bitwise
    Array.from({ length: 200_000 }, (_value, index) => (index * 31 + 7) & 0xff)
  );

  let requestSpy: jest.SpyInstance;
  let requests: FakeUploadRequest[];
  let streamSpy: jest.SpyInstance;

  function baseUploadConfig(
    overrides: Partial<SniNodeUploadFileConfig> = {}
  ): SniNodeUploadFileConfig {
    return {
      ip: '93.184.216.34',
      hostname: 'wallet.onekeycn.com',
      path: '/wallet/v1/client/log',
      headers: {
        'content-type': 'application/zip',
        'Content-Length': '1',
      },
      method: 'POST',
      filePath: ARCHIVE_FILE,
      timeout: 30_000,
      ...overrides,
    };
  }

  function respond(
    callback: ((response: IncomingMessage) => void) | undefined,
    statusCode: number,
    body: string
  ): void {
    const response = new EventEmitter() as IncomingMessage;
    Object.assign(response, {
      statusCode,
      statusMessage: 'OK',
      headers: { 'content-type': 'application/json' },
      rawHeaders: ['Content-Type', 'application/json'],
      resume: jest.fn(),
    });
    callback?.(response);
    response.emit('data', Buffer.from(body));
    response.emit('end');
  }

  function makeClient(
    overrides: ConstructorParameters<typeof SniNodeClient>[0] = {}
  ): SniNodeClient {
    return new SniNodeClient({
      getAllowedUploadRoots: () => [ARCHIVE_DIR],
      ...overrides,
    });
  }

  beforeAll(() => {
    for (const dir of [
      USER_DATA_DIR,
      LOGS_DIR,
      ARCHIVE_DIR,
      APP_TEMP_DIR,
      OUTSIDE_DIR,
    ]) {
      fs.mkdirSync(dir, { recursive: true });
    }
    fs.writeFileSync(ARCHIVE_FILE, ARCHIVE_BYTES);
  });

  afterAll(() => {
    fs.rmSync(TMP_ROOT, { recursive: true, force: true });
  });

  beforeEach(() => {
    requests = [];
    streamSpy = jest.spyOn(fs.ReadStream.prototype, 'destroy');
    requestSpy = jest.spyOn(https, 'request').mockImplementation(((
      options: RequestOptions,
      callback?: (response: IncomingMessage) => void
    ) => {
      const request = new FakeUploadRequest(options);
      requests.push(request);
      request.on('finish', () => {
        setImmediate(() => respond(callback, 200, '{"code":0}'));
      });
      return request;
    }) as unknown as typeof https.request);
  });

  afterEach(() => {
    requestSpy.mockRestore();
    streamSpy.mockRestore();
  });

  test('streams the archive to the pinned IP with SNI and Content-Length', async () => {
    const response = await makeClient().uploadFile(baseUploadConfig());

    expect(response.statusCode).toBe(200);
    expect(response.body).toBe('{"code":0}');
    expect(requests).toHaveLength(1);
    const request = requests[0]!;
    expect(request.options).toMatchObject({
      method: 'POST',
      host: '93.184.216.34',
      port: 443,
      servername: 'wallet.onekeycn.com',
      path: '/wallet/v1/client/log',
      rejectUnauthorized: true,
    });
    expect(request.options.headers).toMatchObject({
      'Host': 'wallet.onekeycn.com',
      'Content-Length': String(ARCHIVE_BYTES.length),
      'content-type': 'application/zip',
    });
    expect(request.body.equals(ARCHIVE_BYTES)).toBe(true);
  });

  test('tracks getUploadProgress(requestId) and emits onUploadProgress while streaming, then clears on settlement', async () => {
    const onUploadProgress = jest.fn();
    let requestCreatedResolve: (() => void) | undefined;
    const requestCreated = new Promise<void>((resolve) => {
      requestCreatedResolve = resolve;
    });

    requestSpy.mockImplementationOnce(((
      options: RequestOptions,
      callback?: (response: IncomingMessage) => void
    ) => {
      const request = new FakeUploadRequest(options);
      request.autoFlushWrites = false;
      requests.push(request);
      requestCreatedResolve?.();
      request.on('finish', () => {
        setImmediate(() => respond(callback, 200, '{"code":0}'));
      });
      return request;
    }) as unknown as typeof https.request);

    const client = makeClient({ onUploadProgress });
    await expect(
      client.getUploadProgress('upload-progress-1')
    ).resolves.toBeNull();

    const uploadPromise = client.uploadFile(
      baseUploadConfig({
        requestId: 'upload-progress-1',
        filePath: pathToFileURL(ARCHIVE_FILE).href,
      })
    );
    await requestCreated;

    // Wait for the first stream data chunk to be written to the request.
    while ((requests[0]?.pendingWriteCallbacks.length ?? 0) === 0) {
      await new Promise((resolve) => setImmediate(resolve));
    }

    // Before the write callback completes, bytesSent is 0 and totalBytes is ARCHIVE_BYTES.length.
    await expect(
      client.getUploadProgress('upload-progress-1')
    ).resolves.toEqual({
      bytesSent: 0,
      totalBytes: ARCHIVE_BYTES.length,
    });

    // Flush writes as they arrive.
    const activeRequest = requests[0]!;
    activeRequest.autoFlushWrites = true;
    while (activeRequest.pendingWriteCallbacks.length > 0) {
      const cb = activeRequest.pendingWriteCallbacks.shift();
      cb?.();
    }

    await expect(uploadPromise).resolves.toMatchObject({ statusCode: 200 });
    expect(onUploadProgress).toHaveBeenCalledWith(
      expect.objectContaining({
        requestId: 'upload-progress-1',
        bytesSent: ARCHIVE_BYTES.length,
        totalBytes: ARCHIVE_BYTES.length,
      })
    );

    // Snapshot is removed on settlement.
    await expect(
      client.getUploadProgress('upload-progress-1')
    ).resolves.toBeNull();
  });

  test('returns null from getUploadProgress for non-upload requests and cancelled uploads', async () => {
    let notifyStarted: (() => void) | undefined;
    requestSpy.mockImplementation(((options: RequestOptions) => {
      const request = new FakeUploadRequest(options);
      requests.push(request);
      notifyStarted?.();
      return request;
    }) as unknown as typeof https.request);

    const client = makeClient();
    const regularReq = observeRejection(
      client.request({ ...baseConfig(), requestId: 'regular-1' })
    );
    await flushMicrotasks();
    await expect(client.getUploadProgress('regular-1')).resolves.toBeNull();
    await client.cancelRequest('regular-1');
    await expect(regularReq).rejects.toMatchObject({ code: 'SNI_CANCELLED' });

    const uploadStarted = new Promise<void>((resolve) => {
      notifyStarted = resolve;
    });
    const uploadReq = observeRejection(
      client.uploadFile(baseUploadConfig({ requestId: 'upload-cancel-1' }))
    );
    await expect(client.getUploadProgress('upload-cancel-1')).resolves.toEqual({
      bytesSent: 0,
      totalBytes: 0,
    });
    await uploadStarted;
    await expect(client.getUploadProgress('upload-cancel-1')).resolves.toEqual({
      bytesSent: expect.any(Number),
      totalBytes: ARCHIVE_BYTES.length,
    });
    await client.cancelRequest('upload-cancel-1');
    await expect(
      client.getUploadProgress('upload-cancel-1')
    ).resolves.toBeNull();
    await expect(uploadReq).rejects.toMatchObject({ code: 'SNI_CANCELLED' });
  });

  test('rejects invalid upload destinations, unverified archives, symlinks and synchronous transport errors', async () => {
    const client = makeClient({
      verifyUploadFile: () => false,
    });
    await expect(client.uploadFile(baseUploadConfig())).rejects.toThrow(
      new SniRequestError(
        'SNI_INVALID_CONFIG',
        'Upload file was not collected by the main process',
        true
      )
    );

    const outsideFile = path.join(OUTSIDE_DIR, 'secret.bin');
    fs.writeFileSync(outsideFile, 'secret');
    const symlink = path.join(ARCHIVE_DIR, 'link.zip');
    if (!fs.existsSync(symlink)) {
      fs.symlinkSync(outsideFile, symlink);
    }
    await expect(
      makeClient().uploadFile(baseUploadConfig({ filePath: symlink }))
    ).rejects.toMatchObject({
      code: 'SNI_INVALID_CONFIG',
      failClosed: true,
    });
    await expect(
      makeClient().uploadFile(
        baseUploadConfig({ hostname: 'wallet.example.com' })
      )
    ).rejects.toMatchObject({
      code: 'SNI_INVALID_CONFIG',
      failClosed: true,
    });
    await expect(
      makeClient().request({
        ...baseConfig(),
        bodyFile: {
          filePath: ARCHIVE_FILE,
          sizeBytes: ARCHIVE_BYTES.length,
        },
      } as never)
    ).rejects.toMatchObject({
      code: 'SNI_INVALID_CONFIG',
      failClosed: true,
    });
    await expect(
      makeClient().uploadFile({
        ...baseUploadConfig(),
        body: 'text',
      } as SniNodeUploadFileConfig)
    ).rejects.toMatchObject({
      code: 'SNI_INVALID_CONFIG',
      failClosed: true,
    });
  });

  test.each(['cancel by ID', 'cancel without ID', 'timeout'])(
    'settles an upload blocked in validation on %s',
    async (action) => {
      jest.useFakeTimers();
      let onValidationStarted!: () => void;
      let finishValidation!: (roots: string[]) => void;
      const validationStarted = new Promise<void>((resolve) => {
        onValidationStarted = resolve;
      });
      const validation = new Promise<string[]>((resolve) => {
        finishValidation = resolve;
      });
      const verifyUploadFile = jest.fn(() => true);
      const client = makeClient({
        getAllowedUploadRoots: () => {
          onValidationStarted();
          return validation;
        },
        verifyUploadFile,
      });
      const requestId =
        action === 'cancel without ID' ? undefined : 'validating';
      const result = observeRejection(
        client.uploadFile(baseUploadConfig({ requestId, timeout: 100 }))
      );
      try {
        await validationStarted;
        if (action === 'timeout') {
          jest.advanceTimersByTime(100);
        } else if (requestId) {
          await expect(client.cancelRequest(requestId)).resolves.toEqual({
            success: true,
          });
        } else {
          await client.cancelAllRequests();
        }

        await expect(result).rejects.toMatchObject({
          code: action === 'timeout' ? 'SNI_TIMEOUT' : 'SNI_CANCELLED',
        });
        await expect(
          client.getUploadProgress('validating')
        ).resolves.toBeNull();
        await expect(client.cancelRequest('validating')).resolves.toEqual({
          success: false,
        });
        finishValidation([ARCHIVE_DIR]);
        await flushMicrotasks();
        expect(verifyUploadFile).not.toHaveBeenCalled();
        expect(requestSpy).not.toHaveBeenCalled();
      } finally {
        finishValidation([ARCHIVE_DIR]);
        await client.cancelAllRequests();
        jest.useRealTimers();
      }
    }
  );

  test('uses the original upload deadline while waiting for admission', async () => {
    jest.useFakeTimers();
    let onVerificationStarted!: () => void;
    let finishVerification!: (verified: boolean) => void;
    const verificationStarted = new Promise<void>((resolve) => {
      onVerificationStarted = resolve;
    });
    const verification = new Promise<boolean>((resolve) => {
      finishVerification = resolve;
    });
    const client = makeClient({
      verifyUploadFile: () => {
        onVerificationStarted();
        return verification;
      },
    });
    const limiter = new SniRequestLimiter(1, 1);
    client.setRequestLimiter(limiter);
    const release = await limiter.acquire(
      'wallet.onekeycn.com',
      '93.184.216.34'
    );
    const result = observeRejection(
      client.uploadFile(baseUploadConfig({ timeout: 100 }))
    );

    try {
      await verificationStarted;
      jest.advanceTimersByTime(75);
      finishVerification(true);
      await jest.advanceTimersByTimeAsync(0);
      expect(limiter.snapshot().pendingRequests).toBe(1);
      jest.advanceTimersByTime(25);
      await expect(result).rejects.toMatchObject({ code: 'SNI_TIMEOUT' });
      expect(limiter.snapshot().pendingRequests).toBe(0);
      expect(requestSpy).not.toHaveBeenCalled();
    } finally {
      finishVerification(true);
      await client.cancelAllRequests();
      release();
      jest.useRealTimers();
    }
  });

  test('keeps the archive path out of file stream errors and logs', async () => {
    const logger = createMockLogger();
    const filePath = path.join(ARCHIVE_DIR, 'removed-before-read.zip');
    fs.writeFileSync(filePath, 'archive');
    const client = makeClient({
      logger,
      verifyUploadFile: () => {
        fs.unlinkSync(filePath);
        return true;
      },
    });

    await expect(
      client.uploadFile(baseUploadConfig({ filePath }))
    ).rejects.toMatchObject({
      code: 'SNI_FILE_READ_FAILED',
      message: 'SNI_FILE_READ_FAILED: Upload file read failed',
    });
    expect(logger.error).toHaveBeenCalledTimes(1);
    expect(logger.error.mock.calls[0]?.[0]).not.toContain(filePath);
  });

  test('times out stalled uploads, classifies pre-write ECONNREFUSED, and destroys incomplete requests on early reply', async () => {
    requestSpy.mockImplementationOnce(((options: RequestOptions) => {
      const request = new FakeUploadRequest(options);
      requests.push(request);
      return request;
    }) as unknown as typeof https.request);

    await expect(
      makeClient().uploadFile(baseUploadConfig({ timeout: 50 }))
    ).rejects.toMatchObject({
      code: 'SNI_TIMEOUT',
    });
    expect(requests[0]?.destroy).toHaveBeenCalled();
    expect(streamSpy).toHaveBeenCalled();

    requestSpy.mockImplementationOnce(((options: RequestOptions) => {
      const request = new FakeUploadRequest(options);
      requests.push(request);
      setImmediate(() => {
        request.destroy(
          Object.assign(new Error('connect ECONNREFUSED 93.184.216.34:443'), {
            code: 'ECONNREFUSED',
          })
        );
      });
      return request;
    }) as unknown as typeof https.request);

    await expect(
      makeClient().uploadFile(baseUploadConfig())
    ).rejects.toMatchObject({
      code: 'SNI_CONNECTION_REFUSED',
    });

    streamSpy.mockClear();
    requestSpy.mockImplementationOnce(((
      options: RequestOptions,
      callback?: (response: IncomingMessage) => void
    ) => {
      const request = new FakeUploadRequest(options);
      requests.push(request);
      setImmediate(() => respond(callback, 413, '{"code":413}'));
      return request;
    }) as unknown as typeof https.request);

    await expect(
      makeClient().uploadFile(baseUploadConfig())
    ).resolves.toMatchObject({
      statusCode: 413,
    });
    expect(requests[2]?.destroy).toHaveBeenCalled();
    expect(streamSpy).toHaveBeenCalled();
  });

  test('keeps long diagnostic requestIds recoverable with uri-hex32 format', async () => {
    const logger = createMockLogger();
    const client = makeClient({ logger });
    const rawId = 'a'.repeat(64);
    const groupedId = `${'a'.repeat(32)}-${'a'.repeat(32)}`;

    await client.cancelRequest(rawId);
    const rawLog = String(logger.warn.mock.calls.at(-1)?.[0] ?? '');
    expect(rawLog).toContain(
      `requestId=${groupedId} requestIdFormat=uri-hex32`
    );

    await client.cancelRequest(groupedId);
    const groupedLog = String(logger.warn.mock.calls.at(-1)?.[0] ?? '');
    expect(groupedLog).toContain(`requestId=${groupedId}`);
    expect(groupedLog).not.toContain('requestIdFormat=');
  });
});
