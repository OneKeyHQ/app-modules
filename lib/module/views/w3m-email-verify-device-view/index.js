import { useSnapshot } from 'valtio';
import { View } from 'react-native';
import { useEffect, useState } from 'react';
import { FlexView, Icon, Link, Text, useTheme } from '@reown/appkit-ui-react-native';
import { ConnectorController, CoreHelperUtil, EventsController, RouterController, SnackController } from '@reown/appkit-core-react-native';
import useTimeout from '../../hooks/useTimeout';
import styles from './styles';
export function EmailVerifyDeviceView() {
  const Theme = useTheme();
  const {
    connectors
  } = useSnapshot(ConnectorController.state);
  const {
    data
  } = RouterController.state;
  const {
    timeLeft,
    startTimer
  } = useTimeout(0);
  const [loading, setLoading] = useState(false);
  const authProvider = connectors.find(c => c.type === 'AUTH')?.provider;
  const listenForDeviceApproval = async () => {
    if (authProvider && data?.email) {
      try {
        await authProvider.connectDevice();
        EventsController.sendEvent({
          type: 'track',
          event: 'DEVICE_REGISTERED_FOR_EMAIL'
        });
        EventsController.sendEvent({
          type: 'track',
          event: 'EMAIL_VERIFICATION_CODE_SENT'
        });
        RouterController.replace('EmailVerifyOtp', {
          email: data.email
        });
      } catch (error) {
        RouterController.goBack();
      }
    }
  };
  const onResendEmail = async () => {
    try {
      if (!data?.email || !authProvider) return;
      setLoading(true);
      authProvider?.connectEmail({
        email: data.email
      });
      listenForDeviceApproval();
      SnackController.showSuccess('Link resent');
      startTimer(30);
      setLoading(false);
    } catch (e) {
      const parsedError = CoreHelperUtil.parseError(e);
      SnackController.showError(parsedError);
    }
  };
  useEffect(() => {
    listenForDeviceApproval();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);
  return /*#__PURE__*/React.createElement(FlexView, {
    alignItems: "center",
    padding: ['0', '4xl', '3xl', '4xl']
  }, /*#__PURE__*/React.createElement(View, {
    style: [styles.iconContainer, {
      backgroundColor: Theme['accent-glass-010']
    }]
  }, /*#__PURE__*/React.createElement(Icon, {
    name: "verify",
    size: "lg",
    height: 28,
    width: 28,
    color: "accent-100"
  })), /*#__PURE__*/React.createElement(Text, {
    center: true,
    variant: "medium-600",
    style: styles.headingText
  }, "Register this device to continue"), /*#__PURE__*/React.createElement(Text, {
    center: true,
    variant: "paragraph-400"
  }, "Check the instructions sent to", ' '), /*#__PURE__*/React.createElement(Text, {
    variant: "paragraph-500"
  }, data?.email ?? 'your email'), /*#__PURE__*/React.createElement(Text, {
    variant: "small-400",
    color: "fg-200",
    style: styles.expiryText
  }, "The link expires in 20 minutes"), /*#__PURE__*/React.createElement(FlexView, {
    alignItems: "center",
    justifyContent: "center",
    flexDirection: "row"
  }, /*#__PURE__*/React.createElement(Text, {
    variant: "small-400"
  }, "Didn't receive it?"), /*#__PURE__*/React.createElement(Link, {
    onPress: onResendEmail,
    disabled: timeLeft > 0 || loading
  }, timeLeft > 0 ? `Resend in ${timeLeft}s` : 'Resend link')));
}
//# sourceMappingURL=index.js.map