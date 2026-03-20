"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.createEmptyOnRampResult = createEmptyOnRampResult;
exports.parseOnRampRedirectUrl = parseOnRampRedirectUrl;
var _appkitCommonReactNative = require("@reown/appkit-common-react-native");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
function parseOnRampRedirectUrl(url) {
  try {
    const parsedUrl = new URL(url);
    const searchParams = new URLSearchParams(parsedUrl.search);
    const asset = searchParams.get('cryptoCurrency') ?? _appkitCoreReactNative.OnRampController.state.purchaseCurrency?.currencyCode ?? null;
    const network = searchParams.get('network') ?? _appkitCoreReactNative.OnRampController.state.purchaseCurrency?.chainName ?? null;
    const purchaseAmountParam = searchParams.get('cryptoAmount');
    const purchaseAmount = purchaseAmountParam ? (() => {
      const parsed = parseFloat(purchaseAmountParam);
      return isNaN(parsed) ? _appkitCoreReactNative.OnRampController.state.selectedQuote?.destinationAmount ?? null : parsed;
    })() : _appkitCoreReactNative.OnRampController.state.selectedQuote?.destinationAmount ?? null;
    const amountParam = searchParams.get('fiatAmount');
    const amount = amountParam ? (() => {
      const parsed = parseFloat(amountParam);
      return isNaN(parsed) ? _appkitCoreReactNative.OnRampController.state.paymentAmount ?? null : parsed;
    })() : _appkitCoreReactNative.OnRampController.state.paymentAmount ?? null;
    const currency = searchParams.get('fiatCurrency') ?? _appkitCoreReactNative.OnRampController.state.paymentCurrency?.currencyCode ?? null;
    const orderId = searchParams.get('orderId') ?? searchParams.get('partnerOrderId');
    const status = searchParams.get('status');
    return {
      purchaseCurrency: asset,
      purchaseAmount: purchaseAmount ? _appkitCommonReactNative.NumberUtil.formatNumberToLocalString(purchaseAmount) : null,
      purchaseImageUrl: _appkitCoreReactNative.OnRampController.state.purchaseCurrency?.symbolImageUrl ?? '',
      paymentCurrency: currency,
      paymentAmount: amount ? _appkitCommonReactNative.NumberUtil.formatNumberToLocalString(amount) : null,
      network,
      status,
      orderId
    };
  } catch (error) {
    // Return null if URL parsing fails
    return null;
  }
}
function createEmptyOnRampResult() {
  return {
    purchaseCurrency: null,
    purchaseAmount: null,
    purchaseImageUrl: '',
    paymentCurrency: null,
    paymentAmount: null,
    network: null,
    status: null,
    orderId: null
  };
}
//# sourceMappingURL=utils.js.map