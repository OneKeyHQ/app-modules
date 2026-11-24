import { configureWebQrScanner } from '../engines';
import { createWebQrScanner } from '../scanner';

const nativeEngine = {
  getSupportedFormats: async () => ['qr_code'],
};

function fakeStream() {
  const track = { stop: jest.fn(), readyState: 'live' };
  return { track, stream: { getTracks: () => [track], getVideoTracks: () => [track] } };
}

function fakeVideo(play: () => Promise<void>) {
  return { play: jest.fn(play), srcObject: null as unknown, muted: false, playsInline: false } as unknown as HTMLVideoElement;
}

function stageVideo(video: HTMLVideoElement) {
  Object.defineProperty(globalThis, 'document', {
    configurable: true,
    value: { createElement: jest.fn(() => video) },
  });
}

function deferred<T>() {
  let resolve!: (value: T) => void;
  let reject!: (error: Error) => void;
  const promise = new Promise<T>((yes, no) => {
    resolve = yes;
    reject = no;
  });
  return { promise, resolve, reject };
}

beforeEach(() => {
  (globalThis as { BarcodeDetector?: unknown }).BarcodeDetector = Object.assign(
    class {
      async detect() {
        return [];
      }
    },
    nativeEngine
  );
  configureWebQrScanner({});
});

afterEach(() => {
  delete (globalThis as { BarcodeDetector?: unknown }).BarcodeDetector;
  Reflect.deleteProperty(globalThis, 'document');
  jest.useRealTimers();
});

function setGetUserMedia(impl: () => Promise<unknown>) {
  Object.defineProperty(globalThis.navigator, 'mediaDevices', {
    configurable: true,
    value: { getUserMedia: jest.fn(impl) },
  });
}

