"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.OtpCodeView = OtpCodeView;
var _reactNative = require("react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _useKeyboard = require("../../hooks/useKeyboard");
var _styles = _interopRequireDefault(require("./styles"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
function OtpCodeView({
  onCodeChange,
  onSubmit,
  onRetry,
  error,
  loading,
  email,
  timeLeft = 0,
  codeExpiry = 20,
  retryLabel = "Didn't receive it?",
  retryDisabledButtonLabel = 'Resend',
  retryButtonLabel = 'Resend code'
}) {
  const {
    keyboardShown,
    keyboardHeight
  } = (0, _useKeyboard.useKeyboard)();
  const paddingBottom = _reactNative.Platform.select({
    android: keyboardShown ? keyboardHeight + _appkitUiReactNative.Spacing.l : _appkitUiReactNative.Spacing.l,
    default: _appkitUiReactNative.Spacing.l
  });
  const handleCodeChange = code => {
    onCodeChange?.(code);
    if (code.length === 6) {
      onSubmit?.(code);
    }
  };
  return /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    padding: ['l', 'l', '3xl', 'l'],
    alignItems: "center",
    style: {
      paddingBottom
    }
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    center: true,
    variant: "paragraph-400"
  }, "Enter the code we sent to", ' '), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "paragraph-500"
  }, email ?? 'your email'), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    style: _styles.default.expiryText,
    variant: "small-400",
    color: "fg-200"
  }, `The code expires in ${codeExpiry} minutes`), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    justifyContent: "center",
    style: _styles.default.otpContainer
  }, loading ? /*#__PURE__*/React.createElement(_appkitUiReactNative.LoadingSpinner, {
    size: "xl"
  }) : /*#__PURE__*/React.createElement(_appkitUiReactNative.Otp, {
    length: 6,
    onChangeText: handleCodeChange,
    autoFocus: true
  })), error && /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-400",
    color: "error-100",
    style: _styles.default.errorText
  }, error), !loading && /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    alignItems: "center",
    flexDirection: "row",
    margin: ['s', '0', '0', '0']
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-400",
    color: "fg-200"
  }, retryLabel), /*#__PURE__*/React.createElement(_appkitUiReactNative.Link, {
    onPress: onRetry,
    disabled: timeLeft > 0 || loading
  }, timeLeft > 0 ? `${retryDisabledButtonLabel} in ${timeLeft}s` : retryButtonLabel)));
}
//# sourceMappingURL=index.js.map