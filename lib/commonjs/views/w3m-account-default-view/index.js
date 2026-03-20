"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.AccountDefaultView = AccountDefaultView;
var _valtio = require("valtio");
var _react = require("react");
var _reactNative = require("react-native");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _useCustomDimensions = require("../../hooks/useCustomDimensions");
var _styles = _interopRequireDefault(require("./styles"));
var _authButtons = require("./components/auth-buttons");
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
function AccountDefaultView() {
  const {
    address,
    profileName,
    profileImage,
    balance,
    balanceSymbol,
    addressExplorerUrl,
    preferredAccountType
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.AccountController.state);
  const {
    loading
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.ModalController.state);
  const [disconnecting, setDisconnecting] = (0, _react.useState)(false);
  const {
    caipNetwork
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.NetworkController.state);
  const {
    connectedConnector
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.ConnectorController.state);
  const {
    connectedSocialProvider
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.ConnectionController.state);
  const {
    features,
    isOnRampEnabled
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.OptionsController.state);
  const {
    history
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.RouterController.state);
  const {
    networkImages
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.AssetController.state);
  const networkImage = _appkitCoreReactNative.AssetUtil.getNetworkImage(caipNetwork, networkImages);
  const showCopy = _appkitCoreReactNative.OptionsController.isClipboardAvailable();
  const isAuth = connectedConnector === 'AUTH';
  const showBalance = balance && !isAuth;
  const showExplorer = addressExplorerUrl && !isAuth;
  const showBack = history.length > 1;
  const showSwitchAccountType = isAuth && _appkitCoreReactNative.NetworkController.checkIfSmartAccountEnabled();
  const {
    padding
  } = (0, _useCustomDimensions.useCustomDimensions)();
  async function onDisconnect() {
    setDisconnecting(true);
    await _appkitCoreReactNative.ConnectionUtil.disconnect();
    setDisconnecting(false);
  }
  const onSwitchAccountType = async () => {
    try {
      if (isAuth) {
        _appkitCoreReactNative.ModalController.setLoading(true);
        const accountType = _appkitCoreReactNative.AccountController.state.preferredAccountType === 'eoa' ? 'smartAccount' : 'eoa';
        const provider = _appkitCoreReactNative.ConnectorController.getAuthConnector()?.provider;
        await provider?.setPreferredAccount(accountType);
        const chainIdString = _appkitCoreReactNative.NetworkController.state.caipNetwork?.id?.split(':')[1];
        const chainId = chainIdString && !isNaN(Number(chainIdString)) ? Number(chainIdString) : undefined;
        // eslint-disable-next-line valtio/state-snapshot-rule
        await provider?.connect({
          chainId,
          preferredAccountType: accountType
        });
        _appkitCoreReactNative.EventsController.sendEvent({
          type: 'track',
          event: 'SET_PREFERRED_ACCOUNT_TYPE',
          properties: {
            accountType,
            network: _appkitCoreReactNative.NetworkController.state.caipNetwork?.id || ''
          }
        });
      }
    } catch (error) {
      _appkitCoreReactNative.ModalController.setLoading(false);
      _appkitCoreReactNative.SnackController.showError('Error switching account type');
    }
  };
  const getUserEmail = () => {
    const provider = _appkitCoreReactNative.ConnectorController.getAuthConnector()?.provider;
    if (!provider) return '';
    return provider.getEmail();
  };
  const getUsername = () => {
    const provider = _appkitCoreReactNative.ConnectorController.getAuthConnector()?.provider;
    if (!provider) return '';
    return provider.getUsername();
  };
  const onExplorerPress = () => {
    if (_appkitCoreReactNative.AccountController.state.addressExplorerUrl) {
      _reactNative.Linking.openURL(_appkitCoreReactNative.AccountController.state.addressExplorerUrl);
    }
  };
  const onCopyAddress = () => {
    if (_appkitCoreReactNative.AccountController.state.profileName) {
      _appkitCoreReactNative.OptionsController.copyToClipboard(_appkitCoreReactNative.AccountController.state.profileName);
      _appkitCoreReactNative.SnackController.showSuccess('Name copied');
    } else if (_appkitCoreReactNative.AccountController.state.address) {
      _appkitCoreReactNative.OptionsController.copyToClipboard(_appkitCoreReactNative.AccountController.state.profileName ?? _appkitCoreReactNative.AccountController.state.address);
      _appkitCoreReactNative.SnackController.showSuccess('Address copied');
    }
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
          isSmartAccount: false
        }
      });
      _appkitCoreReactNative.RouterController.push('Swap');
    }
  };
  const onBuyPress = () => {
    _appkitCoreReactNative.EventsController.sendEvent({
      type: 'track',
      event: 'SELECT_BUY_CRYPTO'
    });
    _appkitCoreReactNative.OnRampController.resetState();
    _appkitCoreReactNative.RouterController.push('OnRamp');
  };
  const onActivityPress = () => {
    _appkitCoreReactNative.RouterController.push('Transactions');
  };
  const onNetworkPress = () => {
    _appkitCoreReactNative.RouterController.push('Networks');
    _appkitCoreReactNative.EventsController.sendEvent({
      type: 'track',
      event: 'CLICK_NETWORKS'
    });
  };
  const onUpgradePress = () => {
    _appkitCoreReactNative.EventsController.sendEvent({
      type: 'track',
      event: 'EMAIL_UPGRADE_FROM_MODAL'
    });
    _appkitCoreReactNative.RouterController.push('UpgradeEmailWallet');
  };
  const onEmailPress = () => {
    if (_appkitCoreReactNative.ConnectionController.state.connectedSocialProvider) return;
    _appkitCoreReactNative.RouterController.push('UpdateEmailWallet', {
      email: getUserEmail()
    });
  };
  return /*#__PURE__*/React.createElement(React.Fragment, null, showBack && /*#__PURE__*/React.createElement(_appkitUiReactNative.IconLink, {
    icon: "chevronLeft",
    style: _styles.default.backIcon,
    onPress: _appkitCoreReactNative.RouterController.goBack
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.IconLink, {
    icon: "close",
    style: _styles.default.closeIcon,
    onPress: _appkitCoreReactNative.ModalController.close,
    testID: "header-close"
  }), /*#__PURE__*/React.createElement(_reactNative.ScrollView, {
    bounces: false,
    fadingEdgeLength: 20,
    style: {
      paddingHorizontal: padding
    }
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    alignItems: "center",
    padding: ['3xl', 's', '3xl', 's']
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Avatar, {
    imageSrc: profileImage,
    address: address
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    alignItems: "center",
    margin: ['s', '0', '0', '0']
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "medium-title-600"
  }, profileName ? _appkitUiReactNative.UiUtil.getTruncateString({
    string: profileName,
    charsStart: 20,
    charsEnd: 0,
    truncate: 'end'
  }) : _appkitUiReactNative.UiUtil.getTruncateString({
    string: address ?? '',
    charsStart: 4,
    charsEnd: 6,
    truncate: 'middle'
  })), showCopy && /*#__PURE__*/React.createElement(_appkitUiReactNative.IconLink, {
    icon: "copy",
    size: "md",
    iconColor: "fg-275",
    onPress: onCopyAddress,
    style: _styles.default.copyButton
  })), showBalance && /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "paragraph-400",
    color: "fg-200"
  }, _appkitCoreReactNative.CoreHelperUtil.formatBalance(balance, balanceSymbol)), showExplorer && /*#__PURE__*/React.createElement(_appkitUiReactNative.Button, {
    size: "sm",
    variant: "shade",
    iconLeft: "compass",
    iconRight: "externalLink",
    onPress: onExplorerPress,
    style: {
      marginVertical: _appkitUiReactNative.Spacing.s
    }
  }, "Block Explorer"), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    margin: ['s', '0', '0', '0']
  }, isAuth && /*#__PURE__*/React.createElement(_authButtons.AuthButtons, {
    onUpgradePress: onUpgradePress,
    socialProvider: connectedSocialProvider,
    onPress: onEmailPress,
    style: _styles.default.actionButton,
    text: _appkitUiReactNative.UiUtil.getTruncateString({
      string: getUsername() || getUserEmail() || '',
      charsStart: 30,
      charsEnd: 0,
      truncate: 'end'
    })
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.ListItem, {
    chevron: true,
    icon: "networkPlaceholder",
    iconColor: "accent-100",
    iconBackgroundColor: "accent-glass-015",
    imageSrc: networkImage,
    imageHeaders: _appkitCoreReactNative.ApiController._getApiHeaders(),
    onPress: onNetworkPress,
    testID: "button-network",
    style: _styles.default.actionButton
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    numberOfLines: 1,
    color: "fg-100",
    testID: "account-select-network-text"
  }, caipNetwork?.name)), !isAuth && isOnRampEnabled && /*#__PURE__*/React.createElement(_appkitUiReactNative.ListItem, {
    chevron: true,
    icon: "card",
    iconColor: "accent-100",
    iconBackgroundColor: "accent-glass-015",
    onPress: onBuyPress,
    testID: "button-onramp",
    style: _styles.default.actionButton
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    color: "fg-100"
  }, "Buy crypto")), !isAuth && features?.swaps && /*#__PURE__*/React.createElement(_appkitUiReactNative.ListItem, {
    chevron: true,
    icon: "recycleHorizontal",
    iconColor: "accent-100",
    iconBackgroundColor: "accent-glass-015",
    onPress: onSwapPress,
    testID: "button-swap",
    style: _styles.default.actionButton
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    color: "fg-100"
  }, "Swap")), !isAuth && /*#__PURE__*/React.createElement(_appkitUiReactNative.ListItem, {
    chevron: true,
    icon: "clock",
    iconColor: "accent-100",
    iconBackgroundColor: "accent-glass-015",
    onPress: onActivityPress,
    testID: "button-activity",
    style: _styles.default.actionButton
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    color: "fg-100"
  }, "Activity")), showSwitchAccountType && /*#__PURE__*/React.createElement(_appkitUiReactNative.ListItem, {
    chevron: true,
    icon: "swapHorizontal",
    onPress: onSwitchAccountType,
    testID: "account-button-type",
    iconColor: "accent-100",
    iconBackgroundColor: "accent-glass-015",
    style: _styles.default.actionButton,
    loading: loading
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    color: "fg-100"
  }, `Switch to your ${preferredAccountType === 'eoa' ? 'smart account' : 'EOA'}`)), /*#__PURE__*/React.createElement(_appkitUiReactNative.ListItem, {
    icon: "disconnect",
    onPress: onDisconnect,
    loading: disconnecting,
    iconBackgroundColor: "gray-glass-010",
    testID: "button-disconnect"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    color: "fg-200"
  }, "Disconnect"))))));
}
//# sourceMappingURL=index.js.map