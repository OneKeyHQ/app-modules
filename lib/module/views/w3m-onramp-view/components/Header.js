import { StyleSheet } from 'react-native';
import { ModalController, RouterController } from '@reown/appkit-core-react-native';
import { IconLink, Text, FlexView } from '@reown/appkit-ui-react-native';
export function Header({
  onSettingsPress
}) {
  const handleGoBack = () => {
    if (RouterController.state.history.length > 1) {
      RouterController.goBack();
    } else {
      ModalController.close();
    }
  };
  return /*#__PURE__*/React.createElement(FlexView, {
    justifyContent: "space-between",
    flexDirection: "row",
    alignItems: "center",
    padding: "l"
  }, /*#__PURE__*/React.createElement(IconLink, {
    icon: "chevronLeft",
    size: "md",
    onPress: handleGoBack,
    testID: "button-back",
    style: styles.icon
  }), /*#__PURE__*/React.createElement(Text, {
    variant: "paragraph-600",
    numberOfLines: 1,
    testID: "header-text"
  }, "Buy crypto"), /*#__PURE__*/React.createElement(IconLink, {
    icon: "settings",
    size: "lg",
    onPress: onSettingsPress,
    style: styles.icon,
    testID: "button-onramp-settings"
  }));
}
const styles = StyleSheet.create({
  icon: {
    height: 40,
    width: 40
  }
});
//# sourceMappingURL=Header.js.map