import { useSnapshot } from 'valtio';
import { useEffect } from 'react';
import { AccountController, ConnectorController, OnRampController, RouterController } from '@reown/appkit-core-react-native';
import { StringUtil } from '@reown/appkit-common-react-native';
import { Button, FlexView, IconBox, Image, Text, useTheme } from '@reown/appkit-ui-react-native';
import styles from './styles';
export function OnRampTransactionView() {
  const Theme = useTheme();
  const {
    purchaseCurrency
  } = useSnapshot(OnRampController.state);
  const {
    data
  } = useSnapshot(RouterController.state);
  const onClose = () => {
    const isAuth = ConnectorController.state.connectedConnector === 'AUTH';
    RouterController.replace(isAuth ? 'Account' : 'AccountDefault');
  };
  const currency = data?.onrampResult?.purchaseCurrency ?? (purchaseCurrency?.name || purchaseCurrency?.currencyCode) ?? 'crypto';
  const showPaid = !!data?.onrampResult?.paymentAmount && !!data?.onrampResult?.paymentCurrency;
  const showBought = !!data?.onrampResult?.purchaseAmount && !!data?.onrampResult?.purchaseCurrency;
  const showNetwork = !!data?.onrampResult?.network;
  const showStatus = !!data?.onrampResult?.status;
  const showDetails = showPaid || showBought || showNetwork || showStatus;
  const hasAnyRedirectData = !!data?.onrampResult?.status || showPaid || showBought;
  const isProcessingError = !hasAnyRedirectData;
  const getPurchaseCurrencyDisplay = () => {
    const _purchaseCurrency = RouterController.state.data?.onrampResult?.purchaseCurrency;
    if (!_purchaseCurrency) return '';
    try {
      return _purchaseCurrency.split('_')[0] ?? _purchaseCurrency;
    } catch {
      return _purchaseCurrency;
    }
  };
  useEffect(() => {
    return () => {
      OnRampController.resetState();
      AccountController.fetchTokenBalance().catch(() => {
        // Silently handle any errors
      });
    };
  }, []);
  return /*#__PURE__*/React.createElement(FlexView, {
    padding: ['3xs', 'l', '4xl', 'l']
  }, /*#__PURE__*/React.createElement(FlexView, null, /*#__PURE__*/React.createElement(FlexView, {
    alignItems: "center"
  }, isProcessingError ? /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(IconBox, {
    icon: "warningCircle",
    size: "xl",
    iconColor: "yellow-100",
    background: true,
    backgroundColor: "gray-glass-005",
    style: styles.icon
  }), /*#__PURE__*/React.createElement(Text, {
    variant: "medium-600",
    color: "fg-100",
    style: styles.errorTitle
  }, "Unable to process provider information"), /*#__PURE__*/React.createElement(Text, {
    variant: "paragraph-400",
    color: "fg-150",
    style: styles.errorDescription
  }, "Please refresh your activity to see if the transaction was successful")) : /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(IconBox, {
    icon: "checkmark",
    size: "xl",
    iconColor: "success-100",
    background: true,
    backgroundColor: "success-glass-020",
    style: styles.icon
  }), /*#__PURE__*/React.createElement(Text, {
    variant: "medium-600",
    color: "fg-100"
  }, "You successfully bought ", currency))), showDetails && !isProcessingError && /*#__PURE__*/React.createElement(FlexView, {
    style: [styles.card, {
      backgroundColor: Theme['gray-glass-005']
    }],
    padding: ['s', 'm', 's', 'm'],
    margin: ['s', '0', '0', '0']
  }, showPaid && /*#__PURE__*/React.createElement(FlexView, {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    margin: ['2xs', '0', '2xs', '0']
  }, /*#__PURE__*/React.createElement(Text, {
    variant: "paragraph-400",
    color: "fg-150"
  }, "You Paid"), /*#__PURE__*/React.createElement(Text, {
    variant: "paragraph-500"
  }, data?.onrampResult?.paymentAmount, " ", data?.onrampResult?.paymentCurrency)), showBought && /*#__PURE__*/React.createElement(FlexView, {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    margin: ['2xs', '0', '2xs', '0']
  }, /*#__PURE__*/React.createElement(Text, {
    variant: "paragraph-400",
    color: "fg-150"
  }, "You Bought"), /*#__PURE__*/React.createElement(FlexView, {
    flexDirection: "row",
    alignItems: "center"
  }, /*#__PURE__*/React.createElement(Text, {
    variant: "paragraph-500"
  }, data?.onrampResult?.purchaseAmount, " ", getPurchaseCurrencyDisplay()), data?.onrampResult?.purchaseImageUrl && /*#__PURE__*/React.createElement(Image, {
    source: data?.onrampResult?.purchaseImageUrl,
    style: [styles.tokenImage, {
      borderColor: Theme['gray-glass-010']
    }],
    onError: () => {
      // Silently handle image loading errors
    }
  }))), showNetwork && /*#__PURE__*/React.createElement(FlexView, {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    margin: ['2xs', '0', '2xs', '0']
  }, /*#__PURE__*/React.createElement(Text, {
    variant: "paragraph-400",
    color: "fg-150"
  }, "Network"), /*#__PURE__*/React.createElement(Text, {
    variant: "paragraph-500"
  }, StringUtil.capitalize(data?.onrampResult?.network))), showStatus && /*#__PURE__*/React.createElement(FlexView, {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    margin: ['2xs', '0', '0', '0']
  }, /*#__PURE__*/React.createElement(Text, {
    variant: "paragraph-400",
    color: "fg-150"
  }, "Status"), /*#__PURE__*/React.createElement(Text, {
    variant: "paragraph-500"
  }, StringUtil.capitalize(data?.onrampResult?.status))))), /*#__PURE__*/React.createElement(Button, {
    variant: "fill",
    size: "md",
    onPress: onClose,
    style: styles.button
  }, "Go back"));
}
//# sourceMappingURL=index.js.map