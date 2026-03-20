"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.WalletSendSelectTokenView = WalletSendSelectTokenView;
var _react = require("react");
var _valtio = require("valtio");
var _reactNative = require("react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _useCustomDimensions = require("../../hooks/useCustomDimensions");
var _w3mPlaceholder = require("../../partials/w3m-placeholder");
var _styles = _interopRequireDefault(require("./styles"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
function WalletSendSelectTokenView() {
  const {
    padding
  } = (0, _useCustomDimensions.useCustomDimensions)();
  const {
    tokenBalance
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.AccountController.state);
  const {
    caipNetwork
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.NetworkController.state);
  const {
    token
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.SendController.state);
  const {
    networkImages
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.AssetController.state);
  const networkImage = _appkitCoreReactNative.AssetUtil.getNetworkImage(caipNetwork, networkImages);
  const [tokenSearch, setTokenSearch] = (0, _react.useState)('');
  const [filteredTokens, setFilteredTokens] = (0, _react.useState)(tokenBalance ?? []);
  const onSearchChange = value => {
    setTokenSearch(value);
    const filtered = _appkitCoreReactNative.AccountController.state.tokenBalance?.filter(_token => _token.name.toLowerCase().includes(value.toLowerCase()));
    setFilteredTokens(filtered ?? []);
  };
  const onTokenPress = _token => {
    _appkitCoreReactNative.SendController.setToken(_token);
    _appkitCoreReactNative.SendController.setTokenAmount(undefined);
    _appkitCoreReactNative.RouterController.goBack();
  };
  return /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    margin: ['l', '0', '2xl', '0'],
    style: [_styles.default.container, {
      paddingHorizontal: padding
    }]
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    margin: ['0', 'm', 'm', 'm']
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.InputText, {
    value: tokenSearch,
    icon: "search",
    placeholder: "Search token",
    onChangeText: onSearchChange,
    clearButtonMode: "while-editing"
  })), /*#__PURE__*/React.createElement(_reactNative.ScrollView, {
    bounces: false,
    fadingEdgeLength: 20,
    contentContainerStyle: _styles.default.tokenList
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "paragraph-500",
    color: "fg-200",
    style: _styles.default.title
  }, "Your tokens"), filteredTokens.length ? filteredTokens.map((_token, index) => /*#__PURE__*/React.createElement(_appkitUiReactNative.ListToken, {
    key: `${_token.name}${index}`,
    name: _token.name,
    imageSrc: _token.iconUrl,
    networkSrc: networkImage,
    value: _token.value,
    amount: _token.quantity.numeric,
    currency: _token.symbol,
    onPress: () => onTokenPress(_token),
    disabled: _token.address === token?.address
  })) : /*#__PURE__*/React.createElement(_w3mPlaceholder.Placeholder, {
    icon: "coinPlaceholder",
    title: "No tokens found",
    description: "Your tokens will appear here"
  })));
}
//# sourceMappingURL=index.js.map