import { type OnRampPaymentMethod } from '@reown/appkit-core-react-native';
export declare const ITEM_SIZE = 100;
interface Props {
    onPress: (item: OnRampPaymentMethod) => void;
    item: OnRampPaymentMethod;
    selected: boolean;
    testID?: string;
}
export declare function PaymentMethod({ onPress, item, selected, testID }: Props): import("react/jsx-runtime").JSX.Element;
export {};
//# sourceMappingURL=PaymentMethod.d.ts.map