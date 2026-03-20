import { ITEM_HEIGHT as COUNTRY_ITEM_HEIGHT } from './components/Country';
import { ITEM_HEIGHT as CURRENCY_ITEM_HEIGHT } from '../w3m-onramp-view/components/Currency';
import { OnRampController } from '@reown/appkit-core-react-native';

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
  country: COUNTRY_ITEM_HEIGHT,
  paymentCurrency: CURRENCY_ITEM_HEIGHT
};
const KEY_EXTRACTORS = {
  country: item => item.countryCode,
  paymentCurrency: item => item.currencyCode
};

// -------------------------- Utils --------------------------
export const getItemHeight = type => {
  return type ? ITEM_HEIGHTS[type] : 0;
};
export const getModalTitle = type => {
  return type ? MODAL_TITLES[type] : undefined;
};
export const getModalSearchPlaceholder = type => {
  return type ? MODAL_SEARCH_PLACEHOLDERS[type] : undefined;
};
const searchFilter = (item, searchValue) => {
  const search = searchValue.toLowerCase();
  return item.name.toLowerCase().includes(search) || (item.currencyCode?.toLowerCase().includes(search) ?? false) || (item.countryCode?.toLowerCase().includes(search) ?? false);
};
export const getModalItemKey = (type, index, item) => {
  return type ? KEY_EXTRACTORS[type](item) : index.toString();
};
export const getModalItems = (type, searchValue, filterSelected) => {
  const items = {
    country: () => filterSelected ? OnRampController.state.countries.filter(c => c.countryCode !== OnRampController.state.selectedCountry?.countryCode) : OnRampController.state.countries,
    paymentCurrency: () => filterSelected ? OnRampController.state.paymentCurrencies?.filter(pc => pc.currencyCode !== OnRampController.state.paymentCurrency?.currencyCode) : OnRampController.state.paymentCurrencies
  };
  const result = items[type]?.() || [];
  return searchValue ? result.filter(item => searchFilter(item, searchValue)) : result;
};
//# sourceMappingURL=utils.js.map