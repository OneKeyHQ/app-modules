import { NumberUtil } from '@reown/appkit-common-react-native';
import { FlexView, Image, Spacing, Text, Tag, useTheme, BorderRadius, Icon, Pressable } from '@reown/appkit-ui-react-native';
import { StyleSheet } from 'react-native';
export const ITEM_HEIGHT = 64;
export function Quote({
  item,
  logoURL,
  onQuotePress,
  selected,
  tagText,
  testID
}) {
  const Theme = useTheme();
  return /*#__PURE__*/React.createElement(Pressable, {
    style: [styles.container, selected && {
      borderColor: Theme['accent-100']
    }],
    onPress: () => onQuotePress(item),
    testID: testID
  }, /*#__PURE__*/React.createElement(FlexView, {
    justifyContent: "space-between",
    alignItems: "center",
    flexDirection: "row",
    padding: ['s', 's', 's', 's']
  }, /*#__PURE__*/React.createElement(FlexView, {
    flexDirection: "row",
    alignItems: "center"
  }, logoURL ? /*#__PURE__*/React.createElement(Image, {
    source: logoURL,
    style: styles.logo
  }) : /*#__PURE__*/React.createElement(FlexView, {
    style: [styles.logo, {
      backgroundColor: Theme['gray-glass-005']
    }],
    justifyContent: "center",
    alignItems: "center"
  }), /*#__PURE__*/React.createElement(FlexView, {
    flexDirection: "column"
  }, /*#__PURE__*/React.createElement(FlexView, {
    flexDirection: "row",
    alignItems: "center",
    margin: ['0', '0', '4xs', '0']
  }, /*#__PURE__*/React.createElement(Text, {
    variant: "paragraph-500",
    style: styles.providerText
  }, item.serviceProvider?.toLowerCase()), tagText && /*#__PURE__*/React.createElement(Tag, {
    variant: "main",
    style: styles.tag
  }, tagText)), /*#__PURE__*/React.createElement(Text, {
    variant: "tiny-500"
  }, NumberUtil.roundNumber(item.destinationAmount, 6, 5), ' ', item.destinationCurrencyCode?.split('_')[0]))), selected && /*#__PURE__*/React.createElement(Icon, {
    name: "checkmark",
    color: "accent-100"
  })));
}
const styles = StyleSheet.create({
  container: {
    borderRadius: BorderRadius.xs,
    borderWidth: 1,
    borderColor: 'transparent',
    height: ITEM_HEIGHT,
    justifyContent: 'center'
  },
  logo: {
    height: 40,
    width: 40,
    borderRadius: BorderRadius['3xs'],
    marginRight: Spacing.xs
  },
  providerText: {
    textTransform: 'capitalize'
  },
  tag: {
    padding: Spacing['3xs'],
    marginLeft: Spacing['2xs']
  }
});
//# sourceMappingURL=Quote.js.map