"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.UpgradeToSmartAccountView = UpgradeToSmartAccountView;
var _reactNative = require("react-native");
var _react = require("react");
var _valtio = require("valtio");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _styles = _interopRequireDefault(require("./styles"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
function UpgradeToSmartAccountView() {
  const {
    address
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.AccountController.state);
  const {
    loading
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.ModalController.state);
  const [initialAddress] = (0, _react.useState)(address);
  const onSwitchAccountType = async () => {
    try {
      _appkitCoreReactNative.ModalController.setLoading(true);
      const accountType = _appkitCoreReactNative.AccountController.state.preferredAccountType === 'eoa' ? 'smartAccount' : 'eoa';
      const provider = _appkitCoreReactNative.ConnectorController.getAuthConnector()?.provider;
      await provider?.setPreferredAccount(accountType);
      _appkitCoreReactNative.EventsController.sendEvent({
        type: 'track',
        event: 'SET_PREFERRED_ACCOUNT_TYPE',
        properties: {
          accountType,
          network: _appkitCoreReactNative.NetworkController.state.caipNetwork?.id || ''
        }
      });
    } catch (error) {
      _appkitCoreReactNative.ModalController.setLoading(false);
      _appkitCoreReactNative.SnackController.showError('Error switching account type');
    }
  };
  const onClose = () => {
    _appkitCoreReactNative.ModalController.close();
    _appkitCoreReactNative.ModalController.setLoading(false);
  };
  const onGoBack = () => {
    _appkitCoreReactNative.RouterController.goBack();
    _appkitCoreReactNative.ModalController.setLoading(false);
  };
  const onLearnMorePress = () => {
    _reactNative.Linking.openURL('https://reown.com/faq');
  };
  (0, _react.useEffect)(() => {
    // Go back if the address has changed
    if (address && initialAddress !== address) {
      _appkitCoreReactNative.RouterController.goBack();
    }
  }, [initialAddress, address]);
  return /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(_appkitUiReactNative.IconLink, {
    icon: "close",
    size: "md",
    onPress: onClose,
    testID: "header-close",
    style: _styles.default.closeButton
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    style: _styles.default.container,
    padding: ['4xl', 'm', '2xl', 'm']
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    alignItems: "center",
    justifyContent: "center",
    flexDirection: "row"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Visual, {
    name: "google"
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.Visual, {
    style: _styles.default.middleIcon,
    name: "pencil"
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.Visual, {
    name: "lightbulb"
  })), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "medium-600",
    color: "fg-100",
    style: _styles.default.title
  }, "Discover Smart Accounts"), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "paragraph-400",
    color: "fg-100",
    center: true
  }, "Access advanced brand new features as username, improved security and a smoother user experience!"), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    margin: ['m', '4xl', 'm', '4xl']
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Button, {
    variant: "accent",
    onPress: onGoBack,
    disabled: loading,
    style: [_styles.default.button, _styles.default.cancelButton]
  }, "Do it later"), /*#__PURE__*/React.createElement(_appkitUiReactNative.Button, {
    onPress: onSwitchAccountType,
    loading: loading,
    style: _styles.default.button
  }, "Continue")), /*#__PURE__*/React.createElement(_appkitUiReactNative.Link, {
    onPress: onLearnMorePress,
    iconRight: "externalLink"
  }, "Learn more")));
}
//# sourceMappingURL=index.js.map