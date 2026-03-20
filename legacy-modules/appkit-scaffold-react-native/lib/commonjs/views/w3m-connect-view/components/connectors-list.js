"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.ConnectorList = ConnectorList;
var _valtio = require("valtio");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
function ConnectorList({
  itemStyle,
  isWalletConnectEnabled
}) {
  const {
    connectors
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.ConnectorController.state);
  const excludeConnectors = ['WALLET_CONNECT', 'AUTH'];
  const imageHeaders = _appkitCoreReactNative.ApiController._getApiHeaders();
  if (isWalletConnectEnabled) {
    // use wallet from api list
    excludeConnectors.push('COINBASE');
  }
  return connectors.map(connector => {
    if (excludeConnectors.includes(connector.type)) {
      return null;
    }
    return /*#__PURE__*/React.createElement(_appkitUiReactNative.ListWallet, {
      key: connector.type,
      imageSrc: _appkitCoreReactNative.AssetUtil.getConnectorImage(connector),
      imageHeaders: imageHeaders,
      name: connector.name || 'Unknown',
      onPress: () => _appkitCoreReactNative.RouterController.push('ConnectingExternal', {
        connector
      }),
      style: itemStyle,
      installed: connector.installed
    });
  });
}
//# sourceMappingURL=connectors-list.js.map