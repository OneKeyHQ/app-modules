import { useSnapshot } from 'valtio';
import { ConnectorController, AssetUtil, RouterController, ApiController } from '@reown/appkit-core-react-native';
import { ListWallet } from '@reown/appkit-ui-react-native';
export function ConnectorList({
  itemStyle,
  isWalletConnectEnabled
}) {
  const {
    connectors
  } = useSnapshot(ConnectorController.state);
  const excludeConnectors = ['WALLET_CONNECT', 'AUTH'];
  const imageHeaders = ApiController._getApiHeaders();
  if (isWalletConnectEnabled) {
    // use wallet from api list
    excludeConnectors.push('COINBASE');
  }
  return connectors.map(connector => {
    if (excludeConnectors.includes(connector.type)) {
      return null;
    }
    return /*#__PURE__*/React.createElement(ListWallet, {
      key: connector.type,
      imageSrc: AssetUtil.getConnectorImage(connector),
      imageHeaders: imageHeaders,
      name: connector.name || 'Unknown',
      onPress: () => RouterController.push('ConnectingExternal', {
        connector
      }),
      style: itemStyle,
      installed: connector.installed
    });
  });
}
//# sourceMappingURL=connectors-list.js.map