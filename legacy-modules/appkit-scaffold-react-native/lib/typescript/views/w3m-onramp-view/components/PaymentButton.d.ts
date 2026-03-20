interface PaymentButtonProps {
    disabled?: boolean;
    loading?: boolean;
    title: string;
    subtitle?: string;
    paymentLogo?: string;
    providerLogo?: string;
    onPress: () => void;
    testID?: string;
}
declare function PaymentButton({ disabled, loading, title, subtitle, paymentLogo, providerLogo, onPress, testID }: PaymentButtonProps): import("react/jsx-runtime").JSX.Element;
export default PaymentButton;
//# sourceMappingURL=PaymentButton.d.ts.map