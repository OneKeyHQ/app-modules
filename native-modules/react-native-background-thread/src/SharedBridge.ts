/**
 * SharedBridge — a JSI HostObject installed in both the main and background
 * runtimes, backed by the same C++ queues. Available as `globalThis.sharedBridge`
 * once installed on the native side.
 *
 * Main runtime:  send() pushes to background queue, drain() pulls from main queue.
 * Background:    send() pushes to main queue, drain() pulls from background queue.
 */
export interface ISharedBridge {
  /** Push a string message into the OTHER runtime's queue. */
  send(message: string): void;

  /** Pull all pending messages from THIS runtime's queue. */
  drain(): string[];

  /** True if there are pending messages for THIS runtime. Atomic read, ~0 cost. */
  readonly hasMessages: boolean;

  /** True if this instance is installed in the main (UI) runtime. */
  readonly isMain: boolean;
}

declare global {
  // eslint-disable-next-line no-var
  var sharedBridge: ISharedBridge | undefined;
}

/**
 * Get the SharedBridge instance from globalThis.
 * Returns undefined if native side hasn't installed it yet.
 */
export function getSharedBridge(): ISharedBridge | undefined {
  return globalThis.sharedBridge;
}
