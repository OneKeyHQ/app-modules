"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.AllWalletsList = AllWalletsList;
var _react = require("react");
var _valtio = require("valtio");
var _reactNative = require("react-native");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _styles = _interopRequireDefault(require("./styles"));
var _UiUtil = require("../../utils/UiUtil");
var _useCustomDimensions = require("../../hooks/useCustomDimensions");
var _w3mPlaceholder = require("../w3m-placeholder");
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
function AllWalletsList({
  columns,
  itemWidth,
  onItemPress
}) {
  const [loading, setLoading] = (0, _react.useState)(_appkitCoreReactNative.ApiController.state.wallets.length === 0);
  const [loadingError, setLoadingError] = (0, _react.useState)(false);
  const [pageLoading, setPageLoading] = (0, _react.useState)(false);
  const {
    maxWidth,
    padding
  } = (0, _useCustomDimensions.useCustomDimensions)();
  const {
    installed,
    featured,
    recommended,
    wallets
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.ApiController.state);
  const {
    customWallets
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.OptionsController.state);
  const imageHeaders = _appkitCoreReactNative.ApiController._getApiHeaders();
  const preloadedWallets = installed.length + featured.length + recommended.length;
  const loadingItems = columns - (100 + preloadedWallets) % columns;
  const combinedWallets = [...(customWallets ?? []), ...installed, ...featured, ...recommended, ...wallets];

  // Deduplicate by wallet ID
  const uniqueWallets = Array.from(new Map(combinedWallets.map(wallet => [wallet?.id, wallet])).values()).filter(wallet => wallet?.id); // Filter out any undefined wallets

  const walletList = [...uniqueWallets, ...(pageLoading ? Array.from({
    length: loadingItems
  }) : [])];
  const ITEM_HEIGHT = _appkitUiReactNative.CardSelectHeight + _appkitUiReactNative.Spacing.xs * 2;
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
  const walletTemplate = ({
    item
  }) => {
    const isInstalled = _appkitCoreReactNative.ApiController.state.installed.find(wallet => wallet?.id === item?.id);
    if (!item?.id) {
      return /*#__PURE__*/React.createElement(_reactNative.View, {
        style: [_styles.default.itemContainer, {
          width: itemWidth
        }]
      }, /*#__PURE__*/React.createElement(_appkitUiReactNative.CardSelectLoader, null));
    }
    return /*#__PURE__*/React.createElement(_reactNative.View, {
      style: [_styles.default.itemContainer, {
        width: itemWidth
      }]
    }, /*#__PURE__*/React.createElement(_appkitUiReactNative.CardSelect, {
      imageSrc: _appkitCoreReactNative.AssetUtil.getWalletImage(item),
      imageHeaders: imageHeaders,
      name: item?.name ?? 'Unknown',
      onPress: () => onItemPress(item),
      installed: !!isInstalled
    }));
  };
  const initialFetch = async () => {
    try {
      setLoading(true);
      setLoadingError(false);
      await _appkitCoreReactNative.ApiController.fetchWallets({
        page: 1
      });
      _UiUtil.UiUtil.createViewTransition();
      setLoading(false);
    } catch (error) {
      _appkitCoreReactNative.SnackController.showError('Failed to load wallets');
      setLoading(false);
      setLoadingError(true);
    }
  };
  const fetchNextPage = async () => {
    try {
      if (walletList.length < _appkitCoreReactNative.ApiController.state.count && !pageLoading && !loading && _appkitCoreReactNative.ApiController.state.page > 0) {
        setPageLoading(true);
        await _appkitCoreReactNative.ApiController.fetchWallets({
          page: _appkitCoreReactNative.ApiController.state.page + 1
        });
        setPageLoading(false);
      }
    } catch (error) {
      _appkitCoreReactNative.SnackController.showError('Failed to load more wallets');
      setPageLoading(false);
    }
  };
  (0, _react.useEffect)(() => {
    if (!_appkitCoreReactNative.ApiController.state.wallets.length) {
      initialFetch();
    }
  }, []);
  if (loading) {
    return loadingTemplate(20);
  }
  if (loadingError) {
    return /*#__PURE__*/React.createElement(_w3mPlaceholder.Placeholder, {
      icon: "warningCircle",
      iconColor: "error-100",
      title: "Oops, we couldn't load the wallets at the moment",
      description: `This might be due to a temporary network issue.\nPlease try reloading to see if that helps.`,
      actionIcon: "refresh",
      actionPress: initialFetch,
      actionTitle: "Retry",
      style: _styles.default.placeholderContainer
    });
  }
  return /*#__PURE__*/React.createElement(_reactNative.FlatList, {
    key: columns,
    fadingEdgeLength: 20,
    bounces: false,
    numColumns: columns,
    data: walletList,
    renderItem: walletTemplate,
    style: _styles.default.container,
    contentContainerStyle: [_styles.default.contentContainer, {
      paddingHorizontal: padding + _appkitUiReactNative.Spacing.xs
    }],
    onEndReached: fetchNextPage,
    onEndReachedThreshold: 2,
    keyExtractor: (item, index) => item?.id ?? index,
    getItemLayout: (_, index) => ({
      length: ITEM_HEIGHT,
      offset: ITEM_HEIGHT * index,
      index
    })
  });
}
//# sourceMappingURL=index.js.map