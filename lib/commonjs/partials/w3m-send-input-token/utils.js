"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.getMaxAmount = getMaxAmount;
exports.getSendValue = getSendValue;
var _appkitCommonReactNative = require("@reown/appkit-common-react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
function getSendValue(token, sendTokenAmount) {
  if (token && sendTokenAmount) {
    const price = token.price;
    const totalValue = price * sendTokenAmount;
    return totalValue ? `$${_appkitUiReactNative.UiUtil.formatNumberToLocalString(totalValue, 2)}` : 'Incorrect value';
  }
  return null;
}
function getMaxAmount(token) {
  if (token) {
    return _appkitCommonReactNative.NumberUtil.roundNumber(Number(token.quantity.numeric), 6, 5);
  }
  return null;
}
//# sourceMappingURL=utils.js.map