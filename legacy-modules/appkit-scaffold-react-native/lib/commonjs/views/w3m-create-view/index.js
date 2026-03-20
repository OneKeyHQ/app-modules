"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.CreateView = CreateView;
var _reactNative = require("react-native");
var _valtio = require("valtio");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _connectEmailInput = require("../w3m-connect-view/components/connect-email-input");
var _socialLoginList = require("../w3m-connect-view/components/social-login-list");
var _walletGuide = require("../w3m-connect-view/components/wallet-guide");
var _useCustomDimensions = require("../../hooks/useCustomDimensions");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _useKeyboard = require("../../hooks/useKeyboard");
function CreateView() {
  const connectors = _appkitCoreReactNative.ConnectorController.state.connectors;
  const {
    authLoading
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.ConnectorController.state);
  const {
    features
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.OptionsController.state);
  const {
    padding
  } = (0, _useCustomDimensions.useCustomDimensions)();
  const {
    keyboardShown,
    keyboardHeight
  } = (0, _useKeyboard.useKeyboard)();
  const isAuthEnabled = connectors.some(c => c.type === 'AUTH');
  const isEmailEnabled = isAuthEnabled && features?.email;
  const isSocialEnabled = isAuthEnabled && features?.socials && features?.socials.length > 0;
  const paddingBottom = _reactNative.Platform.select({
    android: keyboardShown ? keyboardHeight + _appkitUiReactNative.Spacing.xl : _appkitUiReactNative.Spacing.xl,
    default: _appkitUiReactNative.Spacing.xl
  });
  return /*#__PURE__*/React.createElement(_reactNative.ScrollView, {
    style: {
      paddingHorizontal: padding
    },
    bounces: false
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    padding: ['xs', '0', '0', '0'],
    style: {
      paddingBottom
    }
  }, isEmailEnabled && /*#__PURE__*/React.createElement(_connectEmailInput.ConnectEmailInput, {
    loading: authLoading
  }), isSocialEnabled && /*#__PURE__*/React.createElement(_socialLoginList.SocialLoginList, {
    options: features?.socials,
    disabled: authLoading
  }), isAuthEnabled && /*#__PURE__*/React.createElement(_walletGuide.WalletGuide, {
    guide: "explore"
  })));
}
//# sourceMappingURL=index.js.map