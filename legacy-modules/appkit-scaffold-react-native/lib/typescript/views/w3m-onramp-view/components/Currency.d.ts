import { type OnRampFiatCurrency, type OnRampCryptoCurrency } from '@reown/appkit-core-react-native';
export declare const ITEM_HEIGHT = 60;
interface Props {
    onPress: (item: OnRampFiatCurrency | OnRampCryptoCurrency) => void;
    item: OnRampFiatCurrency | OnRampCryptoCurrency;
    selected: boolean;
    title: string;
    subtitle: string;
    testID?: string;
}
export declare function Currency({ onPress, item, selected, title, subtitle, testID }: Props): import("react/jsx-runtime").JSX.Element;
export {};
//# sourceMappingURL=Currency.d.ts.map