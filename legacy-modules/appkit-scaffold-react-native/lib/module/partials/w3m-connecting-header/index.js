import { FlexView, Tabs } from '@reown/appkit-ui-react-native';
import { StyleSheet } from 'react-native';
export function ConnectingHeader({
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
  return /*#__PURE__*/React.createElement(FlexView, {
    alignItems: "center",
    padding: ['xs', '0', '0', '0']
  }, /*#__PURE__*/React.createElement(Tabs, {
    tabs: tabs,
    onTabChange: onTabChange,
    style: styles.tab
  }));
}
const styles = StyleSheet.create({
  tab: {
    maxWidth: '50%'
  }
});
//# sourceMappingURL=index.js.map