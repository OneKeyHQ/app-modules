"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.AccountWalletFeatures = AccountWalletFeatures;
var _react = require("react");
var _valtio = require("valtio");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _w3mAccountActivity = require("../w3m-account-activity");
var _w3mAccountTokens = require("../w3m-account-tokens");
var _styles = _interopRequireDefault(require("./styles"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
function AccountWalletFeatures() {
  const [activeTab, setActiveTab] = (0, _react.useState)(0);
  const {
    tokenBalance
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.AccountController.state);
  const {
    features,
    isOnRampEnabled
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.OptionsController.state);
  const balance = _appkitCoreReactNative.CoreHelperUtil.calculateAndFormatBalance(tokenBalance);
  const isSwapsEnabled = features?.swaps;
  const onTabChange = index => {
    setActiveTab(index);
    if (index === 2) {
      onTransactionsPress();
    }
  };
  const onTransactionsPress = () => {
    _appkitCoreReactNative.EventsController.sendEvent({
      type: 'track',
      event: 'CLICK_TRANSACTIONS',
      properties: {
        isSmartAccount: _appkitCoreReactNative.AccountController.state.preferredAccountType === 'smartAccount'
      }
    });
  };
  const onSwapPress = () => {
    if (_appkitCoreReactNative.NetworkController.state.caipNetwork?.id && !_appkitCoreReactNative.ConstantsUtil.SWAP_SUPPORTED_NETWORKS.includes(`${_appkitCoreReactNative.NetworkController.state.caipNetwork.id}`)) {
      _appkitCoreReactNative.RouterController.push('UnsupportedChain');
    } else {
      _appkitCoreReactNative.SwapController.resetState();
      _appkitCoreReactNative.EventsController.sendEvent({
        type: 'track',
        event: 'OPEN_SWAP',
        properties: {
          network: _appkitCoreReactNative.NetworkController.state.caipNetwork?.id || '',
          isSmartAccount: _appkitCoreReactNative.AccountController.state.preferredAccountType === 'smartAccount'
        }
      });
      _appkitCoreReactNative.RouterController.push('Swap');
    }
  };
  const onSendPress = () => {
    _appkitCoreReactNative.EventsController.sendEvent({
      type: 'track',
      event: 'OPEN_SEND',
      properties: {
        network: _appkitCoreReactNative.NetworkController.state.caipNetwork?.id || '',
        isSmartAccount: _appkitCoreReactNative.AccountController.state.preferredAccountType === 'smartAccount'
      }
    });
    _appkitCoreReactNative.RouterController.push('WalletSend');
  };
  const onReceivePress = () => {
    _appkitCoreReactNative.RouterController.push('WalletReceive');
  };
  const onBuyPress = () => {
    _appkitCoreReactNative.EventsController.sendEvent({
      type: 'track',
      event: 'SELECT_BUY_CRYPTO'
    });
    _appkitCoreReactNative.OnRampController.resetState();
    _appkitCoreReactNative.RouterController.push('OnRamp');
  };
  return /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    style: _styles.default.container,
    alignItems: "center"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Balance, {
    integer: balance.dollars,
    decimal: balance.pennies
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    style: _styles.default.actionsContainer,
    flexDirection: "row",
    justifyContent: "space-around",
    padding: ['0', 's', '0', 's']
  }, isOnRampEnabled && /*#__PURE__*/React.createElement(_appkitUiReactNative.IconLink, {
    icon: "card",
    size: "lg",
    iconColor: "accent-100",
    background: true,
    backgroundColor: "accent-glass-010",
    pressedColor: "accent-glass-020",
    style: [_styles.default.action, isSwapsEnabled ? _styles.default.actionCenter : _styles.default.actionLeft],
    onPress: onBuyPress
  }), isSwapsEnabled && /*#__PURE__*/React.createElement(_appkitUiReactNative.IconLink, {
    icon: "recycleHorizontal",
    size: "lg",
    iconColor: "accent-100",
    background: true,
    backgroundColor: "accent-glass-010",
    pressedColor: "accent-glass-020",
    style: [_styles.default.action, _styles.default.actionLeft],
    onPress: onSwapPress
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.IconLink, {
    icon: "arrowBottomCircle",
    size: "lg",
    iconColor: "accent-100",
    background: true,
    backgroundColor: "accent-glass-010",
    pressedColor: "accent-glass-020",
    style: [_styles.default.action, isSwapsEnabled ? _styles.default.actionCenter : _styles.default.actionLeft],
    onPress: onReceivePress
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.IconLink, {
    icon: "paperplane",
    size: "lg",
    iconColor: "accent-100",
    background: true,
    backgroundColor: "accent-glass-010",
    pressedColor: "accent-glass-020",
    style: [_styles.default.action, _styles.default.actionRight],
    onPress: onSendPress
  })), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    style: _styles.default.tab
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Tabs, {
    tabs: ['Tokens', 'Activity'],
    onTabChange: onTabChange
  })), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    padding: ['m', '0', '0', '0'],
    style: _styles.default.tabContainer
  }, activeTab === 0 && /*#__PURE__*/React.createElement(_w3mAccountTokens.AccountTokens, {
    style: _styles.default.tabContent
  }), activeTab === 1 && /*#__PURE__*/React.createElement(_w3mAccountActivity.AccountActivity, {
    style: _styles.default.tabContent
  })));
}
//# sourceMappingURL=index.js.map