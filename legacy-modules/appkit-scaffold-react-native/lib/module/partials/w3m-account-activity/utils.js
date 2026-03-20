import { DateUtil } from '@reown/appkit-common-react-native';
import { TransactionUtil } from '@reown/appkit-ui-react-native';
export function getTransactionListItemProps(transaction) {
  const date = DateUtil.formatDate(transaction?.metadata?.minedAt);
  const descriptions = TransactionUtil.getTransactionDescriptions(transaction);
  const transfers = transaction?.transfers;
  const transfer = transaction?.transfers?.[0];
  const isAllNFT = Boolean(transfer) && transaction?.transfers?.every(item => Boolean(item.nft_info));
  const images = TransactionUtil.getTransactionImages(transfers);
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