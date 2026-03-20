import { type StyleProp, type ViewStyle } from 'react-native';
import type { SocialProvider } from '@reown/appkit-common-react-native';
export interface AuthButtonsProps {
    onUpgradePress: () => void;
    onPress: () => void;
    socialProvider?: SocialProvider;
    text: string;
    style?: StyleProp<ViewStyle>;
}
export declare function AuthButtons({ onUpgradePress, onPress, socialProvider, text, style }: AuthButtonsProps): import("react/jsx-runtime").JSX.Element;
//# sourceMappingURL=auth-buttons.d.ts.map