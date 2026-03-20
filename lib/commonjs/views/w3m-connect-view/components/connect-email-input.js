"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.ConnectEmailInput = ConnectEmailInput;
var _valtio = require("valtio");
var _react = require("react");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
function ConnectEmailInput({
  loading
}) {
  const {
    connectors
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.ConnectorController.state);
  const [inputLoading, setInputLoading] = (0, _react.useState)(false);
  const [error, setError] = (0, _react.useState)('');
  const [isValidEmail, setIsValidEmail] = (0, _react.useState)(false);
  const authProvider = connectors.find(c => c.type === 'AUTH')?.provider;
  const onChangeText = value => {
    setIsValidEmail(_appkitCoreReactNative.CoreHelperUtil.isValidEmail(value));
    setError('');
  };
  const onEmailFocus = () => {
    _appkitCoreReactNative.EventsController.sendEvent({
      type: 'track',
      event: 'EMAIL_LOGIN_SELECTED'
    });
  };
  const onEmailSubmit = async email => {
    try {
      if (email.length === 0) return;
      setInputLoading(true);
      const response = await authProvider.connectEmail({
        email
      });
      _appkitCoreReactNative.EventsController.sendEvent({
        type: 'track',
        event: 'EMAIL_SUBMITTED'
      });
      if (response.action === 'VERIFY_DEVICE') {
        _appkitCoreReactNative.RouterController.push('EmailVerifyDevice', {
          email
        });
      } else if (response.action === 'VERIFY_OTP') {
        _appkitCoreReactNative.RouterController.push('EmailVerifyOtp', {
          email
        });
      }
    } catch (e) {
      const parsedError = _appkitCoreReactNative.CoreHelperUtil.parseError(e);
      if (parsedError?.includes('valid email')) {
        setError('Invalid email. Try again.');
      } else {
        _appkitCoreReactNative.SnackController.showError(parsedError);
      }
    } finally {
      setInputLoading(false);
    }
  };
  return /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    padding: ['0', 's', '0', 's']
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.EmailInput, {
    onSubmit: onEmailSubmit,
    onFocus: onEmailFocus,
    loading: inputLoading || loading,
    errorMessage: error,
    onChangeText: onChangeText,
    submitEnabled: isValidEmail
  }));
}
//# sourceMappingURL=connect-email-input.js.map