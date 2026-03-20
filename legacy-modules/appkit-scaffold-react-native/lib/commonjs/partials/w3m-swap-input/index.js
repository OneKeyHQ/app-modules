"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.SwapInput = SwapInput;
var _react = require("react");
var _reactNative = require("react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _styles = _interopRequireDefault(require("./styles"));
var _appkitCommonReactNative = require("@reown/appkit-common-react-native");
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
const MINIMUM_USD_VALUE_TO_CONVERT = 0.00005;
function SwapInput({
  token,
  value,
  style,
  loading,
  onTokenPress,
  onMaxPress,
  onChange,
  marketValue,
  editable,
  autoFocus
}) {
  const Theme = (0, _appkitUiReactNative.useTheme)();
  const valueInputRef = (0, _react.useRef)(null);
  const isMarketValueGreaterThanZero = !!marketValue && _appkitCommonReactNative.NumberUtil.bigNumber(marketValue).isGreaterThan('0');
  const maxAmount = _appkitUiReactNative.UiUtil.formatNumberToLocalString(token?.quantity.numeric, 3);
  const maxError = Number(value) > Number(token?.quantity.numeric);
  const showMax = onMaxPress && !!token?.quantity.numeric && _appkitCommonReactNative.NumberUtil.multiply(token?.quantity.numeric, token?.price).isGreaterThan(MINIMUM_USD_VALUE_TO_CONVERT);
  const handleInputChange = _value => {
    const formattedValue = _value.replace(/,/g, '.');
    if (Number(formattedValue) >= 0 || formattedValue === '') {
      onChange?.(formattedValue);
    }
  };
  const handleMaxPress = () => {
    if (valueInputRef.current) {
      valueInputRef.current.blur();
    }
    onMaxPress?.();
  };
  return /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    style: [_styles.default.container, {
      backgroundColor: Theme['gray-glass-005'],
      borderColor: Theme['gray-glass-005']
    }, style],
    justifyContent: "center",
    padding: ['xl', 'l', 'l', 'l']
  }, loading ? /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Shimmer, {
    height: 36,
    width: 80,
    borderRadius: 12
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.Shimmer, {
    height: 36,
    width: 80,
    borderRadius: 18
  })) : /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
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
    value: value,
    onChangeText: handleInputChange,
    keyboardType: "decimal-pad",
    inputMode: "decimal",
    autoComplete: "off",
    spellCheck: false,
    selectionColor: Theme['accent-100'],
    underlineColorAndroid: "transparent",
    selectTextOnFocus: false,
    numberOfLines: 1,
    editable: editable,
    autoFocus: autoFocus
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.TokenButton, {
    text: token?.symbol,
    imageUrl: token?.logoUri,
    onPress: onTokenPress,
    chevron: true
  })), (showMax || isMarketValueGreaterThanZero) && /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    margin: ['3xs', '0', '0', '0']
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-400",
    color: "fg-200",
    numberOfLines: 1
  }, isMarketValueGreaterThanZero ? `~$${_appkitUiReactNative.UiUtil.formatNumberToLocalString(marketValue, 2)}` : ''), showMax && /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-400",
    color: maxError ? 'error-100' : 'fg-200',
    numberOfLines: 1
  }, showMax ? maxAmount : ''), /*#__PURE__*/React.createElement(_appkitUiReactNative.Link, {
    onPress: handleMaxPress
  }, "Max")))));
}
//# sourceMappingURL=index.js.map