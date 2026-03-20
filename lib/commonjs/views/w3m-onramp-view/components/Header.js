"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.Header = Header;
var _reactNative = require("react-native");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
function Header({
  onSettingsPress
}) {
  const handleGoBack = () => {
    if (_appkitCoreReactNative.RouterController.state.history.length > 1) {
      _appkitCoreReactNative.RouterController.goBack();
    } else {
      _appkitCoreReactNative.ModalController.close();
    }
  };
  return /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    justifyContent: "space-between",
    flexDirection: "row",
    alignItems: "center",
    padding: "l"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.IconLink, {
    icon: "chevronLeft",
    size: "md",
    onPress: handleGoBack,
    testID: "button-back",
    style: styles.icon
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "paragraph-600",
    numberOfLines: 1,
    testID: "header-text"
  }, "Buy crypto"), /*#__PURE__*/React.createElement(_appkitUiReactNative.IconLink, {
    icon: "settings",
    size: "lg",
    onPress: onSettingsPress,
    style: styles.icon,
    testID: "button-onramp-settings"
  }));
}
const styles = _reactNative.StyleSheet.create({
  icon: {
    height: 40,
    width: 40
  }
});
//# sourceMappingURL=Header.js.map