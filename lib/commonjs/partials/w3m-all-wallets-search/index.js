"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.AllWalletsSearch = AllWalletsSearch;
var _react = require("react");
var _reactNative = require("react-native");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _useCustomDimensions = require("../../hooks/useCustomDimensions");
var _w3mPlaceholder = require("../w3m-placeholder");
var _styles = _interopRequireDefault(require("./styles"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
function AllWalletsSearch({
  searchQuery,
  columns,
  itemWidth,
  onItemPress
}) {
  const [loading, setLoading] = (0, _react.useState)(false);
  const [loadingError, setLoadingError] = (0, _react.useState)(false);
  const [prevSearchQuery, setPrevSearchQuery] = (0, _react.useState)('');
  const imageHeaders = _appkitCoreReactNative.ApiController._getApiHeaders();
  const {
    maxWidth,
    padding,
    isLandscape
  } = (0, _useCustomDimensions.useCustomDimensions)();
  const ITEM_HEIGHT = _appkitUiReactNative.CardSelectHeight + _appkitUiReactNative.Spacing.xs * 2;
  const walletTemplate = ({
    item
  }) => {
    const isInstalled = _appkitCoreReactNative.ApiController.state.installed.find(wallet => wallet?.id === item?.id);
    return /*#__PURE__*/React.createElement(_reactNative.View, {
      key: item?.id,
      style: [_styles.default.itemContainer, {
        width: itemWidth
      }]
    }, /*#__PURE__*/React.createElement(_appkitUiReactNative.CardSelect, {
      imageSrc: _appkitCoreReactNative.AssetUtil.getWalletImage(item),
      imageHeaders: imageHeaders,
      name: item?.name ?? 'Unknown',
      onPress: () => onItemPress(item),
      installed: !!isInstalled,
      testID: `wallet-search-item-${item?.id}`
    }));
  };
  const loadingTemplate = items => {
    return /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
      flexDirection: "row",
      flexWrap: "wrap",
      alignSelf: "center",
      padding: ['0', '0', 's', 'xs'],
      style: [_styles.default.container, {
        maxWidth
      }]
    }, Array.from({
      length: items
    }).map((_, index) => /*#__PURE__*/React.createElement(_reactNative.View, {
      key: index,
      style: [_styles.default.itemContainer, {
        width: itemWidth
      }]
    }, /*#__PURE__*/React.createElement(_appkitUiReactNative.CardSelectLoader, null))));
  };
  const emptyTemplate = () => {
    return /*#__PURE__*/React.createElement(_w3mPlaceholder.Placeholder, {
      icon: "walletPlaceholder",
      description: "No results found",
      style: [_styles.default.emptyContainer, isLandscape && _styles.default.emptyLandscape]
    });
  };
  const searchFetch = (0, _react.useCallback)(async () => {
    try {
      setLoading(true);
      setLoadingError(false);
      await _appkitCoreReactNative.ApiController.searchWallet({
        search: searchQuery
      });
      setLoading(false);
    } catch (error) {
      _appkitCoreReactNative.SnackController.showError('Failed to load wallets');
      setLoading(false);
      setLoadingError(true);
    }
  }, [searchQuery]);
  (0, _react.useEffect)(() => {
    if (prevSearchQuery !== searchQuery) {
      setPrevSearchQuery(searchQuery || '');
      searchFetch();
    }
  }, [searchQuery, prevSearchQuery, searchFetch]);
  if (loading) {
    return loadingTemplate(20);
  }
  if (loadingError) {
    return /*#__PURE__*/React.createElement(_w3mPlaceholder.Placeholder, {
      icon: "warningCircle",
      iconColor: "error-100",
      title: "Oops, we couldn\u2019t load the wallets at the moment",
      description: `This might be due to a temporary network issue.\nPlease try reloading to see if that helps.`,
      actionIcon: "refresh",
      actionPress: searchFetch,
      style: _styles.default.placeholderContainer,
      actionTitle: "Retry"
    });
  }
  if (_appkitCoreReactNative.ApiController.state.search.length === 0) {
    return emptyTemplate();
  }
  return /*#__PURE__*/React.createElement(_reactNative.FlatList, {
    key: columns,
    fadingEdgeLength: 20,
    bounces: false,
    numColumns: columns,
    data: _appkitCoreReactNative.ApiController.state.search,
    renderItem: walletTemplate,
    style: _styles.default.container,
    contentContainerStyle: [_styles.default.contentContainer, {
      paddingHorizontal: padding + _appkitUiReactNative.Spacing.xs
    }],
    keyExtractor: item => item.id,
    getItemLayout: (_, index) => ({
      length: ITEM_HEIGHT,
      offset: ITEM_HEIGHT * index,
      index
    })
  });
}
//# sourceMappingURL=index.js.map