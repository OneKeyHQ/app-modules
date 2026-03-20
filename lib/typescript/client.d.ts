import { type LibraryOptions, type PublicStateControllerState, type Token, AppKitScaffold } from '@reown/appkit-scaffold-react-native';
import { type ProviderType, type Chain, type Provider, type EthersStoreUtilState } from '@reown/appkit-scaffold-utils-react-native';
import { type AppKitSIWEClient } from '@reown/appkit-siwe-react-native';
export interface AppKitClientOptions extends Omit<LibraryOptions, 'defaultChain' | 'tokens'> {
    config: ProviderType;
    siweConfig?: AppKitSIWEClient;
    chains: Chain[];
    defaultChain?: Chain;
    chainImages?: Record<number, string>;
    connectorImages?: Record<string, string>;
    tokens?: Record<number, Token>;
}
export type AppKitOptions = Omit<AppKitClientOptions, '_sdkVersion'>;
interface AppKitState extends PublicStateControllerState {
    selectedNetworkId: number | undefined;
}
export declare class AppKit extends AppKitScaffold {
    private hasSyncedConnectedAccount;
    private walletConnectProvider?;
    private walletConnectProviderInitPromise?;
    private projectId;
    private chains;
    private metadata;
    private options;
    private authProvider?;
    constructor(options: AppKitClientOptions);
    getState(): {
        selectedNetworkId: number | undefined;
        open: boolean;
    };
    subscribeState(callback: (state: AppKitState) => void): () => void;
    setAddress(address?: string): void;
    getAddress(): string | undefined;
    getError(): unknown;
    getChainId(): number | undefined;
    getIsConnected(): boolean;
    getWalletProvider(): Provider | undefined;
    getWalletProviderType(): "WALLET_CONNECT" | "COINBASE" | "AUTH" | "EXTERNAL" | undefined;
    subscribeProvider(callback: (newState: EthersStoreUtilState) => void): () => void;
    disconnect(): Promise<void>;
    private createProvider;
    private initWalletConnectProvider;
    private getWalletConnectProvider;
    private syncRequestedNetworks;
    private checkActiveWalletConnectProvider;
    private checkActiveCoinbaseProvider;
    private setWalletConnectProvider;
    private setCoinbaseProvider;
    private setAuthProvider;
    private watchWalletConnect;
    private watchCoinbase;
    private syncAccount;
    private syncNetwork;
    private syncProfile;
    private syncBalance;
    private switchNetwork;
    private handleAuthSetPreferredAccount;
    private syncConnectors;
    private syncAuthConnector;
    private syncConnectedWalletInfo;
    private addAuthListeners;
    private addWalletConnectListeners;
}
export {};
//# sourceMappingURL=client.d.ts.map