"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.SelectorModal = SelectorModal;
var _valtio = require("valtio");
var _reactNativeModal = _interopRequireDefault(require("react-native-modal"));
var _reactNative = require("react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _styles = _interopRequireDefault(require("./styles"));
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
const SEPARATOR_HEIGHT = _appkitUiReactNative.Spacing.s;
function SelectorModal({
  title,
  visible,
  onClose,
  items,
  selectedItem,
  renderItem,
  onSearch,
  searchPlaceholder,
  keyExtractor,
  itemHeight,
  showNetwork
}) {
  const Theme = (0, _appkitUiReactNative.useTheme)();
  const {
    caipNetwork
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.NetworkController.state);
  const {
    networkImages
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.AssetController.state);
  const networkImage = _appkitCoreReactNative.AssetUtil.getNetworkImage(caipNetwork, networkImages);
  const renderSeparator = () => {
    return /*#__PURE__*/React.createElement(_reactNative.View, {
      style: {
        height: SEPARATOR_HEIGHT
      }
    });
  };
  return /*#__PURE__*/React.createElement(_reactNativeModal.default, {
    isVisible: visible,
    useNativeDriver: true,
    useNativeDriverForBackdrop: true,
    statusBarTranslucent: true,
    hideModalContentWhileAnimating: true,
    onBackdropPress: onClose,
    onDismiss: onClose,
    style: _styles.default.modal
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    style: [_styles.default.container, {
      backgroundColor: Theme['bg-100']
    }]
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    alignItems: "center",
    justifyContent: "space-between",
    flexDirection: "row",
    style: _styles.default.header
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.IconLink, {
    icon: "chevronLeft",
    onPress: onClose,
    testID: "selector-modal-button-back"
  }), !!title && /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "paragraph-600"
  }, title), showNetwork ? networkImage ? /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    alignItems: "center",
    justifyContent: "center",
    style: _styles.default.iconPlaceholder
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Image, {
    source: networkImage,
    style: _styles.default.networkImage
  })) : /*#__PURE__*/React.createElement(_appkitUiReactNative.IconBox, {
    style: _styles.default.iconPlaceholder,
    icon: "networkPlaceholder",
    background: true,
    iconColor: "fg-200",
    size: "sm"
  }) : /*#__PURE__*/React.createElement(_reactNative.View, {
    style: _styles.default.iconPlaceholder
  })), /*#__PURE__*/React.createElement(_appkitUiReactNative.SearchBar, {
    onChangeText: onSearch,
    style: _styles.default.searchBar,
    placeholder: searchPlaceholder
  }), selectedItem && /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    style: _styles.default.selectedContainer
  }, renderItem({
    item: selectedItem
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.Separator, {
    style: _styles.default.separator,
    color: "gray-glass-020"
  })), /*#__PURE__*/React.createElement(_reactNative.FlatList, {
    data: items,
    renderItem: renderItem,
    fadingEdgeLength: 20,
    contentContainerStyle: _styles.default.listContent,
    ItemSeparatorComponent: renderSeparator,
    keyExtractor: keyExtractor,
    getItemLayout: itemHeight ? (_, index) => ({
      length: itemHeight + SEPARATOR_HEIGHT,
      offset: (itemHeight + SEPARATOR_HEIGHT) * index,
      index
    }) : undefined
  })));
}
//# sourceMappingURL=index.js.map