"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.PreviewSendDetails = PreviewSendDetails;
var _valtio = require("valtio");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _reactNative = require("react-native");
function PreviewSendDetails({
  address,
  name,
  caipNetwork,
  networkFee,
  style
}) {
  const Theme = (0, _appkitUiReactNative.useTheme)();
  const {
    networkImages
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.AssetController.state);
  const formattedName = _appkitUiReactNative.UiUtil.getTruncateString({
    string: name ?? '',
    charsStart: 20,
    charsEnd: 0,
    truncate: 'end'
  });
  const formattedAddress = _appkitUiReactNative.UiUtil.getTruncateString({
    string: address || '',
    charsStart: 6,
    charsEnd: 8,
    truncate: 'middle'
  });
  const networkImage = _appkitCoreReactNative.AssetUtil.getNetworkImage(caipNetwork, networkImages);
  return /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    style: [styles.container, {
      backgroundColor: Theme['gray-glass-002']
    }, style],
    padding: "s"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-400",
    color: "fg-200",
    style: styles.title
  }, "Details"), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    style: [styles.item, {
      backgroundColor: Theme['gray-glass-002']
    }]
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-400",
    color: "fg-150"
  }, "Network cost"), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-400",
    color: "fg-100"
  }, "$", _appkitUiReactNative.UiUtil.formatNumberToLocalString(networkFee, 2))), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    style: [styles.item, {
      backgroundColor: Theme['gray-glass-002']
    }]
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-400",
    color: "fg-150"
  }, formattedName || 'Address'), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-400",
    color: "fg-100"
  }, formattedAddress)), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    style: [styles.item, {
      backgroundColor: Theme['gray-glass-002']
    }]
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-400",
    color: "fg-150"
  }, "Network"), /*#__PURE__*/React.createElement(_appkitUiReactNative.NetworkImage, {
    imageSrc: networkImage,
    size: "xs"
  })));
}
const styles = _reactNative.StyleSheet.create({
  container: {
    justifyContent: 'center',
    borderRadius: _appkitUiReactNative.BorderRadius.xxs
  },
  title: {
    marginBottom: _appkitUiReactNative.Spacing.xs
  },
  item: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: _appkitUiReactNative.Spacing.s,
    borderRadius: _appkitUiReactNative.BorderRadius.xxs,
    marginTop: _appkitUiReactNative.Spacing['2xs']
  },
  networkImage: {
    height: 24,
    width: 24,
    borderRadius: _appkitUiReactNative.BorderRadius.full
  }
});
//# sourceMappingURL=preview-send-details.js.map