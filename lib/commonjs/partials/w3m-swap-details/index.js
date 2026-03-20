"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.SwapDetails = SwapDetails;
var _valtio = require("valtio");
var _react = require("react");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _appkitCommonReactNative = require("@reown/appkit-common-react-native");
var _w3mInformationModal = require("../w3m-information-modal");
var _styles = _interopRequireDefault(require("./styles"));
var _utils = require("./utils");
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
// -- Constants ----------------------------------------- //
const slippageRate = _appkitCoreReactNative.ConstantsUtil.CONVERT_SLIPPAGE_TOLERANCE;
function SwapDetails({
  initialOpen,
  canClose
}) {
  const Theme = (0, _appkitUiReactNative.useTheme)();
  const {
    maxSlippage = 0,
    sourceToken,
    toToken,
    gasPriceInUSD = 0,
    priceImpact,
    toTokenAmount
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.SwapController.state);
  const [modalData, setModalData] = (0, _react.useState)();
  const toTokenSwappedAmount = _appkitCoreReactNative.SwapController.state.sourceTokenPriceInUSD && _appkitCoreReactNative.SwapController.state.toTokenPriceInUSD ? 1 / _appkitCoreReactNative.SwapController.state.toTokenPriceInUSD * _appkitCoreReactNative.SwapController.state.sourceTokenPriceInUSD : 0;
  const renderTitle = () => /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    alignItems: "flex-start"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-400",
    color: "fg-100"
  }, "1 ", _appkitCoreReactNative.SwapController.state.sourceToken?.symbol, " = ", '', _appkitUiReactNative.UiUtil.formatNumberToLocalString(toTokenSwappedAmount, 3), ' ', _appkitCoreReactNative.SwapController.state.toToken?.symbol), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-400",
    color: "fg-200",
    style: _styles.default.titlePrice
  }, "~$", _appkitUiReactNative.UiUtil.formatNumberToLocalString(_appkitCoreReactNative.SwapController.state.sourceTokenPriceInUSD)));
  const minimumReceive = _appkitCommonReactNative.NumberUtil.parseLocalStringToNumber(toTokenAmount) - maxSlippage;
  const providerFee = _appkitCoreReactNative.SwapController.getProviderFeePrice();
  const onPriceImpactPress = () => {
    setModalData((0, _utils.getModalData)('priceImpact'));
  };
  const onSlippagePress = () => {
    const minimumString = _appkitUiReactNative.UiUtil.formatNumberToLocalString(minimumReceive, minimumReceive < 1 ? 8 : 2);
    setModalData((0, _utils.getModalData)('slippage', {
      minimumReceive: minimumString,
      toTokenSymbol: _appkitCoreReactNative.SwapController.state.toToken?.symbol
    }));
  };
  const onNetworkCostPress = () => {
    setModalData((0, _utils.getModalData)('networkCost', {
      networkSymbol: _appkitCoreReactNative.SwapController.state.networkTokenSymbol,
      networkName: _appkitCoreReactNative.NetworkController.state.caipNetwork?.name
    }));
  };
  return /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(_appkitUiReactNative.Toggle, {
    title: renderTitle(),
    style: [_styles.default.container, {
      backgroundColor: Theme['gray-glass-005']
    }],
    initialOpen: initialOpen,
    canClose: canClose
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    style: [_styles.default.item, {
      backgroundColor: Theme['gray-glass-002']
    }]
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    alignItems: "center"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-400",
    color: "fg-150",
    style: _styles.default.detailTitle
  }, "Network cost"), /*#__PURE__*/React.createElement(_appkitUiReactNative.Pressable, {
    onPress: onNetworkCostPress,
    style: _styles.default.infoIcon,
    hitSlop: 10
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Icon, {
    name: "infoCircle",
    size: "sm",
    color: "fg-150"
  }))), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-400",
    color: "fg-100"
  }, "$", _appkitUiReactNative.UiUtil.formatNumberToLocalString(gasPriceInUSD, gasPriceInUSD < 1 ? 8 : 2))), !!priceImpact && /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    style: [_styles.default.item, {
      backgroundColor: Theme['gray-glass-002']
    }]
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    alignItems: "center"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-400",
    color: "fg-150",
    style: _styles.default.detailTitle
  }, "Price impact"), /*#__PURE__*/React.createElement(_appkitUiReactNative.Pressable, {
    onPress: onPriceImpactPress,
    style: _styles.default.infoIcon,
    hitSlop: 10
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Icon, {
    name: "infoCircle",
    size: "sm",
    color: "fg-150"
  }))), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-400",
    color: "fg-100"
  }, "~", _appkitUiReactNative.UiUtil.formatNumberToLocalString(priceImpact, 3), "%")), maxSlippage !== undefined && maxSlippage > 0 && !!sourceToken?.symbol && /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    style: [_styles.default.item, {
      backgroundColor: Theme['gray-glass-002']
    }]
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    alignItems: "center"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-400",
    color: "fg-150",
    style: _styles.default.detailTitle
  }, "Max. slippage"), /*#__PURE__*/React.createElement(_appkitUiReactNative.Pressable, {
    onPress: onSlippagePress,
    style: _styles.default.infoIcon,
    hitSlop: 10
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Icon, {
    name: "infoCircle",
    size: "sm",
    color: "fg-150"
  }))), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-400",
    color: "fg-200"
  }, _appkitUiReactNative.UiUtil.formatNumberToLocalString(maxSlippage, 6), " ", toToken?.symbol, ' ', /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-400",
    color: "fg-100"
  }, slippageRate, "%"))), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    style: [_styles.default.item, {
      backgroundColor: Theme['gray-glass-002']
    }]
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-400",
    color: "fg-150",
    style: _styles.default.detailTitle
  }, "Included provider fee"), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-400",
    color: "fg-100"
  }, "$", _appkitUiReactNative.UiUtil.formatNumberToLocalString(providerFee, providerFee < 1 ? 8 : 2)))), /*#__PURE__*/React.createElement(_w3mInformationModal.InformationModal, {
    iconName: "infoCircle",
    title: modalData?.title,
    description: modalData?.description,
    visible: !!modalData,
    onClose: () => setModalData(undefined)
  }));
}
//# sourceMappingURL=index.js.map