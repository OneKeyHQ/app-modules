"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
var _exportNames = {
  ConnectingBody: true
};
exports.ConnectingBody = ConnectingBody;
var _reactNative = require("react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _utils = require("./utils");
Object.keys(_utils).forEach(function (key) {
  if (key === "default" || key === "__esModule") return;
  if (Object.prototype.hasOwnProperty.call(_exportNames, key)) return;
  if (key in exports && exports[key] === _utils[key]) return;
  Object.defineProperty(exports, key, {
    enumerable: true,
    get: function () {
      return _utils[key];
    }
  });
});
function ConnectingBody({
  title,
  description
}) {
  return /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    padding: ['3xs', '2xl', '0', '2xl'],
    alignItems: "center",
    style: styles.textContainer
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "paragraph-500"
  }, title), description && /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    center: true,
    variant: "small-400",
    color: "fg-200",
    style: styles.descriptionText
  }, description));
}
const styles = _reactNative.StyleSheet.create({
  textContainer: {
    marginVertical: _appkitUiReactNative.Spacing.xs
  },
  descriptionText: {
    marginTop: _appkitUiReactNative.Spacing.xs,
    marginHorizontal: _appkitUiReactNative.Spacing['3xl']
  }
});
//# sourceMappingURL=index.js.map