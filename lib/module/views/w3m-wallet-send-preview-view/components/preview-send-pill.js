import { BorderRadius, FlexView, Text, useTheme } from '@reown/appkit-ui-react-native';
import { StyleSheet } from 'react-native';
export function PreviewSendPill({
  text,
  children
}) {
  const Theme = useTheme();
  return /*#__PURE__*/React.createElement(FlexView, {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
    padding: ['xs', 's', 'xs', 's'],
    style: [styles.pill, {
      backgroundColor: Theme['gray-glass-002'],
      borderColor: Theme['gray-glass-010']
    }]
  }, /*#__PURE__*/React.createElement(Text, {
    variant: "large-500",
    color: "fg-100"
  }, text), children);
}
const styles = StyleSheet.create({
  pill: {
    borderRadius: BorderRadius.full,
    borderWidth: StyleSheet.hairlineWidth
  }
});
//# sourceMappingURL=preview-send-pill.js.map