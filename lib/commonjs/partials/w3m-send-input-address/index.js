"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.SendInputAddress = SendInputAddress;
var _react = require("react");
var _reactNative = require("react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _useDebounceCallback = require("../../hooks/useDebounceCallback");
var _styles = _interopRequireDefault(require("./styles"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
function SendInputAddress({
  value
}) {
  const Theme = (0, _appkitUiReactNative.useTheme)();
  const [inputValue, setInputValue] = (0, _react.useState)(value);
  const onSearch = async search => {
    _appkitCoreReactNative.SendController.setLoading(true);
    const address = await _appkitCoreReactNative.ConnectionController.getEnsAddress(search);
    _appkitCoreReactNative.SendController.setLoading(false);
    if (address) {
      _appkitCoreReactNative.SendController.setReceiverProfileName(search);
      _appkitCoreReactNative.SendController.setReceiverAddress(address);
      const avatar = await _appkitCoreReactNative.ConnectionController.getEnsAvatar(search);
      _appkitCoreReactNative.SendController.setReceiverProfileImageUrl(avatar || undefined);
    } else {
      _appkitCoreReactNative.SendController.setReceiverAddress(search);
      _appkitCoreReactNative.SendController.setReceiverProfileName(undefined);
      _appkitCoreReactNative.SendController.setReceiverProfileImageUrl(undefined);
    }
  };
  const {
    debouncedCallback: onDebounceSearch
  } = (0, _useDebounceCallback.useDebounceCallback)({
    callback: onSearch,
    delay: 800
  });
  const onInputChange = address => {
    setInputValue(address);
    _appkitCoreReactNative.SendController.setReceiverAddress(address);
    onDebounceSearch(address);
  };
  return /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    style: [_styles.default.container, {
      backgroundColor: Theme['gray-glass-005'],
      borderColor: Theme['gray-glass-005']
    }],
    justifyContent: "center",
    padding: ['xl', 'l', 'l', 'l']
  }, /*#__PURE__*/React.createElement(_reactNative.TextInput, {
    placeholder: "Type or paste address",
    placeholderTextColor: Theme['fg-275'],
    returnKeyType: "done",
    style: [_styles.default.input, {
      color: Theme['fg-100']
    }],
    autoCapitalize: "none",
    autoCorrect: false,
    value: inputValue,
    onChangeText: onInputChange,
    keyboardType: "default",
    inputMode: "text",
    autoComplete: "off",
    spellCheck: false,
    selectionColor: Theme['accent-100'],
    underlineColorAndroid: "transparent",
    selectTextOnFocus: false,
    returnKeyLabel: "Done"
  }));
}
//# sourceMappingURL=index.js.map