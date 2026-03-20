import { useEffect } from 'react';
import { useSnapshot } from 'valtio';
import { ScrollView } from 'react-native';
import { StringUtil } from '@reown/appkit-common-react-native';
import { ConnectionController, EventsController, OptionsController, RouterController, WebviewController } from '@reown/appkit-core-react-native';
import { FlexView, ListSocial, Text } from '@reown/appkit-ui-react-native';
import { useCustomDimensions } from '../../hooks/useCustomDimensions';
import styles from './styles';
export function ConnectSocialsView() {
  const {
    features
  } = useSnapshot(OptionsController.state);
  const {
    padding
  } = useCustomDimensions();
  const socialProviders = features?.socials ?? [];
  const onItemPress = provider => {
    ConnectionController.setSelectedSocialProvider(provider);
    EventsController.sendEvent({
      type: 'track',
      event: 'SOCIAL_LOGIN_STARTED',
      properties: {
        provider
      }
    });
    if (provider === 'farcaster') {
      RouterController.push('ConnectingFarcaster');
    } else {
      RouterController.push('ConnectingSocial');
    }
  };
  useEffect(() => {
    WebviewController.setConnecting(false);
  }, []);
  return /*#__PURE__*/React.createElement(ScrollView, {
    style: {
      paddingHorizontal: padding
    },
    bounces: false
  }, /*#__PURE__*/React.createElement(FlexView, {
    padding: ['xs', 'm', '2xl', 'm']
  }, socialProviders.map(provider => /*#__PURE__*/React.createElement(ListSocial, {
    key: provider,
    logo: provider,
    onPress: () => onItemPress(provider),
    style: styles.item
  }, /*#__PURE__*/React.createElement(Text, {
    style: styles.text,
    color: 'fg-100'
  }, StringUtil.capitalize(provider))))));
}
//# sourceMappingURL=index.js.map