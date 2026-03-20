import { StyleSheet } from 'react-native';
import { FlexView, Spacing, Text } from '@reown/appkit-ui-react-native';
export * from './utils';
export function ConnectingBody({
  title,
  description
}) {
  return /*#__PURE__*/React.createElement(FlexView, {
    padding: ['3xs', '2xl', '0', '2xl'],
    alignItems: "center",
    style: styles.textContainer
  }, /*#__PURE__*/React.createElement(Text, {
    variant: "paragraph-500"
  }, title), description && /*#__PURE__*/React.createElement(Text, {
    center: true,
    variant: "small-400",
    color: "fg-200",
    style: styles.descriptionText
  }, description));
}
const styles = StyleSheet.create({
  textContainer: {
    marginVertical: Spacing.xs
  },
  descriptionText: {
    marginTop: Spacing.xs,
    marginHorizontal: Spacing['3xl']
  }
});
//# sourceMappingURL=index.js.map