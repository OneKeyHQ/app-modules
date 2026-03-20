"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.createSections = createSections;
exports.filterTokens = filterTokens;
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
function filterTokens(tokens, searchValue) {
  if (!searchValue) {
    return tokens;
  }
  return tokens.filter(token => token.name.toLowerCase().includes(searchValue.toLowerCase()) || token.symbol.toLowerCase().includes(searchValue.toLowerCase()));
}
function createSections(isSourceToken, searchValue) {
  const myTokensFiltered = filterTokens(_appkitCoreReactNative.SwapController.state.myTokensWithBalance ?? [], searchValue);
  const popularFiltered = isSourceToken ? [] : filterTokens(_appkitCoreReactNative.SwapController.getFilteredPopularTokens() ?? [], searchValue);
  const sections = [];
  if (myTokensFiltered.length > 0) {
    sections.push({
      title: 'Your tokens',
      data: myTokensFiltered
    });
  }
  if (popularFiltered.length > 0) {
    sections.push({
      title: 'Popular tokens',
      data: popularFiltered
    });
  }
  return sections;
}
//# sourceMappingURL=utils.js.map