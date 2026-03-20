"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.EmailVerifyDeviceView = EmailVerifyDeviceView;
var _valtio = require("valtio");
var _reactNative = require("react-native");
var _react = require("react");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _useTimeout = _interopRequireDefault(require("../../hooks/useTimeout"));
var _styles = _interopRequireDefault(require("./styles"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
function EmailVerifyDeviceView() {
  const Theme = (0, _appkitUiReactNative.useTheme)();
  const {
    connectors
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.ConnectorController.state);
  const {
    data
  } = _appkitCoreReactNative.RouterController.state;
  const {
    timeLeft,
    startTimer
  } = (0, _useTimeout.default)(0);
  const [loading, setLoading] = (0, _react.useState)(false);
  const authProvider = connectors.find(c => c.type === 'AUTH')?.provider;
  const listenForDeviceApproval = async () => {
    if (authProvider && data?.email) {
      try {
        await authProvider.connectDevice();
        _appkitCoreReactNative.EventsController.sendEvent({
          type: 'track',
          event: 'DEVICE_REGISTERED_FOR_EMAIL'
        });
        _appkitCoreReactNative.EventsController.sendEvent({
          type: 'track',
          event: 'EMAIL_VERIFICATION_CODE_SENT'
        });
        _appkitCoreReactNative.RouterController.replace('EmailVerifyOtp', {
          email: data.email
        });
      } catch (error) {
        _appkitCoreReactNative.RouterController.goBack();
      }
    }
  };
  const onResendEmail = async () => {
    try {
      if (!data?.email || !authProvider) return;
      setLoading(true);
      authProvider?.connectEmail({
        email: data.email
      });
      listenForDeviceApproval();
      _appkitCoreReactNative.SnackController.showSuccess('Link resent');
      startTimer(30);
      setLoading(false);
    } catch (e) {
      const parsedError = _appkitCoreReactNative.CoreHelperUtil.parseError(e);
      _appkitCoreReactNative.SnackController.showError(parsedError);
    }
  };
  (0, _react.useEffect)(() => {
    listenForDeviceApproval();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);
  return /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    alignItems: "center",
    padding: ['0', '4xl', '3xl', '4xl']
  }, /*#__PURE__*/React.createElement(_reactNative.View, {
    style: [_styles.default.iconContainer, {
      backgroundColor: Theme['accent-glass-010']
    }]
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Icon, {
    name: "verify",
    size: "lg",
    height: 28,
    width: 28,
    color: "accent-100"
  })), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    center: true,
    variant: "medium-600",
    style: _styles.default.headingText
  }, "Register this device to continue"), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    center: true,
    variant: "paragraph-400"
  }, "Check the instructions sent to", ' '), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "paragraph-500"
  }, data?.email ?? 'your email'), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-400",
    color: "fg-200",
    style: _styles.default.expiryText
  }, "The link expires in 20 minutes"), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    alignItems: "center",
    justifyContent: "center",
    flexDirection: "row"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-400"
  }, "Didn't receive it?"), /*#__PURE__*/React.createElement(_appkitUiReactNative.Link, {
    onPress: onResendEmail,
    disabled: timeLeft > 0 || loading
  }, timeLeft > 0 ? `Resend in ${timeLeft}s` : 'Resend link')));
}
//# sourceMappingURL=index.js.map