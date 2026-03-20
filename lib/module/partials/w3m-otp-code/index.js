import { Platform } from 'react-native';
import { FlexView, Link, LoadingSpinner, Otp, Spacing, Text } from '@reown/appkit-ui-react-native';
import { useKeyboard } from '../../hooks/useKeyboard';
import styles from './styles';
export function OtpCodeView({
  onCodeChange,
  onSubmit,
  onRetry,
  error,
  loading,
  email,
  timeLeft = 0,
  codeExpiry = 20,
  retryLabel = "Didn't receive it?",
  retryDisabledButtonLabel = 'Resend',
  retryButtonLabel = 'Resend code'
}) {
  const {
    keyboardShown,
    keyboardHeight
  } = useKeyboard();
  const paddingBottom = Platform.select({
    android: keyboardShown ? keyboardHeight + Spacing.l : Spacing.l,
    default: Spacing.l
  });
  const handleCodeChange = code => {
    onCodeChange?.(code);
    if (code.length === 6) {
      onSubmit?.(code);
    }
  };
  return /*#__PURE__*/React.createElement(FlexView, {
    padding: ['l', 'l', '3xl', 'l'],
    alignItems: "center",
    style: {
      paddingBottom
    }
  }, /*#__PURE__*/React.createElement(Text, {
    center: true,
    variant: "paragraph-400"
  }, "Enter the code we sent to", ' '), /*#__PURE__*/React.createElement(Text, {
    variant: "paragraph-500"
  }, email ?? 'your email'), /*#__PURE__*/React.createElement(Text, {
    style: styles.expiryText,
    variant: "small-400",
    color: "fg-200"
  }, `The code expires in ${codeExpiry} minutes`), /*#__PURE__*/React.createElement(FlexView, {
    justifyContent: "center",
    style: styles.otpContainer
  }, loading ? /*#__PURE__*/React.createElement(LoadingSpinner, {
    size: "xl"
  }) : /*#__PURE__*/React.createElement(Otp, {
    length: 6,
    onChangeText: handleCodeChange,
    autoFocus: true
  })), error && /*#__PURE__*/React.createElement(Text, {
    variant: "small-400",
    color: "error-100",
    style: styles.errorText
  }, error), !loading && /*#__PURE__*/React.createElement(FlexView, {
    alignItems: "center",
    flexDirection: "row",
    margin: ['s', '0', '0', '0']
  }, /*#__PURE__*/React.createElement(Text, {
    variant: "small-400",
    color: "fg-200"
  }, retryLabel), /*#__PURE__*/React.createElement(Link, {
    onPress: onRetry,
    disabled: timeLeft > 0 || loading
  }, timeLeft > 0 ? `${retryDisabledButtonLabel} in ${timeLeft}s` : retryButtonLabel)));
}
//# sourceMappingURL=index.js.map