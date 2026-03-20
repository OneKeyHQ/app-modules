import { useSnapshot } from 'valtio';
import { useEffect } from 'react';
import { ScrollView } from 'react-native';
import { AccountPill, FlexView, Icon, IconLink, NetworkButton, useTheme, Promo } from '@reown/appkit-ui-react-native';
import { AccountController, ApiController, AssetController, AssetUtil, ModalController, NetworkController, RouterController, SendController } from '@reown/appkit-core-react-native';
import { useCustomDimensions } from '../../hooks/useCustomDimensions';
import { AccountWalletFeatures } from '../../partials/w3m-account-wallet-features';
import styles from './styles';
export function AccountView() {
  const Theme = useTheme();
  const {
    padding
  } = useCustomDimensions();
  const {
    caipNetwork
  } = useSnapshot(NetworkController.state);
  const {
    networkImages
  } = useSnapshot(AssetController.state);
  const {
    address,
    profileName,
    profileImage,
    preferredAccountType
  } = useSnapshot(AccountController.state);
  const showActivate = preferredAccountType === 'eoa' && NetworkController.checkIfSmartAccountEnabled();
  const onProfilePress = () => {
    RouterController.push('AccountDefault');
  };
  const onNetworkPress = () => {
    RouterController.push('Networks');
  };
  const onActivatePress = () => {
    RouterController.push('UpgradeToSmartAccount');
  };
  useEffect(() => {
    AccountController.fetchTokenBalance();
    SendController.resetSend();
  }, []);
  useEffect(() => {
    AccountController.fetchTokenBalance();
    const balanceInterval = setInterval(() => {
      AccountController.fetchTokenBalance();
    }, 10000);
    return () => {
      clearInterval(balanceInterval);
    };
  }, []);
  return /*#__PURE__*/React.createElement(ScrollView, {
    bounces: false,
    contentContainerStyle: [styles.contentContainer, {
      paddingHorizontal: padding
    }]
  }, /*#__PURE__*/React.createElement(NetworkButton, {
    imageSrc: AssetUtil.getNetworkImage(caipNetwork, networkImages),
    imageHeaders: ApiController._getApiHeaders(),
    onPress: onNetworkPress,
    style: styles.networkIcon,
    background: false
  }, /*#__PURE__*/React.createElement(Icon, {
    name: "chevronBottom",
    size: "sm",
    color: "fg-200"
  })), /*#__PURE__*/React.createElement(IconLink, {
    icon: "close",
    style: styles.closeIcon,
    onPress: ModalController.close
  }), /*#__PURE__*/React.createElement(FlexView, {
    padding: ['3xl', '0', '0', '0'],
    style: [{
      backgroundColor: Theme['bg-100']
    }]
  }, showActivate && /*#__PURE__*/React.createElement(Promo, {
    style: styles.promoPill,
    text: "Switch to your smart account",
    onPress: onActivatePress
  }), /*#__PURE__*/React.createElement(AccountPill, {
    address: address,
    profileName: profileName,
    profileImage: profileImage,
    onPress: onProfilePress,
    style: styles.accountPill
  }), /*#__PURE__*/React.createElement(AccountWalletFeatures, null)));
}
//# sourceMappingURL=index.js.map