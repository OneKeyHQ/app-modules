import { useSnapshot } from 'valtio';
import { useState } from 'react';
import { FlatList } from 'react-native';
import { Icon, ListItem, Separator, Text } from '@reown/appkit-ui-react-native';
import { ApiController, AssetUtil, CoreHelperUtil, ConnectionUtil, EventsController, NetworkController, NetworkUtil, AssetController } from '@reown/appkit-core-react-native';
import styles from './styles';
export function UnsupportedChainView() {
  const {
    caipNetwork,
    supportsAllNetworks,
    approvedCaipNetworkIds,
    requestedCaipNetworks
  } = useSnapshot(NetworkController.state);
  const {
    networkImages
  } = useSnapshot(AssetController.state);
  const [disconnecting, setDisconnecting] = useState(false);
  const networks = CoreHelperUtil.sortNetworks(approvedCaipNetworkIds, requestedCaipNetworks);
  const imageHeaders = ApiController._getApiHeaders();
  const onNetworkPress = async network => {
    const result = await NetworkUtil.handleNetworkSwitch(network);
    if (result?.type === 'SWITCH_NETWORK') {
      EventsController.sendEvent({
        type: 'track',
        event: 'SWITCH_NETWORK',
        properties: {
          network: network.id
        }
      });
    }
  };
  const onDisconnect = async () => {
    setDisconnecting(true);
    await ConnectionUtil.disconnect();
    setDisconnecting(false);
  };
  return /*#__PURE__*/React.createElement(FlatList, {
    data: networks,
    fadingEdgeLength: 20,
    ListHeaderComponentStyle: styles.header,
    ListHeaderComponent: /*#__PURE__*/React.createElement(Text, {
      variant: "small-400",
      color: "fg-200",
      center: true
    }, "The swap feature doesn't support your current network. Switch to an available option to continue."),
    contentContainerStyle: styles.contentContainer,
    renderItem: ({
      item
    }) => /*#__PURE__*/React.createElement(ListItem, {
      key: item.id,
      icon: "networkPlaceholder",
      iconBackgroundColor: "gray-glass-010",
      imageSrc: AssetUtil.getNetworkImage(item, networkImages),
      imageHeaders: imageHeaders,
      onPress: () => onNetworkPress(item),
      testID: "button-network",
      style: styles.networkItem,
      contentStyle: styles.networkItemContent,
      disabled: !supportsAllNetworks && !approvedCaipNetworkIds?.includes(item.id)
    }, /*#__PURE__*/React.createElement(Text, {
      numberOfLines: 1,
      color: "fg-100"
    }, item.name ?? 'Unknown'), item.id === caipNetwork?.id && /*#__PURE__*/React.createElement(Icon, {
      name: "checkmark",
      color: "accent-100"
    })),
    ListFooterComponent: /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(Separator, {
      text: "or",
      style: styles.separator
    }), /*#__PURE__*/React.createElement(ListItem, {
      icon: "disconnect",
      onPress: onDisconnect,
      loading: disconnecting,
      iconBackgroundColor: "gray-glass-010",
      testID: "button-disconnect"
    }, /*#__PURE__*/React.createElement(Text, {
      color: "fg-200"
    }, "Disconnect")))
  });
}
//# sourceMappingURL=index.js.map