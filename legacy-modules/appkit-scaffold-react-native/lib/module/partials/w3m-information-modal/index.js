import Modal from 'react-native-modal';
import { FlexView, Text, IconBox, useTheme, Button } from '@reown/appkit-ui-react-native';
import styles from './styles';
export function InformationModal({
  iconName,
  title,
  description,
  visible,
  onClose
}) {
  const Theme = useTheme();
  return /*#__PURE__*/React.createElement(Modal, {
    isVisible: visible,
    useNativeDriver: true,
    useNativeDriverForBackdrop: true,
    onBackdropPress: onClose,
    onDismiss: onClose,
    style: styles.modal
  }, /*#__PURE__*/React.createElement(FlexView, {
    style: [styles.content, {
      backgroundColor: Theme['bg-200']
    }],
    padding: "2xl"
  }, /*#__PURE__*/React.createElement(IconBox, {
    icon: iconName,
    size: "lg",
    background: true
  }), !!title && /*#__PURE__*/React.createElement(Text, {
    variant: "paragraph-500",
    style: styles.title
  }, title), !!description && /*#__PURE__*/React.createElement(Text, {
    variant: "small-400",
    color: "fg-150",
    center: true
  }, description), /*#__PURE__*/React.createElement(Button, {
    onPress: onClose,
    variant: "fill",
    style: styles.button
  }, "Got it")));
}
//# sourceMappingURL=index.js.map