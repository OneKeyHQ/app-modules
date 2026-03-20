"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;
var _reactNative = require("react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _default = exports.default = _reactNative.StyleSheet.create({
  container: {
    paddingBottom: _appkitUiReactNative.Spacing['3xl']
  },
  backButton: {
    alignSelf: 'flex-start'
  },
  imageContainer: {
    marginBottom: _appkitUiReactNative.Spacing.s
  },
  retryButton: {
    marginTop: _appkitUiReactNative.Spacing.m
  },
  retryIcon: {
    transform: [{
      rotateY: '180deg'
    }]
  },
  errorText: {
    marginHorizontal: _appkitUiReactNative.Spacing['4xl']
  }
});
//# sourceMappingURL=styles.js.map