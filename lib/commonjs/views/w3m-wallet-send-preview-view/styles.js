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
  tokenLogo: {
    height: 32,
    width: 32,
    borderRadius: _appkitUiReactNative.BorderRadius.full,
    marginLeft: _appkitUiReactNative.Spacing.xs
  },
  arrow: {
    marginVertical: _appkitUiReactNative.Spacing.xs
  },
  avatar: {
    marginLeft: _appkitUiReactNative.Spacing.xs
  },
  details: {
    marginTop: _appkitUiReactNative.Spacing['2xl'],
    marginBottom: _appkitUiReactNative.Spacing.s
  },
  reviewIcon: {
    marginRight: _appkitUiReactNative.Spacing['3xs']
  },
  cancelButton: {
    flex: 1
  },
  sendButton: {
    marginLeft: _appkitUiReactNative.Spacing.s,
    flex: 3
  }
});
//# sourceMappingURL=styles.js.map