"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.LoadingView = LoadingView;
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _reactNative = require("react-native");
var _Header = require("./Header");
var _styles = _interopRequireDefault(require("../styles"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
function LoadingView() {
  const windowWidth = _reactNative.Dimensions.get('window').width;
  return /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(_Header.Header, {
    onSettingsPress: () => {}
  }), /*#__PURE__*/React.createElement(_reactNative.ScrollView, {
    bounces: false,
    testID: "onramp-loading-view"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    padding: ['s', 'l', '4xl', 'l']
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-400",
    color: "fg-150"
  }, "You Buy"), /*#__PURE__*/React.createElement(_appkitUiReactNative.Shimmer, {
    width: 100,
    height: 40,
    borderRadius: 20
  })), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    margin: ['m', '0', 'm', '0']
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Shimmer, {
    width: "100%",
    height: 323,
    borderRadius: 16
  })), /*#__PURE__*/React.createElement(_appkitUiReactNative.Shimmer, {
    width: "100%",
    height: 64,
    borderRadius: 16,
    style: _styles.default.paymentButtonMock
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    margin: ['m', '0', '0', '0']
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Shimmer, {
    width: windowWidth * 0.2,
    height: 48,
    borderRadius: 16
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.Shimmer, {
    width: windowWidth * 0.68,
    height: 48,
    borderRadius: 16
  })))));
}
//# sourceMappingURL=LoadingView.js.map