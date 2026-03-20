import { useSnapshot } from 'valtio';
import { Platform, ScrollView, View } from 'react-native';
import { ApiController, ConnectorController, EventUtil, EventsController, OptionsController, RouterController } from '@reown/appkit-core-react-native';
import { FlexView, Icon, ListItem, Separator, Spacing, Text } from '@reown/appkit-ui-react-native';
import { useCustomDimensions } from '../../hooks/useCustomDimensions';
import { ConnectEmailInput } from './components/connect-email-input';
import { useKeyboard } from '../../hooks/useKeyboard';
import { Placeholder } from '../../partials/w3m-placeholder';
import { ConnectorList } from './components/connectors-list';
import { CustomWalletList } from './components/custom-wallet-list';
import { AllWalletsButton } from './components/all-wallets-button';
import { AllWalletList } from './components/all-wallet-list';
import { RecentWalletList } from './components/recent-wallet-list';
import { SocialLoginList } from './components/social-login-list';
import { WalletGuide } from './components/wallet-guide';
import styles from './styles';
export function ConnectView() {
  const connectors = ConnectorController.state.connectors;
  const {
    authLoading
  } = useSnapshot(ConnectorController.state);
  const {
    prefetchError
  } = useSnapshot(ApiController.state);
  const {
    features
  } = useSnapshot(OptionsController.state);
  const {
    padding
  } = useCustomDimensions();
  const {
    keyboardShown,
    keyboardHeight
  } = useKeyboard();
  const isWalletConnectEnabled = connectors.some(c => c.type === 'WALLET_CONNECT');
  const isAuthEnabled = connectors.some(c => c.type === 'AUTH');
  const isCoinbaseEnabled = connectors.some(c => c.type === 'COINBASE');
  const isEmailEnabled = isAuthEnabled && features?.email;
  const isSocialEnabled = isAuthEnabled && features?.socials && features?.socials.length > 0;
  const showConnectWalletsButton = isWalletConnectEnabled && isAuthEnabled && !features?.emailShowWallets;
  const showSeparator = isAuthEnabled && (isEmailEnabled || isSocialEnabled) && (isWalletConnectEnabled || isCoinbaseEnabled);
  const showLoadingError = !showConnectWalletsButton && prefetchError;
  const showList = !showConnectWalletsButton && !showLoadingError;
  const paddingBottom = Platform.select({
    android: keyboardShown ? keyboardHeight + Spacing['2xl'] : Spacing['2xl'],
    default: Spacing['2xl']
  });
  const onWalletPress = (wallet, isInstalled) => {
    const connector = connectors.find(c => c.explorerId === wallet.id);
    if (connector) {
      RouterController.push('ConnectingExternal', {
        connector,
        wallet
      });
    } else {
      RouterController.push('ConnectingWalletConnect', {
        wallet
      });
    }
    const platform = EventUtil.getWalletPlatform(wallet, isInstalled);
    EventsController.sendEvent({
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
    RouterController.push('AllWallets');
    EventsController.sendEvent({
      type: 'track',
      event: 'CLICK_ALL_WALLETS'
    });
  };
  return /*#__PURE__*/React.createElement(ScrollView, {
    style: {
      paddingHorizontal: padding
    },
    bounces: false
  }, /*#__PURE__*/React.createElement(FlexView, {
    padding: ['xs', '0', '0', '0'],
    style: {
      paddingBottom
    }
  }, isEmailEnabled && /*#__PURE__*/React.createElement(ConnectEmailInput, {
    loading: authLoading
  }), isSocialEnabled && /*#__PURE__*/React.createElement(SocialLoginList, {
    options: features?.socials,
    disabled: authLoading
  }), showSeparator && /*#__PURE__*/React.createElement(Separator, {
    text: "or",
    style: styles.socialSeparator
  }), /*#__PURE__*/React.createElement(FlexView, {
    padding: ['0', 's', '0', 's']
  }, showConnectWalletsButton && /*#__PURE__*/React.createElement(ListItem, {
    contentStyle: styles.connectWalletButton,
    onPress: onViewAllPress
  }, /*#__PURE__*/React.createElement(Icon, {
    name: "wallet",
    size: "lg"
  }), /*#__PURE__*/React.createElement(Text, {
    variant: "paragraph-500"
  }, "Continue with a wallet"), /*#__PURE__*/React.createElement(View, {
    style: styles.connectWalletEmpty
  })), showLoadingError && /*#__PURE__*/React.createElement(FlexView, {
    alignItems: "center",
    justifyContent: "center",
    margin: ['l', '0', '0', '0']
  }, /*#__PURE__*/React.createElement(Placeholder, {
    icon: "warningCircle",
    iconColor: "error-100",
    title: "Oops, we couldn\u2019t load the wallets at the moment",
    description: `This might be due to a temporary network issue.\nPlease try reloading to see if that helps.`,
    actionIcon: "refresh",
    actionPress: ApiController.prefetch,
    actionTitle: "Retry"
  }), /*#__PURE__*/React.createElement(Separator, {
    style: styles.socialSeparator
  })), showList && /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(RecentWalletList, {
    itemStyle: styles.item,
    onWalletPress: onWalletPress,
    isWalletConnectEnabled: isWalletConnectEnabled
  }), /*#__PURE__*/React.createElement(AllWalletList, {
    itemStyle: styles.item,
    onWalletPress: onWalletPress,
    isWalletConnectEnabled: isWalletConnectEnabled
  }), /*#__PURE__*/React.createElement(CustomWalletList, {
    itemStyle: styles.item,
    onWalletPress: onWalletPress,
    isWalletConnectEnabled: isWalletConnectEnabled
  }), /*#__PURE__*/React.createElement(ConnectorList, {
    itemStyle: styles.item,
    isWalletConnectEnabled: isWalletConnectEnabled
  }), /*#__PURE__*/React.createElement(AllWalletsButton, {
    itemStyle: styles.item,
    onPress: onViewAllPress,
    isWalletConnectEnabled: isWalletConnectEnabled
  })), isAuthEnabled && /*#__PURE__*/React.createElement(WalletGuide, {
    guide: "get-started"
  }))));
}
//# sourceMappingURL=index.js.map