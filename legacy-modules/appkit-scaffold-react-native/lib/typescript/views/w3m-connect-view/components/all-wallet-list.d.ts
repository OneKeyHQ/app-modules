import { type StyleProp, type ViewStyle } from 'react-native';
import { type WcWallet } from '@reown/appkit-core-react-native';
interface Props {
    itemStyle: StyleProp<ViewStyle>;
    onWalletPress: (wallet: WcWallet) => void;
    isWalletConnectEnabled: boolean;
}
export declare function AllWalletList({ itemStyle, onWalletPress, isWalletConnectEnabled }: Props): import("react/jsx-runtime").JSX.Element | import("react/jsx-runtime").JSX.Element[] | null;
export {};
//# sourceMappingURL=all-wallet-list.d.ts.map