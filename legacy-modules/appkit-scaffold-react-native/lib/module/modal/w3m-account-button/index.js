import { useSnapshot } from 'valtio';
import { AccountController, CoreHelperUtil, NetworkController, ModalController, AssetUtil, ThemeController, ApiController, AssetController } from '@reown/appkit-core-react-native';
import { AccountButton as AccountButtonUI, ThemeProvider } from '@reown/appkit-ui-react-native';
export function AccountButton({
  balance,
  disabled,
  style,
  testID
}) {
  const {
    address,
    balance: balanceVal,
    balanceSymbol,
    profileImage,
    profileName
  } = useSnapshot(AccountController.state);
  const {
    caipNetwork
  } = useSnapshot(NetworkController.state);
  const {
    networkImages
  } = useSnapshot(AssetController.state);
  const {
    themeMode,
    themeVariables
  } = useSnapshot(ThemeController.state);
  const networkImage = AssetUtil.getNetworkImage(caipNetwork, networkImages);
  const showBalance = balance === 'show';
  return /*#__PURE__*/React.createElement(ThemeProvider, {
    themeMode: themeMode,
    themeVariables: themeVariables
  }, /*#__PURE__*/React.createElement(AccountButtonUI, {
    onPress: () => ModalController.open(),
    address: address,
    profileName: profileName,
    networkSrc: networkImage,
    imageHeaders: ApiController._getApiHeaders(),
    avatarSrc: profileImage,
    disabled: disabled,
    style: style,
    balance: showBalance ? CoreHelperUtil.formatBalance(balanceVal, balanceSymbol) : '',
    testID: testID
  }));
}
//# sourceMappingURL=index.js.map