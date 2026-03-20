import type { Address, Provider } from './EthersTypesUtil';
export interface EthersStoreUtilState {
    provider?: Provider;
    providerType?: 'WALLET_CONNECT' | 'COINBASE' | 'AUTH' | 'EXTERNAL';
    address?: Address;
    chainId?: number;
    error?: unknown;
    isConnected: boolean;
}
export declare const EthersStoreUtil: {
    state: EthersStoreUtilState;
    subscribeKey<K extends keyof EthersStoreUtilState>(key: K, callback: (value: EthersStoreUtilState[K]) => void): () => void;
    subscribe(callback: (newState: EthersStoreUtilState) => void): () => void;
    setProvider(provider: EthersStoreUtilState['provider']): void;
    setProviderType(providerType: EthersStoreUtilState['providerType']): void;
    setAddress(address: EthersStoreUtilState['address']): void;
    setChainId(chainId: EthersStoreUtilState['chainId']): void;
    setIsConnected(isConnected: EthersStoreUtilState['isConnected']): void;
    setError(error: EthersStoreUtilState['error']): void;
    reset(): void;
};
//# sourceMappingURL=EthersStoreUtil.d.ts.map