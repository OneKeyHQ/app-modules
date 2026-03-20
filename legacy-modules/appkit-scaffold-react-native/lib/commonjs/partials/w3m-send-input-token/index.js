"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.SendInputToken = SendInputToken;
var _react = require("react");
var _reactNative = require("react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _appkitCommonReactNative = require("@reown/appkit-common-react-native");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _utils = require("./utils");
var _styles = _interopRequireDefault(require("./styles"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
function SendInputToken({
  token,
  sendTokenAmount,
  gasPrice,
  style,
  onTokenPress
}) {
  const Theme = (0, _appkitUiReactNative.useTheme)();
  const valueInputRef = (0, _react.useRef)(null);
  const [inputValue, setInputValue] = (0, _react.useState)(sendTokenAmount?.toString());
  const sendValue = (0, _utils.getSendValue)(token, sendTokenAmount);
  const maxAmount = (0, _utils.getMaxAmount)(token);
  const maxError = token && sendTokenAmount && sendTokenAmount > Number(token.quantity.numeric);
  const onInputChange = value => {
    const formattedValue = value.replace(/,/g, '.');
    if (Number(formattedValue) >= 0 || formattedValue === '') {
      setInputValue(formattedValue);
      _appkitCoreReactNative.SendController.setTokenAmount(Number(formattedValue));
    }
  };
  const onMaxPress = () => {
    if (token && gasPrice) {
      const isNetworkToken = token.address === undefined || Object.values(_appkitCoreReactNative.ConstantsUtil.NATIVE_TOKEN_ADDRESS).some(nativeAddress => token?.address === nativeAddress);
      const numericGas = _appkitCommonReactNative.NumberUtil.bigNumber(gasPrice).shiftedBy(-token.quantity.decimals);
      const maxValue = isNetworkToken ? _appkitCommonReactNative.NumberUtil.bigNumber(token.quantity.numeric).minus(numericGas) : _appkitCommonReactNative.NumberUtil.bigNumber(token.quantity.numeric);
      _appkitCoreReactNative.SendController.setTokenAmount(Number(maxValue.toFixed(20)));
      setInputValue(maxValue.toFixed(20));
      valueInputRef.current?.blur();
    }
  };
  return /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    style: [_styles.default.container, {
      backgroundColor: Theme['gray-glass-005'],
      borderColor: Theme['gray-glass-005']
    }, style],
    justifyContent: "center",
    padding: ['xl', 'l', 'l', 'l']
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between"
  }, /*#__PURE__*/React.createElement(_reactNative.TextInput, {
    ref: valueInputRef,
    placeholder: "0",
    placeholderTextColor: Theme['fg-275'],
    returnKeyType: "done",
    style: [_styles.default.input, {
      color: Theme['fg-100']
    }],
    autoCapitalize: "none",
    autoCorrect: false,
    value: inputValue,
    onChangeText: onInputChange,
    keyboardType: "decimal-pad",
    inputMode: "decimal",
    autoComplete: "off",
    spellCheck: false,
    selectionColor: Theme['accent-100'],
    underlineColorAndroid: "transparent",
    selectTextOnFocus: false,
    numberOfLines: 1,
    autoFocus: !!token
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.TokenButton, {
    imageUrl: token?.iconUrl,
    text: token?.symbol,
    onPress: onTokenPress,
    chevron: true
  })), token && /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    margin: ['3xs', '0', '0', '0']
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-400",
    color: "fg-200",
    style: _styles.default.sendValue,
    numberOfLines: 1
  }, sendValue ?? ''), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-400",
    color: maxError ? 'error-100' : 'fg-200',
    numberOfLines: 1
  }, maxAmount ?? ''), /*#__PURE__*/React.createElement(_appkitUiReactNative.Link, {
    onPress: onMaxPress
  }, "Max"))));
}
//# sourceMappingURL=index.js.map