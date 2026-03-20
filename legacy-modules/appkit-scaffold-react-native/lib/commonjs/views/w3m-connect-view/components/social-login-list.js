"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.SocialLoginList = SocialLoginList;
var _reactNative = require("react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _appkitCommonReactNative = require("@reown/appkit-common-react-native");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
const MAX_OPTIONS = 6;
function SocialLoginList({
  options,
  disabled
}) {
  const showBigSocial = options?.length > 2 || options?.length === 1;
  const showMoreButton = options?.length > MAX_OPTIONS;
  const topSocial = showBigSocial ? options[0] : null;
  let bottomSocials = showBigSocial ? options.slice(1) : options;
  bottomSocials = showMoreButton ? bottomSocials.slice(0, MAX_OPTIONS - 2) : bottomSocials;
  const onItemPress = provider => {
    _appkitCoreReactNative.ConnectionController.setSelectedSocialProvider(provider);
    _appkitCoreReactNative.EventsController.sendEvent({
      type: 'track',
      event: 'SOCIAL_LOGIN_STARTED',
      properties: {
        provider
      }
    });
    _appkitCoreReactNative.WebviewController.setConnecting(false);
    if (provider === 'farcaster') {
      _appkitCoreReactNative.RouterController.push('ConnectingFarcaster');
    } else {
      _appkitCoreReactNative.RouterController.push('ConnectingSocial');
    }
  };
  const onMorePress = () => {
    _appkitCoreReactNative.RouterController.push('ConnectSocials');
  };
  return /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    padding: ['xs', 's', '0', 's']
  }, topSocial && /*#__PURE__*/React.createElement(_appkitUiReactNative.ListSocial, {
    logo: topSocial,
    disabled: disabled,
    onPress: () => onItemPress(topSocial)
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    style: styles.topDescription,
    color: disabled ? 'fg-300' : 'fg-100'
  }, `Continue with ${_appkitCommonReactNative.StringUtil.capitalize(topSocial)}`)), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    justifyContent: "space-between",
    margin: ['xs', '0', '0', '0']
  }, bottomSocials?.map((social, index) => /*#__PURE__*/React.createElement(_appkitUiReactNative.LogoSelect, {
    key: social,
    disabled: disabled,
    logo: social,
    onPress: () => onItemPress(social),
    style: [styles.socialItem, index === 0 && styles.socialItemFirst, !showMoreButton && index === bottomSocials.length - 1 && styles.socialItemLast]
  })), showMoreButton && /*#__PURE__*/React.createElement(_appkitUiReactNative.LogoSelect, {
    logo: "more",
    disabled: disabled,
    style: [styles.socialItem, styles.socialItemLast],
    onPress: onMorePress
  })));
}
const styles = _reactNative.StyleSheet.create({
  topDescription: {
    textAlign: 'center'
  },
  socialItem: {
    flex: 1,
    marginHorizontal: _appkitUiReactNative.Spacing['2xs']
  },
  socialItemFirst: {
    marginLeft: 0
  },
  socialItemLast: {
    marginRight: 0
  }
});
//# sourceMappingURL=social-login-list.js.map