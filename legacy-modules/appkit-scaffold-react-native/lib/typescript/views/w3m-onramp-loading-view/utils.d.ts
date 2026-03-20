export interface OnRampUrlData {
    purchaseCurrency: string | null;
    purchaseAmount: string | null;
    purchaseImageUrl: string;
    paymentCurrency: string | null;
    paymentAmount: string | null;
    network: string | null;
    status: string | null;
    orderId: string | null;
}
export declare function parseOnRampRedirectUrl(url: string): OnRampUrlData | null;
export declare function createEmptyOnRampResult(): OnRampUrlData;
//# sourceMappingURL=utils.d.ts.map