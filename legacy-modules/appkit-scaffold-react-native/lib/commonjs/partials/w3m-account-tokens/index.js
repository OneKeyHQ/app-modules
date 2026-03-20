"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.AccountTokens = AccountTokens;
var _react = require("react");
var _reactNative = require("react-native");
var _valtio = require("valtio");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
function AccountTokens({
  style
}) {
  const Theme = (0, _appkitUiReactNative.useTheme)();
  const [refreshing, setRefreshing] = (0, _react.useState)(false);
  const {
    tokenBalance
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.AccountController.state);
  const {
    caipNetwork
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.NetworkController.state);
  const {
    networkImages
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.AssetController.state);
  const networkImage = _appkitCoreReactNative.AssetUtil.getNetworkImage(caipNetwork, networkImages);
  const onRefresh = (0, _react.useCallback)(async () => {
    setRefreshing(true);
    _appkitCoreReactNative.AccountController.fetchTokenBalance();
    setRefreshing(false);
  }, []);
  const onReceivePress = () => {
    _appkitCoreReactNative.RouterController.push('WalletReceive');
  };
  if (!tokenBalance?.length) {
    return /*#__PURE__*/React.createElement(_appkitUiReactNative.ListItem, {
      icon: "arrowBottomCircle",
      iconColor: "magenta-100",
      onPress: onReceivePress,
      style: styles.receiveButton
    }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
      flexDirection: "column",
      alignItems: "flex-start"
    }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
      variant: "paragraph-500",
      color: "fg-100"
    }, "Receive funds"), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
      variant: "small-400",
      color: "fg-200"
    }, "Transfer tokens on your wallet")));
  }
  return /*#__PURE__*/React.createElement(_reactNative.ScrollView, {
    fadingEdgeLength: 20,
    style: style,
    refreshControl: /*#__PURE__*/React.createElement(_reactNative.RefreshControl, {
      refreshing: refreshing,
      onRefresh: onRefresh,
      tintColor: Theme['accent-100'],
      colors: [Theme['accent-100']]
    })
  }, tokenBalance.map(token => /*#__PURE__*/React.createElement(_appkitUiReactNative.ListToken, {
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
const styles = _reactNative.StyleSheet.create({
  receiveButton: {
    width: 'auto',
    marginHorizontal: _appkitUiReactNative.Spacing.s
  }
});
//# sourceMappingURL=index.js.map