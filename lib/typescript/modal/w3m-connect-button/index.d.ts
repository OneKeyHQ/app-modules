import { type ConnectButtonProps as ConnectButtonUIProps } from '@reown/appkit-ui-react-native';
export interface ConnectButtonProps {
    label: string;
    loadingLabel: string;
    size?: ConnectButtonUIProps['size'];
    style?: ConnectButtonUIProps['style'];
    disabled?: ConnectButtonUIProps['disabled'];
    testID?: string;
}
export declare function ConnectButton({ label, loadingLabel, size, style, disabled, testID }: ConnectButtonProps): import("react/jsx-runtime").JSX.Element;
//# sourceMappingURL=index.d.ts.map