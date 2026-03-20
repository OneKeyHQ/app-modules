import { useSnapshot } from 'valtio';
export { AccountButton, AppKitButton, ConnectButton, NetworkButton, AppKit } from '@reown/appkit-scaffold-react-native';
import { EthersStoreUtil } from '@reown/appkit-scaffold-utils-react-native';
import { ConstantsUtil } from '@reown/appkit-common-react-native';
export { defaultConfig } from './utils/defaultConfig';
import { useEffect, useState, useSyncExternalStore } from 'react';
import { AppKit } from './client';

// -- Types -------------------------------------------------------------------

// -- Setup -------------------------------------------------------------------
let modal;
export function createAppKit(options) {
  if (!modal) {
    modal = new AppKit({
      ...options,
      _sdkVersion: `react-native-ethers5-${ConstantsUtil.VERSION}`
    });
  }
  return modal;
}

// -- Hooks -------------------------------------------------------------------
export function useAppKit() {
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
export function useAppKitState() {
  if (!modal) {
    throw new Error('Please call "createAppKit" before using "useAppKitState" hook');
  }
  const [state, setState] = useState(modal.getState());
  useEffect(() => {
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
export function useAppKitProvider() {
  const {
    provider,
    providerType
  } = useSnapshot(EthersStoreUtil.state);
  const walletProvider = provider;
  const walletProviderType = providerType;
  return {
    walletProvider,
    walletProviderType
  };
}
export function useDisconnect() {
  async function disconnect() {
    await modal?.disconnect();
  }
  return {
    disconnect
  };
}
export function useAppKitAccount() {
  const {
    address,
    isConnected,
    chainId
  } = useSnapshot(EthersStoreUtil.state);
  return {
    address,
    isConnected,
    chainId
  };
}
export function useWalletInfo() {
  if (!modal) {
    throw new Error('Please call "createAppKit" before using "useWalletInfo" hook');
  }
  const walletInfo = useSyncExternalStore(modal.subscribeWalletInfo, modal.getWalletInfo, modal.getWalletInfo);
  return {
    walletInfo
  };
}
export function useAppKitError() {
  const {
    error
  } = useSnapshot(EthersStoreUtil.state);
  return {
    error
  };
}
export function useAppKitEvents(callback) {
  if (!modal) {
    throw new Error('Please call "createAppKit" before using "useAppKitEvents" hook');
  }
  const [event, setEvents] = useState(modal.getEvent());
  useEffect(() => {
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
export function useAppKitEventSubscription(event, callback) {
  if (!modal) {
    throw new Error('Please call "createAppKit" before using "useAppKitEventSubscription" hook');
  }
  useEffect(() => {
    const unsubscribe = modal?.subscribeEvent(event, callback);
    return () => {
      unsubscribe?.();
    };
  }, [callback, event]);
}
//# sourceMappingURL=index.js.map