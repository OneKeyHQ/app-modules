import { Pressable, FlexView, Spacing, Text, Icon, BorderRadius } from '@reown/appkit-ui-react-native';
import { StyleSheet } from 'react-native';
import { SvgUri } from 'react-native-svg';
export const ITEM_HEIGHT = 60;
export function Country({
  onPress,
  item,
  selected
}) {
  const handlePress = () => {
    onPress(item);
  };
  return /*#__PURE__*/React.createElement(Pressable, {
    onPress: handlePress,
    style: styles.container,
    backgroundColor: "transparent",
    testID: `country-item-${item.countryCode}`
  }, /*#__PURE__*/React.createElement(FlexView, {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "flex-start",
    padding: "s"
  }, /*#__PURE__*/React.createElement(FlexView, {
    style: styles.imageContainer
  }, item.flagImageUrl && SvgUri && /*#__PURE__*/React.createElement(SvgUri, {
    uri: item.flagImageUrl,
    width: 36,
    height: 36
  })), /*#__PURE__*/React.createElement(FlexView, {
    style: styles.textContainer
  }, /*#__PURE__*/React.createElement(Text, {
    variant: "paragraph-400",
    color: "fg-100",
    numberOfLines: 1,
    ellipsizeMode: "tail"
  }, item.name), /*#__PURE__*/React.createElement(Text, {
    variant: "small-400",
    color: "fg-150"
  }, item.countryCode)), selected && /*#__PURE__*/React.createElement(Icon, {
    name: "checkmark",
    size: "md",
    color: "accent-100",
    style: styles.checkmark
  })));
}
const styles = StyleSheet.create({
  container: {
    borderRadius: BorderRadius.s,
    height: ITEM_HEIGHT,
    justifyContent: 'center'
  },
  imageContainer: {
    borderRadius: BorderRadius.full,
    overflow: 'hidden',
    marginRight: Spacing.xs
  },
  textContainer: {
    flex: 1
  },
  checkmark: {
    marginRight: Spacing['2xs']
  }
});
//# sourceMappingURL=Country.js.map