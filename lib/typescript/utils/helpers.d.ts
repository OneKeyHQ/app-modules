import EthereumProvider from '@walletconnect/ethereum-provider';
export declare function getWalletConnectCaipNetworks(provider?: EthereumProvider): Promise<{
    supportsAllNetworks: boolean;
    approvedCaipNetworkIds: `${string}:${string}`[];
}>;
export declare function getAuthCaipNetworks(): {
    supportsAllNetworks: boolean;
    approvedCaipNetworkIds: `${string}:${string}`[];
};
//# sourceMappingURL=helpers.d.ts.map