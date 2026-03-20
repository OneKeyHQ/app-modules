"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;
var _reactNative = require("react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _default = exports.default = _reactNative.StyleSheet.create({
  descriptionText: {
    marginHorizontal: _appkitUiReactNative.Spacing['3xl']
  },
  errorIcon: {
    position: 'absolute',
    bottom: 12,
    right: 20,
    zIndex: 2
  },
  retryButton: {
    marginTop: _appkitUiReactNative.Spacing.xl
  },
  retryIcon: {
    transform: [{
      rotateY: '180deg'
    }]
  },
  text: {
    marginVertical: _appkitUiReactNative.Spacing.xs
  }
});
//# sourceMappingURL=styles.js.map