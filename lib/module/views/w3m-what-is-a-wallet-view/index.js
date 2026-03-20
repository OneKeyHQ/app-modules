import { ScrollView } from 'react-native';
import { Button, FlexView, Text, Visual } from '@reown/appkit-ui-react-native';
import { EventsController, RouterController } from '@reown/appkit-core-react-native';
import { useCustomDimensions } from '../../hooks/useCustomDimensions';
import styles from './styles';
export function WhatIsAWalletView() {
  const {
    padding
  } = useCustomDimensions();
  const onGetWalletPress = () => {
    RouterController.push('GetWallet');
    EventsController.sendEvent({
      type: 'track',
      event: 'CLICK_GET_WALLET'
    });
  };
  return /*#__PURE__*/React.createElement(ScrollView, {
    bounces: false,
    fadingEdgeLength: 20,
    style: {
      paddingHorizontal: padding
    },
    testID: "what-is-a-wallet-view"
  }, /*#__PURE__*/React.createElement(FlexView, {
    alignItems: "center",
    padding: ['xs', '4xl', 'xl', '4xl']
  }, /*#__PURE__*/React.createElement(FlexView, {
    flexDirection: "row",
    padding: ['0', '0', 's', '0']
  }, /*#__PURE__*/React.createElement(Visual, {
    name: "login"
  }), /*#__PURE__*/React.createElement(Visual, {
    name: "profile",
    style: styles.visual
  }), /*#__PURE__*/React.createElement(Visual, {
    name: "lock"
  })), /*#__PURE__*/React.createElement(Text, {
    variant: "paragraph-500",
    style: styles.text
  }, "Your web3 account"), /*#__PURE__*/React.createElement(Text, {
    variant: "small-500",
    color: "fg-200",
    center: true
  }, "Create a wallet with your email or by choosing a wallet provider."), /*#__PURE__*/React.createElement(FlexView, {
    flexDirection: "row",
    padding: ['xl', '0', 's', '0']
  }, /*#__PURE__*/React.createElement(Visual, {
    name: "defi"
  }), /*#__PURE__*/React.createElement(Visual, {
    name: "nft",
    style: styles.visual
  }), /*#__PURE__*/React.createElement(Visual, {
    name: "eth"
  })), /*#__PURE__*/React.createElement(Text, {
    variant: "paragraph-500",
    style: styles.text
  }, "The home for your digital assets"), /*#__PURE__*/React.createElement(Text, {
    variant: "small-500",
    color: "fg-200",
    center: true
  }, "Store, send, and receive digital assets like crypto and NFTs."), /*#__PURE__*/React.createElement(FlexView, {
    flexDirection: "row",
    padding: ['xl', '0', 's', '0']
  }, /*#__PURE__*/React.createElement(Visual, {
    name: "browser"
  }), /*#__PURE__*/React.createElement(Visual, {
    name: "noun",
    style: styles.visual
  }), /*#__PURE__*/React.createElement(Visual, {
    name: "dao"
  })), /*#__PURE__*/React.createElement(Text, {
    variant: "paragraph-500",
    style: styles.text
  }, "Your gateway to web3 apps"), /*#__PURE__*/React.createElement(Text, {
    variant: "small-500",
    color: "fg-200",
    center: true
  }, "Connect your wallet to start exploring DeFi, DAOs, and much more."), /*#__PURE__*/React.createElement(Button, {
    size: "sm",
    iconLeft: "walletSmall",
    style: styles.getWalletButton,
    onPress: onGetWalletPress,
    testID: "get-a-wallet-button"
  }, "Get a wallet")));
}
//# sourceMappingURL=index.js.map