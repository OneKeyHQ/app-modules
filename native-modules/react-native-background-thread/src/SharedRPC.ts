export interface ISharedRPC {
  write(callId: string, value: string | number | boolean): void;
  // Value-inline messaging: the notify callback delivers BOTH the callId and
  // the payload value, so there is no read-back. `read`/`has`/`pendingCount`
  // (the old slot-map introspection) are gone.
  onWrite(
    callback: (callId: string, value: string | number | boolean) => void
  ): void;
  // The calling runtime declares which SharedStore key holds its readiness
  // payload, so the native invalidate path clears exactly that key on teardown
  // (restart freshness — prevents a respawned reader seeing prior-life state).
  registerReadinessKey(key: string): void;
}

declare global {
  var sharedRPC: ISharedRPC | undefined;
}

export function getSharedRPC(): ISharedRPC | undefined {
  return globalThis.sharedRPC;
}
