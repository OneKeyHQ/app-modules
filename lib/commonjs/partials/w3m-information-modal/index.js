"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.InformationModal = InformationModal;
var _reactNativeModal = _interopRequireDefault(require("react-native-modal"));
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _styles = _interopRequireDefault(require("./styles"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
function InformationModal({
  iconName,
  title,
  description,
  visible,
  onClose
}) {
  const Theme = (0, _appkitUiReactNative.useTheme)();
  return /*#__PURE__*/React.createElement(_reactNativeModal.default, {
    isVisible: visible,
    useNativeDriver: true,
    useNativeDriverForBackdrop: true,
    onBackdropPress: onClose,
    onDismiss: onClose,
    style: _styles.default.modal
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    style: [_styles.default.content, {
      backgroundColor: Theme['bg-200']
    }],
    padding: "2xl"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.IconBox, {
    icon: iconName,
    size: "lg",
    background: true
  }), !!title && /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "paragraph-500",
    style: _styles.default.title
  }, title), !!description && /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-400",
    color: "fg-150",
    center: true
  }, description), /*#__PURE__*/React.createElement(_appkitUiReactNative.Button, {
    onPress: onClose,
    variant: "fill",
    style: _styles.default.button
  }, "Got it")));
}
//# sourceMappingURL=index.js.map