import './config/animations';
import { AccountController, BlockchainApiController, ConnectionController, ConnectorController, EnsController, EventsController, ModalController, NetworkController, OptionsController, PublicStateController, SnackController, StorageUtil, ThemeController, TransactionsController } from '@reown/appkit-core-react-native';
import { SIWEController } from '@reown/appkit-siwe-react-native';
import { ConstantsUtil, ErrorUtil } from '@reown/appkit-common-react-native';
import { Appearance } from 'react-native';

// -- Types ---------------------------------------------------------------------

// -- Client --------------------------------------------------------------------
export class AppKitScaffold {
  reportedAlertErrors = {};
  constructor(options) {
    this.initControllers(options);
  }

  // -- Public -------------------------------------------------------------------
  async open(options) {
    ModalController.open(options);
  }
  async close() {
    ModalController.close();
  }
  getThemeMode() {
    return ThemeController.state.themeMode;
  }
  getThemeVariables() {
    return ThemeController.state.themeVariables;
  }
  setThemeMode(themeMode) {
    ThemeController.setThemeMode(themeMode);
  }
  setThemeVariables(themeVariables) {
    ThemeController.setThemeVariables(themeVariables);
  }
  subscribeTheme(callback) {
    return ThemeController.subscribe(callback);
  }
  getWalletInfo() {
    return AccountController.state.connectedWalletInfo;
  }
  subscribeWalletInfo(callback) {
    return AccountController.subscribeKey('connectedWalletInfo', callback);
  }
  getState() {
    return {
      ...PublicStateController.state
    };
  }
  subscribeState(callback) {
    return PublicStateController.subscribe(callback);
  }
  subscribeStateKey(key, callback) {
    return PublicStateController.subscribeKey(key, callback);
  }
  subscribeConnection(callback) {
    return AccountController.subscribeKey('isConnected', callback);
  }
  setLoading(loading) {
    ModalController.setLoading(loading);
  }
  getEvent() {
    return {
      ...EventsController.state
    };
  }
  subscribeEvents(callback) {
    return EventsController.subscribe(callback);
  }
  subscribeEvent(event, callback) {
    return EventsController.subscribeEvent(event, callback);
  }
  resolveReownName = async name => {
    const wcNameAddress = await EnsController.resolveName(name);
    const networkNameAddresses = wcNameAddress?.addresses ? Object.values(wcNameAddress?.addresses) : [];
    return networkNameAddresses[0]?.address || false;
  };

  // -- Protected ----------------------------------------------------------------
  setIsConnected = isConnected => {
    AccountController.setIsConnected(isConnected);
  };
  setCaipAddress = caipAddress => {
    AccountController.setCaipAddress(caipAddress);
  };
  getCaipAddress = () => AccountController.state.caipAddress;
  setBalance = (balance, balanceSymbol) => {
    AccountController.setBalance(balance, balanceSymbol);
  };
  setProfileName = profileName => {
    AccountController.setProfileName(profileName);
  };
  setProfileImage = profileImage => {
    AccountController.setProfileImage(profileImage);
  };
  resetAccount = () => {
    AccountController.resetAccount();
  };
  setCaipNetwork = caipNetwork => {
    NetworkController.setCaipNetwork(caipNetwork);
  };
  getCaipNetwork = () => NetworkController.state.caipNetwork;
  setRequestedCaipNetworks = requestedCaipNetworks => {
    NetworkController.setRequestedCaipNetworks(requestedCaipNetworks);
  };
  getApprovedCaipNetworksData = connectorType => NetworkController.getApprovedCaipNetworksData(connectorType);
  resetNetwork = () => {
    NetworkController.resetNetwork();
  };
  setConnectors = connectors => {
    ConnectorController.setConnectors(connectors);
    this.setConnectorExcludedWallets(connectors);
  };
  addConnector = connector => {
    ConnectorController.addConnector(connector);
  };
  getConnectors = () => ConnectorController.getConnectors();
  resetWcConnection = () => {
    ConnectionController.resetWcConnection();
    TransactionsController.resetTransactions();
  };
  fetchIdentity = request => BlockchainApiController.fetchIdentity(request);
  setAddressExplorerUrl = addressExplorerUrl => {
    AccountController.setAddressExplorerUrl(addressExplorerUrl);
  };
  setConnectedWalletInfo = connectedWalletInfo => {
    AccountController.setConnectedWalletInfo(connectedWalletInfo);
  };
  setClientId = clientId => {
    BlockchainApiController.setClientId(clientId);
  };
  setPreferredAccountType = preferredAccountType => {
    AccountController.setPreferredAccountType(preferredAccountType);
  };
  handleAlertError(error) {
    if (!error) return;
    if (typeof error === 'object') {
      SnackController.showInternalError(error);
      return;
    }

    // Check if the error is a universal provider error
    const matchedUniversalProviderError = Object.entries(ErrorUtil.UniversalProviderErrors).find(([, {
      message
    }]) => error?.includes(message));
    const [errorKey, errorValue] = matchedUniversalProviderError ?? [];
    const {
      message,
      alertErrorKey
    } = errorValue ?? {};
    if (errorKey && message && !this.reportedAlertErrors[errorKey]) {
      const alertError = ErrorUtil.ALERT_ERRORS[alertErrorKey];
      if (alertError) {
        SnackController.showInternalError(alertError);
        this.reportedAlertErrors[errorKey] = true;
      }
    }
  }

