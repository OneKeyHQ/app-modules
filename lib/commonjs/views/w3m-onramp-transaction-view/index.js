"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.OnRampTransactionView = OnRampTransactionView;
var _valtio = require("valtio");
var _react = require("react");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _appkitCommonReactNative = require("@reown/appkit-common-react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _styles = _interopRequireDefault(require("./styles"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
function OnRampTransactionView() {
  const Theme = (0, _appkitUiReactNative.useTheme)();
  const {
    purchaseCurrency
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.OnRampController.state);
  const {
    data
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.RouterController.state);
  const onClose = () => {
    const isAuth = _appkitCoreReactNative.ConnectorController.state.connectedConnector === 'AUTH';
    _appkitCoreReactNative.RouterController.replace(isAuth ? 'Account' : 'AccountDefault');
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
    const _purchaseCurrency = _appkitCoreReactNative.RouterController.state.data?.onrampResult?.purchaseCurrency;
    if (!_purchaseCurrency) return '';
    try {
      return _purchaseCurrency.split('_')[0] ?? _purchaseCurrency;
    } catch {
      return _purchaseCurrency;
    }
  };
  (0, _react.useEffect)(() => {
    return () => {
      _appkitCoreReactNative.OnRampController.resetState();
      _appkitCoreReactNative.AccountController.fetchTokenBalance().catch(() => {
        // Silently handle any errors
      });
    };
  }, []);
  return /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    padding: ['3xs', 'l', '4xl', 'l']
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, null, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    alignItems: "center"
  }, isProcessingError ? /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(_appkitUiReactNative.IconBox, {
    icon: "warningCircle",
    size: "xl",
    iconColor: "yellow-100",
    background: true,
    backgroundColor: "gray-glass-005",
    style: _styles.default.icon
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "medium-600",
    color: "fg-100",
    style: _styles.default.errorTitle
  }, "Unable to process provider information"), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "paragraph-400",
    color: "fg-150",
    style: _styles.default.errorDescription
  }, "Please refresh your activity to see if the transaction was successful")) : /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(_appkitUiReactNative.IconBox, {
    icon: "checkmark",
    size: "xl",
    iconColor: "success-100",
    background: true,
    backgroundColor: "success-glass-020",
    style: _styles.default.icon
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "medium-600",
    color: "fg-100"
  }, "You successfully bought ", currency))), showDetails && !isProcessingError && /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    style: [_styles.default.card, {
      backgroundColor: Theme['gray-glass-005']
    }],
    padding: ['s', 'm', 's', 'm'],
    margin: ['s', '0', '0', '0']
  }, showPaid && /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    margin: ['2xs', '0', '2xs', '0']
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "paragraph-400",
    color: "fg-150"
  }, "You Paid"), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "paragraph-500"
  }, data?.onrampResult?.paymentAmount, " ", data?.onrampResult?.paymentCurrency)), showBought && /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    margin: ['2xs', '0', '2xs', '0']
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "paragraph-400",
    color: "fg-150"
  }, "You Bought"), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    alignItems: "center"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "paragraph-500"
  }, data?.onrampResult?.purchaseAmount, " ", getPurchaseCurrencyDisplay()), data?.onrampResult?.purchaseImageUrl && /*#__PURE__*/React.createElement(_appkitUiReactNative.Image, {
    source: data?.onrampResult?.purchaseImageUrl,
    style: [_styles.default.tokenImage, {
      borderColor: Theme['gray-glass-010']
    }],
    onError: () => {
      // Silently handle image loading errors
    }
  }))), showNetwork && /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    margin: ['2xs', '0', '2xs', '0']
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "paragraph-400",
    color: "fg-150"
  }, "Network"), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "paragraph-500"
  }, _appkitCommonReactNative.StringUtil.capitalize(data?.onrampResult?.network))), showStatus && /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    margin: ['2xs', '0', '0', '0']
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "paragraph-400",
    color: "fg-150"
  }, "Status"), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "paragraph-500"
  }, _appkitCommonReactNative.StringUtil.capitalize(data?.onrampResult?.status))))), /*#__PURE__*/React.createElement(_appkitUiReactNative.Button, {
    variant: "fill",
    size: "md",
    onPress: onClose,
    style: _styles.default.button
  }, "Go back"));
}
//# sourceMappingURL=index.js.map