"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.SwapPreviewView = SwapPreviewView;
var _valtio = require("valtio");
var _react = require("react");
var _reactNative = require("react-native");
var _appkitCommonReactNative = require("@reown/appkit-common-react-native");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _w3mSwapDetails = require("../../partials/w3m-swap-details");
var _useCustomDimensions = require("../../hooks/useCustomDimensions");
var _useKeyboard = require("../../hooks/useKeyboard");
var _styles = _interopRequireDefault(require("./styles"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
function SwapPreviewView() {
  const {
    padding
  } = (0, _useCustomDimensions.useCustomDimensions)();
  const {
    keyboardShown,
    keyboardHeight
  } = (0, _useKeyboard.useKeyboard)();
  const {
    sourceToken,
    sourceTokenAmount,
    sourceTokenPriceInUSD,
    toToken,
    toTokenAmount,
    toTokenPriceInUSD,
    loadingQuote,
    loadingBuildTransaction,
    loadingTransaction,
    loadingApprovalTransaction
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.SwapController.state);
  const sourceTokenMarketValue = _appkitCommonReactNative.NumberUtil.parseLocalStringToNumber(sourceTokenAmount) * sourceTokenPriceInUSD;
  const toTokenMarketValue = _appkitCommonReactNative.NumberUtil.parseLocalStringToNumber(toTokenAmount) * toTokenPriceInUSD;
  const paddingBottom = _reactNative.Platform.select({
    android: keyboardShown ? keyboardHeight + _appkitUiReactNative.Spacing['2xl'] : _appkitUiReactNative.Spacing['2xl'],
    default: _appkitUiReactNative.Spacing['2xl']
  });
  const loading = loadingQuote || loadingBuildTransaction || loadingTransaction || loadingApprovalTransaction;
  const onCancel = () => {
    _appkitCoreReactNative.RouterController.goBack();
  };
  const onSwap = () => {
    if (_appkitCoreReactNative.SwapController.state.approvalTransaction) {
      _appkitCoreReactNative.SwapController.sendTransactionForApproval(_appkitCoreReactNative.SwapController.state.approvalTransaction);
    } else {
      _appkitCoreReactNative.SwapController.sendTransactionForSwap(_appkitCoreReactNative.SwapController.state.swapTransaction);
    }
  };
  (0, _react.useEffect)(() => {
    function refreshTransaction() {
      if (!_appkitCoreReactNative.SwapController.state.loadingApprovalTransaction) {
        _appkitCoreReactNative.SwapController.getTransaction();
      }
    }
    _appkitCoreReactNative.SwapController.getTransaction();
    const interval = setInterval(refreshTransaction, 10000);
    return () => {
      clearInterval(interval);
    };
  }, []);
  return /*#__PURE__*/React.createElement(_reactNative.ScrollView, {
    style: {
      paddingHorizontal: padding
    },
    bounces: false,
    keyboardShouldPersistTaps: "always"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    padding: "l",
    justifyContent: "center",
    style: {
      paddingBottom
    }
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    justifyContent: "space-between"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, null, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-400",
    color: "fg-150"
  }, "Send"), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "paragraph-400",
    color: "fg-100"
  }, "$", _appkitUiReactNative.UiUtil.formatNumberToLocalString(sourceTokenMarketValue, 2))), /*#__PURE__*/React.createElement(_appkitUiReactNative.TokenButton, {
    text: ` ${sourceTokenAmount} ${sourceToken?.symbol}`,
    imageUrl: sourceToken?.logoUri,
    inverse: true,
    disabled: true
  })), /*#__PURE__*/React.createElement(_appkitUiReactNative.Icon, {
    name: "recycleHorizontal",
    size: "md",
    color: "fg-200",
    style: _styles.default.swapIcon
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    justifyContent: "space-between",
    margin: ['0', '0', 'xl', '0']
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, null, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-400",
    color: "fg-150"
  }, "Receive"), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "paragraph-400",
    color: "fg-100"
  }, "$", _appkitUiReactNative.UiUtil.formatNumberToLocalString(toTokenMarketValue, 2))), /*#__PURE__*/React.createElement(_appkitUiReactNative.TokenButton, {
    text: ` ${toTokenAmount} ${toToken?.symbol}`,
    imageUrl: toToken?.logoUri,
    inverse: true,
    disabled: true
  })), /*#__PURE__*/React.createElement(_w3mSwapDetails.SwapDetails, {
    initialOpen: true,
    canClose: false
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
    margin: ['m', '0', 'm', '0']
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Icon, {
    name: "warningCircle",
    size: "sm",
    color: "fg-200",
    style: _styles.default.reviewIcon
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-400",
    color: "fg-200"
  }, "Review transaction carefully")), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Button, {
    variant: "shade",
    style: _styles.default.cancelButton,
    onPress: onCancel
  }, "Cancel"), /*#__PURE__*/React.createElement(_appkitUiReactNative.Button, {
    variant: "fill",
    style: _styles.default.sendButton,
    loading: loading,
    disabled: loading,
    onPress: onSwap
  }, "Swap"))));
}
//# sourceMappingURL=index.js.map