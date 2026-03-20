import { StyleSheet } from 'react-native';
import { UpgradeWalletButton } from './upgrade-wallet-button';
import { ListItem, ListSocial, Spacing, Text } from '@reown/appkit-ui-react-native';
export function AuthButtons({
  onUpgradePress,
  onPress,
  socialProvider,
  text,
  style
}) {
  return /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(UpgradeWalletButton, {
    onPress: onUpgradePress,
    style: styles.upgradeButton
  }), socialProvider ? /*#__PURE__*/React.createElement(ListSocial, {
    logo: socialProvider,
    logoHeight: 32,
    logoWidth: 32,
    style: [styles.socialContainer, style]
  }, /*#__PURE__*/React.createElement(Text, {
    color: "fg-100",
    numberOfLines: 1,
    ellipsizeMode: "tail",
    style: styles.socialText
  }, text)) : /*#__PURE__*/React.createElement(ListItem, {
    icon: "mail",
    onPress: onPress,
    chevron: true,
    testID: "button-email",
    iconColor: "fg-100",
    style: style
  }, /*#__PURE__*/React.createElement(Text, {
    color: "fg-100",
    numberOfLines: 1,
    ellipsizeMode: "tail"
  }, text)));
}
const styles = StyleSheet.create({
  actionButton: {
    marginBottom: Spacing.xs
  },
  upgradeButton: {
    marginBottom: Spacing.s
  },
  socialContainer: {
    justifyContent: 'flex-start',
    width: '100%'
  },
  socialText: {
    flex: 1,
    marginLeft: Spacing.s
  }
});
//# sourceMappingURL=auth-buttons.js.map