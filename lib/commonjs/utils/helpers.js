"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.getAuthCaipNetworks = getAuthCaipNetworks;
exports.getWalletConnectCaipNetworks = getWalletConnectCaipNetworks;
var _appkitCommonReactNative = require("@reown/appkit-common-react-native");
async function getWalletConnectCaipNetworks(provider) {
  if (!provider) {
    throw new Error('networkControllerClient:getApprovedCaipNetworks - provider is undefined');
  }
  const ns = provider.signer?.session?.namespaces;
  const nsMethods = ns?.[_appkitCommonReactNative.ConstantsUtil.EIP155]?.methods;
  const nsChains = ns?.[_appkitCommonReactNative.ConstantsUtil.EIP155]?.chains;
  return {
    supportsAllNetworks: Boolean(nsMethods?.includes(_appkitCommonReactNative.ConstantsUtil.ADD_CHAIN_METHOD)),
    approvedCaipNetworkIds: nsChains
  };
}
function getAuthCaipNetworks() {
  return {
    supportsAllNetworks: false,
    approvedCaipNetworkIds: _appkitCommonReactNative.PresetsUtil.RpcChainIds.map(id => `${_appkitCommonReactNative.ConstantsUtil.EIP155}:${id}`)
  };
}
//# sourceMappingURL=helpers.js.map