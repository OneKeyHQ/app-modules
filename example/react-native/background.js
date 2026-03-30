
import ReactNative from 'react-native';

// Wait for sharedRPC to be available, then set up onWrite listener
function waitForSharedRPC(times = 0) {
  if (globalThis.$$isNativeUiThread) {
    return;
  }
  if (globalThis.sharedRPC) {
    setupHandlers();
    return;
  }
  if (times > 5000) {
    console.error('[BG] sharedRPC not available after timeout');
    return;
  }
  setTimeout(() => waitForSharedRPC(times + 1), 10);
}

function setupHandlers() {
  console.log('[BG] sharedRPC ready, setting up onWrite listener');

  globalThis.sharedRPC.onWrite((callId) => {
    console.log('[BG] onWrite notification:', callId);

    const params = globalThis.sharedRPC.read(callId);
    if (params === undefined) return;

    console.log('[BG] received RPC call:', callId, params);

    // Process and write result back — this will notify main runtime
    globalThis.sharedRPC.write(callId + ':result', 'echo: ' + params);
  });
}

waitForSharedRPC();

// SharedStore: read config values set by main runtime
function checkSharedStore() {
  if (globalThis.sharedStore) {
    const locale = globalThis.sharedStore.get('locale');
    if (locale) {
      console.log('[SharedStore BG] locale =', locale);
    }
  }
  setTimeout(checkSharedStore, 1000);
}
checkSharedStore();
