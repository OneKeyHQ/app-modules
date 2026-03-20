import { type WcWallet } from '@reown/appkit-core-react-native';
export interface AllWalletsSearchProps {
    columns: number;
    onItemPress: (wallet: WcWallet) => void;
    itemWidth?: number;
    searchQuery?: string;
}
export declare function AllWalletsSearch({ searchQuery, columns, itemWidth, onItemPress }: AllWalletsSearchProps): import("react/jsx-runtime").JSX.Element;
//# sourceMappingURL=index.d.ts.map