"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.RecentWalletList = RecentWalletList;
var _valtio = require("valtio");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
function RecentWalletList({
  itemStyle,
  onWalletPress,
  isWalletConnectEnabled
}) {
  const installed = _appkitCoreReactNative.ApiController.state.installed;
  const {
    recentWallets
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.ConnectionController.state);
  const imageHeaders = _appkitCoreReactNative.ApiController._getApiHeaders();
  const RECENT_COUNT = recentWallets?.length && installed.length ? 1 : recentWallets?.length ?? 0;
  if (!isWalletConnectEnabled || !recentWallets?.length) {
    return null;
  }
  return recentWallets.slice(0, RECENT_COUNT).map(wallet => {
    const isInstalled = !!installed.find(installedWallet => installedWallet.id === wallet.id);
    return /*#__PURE__*/React.createElement(_appkitUiReactNative.ListWallet, {
      key: wallet?.id,
      imageSrc: _appkitCoreReactNative.AssetUtil.getWalletImage(wallet),
      imageHeaders: imageHeaders,
      name: wallet?.name ?? 'Unknown',
      onPress: () => onWalletPress(wallet, isInstalled),
      tagLabel: "Recent",
      tagVariant: "shade",
      style: itemStyle,
      installed: isInstalled
    });
  });
}
//# sourceMappingURL=recent-wallet-list.js.map