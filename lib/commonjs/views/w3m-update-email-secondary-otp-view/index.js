"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.UpdateEmailSecondaryOtpView = UpdateEmailSecondaryOtpView;
var _valtio = require("valtio");
var _react = require("react");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _w3mOtpCode = require("../../partials/w3m-otp-code");
function UpdateEmailSecondaryOtpView() {
  const {
    data
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.RouterController.state);
  const [loading, setLoading] = (0, _react.useState)(false);
  const [error, setError] = (0, _react.useState)('');
  const authConnector = _appkitCoreReactNative.ConnectorController.getAuthConnector();
  const onOtpSubmit = async value => {
    if (!authConnector) return;
    setLoading(true);
    setError('');
    try {
      const provider = authConnector?.provider;
      await provider.updateEmailSecondaryOtp({
        otp: value
      });
      _appkitCoreReactNative.EventsController.sendEvent({
        type: 'track',
        event: 'EMAIL_VERIFICATION_CODE_PASS'
      });
      _appkitCoreReactNative.EventsController.sendEvent({
        type: 'track',
        event: 'EMAIL_EDIT_COMPLETE'
      });
      _appkitCoreReactNative.RouterController.reset('Account');
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
    email: data?.newEmail,
    onSubmit: onOtpSubmit,
    onRetry: _appkitCoreReactNative.RouterController.goBack,
    codeExpiry: 10,
    retryLabel: "Something wrong?",
    retryDisabledButtonLabel: "Try again",
    retryButtonLabel: "Try again"
  });
}
//# sourceMappingURL=index.js.map