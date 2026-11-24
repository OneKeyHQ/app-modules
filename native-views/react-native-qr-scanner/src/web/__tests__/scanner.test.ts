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
});
