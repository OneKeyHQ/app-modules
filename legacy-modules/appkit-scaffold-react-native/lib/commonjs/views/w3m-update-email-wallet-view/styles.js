"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _reactNative = require("react-native");
var _default = exports.default = _reactNative.StyleSheet.create({
  container: {
    justifyContent: 'center',
    alignItems: 'center'
  },
  emailInput: {
    marginBottom: _appkitUiReactNative.Spacing.s
  },
  cancelButton: {
    flex: 1,
    height: 48,
    marginRight: _appkitUiReactNative.Spacing['2xs'],
    borderRadius: _appkitUiReactNative.BorderRadius.xs
  },
  saveButton: {
    flex: 1,
    height: 48,
    marginLeft: _appkitUiReactNative.Spacing['2xs'],
    borderRadius: _appkitUiReactNative.BorderRadius.xs
  }
});
//# sourceMappingURL=styles.js.map