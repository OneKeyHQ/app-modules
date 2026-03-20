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
  header: {
    marginBottom: _appkitUiReactNative.Spacing.s,
    paddingHorizontal: _appkitUiReactNative.Spacing.m
  },
  container: {
    height: '80%',
    borderTopLeftRadius: _appkitUiReactNative.BorderRadius.l,
    borderTopRightRadius: _appkitUiReactNative.BorderRadius.l,
    paddingTop: _appkitUiReactNative.Spacing.m
  },
  selectedContainer: {
    paddingHorizontal: _appkitUiReactNative.Spacing.m
  },
  listContent: {
    paddingTop: _appkitUiReactNative.Spacing.s,
    paddingHorizontal: _appkitUiReactNative.Spacing.m
  },
  iconPlaceholder: {
    height: 32,
    width: 32
  },
  networkImage: {
    height: 20,
    width: 20,
    borderRadius: _appkitUiReactNative.BorderRadius.full
  },
  searchBar: {
    marginBottom: _appkitUiReactNative.Spacing.s,
    marginHorizontal: _appkitUiReactNative.Spacing.s
  },
  separator: {
    marginTop: _appkitUiReactNative.Spacing.m
  }
});
//# sourceMappingURL=styles.js.map