"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.CustomWalletList = CustomWalletList;
var _valtio = require("valtio");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _utils = require("../utils");
function CustomWalletList({
  itemStyle,
  onWalletPress,
  isWalletConnectEnabled
}) {
  const {
    installed
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.ApiController.state);
  const {
    recentWallets
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.ConnectionController.state);
  const {
    customWallets
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.OptionsController.state);
  const RECENT_COUNT = recentWallets?.length && installed.length ? 1 : recentWallets?.length ?? 0;
  if (!isWalletConnectEnabled || !customWallets?.length) {
    return null;
  }
  const list = (0, _utils.filterOutRecentWallets)(recentWallets, customWallets, RECENT_COUNT);
  return list.map(wallet => /*#__PURE__*/React.createElement(_appkitUiReactNative.ListWallet, {
    key: wallet.id,
    imageSrc: wallet.image_url,
    name: wallet.name,
    onPress: () => onWalletPress(wallet),
    style: itemStyle
  }));
}
//# sourceMappingURL=custom-wallet-list.js.map