"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.AppKitRouter = AppKitRouter;
var _react = require("react");
var _valtio = require("valtio");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _w3mAccountDefaultView = require("../../views/w3m-account-default-view");
var _w3mAccountView = require("../../views/w3m-account-view");
var _w3mAllWalletsView = require("../../views/w3m-all-wallets-view");
var _w3mConnectView = require("../../views/w3m-connect-view");
var _w3mConnectSocialsView = require("../../views/w3m-connect-socials-view");
var _w3mConnectingView = require("../../views/w3m-connecting-view");
var _w3mConnectingExternalView = require("../../views/w3m-connecting-external-view");
var _w3mConnectingFarcasterView = require("../../views/w3m-connecting-farcaster-view");
var _w3mConnectingSocialView = require("../../views/w3m-connecting-social-view");
var _w3mCreateView = require("../../views/w3m-create-view");
var _appkitSiweReactNative = require("@reown/appkit-siwe-react-native");
var _w3mEmailVerifyOtpView = require("../../views/w3m-email-verify-otp-view");
var _w3mEmailVerifyDeviceView = require("../../views/w3m-email-verify-device-view");
var _w3mGetWalletView = require("../../views/w3m-get-wallet-view");
var _w3mNetworksView = require("../../views/w3m-networks-view");
var _w3mNetworkSwitchView = require("../../views/w3m-network-switch-view");
var _w3mOnrampLoadingView = require("../../views/w3m-onramp-loading-view");
var _w3mOnrampView = require("../../views/w3m-onramp-view");
var _w3mOnrampCheckoutView = require("../../views/w3m-onramp-checkout-view");
var _w3mOnrampSettingsView = require("../../views/w3m-onramp-settings-view");
var _w3mOnrampTransactionView = require("../../views/w3m-onramp-transaction-view");
var _w3mSwapView = require("../../views/w3m-swap-view");
var _w3mSwapPreviewView = require("../../views/w3m-swap-preview-view");
var _w3mSwapSelectTokenView = require("../../views/w3m-swap-select-token-view");
var _w3mTransactionsView = require("../../views/w3m-transactions-view");
var _w3mUnsupportedChainView = require("../../views/w3m-unsupported-chain-view");
var _w3mUpdateEmailWalletView = require("../../views/w3m-update-email-wallet-view");
var _w3mUpdateEmailPrimaryOtpView = require("../../views/w3m-update-email-primary-otp-view");
var _w3mUpdateEmailSecondaryOtpView = require("../../views/w3m-update-email-secondary-otp-view");
var _w3mUpgradeEmailWalletView = require("../../views/w3m-upgrade-email-wallet-view");
var _w3mUpgradeToSmartAccountView = require("../../views/w3m-upgrade-to-smart-account-view");
var _w3mWalletCompatibleNetworksView = require("../../views/w3m-wallet-compatible-networks-view");
var _w3mWalletReceiveView = require("../../views/w3m-wallet-receive-view");
var _w3mWalletSendView = require("../../views/w3m-wallet-send-view");
var _w3mWalletSendPreviewView = require("../../views/w3m-wallet-send-preview-view");
var _w3mWalletSendSelectTokenView = require("../../views/w3m-wallet-send-select-token-view");
var _w3mWhatIsANetworkView = require("../../views/w3m-what-is-a-network-view");
var _w3mWhatIsAWalletView = require("../../views/w3m-what-is-a-wallet-view");
var _UiUtil = require("../../utils/UiUtil");
function AppKitRouter() {
  const {
    view
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.RouterController.state);
  (0, _react.useLayoutEffect)(() => {
    _UiUtil.UiUtil.createViewTransition();
  }, [view]);
  const ViewComponent = (0, _react.useMemo)(() => {
    switch (view) {
      case 'Account':
        return _w3mAccountView.AccountView;
      case 'AccountDefault':
        return _w3mAccountDefaultView.AccountDefaultView;
      case 'AllWallets':
        return _w3mAllWalletsView.AllWalletsView;
      case 'Connect':
        return _w3mConnectView.ConnectView;
      case 'ConnectSocials':
        return _w3mConnectSocialsView.ConnectSocialsView;
      case 'ConnectingExternal':
        return _w3mConnectingExternalView.ConnectingExternalView;
      case 'ConnectingSiwe':
        return _appkitSiweReactNative.ConnectingSiweView;
      case 'ConnectingSocial':
        return _w3mConnectingSocialView.ConnectingSocialView;
      case 'ConnectingFarcaster':
        return _w3mConnectingFarcasterView.ConnectingFarcasterView;
      case 'ConnectingWalletConnect':
        return _w3mConnectingView.ConnectingView;
      case 'Create':
        return _w3mCreateView.CreateView;
      case 'EmailVerifyDevice':
        return _w3mEmailVerifyDeviceView.EmailVerifyDeviceView;
      case 'EmailVerifyOtp':
        return _w3mEmailVerifyOtpView.EmailVerifyOtpView;
      case 'GetWallet':
        return _w3mGetWalletView.GetWalletView;
      case 'Networks':
        return _w3mNetworksView.NetworksView;
      case 'OnRamp':
        return _w3mOnrampView.OnRampView;
      case 'OnRampCheckout':
        return _w3mOnrampCheckoutView.OnRampCheckoutView;
      case 'OnRampSettings':
        return _w3mOnrampSettingsView.OnRampSettingsView;
      case 'OnRampLoading':
        return _w3mOnrampLoadingView.OnRampLoadingView;
      case 'SwitchNetwork':
        return _w3mNetworkSwitchView.NetworkSwitchView;
      case 'OnRampTransaction':
        return _w3mOnrampTransactionView.OnRampTransactionView;
      case 'Swap':
        return _w3mSwapView.SwapView;
      case 'SwapPreview':
        return _w3mSwapPreviewView.SwapPreviewView;
      case 'SwapSelectToken':
        return _w3mSwapSelectTokenView.SwapSelectTokenView;
      case 'Transactions':
        return _w3mTransactionsView.TransactionsView;
      case 'UnsupportedChain':
        return _w3mUnsupportedChainView.UnsupportedChainView;
      case 'UpdateEmailPrimaryOtp':
        return _w3mUpdateEmailPrimaryOtpView.UpdateEmailPrimaryOtpView;
      case 'UpdateEmailSecondaryOtp':
        return _w3mUpdateEmailSecondaryOtpView.UpdateEmailSecondaryOtpView;
      case 'UpdateEmailWallet':
        return _w3mUpdateEmailWalletView.UpdateEmailWalletView;
      case 'UpgradeEmailWallet':
        return _w3mUpgradeEmailWalletView.UpgradeEmailWalletView;
      case 'UpgradeToSmartAccount':
        return _w3mUpgradeToSmartAccountView.UpgradeToSmartAccountView;
      case 'WalletCompatibleNetworks':
        return _w3mWalletCompatibleNetworksView.WalletCompatibleNetworks;
      case 'WalletReceive':
        return _w3mWalletReceiveView.WalletReceiveView;
      case 'WalletSend':
        return _w3mWalletSendView.WalletSendView;
      case 'WalletSendPreview':
        return _w3mWalletSendPreviewView.WalletSendPreviewView;
      case 'WalletSendSelectToken':
        return _w3mWalletSendSelectTokenView.WalletSendSelectTokenView;
      case 'WhatIsANetwork':
        return _w3mWhatIsANetworkView.WhatIsANetworkView;
      case 'WhatIsAWallet':
        return _w3mWhatIsAWalletView.WhatIsAWalletView;
      default:
        return _w3mConnectView.ConnectView;
    }
  }, [view]);
  return /*#__PURE__*/React.createElement(ViewComponent, null);
}
//# sourceMappingURL=index.js.map