import { StyleSheet } from 'react-native';
import { IconBox, Text, FlexView, Spacing, Button } from '@reown/appkit-ui-react-native';
export function Placeholder({
  icon,
  iconColor = 'fg-175',
  title,
  description,
  style,
  actionPress,
  actionTitle,
  actionIcon
}) {
  return /*#__PURE__*/React.createElement(FlexView, {
    alignItems: "center",
    justifyContent: "center",
    style: [styles.container, style]
  }, icon && /*#__PURE__*/React.createElement(IconBox, {
    icon: icon,
    size: "xl",
    iconColor: iconColor,
    background: true,
    style: styles.icon
  }), title && /*#__PURE__*/React.createElement(Text, {
    variant: "paragraph-500",
    style: styles.title
  }, title), description && /*#__PURE__*/React.createElement(Text, {
    variant: "small-400",
    color: "fg-200",
    center: true
  }, description), actionPress && /*#__PURE__*/React.createElement(Button, {
    style: styles.button,
    iconLeft: actionIcon,
    size: "sm",
    variant: "accent",
    onPress: actionPress
  }, actionTitle ?? ''));
}
const styles = StyleSheet.create({
  container: {
    flex: 1,
    minHeight: 200
  },
  icon: {
    marginBottom: Spacing.l
  },
  title: {
    marginBottom: Spacing['2xs']
  },
  button: {
    marginTop: Spacing.m
  }
});
//# sourceMappingURL=index.js.map