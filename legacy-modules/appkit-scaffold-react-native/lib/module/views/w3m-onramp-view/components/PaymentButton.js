import { BorderRadius, FlexView, Icon, Image, LoadingSpinner, Pressable, Spacing, Text, useTheme } from '@reown/appkit-ui-react-native';
import { StyleSheet, View } from 'react-native';
function PaymentButton({
  disabled,
  loading,
  title,
  subtitle,
  paymentLogo,
  providerLogo,
  onPress,
  testID
}) {
  const Theme = useTheme();
  const backgroundColor = Theme['gray-glass-005'];
  return /*#__PURE__*/React.createElement(Pressable, {
    disabled: disabled || loading,
    onPress: onPress,
    style: styles.pressable,
    testID: testID
  }, /*#__PURE__*/React.createElement(FlexView, {
    style: [styles.container, {
      backgroundColor
    }],
    alignItems: "center",
    justifyContent: "space-between",
    flexDirection: "row"
  }, /*#__PURE__*/React.createElement(FlexView, {
    alignItems: "center",
    justifyContent: "center",
    style: [styles.iconContainer, {
      backgroundColor: Theme['bg-300']
    }]
  }, paymentLogo ? /*#__PURE__*/React.createElement(Image, {
    source: paymentLogo,
    style: styles.paymentLogo,
    resizeMethod: "resize",
    resizeMode: "contain"
  }) : /*#__PURE__*/React.createElement(Icon, {
    name: "card",
    size: "lg"
  })), /*#__PURE__*/React.createElement(FlexView, {
    flexGrow: 1,
    flexDirection: "column",
    alignItems: "flex-start",
    margin: ['0', '0', '0', 's']
  }, /*#__PURE__*/React.createElement(Text, {
    variant: "paragraph-400",
    color: "fg-100"
  }, title), subtitle && /*#__PURE__*/React.createElement(FlexView, {
    flexDirection: "row",
    alignItems: "center",
    margin: ['4xs', '0', '0', '0']
  }, providerLogo && /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(Text, {
    variant: "small-400",
    color: "fg-150"
  }, "via"), /*#__PURE__*/React.createElement(Image, {
    source: providerLogo,
    style: styles.providerLogo,
    resizeMethod: "resize",
    resizeMode: "contain"
  })), /*#__PURE__*/React.createElement(Text, {
    variant: "small-400",
    color: "fg-150"
  }, subtitle))), loading ? /*#__PURE__*/React.createElement(LoadingSpinner, {
    size: "md",
    color: "fg-200",
    style: styles.rightIcon
  }) : disabled ? /*#__PURE__*/React.createElement(View, null) : /*#__PURE__*/React.createElement(Icon, {
    name: "chevronRight",
    size: "md",
    color: "fg-200",
    style: styles.rightIcon
  })));
}
const styles = StyleSheet.create({
  pressable: {
    borderRadius: BorderRadius.xs
  },
  container: {
    padding: Spacing.s,
    borderRadius: BorderRadius.xs
  },
  iconContainer: {
    height: 40,
    width: 40,
    borderRadius: BorderRadius['3xs']
  },
  paymentLogo: {
    height: 24,
    width: 24
  },
  providerLogo: {
    height: 16,
    width: 16,
    marginHorizontal: Spacing['4xs'],
    borderRadius: BorderRadius['5xs']
  },
  rightIcon: {
    marginRight: Spacing.xs
  }
});
export default PaymentButton;
//# sourceMappingURL=PaymentButton.js.map