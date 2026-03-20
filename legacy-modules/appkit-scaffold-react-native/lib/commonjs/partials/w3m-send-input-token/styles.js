"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _reactNative = require("react-native");
var _default = exports.default = _reactNative.StyleSheet.create({
  container: {
    height: 100,
    width: '100%',
    borderRadius: _appkitUiReactNative.BorderRadius.s,
    borderWidth: _reactNative.StyleSheet.hairlineWidth
  },
  input: {
    fontSize: 32,
    flex: 1,
    marginRight: _appkitUiReactNative.Spacing.xs
  },
  sendValue: {
    flex: 1,
    marginRight: _appkitUiReactNative.Spacing.xs
  }
});
//# sourceMappingURL=styles.js.map