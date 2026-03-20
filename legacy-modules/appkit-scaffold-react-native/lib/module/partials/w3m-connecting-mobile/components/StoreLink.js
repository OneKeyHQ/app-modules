import { ActionEntry, Button, Spacing, Text } from '@reown/appkit-ui-react-native';
import { StyleSheet } from 'react-native';
export function StoreLink({
  visible,
  walletName = 'Wallet',
  onPress
}) {
  if (!visible) return null;
  return /*#__PURE__*/React.createElement(ActionEntry, {
    style: styles.storeButton
  }, /*#__PURE__*/React.createElement(Text, {
    numberOfLines: 1,
    variant: "paragraph-500",
    color: "fg-200"
  }, `Don't have ${walletName}?`), /*#__PURE__*/React.createElement(Button, {
    variant: "accent",
    iconRight: "chevronRightSmall",
    onPress: onPress,
    size: "sm",
    hitSlop: 20
  }, "Get"));
}
const styles = StyleSheet.create({
  storeButton: {
    justifyContent: 'space-between',
    paddingHorizontal: Spacing.l,
    marginHorizontal: Spacing.xl,
    marginTop: Spacing.l
  }
});
//# sourceMappingURL=StoreLink.js.map