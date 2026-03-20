/// <reference types="react" />
interface SelectorModalProps {
    title?: string;
    visible: boolean;
    onClose: () => void;
    items: any[];
    selectedItem?: any;
    renderItem: ({ item }: {
        item: any;
    }) => React.ReactElement;
    keyExtractor: (item: any, index: number) => string;
    onSearch: (value: string) => void;
    itemHeight?: number;
    showNetwork?: boolean;
    searchPlaceholder?: string;
}
export declare function SelectorModal({ title, visible, onClose, items, selectedItem, renderItem, onSearch, searchPlaceholder, keyExtractor, itemHeight, showNetwork }: SelectorModalProps): import("react/jsx-runtime").JSX.Element;
export {};
//# sourceMappingURL=index.d.ts.map