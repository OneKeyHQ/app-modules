"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.SelectPaymentModal = SelectPaymentModal;
var _valtio = require("valtio");
var _react = require("react");
var _reactNativeModal = _interopRequireDefault(require("react-native-modal"));
var _reactNative = require("react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _Quote = require("./Quote");
var _PaymentMethod = require("./PaymentMethod");
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
/* eslint-disable valtio/state-snapshot-rule */

const SEPARATOR_HEIGHT = _appkitUiReactNative.Spacing.s;
function SelectPaymentModal({
  title,
  visible,
  onClose
}) {
  const Theme = (0, _appkitUiReactNative.useTheme)();
  const {
    selectedQuote,
    quotes,
    selectedPaymentMethod
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.OnRampController.state);
  const paymentMethodsRef = (0, _react.useRef)(null);
  const [paymentMethods, setPaymentMethods] = (0, _react.useState)(_appkitCoreReactNative.OnRampController.state.paymentMethods);
  const [activePaymentMethod, setActivePaymentMethod] = (0, _react.useState)(_appkitCoreReactNative.OnRampController.state.selectedPaymentMethod);
  const availablePaymentMethods = (0, _react.useMemo)(() => {
    return paymentMethods.filter(paymentMethod => quotes?.some(quote => quote.paymentMethodType === paymentMethod.paymentMethod));
  }, [paymentMethods, quotes]);
  const availableQuotes = (0, _react.useMemo)(() => {
    return quotes?.filter(quote => activePaymentMethod?.paymentMethod === quote.paymentMethodType);
  }, [quotes, activePaymentMethod]);
  const sortedQuotes = (0, _react.useMemo)(() => {
    if (!selectedQuote || selectedQuote.paymentMethodType !== activePaymentMethod?.paymentMethod) {
      return availableQuotes;
    }
    return [selectedQuote, ...(availableQuotes?.filter(quote => quote.serviceProvider !== selectedQuote.serviceProvider) ?? [])];
  }, [availableQuotes, selectedQuote, activePaymentMethod]);
  const renderSeparator = () => {
    return /*#__PURE__*/React.createElement(_reactNative.View, {
      style: {
        height: SEPARATOR_HEIGHT
      }
    });
  };
  const handleQuotePress = quote => {
    if (activePaymentMethod) {
      _appkitCoreReactNative.OnRampController.clearError();
      _appkitCoreReactNative.OnRampController.setSelectedQuote(quote);
      _appkitCoreReactNative.OnRampController.setSelectedPaymentMethod(activePaymentMethod);
    }
    onClose();
  };
  const handlePaymentMethodPress = paymentMethod => {
    setActivePaymentMethod(paymentMethod);
  };
  const renderQuote = ({
    item,
    index
  }) => {
    const logoURL = _appkitCoreReactNative.OnRampController.getServiceProviderImage(item.serviceProvider);
    const isSelected = item.serviceProvider === _appkitCoreReactNative.OnRampController.state.selectedQuote?.serviceProvider && item.paymentMethodType === _appkitCoreReactNative.OnRampController.state.selectedQuote?.paymentMethodType;
    const isRecommended = availableQuotes?.findIndex(quote => quote.serviceProvider === item.serviceProvider) === 0 && availableQuotes?.length > 1;
    const tagText = isRecommended ? 'Recommended' : item.lowKyc ? 'Low KYC' : undefined;
    return /*#__PURE__*/React.createElement(_Quote.Quote, {
      item: item,
      selected: isSelected,
      logoURL: logoURL,
      onQuotePress: () => handleQuotePress(item),
      tagText: tagText,
      testID: `quote-item-${index}`
    });
  };
  const renderPaymentMethod = ({
    item
  }) => {
    const parsedItem = item;
    const isSelected = parsedItem.paymentMethod === activePaymentMethod?.paymentMethod;
    return /*#__PURE__*/React.createElement(_PaymentMethod.PaymentMethod, {
      item: parsedItem,
      onPress: () => handlePaymentMethodPress(parsedItem),
      selected: isSelected,
      testID: `payment-method-item-${parsedItem.paymentMethod}`
    });
  };
  (0, _react.useEffect)(() => {
    if (visible && _appkitCoreReactNative.OnRampController.state.selectedPaymentMethod) {
      const methods = [_appkitCoreReactNative.OnRampController.state.selectedPaymentMethod, ..._appkitCoreReactNative.OnRampController.state.paymentMethods.filter(m => m.paymentMethod !== _appkitCoreReactNative.OnRampController.state.selectedPaymentMethod?.paymentMethod)];
      //Update payment methods order
      setPaymentMethods(methods);
      setActivePaymentMethod(_appkitCoreReactNative.OnRampController.state.selectedPaymentMethod);
    }
  }, [visible]);
  return /*#__PURE__*/React.createElement(_reactNativeModal.default, {
    isVisible: visible,
    useNativeDriver: true,
    useNativeDriverForBackdrop: true,
    statusBarTranslucent: true,
    hideModalContentWhileAnimating: true,
    onBackdropPress: onClose,
    onDismiss: onClose,
    style: styles.modal
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    style: [styles.container, {
      backgroundColor: Theme['bg-100']
    }]
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    alignItems: "center",
    justifyContent: "space-between",
    flexDirection: "row",
    style: styles.header
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.IconLink, {
    icon: "arrowLeft",
    onPress: onClose,
    testID: "payment-modal-button-back"
  }), !!title && /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "paragraph-600"
  }, title), /*#__PURE__*/React.createElement(_reactNative.View, {
    style: styles.iconPlaceholder
  })), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-500",
    color: "fg-150",
    style: styles.subtitle
  }, "Pay with"), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, null, /*#__PURE__*/React.createElement(_reactNative.FlatList, {
    data: availablePaymentMethods,
    renderItem: renderPaymentMethod,
    ref: paymentMethodsRef,
    style: styles.paymentMethodsContainer,
    contentContainerStyle: styles.paymentMethodsContent,
    fadingEdgeLength: 20,
    keyExtractor: item => item.paymentMethod,
    horizontal: true,
    showsHorizontalScrollIndicator: false
  })), /*#__PURE__*/React.createElement(_appkitUiReactNative.Separator, {
    style: styles.separator,
    color: "gray-glass-010"
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-500",
    color: "fg-150",
    style: styles.subtitle
  }, "Providers"), /*#__PURE__*/React.createElement(_reactNative.FlatList, {
    data: sortedQuotes,
    bounces: false,
    renderItem: renderQuote,
    extraData: selectedPaymentMethod,
    contentContainerStyle: styles.listContent,
    ItemSeparatorComponent: renderSeparator,
    fadingEdgeLength: 20,
    keyExtractor: item => `${item.serviceProvider}-${item.paymentMethodType}`,
    getItemLayout: (_, index) => ({
      length: _Quote.ITEM_HEIGHT + SEPARATOR_HEIGHT,
      offset: (_Quote.ITEM_HEIGHT + SEPARATOR_HEIGHT) * index,
      index
    })
  })));
}
const styles = _reactNative.StyleSheet.create({
  modal: {
    margin: 0,
    justifyContent: 'flex-end'
  },
  header: {
    marginBottom: _appkitUiReactNative.Spacing.l,
    paddingHorizontal: _appkitUiReactNative.Spacing.m,
    paddingTop: _appkitUiReactNative.Spacing.m
  },
  container: {
    height: '80%',
    borderTopLeftRadius: _appkitUiReactNative.BorderRadius.l,
    borderTopRightRadius: _appkitUiReactNative.BorderRadius.l
  },
  separator: {
    width: undefined,
    marginVertical: _appkitUiReactNative.Spacing.m,
    marginHorizontal: _appkitUiReactNative.Spacing.m
  },
  listContent: {
    paddingTop: _appkitUiReactNative.Spacing['3xs'],
    paddingBottom: _appkitUiReactNative.Spacing['4xl'],
    paddingHorizontal: _appkitUiReactNative.Spacing.m
  },
  iconPlaceholder: {
    height: 32,
    width: 32
  },
  subtitle: {
    marginBottom: _appkitUiReactNative.Spacing.xs,
    marginHorizontal: _appkitUiReactNative.Spacing.m
  },
  emptyContainer: {
    height: 150
  },
  paymentMethodsContainer: {
    paddingHorizontal: _appkitUiReactNative.Spacing['3xs']
  },
  paymentMethodsContent: {
    paddingLeft: _appkitUiReactNative.Spacing.xs
  }
});
//# sourceMappingURL=SelectPaymentModal.js.map