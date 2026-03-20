"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.AppKitScaffold = void 0;
require("./config/animations");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _appkitSiweReactNative = require("@reown/appkit-siwe-react-native");
var _appkitCommonReactNative = require("@reown/appkit-common-react-native");
var _reactNative = require("react-native");
// -- Types ---------------------------------------------------------------------

// -- Client --------------------------------------------------------------------
class AppKitScaffold {
  reportedAlertErrors = {};
  constructor(options) {
    this.initControllers(options);
  }

  // -- Public -------------------------------------------------------------------
  async open(options) {
    _appkitCoreReactNative.ModalController.open(options);
  }
  async close() {
    _appkitCoreReactNative.ModalController.close();
  }
  getThemeMode() {
    return _appkitCoreReactNative.ThemeController.state.themeMode;
  }
  getThemeVariables() {
    return _appkitCoreReactNative.ThemeController.state.themeVariables;
  }
  setThemeMode(themeMode) {
    _appkitCoreReactNative.ThemeController.setThemeMode(themeMode);
  }
  setThemeVariables(themeVariables) {
    _appkitCoreReactNative.ThemeController.setThemeVariables(themeVariables);
  }
  subscribeTheme(callback) {
    return _appkitCoreReactNative.ThemeController.subscribe(callback);
  }
  getWalletInfo() {
    return _appkitCoreReactNative.AccountController.state.connectedWalletInfo;
  }
  subscribeWalletInfo(callback) {
    return _appkitCoreReactNative.AccountController.subscribeKey('connectedWalletInfo', callback);
  }
  getState() {
    return {
      ..._appkitCoreReactNative.PublicStateController.state
    };
  }
  subscribeState(callback) {
    return _appkitCoreReactNative.PublicStateController.subscribe(callback);
  }
  subscribeStateKey(key, callback) {
    return _appkitCoreReactNative.PublicStateController.subscribeKey(key, callback);
  }
  subscribeConnection(callback) {
    return _appkitCoreReactNative.AccountController.subscribeKey('isConnected', callback);
  }
  setLoading(loading) {
    _appkitCoreReactNative.ModalController.setLoading(loading);
  }
  getEvent() {
    return {
      ..._appkitCoreReactNative.EventsController.state
    };
  }
  subscribeEvents(callback) {
    return _appkitCoreReactNative.EventsController.subscribe(callback);
  }
  subscribeEvent(event, callback) {
    return _appkitCoreReactNative.EventsController.subscribeEvent(event, callback);
  }
  resolveReownName = async name => {
    const wcNameAddress = await _appkitCoreReactNative.EnsController.resolveName(name);
    const networkNameAddresses = wcNameAddress?.addresses ? Object.values(wcNameAddress?.addresses) : [];
    return networkNameAddresses[0]?.address || false;
  };

  // -- Protected ----------------------------------------------------------------
  setIsConnected = isConnected => {
    _appkitCoreReactNative.AccountController.setIsConnected(isConnected);
  };
  setCaipAddress = caipAddress => {
    _appkitCoreReactNative.AccountController.setCaipAddress(caipAddress);
  };
  getCaipAddress = () => _appkitCoreReactNative.AccountController.state.caipAddress;
  setBalance = (balance, balanceSymbol) => {
    _appkitCoreReactNative.AccountController.setBalance(balance, balanceSymbol);
  };
  setProfileName = profileName => {
    _appkitCoreReactNative.AccountController.setProfileName(profileName);
  };
  setProfileImage = profileImage => {
    _appkitCoreReactNative.AccountController.setProfileImage(profileImage);
  };
  resetAccount = () => {
    _appkitCoreReactNative.AccountController.resetAccount();
  };
  setCaipNetwork = caipNetwork => {
    _appkitCoreReactNative.NetworkController.setCaipNetwork(caipNetwork);
  };
  getCaipNetwork = () => _appkitCoreReactNative.NetworkController.state.caipNetwork;
  setRequestedCaipNetworks = requestedCaipNetworks => {
    _appkitCoreReactNative.NetworkController.setRequestedCaipNetworks(requestedCaipNetworks);
  };
  getApprovedCaipNetworksData = connectorType => _appkitCoreReactNative.NetworkController.getApprovedCaipNetworksData(connectorType);
  resetNetwork = () => {
    _appkitCoreReactNative.NetworkController.resetNetwork();
  };
  setConnectors = connectors => {
    _appkitCoreReactNative.ConnectorController.setConnectors(connectors);
    this.setConnectorExcludedWallets(connectors);
  };
  addConnector = connector => {
    _appkitCoreReactNative.ConnectorController.addConnector(connector);
  };
  getConnectors = () => _appkitCoreReactNative.ConnectorController.getConnectors();
  resetWcConnection = () => {
    _appkitCoreReactNative.ConnectionController.resetWcConnection();
    _appkitCoreReactNative.TransactionsController.resetTransactions();
  };
  fetchIdentity = request => _appkitCoreReactNative.BlockchainApiController.fetchIdentity(request);
  setAddressExplorerUrl = addressExplorerUrl => {
    _appkitCoreReactNative.AccountController.setAddressExplorerUrl(addressExplorerUrl);
  };
  setConnectedWalletInfo = connectedWalletInfo => {
    _appkitCoreReactNative.AccountController.setConnectedWalletInfo(connectedWalletInfo);
  };
  setClientId = clientId => {
    _appkitCoreReactNative.BlockchainApiController.setClientId(clientId);
  };
  setPreferredAccountType = preferredAccountType => {
    _appkitCoreReactNative.AccountController.setPreferredAccountType(preferredAccountType);
  };
  handleAlertError(error) {
    if (!error) return;
    if (typeof error === 'object') {
      _appkitCoreReactNative.SnackController.showInternalError(error);
      return;
    }

    // Check if the error is a universal provider error
    const matchedUniversalProviderError = Object.entries(_appkitCommonReactNative.ErrorUtil.UniversalProviderErrors).find(([, {
      message
    }]) => error?.includes(message));
    const [errorKey, errorValue] = matchedUniversalProviderError ?? [];
    const {
      message,
      alertErrorKey
    } = errorValue ?? {};
    if (errorKey && message && !this.reportedAlertErrors[errorKey]) {
      const alertError = _appkitCommonReactNative.ErrorUtil.ALERT_ERRORS[alertErrorKey];
      if (alertError) {
        _appkitCoreReactNative.SnackController.showInternalError(alertError);
        this.reportedAlertErrors[errorKey] = true;
      }
    }
  }

