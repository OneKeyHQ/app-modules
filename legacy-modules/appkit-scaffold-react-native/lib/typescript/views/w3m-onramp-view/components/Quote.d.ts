import { type OnRampQuote } from '@reown/appkit-core-react-native';
interface Props {
    item: OnRampQuote;
    isBestDeal?: boolean;
    tagText?: string;
    logoURL?: string;
    onQuotePress: (item: OnRampQuote) => void;
    selected?: boolean;
    testID?: string;
}
export declare const ITEM_HEIGHT = 64;
export declare function Quote({ item, logoURL, onQuotePress, selected, tagText, testID }: Props): import("react/jsx-runtime").JSX.Element;
export {};
//# sourceMappingURL=Quote.d.ts.map