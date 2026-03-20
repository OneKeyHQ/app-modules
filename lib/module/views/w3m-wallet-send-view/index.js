import { useCallback, useEffect } from 'react';
import { Platform, ScrollView } from 'react-native';
import { useSnapshot } from 'valtio';
import { AccountController, CoreHelperUtil, RouterController, SendController, SwapController } from '@reown/appkit-core-react-native';
import { Button, FlexView, IconBox, Spacing } from '@reown/appkit-ui-react-native';
import { SendInputToken } from '../../partials/w3m-send-input-token';
import { useCustomDimensions } from '../../hooks/useCustomDimensions';
import { useKeyboard } from '../../hooks/useKeyboard';
import { SendInputAddress } from '../../partials/w3m-send-input-address';
import styles from './styles';
export function WalletSendView() {
  const {
    padding
  } = useCustomDimensions();
  const {
    keyboardShown,
    keyboardHeight
  } = useKeyboard();
  const {
    token,
    sendTokenAmount,
    receiverAddress,
    receiverProfileName,
    loading,
    gasPrice
  } = useSnapshot(SendController.state);
  const {
    tokenBalance
  } = useSnapshot(AccountController.state);
  const paddingBottom = Platform.select({
    android: keyboardShown ? keyboardHeight + Spacing['2xl'] : Spacing['2xl'],
    default: Spacing['2xl']
  });
  const fetchNetworkPrice = useCallback(async () => {
    await SwapController.getNetworkTokenPrice();
    const gas = await SwapController.getInitialGasPrice();
    if (gas?.gasPrice && gas?.gasPriceInUSD) {
      SendController.setGasPrice(gas.gasPrice);
      SendController.setGasPriceInUsd(gas.gasPriceInUSD);
    }
  }, []);
  const onSendPress = () => {
    RouterController.push('WalletSendPreview');
  };
  const getActionText = () => {
    if (!SendController.state.token) {
      return 'Select token';
    }
    if (SendController.state.sendTokenAmount && SendController.state.token && SendController.state.sendTokenAmount > Number(SendController.state.token.quantity.numeric)) {
      return 'Insufficient funds';
    }
    if (!SendController.state.sendTokenAmount) {
      return 'Add amount';
    }
    if (SendController.state.sendTokenAmount && SendController.state.token?.price) {
      const value = SendController.state.sendTokenAmount * SendController.state.token.price;
      if (!value) {
        return 'Incorrect value';
      }
    }
    if (SendController.state.receiverAddress && !CoreHelperUtil.isAddress(SendController.state.receiverAddress)) {
      return 'Invalid address';
    }
    if (!SendController.state.receiverAddress) {
      return 'Add address';
    }
    return 'Preview send';
  };
  useEffect(() => {
    if (!token) {
      SendController.setToken(tokenBalance?.[0]);
    }
    fetchNetworkPrice();
  }, [token, tokenBalance, fetchNetworkPrice]);
  const actionText = getActionText();
  return /*#__PURE__*/React.createElement(ScrollView, {
    style: {
      paddingHorizontal: padding
    },
    bounces: false,
    keyboardShouldPersistTaps: "always"
  }, /*#__PURE__*/React.createElement(FlexView, {
    padding: "l",
    alignItems: "center",
    justifyContent: "center",
    style: {
      paddingBottom
    }
  }, /*#__PURE__*/React.createElement(SendInputToken, {
    token: token,
    sendTokenAmount: sendTokenAmount,
    gasPrice: Number(gasPrice),
    style: styles.tokenInput,
    onTokenPress: () => RouterController.push('WalletSendSelectToken')
  }), /*#__PURE__*/React.createElement(FlexView, {
    alignItems: "center",
    justifyContent: "center",
    style: styles.addressContainer
  }, /*#__PURE__*/React.createElement(SendInputAddress, {
    value: receiverProfileName || receiverAddress
  }), /*#__PURE__*/React.createElement(IconBox, {
    icon: "arrowBottom",
    size: "lg",
    iconColor: "fg-275",
    background: true,
    backgroundColor: "bg-175",
    border: true,
    borderColor: "bg-100",
    borderSize: 10,
    style: styles.arrowIcon
  })), /*#__PURE__*/React.createElement(Button, {
    style: styles.sendButton,
    onPress: onSendPress,
    disabled: !actionText.includes('Preview send'),
    loading: loading
  }, actionText)));
}
//# sourceMappingURL=index.js.map