"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.UpdateEmailWalletView = UpdateEmailWalletView;
var _react = require("react");
var _reactNative = require("react-native");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _useKeyboard = require("../../hooks/useKeyboard");
var _styles = _interopRequireDefault(require("./styles"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
function UpdateEmailWalletView() {
  const {
    data
  } = _appkitCoreReactNative.RouterController.state;
  const [loading, setLoading] = (0, _react.useState)(false);
  const [error, setError] = (0, _react.useState)('');
  const [email, setEmail] = (0, _react.useState)(data?.email || '');
  const [isValidNewEmail, setIsValidNewEmail] = (0, _react.useState)(false);
  const authConnector = _appkitCoreReactNative.ConnectorController.getAuthConnector();
  const {
    keyboardShown,
    keyboardHeight
  } = (0, _useKeyboard.useKeyboard)();
  const paddingBottom = _reactNative.Platform.select({
    android: keyboardShown ? keyboardHeight + _appkitUiReactNative.Spacing.l : _appkitUiReactNative.Spacing.l,
    default: _appkitUiReactNative.Spacing.l
  });
  const onChangeText = value => {
    setIsValidNewEmail(data?.email !== value && _appkitCoreReactNative.CoreHelperUtil.isValidEmail(value));
    setEmail(value);
    setError('');
  };
  const onEmailSubmit = async value => {
    if (!authConnector) return;
    const provider = authConnector.provider;
    setLoading(true);
    setError('');
    try {
      const response = await provider.updateEmail({
        email: value
      });
      _appkitCoreReactNative.EventsController.sendEvent({
        type: 'track',
        event: 'EMAIL_EDIT'
      });
      if (response.action === 'VERIFY_SECONDARY_OTP') {
        _appkitCoreReactNative.RouterController.push('UpdateEmailSecondaryOtp', {
          email: data?.email,
          newEmail: value
        });
      } else {
        _appkitCoreReactNative.RouterController.push('UpdateEmailPrimaryOtp', {
          email: data?.email,
          newEmail: value
        });
      }
    } catch (e) {
      const parsedError = _appkitCoreReactNative.CoreHelperUtil.parseError(e);
      if (parsedError?.includes('Invalid email')) {
        setError('Invalid email. Try again.');
      } else {
        _appkitCoreReactNative.SnackController.showError(parsedError);
      }
    } finally {
      setLoading(false);
    }
  };
  return /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    padding: ['l', 's', '0', 's'],
    style: {
      paddingBottom
    }
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.EmailInput, {
    initialValue: data?.email,
    onSubmit: onEmailSubmit,
    submitEnabled: isValidNewEmail,
    onChangeText: onChangeText,
    loading: loading,
    errorMessage: error,
    style: _styles.default.emailInput,
    autoFocus: true
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    justifyContent: "center",
    alignItems: "center",
    margin: ['0', 'xs', '0', 'xs']
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Button, {
    onPress: _appkitCoreReactNative.RouterController.goBack,
    variant: "shade",
    style: _styles.default.cancelButton
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "paragraph-600",
    color: "fg-100"
  }, "Cancel")), /*#__PURE__*/React.createElement(_appkitUiReactNative.Button, {
    onPress: () => onEmailSubmit(email),
    variant: "fill",
    style: _styles.default.saveButton,
    disabled: loading || !isValidNewEmail
  }, "Save")));
}
//# sourceMappingURL=index.js.map