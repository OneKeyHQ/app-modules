import { useState } from 'react';
import { useSnapshot } from 'valtio';
import { Balance, FlexView, IconLink, Tabs } from '@reown/appkit-ui-react-native';
import { AccountController, ConstantsUtil, CoreHelperUtil, EventsController, NetworkController, OnRampController, OptionsController, RouterController, SwapController } from '@reown/appkit-core-react-native';
import { AccountActivity } from '../w3m-account-activity';
import { AccountTokens } from '../w3m-account-tokens';
import styles from './styles';
export function AccountWalletFeatures() {
  const [activeTab, setActiveTab] = useState(0);
  const {
    tokenBalance
  } = useSnapshot(AccountController.state);
  const {
    features,
    isOnRampEnabled
  } = useSnapshot(OptionsController.state);
  const balance = CoreHelperUtil.calculateAndFormatBalance(tokenBalance);
  const isSwapsEnabled = features?.swaps;
  const onTabChange = index => {
    setActiveTab(index);
    if (index === 2) {
      onTransactionsPress();
    }
  };
  const onTransactionsPress = () => {
    EventsController.sendEvent({
      type: 'track',
      event: 'CLICK_TRANSACTIONS',
      properties: {
        isSmartAccount: AccountController.state.preferredAccountType === 'smartAccount'
      }
    });
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
          isSmartAccount: AccountController.state.preferredAccountType === 'smartAccount'
        }
      });
      RouterController.push('Swap');
    }
  };
  const onSendPress = () => {
    EventsController.sendEvent({
      type: 'track',
      event: 'OPEN_SEND',
      properties: {
        network: NetworkController.state.caipNetwork?.id || '',
        isSmartAccount: AccountController.state.preferredAccountType === 'smartAccount'
      }
    });
    RouterController.push('WalletSend');
  };
  const onReceivePress = () => {
    RouterController.push('WalletReceive');
  };
  const onBuyPress = () => {
    EventsController.sendEvent({
      type: 'track',
      event: 'SELECT_BUY_CRYPTO'
    });
    OnRampController.resetState();
    RouterController.push('OnRamp');
  };
  return /*#__PURE__*/React.createElement(FlexView, {
    style: styles.container,
    alignItems: "center"
  }, /*#__PURE__*/React.createElement(Balance, {
    integer: balance.dollars,
    decimal: balance.pennies
  }), /*#__PURE__*/React.createElement(FlexView, {
    style: styles.actionsContainer,
    flexDirection: "row",
    justifyContent: "space-around",
    padding: ['0', 's', '0', 's']
  }, isOnRampEnabled && /*#__PURE__*/React.createElement(IconLink, {
    icon: "card",
    size: "lg",
    iconColor: "accent-100",
    background: true,
    backgroundColor: "accent-glass-010",
    pressedColor: "accent-glass-020",
    style: [styles.action, isSwapsEnabled ? styles.actionCenter : styles.actionLeft],
    onPress: onBuyPress
  }), isSwapsEnabled && /*#__PURE__*/React.createElement(IconLink, {
    icon: "recycleHorizontal",
    size: "lg",
    iconColor: "accent-100",
    background: true,
    backgroundColor: "accent-glass-010",
    pressedColor: "accent-glass-020",
    style: [styles.action, styles.actionLeft],
    onPress: onSwapPress
  }), /*#__PURE__*/React.createElement(IconLink, {
    icon: "arrowBottomCircle",
    size: "lg",
    iconColor: "accent-100",
    background: true,
    backgroundColor: "accent-glass-010",
    pressedColor: "accent-glass-020",
    style: [styles.action, isSwapsEnabled ? styles.actionCenter : styles.actionLeft],
    onPress: onReceivePress
  }), /*#__PURE__*/React.createElement(IconLink, {
    icon: "paperplane",
    size: "lg",
    iconColor: "accent-100",
    background: true,
    backgroundColor: "accent-glass-010",
    pressedColor: "accent-glass-020",
    style: [styles.action, styles.actionRight],
    onPress: onSendPress
  })), /*#__PURE__*/React.createElement(FlexView, {
    style: styles.tab
  }, /*#__PURE__*/React.createElement(Tabs, {
    tabs: ['Tokens', 'Activity'],
    onTabChange: onTabChange
  })), /*#__PURE__*/React.createElement(FlexView, {
    padding: ['m', '0', '0', '0'],
    style: styles.tabContainer
  }, activeTab === 0 && /*#__PURE__*/React.createElement(AccountTokens, {
    style: styles.tabContent
  }), activeTab === 1 && /*#__PURE__*/React.createElement(AccountActivity, {
    style: styles.tabContent
  })));
}
//# sourceMappingURL=index.js.map