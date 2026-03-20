import { PresetsUtil, ConstantsUtil } from '@reown/appkit-common-react-native';
export async function getWalletConnectCaipNetworks(provider) {
  if (!provider) {
    throw new Error('networkControllerClient:getApprovedCaipNetworks - provider is undefined');
  }
  const ns = provider.signer?.session?.namespaces;
  const nsMethods = ns?.[ConstantsUtil.EIP155]?.methods;
  const nsChains = ns?.[ConstantsUtil.EIP155]?.chains;
  return {
    supportsAllNetworks: Boolean(nsMethods?.includes(ConstantsUtil.ADD_CHAIN_METHOD)),
    approvedCaipNetworkIds: nsChains
  };
}
export function getAuthCaipNetworks() {
  return {
    supportsAllNetworks: false,
    approvedCaipNetworkIds: PresetsUtil.RpcChainIds.map(id => `${ConstantsUtil.EIP155}:${id}`)
  };
}
//# sourceMappingURL=helpers.js.map