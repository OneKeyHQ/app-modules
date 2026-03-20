export interface ModalData {
    detail: ModalDetail;
    opts?: ModalDataOpts;
}
export type ModalDetail = 'slippage' | 'networkCost' | 'priceImpact';
export interface ModalDataOpts {
    networkSymbol?: string;
    networkName?: string;
    minimumReceive?: string;
    toTokenSymbol?: string;
}
export declare const getModalData: (detail: ModalDetail, opts?: ModalDataOpts) => {
    title: string;
    description: string;
};
//# sourceMappingURL=utils.d.ts.map