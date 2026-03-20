import { type StyleProp, type ViewStyle } from 'react-native';
import { type Balance } from '@reown/appkit-common-react-native';
export interface SendInputTokenProps {
    token?: Balance;
    sendTokenAmount?: number;
    gasPrice?: number;
    style?: StyleProp<ViewStyle>;
    onTokenPress?: () => void;
}
export declare function SendInputToken({ token, sendTokenAmount, gasPrice, style, onTokenPress }: SendInputTokenProps): import("react/jsx-runtime").JSX.Element;
//# sourceMappingURL=index.d.ts.map