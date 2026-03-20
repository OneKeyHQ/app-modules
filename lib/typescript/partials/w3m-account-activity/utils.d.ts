import { type Transaction } from '@reown/appkit-common-react-native';
import type { TransactionType } from '@reown/appkit-ui-react-native/lib/typescript/utils/TypesUtil';
export declare function getTransactionListItemProps(transaction: Transaction): {
    date: string;
    direction: import("@reown/appkit-common-react-native").TransactionDirection | undefined;
    descriptions: string[];
    isAllNFT: boolean;
    images: import("@reown/appkit-common-react-native").TransactionImage[];
    status: import("@reown/appkit-common-react-native").TransactionStatus;
    transfers: import("@reown/appkit-common-react-native").TransactionTransfer[];
    type: TransactionType;
};
//# sourceMappingURL=utils.d.ts.map