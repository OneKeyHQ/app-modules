
import ReactNative from 'react-native';

let isReady = false;
let waitMessages = [];
const callbacks  = new Set();
const onMessageCallback = (message) => {
  let parsed = message;
  if (typeof message === 'string') {
    try {
      parsed = JSON.parse(message);
    } catch {
      // keep as raw string
    }
  }
  callbacks.forEach((callback) => callback(parsed));
};

function checkReady(times = 0) {
  if (globalThis.$$isNativeUiThread || times > 10_000) {
    return;
  }
  if (
    typeof globalThis.postHostMessage === 'function' &&
    typeof globalThis.onHostMessage === 'function'
  ) {
    isReady = true;
    globalThis.onHostMessage(onMessageCallback);
    setTimeout(() => {
      console.log('waitMessages.length', waitMessages.length);
      waitMessages.forEach((message) => {
        globalThis.postHostMessage(message);
      });
      waitMessages = [];
    }, 0);
  } else {
    console.log('checkReady', times);
    setTimeout(() => checkReady(times + 1), 10);
  }
}

checkReady();

const checkThread = () => {
  if (globalThis.$$isNativeUiThread) {
    // eslint-disable-next-line no-restricted-syntax
    throw new Error(
      'this function is not available in native UI thread',
    );
  }
};

export const nativeBGBridge = {
  postHostMessage: (message) => {
    checkThread();
    const str = typeof message === 'string' ? message : JSON.stringify(message);
    if (!isReady) {
      waitMessages.push(str);
      return;
    }
    globalThis.postHostMessage(str);
  },
  onHostMessage: (callback) => {
    checkThread();
    callbacks.add(callback);
    return () => {
      callbacks.delete(callback);
    };
  },
};


nativeBGBridge.onHostMessage((message) => {
  console.log('message', message);
  if (message.type === 'test1') {
    console.log(`[BG] Received test1: ${JSON.stringify(message)}, will reply in 3s`);
    setTimeout(() => {
      console.log('[BG] Sending test2 response');
      nativeBGBridge.postHostMessage({ type: 'test2' });
    }, 3000);
  }
});

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

// SharedRPC: handle RPC calls from main runtime via onHostMessage
nativeBGBridge.onHostMessage((message) => {
  if (message.type === 'rpc' && message.callId) {
    const params = globalThis.sharedRPC?.read(message.callId);
    if (params !== undefined) {
      console.log('[SharedRPC BG] received call:', message.callId, params);
      // Echo back the params as the result
      globalThis.sharedRPC?.write(message.callId, 'echo: ' + params);
      nativeBGBridge.postHostMessage({ type: 'rpc_response', callId: message.callId });
    }
  }
});
