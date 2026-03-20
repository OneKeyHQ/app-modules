"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _reactNative = require("react-native");
var _default = exports.default = _reactNative.StyleSheet.create({
  container: {
    height: '100%'
  },
  contentContainer: {
    paddingBottom: _appkitUiReactNative.Spacing['2xl']
  },
  itemContainer: {
    alignItems: 'center',
    justifyContent: 'center',
    marginVertical: _appkitUiReactNative.Spacing.xs
  },
  pageLoader: {
    marginTop: _appkitUiReactNative.Spacing.xl
  },
  errorContainer: {
    height: '90%'
  },
  placeholderContainer: {
    flex: 0,
    height: '90%'
  }
});
//# sourceMappingURL=styles.js.map