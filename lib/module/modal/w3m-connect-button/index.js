import { useSnapshot } from 'valtio';
import { ModalController, ThemeController } from '@reown/appkit-core-react-native';
import { ConnectButton as ConnectButtonUI, ThemeProvider } from '@reown/appkit-ui-react-native';
export function ConnectButton({
  label,
  loadingLabel,
  size = 'md',
  style,
  disabled,
  testID
}) {
  const {
    open,
    loading
  } = useSnapshot(ModalController.state);
  const {
    themeMode,
    themeVariables
  } = useSnapshot(ThemeController.state);
  return /*#__PURE__*/React.createElement(ThemeProvider, {
    themeMode: themeMode,
    themeVariables: themeVariables
  }, /*#__PURE__*/React.createElement(ConnectButtonUI, {
    onPress: () => ModalController.open(),
    size: size,
    loading: loading || open,
    style: style,
    testID: testID,
    disabled: disabled
  }, loading || open ? loadingLabel : label));
}
//# sourceMappingURL=index.js.map