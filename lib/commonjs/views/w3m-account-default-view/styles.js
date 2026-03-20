"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _reactNative = require("react-native");
var _default = exports.default = _reactNative.StyleSheet.create({
  backIcon: {
    alignSelf: 'flex-end',
    position: 'absolute',
    zIndex: 1,
    top: _appkitUiReactNative.Spacing.l,
    left: _appkitUiReactNative.Spacing.xl
  },
  closeIcon: {
    alignSelf: 'flex-end',
    position: 'absolute',
    zIndex: 1,
    top: _appkitUiReactNative.Spacing.l,
    right: _appkitUiReactNative.Spacing.xl
  },
  copyButton: {
    marginLeft: _appkitUiReactNative.Spacing['4xs']
  },
  actionButton: {
    marginBottom: _appkitUiReactNative.Spacing.xs
  },
  upgradeButton: {
    marginBottom: _appkitUiReactNative.Spacing.s
  }
});
//# sourceMappingURL=styles.js.map