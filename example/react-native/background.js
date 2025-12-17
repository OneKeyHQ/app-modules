
import ReactNative from 'react-native';

let waitMessages = [];
const callbacks  = new Set();
const onMessageCallback = (message) => {
  callbacks.forEach((callback) => callback(message));
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
      'this function is not available in native background thread',
    );
  }
};

export const nativeBGBridge = {
  postHostMessage: (message) => {
    checkThread();
    if (!isReady) {
      waitMessages.push(message);
      return;
    }
    globalThis.postHostMessage(JSON.stringify(message));
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
    alert(`${JSON.stringify(message)} in background, wait 3 seconds`);
    setTimeout(() => {
      alert('post message from background thread');
      nativeBGBridge.postHostMessage({ type: 'test2' });
    }, 3000);
  }
});
