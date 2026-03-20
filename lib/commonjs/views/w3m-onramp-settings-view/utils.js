"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.getModalTitle = exports.getModalSearchPlaceholder = exports.getModalItems = exports.getModalItemKey = exports.getItemHeight = void 0;
var _Country = require("./components/Country");
var _Currency = require("../w3m-onramp-view/components/Currency");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
// -------------------------- Types --------------------------

// -------------------------- Constants --------------------------
const MODAL_TITLES = {
  country: 'Select Country',
  paymentCurrency: 'Select Currency'
};
const MODAL_SEARCH_PLACEHOLDERS = {
  country: 'Search country',
  paymentCurrency: 'Search currency'
};
const ITEM_HEIGHTS = {
  country: _Country.ITEM_HEIGHT,
  paymentCurrency: _Currency.ITEM_HEIGHT
};
const KEY_EXTRACTORS = {
  country: item => item.countryCode,
  paymentCurrency: item => item.currencyCode
};

// -------------------------- Utils --------------------------
const getItemHeight = type => {
  return type ? ITEM_HEIGHTS[type] : 0;
};
exports.getItemHeight = getItemHeight;
const getModalTitle = type => {
  return type ? MODAL_TITLES[type] : undefined;
};
exports.getModalTitle = getModalTitle;
const getModalSearchPlaceholder = type => {
  return type ? MODAL_SEARCH_PLACEHOLDERS[type] : undefined;
};
exports.getModalSearchPlaceholder = getModalSearchPlaceholder;
const searchFilter = (item, searchValue) => {
  const search = searchValue.toLowerCase();
  return item.name.toLowerCase().includes(search) || (item.currencyCode?.toLowerCase().includes(search) ?? false) || (item.countryCode?.toLowerCase().includes(search) ?? false);
};
const getModalItemKey = (type, index, item) => {
  return type ? KEY_EXTRACTORS[type](item) : index.toString();
};
exports.getModalItemKey = getModalItemKey;
const getModalItems = (type, searchValue, filterSelected) => {
  const items = {
    country: () => filterSelected ? _appkitCoreReactNative.OnRampController.state.countries.filter(c => c.countryCode !== _appkitCoreReactNative.OnRampController.state.selectedCountry?.countryCode) : _appkitCoreReactNative.OnRampController.state.countries,
    paymentCurrency: () => filterSelected ? _appkitCoreReactNative.OnRampController.state.paymentCurrencies?.filter(pc => pc.currencyCode !== _appkitCoreReactNative.OnRampController.state.paymentCurrency?.currencyCode) : _appkitCoreReactNative.OnRampController.state.paymentCurrencies
  };
  const result = items[type]?.() || [];
  return searchValue ? result.filter(item => searchFilter(item, searchValue)) : result;
};
exports.getModalItems = getModalItems;
//# sourceMappingURL=utils.js.map