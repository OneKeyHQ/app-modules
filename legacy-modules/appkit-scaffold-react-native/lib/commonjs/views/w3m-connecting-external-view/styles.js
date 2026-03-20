"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _reactNative = require("react-native");
var _default = exports.default = _reactNative.StyleSheet.create({
  container: {
    paddingBottom: _appkitUiReactNative.Spacing['3xl']
  },
  retryButton: {
    marginTop: _appkitUiReactNative.Spacing.m
  },
  retryIcon: {
    transform: [{
      rotateY: '180deg'
    }]
  },
  errorIcon: {
    position: 'absolute',
    bottom: 5,
    right: 5,
    zIndex: 2
  }
});
//# sourceMappingURL=styles.js.map