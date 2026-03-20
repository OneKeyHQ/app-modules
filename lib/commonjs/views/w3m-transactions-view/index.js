"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.TransactionsView = TransactionsView;
var _reactNative = require("react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _w3mAccountActivity = require("../../partials/w3m-account-activity");
function TransactionsView() {
  return /*#__PURE__*/React.createElement(_w3mAccountActivity.AccountActivity, {
    style: styles.container
  });
}
const styles = _reactNative.StyleSheet.create({
  container: {
    paddingHorizontal: _appkitUiReactNative.Spacing.l,
    marginTop: _appkitUiReactNative.Spacing.s
  }
});
//# sourceMappingURL=index.js.map