"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.WalletReceiveView = WalletReceiveView;
var _valtio = require("valtio");
var _reactNative = require("react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _useCustomDimensions = require("../../hooks/useCustomDimensions");
function WalletReceiveView() {
  const {
    address,
    profileName,
    preferredAccountType
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.AccountController.state);
  const {
    caipNetwork
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.NetworkController.state);
  const {
    networkImages
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.AssetController.state);
  const networkImage = _appkitCoreReactNative.AssetUtil.getNetworkImage(caipNetwork, networkImages);
  const {
    padding
  } = (0, _useCustomDimensions.useCustomDimensions)();
  const canCopy = _appkitCoreReactNative.OptionsController.isClipboardAvailable();
  const isSmartAccount = preferredAccountType === 'smartAccount' && _appkitCoreReactNative.NetworkController.checkIfSmartAccountEnabled();
  const networks = isSmartAccount ? _appkitCoreReactNative.NetworkController.getSmartAccountEnabledNetworks() : _appkitCoreReactNative.NetworkController.getApprovedCaipNetworks();
  const imagesArray = networks.filter(network => network?.imageId).slice(0, 5).map(network => _appkitCoreReactNative.AssetUtil.getNetworkImage(network, _appkitCoreReactNative.AssetController.state.networkImages)).filter(Boolean);
  const label = _appkitUiReactNative.UiUtil.getTruncateString({
    string: profileName ?? address ?? '',
    charsStart: profileName ? 30 : 4,
    charsEnd: profileName ? 0 : 4,
    truncate: profileName ? 'end' : 'middle'
  });
  const onNetworkPress = () => {
    _appkitCoreReactNative.RouterController.push('WalletCompatibleNetworks');
  };
  const onCopyAddress = () => {
    if (canCopy && _appkitCoreReactNative.AccountController.state.address) {
      _appkitCoreReactNative.OptionsController.copyToClipboard(_appkitCoreReactNative.AccountController.state.address);
      _appkitCoreReactNative.SnackController.showSuccess('Address copied');
    }
  };
  if (!address) return;
  return /*#__PURE__*/React.createElement(_reactNative.ScrollView, {
    bounces: false,
    style: {
      paddingHorizontal: padding
    }
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    padding: ['xl', 'xl', '2xl', 'xl'],
    alignItems: "center"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Chip, {
    label: label,
    rightIcon: canCopy ? 'copy' : undefined,
    imageSrc: networkImage,
    variant: "transparent",
    onPress: onCopyAddress
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.QrCode, {
    uri: address,
    size: 232,
    arenaClear: true,
    style: styles.qrContainer
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "paragraph-500",
    color: "fg-100"
  }, canCopy ? 'Copy your address or scan this QR code' : 'Scan this QR code'), /*#__PURE__*/React.createElement(_appkitUiReactNative.CompatibleNetwork, {
    text: "Only receive from networks",
    onPress: onNetworkPress,
    networkImages: imagesArray,
    imageHeaders: _appkitCoreReactNative.ApiController._getApiHeaders(),
    style: styles.networksButton
  })));
}
const styles = _reactNative.StyleSheet.create({
  qrContainer: {
    marginVertical: _appkitUiReactNative.Spacing.xl
  },
  networksButton: {
    marginTop: _appkitUiReactNative.Spacing.l
  }
});
//# sourceMappingURL=index.js.map