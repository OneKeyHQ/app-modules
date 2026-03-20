export type BodyErrorType = 'not_installed' | 'default' | 'declined' | undefined;
interface Props {
    walletName?: string;
    declined?: boolean;
    errorType?: BodyErrorType;
    isWeb?: boolean;
}
export declare const getMessage: ({ walletName, declined, errorType, isWeb }: Props) => {
    title: string;
    description: string;
} | {
    title: string;
    description?: undefined;
};
export {};
//# sourceMappingURL=utils.d.ts.map