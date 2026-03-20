"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.Currency = Currency;
exports.ITEM_HEIGHT = void 0;
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _reactNative = require("react-native");
const ITEM_HEIGHT = exports.ITEM_HEIGHT = 60;
function Currency({
  onPress,
  item,
  selected,
  title,
  subtitle,
  testID
}) {
  const Theme = (0, _appkitUiReactNative.useTheme)();
  const handlePress = () => {
    onPress(item);
  };
  return /*#__PURE__*/React.createElement(_appkitUiReactNative.Pressable, {
    onPress: handlePress,
    style: styles.container,
    backgroundColor: "transparent",
    testID: testID
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    padding: "xs"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "flex-start"
  }, /*#__PURE__*/React.createElement(_reactNative.Image, {
    source: {
      uri: item.symbolImageUrl
    },
    style: [styles.logo, {
      backgroundColor: Theme['inverse-100']
    }]
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, null, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "paragraph-500",
    color: "fg-100",
    numberOfLines: 1,
    ellipsizeMode: "tail"
  }, title), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-400",
    color: "fg-150"
  }, subtitle))), selected && /*#__PURE__*/React.createElement(_appkitUiReactNative.Icon, {
    name: "checkmark",
    size: "md",
    color: "accent-100",
    style: styles.checkmark
  })));
}
const styles = _reactNative.StyleSheet.create({
  container: {
    justifyContent: 'center',
    height: ITEM_HEIGHT,
    borderRadius: _appkitUiReactNative.BorderRadius.s
  },
  logo: {
    width: 36,
    height: 36,
    borderRadius: _appkitUiReactNative.BorderRadius.full,
    marginRight: _appkitUiReactNative.Spacing.xs
  },
  checkmark: {
    marginRight: _appkitUiReactNative.Spacing['2xs']
  },
  selected: {
    borderWidth: 1
  },
  text: {
    flex: 1
  }
});
//# sourceMappingURL=Currency.js.map