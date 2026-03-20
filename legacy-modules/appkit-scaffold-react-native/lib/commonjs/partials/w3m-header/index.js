"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.Header = Header;
var _valtio = require("valtio");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _appkitCommonReactNative = require("@reown/appkit-common-react-native");
var _styles = _interopRequireDefault(require("./styles"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
function Header() {
  const {
    data,
    view
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.RouterController.state);
  const onHelpPress = () => {
    _appkitCoreReactNative.RouterController.push('WhatIsAWallet');
    _appkitCoreReactNative.EventsController.sendEvent({
      type: 'track',
      event: 'CLICK_WALLET_HELP'
    });
  };
  const headings = (_data, _view) => {
    const connectorName = _data?.connector?.name;
    const walletName = _data?.wallet?.name;
    const networkName = _data?.network?.name;
    const socialName = _appkitCoreReactNative.ConnectionController.state.selectedSocialProvider ? _appkitCommonReactNative.StringUtil.capitalize(_appkitCoreReactNative.ConnectionController.state.selectedSocialProvider) : undefined;
    return {
      Account: undefined,
      AccountDefault: undefined,
      AllWallets: 'All wallets',
      Connect: 'Connect wallet',
      ConnectSocials: 'All socials',
      ConnectingExternal: connectorName ?? 'Connect wallet',
      ConnectingSiwe: undefined,
      ConnectingFarcaster: socialName ?? 'Connecting Social',
      ConnectingSocial: socialName ?? 'Connecting Social',
      ConnectingWalletConnect: walletName ?? 'WalletConnect',
      Create: 'Create wallet',
      EmailVerifyDevice: ' ',
      EmailVerifyOtp: 'Confirm email',
      GetWallet: 'Get a wallet',
      Networks: 'Select network',
      OnRamp: undefined,
      OnRampCheckout: 'Checkout',
      OnRampSettings: 'Preferences',
      OnRampLoading: undefined,
      OnRampTransaction: ' ',
      SwitchNetwork: networkName ?? 'Switch network',
      Swap: 'Swap',
      SwapSelectToken: 'Select token',
      SwapPreview: 'Review swap',
      Transactions: 'Activity',
      UnsupportedChain: 'Switch network',
      UpdateEmailPrimaryOtp: 'Confirm current email',
      UpdateEmailSecondaryOtp: 'Confirm new email',
      UpdateEmailWallet: 'Edit email',
      UpgradeEmailWallet: 'Upgrade wallet',
      UpgradeToSmartAccount: undefined,
      WalletCompatibleNetworks: 'Compatible networks',
      WalletReceive: 'Receive',
      WalletSend: 'Send',
      WalletSendPreview: 'Review send',
      WalletSendSelectToken: 'Select token',
      WhatIsANetwork: 'What is a network?',
      WhatIsAWallet: 'What is a wallet?'
    }[_view];
  };
  const noCloseViews = ['OnRampSettings'];
  const showClose = !noCloseViews.includes(view);
  const header = headings(data, view);
  const checkSocial = () => {
    if (_appkitCoreReactNative.RouterController.state.view === 'ConnectingFarcaster' || _appkitCoreReactNative.RouterController.state.view === 'ConnectingSocial') {
      const socialProvider = _appkitCoreReactNative.ConnectionController.state.selectedSocialProvider;
      const authProvider = _appkitCoreReactNative.ConnectorController.getAuthConnector()?.provider;
      if (authProvider && socialProvider === 'farcaster') {
        // TODO: remove this once Farcaster session refresh is implemented
        // @ts-expect-error
        authProvider.webviewRef?.current?.reload();
      }
      _appkitCoreReactNative.EventsController.sendEvent({
        type: 'track',
        event: 'SOCIAL_LOGIN_CANCELED',
        properties: {
          provider: _appkitCoreReactNative.ConnectionController.state.selectedSocialProvider
        }
      });
    }
  };
  const handleGoBack = () => {
    checkSocial();
    _appkitCoreReactNative.RouterController.goBack();
  };
  const handleClose = () => {
    checkSocial();
    _appkitCoreReactNative.ModalController.close();
  };
  const dynamicButtonTemplate = () => {
    const showBack = _appkitCoreReactNative.RouterController.state.history.length > 1;
    const showHelp = _appkitCoreReactNative.RouterController.state.view === 'Connect';
    if (showHelp) {
      return /*#__PURE__*/React.createElement(_appkitUiReactNative.IconLink, {
        icon: "helpCircle",
        size: "md",
        onPress: onHelpPress,
        testID: "help-button"
      });
    }
    if (showBack) {
      return /*#__PURE__*/React.createElement(_appkitUiReactNative.IconLink, {
        icon: "chevronLeft",
        size: "md",
        onPress: handleGoBack,
        testID: "button-back"
      });
    }
    return /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
      style: _styles.default.iconPlaceholder
    });
  };
  if (!header) return null;
  const bottomPadding = header === ' ' ? '0' : '4xs';
  return /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    justifyContent: "space-between",
    flexDirection: "row",
    alignItems: "center",
    padding: ['l', 'xl', bottomPadding, 'xl']
  }, dynamicButtonTemplate(), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "paragraph-600",
    numberOfLines: 1,
    testID: "header-text"
  }, header), showClose ? /*#__PURE__*/React.createElement(_appkitUiReactNative.IconLink, {
    icon: "close",
    size: "md",
    onPress: handleClose,
    testID: "header-close"
  }) : /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    style: _styles.default.iconPlaceholder
  }));
}
//# sourceMappingURL=index.js.map