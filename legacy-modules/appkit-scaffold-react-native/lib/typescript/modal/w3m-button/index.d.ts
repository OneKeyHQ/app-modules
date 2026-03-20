import { type AccountButtonProps } from '../w3m-account-button';
import { type ConnectButtonProps } from '../w3m-connect-button';
export interface AppKitButtonProps {
    balance?: AccountButtonProps['balance'];
    disabled?: AccountButtonProps['disabled'];
    size?: ConnectButtonProps['size'];
    label?: ConnectButtonProps['label'];
    loadingLabel?: ConnectButtonProps['loadingLabel'];
    accountStyle?: AccountButtonProps['style'];
    connectStyle?: ConnectButtonProps['style'];
}
export declare function AppKitButton({ balance, disabled, size, label, loadingLabel, accountStyle, connectStyle }: AppKitButtonProps): import("react/jsx-runtime").JSX.Element;
//# sourceMappingURL=index.d.ts.map