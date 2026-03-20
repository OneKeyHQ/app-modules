"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.UiUtil = void 0;
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _reactNative = require("react-native");
const UiUtil = exports.UiUtil = {
  TOTAL_VISIBLE_WALLETS: 4,
  createViewTransition: () => {
    const IS_IOS_NEW_ARCH = _reactNative.Platform.OS === 'ios' && global?.nativeFabricUIManager != null;

    // Disable layout animation for new arch on iOS -> https://github.com/facebook/react-native/issues/47617
    if (!IS_IOS_NEW_ARCH) {
      _reactNative.LayoutAnimation.configureNext(_reactNative.LayoutAnimation.create(200, 'easeInEaseOut', 'opacity'));
    }
  },
  storeConnectedWallet: async (wcLinking, pressedWallet) => {
    _appkitCoreReactNative.StorageUtil.setWalletConnectDeepLink(wcLinking);
    if (pressedWallet) {
      const recentWallets = await _appkitCoreReactNative.StorageUtil.addRecentWallet(pressedWallet);
      if (recentWallets) {
        _appkitCoreReactNative.ConnectionController.setRecentWallets(recentWallets);
      }
      const url = _appkitCoreReactNative.AssetUtil.getWalletImage(pressedWallet);
      _appkitCoreReactNative.ConnectionController.setConnectedWalletImageUrl(url);
    }
  }
};
//# sourceMappingURL=UiUtil.js.map