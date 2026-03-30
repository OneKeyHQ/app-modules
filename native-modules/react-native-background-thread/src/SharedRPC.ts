export interface ISharedRPC {
  write(callId: string, value: string | number | boolean): void;
  read(callId: string): string | number | boolean | undefined;
  has(callId: string): boolean;
  readonly pendingCount: number;
  onWrite(callback: (callId: string) => void): void;
}

declare global {
  // eslint-disable-next-line no-var
  var sharedRPC: ISharedRPC | undefined;
}

export function getSharedRPC(): ISharedRPC | undefined {
  return globalThis.sharedRPC;
}
