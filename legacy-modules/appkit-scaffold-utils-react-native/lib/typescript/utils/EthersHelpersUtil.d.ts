import type { CaipNetwork } from '@reown/appkit-scaffold-react-native';
import type { Chain, Provider } from './EthersTypesUtil';
export declare const EthersHelpersUtil: {
    getCaipDefaultChain(chain?: Chain): CaipNetwork | undefined;
    hexStringToNumber(value: string): number;
    numberToHexString(value: number): string;
    getUserInfo(provider: Provider): Promise<{
        chainId: number;
        address: string | undefined;
    }>;
    getChainId(provider: Provider): Promise<number>;
    getAddress(provider: Provider): Promise<string | undefined>;
    addEthereumChain(provider: Provider, chain: Chain): Promise<void>;
};
//# sourceMappingURL=EthersHelpersUtil.d.ts.map