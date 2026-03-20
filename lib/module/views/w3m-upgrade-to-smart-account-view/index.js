import { Linking } from 'react-native';
import { useEffect, useState } from 'react';
import { useSnapshot } from 'valtio';
import { Button, FlexView, IconLink, Link, Text, Visual } from '@reown/appkit-ui-react-native';
import { AccountController, ConnectorController, EventsController, ModalController, NetworkController, RouterController, SnackController } from '@reown/appkit-core-react-native';
import styles from './styles';
export function UpgradeToSmartAccountView() {
  const {
    address
  } = useSnapshot(AccountController.state);
  const {
    loading
  } = useSnapshot(ModalController.state);
  const [initialAddress] = useState(address);
  const onSwitchAccountType = async () => {
    try {
      ModalController.setLoading(true);
      const accountType = AccountController.state.preferredAccountType === 'eoa' ? 'smartAccount' : 'eoa';
      const provider = ConnectorController.getAuthConnector()?.provider;
      await provider?.setPreferredAccount(accountType);
      EventsController.sendEvent({
        type: 'track',
        event: 'SET_PREFERRED_ACCOUNT_TYPE',
        properties: {
          accountType,
          network: NetworkController.state.caipNetwork?.id || ''
        }
      });
    } catch (error) {
      ModalController.setLoading(false);
      SnackController.showError('Error switching account type');
    }
  };
  const onClose = () => {
    ModalController.close();
    ModalController.setLoading(false);
  };
  const onGoBack = () => {
    RouterController.goBack();
    ModalController.setLoading(false);
  };
  const onLearnMorePress = () => {
    Linking.openURL('https://reown.com/faq');
  };
  useEffect(() => {
    // Go back if the address has changed
    if (address && initialAddress !== address) {
      RouterController.goBack();
    }
  }, [initialAddress, address]);
  return /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(IconLink, {
    icon: "close",
    size: "md",
    onPress: onClose,
    testID: "header-close",
    style: styles.closeButton
  }), /*#__PURE__*/React.createElement(FlexView, {
    style: styles.container,
    padding: ['4xl', 'm', '2xl', 'm']
  }, /*#__PURE__*/React.createElement(FlexView, {
    alignItems: "center",
    justifyContent: "center",
    flexDirection: "row"
  }, /*#__PURE__*/React.createElement(Visual, {
    name: "google"
  }), /*#__PURE__*/React.createElement(Visual, {
    style: styles.middleIcon,
    name: "pencil"
  }), /*#__PURE__*/React.createElement(Visual, {
    name: "lightbulb"
  })), /*#__PURE__*/React.createElement(Text, {
    variant: "medium-600",
    color: "fg-100",
    style: styles.title
  }, "Discover Smart Accounts"), /*#__PURE__*/React.createElement(Text, {
    variant: "paragraph-400",
    color: "fg-100",
    center: true
  }, "Access advanced brand new features as username, improved security and a smoother user experience!"), /*#__PURE__*/React.createElement(FlexView, {
    flexDirection: "row",
    margin: ['m', '4xl', 'm', '4xl']
  }, /*#__PURE__*/React.createElement(Button, {
    variant: "accent",
    onPress: onGoBack,
    disabled: loading,
    style: [styles.button, styles.cancelButton]
  }, "Do it later"), /*#__PURE__*/React.createElement(Button, {
    onPress: onSwitchAccountType,
    loading: loading,
    style: styles.button
  }, "Continue")), /*#__PURE__*/React.createElement(Link, {
    onPress: onLearnMorePress,
    iconRight: "externalLink"
  }, "Learn more")));
}
//# sourceMappingURL=index.js.map