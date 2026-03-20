"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.Country = Country;
exports.ITEM_HEIGHT = void 0;
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _reactNative = require("react-native");
var _reactNativeSvg = require("react-native-svg");
const ITEM_HEIGHT = exports.ITEM_HEIGHT = 60;
function Country({
  onPress,
  item,
  selected
}) {
  const handlePress = () => {
    onPress(item);
  };
  return /*#__PURE__*/React.createElement(_appkitUiReactNative.Pressable, {
    onPress: handlePress,
    style: styles.container,
    backgroundColor: "transparent",
    testID: `country-item-${item.countryCode}`
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "flex-start",
    padding: "s"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    style: styles.imageContainer
  }, item.flagImageUrl && _reactNativeSvg.SvgUri && /*#__PURE__*/React.createElement(_reactNativeSvg.SvgUri, {
    uri: item.flagImageUrl,
    width: 36,
    height: 36
  })), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    style: styles.textContainer
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "paragraph-400",
    color: "fg-100",
    numberOfLines: 1,
    ellipsizeMode: "tail"
  }, item.name), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-400",
    color: "fg-150"
  }, item.countryCode)), selected && /*#__PURE__*/React.createElement(_appkitUiReactNative.Icon, {
    name: "checkmark",
    size: "md",
    color: "accent-100",
    style: styles.checkmark
  })));
}
const styles = _reactNative.StyleSheet.create({
  container: {
    borderRadius: _appkitUiReactNative.BorderRadius.s,
    height: ITEM_HEIGHT,
    justifyContent: 'center'
  },
  imageContainer: {
    borderRadius: _appkitUiReactNative.BorderRadius.full,
    overflow: 'hidden',
    marginRight: _appkitUiReactNative.Spacing.xs
  },
  textContainer: {
    flex: 1
  },
  checkmark: {
    marginRight: _appkitUiReactNative.Spacing['2xs']
  }
});
//# sourceMappingURL=Country.js.map