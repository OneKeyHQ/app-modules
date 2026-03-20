"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.SwapView = SwapView;
var _valtio = require("valtio");
var _react = require("react");
var _reactNative = require("react-native");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _appkitCommonReactNative = require("@reown/appkit-common-react-native");
var _useKeyboard = require("../../hooks/useKeyboard");
var _useCustomDimensions = require("../../hooks/useCustomDimensions");
var _w3mSwapInput = require("../../partials/w3m-swap-input");
var _useDebounceCallback = require("../../hooks/useDebounceCallback");
var _w3mSwapDetails = require("../../partials/w3m-swap-details");
var _styles = _interopRequireDefault(require("./styles"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
function SwapView() {
  const {
    initializing,
    sourceToken,
    toToken,
    sourceTokenAmount,
    toTokenAmount,
    loadingPrices,
    loadingQuote,
    sourceTokenPriceInUSD,
    toTokenPriceInUSD,
    myTokensWithBalance,
    inputError
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.SwapController.state);
  const Theme = (0, _appkitUiReactNative.useTheme)();
  const {
    padding
  } = (0, _useCustomDimensions.useCustomDimensions)();
  const {
    keyboardShown,
    keyboardHeight
  } = (0, _useKeyboard.useKeyboard)();
  const showDetails = !!sourceToken && !!toToken && !inputError;
  const showSwitch = myTokensWithBalance && myTokensWithBalance.findIndex(token => token.address === _appkitCoreReactNative.SwapController.state.toToken?.address) >= 0;
  const paddingBottom = _reactNative.Platform.select({
    android: keyboardShown ? keyboardHeight + _appkitUiReactNative.Spacing['2xl'] : _appkitUiReactNative.Spacing['2xl'],
    default: _appkitUiReactNative.Spacing['2xl']
  });
  const getActionButtonState = () => {
    if (!_appkitCoreReactNative.SwapController.state.sourceToken || !_appkitCoreReactNative.SwapController.state.toToken) {
      return {
        text: 'Select token',
        disabled: true
      };
    }
    if (!_appkitCoreReactNative.SwapController.state.sourceTokenAmount || !_appkitCoreReactNative.SwapController.state.toTokenAmount) {
      return {
        text: 'Enter amount',
        disabled: true
      };
    }
    if (_appkitCoreReactNative.SwapController.state.inputError) {
      return {
        text: _appkitCoreReactNative.SwapController.state.inputError,
        disabled: true
      };
    }
    return {
      text: 'Review swap',
      disabled: false
    };
  };
  const actionState = getActionButtonState();
  const actionLoading = initializing || loadingPrices || loadingQuote;
  const {
    debouncedCallback: onDebouncedSwap
  } = (0, _useDebounceCallback.useDebounceCallback)({
    callback: _appkitCoreReactNative.SwapController.swapTokens.bind(_appkitCoreReactNative.SwapController),
    delay: 400
  });
  const onSourceTokenChange = value => {
    _appkitCoreReactNative.SwapController.setSourceTokenAmount(value);
    onDebouncedSwap();
  };
  const onToTokenChange = value => {
    _appkitCoreReactNative.SwapController.setToTokenAmount(value);
    onDebouncedSwap();
  };
  const onSourceTokenPress = () => {
    _appkitCoreReactNative.RouterController.push('SwapSelectToken', {
      swapTarget: 'sourceToken'
    });
  };
  const onReviewPress = () => {
    _appkitCoreReactNative.EventsController.sendEvent({
      type: 'track',
      event: 'INITIATE_SWAP',
      properties: {
        network: _appkitCoreReactNative.NetworkController.state.caipNetwork?.id || '',
        swapFromToken: _appkitCoreReactNative.SwapController.state.sourceToken?.symbol || '',
        swapToToken: _appkitCoreReactNative.SwapController.state.toToken?.symbol || '',
        swapFromAmount: _appkitCoreReactNative.SwapController.state.sourceTokenAmount || '',
        swapToAmount: _appkitCoreReactNative.SwapController.state.toTokenAmount || '',
        isSmartAccount: _appkitCoreReactNative.AccountController.state.preferredAccountType === 'smartAccount'
      }
    });
    _appkitCoreReactNative.RouterController.push('SwapPreview');
  };
  const onSourceMaxPress = () => {
    const isNetworkToken = _appkitCoreReactNative.SwapController.state.sourceToken?.address === _appkitCoreReactNative.NetworkController.getActiveNetworkTokenAddress();
    const _gasPriceInUSD = _appkitCoreReactNative.SwapController.state.gasPriceInUSD;
    const _sourceTokenPriceInUSD = _appkitCoreReactNative.SwapController.state.sourceTokenPriceInUSD;
    const _balance = _appkitCoreReactNative.SwapController.state.sourceToken?.quantity.numeric;
    if (_balance) {
      if (!_gasPriceInUSD) {
        return _appkitCoreReactNative.SwapController.setSourceTokenAmount(_balance);
      }
      const amountOfTokenGasRequires = _appkitCommonReactNative.NumberUtil.bigNumber(_gasPriceInUSD.toFixed(5)).dividedBy(_sourceTokenPriceInUSD);
      const maxValue = isNetworkToken ? _appkitCommonReactNative.NumberUtil.bigNumber(_balance).minus(amountOfTokenGasRequires) : _appkitCommonReactNative.NumberUtil.bigNumber(_balance);
      _appkitCoreReactNative.SwapController.setSourceTokenAmount(maxValue.isGreaterThan(0) ? maxValue.toFixed(20) : '0');
      _appkitCoreReactNative.SwapController.swapTokens();
    }
  };
  const onToTokenPress = () => {
    _appkitCoreReactNative.RouterController.push('SwapSelectToken', {
      swapTarget: 'toToken'
    });
  };
  const onSwitchPress = () => {
    _appkitCoreReactNative.SwapController.switchTokens();
  };
  const watchTokens = (0, _react.useCallback)(() => {
    _appkitCoreReactNative.SwapController.getNetworkTokenPrice();
    _appkitCoreReactNative.SwapController.getMyTokensWithBalance();
    _appkitCoreReactNative.SwapController.swapTokens();
  }, []);
  (0, _react.useEffect)(() => {
    _appkitCoreReactNative.SwapController.initializeState();
    const interval = setInterval(watchTokens, 10000);
    return () => {
      clearInterval(interval);
    };
  }, [watchTokens]);
  return /*#__PURE__*/React.createElement(_reactNative.ScrollView, {
    style: {
      paddingHorizontal: padding
    },
    bounces: false,
    keyboardShouldPersistTaps: "always"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    padding: "l",
    alignItems: "center",
    justifyContent: "center",
    style: {
      paddingBottom
    }
  }, /*#__PURE__*/React.createElement(_w3mSwapInput.SwapInput, {
    token: sourceToken,
    value: sourceTokenAmount,
    marketValue: parseFloat(sourceTokenAmount) * sourceTokenPriceInUSD,
    style: _styles.default.tokenInput,
    loading: initializing,
    onChange: onSourceTokenChange,
    onTokenPress: onSourceTokenPress,
    onMaxPress: onSourceMaxPress
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    alignItems: "center",
    justifyContent: "center",
    style: _styles.default.bottomInputContainer
  }, /*#__PURE__*/React.createElement(_w3mSwapInput.SwapInput, {
    token: toToken,
    value: toTokenAmount,
    marketValue: _appkitCommonReactNative.NumberUtil.parseLocalStringToNumber(toTokenAmount) * toTokenPriceInUSD,
    style: _styles.default.tokenInput,
    loading: initializing,
    onChange: onToTokenChange,
    onTokenPress: onToTokenPress,
    editable: false
  }), showSwitch && /*#__PURE__*/React.createElement(_appkitUiReactNative.IconLink, {
    icon: "recycleHorizontal",
    size: "lg",
    iconColor: "fg-275",
    background: true,
    backgroundColor: "bg-175",
    pressedColor: "bg-250",
    style: [_styles.default.arrowIcon, {
      borderColor: Theme['bg-100']
    }],
    onPress: onSwitchPress
  })), showDetails && /*#__PURE__*/React.createElement(_w3mSwapDetails.SwapDetails, null), /*#__PURE__*/React.createElement(_appkitUiReactNative.Button, {
    style: _styles.default.actionButton,
    loading: actionLoading,
    disabled: actionState.disabled || actionLoading,
    onPress: onReviewPress
  }, actionState.text)));
}
//# sourceMappingURL=index.js.map