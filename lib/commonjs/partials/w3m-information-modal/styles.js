"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _reactNative = require("react-native");
var _default = exports.default = _reactNative.StyleSheet.create({
  modal: {
    margin: 0,
    justifyContent: 'flex-end'
  },
  content: {
    borderTopLeftRadius: 16,
    borderTopRightRadius: 16,
    alignItems: 'center'
  },
  title: {
    marginTop: _appkitUiReactNative.Spacing.s,
    marginBottom: _appkitUiReactNative.Spacing.xs
  },
  button: {
    marginTop: _appkitUiReactNative.Spacing.xl,
    width: '100%'
  }
});
//# sourceMappingURL=styles.js.map