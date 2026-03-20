"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.StoreLink = StoreLink;
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _reactNative = require("react-native");
function StoreLink({
  visible,
  walletName = 'Wallet',
  onPress
}) {
  if (!visible) return null;
  return /*#__PURE__*/React.createElement(_appkitUiReactNative.ActionEntry, {
    style: styles.storeButton
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    numberOfLines: 1,
    variant: "paragraph-500",
    color: "fg-200"
  }, `Don't have ${walletName}?`), /*#__PURE__*/React.createElement(_appkitUiReactNative.Button, {
    variant: "accent",
    iconRight: "chevronRightSmall",
    onPress: onPress,
    size: "sm",
    hitSlop: 20
  }, "Get"));
}
const styles = _reactNative.StyleSheet.create({
  storeButton: {
    justifyContent: 'space-between',
    paddingHorizontal: _appkitUiReactNative.Spacing.l,
    marginHorizontal: _appkitUiReactNative.Spacing.xl,
    marginTop: _appkitUiReactNative.Spacing.l
  }
});
//# sourceMappingURL=StoreLink.js.map