import { useCallback, useState } from 'react';
import { RefreshControl, ScrollView, StyleSheet } from 'react-native';
import { useSnapshot } from 'valtio';
import { AccountController, AssetController, AssetUtil, NetworkController, RouterController } from '@reown/appkit-core-react-native';
import { FlexView, ListItem, Text, ListToken, useTheme, Spacing } from '@reown/appkit-ui-react-native';
export function AccountTokens({
  style
}) {
  const Theme = useTheme();
  const [refreshing, setRefreshing] = useState(false);
  const {
    tokenBalance
  } = useSnapshot(AccountController.state);
  const {
    caipNetwork
  } = useSnapshot(NetworkController.state);
  const {
    networkImages
  } = useSnapshot(AssetController.state);
  const networkImage = AssetUtil.getNetworkImage(caipNetwork, networkImages);
  const onRefresh = useCallback(async () => {
    setRefreshing(true);
    AccountController.fetchTokenBalance();
    setRefreshing(false);
  }, []);
  const onReceivePress = () => {
    RouterController.push('WalletReceive');
  };
  if (!tokenBalance?.length) {
    return /*#__PURE__*/React.createElement(ListItem, {
      icon: "arrowBottomCircle",
      iconColor: "magenta-100",
      onPress: onReceivePress,
      style: styles.receiveButton
    }, /*#__PURE__*/React.createElement(FlexView, {
      flexDirection: "column",
      alignItems: "flex-start"
    }, /*#__PURE__*/React.createElement(Text, {
      variant: "paragraph-500",
      color: "fg-100"
    }, "Receive funds"), /*#__PURE__*/React.createElement(Text, {
      variant: "small-400",
      color: "fg-200"
    }, "Transfer tokens on your wallet")));
  }
  return /*#__PURE__*/React.createElement(ScrollView, {
    fadingEdgeLength: 20,
    style: style,
    refreshControl: /*#__PURE__*/React.createElement(RefreshControl, {
      refreshing: refreshing,
      onRefresh: onRefresh,
      tintColor: Theme['accent-100'],
      colors: [Theme['accent-100']]
    })
  }, tokenBalance.map(token => /*#__PURE__*/React.createElement(ListToken, {
    key: token.name,
    name: token.name,
    imageSrc: token.iconUrl,
    networkSrc: networkImage,
    value: token.value,
    amount: token.quantity.numeric,
    currency: token.symbol,
    pressable: false
  })));
}
const styles = StyleSheet.create({
  receiveButton: {
    width: 'auto',
    marginHorizontal: Spacing.s
  }
});
//# sourceMappingURL=index.js.map