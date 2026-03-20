"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;
var _reactNative = require("react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _default = exports.default = _reactNative.StyleSheet.create({
  container: {
    width: '100%',
    borderRadius: 16
  },
  titlePrice: {
    marginLeft: _appkitUiReactNative.Spacing['3xs']
  },
  detailTitle: {
    marginRight: _appkitUiReactNative.Spacing['3xs']
  },
  item: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: _appkitUiReactNative.Spacing.s,
    borderRadius: _appkitUiReactNative.BorderRadius.xxs,
    marginTop: _appkitUiReactNative.Spacing['2xs']
  },
  infoIcon: {
    borderRadius: _appkitUiReactNative.BorderRadius.full
  }
});
//# sourceMappingURL=styles.js.map