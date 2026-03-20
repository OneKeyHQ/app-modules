import { OnRampController, RouterController, ThemeController } from '@reown/appkit-core-react-native';
import { BorderRadius, Button, FlexView, Image, Separator, Spacing, Text, useTheme } from '@reown/appkit-ui-react-native';
import { StyleSheet } from 'react-native';
import { useSnapshot } from 'valtio';
import { NumberUtil, StringUtil } from '@reown/appkit-common-react-native';
export function OnRampCheckoutView() {
  const Theme = useTheme();
  const {
    themeMode
  } = useSnapshot(ThemeController.state);
  const {
    selectedQuote,
    selectedPaymentMethod,
    purchaseCurrency
  } = useSnapshot(OnRampController.state);
  const value = NumberUtil.roundNumber(selectedQuote?.destinationAmount ?? 0, 6, 5);
  const symbol = selectedQuote?.destinationCurrencyCode;
  const paymentLogo = selectedPaymentMethod?.logos[themeMode ?? 'light'];
  const providerImage = OnRampController.getServiceProviderImage(selectedQuote?.serviceProvider ?? '');
  const showNetworkFee = selectedQuote?.networkFee != null;
  const showTransactionFee = selectedQuote?.transactionFee != null;
  const showTotalFee = selectedQuote?.totalFee != null;
  const showFees = showNetworkFee || showTransactionFee || showTotalFee;
  const onConfirm = () => {
    RouterController.push('OnRampLoading');
  };
  return /*#__PURE__*/React.createElement(FlexView, {
    padding: ['2xl', 'l', '4xl', 'l']
  }, /*#__PURE__*/React.createElement(FlexView, {
    alignItems: "center"
  }, /*#__PURE__*/React.createElement(Text, {
    color: "fg-200"
  }, "You Buy"), /*#__PURE__*/React.createElement(FlexView, {
    flexDirection: "row",
    alignItems: "center"
  }, /*#__PURE__*/React.createElement(Text, {
    style: [styles.amount, {
      color: Theme['fg-100']
    }]
  }, value), /*#__PURE__*/React.createElement(Text, {
    variant: "paragraph-400",
    color: "fg-200"
  }, symbol?.split('_')[0] ?? symbol ?? '')), /*#__PURE__*/React.createElement(FlexView, {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center"
  }, /*#__PURE__*/React.createElement(Text, {
    color: "fg-200"
  }, "via "), providerImage && /*#__PURE__*/React.createElement(Image, {
    source: providerImage,
    style: styles.providerImage
  }), /*#__PURE__*/React.createElement(Text, {
    color: "fg-200"
  }, StringUtil.capitalize(selectedQuote?.serviceProvider)))), /*#__PURE__*/React.createElement(Separator, {
    style: styles.separator,
    color: "gray-glass-010"
  }), /*#__PURE__*/React.createElement(FlexView, {
    padding: ['s', 's', 'xs', 's'],
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center"
  }, /*#__PURE__*/React.createElement(Text, {
    color: "fg-200"
  }, "You Pay"), /*#__PURE__*/React.createElement(Text, null, selectedQuote?.sourceAmount, " ", selectedQuote?.sourceCurrencyCode)), /*#__PURE__*/React.createElement(FlexView, {
    padding: ['xs', 's', 'xs', 's'],
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center"
  }, /*#__PURE__*/React.createElement(Text, {
    color: "fg-200"
  }, "You Receive"), /*#__PURE__*/React.createElement(FlexView, {
    flexDirection: "row",
    alignItems: "center"
  }, /*#__PURE__*/React.createElement(Text, null, value, " ", symbol?.split('_')[0] ?? ''), purchaseCurrency?.symbolImageUrl && /*#__PURE__*/React.createElement(Image, {
    source: purchaseCurrency?.symbolImageUrl,
    style: [styles.tokenImage, {
      borderColor: Theme['gray-glass-010']
    }]
  }))), /*#__PURE__*/React.createElement(FlexView, {
    padding: ['xs', 's', 'xs', 's'],
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between"
  }, /*#__PURE__*/React.createElement(Text, {
    color: "fg-200"
  }, "Network"), /*#__PURE__*/React.createElement(FlexView, {
    flexDirection: "row",
    alignItems: "center"
  }, /*#__PURE__*/React.createElement(Text, null, purchaseCurrency?.chainName))), /*#__PURE__*/React.createElement(FlexView, {
    padding: ['xs', 's', 'xs', 's'],
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between"
  }, /*#__PURE__*/React.createElement(Text, {
    color: "fg-200"
  }, "Pay with"), /*#__PURE__*/React.createElement(FlexView, {
    flexDirection: "row",
    alignItems: "center",
    style: [styles.paymentMethodContainer, {
      borderColor: Theme['gray-glass-020']
    }]
  }, paymentLogo && /*#__PURE__*/React.createElement(Image, {
    source: paymentLogo,
    style: styles.paymentMethodImage,
    tintColor: Theme['fg-150']
  }), /*#__PURE__*/React.createElement(Text, {
    variant: "small-600",
    color: "fg-150"
  }, selectedPaymentMethod?.name))), showFees && /*#__PURE__*/React.createElement(FlexView, {
    padding: ['xs', 's', 'xs', 's'],
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center"
  }, /*#__PURE__*/React.createElement(Text, {
    color: "fg-200"
  }, "Fees"), /*#__PURE__*/React.createElement(Text, null, selectedQuote?.totalFee, " ", selectedQuote?.sourceCurrencyCode)), /*#__PURE__*/React.createElement(FlexView, {
    flexDirection: "row",
    justifyContent: "space-between",
    margin: ['xl', '0', '0', '0']
  }, /*#__PURE__*/React.createElement(Button, {
    variant: "shade",
    size: "md",
    style: styles.cancelButton,
    onPress: RouterController.goBack
  }, "Back"), /*#__PURE__*/React.createElement(Button, {
    variant: "fill",
    size: "md",
    style: styles.confirmButton,
    onPress: onConfirm,
    testID: "button-confirm"
  }, "Confirm")));
}
const styles = StyleSheet.create({
  amount: {
    fontSize: 38,
    marginRight: Spacing['3xs']
  },
  separator: {
    marginVertical: Spacing.m
  },
  paymentMethodImage: {
    width: 14,
    height: 14,
    marginRight: Spacing['3xs']
  },
  confirmButton: {
    marginLeft: Spacing.s,
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
    borderRadius: BorderRadius.full,
    borderWidth: 1
  },
  networkImage: {
    height: 16,
    width: 16,
    marginRight: 4,
    borderRadius: BorderRadius.full,
    borderWidth: 1
  },
  paymentMethodContainer: {
    borderWidth: StyleSheet.hairlineWidth,
    borderRadius: BorderRadius.full,
    padding: Spacing.xs
  }
});
//# sourceMappingURL=index.js.map