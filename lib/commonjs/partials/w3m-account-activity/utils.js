"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.getTransactionListItemProps = getTransactionListItemProps;
var _appkitCommonReactNative = require("@reown/appkit-common-react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
function getTransactionListItemProps(transaction) {
  const date = _appkitCommonReactNative.DateUtil.formatDate(transaction?.metadata?.minedAt);
  const descriptions = _appkitUiReactNative.TransactionUtil.getTransactionDescriptions(transaction);
  const transfers = transaction?.transfers;
  const transfer = transaction?.transfers?.[0];
  const isAllNFT = Boolean(transfer) && transaction?.transfers?.every(item => Boolean(item.nft_info));
  const images = _appkitUiReactNative.TransactionUtil.getTransactionImages(transfers);
  return {
    date,
    direction: transfer?.direction,
    descriptions,
    isAllNFT,
    images,
    status: transaction.metadata?.status,
    transfers,
    type: transaction.metadata?.operationType
  };
}
//# sourceMappingURL=utils.js.map