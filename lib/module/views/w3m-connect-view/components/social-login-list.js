import { StyleSheet } from 'react-native';
import { FlexView, ListSocial, LogoSelect, Spacing, Text } from '@reown/appkit-ui-react-native';
import { StringUtil } from '@reown/appkit-common-react-native';
import { ConnectionController, EventsController, RouterController, WebviewController } from '@reown/appkit-core-react-native';
const MAX_OPTIONS = 6;
export function SocialLoginList({
  options,
  disabled
}) {
  const showBigSocial = options?.length > 2 || options?.length === 1;
  const showMoreButton = options?.length > MAX_OPTIONS;
  const topSocial = showBigSocial ? options[0] : null;
  let bottomSocials = showBigSocial ? options.slice(1) : options;
  bottomSocials = showMoreButton ? bottomSocials.slice(0, MAX_OPTIONS - 2) : bottomSocials;
  const onItemPress = provider => {
    ConnectionController.setSelectedSocialProvider(provider);
    EventsController.sendEvent({
      type: 'track',
      event: 'SOCIAL_LOGIN_STARTED',
      properties: {
        provider
      }
    });
    WebviewController.setConnecting(false);
    if (provider === 'farcaster') {
      RouterController.push('ConnectingFarcaster');
    } else {
      RouterController.push('ConnectingSocial');
    }
  };
  const onMorePress = () => {
    RouterController.push('ConnectSocials');
  };
  return /*#__PURE__*/React.createElement(FlexView, {
    padding: ['xs', 's', '0', 's']
  }, topSocial && /*#__PURE__*/React.createElement(ListSocial, {
    logo: topSocial,
    disabled: disabled,
    onPress: () => onItemPress(topSocial)
  }, /*#__PURE__*/React.createElement(Text, {
    style: styles.topDescription,
    color: disabled ? 'fg-300' : 'fg-100'
  }, `Continue with ${StringUtil.capitalize(topSocial)}`)), /*#__PURE__*/React.createElement(FlexView, {
    flexDirection: "row",
    justifyContent: "space-between",
    margin: ['xs', '0', '0', '0']
  }, bottomSocials?.map((social, index) => /*#__PURE__*/React.createElement(LogoSelect, {
    key: social,
    disabled: disabled,
    logo: social,
    onPress: () => onItemPress(social),
    style: [styles.socialItem, index === 0 && styles.socialItemFirst, !showMoreButton && index === bottomSocials.length - 1 && styles.socialItemLast]
  })), showMoreButton && /*#__PURE__*/React.createElement(LogoSelect, {
    logo: "more",
    disabled: disabled,
    style: [styles.socialItem, styles.socialItemLast],
    onPress: onMorePress
  })));
}
const styles = StyleSheet.create({
  topDescription: {
    textAlign: 'center'
  },
  socialItem: {
    flex: 1,
    marginHorizontal: Spacing['2xs']
  },
  socialItemFirst: {
    marginLeft: 0
  },
  socialItemLast: {
    marginRight: 0
  }
});
//# sourceMappingURL=social-login-list.js.map