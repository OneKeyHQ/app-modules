import { useSnapshot } from 'valtio';
import { useState } from 'react';
import { Linking, ScrollView } from 'react-native';
import { AccountController, ApiController, AssetUtil, ConnectionController, ConnectorController, CoreHelperUtil, ConnectionUtil, EventsController, ModalController, NetworkController, OptionsController, RouterController, SnackController, ConstantsUtil, SwapController, OnRampController, AssetController } from '@reown/appkit-core-react-native';
import { Avatar, Button, FlexView, IconLink, Text, UiUtil, Spacing, ListItem } from '@reown/appkit-ui-react-native';
import { useCustomDimensions } from '../../hooks/useCustomDimensions';
import styles from './styles';
import { AuthButtons } from './components/auth-buttons';
export function AccountDefaultView() {
  const {
    address,
    profileName,
    profileImage,
    balance,
    balanceSymbol,
    addressExplorerUrl,
    preferredAccountType
  } = useSnapshot(AccountController.state);
  const {
    loading
  } = useSnapshot(ModalController.state);
  const [disconnecting, setDisconnecting] = useState(false);
  const {
    caipNetwork
  } = useSnapshot(NetworkController.state);
  const {
    connectedConnector
  } = useSnapshot(ConnectorController.state);
  const {
    connectedSocialProvider
  } = useSnapshot(ConnectionController.state);
  const {
    features,
    isOnRampEnabled
  } = useSnapshot(OptionsController.state);
  const {
    history
  } = useSnapshot(RouterController.state);
  const {
    networkImages
  } = useSnapshot(AssetController.state);
  const networkImage = AssetUtil.getNetworkImage(caipNetwork, networkImages);
  const showCopy = OptionsController.isClipboardAvailable();
  const isAuth = connectedConnector === 'AUTH';
  const showBalance = balance && !isAuth;
  const showExplorer = addressExplorerUrl && !isAuth;
  const showBack = history.length > 1;
  const showSwitchAccountType = isAuth && NetworkController.checkIfSmartAccountEnabled();
  const {
    padding
  } = useCustomDimensions();
  async function onDisconnect() {
    setDisconnecting(true);
    await ConnectionUtil.disconnect();
    setDisconnecting(false);
  }
  const onSwitchAccountType = async () => {
    try {
      if (isAuth) {
        ModalController.setLoading(true);
        const accountType = AccountController.state.preferredAccountType === 'eoa' ? 'smartAccount' : 'eoa';
        const provider = ConnectorController.getAuthConnector()?.provider;
        await provider?.setPreferredAccount(accountType);
        const chainIdString = NetworkController.state.caipNetwork?.id?.split(':')[1];
        const chainId = chainIdString && !isNaN(Number(chainIdString)) ? Number(chainIdString) : undefined;
        // eslint-disable-next-line valtio/state-snapshot-rule
        await provider?.connect({
          chainId,
          preferredAccountType: accountType
        });
        EventsController.sendEvent({
          type: 'track',
          event: 'SET_PREFERRED_ACCOUNT_TYPE',
          properties: {
            accountType,
            network: NetworkController.state.caipNetwork?.id || ''
          }
        });
      }
    } catch (error) {
      ModalController.setLoading(false);
      SnackController.showError('Error switching account type');
    }
  };
  const getUserEmail = () => {
    const provider = ConnectorController.getAuthConnector()?.provider;
    if (!provider) return '';
    return provider.getEmail();
  };
  const getUsername = () => {
    const provider = ConnectorController.getAuthConnector()?.provider;
    if (!provider) return '';
    return provider.getUsername();
  };
  const onExplorerPress = () => {
    if (AccountController.state.addressExplorerUrl) {
      Linking.openURL(AccountController.state.addressExplorerUrl);
    }
  };
  const onCopyAddress = () => {
    if (AccountController.state.profileName) {
      OptionsController.copyToClipboard(AccountController.state.profileName);
      SnackController.showSuccess('Name copied');
    } else if (AccountController.state.address) {
      OptionsController.copyToClipboard(AccountController.state.profileName ?? AccountController.state.address);
      SnackController.showSuccess('Address copied');
    }
  };
  const onSwapPress = () => {
    if (NetworkController.state.caipNetwork?.id && !ConstantsUtil.SWAP_SUPPORTED_NETWORKS.includes(`${NetworkController.state.caipNetwork.id}`)) {
      RouterController.push('UnsupportedChain');
    } else {
      SwapController.resetState();
      EventsController.sendEvent({
        type: 'track',
        event: 'OPEN_SWAP',
        properties: {
          network: NetworkController.state.caipNetwork?.id || '',
          isSmartAccount: false
        }
      });
      RouterController.push('Swap');
    }
  };
  const onBuyPress = () => {
    EventsController.sendEvent({
      type: 'track',
      event: 'SELECT_BUY_CRYPTO'
    });
    OnRampController.resetState();
    RouterController.push('OnRamp');
  };
  const onActivityPress = () => {
    RouterController.push('Transactions');
  };
  const onNetworkPress = () => {
    RouterController.push('Networks');
    EventsController.sendEvent({
      type: 'track',
      event: 'CLICK_NETWORKS'
    });
  };
  const onUpgradePress = () => {
    EventsController.sendEvent({
      type: 'track',
      event: 'EMAIL_UPGRADE_FROM_MODAL'
    });
    RouterController.push('UpgradeEmailWallet');
  };
  const onEmailPress = () => {
    if (ConnectionController.state.connectedSocialProvider) return;
    RouterController.push('UpdateEmailWallet', {
      email: getUserEmail()
    });
  };
  return /*#__PURE__*/React.createElement(React.Fragment, null, showBack && /*#__PURE__*/React.createElement(IconLink, {
    icon: "chevronLeft",
    style: styles.backIcon,
    onPress: RouterController.goBack
  }), /*#__PURE__*/React.createElement(IconLink, {
    icon: "close",
    style: styles.closeIcon,
    onPress: ModalController.close,
    testID: "header-close"
  }), /*#__PURE__*/React.createElement(ScrollView, {
    bounces: false,
    fadingEdgeLength: 20,
    style: {
      paddingHorizontal: padding
    }
  }, /*#__PURE__*/React.createElement(FlexView, {
    alignItems: "center",
    padding: ['3xl', 's', '3xl', 's']
  }, /*#__PURE__*/React.createElement(Avatar, {
    imageSrc: profileImage,
    address: address
  }), /*#__PURE__*/React.createElement(FlexView, {
    flexDirection: "row",
    alignItems: "center",
    margin: ['s', '0', '0', '0']
  }, /*#__PURE__*/React.createElement(Text, {
    variant: "medium-title-600"
  }, profileName ? UiUtil.getTruncateString({
    string: profileName,
    charsStart: 20,
    charsEnd: 0,
    truncate: 'end'
  }) : UiUtil.getTruncateString({
    string: address ?? '',
    charsStart: 4,
    charsEnd: 6,
    truncate: 'middle'
  })), showCopy && /*#__PURE__*/React.createElement(IconLink, {
    icon: "copy",
    size: "md",
    iconColor: "fg-275",
    onPress: onCopyAddress,
    style: styles.copyButton
  })), showBalance && /*#__PURE__*/React.createElement(Text, {
    variant: "paragraph-400",
    color: "fg-200"
  }, CoreHelperUtil.formatBalance(balance, balanceSymbol)), showExplorer && /*#__PURE__*/React.createElement(Button, {
    size: "sm",
    variant: "shade",
    iconLeft: "compass",
    iconRight: "externalLink",
    onPress: onExplorerPress,
    style: {
      marginVertical: Spacing.s
    }
  }, "Block Explorer"), /*#__PURE__*/React.createElement(FlexView, {
    margin: ['s', '0', '0', '0']
  }, isAuth && /*#__PURE__*/React.createElement(AuthButtons, {
    onUpgradePress: onUpgradePress,
    socialProvider: connectedSocialProvider,
    onPress: onEmailPress,
    style: styles.actionButton,
    text: UiUtil.getTruncateString({
      string: getUsername() || getUserEmail() || '',
      charsStart: 30,
      charsEnd: 0,
      truncate: 'end'
    })
  }), /*#__PURE__*/React.createElement(ListItem, {
    chevron: true,
    icon: "networkPlaceholder",
    iconColor: "accent-100",
    iconBackgroundColor: "accent-glass-015",
    imageSrc: networkImage,
    imageHeaders: ApiController._getApiHeaders(),
    onPress: onNetworkPress,
    testID: "button-network",
    style: styles.actionButton
  }, /*#__PURE__*/React.createElement(Text, {
    numberOfLines: 1,
    color: "fg-100",
    testID: "account-select-network-text"
  }, caipNetwork?.name)), !isAuth && isOnRampEnabled && /*#__PURE__*/React.createElement(ListItem, {
    chevron: true,
    icon: "card",
    iconColor: "accent-100",
    iconBackgroundColor: "accent-glass-015",
    onPress: onBuyPress,
    testID: "button-onramp",
    style: styles.actionButton
  }, /*#__PURE__*/React.createElement(Text, {
    color: "fg-100"
  }, "Buy crypto")), !isAuth && features?.swaps && /*#__PURE__*/React.createElement(ListItem, {
    chevron: true,
    icon: "recycleHorizontal",
    iconColor: "accent-100",
    iconBackgroundColor: "accent-glass-015",
    onPress: onSwapPress,
    testID: "button-swap",
    style: styles.actionButton
  }, /*#__PURE__*/React.createElement(Text, {
    color: "fg-100"
  }, "Swap")), !isAuth && /*#__PURE__*/React.createElement(ListItem, {
    chevron: true,
    icon: "clock",
    iconColor: "accent-100",
    iconBackgroundColor: "accent-glass-015",
    onPress: onActivityPress,
    testID: "button-activity",
    style: styles.actionButton
  }, /*#__PURE__*/React.createElement(Text, {
    color: "fg-100"
  }, "Activity")), showSwitchAccountType && /*#__PURE__*/React.createElement(ListItem, {
    chevron: true,
    icon: "swapHorizontal",
    onPress: onSwitchAccountType,
    testID: "account-button-type",
    iconColor: "accent-100",
    iconBackgroundColor: "accent-glass-015",
    style: styles.actionButton,
    loading: loading
  }, /*#__PURE__*/React.createElement(Text, {
    color: "fg-100"
  }, `Switch to your ${preferredAccountType === 'eoa' ? 'smart account' : 'EOA'}`)), /*#__PURE__*/React.createElement(ListItem, {
    icon: "disconnect",
    onPress: onDisconnect,
    loading: disconnecting,
    iconBackgroundColor: "gray-glass-010",
    testID: "button-disconnect"
  }, /*#__PURE__*/React.createElement(Text, {
    color: "fg-200"
  }, "Disconnect"))))));
}
//# sourceMappingURL=index.js.map