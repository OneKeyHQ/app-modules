import { useSnapshot } from 'valtio';
import { AccountController, ApiController, AssetController, AssetUtil, EventsController, ModalController, NetworkController, ThemeController } from '@reown/appkit-core-react-native';
import { NetworkButton as NetworkButtonUI, ThemeProvider } from '@reown/appkit-ui-react-native';
export function NetworkButton({
  disabled,
  style
}) {
  const {
    isConnected
  } = useSnapshot(AccountController.state);
  const {
    networkImages
  } = useSnapshot(AssetController.state);
  const {
    caipNetwork
  } = useSnapshot(NetworkController.state);
  const {
    loading
  } = useSnapshot(ModalController.state);
  const {
    themeMode,
    themeVariables
  } = useSnapshot(ThemeController.state);
  const onNetworkPress = () => {
    ModalController.open({
      view: 'Networks'
    });
    EventsController.sendEvent({
      type: 'track',
      event: 'CLICK_NETWORKS'
    });
  };
  return /*#__PURE__*/React.createElement(ThemeProvider, {
    themeMode: themeMode,
    themeVariables: themeVariables
  }, /*#__PURE__*/React.createElement(NetworkButtonUI, {
    imageSrc: AssetUtil.getNetworkImage(caipNetwork, networkImages),
    imageHeaders: ApiController._getApiHeaders(),
    disabled: disabled || loading,
    style: style,
    onPress: onNetworkPress,
    loading: loading,
    testID: "network-button"
  }, caipNetwork?.name ?? (isConnected ? 'Unknown Network' : 'Select Network')));
}
//# sourceMappingURL=index.js.map