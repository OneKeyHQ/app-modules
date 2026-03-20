import { useSnapshot } from 'valtio';
import { OptionsController, ApiController, ConnectionController } from '@reown/appkit-core-react-native';
import { ListWallet } from '@reown/appkit-ui-react-native';
import { filterOutRecentWallets } from '../utils';
export function CustomWalletList({
  itemStyle,
  onWalletPress,
  isWalletConnectEnabled
}) {
  const {
    installed
  } = useSnapshot(ApiController.state);
  const {
    recentWallets
  } = useSnapshot(ConnectionController.state);
  const {
    customWallets
  } = useSnapshot(OptionsController.state);
  const RECENT_COUNT = recentWallets?.length && installed.length ? 1 : recentWallets?.length ?? 0;
  if (!isWalletConnectEnabled || !customWallets?.length) {
    return null;
  }
  const list = filterOutRecentWallets(recentWallets, customWallets, RECENT_COUNT);
  return list.map(wallet => /*#__PURE__*/React.createElement(ListWallet, {
    key: wallet.id,
    imageSrc: wallet.image_url,
    name: wallet.name,
    onPress: () => onWalletPress(wallet),
    style: itemStyle
  }));
}
//# sourceMappingURL=custom-wallet-list.js.map