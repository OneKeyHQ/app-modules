"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.HelpersUtil = void 0;
var _appkitCommonReactNative = require("@reown/appkit-common-react-native");
const HelpersUtil = exports.HelpersUtil = {
  getCaipTokens(tokens) {
    if (!tokens) {
      return undefined;
    }
    const caipTokens = {};
    Object.entries(tokens).forEach(([id, token]) => {
      caipTokens[`${_appkitCommonReactNative.ConstantsUtil.EIP155}:${id}`] = token;
    });
    return caipTokens;
  }
};
//# sourceMappingURL=HelpersUtil.js.map