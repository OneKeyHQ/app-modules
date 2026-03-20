import { FlexView, Text, Shimmer } from '@reown/appkit-ui-react-native';
import { Dimensions, ScrollView } from 'react-native';
import { Header } from './Header';
import styles from '../styles';
export function LoadingView() {
  const windowWidth = Dimensions.get('window').width;
  return /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(Header, {
    onSettingsPress: () => {}
  }), /*#__PURE__*/React.createElement(ScrollView, {
    bounces: false,
    testID: "onramp-loading-view"
  }, /*#__PURE__*/React.createElement(FlexView, {
    padding: ['s', 'l', '4xl', 'l']
  }, /*#__PURE__*/React.createElement(FlexView, {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between"
  }, /*#__PURE__*/React.createElement(Text, {
    variant: "small-400",
    color: "fg-150"
  }, "You Buy"), /*#__PURE__*/React.createElement(Shimmer, {
    width: 100,
    height: 40,
    borderRadius: 20
  })), /*#__PURE__*/React.createElement(FlexView, {
    margin: ['m', '0', 'm', '0']
  }, /*#__PURE__*/React.createElement(Shimmer, {
    width: "100%",
    height: 323,
    borderRadius: 16
  })), /*#__PURE__*/React.createElement(Shimmer, {
    width: "100%",
    height: 64,
    borderRadius: 16,
    style: styles.paymentButtonMock
  }), /*#__PURE__*/React.createElement(FlexView, {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    margin: ['m', '0', '0', '0']
  }, /*#__PURE__*/React.createElement(Shimmer, {
    width: windowWidth * 0.2,
    height: 48,
    borderRadius: 16
  }), /*#__PURE__*/React.createElement(Shimmer, {
    width: windowWidth * 0.68,
    height: 48,
    borderRadius: 16
  })))));
}
//# sourceMappingURL=LoadingView.js.map