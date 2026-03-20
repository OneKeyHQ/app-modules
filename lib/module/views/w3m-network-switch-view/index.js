/* eslint-disable valtio/state-snapshot-rule */
import { useSnapshot } from 'valtio';
import { useEffect, useState } from 'react';
import { ApiController, AssetController, AssetUtil, ConnectionController, ConnectorController, EventsController, NetworkController, RouterController, RouterUtil } from '@reown/appkit-core-react-native';
import { Button, FlexView, IconBox, LoadingHexagon, NetworkImage, Text } from '@reown/appkit-ui-react-native';
import styles from './styles';
const imageHeaders = ApiController._getApiHeaders();
export function NetworkSwitchView() {
  const {
    data
  } = useSnapshot(RouterController.state);
  const {
    recentWallets
  } = useSnapshot(ConnectionController.state);
  const {
    caipNetwork
  } = useSnapshot(NetworkController.state);
  const {
    networkImages
  } = useSnapshot(AssetController.state);
  const isAuthConnected = ConnectorController.state.connectedConnector === 'AUTH';
  const [error, setError] = useState(false);
  const [showRetry, setShowRetry] = useState(false);
  const network = data?.network;
  const wallet = recentWallets?.[0];
  const onSwitchNetwork = async () => {
    try {
      setError(false);
      await NetworkController.switchActiveNetwork(network);
      EventsController.sendEvent({
        type: 'track',
        event: 'SWITCH_NETWORK',
        properties: {
          network: network.id
        }
      });
    } catch {
      setError(true);
      setShowRetry(true);
    }
  };
  useEffect(() => {
    onSwitchNetwork();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);
  useEffect(() => {
    // Go back if network is already switched
    if (caipNetwork?.id === network?.id) {
      RouterUtil.navigateAfterNetworkSwitch();
    }
  }, [caipNetwork?.id, network?.id]);
  const retryTemplate = () => {
    if (!showRetry) return null;
    return /*#__PURE__*/React.createElement(Button, {
      size: "sm",
      variant: "accent",
      iconLeft: "refresh",
      style: styles.retryButton,
      iconStyle: styles.retryIcon,
      onPress: onSwitchNetwork
    }, "Try again");
  };
  const textTemplate = () => {
    const walletName = wallet?.name ?? 'wallet';
    if (error) {
      return /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(Text, {
        variant: "paragraph-500",
        style: styles.text
      }, "Switch declined"), /*#__PURE__*/React.createElement(Text, {
        center: true,
        variant: "small-400",
        color: "fg-200",
        style: styles.descriptionText
      }, "Switch can be declined if chain is not supported by a wallet or previous request is still active"));
    }
    if (isAuthConnected) {
      return /*#__PURE__*/React.createElement(Text, {
        variant: "paragraph-500",
        style: styles.text
      }, "Switching to ", network.name, " network");
    }
    return /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(Text, {
      variant: "paragraph-500",
      style: styles.text
    }, `Approve in ${walletName}`), /*#__PURE__*/React.createElement(Text, {
      center: true,
      variant: "small-400",
      color: "fg-200",
      style: styles.descriptionText
    }, "Accept switch request in your wallet"));
  };
  return /*#__PURE__*/React.createElement(FlexView, {
    alignItems: "center",
    padding: ['2xl', 's', '4xl', 's']
  }, /*#__PURE__*/React.createElement(LoadingHexagon, {
    paused: error
  }, /*#__PURE__*/React.createElement(NetworkImage, {
    imageSrc: AssetUtil.getNetworkImage(network, networkImages),
    imageHeaders: imageHeaders,
    size: "lg"
  }), error && /*#__PURE__*/React.createElement(IconBox, {
    icon: "close",
    border: true,
    background: true,
    backgroundColor: "icon-box-bg-error-100",
    size: "sm",
    iconColor: "error-100",
    style: styles.errorIcon
  })), textTemplate(), retryTemplate());
}
//# sourceMappingURL=index.js.map