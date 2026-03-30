import ReactNative from 'react-native';

// ── SharedRPC onWrite handler ──────────────────────────────────────────
// Background runtime listens for writes from main runtime,
// processes the RPC call, and writes the result back.

function waitForSharedRPC(times = 0) {
  if (globalThis.$$isNativeUiThread) return;
  if (globalThis.sharedRPC) {
    setupRPCHandler();
    return;
  }
  if (times > 5000) {
    console.error('[BG] sharedRPC not available after timeout');
    return;
  }
  setTimeout(() => waitForSharedRPC(times + 1), 10);
}

function setupRPCHandler() {
  console.log('[BG] sharedRPC ready, registering onWrite listener');

  globalThis.sharedRPC.onWrite((callId) => {
    // Skip result writes (those are our own responses)
    if (callId.endsWith(':result')) return;

    const raw = globalThis.sharedRPC.read(callId);
    if (raw === undefined) return;

    console.log('[BG] RPC call received:', callId, raw);

    let params;
    try {
      params = typeof raw === 'string' ? JSON.parse(raw) : raw;
    } catch {
      params = raw;
    }

    // Dispatch to handler by method name
    const result = handleRPC(params);

    // Write result back — triggers main runtime's onWrite
    globalThis.sharedRPC.write(
      callId + ':result',
      typeof result === 'string' ? result : JSON.stringify(result),
    );
    console.log('[BG] RPC result written:', callId + ':result');
  });
}

function handleRPC(params) {
  const method = params?.method;
  switch (method) {
    case 'echo':
      return { method: 'echo', result: params.params, ts: Date.now() };
    case 'add':
      return { method: 'add', result: (params.params?.a ?? 0) + (params.params?.b ?? 0) };
    case 'delay':
      // Simulate async — but onWrite is synchronous, so just return immediately
      return { method: 'delay', result: 'done', requestedMs: params.params?.ms };
    default:
      return { error: 'unknown method', method };
  }
}

waitForSharedRPC();

// ── SharedStore demo ───────────────────────────────────────────────────
// Periodically read config values set by the main runtime.

function pollSharedStore() {
  if (globalThis.sharedStore) {
    const locale = globalThis.sharedStore.get('locale');
    if (locale) {
      console.log('[BG SharedStore] locale =', locale);
    }
  }
  setTimeout(pollSharedStore, 3000);
}
pollSharedStore();
