import { useSnapshot } from 'valtio';
import { ScrollView, View } from 'react-native';
import { CardSelect, CardSelectWidth, FlexView, Link, Separator, Spacing, Text } from '@reown/appkit-ui-react-native';
import { ApiController, AssetUtil, NetworkController, RouterController, EventsController, CoreHelperUtil, NetworkUtil, AssetController } from '@reown/appkit-core-react-native';
import { useCustomDimensions } from '../../hooks/useCustomDimensions';
import styles from './styles';
export function NetworksView() {
  const {
    caipNetwork,
    requestedCaipNetworks,
    approvedCaipNetworkIds,
    supportsAllNetworks
  } = NetworkController.state;
  const {
    networkImages
  } = useSnapshot(AssetController.state);
  const imageHeaders = ApiController._getApiHeaders();
  const {
    maxWidth: width,
    padding
  } = useCustomDimensions();
  const numColumns = 4;
  const usableWidth = width - Spacing.xs * 2 - Spacing['4xs'];
  const itemWidth = Math.abs(Math.trunc(usableWidth / numColumns));
  const itemGap = Math.abs(Math.trunc((usableWidth - numColumns * CardSelectWidth) / numColumns) / 2);
  const onHelpPress = () => {
    RouterController.push('WhatIsANetwork');
    EventsController.sendEvent({
      type: 'track',
      event: 'CLICK_NETWORK_HELP'
    });
  };
  const networksTemplate = () => {
    const networks = CoreHelperUtil.sortNetworks(approvedCaipNetworkIds, requestedCaipNetworks);
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
    return networks.map(network => /*#__PURE__*/React.createElement(View, {
      key: network.id,
      style: [styles.itemContainer, {
        width: itemWidth,
        marginVertical: itemGap
      }]
    }, /*#__PURE__*/React.createElement(CardSelect, {
      testID: `w3m-network-switch-${network.name ?? network.id}`,
      name: network.name ?? 'Unknown',
      type: "network",
      imageSrc: AssetUtil.getNetworkImage(network, networkImages),
      imageHeaders: imageHeaders,
      disabled: !supportsAllNetworks && !approvedCaipNetworkIds?.includes(network.id),
      selected: caipNetwork?.id === network.id,
      onPress: () => onNetworkPress(network)
    })));
  };
  return /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(ScrollView, {
    bounces: false,
    fadingEdgeLength: 20,
    style: {
      paddingHorizontal: padding
    }
  }, /*#__PURE__*/React.createElement(FlexView, {
    flexDirection: "row",
    flexWrap: "wrap",
    padding: ['xs', 'xs', 's', 'xs']
  }, networksTemplate())), /*#__PURE__*/React.createElement(Separator, null), /*#__PURE__*/React.createElement(FlexView, {
    padding: ['s', 's', '3xl', 's'],
    alignItems: "center",
    alignSelf: "center",
    style: {
      width
    }
  }, /*#__PURE__*/React.createElement(Text, {
    variant: "small-400",
    color: "fg-300",
    center: true
  }, "Your connected wallet may not support some of the networks available for this dApp"), /*#__PURE__*/React.createElement(Link, {
    size: "sm",
    iconLeft: "helpCircle",
    onPress: onHelpPress,
    style: styles.helpButton,
    testID: "what-is-a-network-button"
  }, "What is a network?")));
}
//# sourceMappingURL=index.js.map