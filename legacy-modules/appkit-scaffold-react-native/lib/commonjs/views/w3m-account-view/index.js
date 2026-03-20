"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.AccountView = AccountView;
var _valtio = require("valtio");
var _react = require("react");
var _reactNative = require("react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _useCustomDimensions = require("../../hooks/useCustomDimensions");
var _w3mAccountWalletFeatures = require("../../partials/w3m-account-wallet-features");
var _styles = _interopRequireDefault(require("./styles"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
function AccountView() {
  const Theme = (0, _appkitUiReactNative.useTheme)();
  const {
    padding
  } = (0, _useCustomDimensions.useCustomDimensions)();
  const {
    caipNetwork
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.NetworkController.state);
  const {
    networkImages
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.AssetController.state);
  const {
    address,
    profileName,
    profileImage,
    preferredAccountType
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.AccountController.state);
  const showActivate = preferredAccountType === 'eoa' && _appkitCoreReactNative.NetworkController.checkIfSmartAccountEnabled();
  const onProfilePress = () => {
    _appkitCoreReactNative.RouterController.push('AccountDefault');
  };
  const onNetworkPress = () => {
    _appkitCoreReactNative.RouterController.push('Networks');
  };
  const onActivatePress = () => {
    _appkitCoreReactNative.RouterController.push('UpgradeToSmartAccount');
  };
  (0, _react.useEffect)(() => {
    _appkitCoreReactNative.AccountController.fetchTokenBalance();
    _appkitCoreReactNative.SendController.resetSend();
  }, []);
  (0, _react.useEffect)(() => {
    _appkitCoreReactNative.AccountController.fetchTokenBalance();
    const balanceInterval = setInterval(() => {
      _appkitCoreReactNative.AccountController.fetchTokenBalance();
    }, 10000);
    return () => {
      clearInterval(balanceInterval);
    };
  }, []);
  return /*#__PURE__*/React.createElement(_reactNative.ScrollView, {
    bounces: false,
    contentContainerStyle: [_styles.default.contentContainer, {
      paddingHorizontal: padding
    }]
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.NetworkButton, {
    imageSrc: _appkitCoreReactNative.AssetUtil.getNetworkImage(caipNetwork, networkImages),
    imageHeaders: _appkitCoreReactNative.ApiController._getApiHeaders(),
    onPress: onNetworkPress,
    style: _styles.default.networkIcon,
    background: false
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Icon, {
    name: "chevronBottom",
    size: "sm",
    color: "fg-200"
  })), /*#__PURE__*/React.createElement(_appkitUiReactNative.IconLink, {
    icon: "close",
    style: _styles.default.closeIcon,
    onPress: _appkitCoreReactNative.ModalController.close
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    padding: ['3xl', '0', '0', '0'],
    style: [{
      backgroundColor: Theme['bg-100']
    }]
  }, showActivate && /*#__PURE__*/React.createElement(_appkitUiReactNative.Promo, {
    style: _styles.default.promoPill,
    text: "Switch to your smart account",
    onPress: onActivatePress
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.AccountPill, {
    address: address,
    profileName: profileName,
    profileImage: profileImage,
    onPress: onProfilePress,
    style: _styles.default.accountPill
  }), /*#__PURE__*/React.createElement(_w3mAccountWalletFeatures.AccountWalletFeatures, null)));
}
//# sourceMappingURL=index.js.map