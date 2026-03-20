"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.ITEM_SIZE = void 0;
exports.PaymentMethod = PaymentMethod;
var _valtio = require("valtio");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _reactNative = require("react-native");
const ITEM_SIZE = exports.ITEM_SIZE = 100;
function PaymentMethod({
  onPress,
  item,
  selected,
  testID
}) {
  const Theme = (0, _appkitUiReactNative.useTheme)();
  const {
    themeMode
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.ThemeController.state);
  const handlePress = () => {
    onPress(item);
  };
  return /*#__PURE__*/React.createElement(_appkitUiReactNative.Pressable, {
    onPress: handlePress,
    bounceScale: 0.96,
    style: styles.container,
    transparent: true,
    pressable: !selected,
    testID: testID
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    style: [styles.logoContainer, {
      backgroundColor: Theme['gray-glass-005']
    }],
    alignItems: "center",
    justifyContent: "center"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Image, {
    source: item.logos[themeMode ?? 'light'],
    style: styles.logo,
    resizeMethod: "resize",
    resizeMode: "contain"
  }), selected && /*#__PURE__*/React.createElement(_appkitUiReactNative.IconBox, {
    icon: "checkmark",
    size: "sm",
    background: true,
    backgroundColor: "accent-100",
    iconColor: "inverse-100",
    style: styles.checkmark,
    testID: "payment-method-checkmark"
  })), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "tiny-400",
    color: "fg-100",
    numberOfLines: 2,
    style: styles.text
  }, item.name));
}
const styles = _reactNative.StyleSheet.create({
  container: {
    height: ITEM_SIZE,
    width: 85,
    alignItems: 'center'
  },
  logoContainer: {
    width: 60,
    height: 60,
    borderRadius: _appkitUiReactNative.BorderRadius.full,
    marginBottom: _appkitUiReactNative.Spacing['4xs']
  },
  logo: {
    width: 26,
    height: 26
  },
  checkmark: {
    borderRadius: _appkitUiReactNative.BorderRadius.full,
    position: 'absolute',
    bottom: 0,
    right: -10
  },
  text: {
    marginTop: _appkitUiReactNative.Spacing.xs,
    paddingHorizontal: _appkitUiReactNative.Spacing['3xs'],
    textAlign: 'center'
  }
});
//# sourceMappingURL=PaymentMethod.js.map