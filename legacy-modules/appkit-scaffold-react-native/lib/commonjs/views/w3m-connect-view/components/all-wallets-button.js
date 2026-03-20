"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.AllWalletsButton = AllWalletsButton;
var _valtio = require("valtio");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
function AllWalletsButton({
  itemStyle,
  onPress,
  isWalletConnectEnabled
}) {
  const {
    installed,
    count
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.ApiController.state);
  if (!isWalletConnectEnabled) {
    return null;
  }
  const total = installed.length + count;
  const label = total > 10 ? `${Math.floor(total / 10) * 10}+` : total;
  return /*#__PURE__*/React.createElement(_appkitUiReactNative.ListWallet, {
    name: "All wallets",
    showAllWallets: true,
    tagLabel: String(label),
    tagVariant: "shade",
    onPress: onPress,
    style: itemStyle,
    testID: "all-wallets"
  });
}
//# sourceMappingURL=all-wallets-button.js.map