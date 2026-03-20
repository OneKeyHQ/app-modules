import './config/animations';
import { type AccountControllerState, type ConnectionControllerClient, type ModalControllerState, type NetworkControllerClient, type NetworkControllerState, type OptionsControllerState, type EventsControllerState, type PublicStateControllerState, type ThemeControllerState, type ConnectedWalletInfo, type Features, type EventName, AccountController, BlockchainApiController, ConnectionController, ConnectorController, NetworkController } from '@reown/appkit-core-react-native';
import { type SIWEControllerClient } from '@reown/appkit-siwe-react-native';
import { type ThemeMode, type ThemeVariables } from '@reown/appkit-common-react-native';
export interface LibraryOptions {
    projectId: OptionsControllerState['projectId'];
    metadata: OptionsControllerState['metadata'];
    themeMode?: ThemeMode;
    themeVariables?: ThemeVariables;
    includeWalletIds?: OptionsControllerState['includeWalletIds'];
    excludeWalletIds?: OptionsControllerState['excludeWalletIds'];
    featuredWalletIds?: OptionsControllerState['featuredWalletIds'];
    customWallets?: OptionsControllerState['customWallets'];
    defaultChain?: NetworkControllerState['caipNetwork'];
    tokens?: OptionsControllerState['tokens'];
    clipboardClient?: OptionsControllerState['_clipboardClient'];
    enableAnalytics?: OptionsControllerState['enableAnalytics'];
    _sdkVersion: OptionsControllerState['sdkVersion'];
    debug?: OptionsControllerState['debug'];
    features?: Features;
}
export interface ScaffoldOptions extends LibraryOptions {
    networkControllerClient: NetworkControllerClient;
    connectionControllerClient: ConnectionControllerClient;
    siweControllerClient?: SIWEControllerClient;
}
export interface OpenOptions {
    view: 'Account' | 'Connect' | 'Networks' | 'Swap' | 'OnRamp';
}
export declare class AppKitScaffold {
    reportedAlertErrors: Record<string, boolean>;
    constructor(options: ScaffoldOptions);
    open(options?: OpenOptions): Promise<void>;
    close(): Promise<void>;
    getThemeMode(): ThemeMode | undefined;
    getThemeVariables(): ThemeVariables;
    setThemeMode(themeMode: ThemeControllerState['themeMode']): void;
    setThemeVariables(themeVariables: ThemeControllerState['themeVariables']): void;
    subscribeTheme(callback: (newState: ThemeControllerState) => void): () => void;
    getWalletInfo(): ConnectedWalletInfo;
    subscribeWalletInfo(callback: (newState: ConnectedWalletInfo) => void): () => void;
    getState(): {
        open: boolean;
        selectedNetworkId?: `${string}:${string}` | undefined;
    };
    subscribeState(callback: (newState: PublicStateControllerState) => void): () => void;
    subscribeStateKey<K extends keyof PublicStateControllerState>(key: K, callback: (value: PublicStateControllerState[K]) => void): () => void;
    subscribeConnection(callback: (isConnected: AccountControllerState['isConnected']) => void): () => void;
    setLoading(loading: ModalControllerState['loading']): void;
    getEvent(): {
        timestamp: number;
        data: import("@reown/appkit-core-react-native").Event;
    };
    subscribeEvents(callback: (newEvent: EventsControllerState) => void): () => void;
    subscribeEvent(event: EventName, callback: (newEvent: EventsControllerState) => void): () => void;
    resolveReownName: (name: string) => Promise<string | false>;
    protected setIsConnected: (typeof AccountController)['setIsConnected'];
    protected setCaipAddress: (typeof AccountController)['setCaipAddress'];
    protected getCaipAddress: () => `${string}:${string}:${string}` | undefined;
    protected setBalance: (typeof AccountController)['setBalance'];
    protected setProfileName: (typeof AccountController)['setProfileName'];
    protected setProfileImage: (typeof AccountController)['setProfileImage'];
    protected resetAccount: (typeof AccountController)['resetAccount'];
    protected setCaipNetwork: (typeof NetworkController)['setCaipNetwork'];
    protected getCaipNetwork: () => import("@reown/appkit-core-react-native").CaipNetwork | undefined;
    protected setRequestedCaipNetworks: (typeof NetworkController)['setRequestedCaipNetworks'];
    protected getApprovedCaipNetworksData: (typeof NetworkController)['getApprovedCaipNetworksData'];
    protected resetNetwork: (typeof NetworkController)['resetNetwork'];
    protected setConnectors: (typeof ConnectorController)['setConnectors'];
    protected addConnector: (typeof ConnectorController)['addConnector'];
    protected getConnectors: (typeof ConnectorController)['getConnectors'];
    protected resetWcConnection: (typeof ConnectionController)['resetWcConnection'];
    protected fetchIdentity: (typeof BlockchainApiController)['fetchIdentity'];
    protected setAddressExplorerUrl: (typeof AccountController)['setAddressExplorerUrl'];
    protected setConnectedWalletInfo: (typeof AccountController)['setConnectedWalletInfo'];
    protected setClientId: (typeof BlockchainApiController)['setClientId'];
    protected setPreferredAccountType: (typeof AccountController)['setPreferredAccountType'];
    protected handleAlertError(error?: string | {
        shortMessage: string;
        longMessage: string;
    }): void;
    private initControllers;
    private setConnectorExcludedWallets;
    private initRecentWallets;
    private initConnectedConnector;
    private initSocial;
    private initAsyncValues;
}
//# sourceMappingURL=client.d.ts.map