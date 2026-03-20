"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.getQuotesDebounced = exports.getPurchaseCurrencies = void 0;
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
// -------------------------- Utils --------------------------
const getPurchaseCurrencies = (searchValue, filterSelected) => {
  const networkId = _appkitCoreReactNative.NetworkController.state.caipNetwork?.id?.split(':')[1];
  let networkTokens = _appkitCoreReactNative.OnRampController.state.purchaseCurrencies?.filter(c => c.chainId === networkId) ?? [];
  if (filterSelected) {
    networkTokens = networkTokens?.filter(c => c.currencyCode !== _appkitCoreReactNative.OnRampController.state.purchaseCurrency?.currencyCode);
  }
  return searchValue ? networkTokens.filter(item => item.name.toLowerCase().includes(searchValue) || item.currencyCode.toLowerCase()?.split('_')?.[0]?.includes(searchValue)) : networkTokens;
};
exports.getPurchaseCurrencies = getPurchaseCurrencies;
const getQuotesDebounced = exports.getQuotesDebounced = _appkitCoreReactNative.CoreHelperUtil.debounce(function () {
  _appkitCoreReactNative.OnRampController.getQuotes();
}, 500);
//# sourceMappingURL=utils.js.map