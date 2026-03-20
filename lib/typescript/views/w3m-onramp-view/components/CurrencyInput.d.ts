import { type StyleProp, type ViewStyle } from 'react-native';
export interface InputTokenProps {
    style?: StyleProp<ViewStyle>;
    value?: string;
    symbol?: string;
    loading?: boolean;
    error?: string;
    isAmountError?: boolean;
    purchaseValue?: string;
    onValueChange?: (value: number) => void;
    onSuggestedValuePress?: (value: number) => void;
    suggestedValues?: number[];
}
export declare function CurrencyInput({ value, loading, error, isAmountError, purchaseValue, onValueChange, onSuggestedValuePress, symbol, style, suggestedValues }: InputTokenProps): import("react/jsx-runtime").JSX.Element;
//# sourceMappingURL=CurrencyInput.d.ts.map