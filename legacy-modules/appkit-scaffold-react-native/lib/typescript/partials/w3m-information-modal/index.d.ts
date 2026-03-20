import { type IconType } from '@reown/appkit-ui-react-native';
interface InformationModalProps {
    iconName: IconType;
    title?: string;
    description?: string;
    visible: boolean;
    onClose: () => void;
}
export declare function InformationModal({ iconName, title, description, visible, onClose }: InformationModalProps): import("react/jsx-runtime").JSX.Element;
export {};
//# sourceMappingURL=index.d.ts.map