describe('createWebQrScanner camera start', () => {
  it('fails with camera-busy and releases the camera when no frame arrives', async () => {
    jest.useFakeTimers();
    const { track, stream } = fakeStream();
    setGetUserMedia(async () => stream);
    const video = fakeVideo(() => new Promise(() => {}));
    const onError = jest.fn();
    const onReady = jest.fn();
    const scanner = createWebQrScanner({ video, mode: 'auto', onError, onReady, firstFrameTimeoutMs: 1000 });
    const starting = scanner.start();
    await jest.advanceTimersByTimeAsync(1000);
    await starting;
    expect(onError).toHaveBeenCalledWith(expect.objectContaining({ code: 'camera-busy' }));
    expect(onReady).not.toHaveBeenCalled();
    expect(track.stop).toHaveBeenCalled();
    expect(video.srcObject).toBeNull();
  });

  it('cancels startup before camera access when engine discovery is still pending', async () => {
    const formats = deferred<string[]>();
    (globalThis as { BarcodeDetector?: unknown }).BarcodeDetector = Object.assign(
      class {}, { getSupportedFormats: () => formats.promise }
    );
    setGetUserMedia(async () => fakeStream().stream);
    const scanner = createWebQrScanner({ video: fakeVideo(async () => {}), mode: 'auto' });
    const start = scanner.start();
    scanner.stop();
    formats.resolve(['qr_code']);
    await start;
    expect(navigator.mediaDevices.getUserMedia).not.toHaveBeenCalled();
  });

  it('cancels the fallback when the exact camera request fails after stop', async () => {
    const attempt = deferred<unknown>();
    setGetUserMedia(() => attempt.promise);
    const scanner = createWebQrScanner({
      video: fakeVideo(async () => {}), mode: 'auto', source: { kind: 'camera', deviceId: 'camera-a' },
    });
    const start = scanner.start();
    const open = navigator.mediaDevices.getUserMedia as jest.Mock;
    while (!open.mock.calls.length) await new Promise((resolve) => setImmediate(resolve));
    scanner.stop();
    attempt.reject(Object.assign(new Error('gone'), { name: 'NotFoundError' }));
    await start;
    expect(open).toHaveBeenCalledTimes(1);
  });

  it('reports a missing decoder before requesting camera access', async () => {
    configureWebQrScanner({ disableNativeDetector: true });
    setGetUserMedia(async () => fakeStream().stream);
    const onError = jest.fn();
    const onReady = jest.fn();
    const scanner = createWebQrScanner({ video: fakeVideo(async () => {}), mode: 'auto', onError, onReady });
    await scanner.start();
    expect(onError).toHaveBeenCalledWith(expect.objectContaining({ code: 'decoder-unavailable' }));
    expect(onReady).not.toHaveBeenCalled();
    expect(navigator.mediaDevices.getUserMedia).not.toHaveBeenCalled();
  });

  it.each([false, true])('reacquires a selection preempted by a stale WebKit open (stopped=%s)', async (stopped) => {
    const original = fakeStream();
    const stale = fakeStream();
    const recovered = fakeStream();
    const openingStale = deferred<unknown>();
    const calls: (string | undefined)[] = [];
    const open = jest.fn((constraints: MediaStreamConstraints) => {
      const { deviceId } = constraints.video as MediaTrackConstraints;
      calls.push((deviceId as { exact?: string }).exact);
      if (calls.length === 1) return Promise.resolve(original.stream);
      if (calls.length === 2) return openingStale.promise;
      return Promise.resolve(recovered.stream);
    });
    Object.defineProperty(navigator, 'mediaDevices', {
      configurable: true, value: { getUserMedia: open },
    });
    stageVideo(fakeVideo(async () => {}));
    const video = fakeVideo(async () => {});
    const scanner = createWebQrScanner({
      video, mode: 'auto', source: { kind: 'camera', deviceId: 'camera-a' },
    });
    try {
      await scanner.start();
      const switching = scanner.switchCamera('camera-b');
      while (calls.length < 2) await new Promise((resolve) => setImmediate(resolve));
      await scanner.switchCamera('camera-a');
      if (stopped) scanner.stop();
      original.track.readyState = 'ended';
      openingStale.resolve(stale.stream);
      await switching;
      expect(stale.track.stop).toHaveBeenCalledTimes(1);
      expect(calls).toEqual(stopped ? ['camera-a', 'camera-b'] : ['camera-a', 'camera-b', 'camera-a']);
      expect(video.srcObject).toBe(stopped ? null : recovered.stream);
    } finally {
      scanner.dispose();
    }
  });

  it.each([
    { label: 'unavailable camera', busy: false, success: false },
    { label: 'successful switch', busy: false, success: true },
    { label: 'successful busy retry', busy: true, success: true },
    { label: 'failed busy retry', busy: true, success: false },
  ])('keeps recovery pending through the newest switch ($label)', async ({ busy, success }) => {
    const original = fakeStream();
    const stale = fakeStream();
    const newest = fakeStream();
    const recovered = fakeStream();
    const openingStale = deferred<unknown>();
    const openingNewest = deferred<unknown>();
    const calls: (string | undefined)[] = [];
    const busyError = () => Object.assign(new Error('camera busy'), { name: 'NotReadableError' });
    const open = jest.fn((constraints: MediaStreamConstraints) => {
      const { deviceId } = constraints.video as MediaTrackConstraints;
      calls.push((deviceId as { exact?: string }).exact);
      if (calls.length === 1) return Promise.resolve(original.stream);
      if (calls.length === 2) return openingStale.promise;
      if (calls.length === 3) return openingNewest.promise;
      if (busy && calls.length === 4) {
        return success ? Promise.resolve(newest.stream) : Promise.reject(busyError());
      }
      return Promise.resolve(recovered.stream);
    });
    Object.defineProperty(navigator, 'mediaDevices', {
      configurable: true, value: { getUserMedia: open },
    });
    stageVideo(fakeVideo(async () => {}));
    const video = fakeVideo(async () => {});
    const onError = jest.fn();
    const scanner = createWebQrScanner({
      video, mode: 'auto', source: { kind: 'camera', deviceId: 'camera-a' }, onError,
    });
    try {
      await scanner.start();
      const switchB = scanner.switchCamera('camera-b');
      while (calls.length < 2) await new Promise((resolve) => setImmediate(resolve));
      const switchC = scanner.switchCamera('camera-c');
      while (calls.length < 3) await new Promise((resolve) => setImmediate(resolve));

      original.track.readyState = 'ended';
      openingStale.resolve(stale.stream);
      await switchB;
      expect(stale.track.stop).toHaveBeenCalledTimes(1);
      expect(calls).toEqual(['camera-a', 'camera-b', 'camera-c']);

      if (busy) openingNewest.reject(busyError());
      else if (success) openingNewest.resolve(newest.stream);
      else openingNewest.reject(Object.assign(new Error('disconnected'), { name: 'NotFoundError' }));
      await switchC;

      expect(video.srcObject).toBe(success ? newest.stream : recovered.stream);
      expect(onError).toHaveBeenCalledTimes(success ? 0 : 1);
      const expected = ['camera-a', 'camera-b', 'camera-c'];
      if (busy) expected.push('camera-c');
      if (!success) expected.push('camera-a');
      expect(calls).toEqual(expected);
    } finally {
      scanner.dispose();
    }
  });

  it.each([
    { label: 'cleanup after the busy result', stopped: false, cleanupFirst: false },
    { label: 'cleanup before the busy result', stopped: false, cleanupFirst: true },
    { label: 'scanner stopped before cleanup', stopped: true, cleanupFirst: false },
  ])('handles a busy restore with $label', async ({ stopped, cleanupFirst }) => {
    const original = fakeStream();
    const unrelated = fakeStream();
    const stale = fakeStream();
    const recovered = fakeStream();
    const waitingForPermission = deferred<unknown>();
    const openingStale = deferred<unknown>();
    const restoring = deferred<unknown>();
    const calls: string[] = [];
    const busy = Object.assign(new Error('camera busy'), { name: 'NotReadableError' });
    const open = jest.fn((constraints: MediaStreamConstraints) => {
      const { deviceId } = constraints.video as MediaTrackConstraints;
      calls.push((deviceId as { exact: string }).exact);
      if (calls.length === 1) return Promise.resolve(original.stream);
      if (calls.length === 2) return waitingForPermission.promise;
      if (calls.length === 3) return openingStale.promise;
      if (calls.length === 6) return restoring.promise;
      if (stale.track.stop.mock.calls.length === 0) return Promise.reject(busy);
      return Promise.resolve(recovered.stream);
    });
    Object.defineProperty(navigator, 'mediaDevices', {
      configurable: true, value: { getUserMedia: open },
    });
    const video = fakeVideo(async () => {});
    const onError = jest.fn();
    const scanner = createWebQrScanner({
      video, mode: 'auto', source: { kind: 'camera', deviceId: 'camera-a' }, onError,
    });
    try {
      await scanner.start();
      const switchB = scanner.switchCamera('camera-b');
      while (calls.length < 2) await new Promise((resolve) => setImmediate(resolve));
      const switchC = scanner.switchCamera('camera-c');
      while (calls.length < 3) await new Promise((resolve) => setImmediate(resolve));
      original.track.readyState = 'ended';
      const switchD = scanner.switchCamera('camera-d');
      await new Promise((resolve) => setImmediate(resolve));
      expect(calls).toEqual(['camera-a', 'camera-b', 'camera-c', 'camera-d', 'camera-d', 'camera-a']);
      expect(onError).toHaveBeenCalledTimes(1);

      if (cleanupFirst) {
        openingStale.resolve(stale.stream);
        await switchC;
      }
      restoring.reject(busy);
      await switchD;
      if (stopped) scanner.stop();
      if (!cleanupFirst) {
        openingStale.resolve(stale.stream);
        await switchC;
      }
      expect(stale.track.stop).toHaveBeenCalledTimes(1);
      expect(calls.slice(6)).toEqual(stopped ? [] : ['camera-a']);
      expect(video.srcObject).toBe(stopped ? null : recovered.stream);
      waitingForPermission.resolve(unrelated.stream);
      await switchB;
      expect(unrelated.track.stop).toHaveBeenCalledTimes(1);
      expect(calls.slice(6)).toEqual(stopped ? [] : ['camera-a']);
    } finally {
      scanner.dispose();
    }
  });

  it('restores an available camera while an unrelated permission request remains pending', async () => {
    const original = fakeStream();
    const unrelated = fakeStream();
    const stale = fakeStream();
    const recovered = fakeStream();
    const waitingForPermission = deferred<unknown>();
    const openingStale = deferred<unknown>();
    const open = jest.fn()
      .mockResolvedValueOnce(original.stream)
      .mockReturnValueOnce(waitingForPermission.promise)
      .mockReturnValueOnce(openingStale.promise)
      .mockResolvedValueOnce(recovered.stream);
    setGetUserMedia(open);
    const video = fakeVideo(async () => {});
    const onError = jest.fn();
    const scanner = createWebQrScanner({
      video, mode: 'auto', source: { kind: 'camera', deviceId: 'camera-a' }, onError,
    });
    try {
      await scanner.start();
      const switchB = scanner.switchCamera('camera-b');
      while (open.mock.calls.length < 2) await new Promise((resolve) => setImmediate(resolve));
      const switchC = scanner.switchCamera('camera-c');
      while (open.mock.calls.length < 3) await new Promise((resolve) => setImmediate(resolve));
      await scanner.switchCamera('camera-a');

      original.track.readyState = 'ended';
      openingStale.resolve(stale.stream);
      await switchC;
      expect(stale.track.stop).toHaveBeenCalledTimes(1);
      expect(open).toHaveBeenLastCalledWith(expect.objectContaining({
        video: expect.objectContaining({ deviceId: { exact: 'camera-a' } }),
      }));
      expect(video.srcObject).toBe(recovered.stream);
      expect(onError).not.toHaveBeenCalled();

      waitingForPermission.resolve(unrelated.stream);
      await switchB;
      expect(unrelated.track.stop).toHaveBeenCalledTimes(1);
    } finally {
      scanner.dispose();
    }
  });

  it('maps getUserMedia errors to error codes', async () => {
    for (const [name, code] of [
      ['NotAllowedError', 'permission-denied'],
      ['NotFoundError', 'no-camera'],
      ['NotReadableError', 'camera-busy'],
    ] as const) {
      setGetUserMedia(async () => {
        throw Object.assign(new Error(name), { name });
      });
      const onError = jest.fn();
      await createWebQrScanner({ video: fakeVideo(async () => {}), mode: 'auto', onError }).start();
      expect(onError).toHaveBeenCalledWith(expect.objectContaining({ code }));
    }
  });

  it('stops a stream that arrives after stop()', async () => {
    const { track, stream } = fakeStream();
    let resolveStream: (value: unknown) => void = () => {};
    setGetUserMedia(() => new Promise((resolve) => { resolveStream = resolve; }));
    const onReady = jest.fn();
    const scanner = createWebQrScanner({ video: fakeVideo(async () => {}), mode: 'auto', onReady });
    const starting = scanner.start();
    // start() prepares the engines before asking for the camera.
    const getUserMedia = navigator.mediaDevices.getUserMedia as jest.Mock;
    while (getUserMedia.mock.calls.length === 0) {
      await new Promise((resolve) => setTimeout(resolve, 0));
    }
    scanner.stop();
    resolveStream(stream);
    await starting;
    expect(track.stop).toHaveBeenCalled();
    expect(onReady).not.toHaveBeenCalled();
  });

  it('reports raw frame text with the native repeat throttle', async () => {
    jest.useFakeTimers();
    const texts = ['ur:a/1-2/x', 'ur:a/1-2/x', 'ur:a/2-2/y', 'ur:a/2-2/y'];
    let index = 0;
    (globalThis as { BarcodeDetector?: unknown }).BarcodeDetector = Object.assign(
      class {
        async detect() {
          const text = texts[Math.min(index, texts.length - 1)];
          index += 1;
          return [{ rawValue: text }];
        }
      },
      nativeEngine
    );
    configureWebQrScanner({});
    const context = {
      drawImage: jest.fn(),
      getImageData: () => ({ data: new Uint8ClampedArray(4) }),
    };
    const canvas = { width: 0, height: 0, getContext: () => context };
    (globalThis as { OffscreenCanvas?: unknown }).OffscreenCanvas = jest.fn(() => canvas);
    (globalThis as { ImageData?: unknown }).ImageData ??= class {};
    let time = 0;
    const video = {
      videoWidth: 1,
      videoHeight: 1,
      get currentTime() {
        time += 1;
        return time;
      },
    } as unknown as HTMLVideoElement;
    const onFrameText = jest.fn();
    const scanner = createWebQrScanner({
      video,
      source: { kind: 'element' },
      mode: 'static',
      onFrameText,
    });
    await scanner.start();
    for (let step = 0; step < 6; step += 1) {
      await jest.advanceTimersByTimeAsync(100);
    }
    scanner.dispose();
    delete (globalThis as { OffscreenCanvas?: unknown }).OffscreenCanvas;
    expect(onFrameText.mock.calls.map(([text]) => text)).toEqual([
      'ur:a/1-2/x',
      'ur:a/2-2/y',
    ]);
  });

  it('drops the decode across reset and still assembles when the raw observer throws', async () => {
    jest.useFakeTimers();
    const outstanding = deferred<{ rawValue: string }[]>();
    const detect = jest.fn().mockReturnValueOnce(outstanding.promise)
      .mockResolvedValue([{ rawValue: 'current QR' }]);
    (globalThis as { BarcodeDetector?: unknown }).BarcodeDetector = Object.assign(
      class { detect = detect; },
      nativeEngine
    );
    const context = {
      drawImage: jest.fn(),
      getImageData: () => ({ data: new Uint8ClampedArray(4) }),
    };
    (globalThis as { OffscreenCanvas?: unknown }).OffscreenCanvas = jest.fn(() => ({
      width: 0, height: 0, getContext: () => context,
    }));
    (globalThis as { ImageData?: unknown }).ImageData ??= class {};
    let time = 0;
    const video = {
      videoWidth: 1, videoHeight: 1,
      get currentTime() { return ++time; },
    } as unknown as HTMLVideoElement;
    const onError = jest.fn();
    const onResult = jest.fn();
    const onFrameText = jest.fn(() => { throw new Error('consumer failure'); });
    const scanner = createWebQrScanner({
      video, source: { kind: 'element' }, mode: 'static', onFrameText, onResult, onError,
    });
    try {
      await scanner.start();
      await jest.advanceTimersByTimeAsync(70);
      expect(detect).toHaveBeenCalledTimes(1);
      scanner.resetSession();
      outstanding.resolve([{ rawValue: 'previous QR' }]);
      await jest.advanceTimersByTimeAsync(70);
      expect(onFrameText.mock.calls).toEqual([['current QR']]);
      expect(onResult.mock.calls).toEqual([[{ kind: 'text', text: 'current QR' }]]);
      expect(onError).not.toHaveBeenCalled();
    } finally {
      scanner.dispose();
      delete (globalThis as { OffscreenCanvas?: unknown }).OffscreenCanvas;
    }
  });

  it('switches cameras without dropping the old one first', async () => {
    const first = fakeStream();
    const second = fakeStream();
    stageVideo(fakeVideo(async () => {}));
    const order: string[] = [];
    first.track.stop.mockImplementation(() => order.push('stop first'));
    let opened = 0;
    const getUserMedia = jest.fn(async (constraints: MediaStreamConstraints) => {
      const video = constraints.video as MediaTrackConstraints;
      order.push(`open ${(video.deviceId as { exact?: string } | undefined)?.exact ?? 'default'}`);
      opened += 1;
      return opened === 1 ? first.stream : second.stream;
    });
    Object.defineProperty(globalThis.navigator, 'mediaDevices', {
      configurable: true,
      value: { getUserMedia },
    });
    const video = fakeVideo(async () => {});
    const onReady = jest.fn();
    const scanner = createWebQrScanner({ video, mode: 'auto', onReady });
    await scanner.start();
    await scanner.switchCamera('cam-b');
    expect(order).toEqual(['open default', 'open cam-b', 'stop first']);
    expect(video.srcObject).toBe(second.stream);
    expect(onReady).toHaveBeenCalledTimes(2);
    scanner.dispose();
  });

  it('keeps the active camera when a candidate never starts playing', async () => {
    jest.useFakeTimers();
    const first = fakeStream();
    const second = fakeStream();
    stageVideo(fakeVideo(() => new Promise(() => {})));
    setGetUserMedia(jest.fn().mockResolvedValueOnce(first.stream).mockResolvedValueOnce(second.stream));
    const video = fakeVideo(async () => {});
    const onError = jest.fn();
    const scanner = createWebQrScanner({ video, mode: 'auto', onError, firstFrameTimeoutMs: 1000 });
    await scanner.start();
    const switching = scanner.switchCamera('camera-b');
    await jest.advanceTimersByTimeAsync(1000);
    await switching;
    expect(video.srcObject).toBe(first.stream);
    expect(first.track.stop).not.toHaveBeenCalled();
    expect(second.track.stop).toHaveBeenCalled();
    expect(onError).toHaveBeenCalledWith(expect.objectContaining({ code: 'camera-busy' }));
    scanner.dispose();
  });

  it('returning to the current camera invalidates a pending switch', async () => {
    const first = fakeStream();
    const second = fakeStream();
    const pending = deferred<unknown>();
    setGetUserMedia(jest.fn().mockResolvedValueOnce(first.stream).mockReturnValueOnce(pending.promise));
    const video = fakeVideo(async () => {});
    const scanner = createWebQrScanner({ video, mode: 'auto' });
    await scanner.start();
    const switching = scanner.switchCamera('camera-b');
    await new Promise((resolve) => setImmediate(resolve));
    await scanner.switchCamera();
    pending.resolve(second.stream);
    await switching;
    expect(video.srcObject).toBe(first.stream);
    expect(first.track.stop).not.toHaveBeenCalled();
    expect(second.track.stop).toHaveBeenCalledTimes(1);
    scanner.dispose();
  });

  it('retakes a selected stream whose track ended', async () => {
    const first = fakeStream();
    const second = fakeStream();
    stageVideo(fakeVideo(async () => {}));
    setGetUserMedia(jest.fn().mockResolvedValueOnce(first.stream).mockResolvedValueOnce(second.stream));
    const video = fakeVideo(async () => {});
    const scanner = createWebQrScanner({ video, mode: 'auto' });
    await scanner.start();
    first.track.readyState = 'ended';
    await scanner.switchCamera();
    expect(video.srcObject).toBe(second.stream);
    scanner.dispose();
  });

  it('releases the current camera and retries when only one can be open', async () => {
    const first = fakeStream();
    const second = fakeStream();
    let calls = 0;
    const getUserMedia = jest.fn(async () => {
      calls += 1;
      if (calls === 1) return first.stream;
      if (calls === 2) throw Object.assign(new Error('busy'), { name: 'NotReadableError' });
      return second.stream;
    });
    Object.defineProperty(globalThis.navigator, 'mediaDevices', {
      configurable: true,
      value: { getUserMedia },
    });
    const video = fakeVideo(async () => {});
    const onError = jest.fn();
    const scanner = createWebQrScanner({ video, mode: 'auto', onError });
    await scanner.start();
    await scanner.switchCamera('cam-b');
    expect(first.track.stop).toHaveBeenCalled();
    expect(getUserMedia).toHaveBeenCalledTimes(3);
    expect(video.srcObject).toBe(second.stream);
    expect(onError).not.toHaveBeenCalled();
    scanner.dispose();
  });

  it('only records the camera while stopped', async () => {
    const getUserMedia = jest.fn(async () => fakeStream().stream);
    Object.defineProperty(globalThis.navigator, 'mediaDevices', {
      configurable: true,
      value: { getUserMedia },
    });
    const scanner = createWebQrScanner({ video: fakeVideo(async () => {}), mode: 'auto' });
    await scanner.switchCamera('cam-b');
    expect(getUserMedia).not.toHaveBeenCalled();
    await scanner.start();
    const video = (getUserMedia.mock.calls[0] as unknown as [MediaStreamConstraints])[0]
      .video as MediaTrackConstraints;
    expect(video.deviceId).toEqual({ exact: 'cam-b' });
    scanner.dispose();
  });

  it('opens a camera chosen while the first stream was still being acquired', async () => {
    const first = fakeStream();
    const second = fakeStream();
    let resolveFirst: (value: unknown) => void = () => {};
    const opened: (string | undefined)[] = [];
    const getUserMedia = jest.fn((constraints: MediaStreamConstraints) => {
      const video = constraints.video as MediaTrackConstraints;
      opened.push((video.deviceId as { exact?: string } | undefined)?.exact);
      if (opened.length === 1) return new Promise((resolve) => { resolveFirst = resolve; });
      return Promise.resolve(second.stream);
    });
    Object.defineProperty(globalThis.navigator, 'mediaDevices', {
      configurable: true,
      value: { getUserMedia },
    });
    const video = fakeVideo(async () => {});
    const onReady = jest.fn();
    const scanner = createWebQrScanner({ video, mode: 'auto', onReady });
    const starting = scanner.start();
    while (getUserMedia.mock.calls.length === 0) {
      await new Promise((resolve) => setTimeout(resolve, 0));
    }
    await scanner.switchCamera('cam-b');
    resolveFirst(first.stream);
    await starting;
    expect(opened).toEqual([undefined, 'cam-b']);
    expect(first.track.stop).toHaveBeenCalled();
    expect(video.srcObject).toBe(second.stream);
    expect(onReady).toHaveBeenCalledTimes(1);
    scanner.dispose();
  });

  it('falls back to the default camera when the requested one is gone', async () => {
    const { stream } = fakeStream();
    const opened: (string | undefined)[] = [];
    const getUserMedia = jest.fn(async (constraints: MediaStreamConstraints) => {
      const video = constraints.video as MediaTrackConstraints;
      const id = (video.deviceId as { exact?: string } | undefined)?.exact;
      opened.push(id);
      if (id) throw Object.assign(new Error('gone'), { name: 'OverconstrainedError' });
      return stream;
    });
    Object.defineProperty(globalThis.navigator, 'mediaDevices', {
      configurable: true,
      value: { getUserMedia },
    });
    const onError = jest.fn();
    const onReady = jest.fn();
    const scanner = createWebQrScanner({
      video: fakeVideo(async () => {}),
      source: { kind: 'camera', deviceId: 'unplugged' },
      mode: 'auto',
      onError,
      onReady,
    });
    await scanner.start();
    expect(opened).toEqual(['unplugged', undefined]);
    expect(onError).not.toHaveBeenCalled();
    expect(onReady).toHaveBeenCalledTimes(1);
    // The app then asks for the default camera it already shows.
    await scanner.switchCamera(undefined);
    expect(opened).toEqual(['unplugged', undefined]);
    scanner.dispose();
  });

  it('does not reopen the fallback camera when the default is chosen meanwhile', async () => {
    const { stream } = fakeStream();
    const opened: (string | undefined)[] = [];
    let scanner: ReturnType<typeof createWebQrScanner> | undefined;
    const getUserMedia = jest.fn(async (constraints: MediaStreamConstraints) => {
      const video = constraints.video as MediaTrackConstraints;
      const id = (video.deviceId as { exact?: string } | undefined)?.exact;
      opened.push(id);
      if (id) throw Object.assign(new Error('gone'), { name: 'OverconstrainedError' });
      // The camera list arrives while the fallback is opening.
      await scanner?.switchCamera(undefined);
      return stream;
    });
    Object.defineProperty(globalThis.navigator, 'mediaDevices', {
      configurable: true,
      value: { getUserMedia },
    });
    scanner = createWebQrScanner({
      video: fakeVideo(async () => {}),
      source: { kind: 'camera', deviceId: 'unplugged' },
      mode: 'auto',
    });
    await scanner.start();
    expect(opened).toEqual(['unplugged', undefined]);
    scanner.dispose();
  });
});
