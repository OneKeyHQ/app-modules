"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.Snackbar = Snackbar;
var _valtio = require("valtio");
var _react = require("react");
var _reactNative = require("react-native");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _styles = _interopRequireDefault(require("./styles"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
const getIcon = variant => {
  if (variant === 'loading') return 'loading';
  return variant === 'success' ? 'checkmark' : 'close';
};
function Snackbar() {
  const {
    open,
    message,
    variant,
    long
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.SnackController.state);
  const componentOpacity = (0, _react.useMemo)(() => new _reactNative.Animated.Value(0), []);
  (0, _react.useEffect)(() => {
    if (open) {
      _reactNative.Animated.timing(componentOpacity, {
        toValue: 1,
        duration: 150,
        useNativeDriver: true
      }).start();
      setTimeout(() => {
        _reactNative.Animated.timing(componentOpacity, {
          toValue: 0,
          duration: 300,
          useNativeDriver: true
        }).start(() => {
          _appkitCoreReactNative.SnackController.hide();
        });
      }, long ? 15000 : 2200);
    }
  }, [open, long, componentOpacity]);
  return /*#__PURE__*/React.createElement(_appkitUiReactNative.Snackbar, {
    message: message,
    icon: getIcon(variant),
    iconColor: variant === 'success' ? 'success-100' : 'error-100',
    style: [_styles.default.container, {
      opacity: componentOpacity
    }]
  });
}
//# sourceMappingURL=index.js.map