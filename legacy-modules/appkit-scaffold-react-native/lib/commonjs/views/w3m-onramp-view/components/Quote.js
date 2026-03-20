"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.ITEM_HEIGHT = void 0;
exports.Quote = Quote;
var _appkitCommonReactNative = require("@reown/appkit-common-react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _reactNative = require("react-native");
const ITEM_HEIGHT = exports.ITEM_HEIGHT = 64;
function Quote({
  item,
  logoURL,
  onQuotePress,
  selected,
  tagText,
  testID
}) {
  const Theme = (0, _appkitUiReactNative.useTheme)();
  return /*#__PURE__*/React.createElement(_appkitUiReactNative.Pressable, {
    style: [styles.container, selected && {
      borderColor: Theme['accent-100']
    }],
    onPress: () => onQuotePress(item),
    testID: testID
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    justifyContent: "space-between",
    alignItems: "center",
    flexDirection: "row",
    padding: ['s', 's', 's', 's']
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    alignItems: "center"
  }, logoURL ? /*#__PURE__*/React.createElement(_appkitUiReactNative.Image, {
    source: logoURL,
    style: styles.logo
  }) : /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    style: [styles.logo, {
      backgroundColor: Theme['gray-glass-005']
    }],
    justifyContent: "center",
    alignItems: "center"
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "column"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    alignItems: "center",
    margin: ['0', '0', '4xs', '0']
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "paragraph-500",
    style: styles.providerText
  }, item.serviceProvider?.toLowerCase()), tagText && /*#__PURE__*/React.createElement(_appkitUiReactNative.Tag, {
    variant: "main",
    style: styles.tag
  }, tagText)), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "tiny-500"
  }, _appkitCommonReactNative.NumberUtil.roundNumber(item.destinationAmount, 6, 5), ' ', item.destinationCurrencyCode?.split('_')[0]))), selected && /*#__PURE__*/React.createElement(_appkitUiReactNative.Icon, {
    name: "checkmark",
    color: "accent-100"
  })));
}
const styles = _reactNative.StyleSheet.create({
  container: {
    borderRadius: _appkitUiReactNative.BorderRadius.xs,
    borderWidth: 1,
    borderColor: 'transparent',
    height: ITEM_HEIGHT,
    justifyContent: 'center'
  },
  logo: {
    height: 40,
    width: 40,
    borderRadius: _appkitUiReactNative.BorderRadius['3xs'],
    marginRight: _appkitUiReactNative.Spacing.xs
  },
  providerText: {
    textTransform: 'capitalize'
  },
  tag: {
    padding: _appkitUiReactNative.Spacing['3xs'],
    marginLeft: _appkitUiReactNative.Spacing['2xs']
  }
});
//# sourceMappingURL=Quote.js.map