"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.OnRampCheckoutView = OnRampCheckoutView;
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _reactNative = require("react-native");
var _valtio = require("valtio");
var _appkitCommonReactNative = require("@reown/appkit-common-react-native");
function OnRampCheckoutView() {
  const Theme = (0, _appkitUiReactNative.useTheme)();
  const {
    themeMode
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.ThemeController.state);
  const {
    selectedQuote,
    selectedPaymentMethod,
    purchaseCurrency
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.OnRampController.state);
  const value = _appkitCommonReactNative.NumberUtil.roundNumber(selectedQuote?.destinationAmount ?? 0, 6, 5);
  const symbol = selectedQuote?.destinationCurrencyCode;
  const paymentLogo = selectedPaymentMethod?.logos[themeMode ?? 'light'];
  const providerImage = _appkitCoreReactNative.OnRampController.getServiceProviderImage(selectedQuote?.serviceProvider ?? '');
  const showNetworkFee = selectedQuote?.networkFee != null;
  const showTransactionFee = selectedQuote?.transactionFee != null;
  const showTotalFee = selectedQuote?.totalFee != null;
  const showFees = showNetworkFee || showTransactionFee || showTotalFee;
  const onConfirm = () => {
    _appkitCoreReactNative.RouterController.push('OnRampLoading');
  };
  return /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    padding: ['2xl', 'l', '4xl', 'l']
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    alignItems: "center"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    color: "fg-200"
  }, "You Buy"), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    alignItems: "center"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    style: [styles.amount, {
      color: Theme['fg-100']
    }]
  }, value), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "paragraph-400",
    color: "fg-200"
  }, symbol?.split('_')[0] ?? symbol ?? '')), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    color: "fg-200"
  }, "via "), providerImage && /*#__PURE__*/React.createElement(_appkitUiReactNative.Image, {
    source: providerImage,
    style: styles.providerImage
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    color: "fg-200"
  }, _appkitCommonReactNative.StringUtil.capitalize(selectedQuote?.serviceProvider)))), /*#__PURE__*/React.createElement(_appkitUiReactNative.Separator, {
    style: styles.separator,
    color: "gray-glass-010"
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    padding: ['s', 's', 'xs', 's'],
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    color: "fg-200"
  }, "You Pay"), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, null, selectedQuote?.sourceAmount, " ", selectedQuote?.sourceCurrencyCode)), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    padding: ['xs', 's', 'xs', 's'],
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    color: "fg-200"
  }, "You Receive"), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    alignItems: "center"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, null, value, " ", symbol?.split('_')[0] ?? ''), purchaseCurrency?.symbolImageUrl && /*#__PURE__*/React.createElement(_appkitUiReactNative.Image, {
    source: purchaseCurrency?.symbolImageUrl,
    style: [styles.tokenImage, {
      borderColor: Theme['gray-glass-010']
    }]
  }))), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    padding: ['xs', 's', 'xs', 's'],
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    color: "fg-200"
  }, "Network"), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    alignItems: "center"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, null, purchaseCurrency?.chainName))), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    padding: ['xs', 's', 'xs', 's'],
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    color: "fg-200"
  }, "Pay with"), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    alignItems: "center",
    style: [styles.paymentMethodContainer, {
      borderColor: Theme['gray-glass-020']
    }]
  }, paymentLogo && /*#__PURE__*/React.createElement(_appkitUiReactNative.Image, {
    source: paymentLogo,
    style: styles.paymentMethodImage,
    tintColor: Theme['fg-150']
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-600",
    color: "fg-150"
  }, selectedPaymentMethod?.name))), showFees && /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    padding: ['xs', 's', 'xs', 's'],
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    color: "fg-200"
  }, "Fees"), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, null, selectedQuote?.totalFee, " ", selectedQuote?.sourceCurrencyCode)), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    justifyContent: "space-between",
    margin: ['xl', '0', '0', '0']
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Button, {
    variant: "shade",
    size: "md",
    style: styles.cancelButton,
    onPress: _appkitCoreReactNative.RouterController.goBack
  }, "Back"), /*#__PURE__*/React.createElement(_appkitUiReactNative.Button, {
    variant: "fill",
    size: "md",
    style: styles.confirmButton,
    onPress: onConfirm,
    testID: "button-confirm"
  }, "Confirm")));
}
const styles = _reactNative.StyleSheet.create({
  amount: {
    fontSize: 38,
    marginRight: _appkitUiReactNative.Spacing['3xs']
  },
  separator: {
    marginVertical: _appkitUiReactNative.Spacing.m
  },
  paymentMethodImage: {
    width: 14,
    height: 14,
    marginRight: _appkitUiReactNative.Spacing['3xs']
  },
  confirmButton: {
    marginLeft: _appkitUiReactNative.Spacing.s,
    flex: 3
  },
  cancelButton: {
    flex: 1
  },
  providerImage: {
    height: 16,
    width: 16,
    marginRight: 2
  },
  tokenImage: {
    height: 20,
    width: 20,
    marginLeft: 4,
    borderRadius: _appkitUiReactNative.BorderRadius.full,
    borderWidth: 1
  },
  networkImage: {
    height: 16,
    width: 16,
    marginRight: 4,
    borderRadius: _appkitUiReactNative.BorderRadius.full,
    borderWidth: 1
  },
  paymentMethodContainer: {
    borderWidth: _reactNative.StyleSheet.hairlineWidth,
    borderRadius: _appkitUiReactNative.BorderRadius.full,
    padding: _appkitUiReactNative.Spacing.xs
  }
});
//# sourceMappingURL=index.js.map