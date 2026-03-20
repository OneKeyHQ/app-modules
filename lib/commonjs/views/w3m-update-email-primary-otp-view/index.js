"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.UpdateEmailPrimaryOtpView = UpdateEmailPrimaryOtpView;
var _react = require("react");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _w3mOtpCode = require("../../partials/w3m-otp-code");
function UpdateEmailPrimaryOtpView() {
  const {
    data
  } = _appkitCoreReactNative.RouterController.state;
  const [loading, setLoading] = (0, _react.useState)(false);
  const [error, setError] = (0, _react.useState)('');
  const authProvider = _appkitCoreReactNative.ConnectorController.getAuthConnector()?.provider;
  const onOtpSubmit = async value => {
    if (!authProvider || loading) return;
    setLoading(true);
    setError('');
    try {
      await authProvider.updateEmailPrimaryOtp({
        otp: value
      });
      _appkitCoreReactNative.EventsController.sendEvent({
        type: 'track',
        event: 'EMAIL_VERIFICATION_CODE_PASS'
      });
      _appkitCoreReactNative.RouterController.replace('UpdateEmailSecondaryOtp', data);
    } catch (e) {
      _appkitCoreReactNative.EventsController.sendEvent({
        type: 'track',
        event: 'EMAIL_VERIFICATION_CODE_FAIL'
      });
      const parsedError = _appkitCoreReactNative.CoreHelperUtil.parseError(e);
      if (parsedError?.includes('Invalid Otp')) {
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
    email: data?.email,
    onSubmit: onOtpSubmit,
    onRetry: _appkitCoreReactNative.RouterController.goBack,
    codeExpiry: 10,
    retryLabel: "Something wrong?",
    retryDisabledButtonLabel: "Try again",
    retryButtonLabel: "Try again"
  });
}
//# sourceMappingURL=index.js.map