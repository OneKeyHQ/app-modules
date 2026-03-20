"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.OnRampLoadingView = OnRampLoadingView;
var _react = require("react");
var _valtio = require("valtio");
var _reactNative = require("react-native");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _appkitCommonReactNative = require("@reown/appkit-common-react-native");
var _useCustomDimensions = require("../../hooks/useCustomDimensions");
var _w3mConnectingBody = require("../../partials/w3m-connecting-body");
var _utils = require("./utils");
var _styles = _interopRequireDefault(require("./styles"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
function OnRampLoadingView() {
  const {
    maxWidth: width
  } = (0, _useCustomDimensions.useCustomDimensions)();
  const {
    error
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.OnRampController.state);
  const providerName = _appkitCommonReactNative.StringUtil.capitalize(_appkitCoreReactNative.OnRampController.state.selectedQuote?.serviceProvider.toLowerCase());
  const serviceProvideLogo = _appkitCoreReactNative.OnRampController.getServiceProviderImage(_appkitCoreReactNative.OnRampController.state.selectedQuote?.serviceProvider ?? '');
  const handleGoBack = () => {
    if (_appkitCoreReactNative.EventsController.state.data.event === 'BUY_SUBMITTED') {
      // Send event only if the onramp url was already created
      _appkitCoreReactNative.EventsController.sendEvent({
        type: 'track',
        event: 'BUY_CANCEL'
      });
    }
    _appkitCoreReactNative.RouterController.goBack();
  };
  const onConnect = (0, _react.useCallback)(async () => {
    if (_appkitCoreReactNative.OnRampController.state.selectedQuote) {
      _appkitCoreReactNative.OnRampController.clearError();
      const response = await _appkitCoreReactNative.OnRampController.generateWidget({
        quote: _appkitCoreReactNative.OnRampController.state.selectedQuote
      });
      if (response?.widgetUrl) {
        _reactNative.Linking.openURL(response.widgetUrl);
      }
    }
  }, []);
  (0, _react.useEffect)(() => {
    const unsubscribe = _reactNative.Linking.addEventListener('url', ({
      url
    }) => {
      const metadata = _appkitCoreReactNative.OptionsController.state.metadata;
      if (metadata?.redirect?.universal && url.startsWith(metadata?.redirect?.universal) || metadata?.redirect?.native && url.startsWith(metadata?.redirect?.native)) {
        const urlData = (0, _utils.parseOnRampRedirectUrl)(url);
        if (urlData) {
          _appkitCoreReactNative.EventsController.sendEvent({
            type: 'track',
            event: 'BUY_SUCCESS',
            properties: {
              asset: urlData.purchaseCurrency,
              network: urlData.network,
              amount: urlData.paymentAmount,
              currency: urlData.paymentCurrency,
              orderId: urlData.orderId
            }
          });
          _appkitCoreReactNative.RouterController.reset('OnRampTransaction', {
            onrampResult: urlData
          });
        } else {
          _appkitCoreReactNative.RouterController.reset('OnRampTransaction', {
            onrampResult: (0, _utils.createEmptyOnRampResult)()
          });
        }
      }
    });
    return () => unsubscribe.remove();
  }, []);
  (0, _react.useEffect)(() => {
    onConnect();
  }, [onConnect]);
  return /*#__PURE__*/React.createElement(_reactNative.ScrollView, {
    bounces: false,
    fadingEdgeLength: 20,
    contentContainerStyle: _styles.default.container,
    testID: "onramp-loading-widget-view"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    alignItems: "center",
    alignSelf: "center",
    padding: ['2xl', 'l', '0', 'l'],
    style: {
      width
    }
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.IconLink, {
    icon: "chevronLeft",
    size: "md",
    onPress: handleGoBack,
    testID: "button-back",
    style: _styles.default.backButton
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.DoubleImageLoader, {
    leftImage: _appkitCoreReactNative.OptionsController.state.metadata?.icons[0],
    rightImage: serviceProvideLogo,
    style: _styles.default.imageContainer
  }), error ? /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    alignItems: "center",
    justifyContent: "center",
    padding: ['3xs', '2xl', '0', '2xl']
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    center: true,
    color: "error-100",
    variant: "paragraph-500",
    style: _styles.default.errorText
  }, "There was an error while connecting with ", providerName), /*#__PURE__*/React.createElement(_appkitUiReactNative.Button, {
    size: "sm",
    variant: "accent",
    iconLeft: "refresh",
    style: _styles.default.retryButton,
    iconStyle: _styles.default.retryIcon,
    onPress: onConnect
  }, "Try again")) : /*#__PURE__*/React.createElement(_w3mConnectingBody.ConnectingBody, {
    title: `Connecting with ${providerName}`,
    description: "Please wait while we redirect you to finalize your purchase."
  })));
}
//# sourceMappingURL=index.js.map