"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.ConnectingHeader = ConnectingHeader;
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _reactNative = require("react-native");
function ConnectingHeader({
  platforms,
  onSelectPlatform
}) {
  const generateTabs = () => {
    const tabs = platforms.map(platform => {
      if (platform === 'mobile') {
        return {
          label: 'Mobile',
          icon: 'mobile',
          platform: 'mobile'
        };
      } else if (platform === 'web') {
        return {
          label: 'Web',
          icon: 'browser',
          platform: 'web'
        };
      } else {
        return undefined;
      }
    }).filter(Boolean);
    return tabs;
  };
  const onTabChange = index => {
    const platform = platforms[index];
    if (platform) {
      onSelectPlatform(platform);
    }
  };
  const tabs = generateTabs();
  return /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    alignItems: "center",
    padding: ['xs', '0', '0', '0']
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Tabs, {
    tabs: tabs,
    onTabChange: onTabChange,
    style: styles.tab
  }));
}
const styles = _reactNative.StyleSheet.create({
  tab: {
    maxWidth: '50%'
  }
});
//# sourceMappingURL=index.js.map