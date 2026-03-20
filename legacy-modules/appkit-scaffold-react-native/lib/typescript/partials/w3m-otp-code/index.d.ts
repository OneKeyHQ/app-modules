interface Props {
    onCodeChange?: (code: string) => void;
    onSubmit: (code: string) => void;
    onRetry: () => void;
    loading?: boolean;
    error?: string;
    email?: string;
    timeLeft?: number;
    codeExpiry?: number;
    retryLabel?: string;
    retryDisabledButtonLabel?: string;
    retryButtonLabel?: string;
}
export declare function OtpCodeView({ onCodeChange, onSubmit, onRetry, error, loading, email, timeLeft, codeExpiry, retryLabel, retryDisabledButtonLabel, retryButtonLabel }: Props): import("react/jsx-runtime").JSX.Element;
export {};
//# sourceMappingURL=index.d.ts.map