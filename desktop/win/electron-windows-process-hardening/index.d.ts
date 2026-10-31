export type StageResult =
  | { status: "applied" }
  | { status: "unsupported" }
  | { status: "failed"; message: string; win32Error?: number };

export interface HardeningResult {
  dacl: StageResult;
  extensionPoint: StageResult;
}

/** Synchronously harden only this Windows process. Call from Node or Electron main. */
export declare function hardenCurrentProcess(): HardeningResult;

export type CrashHandlerStartResult =
  | { status: "ready"; handlerPid: number }
  | { status: "failed"; message: string }
  | { status: "disabled" | "unsupported" };

/** Test-only local handler; never loads native code unless the Electron main-process gate is on. */
export declare function startLocalCrashHandler(): CrashHandlerStartResult;

/** Test-only native access violation with a fixed three-level call chain. */
export declare function triggerLocalCrashForTest(): never;

