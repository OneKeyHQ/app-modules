import { useSnapshot } from 'valtio';
import { ApiController, AssetUtil, ConnectionController } from '@reown/appkit-core-react-native';
import { ListWallet } from '@reown/appkit-ui-react-native';
export function RecentWalletList({
  itemStyle,
  onWalletPress,
  isWalletConnectEnabled
}) {
  const installed = ApiController.state.installed;
  const {
    recentWallets
  } = useSnapshot(ConnectionController.state);
  const imageHeaders = ApiController._getApiHeaders();
  const RECENT_COUNT = recentWallets?.length && installed.length ? 1 : recentWallets?.length ?? 0;
  if (!isWalletConnectEnabled || !recentWallets?.length) {
    return null;
  }
  return recentWallets.slice(0, RECENT_COUNT).map(wallet => {
    const isInstalled = !!installed.find(installedWallet => installedWallet.id === wallet.id);
    return /*#__PURE__*/React.createElement(ListWallet, {
      key: wallet?.id,
      imageSrc: AssetUtil.getWalletImage(wallet),
      imageHeaders: imageHeaders,
      name: wallet?.name ?? 'Unknown',
      onPress: () => onWalletPress(wallet, isInstalled),
      tagLabel: "Recent",
      tagVariant: "shade",
      style: itemStyle,
      installed: isInstalled
    });
  });
}
//# sourceMappingURL=recent-wallet-list.js.map