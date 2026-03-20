"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.ConnectSocialsView = ConnectSocialsView;
var _react = require("react");
var _valtio = require("valtio");
var _reactNative = require("react-native");
var _appkitCommonReactNative = require("@reown/appkit-common-react-native");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _useCustomDimensions = require("../../hooks/useCustomDimensions");
var _styles = _interopRequireDefault(require("./styles"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
function ConnectSocialsView() {
  const {
    features
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.OptionsController.state);
  const {
    padding
  } = (0, _useCustomDimensions.useCustomDimensions)();
  const socialProviders = features?.socials ?? [];
  const onItemPress = provider => {
    _appkitCoreReactNative.ConnectionController.setSelectedSocialProvider(provider);
    _appkitCoreReactNative.EventsController.sendEvent({
      type: 'track',
      event: 'SOCIAL_LOGIN_STARTED',
      properties: {
        provider
      }
    });
    if (provider === 'farcaster') {
      _appkitCoreReactNative.RouterController.push('ConnectingFarcaster');
    } else {
      _appkitCoreReactNative.RouterController.push('ConnectingSocial');
    }
  };
  (0, _react.useEffect)(() => {
    _appkitCoreReactNative.WebviewController.setConnecting(false);
  }, []);
  return /*#__PURE__*/React.createElement(_reactNative.ScrollView, {
    style: {
      paddingHorizontal: padding
    },
    bounces: false
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    padding: ['xs', 'm', '2xl', 'm']
  }, socialProviders.map(provider => /*#__PURE__*/React.createElement(_appkitUiReactNative.ListSocial, {
    key: provider,
    logo: provider,
    onPress: () => onItemPress(provider),
    style: _styles.default.item
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    style: _styles.default.text,
    color: 'fg-100'
  }, _appkitCommonReactNative.StringUtil.capitalize(provider))))));
}
//# sourceMappingURL=index.js.map