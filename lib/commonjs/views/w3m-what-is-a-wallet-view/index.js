"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.WhatIsAWalletView = WhatIsAWalletView;
var _reactNative = require("react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _useCustomDimensions = require("../../hooks/useCustomDimensions");
var _styles = _interopRequireDefault(require("./styles"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
function WhatIsAWalletView() {
  const {
    padding
  } = (0, _useCustomDimensions.useCustomDimensions)();
  const onGetWalletPress = () => {
    _appkitCoreReactNative.RouterController.push('GetWallet');
    _appkitCoreReactNative.EventsController.sendEvent({
      type: 'track',
      event: 'CLICK_GET_WALLET'
    });
  };
  return /*#__PURE__*/React.createElement(_reactNative.ScrollView, {
    bounces: false,
    fadingEdgeLength: 20,
    style: {
      paddingHorizontal: padding
    },
    testID: "what-is-a-wallet-view"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    alignItems: "center",
    padding: ['xs', '4xl', 'xl', '4xl']
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    padding: ['0', '0', 's', '0']
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Visual, {
    name: "login"
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.Visual, {
    name: "profile",
    style: _styles.default.visual
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.Visual, {
    name: "lock"
  })), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "paragraph-500",
    style: _styles.default.text
  }, "Your web3 account"), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-500",
    color: "fg-200",
    center: true
  }, "Create a wallet with your email or by choosing a wallet provider."), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    padding: ['xl', '0', 's', '0']
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Visual, {
    name: "defi"
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.Visual, {
    name: "nft",
    style: _styles.default.visual
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.Visual, {
    name: "eth"
  })), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "paragraph-500",
    style: _styles.default.text
  }, "The home for your digital assets"), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-500",
    color: "fg-200",
    center: true
  }, "Store, send, and receive digital assets like crypto and NFTs."), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    padding: ['xl', '0', 's', '0']
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Visual, {
    name: "browser"
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.Visual, {
    name: "noun",
    style: _styles.default.visual
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.Visual, {
    name: "dao"
  })), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "paragraph-500",
    style: _styles.default.text
  }, "Your gateway to web3 apps"), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-500",
    color: "fg-200",
    center: true
  }, "Connect your wallet to start exploring DeFi, DAOs, and much more."), /*#__PURE__*/React.createElement(_appkitUiReactNative.Button, {
    size: "sm",
    iconLeft: "walletSmall",
    style: _styles.default.getWalletButton,
    onPress: onGetWalletPress,
    testID: "get-a-wallet-button"
  }, "Get a wallet")));
}
//# sourceMappingURL=index.js.map