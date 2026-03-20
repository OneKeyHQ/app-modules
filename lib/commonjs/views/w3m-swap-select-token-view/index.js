"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.SwapSelectTokenView = SwapSelectTokenView;
var _react = require("react");
var _valtio = require("valtio");
var _reactNative = require("react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _useCustomDimensions = require("../../hooks/useCustomDimensions");
var _w3mPlaceholder = require("../../partials/w3m-placeholder");
var _styles = _interopRequireDefault(require("./styles"));
var _utils = require("./utils");
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
function SwapSelectTokenView() {
  const {
    padding
  } = (0, _useCustomDimensions.useCustomDimensions)();
  const Theme = (0, _appkitUiReactNative.useTheme)();
  const {
    caipNetwork
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.NetworkController.state);
  const {
    sourceToken,
    suggestedTokens
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.SwapController.state);
  const {
    networkImages
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.AssetController.state);
  const networkImage = _appkitCoreReactNative.AssetUtil.getNetworkImage(caipNetwork, networkImages);
  const [tokenSearch, setTokenSearch] = (0, _react.useState)('');
  const isSourceToken = _appkitCoreReactNative.RouterController.state.data?.swapTarget === 'sourceToken';
  const [filteredTokens, setFilteredTokens] = (0, _react.useState)((0, _utils.createSections)(isSourceToken, tokenSearch));
  const suggestedList = suggestedTokens?.filter(token => token.address !== _appkitCoreReactNative.SwapController.state.sourceToken?.address).slice(0, 8);
  const onSearchChange = value => {
    setTokenSearch(value);
    setFilteredTokens((0, _utils.createSections)(isSourceToken, value));
  };
  const onTokenPress = token => {
    if (isSourceToken) {
      _appkitCoreReactNative.SwapController.setSourceToken(token);
    } else {
      _appkitCoreReactNative.SwapController.setToToken(token);
      if (_appkitCoreReactNative.SwapController.state.sourceToken && _appkitCoreReactNative.SwapController.state.sourceTokenAmount) {
        _appkitCoreReactNative.SwapController.swapTokens();
      }
    }
    _appkitCoreReactNative.RouterController.goBack();
  };
  return /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    margin: ['l', '0', '2xl', '0'],
    style: [_styles.default.container, {
      paddingHorizontal: padding
    }]
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, null, /*#__PURE__*/React.createElement(_appkitUiReactNative.InputText, {
    value: tokenSearch,
    icon: "search",
    placeholder: "Search token",
    onChangeText: onSearchChange,
    clearButtonMode: "while-editing",
    style: _styles.default.input
  }), !isSourceToken && /*#__PURE__*/React.createElement(_reactNative.ScrollView, {
    horizontal: true,
    showsHorizontalScrollIndicator: false,
    bounces: false,
    fadingEdgeLength: 20,
    style: _styles.default.suggestedList,
    contentContainerStyle: _styles.default.suggestedListContent
  }, suggestedList?.map((token, index) => /*#__PURE__*/React.createElement(_appkitUiReactNative.TokenButton, {
    key: token.name,
    text: token.symbol,
    imageUrl: token.logoUri,
    onPress: () => onTokenPress(token),
    style: index !== suggestedList.length - 1 ? _styles.default.suggestedToken : undefined
  })))), /*#__PURE__*/React.createElement(_appkitUiReactNative.Separator, {
    style: _styles.default.suggestedSeparator,
    color: "gray-glass-020"
  }), /*#__PURE__*/React.createElement(_reactNative.SectionList, {
    sections: filteredTokens,
    bounces: false,
    fadingEdgeLength: 20,
    contentContainerStyle: _styles.default.tokenList,
    renderSectionHeader: ({
      section: {
        title
      }
    }) => /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
      variant: "paragraph-500",
      color: "fg-200",
      style: [{
        backgroundColor: Theme['bg-100']
      }, _styles.default.title]
    }, title),
    ListEmptyComponent: /*#__PURE__*/React.createElement(_w3mPlaceholder.Placeholder, {
      icon: "coinPlaceholder",
      title: "No tokens found",
      description: "Your tokens will appear here"
    }),
    getItemLayout: (_, index) => ({
      length: _appkitUiReactNative.ListTokenTotalHeight,
      offset: _appkitUiReactNative.ListTokenTotalHeight * index,
      index
    }),
    renderItem: ({
      item
    }) => /*#__PURE__*/React.createElement(_appkitUiReactNative.ListToken, {
      key: item.name,
      name: item.name,
      imageSrc: item.logoUri,
      networkSrc: networkImage,
      value: item.value,
      amount: item.quantity.numeric,
      currency: item.symbol,
      onPress: () => onTokenPress(item),
      disabled: item.address === sourceToken?.address
    })
  }));
}
//# sourceMappingURL=index.js.map