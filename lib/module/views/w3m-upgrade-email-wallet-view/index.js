import { useSnapshot } from 'valtio';
import { Linking, StyleSheet } from 'react-native';
import { Chip, FlexView, Spacing, Text } from '@reown/appkit-ui-react-native';
import { ConnectorController } from '@reown/appkit-core-react-native';
export function UpgradeEmailWalletView() {
  const {
    connectors
  } = useSnapshot(ConnectorController.state);
  const authProvider = connectors.find(c => c.type === 'AUTH')?.provider;
  const onLinkPress = () => {
    const link = authProvider.getSecureSiteDashboardURL();
    Linking.canOpenURL(link).then(supported => {
      if (supported) Linking.openURL(link);
    });
  };
  return /*#__PURE__*/React.createElement(FlexView, {
    padding: ['l', 'l', '3xl', 'l'],
    alignItems: "center"
  }, /*#__PURE__*/React.createElement(Text, {
    variant: "paragraph-400"
  }, "Follow the instructions on"), /*#__PURE__*/React.createElement(Chip, {
    label: "secure.reown.com",
    rightIcon: "externalLink",
    imageSrc: authProvider.getSecureSiteIconURL(),
    style: styles.chip,
    onPress: onLinkPress
  }), /*#__PURE__*/React.createElement(Text, {
    variant: "small-400",
    color: "fg-200"
  }, "You will have to reconnect for security reasons"));
}
const styles = StyleSheet.create({
  chip: {
    marginVertical: Spacing.m
  }
});
//# sourceMappingURL=index.js.map