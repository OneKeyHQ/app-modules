"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.AllWalletList = AllWalletList;
var _valtio = require("valtio");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _UiUtil = require("../../../utils/UiUtil");
var _utils = require("../utils");
function AllWalletList({
  itemStyle,
  onWalletPress,
  isWalletConnectEnabled
}) {
  const {
    installed,
    featured,
    recommended,
    prefetchLoading
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.ApiController.state);
  const {
    recentWallets
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.ConnectionController.state);
  const imageHeaders = _appkitCoreReactNative.ApiController._getApiHeaders();
  const RECENT_COUNT = recentWallets?.length && installed.length ? 1 : recentWallets?.length ?? 0;
  const combinedWallets = [...installed, ...featured, ...recommended];

  // Deduplicate by wallet ID
  const uniqueWallets = Array.from(new Map(combinedWallets.map(wallet => [wallet.id, wallet])).values());
  const list = (0, _utils.filterOutRecentWallets)(recentWallets, uniqueWallets, RECENT_COUNT).slice(0, _UiUtil.UiUtil.TOTAL_VISIBLE_WALLETS - RECENT_COUNT);
  if (!isWalletConnectEnabled || !list?.length) {
    return null;
  }
  return prefetchLoading ? /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(_appkitUiReactNative.ListItemLoader, {
    style: itemStyle
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.ListItemLoader, {
    style: itemStyle
  })) : list.map(wallet => /*#__PURE__*/React.createElement(_appkitUiReactNative.ListWallet, {
    key: wallet?.id,
    imageSrc: _appkitCoreReactNative.AssetUtil.getWalletImage(wallet),
    imageHeaders: imageHeaders,
    name: wallet?.name ?? 'Unknown',
    onPress: () => onWalletPress(wallet),
    style: itemStyle,
    installed: !!installed.find(installedWallet => installedWallet.id === wallet.id)
  }));
}
//# sourceMappingURL=all-wallet-list.js.map