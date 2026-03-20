export { AccountButton, AppKitButton, ConnectButton, NetworkButton, AppKit } from '@reown/appkit-scaffold-react-native';
import type { EventName, EventsControllerState } from '@reown/appkit-scaffold-react-native';
import { type Provider } from '@reown/appkit-scaffold-utils-react-native';
export { defaultConfig } from './utils/defaultConfig';
import { AppKit, type AppKitOptions } from './client';
export type { AppKitOptions } from './client';
type OpenOptions = Parameters<AppKit['open']>[0];
export declare function createAppKit(options: AppKitOptions): AppKit;
export declare function useAppKit(): {
    open: (options?: OpenOptions) => Promise<void>;
    close: () => Promise<void>;
};
export declare function useAppKitState(): {
    selectedNetworkId: number | undefined;
    open: boolean;
};
export declare function useAppKitProvider(): {
    walletProvider: Provider | undefined;
    walletProviderType: "WALLET_CONNECT" | "COINBASE" | "AUTH" | "EXTERNAL" | undefined;
};
export declare function useDisconnect(): {
    disconnect: () => Promise<void>;
};
export declare function useAppKitAccount(): {
    address: `0x${string}` | undefined;
    isConnected: boolean;
    chainId: number | undefined;
};
export declare function useWalletInfo(): {
    walletInfo: import("@reown/appkit-scaffold-react-native").ConnectedWalletInfo;
};
export declare function useAppKitError(): {
    error: unknown;
};
export declare function useAppKitEvents(callback?: (newEvent: EventsControllerState) => void): {
    timestamp: number;
    data: import("@reown/appkit-scaffold-react-native").Event;
};
export declare function useAppKitEventSubscription(event: EventName, callback: (newEvent: EventsControllerState) => void): void;
//# sourceMappingURL=index.d.ts.map