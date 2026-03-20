"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.EthersHelpersUtil = void 0;
var _appkitCommonReactNative = require("@reown/appkit-common-react-native");
const EthersHelpersUtil = exports.EthersHelpersUtil = {
  getCaipDefaultChain(chain) {
    if (!chain) {
      return undefined;
    }
    return {
      id: `${_appkitCommonReactNative.ConstantsUtil.EIP155}:${chain.chainId}`,
      name: chain.name,
      imageId: _appkitCommonReactNative.PresetsUtil.EIP155NetworkImageIds[chain.chainId]
    };
  },
  hexStringToNumber(value) {
    const string = value.startsWith('0x') ? value.slice(2) : value;
    const number = parseInt(string, 16);
    return number;
  },
  numberToHexString(value) {
    return `0x${value.toString(16)}`;
  },
  async getUserInfo(provider) {
    const [address, chainId] = await Promise.all([EthersHelpersUtil.getAddress(provider), EthersHelpersUtil.getChainId(provider)]);
    return {
      chainId,
      address
    };
  },
  async getChainId(provider) {
    const chainId = await provider.request({
      method: 'eth_chainId'
    });
    return Number(chainId);
  },
  async getAddress(provider) {
    const [address] = await provider.request({
      method: 'eth_accounts'
    });
    return address;
  },
  async addEthereumChain(provider, chain) {
    await provider.request({
      method: 'wallet_addEthereumChain',
      params: [{
        chainId: EthersHelpersUtil.numberToHexString(chain.chainId),
        rpcUrls: [chain.rpcUrl],
        chainName: chain.name,
        nativeCurrency: {
          name: chain.currency,
          decimals: 18,
          symbol: chain.currency
        },
        blockExplorerUrls: [chain.explorerUrl],
        iconUrls: [_appkitCommonReactNative.PresetsUtil.EIP155NetworkImageIds[chain.chainId]]
      }]
    });
  }
};
//# sourceMappingURL=EthersHelpersUtil.js.map