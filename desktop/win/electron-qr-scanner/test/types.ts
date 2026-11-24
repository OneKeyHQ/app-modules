import {
  CameraDevice,
  CaptureOptions,
  DecodeImageOptions,
  QrCapture,
  QrPreviewFrame,
  QrScannerError,
  createCapture,
  decodeImage,
  listCameras,
} from "../index.js";

const decodeOpts: DecodeImageOptions = { maxPixels: 12_000_000, engines: ["zxing"] };
const decoded: Promise<string | null> = decodeImage("C:\\tmp\\qr.png", decodeOpts);
const cameras: Promise<CameraDevice[]> = listCameras();

const opts: CaptureOptions = {
  source: { kind: "camera", deviceId: "\\\\?\\usb#vid_0000" },
  onReady: (gen: number) => {
    void gen;
  },
  onText: (text: string, gen: number) => {
    void text;
    void gen;
  },
  onError: (error: QrScannerError, gen: number) => {
    void error.code;
    void gen;
  },
  onEnded: (gen: number) => {
    void gen;
  },
  onFrame: (frame: QrPreviewFrame) => {
    const handle: Buffer = frame.ntHandle;
    void handle;
    frame.release();
  },
};

const capture: QrCapture = createCapture(opts);
capture.start();
capture.setDevice("\\\\?\\usb#vid_0001");
capture.setDevice(null);
capture.setDevice();
const lent: number = capture.lentFrames();
capture.stop();
capture.dispose();

void decoded;
void cameras;
void lent;
