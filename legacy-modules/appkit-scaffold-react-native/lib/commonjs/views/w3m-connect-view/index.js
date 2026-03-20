"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.ConnectView = ConnectView;
var _valtio = require("valtio");
var _reactNative = require("react-native");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _useCustomDimensions = require("../../hooks/useCustomDimensions");
var _connectEmailInput = require("./components/connect-email-input");
var _useKeyboard = require("../../hooks/useKeyboard");
var _w3mPlaceholder = require("../../partials/w3m-placeholder");
var _connectorsList = require("./components/connectors-list");
var _customWalletList = require("./components/custom-wallet-list");
var _allWalletsButton = require("./components/all-wallets-button");
var _allWalletList = require("./components/all-wallet-list");
var _recentWalletList = require("./components/recent-wallet-list");
var _socialLoginList = require("./components/social-login-list");
var _walletGuide = require("./components/wallet-guide");
var _styles = _interopRequireDefault(require("./styles"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
function ConnectView() {
  const connectors = _appkitCoreReactNative.ConnectorController.state.connectors;
  const {
    authLoading
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.ConnectorController.state);
  const {
    prefetchError
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.ApiController.state);
  const {
    features
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.OptionsController.state);
  const {
    padding
  } = (0, _useCustomDimensions.useCustomDimensions)();
  const {
    keyboardShown,
    keyboardHeight
  } = (0, _useKeyboard.useKeyboard)();
  const isWalletConnectEnabled = connectors.some(c => c.type === 'WALLET_CONNECT');
  const isAuthEnabled = connectors.some(c => c.type === 'AUTH');
  const isCoinbaseEnabled = connectors.some(c => c.type === 'COINBASE');
  const isEmailEnabled = isAuthEnabled && features?.email;
  const isSocialEnabled = isAuthEnabled && features?.socials && features?.socials.length > 0;
  const showConnectWalletsButton = isWalletConnectEnabled && isAuthEnabled && !features?.emailShowWallets;
  const showSeparator = isAuthEnabled && (isEmailEnabled || isSocialEnabled) && (isWalletConnectEnabled || isCoinbaseEnabled);
  const showLoadingError = !showConnectWalletsButton && prefetchError;
  const showList = !showConnectWalletsButton && !showLoadingError;
  const paddingBottom = _reactNative.Platform.select({
    android: keyboardShown ? keyboardHeight + _appkitUiReactNative.Spacing['2xl'] : _appkitUiReactNative.Spacing['2xl'],
    default: _appkitUiReactNative.Spacing['2xl']
  });
  const onWalletPress = (wallet, isInstalled) => {
    const connector = connectors.find(c => c.explorerId === wallet.id);
    if (connector) {
      _appkitCoreReactNative.RouterController.push('ConnectingExternal', {
        connector,
        wallet
      });
    } else {
      _appkitCoreReactNative.RouterController.push('ConnectingWalletConnect', {
        wallet
      });
    }
    const platform = _appkitCoreReactNative.EventUtil.getWalletPlatform(wallet, isInstalled);
    _appkitCoreReactNative.EventsController.sendEvent({
      type: 'track',
      event: 'SELECT_WALLET',
      properties: {
        name: wallet.name ?? connector?.name ?? 'Unknown',
        platform,
        explorer_id: wallet.id
      }
    });
  };
  const onViewAllPress = () => {
    _appkitCoreReactNative.RouterController.push('AllWallets');
    _appkitCoreReactNative.EventsController.sendEvent({
      type: 'track',
      event: 'CLICK_ALL_WALLETS'
    });
  };
  return /*#__PURE__*/React.createElement(_reactNative.ScrollView, {
    style: {
      paddingHorizontal: padding
    },
    bounces: false
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    padding: ['xs', '0', '0', '0'],
    style: {
      paddingBottom
    }
  }, isEmailEnabled && /*#__PURE__*/React.createElement(_connectEmailInput.ConnectEmailInput, {
    loading: authLoading
  }), isSocialEnabled && /*#__PURE__*/React.createElement(_socialLoginList.SocialLoginList, {
    options: features?.socials,
    disabled: authLoading
  }), showSeparator && /*#__PURE__*/React.createElement(_appkitUiReactNative.Separator, {
    text: "or",
    style: _styles.default.socialSeparator
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    padding: ['0', 's', '0', 's']
  }, showConnectWalletsButton && /*#__PURE__*/React.createElement(_appkitUiReactNative.ListItem, {
    contentStyle: _styles.default.connectWalletButton,
    onPress: onViewAllPress
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Icon, {
    name: "wallet",
    size: "lg"
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "paragraph-500"
  }, "Continue with a wallet"), /*#__PURE__*/React.createElement(_reactNative.View, {
    style: _styles.default.connectWalletEmpty
  })), showLoadingError && /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    alignItems: "center",
    justifyContent: "center",
    margin: ['l', '0', '0', '0']
  }, /*#__PURE__*/React.createElement(_w3mPlaceholder.Placeholder, {
    icon: "warningCircle",
    iconColor: "error-100",
    title: "Oops, we couldn\u2019t load the wallets at the moment",
    description: `This might be due to a temporary network issue.\nPlease try reloading to see if that helps.`,
    actionIcon: "refresh",
    actionPress: _appkitCoreReactNative.ApiController.prefetch,
    actionTitle: "Retry"
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.Separator, {
    style: _styles.default.socialSeparator
  })), showList && /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(_recentWalletList.RecentWalletList, {
    itemStyle: _styles.default.item,
    onWalletPress: onWalletPress,
    isWalletConnectEnabled: isWalletConnectEnabled
  }), /*#__PURE__*/React.createElement(_allWalletList.AllWalletList, {
    itemStyle: _styles.default.item,
    onWalletPress: onWalletPress,
    isWalletConnectEnabled: isWalletConnectEnabled
  }), /*#__PURE__*/React.createElement(_customWalletList.CustomWalletList, {
    itemStyle: _styles.default.item,
    onWalletPress: onWalletPress,
    isWalletConnectEnabled: isWalletConnectEnabled
  }), /*#__PURE__*/React.createElement(_connectorsList.ConnectorList, {
    itemStyle: _styles.default.item,
    isWalletConnectEnabled: isWalletConnectEnabled
  }), /*#__PURE__*/React.createElement(_allWalletsButton.AllWalletsButton, {
    itemStyle: _styles.default.item,
    onPress: onViewAllPress,
    isWalletConnectEnabled: isWalletConnectEnabled
  })), isAuthEnabled && /*#__PURE__*/React.createElement(_walletGuide.WalletGuide, {
    guide: "get-started"
  }))));
}
//# sourceMappingURL=index.js.map