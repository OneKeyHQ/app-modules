import { SwapController } from '@reown/appkit-core-react-native';
export function filterTokens(tokens, searchValue) {
  if (!searchValue) {
    return tokens;
  }
  return tokens.filter(token => token.name.toLowerCase().includes(searchValue.toLowerCase()) || token.symbol.toLowerCase().includes(searchValue.toLowerCase()));
}
export function createSections(isSourceToken, searchValue) {
  const myTokensFiltered = filterTokens(SwapController.state.myTokensWithBalance ?? [], searchValue);
  const popularFiltered = isSourceToken ? [] : filterTokens(SwapController.getFilteredPopularTokens() ?? [], searchValue);
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