"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.AllWalletsView = AllWalletsView;
var _react = require("react");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _styles = _interopRequireDefault(require("./styles"));
var _useDebounceCallback = require("../../hooks/useDebounceCallback");
var _w3mAllWalletsList = require("../../partials/w3m-all-wallets-list");
var _w3mAllWalletsSearch = require("../../partials/w3m-all-wallets-search");
var _useCustomDimensions = require("../../hooks/useCustomDimensions");
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
function AllWalletsView() {
  const Theme = (0, _appkitUiReactNative.useTheme)();
  const [searchQuery, setSearchQuery] = (0, _react.useState)('');
  const {
    maxWidth
  } = (0, _useCustomDimensions.useCustomDimensions)();
  const numColumns = 4;
  const usableWidth = maxWidth - _appkitUiReactNative.Spacing.xs * 2;
  const itemWidth = Math.abs(Math.trunc(usableWidth / numColumns));
  const {
    debouncedCallback: onInputChange
  } = (0, _useDebounceCallback.useDebounceCallback)({
    callback: setSearchQuery
  });
  const onWalletPress = wallet => {
    const connector = _appkitCoreReactNative.ConnectorController.state.connectors.find(c => c.explorerId === wallet.id);
    if (connector) {
      _appkitCoreReactNative.RouterController.push('ConnectingExternal', {
        connector,
        wallet
      });
    } else {
      _appkitCoreReactNative.RouterController.push('ConnectingWalletConnect', {
        wallet
      });
    }
    _appkitCoreReactNative.EventsController.sendEvent({
      type: 'track',
      event: 'SELECT_WALLET',
      properties: {
        name: wallet.name ?? 'Unknown',
        platform: 'mobile',
        explorer_id: wallet.id
      }
    });
  };
  const onQrCodePress = () => {
    _appkitCoreReactNative.ConnectionController.removePressedWallet();
    _appkitCoreReactNative.ConnectionController.removeWcLinking();
    _appkitCoreReactNative.RouterController.push('ConnectingWalletConnect');
    _appkitCoreReactNative.EventsController.sendEvent({
      type: 'track',
      event: 'SELECT_WALLET',
      properties: {
        name: 'WalletConnect',
        platform: 'qrcode'
      }
    });
  };
  const headerTemplate = () => {
    return /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
      padding: ['s', 'l', 'xs', 'l'],
      flexDirection: "row",
      alignItems: "center",
      style: [_styles.default.header, {
        backgroundColor: Theme['bg-100'],
        shadowColor: Theme['bg-100'],
        width: maxWidth
      }]
    }, /*#__PURE__*/React.createElement(_appkitUiReactNative.SearchBar, {
      onChangeText: onInputChange,
      placeholder: "Search wallet",
      style: _styles.default.searchBar
    }), /*#__PURE__*/React.createElement(_appkitUiReactNative.IconLink, {
      icon: "qrCode",
      iconColor: "accent-100",
      pressedColor: "accent-glass-020",
      backgroundColor: "accent-glass-010",
      size: "lg",
      onPress: onQrCodePress,
      style: [_styles.default.icon, {
        borderColor: Theme['accent-glass-010']
      }],
      testID: "button-qr-code"
    }));
  };
  const listTemplate = () => {
    if (searchQuery) {
      return /*#__PURE__*/React.createElement(_w3mAllWalletsSearch.AllWalletsSearch, {
        columns: numColumns,
        itemWidth: itemWidth,
        searchQuery: searchQuery,
        onItemPress: onWalletPress
      });
    }
    return /*#__PURE__*/React.createElement(_w3mAllWalletsList.AllWalletsList, {
      columns: numColumns,
      itemWidth: itemWidth,
      onItemPress: onWalletPress
    });
  };
  return /*#__PURE__*/React.createElement(React.Fragment, null, headerTemplate(), listTemplate());
}
//# sourceMappingURL=index.js.map