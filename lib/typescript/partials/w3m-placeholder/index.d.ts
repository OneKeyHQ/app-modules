import { type StyleProp, type ViewStyle } from 'react-native';
import { type IconType, type ColorType } from '@reown/appkit-ui-react-native';
interface Props {
    icon?: IconType;
    iconColor?: ColorType;
    title?: string;
    description?: string;
    style?: StyleProp<ViewStyle>;
    actionIcon?: IconType;
    actionPress?: () => void;
    actionTitle?: string;
}
export declare function Placeholder({ icon, iconColor, title, description, style, actionPress, actionTitle, actionIcon }: Props): import("react/jsx-runtime").JSX.Element;
export {};
//# sourceMappingURL=index.d.ts.map