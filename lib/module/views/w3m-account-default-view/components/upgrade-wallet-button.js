import { Animated, Pressable, StyleSheet } from 'react-native';
import { BorderRadius, FlexView, Icon, IconBox, Spacing, Text, useTheme, useAnimatedValue } from '@reown/appkit-ui-react-native';
const AnimatedPressable = Animated.createAnimatedComponent(Pressable);
export function UpgradeWalletButton({
  style,
  onPress
}) {
  const Theme = useTheme();
  const {
    animatedValue,
    setStartValue,
    setEndValue
  } = useAnimatedValue(Theme['accent-glass-010'], Theme['accent-glass-020']);
  return /*#__PURE__*/React.createElement(AnimatedPressable, {
    onPress: onPress,
    onPressIn: setEndValue,
    onPressOut: setStartValue,
    style: [styles.container, {
      backgroundColor: animatedValue
    }, style]
  }, /*#__PURE__*/React.createElement(IconBox, {
    icon: "wallet",
    size: "lg",
    background: true,
    iconColor: "accent-100"
  }), /*#__PURE__*/React.createElement(FlexView, {
    flexGrow: 1,
    margin: ['0', 's', '0', 's']
  }, /*#__PURE__*/React.createElement(Text, {
    style: styles.upgradeText,
    color: "fg-100"
  }, "Upgrade your wallet"), /*#__PURE__*/React.createElement(Text, {
    variant: "small-400",
    color: "fg-150"
  }, "Transition to a self-custodial wallet")), /*#__PURE__*/React.createElement(Icon, {
    name: "chevronRight",
    size: "md",
    color: "fg-150",
    style: styles.chevron
  }));
}
const styles = StyleSheet.create({
  container: {
    height: 75,
    borderRadius: BorderRadius.s,
    backgroundColor: 'red',
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: Spacing.s
  },
  textContainer: {
    marginHorizontal: Spacing.m
  },
  upgradeText: {
    marginBottom: Spacing['3xs']
  },
  chevron: {
    marginRight: Spacing['2xs']
  }
});
//# sourceMappingURL=upgrade-wallet-button.js.map