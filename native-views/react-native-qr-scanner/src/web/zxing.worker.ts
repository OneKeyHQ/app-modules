// Worker entry: `new Worker(new URL('@onekeyfe/react-native-qr-scanner/web/zxing-worker',
// import.meta.url), { type: 'module' })` (bundler-specific), then pass a
// factory to configureWebQrScanner({ createZXingWorker }).
import { createZXingWorkerHandler } from './zxingWorkerProtocol';

import type { ZXingWorkerRequest } from './zxingWorkerProtocol';

declare const self: {
  onmessage: ((event: { data: ZXingWorkerRequest }) => void) | null;
  postMessage(message: unknown): void;
};

const handle = createZXingWorkerHandler();

self.onmessage = (event) => {
  void handle(event.data).then((response) => self.postMessage(response));
};
