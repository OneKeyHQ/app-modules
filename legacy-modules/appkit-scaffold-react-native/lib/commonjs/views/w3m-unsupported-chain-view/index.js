"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.UnsupportedChainView = UnsupportedChainView;
var _valtio = require("valtio");
var _react = require("react");
var _reactNative = require("react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _styles = _interopRequireDefault(require("./styles"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
function UnsupportedChainView() {
  const {
    caipNetwork,
    supportsAllNetworks,
    approvedCaipNetworkIds,
    requestedCaipNetworks
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.NetworkController.state);
  const {
    networkImages
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.AssetController.state);
  const [disconnecting, setDisconnecting] = (0, _react.useState)(false);
  const networks = _appkitCoreReactNative.CoreHelperUtil.sortNetworks(approvedCaipNetworkIds, requestedCaipNetworks);
  const imageHeaders = _appkitCoreReactNative.ApiController._getApiHeaders();
  const onNetworkPress = async network => {
    const result = await _appkitCoreReactNative.NetworkUtil.handleNetworkSwitch(network);
    if (result?.type === 'SWITCH_NETWORK') {
      _appkitCoreReactNative.EventsController.sendEvent({
        type: 'track',
        event: 'SWITCH_NETWORK',
        properties: {
          network: network.id
        }
      });
    }
  };
  const onDisconnect = async () => {
    setDisconnecting(true);
    await _appkitCoreReactNative.ConnectionUtil.disconnect();
    setDisconnecting(false);
  };
  return /*#__PURE__*/React.createElement(_reactNative.FlatList, {
    data: networks,
    fadingEdgeLength: 20,
    ListHeaderComponentStyle: _styles.default.header,
    ListHeaderComponent: /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
      variant: "small-400",
      color: "fg-200",
      center: true
    }, "The swap feature doesn't support your current network. Switch to an available option to continue."),
    contentContainerStyle: _styles.default.contentContainer,
    renderItem: ({
      item
    }) => /*#__PURE__*/React.createElement(_appkitUiReactNative.ListItem, {
      key: item.id,
      icon: "networkPlaceholder",
      iconBackgroundColor: "gray-glass-010",
      imageSrc: _appkitCoreReactNative.AssetUtil.getNetworkImage(item, networkImages),
      imageHeaders: imageHeaders,
      onPress: () => onNetworkPress(item),
      testID: "button-network",
      style: _styles.default.networkItem,
      contentStyle: _styles.default.networkItemContent,
      disabled: !supportsAllNetworks && !approvedCaipNetworkIds?.includes(item.id)
    }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
      numberOfLines: 1,
      color: "fg-100"
    }, item.name ?? 'Unknown'), item.id === caipNetwork?.id && /*#__PURE__*/React.createElement(_appkitUiReactNative.Icon, {
      name: "checkmark",
      color: "accent-100"
    })),
    ListFooterComponent: /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(_appkitUiReactNative.Separator, {
      text: "or",
      style: _styles.default.separator
    }), /*#__PURE__*/React.createElement(_appkitUiReactNative.ListItem, {
      icon: "disconnect",
      onPress: onDisconnect,
      loading: disconnecting,
      iconBackgroundColor: "gray-glass-010",
      testID: "button-disconnect"
    }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
      color: "fg-200"
    }, "Disconnect")))
  });
}
//# sourceMappingURL=index.js.map