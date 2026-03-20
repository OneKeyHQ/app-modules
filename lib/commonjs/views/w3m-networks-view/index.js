"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.NetworksView = NetworksView;
var _valtio = require("valtio");
var _reactNative = require("react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _useCustomDimensions = require("../../hooks/useCustomDimensions");
var _styles = _interopRequireDefault(require("./styles"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
function NetworksView() {
  const {
    caipNetwork,
    requestedCaipNetworks,
    approvedCaipNetworkIds,
    supportsAllNetworks
  } = _appkitCoreReactNative.NetworkController.state;
  const {
    networkImages
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.AssetController.state);
  const imageHeaders = _appkitCoreReactNative.ApiController._getApiHeaders();
  const {
    maxWidth: width,
    padding
  } = (0, _useCustomDimensions.useCustomDimensions)();
  const numColumns = 4;
  const usableWidth = width - _appkitUiReactNative.Spacing.xs * 2 - _appkitUiReactNative.Spacing['4xs'];
  const itemWidth = Math.abs(Math.trunc(usableWidth / numColumns));
  const itemGap = Math.abs(Math.trunc((usableWidth - numColumns * _appkitUiReactNative.CardSelectWidth) / numColumns) / 2);
  const onHelpPress = () => {
    _appkitCoreReactNative.RouterController.push('WhatIsANetwork');
    _appkitCoreReactNative.EventsController.sendEvent({
      type: 'track',
      event: 'CLICK_NETWORK_HELP'
    });
  };
  const networksTemplate = () => {
    const networks = _appkitCoreReactNative.CoreHelperUtil.sortNetworks(approvedCaipNetworkIds, requestedCaipNetworks);
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
    return networks.map(network => /*#__PURE__*/React.createElement(_reactNative.View, {
      key: network.id,
      style: [_styles.default.itemContainer, {
        width: itemWidth,
        marginVertical: itemGap
      }]
    }, /*#__PURE__*/React.createElement(_appkitUiReactNative.CardSelect, {
      testID: `w3m-network-switch-${network.name ?? network.id}`,
      name: network.name ?? 'Unknown',
      type: "network",
      imageSrc: _appkitCoreReactNative.AssetUtil.getNetworkImage(network, networkImages),
      imageHeaders: imageHeaders,
      disabled: !supportsAllNetworks && !approvedCaipNetworkIds?.includes(network.id),
      selected: caipNetwork?.id === network.id,
      onPress: () => onNetworkPress(network)
    })));
  };
  return /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(_reactNative.ScrollView, {
    bounces: false,
    fadingEdgeLength: 20,
    style: {
      paddingHorizontal: padding
    }
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    flexWrap: "wrap",
    padding: ['xs', 'xs', 's', 'xs']
  }, networksTemplate())), /*#__PURE__*/React.createElement(_appkitUiReactNative.Separator, null), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    padding: ['s', 's', '3xl', 's'],
    alignItems: "center",
    alignSelf: "center",
    style: {
      width
    }
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-400",
    color: "fg-300",
    center: true
  }, "Your connected wallet may not support some of the networks available for this dApp"), /*#__PURE__*/React.createElement(_appkitUiReactNative.Link, {
    size: "sm",
    iconLeft: "helpCircle",
    onPress: onHelpPress,
    style: _styles.default.helpButton,
    testID: "what-is-a-network-button"
  }, "What is a network?")));
}
//# sourceMappingURL=index.js.map