import { useCallback, useEffect, useState } from 'react';
import { ScrollView } from 'react-native';
import { RouterController, ApiController, AssetUtil, ConnectionController, ModalController, EventsController, StorageUtil } from '@reown/appkit-core-react-native';
import { Button, FlexView, IconBox, LoadingThumbnail, WalletImage } from '@reown/appkit-ui-react-native';
import { useCustomDimensions } from '../../hooks/useCustomDimensions';
import { ConnectingBody, getMessage } from '../../partials/w3m-connecting-body';
import styles from './styles';
export function ConnectingExternalView() {
  const {
    data
  } = RouterController.state;
  const connector = data?.connector;
  const {
    maxWidth: width
  } = useCustomDimensions();
  const [errorType, setErrorType] = useState();
  const bodyMessage = getMessage({
    walletName: data?.wallet?.name,
    errorType
  });
  const onRetryPress = () => {
    setErrorType(undefined);
    onConnect();
  };
  const storeConnectedWallet = useCallback(async wallet => {
    if (wallet) {
      const recentWallets = await StorageUtil.addRecentWallet(wallet);
      if (recentWallets) {
        ConnectionController.setRecentWallets(recentWallets);
      }
    }
    if (connector) {
      const url = AssetUtil.getConnectorImage(connector);
      ConnectionController.setConnectedWalletImageUrl(url);
    }
  }, [connector]);
  const onConnect = useCallback(async () => {
    try {
      if (connector) {
        await ConnectionController.connectExternal(connector);
        storeConnectedWallet(data?.wallet);
        ModalController.close();
        EventsController.sendEvent({
          type: 'track',
          event: 'CONNECT_SUCCESS',
          properties: {
            name: data.wallet?.name ?? 'Unknown',
            method: 'mobile',
            explorer_id: data.wallet?.id
          }
        });
      }
    } catch (error) {
      if (/(Wallet not found)/i.test(error.message)) {
        setErrorType('not_installed');
      } else if (/(rejected)/i.test(error.message)) {
        setErrorType('declined');
      } else {
        setErrorType('default');
      }
      EventsController.sendEvent({
        type: 'track',
        event: 'CONNECT_ERROR',
        properties: {
          message: error?.message ?? 'Unknown'
        }
      });
    }
  }, [connector, storeConnectedWallet, data?.wallet]);
  useEffect(() => {
    onConnect();
  }, [onConnect]);
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
    paused: !!errorType
  }, /*#__PURE__*/React.createElement(WalletImage, {
    size: "xl",
    imageSrc: AssetUtil.getConnectorImage(connector),
    imageHeaders: ApiController._getApiHeaders()
  }), errorType && /*#__PURE__*/React.createElement(IconBox, {
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
  }), errorType !== 'not_installed' && /*#__PURE__*/React.createElement(Button, {
    size: "sm",
    variant: "accent",
    iconLeft: "refresh",
    style: styles.retryButton,
    iconStyle: styles.retryIcon,
    onPress: onRetryPress
  }, "Try again")));
}
//# sourceMappingURL=index.js.map