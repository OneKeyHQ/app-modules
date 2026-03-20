"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.AccountActivity = AccountActivity;
var _valtio = require("valtio");
var _react = require("react");
var _reactNative = require("react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _w3mPlaceholder = require("../w3m-placeholder");
var _utils = require("./utils");
var _styles = _interopRequireDefault(require("./styles"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
function AccountActivity({
  style
}) {
  const Theme = (0, _appkitUiReactNative.useTheme)();
  const [refreshing, setRefreshing] = (0, _react.useState)(false);
  const [initialLoad, setInitialLoad] = (0, _react.useState)(true);
  const {
    loading,
    transactions,
    next
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.TransactionsController.state);
  const {
    caipNetwork
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.NetworkController.state);
  const {
    networkImages
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.AssetController.state);
  const networkImage = _appkitCoreReactNative.AssetUtil.getNetworkImage(caipNetwork, networkImages);
  const handleLoadMore = () => {
    _appkitCoreReactNative.TransactionsController.fetchTransactions(_appkitCoreReactNative.AccountController.state.address);
    _appkitCoreReactNative.EventsController.sendEvent({
      type: 'track',
      event: 'LOAD_MORE_TRANSACTIONS',
      properties: {
        address: _appkitCoreReactNative.AccountController.state.address,
        projectId: _appkitCoreReactNative.OptionsController.state.projectId,
        cursor: _appkitCoreReactNative.TransactionsController.state.next,
        isSmartAccount: _appkitCoreReactNative.AccountController.state.preferredAccountType === 'smartAccount'
      }
    });
  };
  const onRefresh = (0, _react.useCallback)(async () => {
    setRefreshing(true);
    await _appkitCoreReactNative.TransactionsController.fetchTransactions(_appkitCoreReactNative.AccountController.state.address, true);
    setRefreshing(false);
  }, []);
  const transactionsByYear = (0, _react.useMemo)(() => {
    return _appkitCoreReactNative.TransactionsController.getTransactionsByYearAndMonth(transactions);
  }, [transactions]);
  (0, _react.useEffect)(() => {
    if (!_appkitCoreReactNative.TransactionsController.state.transactions.length) {
      _appkitCoreReactNative.TransactionsController.fetchTransactions(_appkitCoreReactNative.AccountController.state.address, true);
    }
    // Set initial load to false after first fetch
    const timer = setTimeout(() => setInitialLoad(false), 100);
    return () => clearTimeout(timer);
  }, []);

  // Show loading spinner during initial load or when loading with no transactions
  if ((initialLoad || loading) && !transactions.length) {
    return /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
      style: [_styles.default.placeholder, style],
      alignItems: "center",
      justifyContent: "center"
    }, /*#__PURE__*/React.createElement(_appkitUiReactNative.LoadingSpinner, null));
  }

  // Only show placeholder when we're not in initial load or loading state
  if (!Object.keys(transactionsByYear).length && !loading && !initialLoad) {
    return /*#__PURE__*/React.createElement(_w3mPlaceholder.Placeholder, {
      icon: "swapHorizontal",
      title: "No activity yet",
      description: "Your next transactions will appear here",
      style: [_styles.default.placeholder, style]
    });
  }
  return /*#__PURE__*/React.createElement(_reactNative.ScrollView, {
    style: [_styles.default.container, style],
    fadingEdgeLength: 20,
    contentContainerStyle: [_styles.default.contentContainer],
    refreshControl: /*#__PURE__*/React.createElement(_reactNative.RefreshControl, {
      refreshing: refreshing,
      onRefresh: onRefresh,
      tintColor: Theme['accent-100'],
      colors: [Theme['accent-100']]
    })
  }, Object.keys(transactionsByYear).reverse().map(year => /*#__PURE__*/React.createElement(_reactNative.View, {
    key: year
  }, Object.keys(transactionsByYear[year] || {}).reverse().map(month => /*#__PURE__*/React.createElement(_reactNative.View, {
    key: month
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "paragraph-400",
    color: "fg-200",
    style: _styles.default.separatorText
  }, _appkitUiReactNative.TransactionUtil.getTransactionGroupTitle(year, month)), transactionsByYear[year]?.[month]?.map(transaction => {
    const {
      date,
      type,
      descriptions,
      status,
      images,
      isAllNFT,
      transfers
    } = (0, _utils.getTransactionListItemProps)(transaction);
    const hasMultipleTransfers = transfers?.length > 2;

    // Show only the first transfer
    if (hasMultipleTransfers) {
      const description = _appkitUiReactNative.TransactionUtil.getTransferDescription(transfers[0]);
      return /*#__PURE__*/React.createElement(_appkitUiReactNative.ListTransaction, {
        key: `${transaction.id}@${description}`,
        date: date,
        type: type,
        descriptions: [description],
        status: status,
        images: [images[0]],
        networkSrc: networkImage,
        style: _styles.default.transactionItem,
        isAllNFT: isAllNFT
      });
    }
    return /*#__PURE__*/React.createElement(_appkitUiReactNative.ListTransaction, {
      key: transaction.id,
      date: date,
      type: type,
      descriptions: descriptions,
      status: status,
      images: images,
      networkSrc: networkImage,
      style: _styles.default.transactionItem,
      isAllNFT: isAllNFT
    });
  }))))), (next || loading) && !refreshing && /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    style: _styles.default.footer,
    alignItems: "center",
    justifyContent: "center"
  }, next && !loading && /*#__PURE__*/React.createElement(_appkitUiReactNative.Link, {
    size: "md",
    style: _styles.default.loadMoreButton,
    onPress: handleLoadMore
  }, "Load more"), loading && /*#__PURE__*/React.createElement(_appkitUiReactNative.LoadingSpinner, {
    color: "accent-100"
  })));
}
//# sourceMappingURL=index.js.map