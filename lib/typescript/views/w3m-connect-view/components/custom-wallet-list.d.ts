import type { StyleProp, ViewStyle } from 'react-native';
import { type CustomWallet } from '@reown/appkit-core-react-native';
interface Props {
    itemStyle: StyleProp<ViewStyle>;
    onWalletPress: (wallet: CustomWallet) => void;
    isWalletConnectEnabled: boolean;
}
export declare function CustomWalletList({ itemStyle, onWalletPress, isWalletConnectEnabled }: Props): import("react/jsx-runtime").JSX.Element[] | null;
export {};
//# sourceMappingURL=custom-wallet-list.d.ts.map