  // -- Private ------------------------------------------------------------------
  async initControllers(options) {
    this.initAsyncValues(options);
    _appkitCoreReactNative.NetworkController.setClient(options.networkControllerClient);
    _appkitCoreReactNative.NetworkController.setDefaultCaipNetwork(options.defaultChain);
    _appkitCoreReactNative.OptionsController.setProjectId(options.projectId);
    _appkitCoreReactNative.OptionsController.setIncludeWalletIds(options.includeWalletIds);
    _appkitCoreReactNative.OptionsController.setExcludeWalletIds(options.excludeWalletIds);
    _appkitCoreReactNative.OptionsController.setFeaturedWalletIds(options.featuredWalletIds);
    _appkitCoreReactNative.OptionsController.setTokens(options.tokens);
    _appkitCoreReactNative.OptionsController.setCustomWallets(options.customWallets);
    _appkitCoreReactNative.OptionsController.setEnableAnalytics(options.enableAnalytics);
    _appkitCoreReactNative.OptionsController.setSdkVersion(options._sdkVersion);
    _appkitCoreReactNative.OptionsController.setDebug(options.debug);
    if (options.clipboardClient) {
      _appkitCoreReactNative.OptionsController.setClipboardClient(options.clipboardClient);
    }
    _appkitCoreReactNative.ConnectionController.setClient(options.connectionControllerClient);
    if (options.themeMode) {
      _appkitCoreReactNative.ThemeController.setThemeMode(options.themeMode);
    } else {
      _appkitCoreReactNative.ThemeController.setThemeMode(_reactNative.Appearance.getColorScheme());
    }
    if (options.themeVariables) {
      _appkitCoreReactNative.ThemeController.setThemeVariables(options.themeVariables);
    }
    if (options.metadata) {
      _appkitCoreReactNative.OptionsController.setMetadata(options.metadata);
    }
    if (options.siweControllerClient) {
      _appkitSiweReactNative.SIWEController.setSIWEClient(options.siweControllerClient);
    }
    if (options.features) {
      _appkitCoreReactNative.OptionsController.setFeatures(options.features);
    }
    if ((options.features?.onramp === true || options.features?.onramp === undefined) && (options.metadata?.redirect?.universal || options.metadata?.redirect?.native)) {
      _appkitCoreReactNative.OptionsController.setIsOnRampEnabled(true);
    }
  }
  async setConnectorExcludedWallets(connectors) {
    const excludedWallets = _appkitCoreReactNative.OptionsController.state.excludeWalletIds || [];

    // Exclude Coinbase if the connector is not implemented
    const excludeCoinbase = connectors.findIndex(connector => connector.id === _appkitCommonReactNative.ConstantsUtil.COINBASE_CONNECTOR_ID) === -1;
    if (excludeCoinbase) {
      excludedWallets.push(_appkitCommonReactNative.ConstantsUtil.COINBASE_EXPLORER_ID);
    }
    _appkitCoreReactNative.OptionsController.setExcludeWalletIds(excludedWallets);
  }
  async initRecentWallets(options) {
    const wallets = await _appkitCoreReactNative.StorageUtil.getRecentWallets();
    const connectedWalletImage = await _appkitCoreReactNative.StorageUtil.getConnectedWalletImageUrl();
    const filteredWallets = wallets.filter(wallet => {
      const {
        includeWalletIds,
        excludeWalletIds
      } = options;
      if (includeWalletIds) {
        return includeWalletIds.includes(wallet.id);
      }
      if (excludeWalletIds) {
        return !excludeWalletIds.includes(wallet.id);
      }
      return true;
    });
    _appkitCoreReactNative.ConnectionController.setRecentWallets(filteredWallets);
    if (connectedWalletImage) {
      _appkitCoreReactNative.ConnectionController.setConnectedWalletImageUrl(connectedWalletImage);
    }
  }
  async initConnectedConnector() {
    const connectedConnector = await _appkitCoreReactNative.StorageUtil.getConnectedConnector();
    if (connectedConnector) {
      _appkitCoreReactNative.ConnectorController.setConnectedConnector(connectedConnector, false);
    }
  }
  async initSocial() {
    const connectedSocialProvider = await _appkitCoreReactNative.StorageUtil.getConnectedSocialProvider();
    _appkitCoreReactNative.ConnectionController.setConnectedSocialProvider(connectedSocialProvider);
  }
  async initAsyncValues(options) {
    await this.initConnectedConnector();
    await this.initRecentWallets(options);
    await this.initSocial();
  }
}
exports.AppKitScaffold = AppKitScaffold;
//# sourceMappingURL=client.js.map