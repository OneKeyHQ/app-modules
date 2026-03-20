"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.GetWalletView = GetWalletView;
var _reactNative = require("react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _useCustomDimensions = require("../../hooks/useCustomDimensions");
var _styles = _interopRequireDefault(require("./styles"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
function GetWalletView() {
  const {
    padding
  } = (0, _useCustomDimensions.useCustomDimensions)();
  const imageHeaders = _appkitCoreReactNative.ApiController._getApiHeaders();
  const onWalletPress = wallet => {
    const storeUrl = _reactNative.Platform.select({
      ios: wallet.app_store,
      android: wallet.play_store
    }) || wallet.homepage;
    if (storeUrl) {
      _reactNative.Linking.openURL(storeUrl);
    }
  };
  const listTemplate = () => {
    return _appkitCoreReactNative.ApiController.state.recommended.map(wallet => /*#__PURE__*/React.createElement(_appkitUiReactNative.ListWallet, {
      key: wallet.id,
      name: wallet.name,
      imageSrc: _appkitCoreReactNative.AssetUtil.getWalletImage(wallet),
      imageHeaders: imageHeaders,
      onPress: () => onWalletPress(wallet),
      style: _styles.default.item
    }));
  };
  return /*#__PURE__*/React.createElement(_reactNative.ScrollView, {
    bounces: false,
    style: {
      paddingHorizontal: padding
    },
    fadingEdgeLength: 20,
    testID: "get-a-wallet-view"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    padding: ['s', 's', '3xl', 's']
  }, listTemplate(), /*#__PURE__*/React.createElement(_appkitUiReactNative.ListWallet, {
    name: "Explore all",
    showAllWallets: true,
    icon: "externalLink",
    onPress: () => _reactNative.Linking.openURL('https://walletconnect.com/explorer?type=wallet')
  })));
}
//# sourceMappingURL=index.js.map