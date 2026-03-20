"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _reactNative = require("react-native");
var _default = exports.default = _reactNative.StyleSheet.create({
  container: {
    height: 400
  },
  balanceText: {
    fontSize: 40,
    fontWeight: '500'
  },
  actionsContainer: {
    width: '100%',
    marginTop: _appkitUiReactNative.Spacing.s,
    marginBottom: _appkitUiReactNative.Spacing.l
  },
  action: {
    flex: 1,
    height: 52
  },
  actionLeft: {
    marginRight: 8
  },
  actionRight: {
    marginLeft: 8
  },
  actionCenter: {
    marginHorizontal: 8
  },
  tab: {
    width: '100%',
    paddingHorizontal: _appkitUiReactNative.Spacing.s
  },
  tabContainer: {
    flex: 1,
    width: '100%'
  },
  tabContent: {
    paddingHorizontal: _appkitUiReactNative.Spacing.m
  }
});
//# sourceMappingURL=styles.js.map