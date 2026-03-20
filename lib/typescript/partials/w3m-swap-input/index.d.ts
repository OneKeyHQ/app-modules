import type BigNumber from 'bignumber.js';
import { type StyleProp, type ViewStyle } from 'react-native';
import { type SwapTokenWithBalance } from '@reown/appkit-core-react-native';
export interface SwapInputProps {
    token?: SwapTokenWithBalance;
    value?: string;
    gasPrice?: number;
    style?: StyleProp<ViewStyle>;
    loading?: boolean;
    onTokenPress?: () => void;
    onMaxPress?: () => void;
    onChange?: (value: string) => void;
    balance?: BigNumber;
    marketValue?: number;
    editable?: boolean;
    autoFocus?: boolean;
}
export declare function SwapInput({ token, value, style, loading, onTokenPress, onMaxPress, onChange, marketValue, editable, autoFocus }: SwapInputProps): import("react/jsx-runtime").JSX.Element;
//# sourceMappingURL=index.d.ts.map