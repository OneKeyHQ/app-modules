import { RouterController } from '@reown/appkit-core-react-native';
import { Chip, FlexView, Link, Separator, Spacing, Text } from '@reown/appkit-ui-react-native';
import { Linking, StyleSheet } from 'react-native';
export function WalletGuide({
  guide
}) {
  const onExplorerPress = () => {
    Linking.openURL('https://explorer.walletconnect.com');
  };
  const onGetStartedPress = () => {
    RouterController.push('Create');
  };
  return guide === 'explore' ? /*#__PURE__*/React.createElement(FlexView, {
    alignItems: "center",
    justifyContent: "center"
  }, /*#__PURE__*/React.createElement(Separator, {
    text: "or",
    style: styles.socialSeparator
  }), /*#__PURE__*/React.createElement(Text, {
    variant: "small-400",
    style: styles.text
  }, "Looking for a self-custody wallet?"), /*#__PURE__*/React.createElement(Chip, {
    label: "Visit our explorer",
    variant: "transparent",
    rightIcon: "externalLink",
    leftIcon: "walletConnectLightBrown",
    size: "sm",
    onPress: onExplorerPress
  })) : /*#__PURE__*/React.createElement(FlexView, {
    alignItems: "center",
    justifyContent: "center",
    margin: "m",
    flexDirection: "row"
  }, /*#__PURE__*/React.createElement(Text, {
    variant: "small-400"
  }, "Haven't got a wallet?"), /*#__PURE__*/React.createElement(Link, {
    onPress: onGetStartedPress,
    size: "sm"
  }, "Get started"));
}
const styles = StyleSheet.create({
  text: {
    marginBottom: Spacing.xs
  },
  socialSeparator: {
    marginVertical: Spacing.l
  }
});
//# sourceMappingURL=wallet-guide.js.map