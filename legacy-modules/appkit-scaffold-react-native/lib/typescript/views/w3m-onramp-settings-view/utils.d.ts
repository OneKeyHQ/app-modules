import { type OnRampCountry, type OnRampFiatCurrency } from '@reown/appkit-core-react-native';
type ModalType = 'country' | 'paymentCurrency';
export declare const getItemHeight: (type?: ModalType) => number;
export declare const getModalTitle: (type?: ModalType) => string | undefined;
export declare const getModalSearchPlaceholder: (type?: ModalType) => string | undefined;
export declare const getModalItemKey: (type: ModalType | undefined, index: number, item: any) => string;
export declare const getModalItems: (type?: Exclude<ModalType, 'quotes'>, searchValue?: string, filterSelected?: boolean) => (OnRampFiatCurrency | OnRampCountry)[];
export {};
//# sourceMappingURL=utils.d.ts.map