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
  title: {
    marginTop: _appkitUiReactNative.Spacing.xl,
    marginVertical: _appkitUiReactNative.Spacing.s
  },
  button: {
    width: 110
  },
  cancelButton: {
    marginRight: _appkitUiReactNative.Spacing.m
  },
  middleIcon: {
    marginHorizontal: _appkitUiReactNative.Spacing.s
  },
  closeButton: {
    alignSelf: 'flex-end',
    right: _appkitUiReactNative.Spacing.xl,
    top: _appkitUiReactNative.Spacing.l,
    position: 'absolute',
    zIndex: 2
  }
});
//# sourceMappingURL=styles.js.map