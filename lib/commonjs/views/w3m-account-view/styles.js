"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _reactNative = require("react-native");
var _default = exports.default = _reactNative.StyleSheet.create({
  contentContainer: {
    paddingBottom: _reactNative.Platform.select({
      ios: _appkitUiReactNative.Spacing.s
    })
  },
  networkIcon: {
    alignSelf: 'flex-start',
    position: 'absolute',
    zIndex: 1,
    top: _appkitUiReactNative.Spacing.l,
    left: _appkitUiReactNative.Spacing.l
  },
  closeIcon: {
    alignSelf: 'flex-end',
    position: 'absolute',
    zIndex: 1,
    top: _appkitUiReactNative.Spacing.l,
    right: _appkitUiReactNative.Spacing.xl
  },
  accountPill: {
    alignSelf: 'center',
    marginBottom: _appkitUiReactNative.Spacing.s,
    marginHorizontal: _appkitUiReactNative.Spacing.s
  },
  promoPill: {
    marginTop: _appkitUiReactNative.Spacing.xs,
    marginBottom: _appkitUiReactNative.Spacing['2xl'],
    alignSelf: 'center'
  }
});
//# sourceMappingURL=styles.js.map