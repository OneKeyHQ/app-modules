import { useSnapshot } from 'valtio';
import { useCallback, useEffect, useMemo, useState } from 'react';
import { ScrollView, View, RefreshControl } from 'react-native';
import { FlexView, Link, ListTransaction, LoadingSpinner, Text, TransactionUtil, useTheme } from '@reown/appkit-ui-react-native';
import { AccountController, AssetController, AssetUtil, EventsController, NetworkController, OptionsController, TransactionsController } from '@reown/appkit-core-react-native';
import { Placeholder } from '../w3m-placeholder';
import { getTransactionListItemProps } from './utils';
import styles from './styles';
export function AccountActivity({
  style
}) {
  const Theme = useTheme();
  const [refreshing, setRefreshing] = useState(false);
  const [initialLoad, setInitialLoad] = useState(true);
  const {
    loading,
    transactions,
    next
  } = useSnapshot(TransactionsController.state);
  const {
    caipNetwork
  } = useSnapshot(NetworkController.state);
  const {
    networkImages
  } = useSnapshot(AssetController.state);
  const networkImage = AssetUtil.getNetworkImage(caipNetwork, networkImages);
  const handleLoadMore = () => {
    TransactionsController.fetchTransactions(AccountController.state.address);
    EventsController.sendEvent({
      type: 'track',
      event: 'LOAD_MORE_TRANSACTIONS',
      properties: {
        address: AccountController.state.address,
        projectId: OptionsController.state.projectId,
        cursor: TransactionsController.state.next,
        isSmartAccount: AccountController.state.preferredAccountType === 'smartAccount'
      }
    });
  };
  const onRefresh = useCallback(async () => {
    setRefreshing(true);
    await TransactionsController.fetchTransactions(AccountController.state.address, true);
    setRefreshing(false);
  }, []);
  const transactionsByYear = useMemo(() => {
    return TransactionsController.getTransactionsByYearAndMonth(transactions);
  }, [transactions]);
  useEffect(() => {
    if (!TransactionsController.state.transactions.length) {
      TransactionsController.fetchTransactions(AccountController.state.address, true);
    }
    // Set initial load to false after first fetch
    const timer = setTimeout(() => setInitialLoad(false), 100);
    return () => clearTimeout(timer);
  }, []);

  // Show loading spinner during initial load or when loading with no transactions
  if ((initialLoad || loading) && !transactions.length) {
    return /*#__PURE__*/React.createElement(FlexView, {
      style: [styles.placeholder, style],
      alignItems: "center",
      justifyContent: "center"
    }, /*#__PURE__*/React.createElement(LoadingSpinner, null));
  }

  // Only show placeholder when we're not in initial load or loading state
  if (!Object.keys(transactionsByYear).length && !loading && !initialLoad) {
    return /*#__PURE__*/React.createElement(Placeholder, {
      icon: "swapHorizontal",
      title: "No activity yet",
      description: "Your next transactions will appear here",
      style: [styles.placeholder, style]
    });
  }
  return /*#__PURE__*/React.createElement(ScrollView, {
    style: [styles.container, style],
    fadingEdgeLength: 20,
    contentContainerStyle: [styles.contentContainer],
    refreshControl: /*#__PURE__*/React.createElement(RefreshControl, {
      refreshing: refreshing,
      onRefresh: onRefresh,
      tintColor: Theme['accent-100'],
      colors: [Theme['accent-100']]
    })
  }, Object.keys(transactionsByYear).reverse().map(year => /*#__PURE__*/React.createElement(View, {
    key: year
  }, Object.keys(transactionsByYear[year] || {}).reverse().map(month => /*#__PURE__*/React.createElement(View, {
    key: month
  }, /*#__PURE__*/React.createElement(Text, {
    variant: "paragraph-400",
    color: "fg-200",
    style: styles.separatorText
  }, TransactionUtil.getTransactionGroupTitle(year, month)), transactionsByYear[year]?.[month]?.map(transaction => {
    const {
      date,
      type,
      descriptions,
      status,
      images,
      isAllNFT,
      transfers
    } = getTransactionListItemProps(transaction);
    const hasMultipleTransfers = transfers?.length > 2;

    // Show only the first transfer
    if (hasMultipleTransfers) {
      const description = TransactionUtil.getTransferDescription(transfers[0]);
      return /*#__PURE__*/React.createElement(ListTransaction, {
        key: `${transaction.id}@${description}`,
        date: date,
        type: type,
        descriptions: [description],
        status: status,
        images: [images[0]],
        networkSrc: networkImage,
        style: styles.transactionItem,
        isAllNFT: isAllNFT
      });
    }
    return /*#__PURE__*/React.createElement(ListTransaction, {
      key: transaction.id,
      date: date,
      type: type,
      descriptions: descriptions,
      status: status,
      images: images,
      networkSrc: networkImage,
      style: styles.transactionItem,
      isAllNFT: isAllNFT
    });
  }))))), (next || loading) && !refreshing && /*#__PURE__*/React.createElement(FlexView, {
    style: styles.footer,
    alignItems: "center",
    justifyContent: "center"
  }, next && !loading && /*#__PURE__*/React.createElement(Link, {
    size: "md",
    style: styles.loadMoreButton,
    onPress: handleLoadMore
  }, "Load more"), loading && /*#__PURE__*/React.createElement(LoadingSpinner, {
    color: "accent-100"
  })));
}
//# sourceMappingURL=index.js.map