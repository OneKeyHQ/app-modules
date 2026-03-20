"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;
var _reactNative = require("react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _default = exports.default = _reactNative.StyleSheet.create({
  container: {
    minHeight: 250,
    maxHeight: 600
  },
  title: {
    paddingTop: _appkitUiReactNative.Spacing['2xs']
  },
  tokenList: {
    paddingHorizontal: _appkitUiReactNative.Spacing.m
  },
  input: {
    marginHorizontal: _appkitUiReactNative.Spacing.xs
  },
  suggestedList: {
    marginTop: _appkitUiReactNative.Spacing.xs
  },
  suggestedListContent: {
    paddingHorizontal: _appkitUiReactNative.Spacing.s
  },
  suggestedToken: {
    marginRight: _appkitUiReactNative.Spacing.s
  },
  suggestedSeparator: {
    marginVertical: _appkitUiReactNative.Spacing.s
  }
});
//# sourceMappingURL=styles.js.map