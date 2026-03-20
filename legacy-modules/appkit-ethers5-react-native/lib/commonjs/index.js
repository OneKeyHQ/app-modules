"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
Object.defineProperty(exports, "AccountButton", {
  enumerable: true,
  get: function () {
    return _appkitScaffoldReactNative.AccountButton;
  }
});
Object.defineProperty(exports, "AppKit", {
  enumerable: true,
  get: function () {
    return _appkitScaffoldReactNative.AppKit;
  }
});
Object.defineProperty(exports, "AppKitButton", {
  enumerable: true,
  get: function () {
    return _appkitScaffoldReactNative.AppKitButton;
  }
});
Object.defineProperty(exports, "ConnectButton", {
  enumerable: true,
  get: function () {
    return _appkitScaffoldReactNative.ConnectButton;
  }
});
Object.defineProperty(exports, "NetworkButton", {
  enumerable: true,
  get: function () {
    return _appkitScaffoldReactNative.NetworkButton;
  }
});
exports.createAppKit = createAppKit;
Object.defineProperty(exports, "defaultConfig", {
  enumerable: true,
  get: function () {
    return _defaultConfig.defaultConfig;
  }
});
exports.useAppKit = useAppKit;
exports.useAppKitAccount = useAppKitAccount;
exports.useAppKitError = useAppKitError;
exports.useAppKitEventSubscription = useAppKitEventSubscription;
exports.useAppKitEvents = useAppKitEvents;
exports.useAppKitProvider = useAppKitProvider;
exports.useAppKitState = useAppKitState;
exports.useDisconnect = useDisconnect;
exports.useWalletInfo = useWalletInfo;
var _valtio = require("valtio");
var _appkitScaffoldReactNative = require("@reown/appkit-scaffold-react-native");
var _appkitScaffoldUtilsReactNative = require("@reown/appkit-scaffold-utils-react-native");
var _appkitCommonReactNative = require("@reown/appkit-common-react-native");
var _defaultConfig = require("./utils/defaultConfig");
var _react = require("react");
var _client = require("./client");
// -- Types -------------------------------------------------------------------

// -- Setup -------------------------------------------------------------------
let modal;
function createAppKit(options) {
  if (!modal) {
    modal = new _client.AppKit({
      ...options,
      _sdkVersion: `react-native-ethers5-${_appkitCommonReactNative.ConstantsUtil.VERSION}`
    });
  }
  return modal;
}

// -- Hooks -------------------------------------------------------------------
function useAppKit() {
  if (!modal) {
    throw new Error('Please call "createAppKit" before using "useAppKit" hook');
  }
  async function open(options) {
    await modal?.open(options);
  }
  async function close() {
    await modal?.close();
  }
  return {
    open,
    close
  };
}
function useAppKitState() {
  if (!modal) {
    throw new Error('Please call "createAppKit" before using "useAppKitState" hook');
  }
  const [state, setState] = (0, _react.useState)(modal.getState());
  (0, _react.useEffect)(() => {
    const unsubscribe = modal?.subscribeState(newState => {
      if (newState) setState({
        ...newState
      });
    });
    return () => {
      unsubscribe?.();
    };
  }, []);
  return state;
}
function useAppKitProvider() {
  const {
    provider,
    providerType
  } = (0, _valtio.useSnapshot)(_appkitScaffoldUtilsReactNative.EthersStoreUtil.state);
  const walletProvider = provider;
  const walletProviderType = providerType;
  return {
    walletProvider,
    walletProviderType
  };
}
function useDisconnect() {
  async function disconnect() {
    await modal?.disconnect();
  }
  return {
    disconnect
  };
}
function useAppKitAccount() {
  const {
    address,
    isConnected,
    chainId
  } = (0, _valtio.useSnapshot)(_appkitScaffoldUtilsReactNative.EthersStoreUtil.state);
  return {
    address,
    isConnected,
    chainId
  };
}
function useWalletInfo() {
  if (!modal) {
    throw new Error('Please call "createAppKit" before using "useWalletInfo" hook');
  }
  const walletInfo = (0, _react.useSyncExternalStore)(modal.subscribeWalletInfo, modal.getWalletInfo, modal.getWalletInfo);
  return {
    walletInfo
  };
}
function useAppKitError() {
  const {
    error
  } = (0, _valtio.useSnapshot)(_appkitScaffoldUtilsReactNative.EthersStoreUtil.state);
  return {
    error
  };
}
function useAppKitEvents(callback) {
  if (!modal) {
    throw new Error('Please call "createAppKit" before using "useAppKitEvents" hook');
  }
  const [event, setEvents] = (0, _react.useState)(modal.getEvent());
  (0, _react.useEffect)(() => {
    const unsubscribe = modal?.subscribeEvents(newEvent => {
      setEvents({
        ...newEvent
      });
      callback?.(newEvent);
    });
    return () => {
      unsubscribe?.();
    };
  }, [callback]);
  return event;
}
function useAppKitEventSubscription(event, callback) {
  if (!modal) {
    throw new Error('Please call "createAppKit" before using "useAppKitEventSubscription" hook');
  }
  (0, _react.useEffect)(() => {
    const unsubscribe = modal?.subscribeEvent(event, callback);
    return () => {
      unsubscribe?.();
    };
  }, [callback, event]);
}
//# sourceMappingURL=index.js.map