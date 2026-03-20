import { useSnapshot } from 'valtio';
import { useState } from 'react';
import { EmailInput, FlexView } from '@reown/appkit-ui-react-native';
import { ConnectorController, CoreHelperUtil, EventsController, RouterController, SnackController } from '@reown/appkit-core-react-native';
export function ConnectEmailInput({
  loading
}) {
  const {
    connectors
  } = useSnapshot(ConnectorController.state);
  const [inputLoading, setInputLoading] = useState(false);
  const [error, setError] = useState('');
  const [isValidEmail, setIsValidEmail] = useState(false);
  const authProvider = connectors.find(c => c.type === 'AUTH')?.provider;
  const onChangeText = value => {
    setIsValidEmail(CoreHelperUtil.isValidEmail(value));
    setError('');
  };
  const onEmailFocus = () => {
    EventsController.sendEvent({
      type: 'track',
      event: 'EMAIL_LOGIN_SELECTED'
    });
  };
  const onEmailSubmit = async email => {
    try {
      if (email.length === 0) return;
      setInputLoading(true);
      const response = await authProvider.connectEmail({
        email
      });
      EventsController.sendEvent({
        type: 'track',
        event: 'EMAIL_SUBMITTED'
      });
      if (response.action === 'VERIFY_DEVICE') {
        RouterController.push('EmailVerifyDevice', {
          email
        });
      } else if (response.action === 'VERIFY_OTP') {
        RouterController.push('EmailVerifyOtp', {
          email
        });
      }
    } catch (e) {
      const parsedError = CoreHelperUtil.parseError(e);
      if (parsedError?.includes('valid email')) {
        setError('Invalid email. Try again.');
      } else {
        SnackController.showError(parsedError);
      }
    } finally {
      setInputLoading(false);
    }
  };
  return /*#__PURE__*/React.createElement(FlexView, {
    padding: ['0', 's', '0', 's']
  }, /*#__PURE__*/React.createElement(EmailInput, {
    onSubmit: onEmailSubmit,
    onFocus: onEmailFocus,
    loading: inputLoading || loading,
    errorMessage: error,
    onChangeText: onChangeText,
    submitEnabled: isValidEmail
  }));
}
//# sourceMappingURL=connect-email-input.js.map