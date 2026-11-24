import { createCapture } from "../../dist/qr-scanner/staging/package";

createCapture({
  onReady(generation: number, deviceId?: string) {
    generation.toFixed();
    deviceId?.toLowerCase();
  },
});

createCapture({
  onReady(generation, deviceId, info) {
    generation.toFixed();
    deviceId?.toLowerCase();
    info?.width.toFixed();
  },
});
