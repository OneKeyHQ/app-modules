import { useSnapshot } from 'valtio';
import { AccountButton } from '../w3m-account-button';
import { ConnectButton } from '../w3m-connect-button';
import { AccountController, ModalController } from '@reown/appkit-core-react-native';
export function AppKitButton({
  balance,
  disabled,
  size,
  label = 'Connect',
  loadingLabel = 'Connecting',
  accountStyle,
  connectStyle
}) {
  const {
    isConnected
  } = useSnapshot(AccountController.state);
  const {
    loading
  } = useSnapshot(ModalController.state);
  return !loading && isConnected ? /*#__PURE__*/React.createElement(AccountButton, {
    style: accountStyle,
    balance: balance,
    disabled: disabled,
    testID: "account-button"
  }) : /*#__PURE__*/React.createElement(ConnectButton, {
    style: connectStyle,
    size: size,
    label: label,
    loadingLabel: loadingLabel,
    disabled: disabled,
    testID: "connect-button"
  });
}
//# sourceMappingURL=index.js.map