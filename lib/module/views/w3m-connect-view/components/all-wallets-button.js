import { useSnapshot } from 'valtio';
import { ApiController } from '@reown/appkit-core-react-native';
import { ListWallet } from '@reown/appkit-ui-react-native';
export function AllWalletsButton({
  itemStyle,
  onPress,
  isWalletConnectEnabled
}) {
  const {
    installed,
    count
  } = useSnapshot(ApiController.state);
  if (!isWalletConnectEnabled) {
    return null;
  }
  const total = installed.length + count;
  const label = total > 10 ? `${Math.floor(total / 10) * 10}+` : total;
  return /*#__PURE__*/React.createElement(ListWallet, {
    name: "All wallets",
    showAllWallets: true,
    tagLabel: String(label),
    tagVariant: "shade",
    onPress: onPress,
    style: itemStyle,
    testID: "all-wallets"
  });
}
//# sourceMappingURL=all-wallets-button.js.map