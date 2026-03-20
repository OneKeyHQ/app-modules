import { type WcWallet } from '@reown/appkit-core-react-native';
import type { StyleProp, ViewStyle } from 'react-native';
interface Props {
    itemStyle: StyleProp<ViewStyle>;
    onWalletPress: (wallet: WcWallet, installed: boolean) => void;
    isWalletConnectEnabled: boolean;
}
export declare function RecentWalletList({ itemStyle, onWalletPress, isWalletConnectEnabled }: Props): import("react/jsx-runtime").JSX.Element[] | null;
export {};
//# sourceMappingURL=recent-wallet-list.d.ts.map