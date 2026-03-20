import { Pressable, FlexView, Spacing, Text, useTheme, Icon, BorderRadius } from '@reown/appkit-ui-react-native';
import { StyleSheet, Image } from 'react-native';
export const ITEM_HEIGHT = 60;
export function Currency({
  onPress,
  item,
  selected,
  title,
  subtitle,
  testID
}) {
  const Theme = useTheme();
  const handlePress = () => {
    onPress(item);
  };
  return /*#__PURE__*/React.createElement(Pressable, {
    onPress: handlePress,
    style: styles.container,
    backgroundColor: "transparent",
    testID: testID
  }, /*#__PURE__*/React.createElement(FlexView, {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    padding: "xs"
  }, /*#__PURE__*/React.createElement(FlexView, {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "flex-start"
  }, /*#__PURE__*/React.createElement(Image, {
    source: {
      uri: item.symbolImageUrl
    },
    style: [styles.logo, {
      backgroundColor: Theme['inverse-100']
    }]
  }), /*#__PURE__*/React.createElement(FlexView, null, /*#__PURE__*/React.createElement(Text, {
    variant: "paragraph-500",
    color: "fg-100",
    numberOfLines: 1,
    ellipsizeMode: "tail"
  }, title), /*#__PURE__*/React.createElement(Text, {
    variant: "small-400",
    color: "fg-150"
  }, subtitle))), selected && /*#__PURE__*/React.createElement(Icon, {
    name: "checkmark",
    size: "md",
    color: "accent-100",
    style: styles.checkmark
  })));
}
const styles = StyleSheet.create({
  container: {
    justifyContent: 'center',
    height: ITEM_HEIGHT,
    borderRadius: BorderRadius.s
  },
  logo: {
    width: 36,
    height: 36,
    borderRadius: BorderRadius.full,
    marginRight: Spacing.xs
  },
  checkmark: {
    marginRight: Spacing['2xs']
  },
  selected: {
    borderWidth: 1
  },
  text: {
    flex: 1
  }
});
//# sourceMappingURL=Currency.js.map