"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;
var _reactNative = require("react-native");
var _default = exports.default = _reactNative.StyleSheet.create({
  header: {
    zIndex: 1,
    alignSelf: 'center',
    ..._reactNative.Platform.select({
      ios: {
        shadowOpacity: 1,
        shadowOffset: {
          width: 0,
          height: 6
        }
      }
    })
  },
  icon: {
    marginLeft: 8,
    borderWidth: _reactNative.StyleSheet.hairlineWidth
  },
  searchBar: {
    flex: 1
  }
});
//# sourceMappingURL=styles.js.map