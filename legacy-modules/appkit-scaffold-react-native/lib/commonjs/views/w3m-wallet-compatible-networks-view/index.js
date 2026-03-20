"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.WalletCompatibleNetworks = WalletCompatibleNetworks;
var _reactNative = require("react-native");
var _valtio = require("valtio");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _useCustomDimensions = require("../../hooks/useCustomDimensions");
var _styles = _interopRequireDefault(require("./styles"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
function WalletCompatibleNetworks() {
  const {
    padding
  } = (0, _useCustomDimensions.useCustomDimensions)();
  const {
    preferredAccountType
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.AccountController.state);
  const {
    networkImages
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.AssetController.state);
  const isSmartAccount = preferredAccountType === 'smartAccount' && _appkitCoreReactNative.NetworkController.checkIfSmartAccountEnabled();
  const approvedNetworks = isSmartAccount ? _appkitCoreReactNative.NetworkController.getSmartAccountEnabledNetworks() : _appkitCoreReactNative.NetworkController.getApprovedCaipNetworks();
  const imageHeaders = _appkitCoreReactNative.ApiController._getApiHeaders();
  return /*#__PURE__*/React.createElement(_reactNative.ScrollView, {
    bounces: false,
    style: {
      paddingHorizontal: padding
    },
    fadingEdgeLength: 20
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    padding: ['xl', 's', '2xl', 's']
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Banner, {
    icon: "warningCircle",
    text: "You can only receive assets on these networks."
  }), approvedNetworks.map((network, index) => /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    key: network?.id ?? index,
    flexDirection: "row",
    alignItems: "center",
    padding: ['s', 's', 's', 's']
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.NetworkImage, {
    imageSrc: _appkitCoreReactNative.AssetUtil.getNetworkImage(network, networkImages),
    imageHeaders: imageHeaders,
    size: "sm",
    style: _styles.default.image
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    color: "fg-100",
    variant: "paragraph-500"
  }, network?.name ?? 'Unknown Network')))));
}
//# sourceMappingURL=index.js.map