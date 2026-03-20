import { Linking, ScrollView } from 'react-native';
import { Button, FlexView, Text, Visual } from '@reown/appkit-ui-react-native';
import { useCustomDimensions } from '../../hooks/useCustomDimensions';
import styles from './styles';
export function WhatIsANetworkView() {
  const {
    padding
  } = useCustomDimensions();
  const onLearnMorePress = () => {
    Linking.openURL('https://ethereum.org/en/developers/docs/networks/');
  };
  return /*#__PURE__*/React.createElement(ScrollView, {
    bounces: false,
    fadingEdgeLength: 20,
    style: {
      paddingHorizontal: padding
    }
  }, /*#__PURE__*/React.createElement(FlexView, {
    alignItems: "center",
    padding: ['l', '4xl', '3xl', '4xl']
  }, /*#__PURE__*/React.createElement(FlexView, {
    flexDirection: "row",
    padding: ['0', '0', 'xs', '0']
  }, /*#__PURE__*/React.createElement(Visual, {
    name: "network"
  }), /*#__PURE__*/React.createElement(Visual, {
    name: "layers",
    style: styles.visual
  }), /*#__PURE__*/React.createElement(Visual, {
    name: "system"
  })), /*#__PURE__*/React.createElement(Text, {
    variant: "paragraph-500",
    style: styles.text
  }, "The system\u2019s nuts and bolts"), /*#__PURE__*/React.createElement(Text, {
    variant: "small-500",
    color: "fg-200",
    center: true
  }, "A network is what brings the blockchain to life, as this technical infrastructure allows apps to access the ledger and smart contract services."), /*#__PURE__*/React.createElement(FlexView, {
    flexDirection: "row",
    padding: ['3xl', '0', 'xs', '0']
  }, /*#__PURE__*/React.createElement(Visual, {
    name: "noun"
  }), /*#__PURE__*/React.createElement(Visual, {
    name: "defiAlt",
    style: styles.visual
  }), /*#__PURE__*/React.createElement(Visual, {
    name: "dao"
  })), /*#__PURE__*/React.createElement(Text, {
    variant: "paragraph-500",
    style: styles.text
  }, "Designed for different uses"), /*#__PURE__*/React.createElement(Text, {
    variant: "small-500",
    color: "fg-200",
    center: true
  }, "Each network is designed differently, and may therefore suit certain apps and experiences."), /*#__PURE__*/React.createElement(Button, {
    size: "sm",
    iconRight: "externalLink",
    style: styles.learnButton,
    onPress: onLearnMorePress
  }, "Learn more")));
}
//# sourceMappingURL=index.js.map