"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.EmailVerifyOtpView = EmailVerifyOtpView;
var _react = require("react");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _useTimeout = _interopRequireDefault(require("../../hooks/useTimeout"));
var _w3mOtpCode = require("../../partials/w3m-otp-code");
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
function EmailVerifyOtpView() {
  const {
    timeLeft,
    startTimer
  } = (0, _useTimeout.default)(0);
  const {
    data
  } = _appkitCoreReactNative.RouterController.state;
  const [loading, setLoading] = (0, _react.useState)(false);
  const [error, setError] = (0, _react.useState)('');
  const authConnector = _appkitCoreReactNative.ConnectorController.getAuthConnector();
  const onOtpResend = async () => {
    try {
      if (!data?.email || !authConnector) return;
      setLoading(true);
      const provider = authConnector?.provider;
      await provider.connectEmail({
        email: data.email
      });
      _appkitCoreReactNative.SnackController.showSuccess('Code resent');
      startTimer(30);
      setLoading(false);
    } catch (e) {
      const parsedError = _appkitCoreReactNative.CoreHelperUtil.parseError(e);
      _appkitCoreReactNative.SnackController.showError(parsedError);
      setLoading(false);
    }
  };
  const onOtpSubmit = async otp => {
    if (!authConnector) return;
    setLoading(true);
    setError('');
    try {
      const provider = authConnector?.provider;
      await provider.connectOtp({
        otp
      });
      _appkitCoreReactNative.EventsController.sendEvent({
        type: 'track',
        event: 'EMAIL_VERIFICATION_CODE_PASS'
      });
      await _appkitCoreReactNative.ConnectionController.connectExternal(authConnector);
      _appkitCoreReactNative.ModalController.close();
      _appkitCoreReactNative.EventsController.sendEvent({
        type: 'track',
        event: 'CONNECT_SUCCESS',
        properties: {
          method: 'email',
          name: authConnector.name || 'Unknown'
        }
      });
    } catch (e) {
      _appkitCoreReactNative.EventsController.sendEvent({
        type: 'track',
        event: 'EMAIL_VERIFICATION_CODE_FAIL'
      });
      const parsedError = _appkitCoreReactNative.CoreHelperUtil.parseError(e);
      if (parsedError?.includes('Invalid code')) {
        setError('Invalid code. Try again.');
      } else {
        _appkitCoreReactNative.SnackController.showError(parsedError);
      }
    }
    setLoading(false);
  };
  return /*#__PURE__*/React.createElement(_w3mOtpCode.OtpCodeView, {
    loading: loading,
    error: error,
    timeLeft: timeLeft,
    email: data?.email,
    onRetry: onOtpResend,
    onSubmit: onOtpSubmit
  });
}
//# sourceMappingURL=index.js.map