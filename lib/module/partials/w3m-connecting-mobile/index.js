import { useSnapshot } from 'valtio';
import { useCallback, useEffect, useState } from 'react';
import { Platform, ScrollView } from 'react-native';
import { RouterController, ApiController, AssetUtil, ConnectionController, CoreHelperUtil, OptionsController, EventsController, ConstantsUtil } from '@reown/appkit-core-react-native';
import { Button, FlexView, LoadingThumbnail, WalletImage, Link, IconBox } from '@reown/appkit-ui-react-native';
import { useCustomDimensions } from '../../hooks/useCustomDimensions';
import { UiUtil } from '../../utils/UiUtil';
import { StoreLink } from './components/StoreLink';
import { ConnectingBody, getMessage } from '../w3m-connecting-body';
import styles from './styles';
export function ConnectingMobile({
  onRetry,
  onCopyUri,
  isInstalled
}) {
  const {
    data
  } = RouterController.state;
  const {
    maxWidth: width
  } = useCustomDimensions();
  const {
    wcUri,
    wcError
  } = useSnapshot(ConnectionController.state);
  const [errorType, setErrorType] = useState();
  const showCopy = OptionsController.isClipboardAvailable() && errorType !== 'not_installed' && !CoreHelperUtil.isLinkModeURL(wcUri);
  const showRetry = errorType !== 'not_installed';
  const bodyMessage = getMessage({
    walletName: data?.wallet?.name,
    errorType,
    declined: wcError
  });
  const storeUrl = Platform.select({
    ios: data?.wallet?.app_store,
    android: data?.wallet?.play_store
  });
  const onRetryPress = () => {
    setErrorType(undefined);
    ConnectionController.setWcError(false);
    onRetry?.();
  };
  const onStorePress = () => {
    if (storeUrl) {
      CoreHelperUtil.openLink(storeUrl);
    }
  };
  const onConnect = useCallback(async () => {
    try {
      const {
        name,
        mobile_link
      } = data?.wallet ?? {};
      if (name && mobile_link && wcUri) {
        const {
          redirect,
          href
        } = CoreHelperUtil.formatNativeUrl(mobile_link, wcUri);
        const wcLinking = {
          name,
          href
        };
        ConnectionController.setWcLinking(wcLinking);
        ConnectionController.setPressedWallet(data?.wallet);
        await CoreHelperUtil.openLink(redirect);
        await ConnectionController.state.wcPromise;
        UiUtil.storeConnectedWallet(wcLinking, data?.wallet);
        EventsController.sendEvent({
          type: 'track',
          event: 'CONNECT_SUCCESS',
          properties: {
            method: 'mobile',
            name: data?.wallet?.name ?? 'Unknown',
            explorer_id: data?.wallet?.id
          }
        });
      }
    } catch (error) {
      if (error.message.includes(ConstantsUtil.LINKING_ERROR)) {
        setErrorType('not_installed');
      } else {
        setErrorType('default');
      }
    }
  }, [wcUri, data]);
  useEffect(() => {
    if (wcUri) {
      onConnect();
    }
  }, [wcUri, onConnect]);
  return /*#__PURE__*/React.createElement(ScrollView, {
    bounces: false,
    fadingEdgeLength: 20,
    contentContainerStyle: styles.container
  }, /*#__PURE__*/React.createElement(FlexView, {
    alignItems: "center",
    alignSelf: "center",
    padding: ['2xl', 'l', '0', 'l'],
    style: {
      width
    }
  }, /*#__PURE__*/React.createElement(LoadingThumbnail, {
    paused: !!errorType || wcError
  }, /*#__PURE__*/React.createElement(WalletImage, {
    size: "xl",
    imageSrc: AssetUtil.getWalletImage(RouterController.state.data?.wallet),
    imageHeaders: ApiController._getApiHeaders()
  }), wcError && /*#__PURE__*/React.createElement(IconBox, {
    icon: 'close',
    border: true,
    background: true,
    backgroundColor: "icon-box-bg-error-100",
    size: "sm",
    iconColor: "error-100",
    style: styles.errorIcon
  })), /*#__PURE__*/React.createElement(ConnectingBody, {
    title: bodyMessage.title,
    description: bodyMessage.description
  }), showRetry && /*#__PURE__*/React.createElement(Button, {
    size: "sm",
    variant: "accent",
    iconLeft: "refresh",
    style: styles.retryButton,
    iconStyle: styles.retryIcon,
    onPress: onRetryPress
  }, "Try again")), showCopy && /*#__PURE__*/React.createElement(Link, {
    iconLeft: "copySmall",
    color: "fg-200",
    style: styles.copyButton,
    onPress: () => onCopyUri(wcUri)
  }, "Copy link"), /*#__PURE__*/React.createElement(StoreLink, {
    visible: !isInstalled && !!storeUrl,
    walletName: data?.wallet?.name,
    onPress: onStorePress
  }));
}
//# sourceMappingURL=index.js.map