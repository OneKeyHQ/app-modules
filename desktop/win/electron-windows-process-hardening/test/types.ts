import { hardenCurrentProcess, type HardeningResult, type StageResult } from "../index";

const result: HardeningResult = hardenCurrentProcess();
const dacl: StageResult = result.dacl;
const extensionPoint: StageResult = result.extensionPoint;
if (dacl.status === "failed") {
  const code: number | undefined = dacl.win32Error;
  const message: string = dacl.message;
  void code;
  void message;
}
void extensionPoint;
