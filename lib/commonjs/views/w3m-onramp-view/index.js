"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.OnRampView = OnRampView;
var _valtio = require("valtio");
var _react = require("react");
var _reactNative = require("react-native");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _appkitCommonReactNative = require("@reown/appkit-common-react-native");
var _w3mSelectorModal = require("../../partials/w3m-selector-modal");
var _Currency = require("./components/Currency");
var _utils = require("./utils");
var _CurrencyInput = require("./components/CurrencyInput");
var _SelectPaymentModal = require("./components/SelectPaymentModal");
var _Header = require("./components/Header");
var _LoadingView = require("./components/LoadingView");
var _PaymentButton = _interopRequireDefault(require("./components/PaymentButton"));
var _styles = _interopRequireDefault(require("./styles"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
const MemoizedCurrency = /*#__PURE__*/(0, _react.memo)(_Currency.Currency);
function OnRampView() {
  const {
    themeMode
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.ThemeController.state);
  const Theme = (0, _appkitUiReactNative.useTheme)();
  const {
    purchaseCurrency,
    paymentCurrency,
    selectedPaymentMethod,
    paymentAmount,
    quotesLoading,
    quotes,
    selectedQuote,
    error,
    loading,
    initialLoading
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.OnRampController.state);
  const {
    caipNetwork
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.NetworkController.state);
  const {
    networkImages
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.AssetController.state);
  const [searchValue, setSearchValue] = (0, _react.useState)('');
  const [isCurrencyModalVisible, setIsCurrencyModalVisible] = (0, _react.useState)(false);
  const [isPaymentMethodModalVisible, setIsPaymentMethodModalVisible] = (0, _react.useState)(false);
  const purchaseCurrencyCode = purchaseCurrency?.currencyCode?.split('_')[0] ?? purchaseCurrency?.currencyCode;
  const networkImage = _appkitCoreReactNative.AssetUtil.getNetworkImage(caipNetwork, networkImages);
  const getQuotes = (0, _react.useCallback)(() => {
    if (_appkitCoreReactNative.OnRampController.canGenerateQuote()) {
      _appkitCoreReactNative.OnRampController.getQuotes();
    }
  }, []);
  const getPaymentButtonTitle = () => {
    if (selectedPaymentMethod) {
      return selectedPaymentMethod.name;
    }
    if (quotesLoading) {
      return 'Loading quotes';
    }
    if (!paymentAmount || quotes?.length === 0) {
      return 'Enter a valid amount';
    }
    return '';
  };
  const getPaymentButtonSubtitle = () => {
    if (selectedQuote) {
      return _appkitCommonReactNative.StringUtil.capitalize(selectedQuote?.serviceProvider);
    }
    if (selectedPaymentMethod) {
      if (quotesLoading) {
        return 'Loading quotes';
      }
      if (!paymentAmount || quotes?.length === 0) {
        return 'Enter a valid amount';
      }
    }
    return undefined;
  };
  const onValueChange = value => {
    if (!value) {
      _appkitCoreReactNative.OnRampController.abortGetQuotes();
      _appkitCoreReactNative.OnRampController.setPaymentAmount(0);
      _appkitCoreReactNative.OnRampController.setSelectedQuote(undefined);
      _appkitCoreReactNative.OnRampController.clearError();
      return;
    }
    _appkitCoreReactNative.OnRampController.setPaymentAmount(value);
    (0, _utils.getQuotesDebounced)();
  };
  const handleSearch = value => {
    setSearchValue(value);
  };
  const handleContinue = async () => {
    if (_appkitCoreReactNative.OnRampController.state.selectedQuote) {
      _appkitCoreReactNative.RouterController.push('OnRampCheckout');
    }
  };
  const renderCurrencyItem = ({
    item
  }) => {
    return /*#__PURE__*/React.createElement(MemoizedCurrency, {
      item: item,
      onPress: onPressPurchaseCurrency,
      selected: item.currencyCode === purchaseCurrency?.currencyCode,
      title: item.name,
      subtitle: item.currencyCode.split('_')[0] ?? item.currencyCode,
      testID: `currency-item-${item.currencyCode}`
    });
  };
  const onPressPurchaseCurrency = item => {
    setIsCurrencyModalVisible(false);
    setIsPaymentMethodModalVisible(false);
    setSearchValue('');
    _appkitCoreReactNative.OnRampController.setPurchaseCurrency(item);
    getQuotes();
  };
  const onModalClose = () => {
    setSearchValue('');
    setIsCurrencyModalVisible(false);
    setIsPaymentMethodModalVisible(false);
  };
  (0, _react.useEffect)(() => {
    if (error?.type === _appkitCoreReactNative.ConstantsUtil.ONRAMP_ERROR_TYPES.FAILED_TO_LOAD) {
      _appkitCoreReactNative.SnackController.showInternalError({
        shortMessage: 'Failed to load data. Please try again later.',
        longMessage: error?.message
      });
      _appkitCoreReactNative.RouterController.goBack();
    }
  }, [error]);
  (0, _react.useEffect)(() => {
    if (_appkitCoreReactNative.OnRampController.state.countries.length === 0) {
      _appkitCoreReactNative.OnRampController.loadOnRampData();
    }
  }, []);
  if (initialLoading || _appkitCoreReactNative.OnRampController.state.countries.length === 0) {
    return /*#__PURE__*/React.createElement(_LoadingView.LoadingView, null);
  }
  return /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(_Header.Header, {
    onSettingsPress: () => _appkitCoreReactNative.RouterController.push('OnRampSettings')
  }), /*#__PURE__*/React.createElement(_reactNative.ScrollView, {
    bounces: false
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    padding: ['s', 'l', '4xl', 'l']
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-400",
    color: "fg-150"
  }, "You Buy"), /*#__PURE__*/React.createElement(_appkitUiReactNative.TokenButton, {
    placeholder: 'Select currency',
    imageUrl: purchaseCurrency?.symbolImageUrl,
    text: purchaseCurrencyCode,
    onPress: () => setIsCurrencyModalVisible(true),
    testID: "currency-selector",
    chevron: true,
    renderClip: networkImage ? /*#__PURE__*/React.createElement(_appkitUiReactNative.Image, {
      source: networkImage,
      style: [_styles.default.networkImage, {
        borderColor: Theme['bg-300']
      }]
    }) : null
  })), /*#__PURE__*/React.createElement(_CurrencyInput.CurrencyInput, {
    value: paymentAmount?.toString(),
    symbol: paymentCurrency?.currencyCode,
    error: error?.message,
    isAmountError: error?.type === _appkitCoreReactNative.ConstantsUtil.ONRAMP_ERROR_TYPES.AMOUNT_TOO_LOW || error?.type === _appkitCoreReactNative.ConstantsUtil.ONRAMP_ERROR_TYPES.AMOUNT_TOO_HIGH || error?.type === _appkitCoreReactNative.ConstantsUtil.ONRAMP_ERROR_TYPES.INVALID_AMOUNT,
    loading: loading || quotesLoading,
    purchaseValue: `${selectedQuote?.destinationAmount ? _appkitCommonReactNative.NumberUtil.roundNumber(selectedQuote.destinationAmount, 6, 5)?.toString() : '0.00'} ${purchaseCurrencyCode ?? ''}`,
    onValueChange: onValueChange,
    style: _styles.default.currencyInput
  }), /*#__PURE__*/React.createElement(_PaymentButton.default, {
    title: getPaymentButtonTitle(),
    subtitle: getPaymentButtonSubtitle(),
    paymentLogo: selectedPaymentMethod?.logos[themeMode ?? 'light'],
    providerLogo: _appkitCoreReactNative.OnRampController.getServiceProviderImage(selectedQuote?.serviceProvider),
    disabled: !paymentAmount || quotes?.length === 0,
    loading: quotesLoading || loading,
    onPress: () => setIsPaymentMethodModalVisible(true),
    testID: "payment-method-button"
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
    margin: ['m', '0', '0', '0']
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Button, {
    variant: "shade",
    style: _styles.default.cancelButton,
    onPress: _appkitCoreReactNative.RouterController.goBack
  }, "Cancel"), /*#__PURE__*/React.createElement(_appkitUiReactNative.Button, {
    style: _styles.default.continueButton,
    onPress: handleContinue,
    disabled: quotesLoading || loading || !selectedQuote,
    testID: "button-continue"
  }, "Continue")), /*#__PURE__*/React.createElement(_SelectPaymentModal.SelectPaymentModal, {
    visible: isPaymentMethodModalVisible,
    onClose: onModalClose,
    title: "Payment"
  }), /*#__PURE__*/React.createElement(_w3mSelectorModal.SelectorModal, {
    selectedItem: purchaseCurrency,
    visible: isCurrencyModalVisible,
    onClose: onModalClose,
    items: (0, _utils.getPurchaseCurrencies)(searchValue, true),
    onSearch: handleSearch,
    renderItem: renderCurrencyItem,
    keyExtractor: item => item.currencyCode,
    title: "Select token",
    itemHeight: _Currency.ITEM_HEIGHT,
    showNetwork: true
  }))));
}
//# sourceMappingURL=index.js.map