  // -- Private ------------------------------------------------------------------
  async initControllers(options) {
    this.initAsyncValues(options);
    NetworkController.setClient(options.networkControllerClient);
    NetworkController.setDefaultCaipNetwork(options.defaultChain);
    OptionsController.setProjectId(options.projectId);
    OptionsController.setIncludeWalletIds(options.includeWalletIds);
    OptionsController.setExcludeWalletIds(options.excludeWalletIds);
    OptionsController.setFeaturedWalletIds(options.featuredWalletIds);
    OptionsController.setTokens(options.tokens);
    OptionsController.setCustomWallets(options.customWallets);
    OptionsController.setEnableAnalytics(options.enableAnalytics);
    OptionsController.setSdkVersion(options._sdkVersion);
    OptionsController.setDebug(options.debug);
    if (options.clipboardClient) {
      OptionsController.setClipboardClient(options.clipboardClient);
    }
    ConnectionController.setClient(options.connectionControllerClient);
    if (options.themeMode) {
      ThemeController.setThemeMode(options.themeMode);
    } else {
      ThemeController.setThemeMode(Appearance.getColorScheme());
    }
    if (options.themeVariables) {
      ThemeController.setThemeVariables(options.themeVariables);
    }
    if (options.metadata) {
      OptionsController.setMetadata(options.metadata);
    }
    if (options.siweControllerClient) {
      SIWEController.setSIWEClient(options.siweControllerClient);
    }
    if (options.features) {
      OptionsController.setFeatures(options.features);
    }
    if ((options.features?.onramp === true || options.features?.onramp === undefined) && (options.metadata?.redirect?.universal || options.metadata?.redirect?.native)) {
      OptionsController.setIsOnRampEnabled(true);
    }
  }
  async setConnectorExcludedWallets(connectors) {
    const excludedWallets = OptionsController.state.excludeWalletIds || [];

    // Exclude Coinbase if the connector is not implemented
    const excludeCoinbase = connectors.findIndex(connector => connector.id === ConstantsUtil.COINBASE_CONNECTOR_ID) === -1;
    if (excludeCoinbase) {
      excludedWallets.push(ConstantsUtil.COINBASE_EXPLORER_ID);
    }
    OptionsController.setExcludeWalletIds(excludedWallets);
  }
  async initRecentWallets(options) {
    const wallets = await StorageUtil.getRecentWallets();
    const connectedWalletImage = await StorageUtil.getConnectedWalletImageUrl();
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
    ConnectionController.setRecentWallets(filteredWallets);
    if (connectedWalletImage) {
      ConnectionController.setConnectedWalletImageUrl(connectedWalletImage);
    }
  }
  async initConnectedConnector() {
    const connectedConnector = await StorageUtil.getConnectedConnector();
    if (connectedConnector) {
      ConnectorController.setConnectedConnector(connectedConnector, false);
    }
  }
  async initSocial() {
    const connectedSocialProvider = await StorageUtil.getConnectedSocialProvider();
    ConnectionController.setConnectedSocialProvider(connectedSocialProvider);
  }
  async initAsyncValues(options) {
    await this.initConnectedConnector();
    await this.initRecentWallets(options);
    await this.initSocial();
  }
}
//# sourceMappingURL=client.js.map