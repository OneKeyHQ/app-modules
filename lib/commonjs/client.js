"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.AppKit = void 0;
var _ethers = require("ethers");
var _appkitScaffoldReactNative = require("@reown/appkit-scaffold-react-native");
var _appkitScaffoldUtilsReactNative = require("@reown/appkit-scaffold-utils-react-native");
var _appkitSiweReactNative = require("@reown/appkit-siwe-react-native");
var _appkitCommonReactNative = require("@reown/appkit-common-react-native");
var _ethereumProvider = _interopRequireWildcard(require("@walletconnect/ethereum-provider"));
var _helpers = require("./utils/helpers");
function _getRequireWildcardCache(e) { if ("function" != typeof WeakMap) return null; var r = new WeakMap(), t = new WeakMap(); return (_getRequireWildcardCache = function (e) { return e ? t : r; })(e); }
function _interopRequireWildcard(e, r) { if (!r && e && e.__esModule) return e; if (null === e || "object" != typeof e && "function" != typeof e) return { default: e }; var t = _getRequireWildcardCache(r); if (t && t.has(e)) return t.get(e); var n = { __proto__: null }, a = Object.defineProperty && Object.getOwnPropertyDescriptor; for (var u in e) if ("default" !== u && {}.hasOwnProperty.call(e, u)) { var i = a ? Object.getOwnPropertyDescriptor(e, u) : null; i && (i.get || i.set) ? Object.defineProperty(n, u, i) : n[u] = e[u]; } return n.default = e, t && t.set(e, n), n; }
// -- Types ---------------------------------------------------------------------

// @ts-expect-error: Overriden state type is correct

// -- Client --------------------------------------------------------------------
class AppKit extends _appkitScaffoldReactNative.AppKitScaffold {
  hasSyncedConnectedAccount = false;
  options = undefined;
  constructor(options) {
    const {
      config,
      siweConfig,
      chains,
      defaultChain,
      tokens,
      chainImages,
      _sdkVersion,
      ...appKitOptions
    } = options;
    if (!config) {
      throw new Error('appkit:constructor - config is undefined');
    }
    if (!appKitOptions.projectId) {
      throw new Error(_appkitCommonReactNative.ErrorUtil.ALERT_ERRORS.PROJECT_ID_NOT_CONFIGURED.shortMessage);
    }
    const networkControllerClient = {
      switchCaipNetwork: async caipNetwork => {
        const chainId = _appkitCommonReactNative.NetworkUtil.caipNetworkIdToNumber(caipNetwork?.id);
        if (chainId) {
          try {
            await this.switchNetwork(chainId);
          } catch (error) {
            _appkitScaffoldUtilsReactNative.EthersStoreUtil.setError(error);
          }
        }
      },
      getApprovedCaipNetworksData: async () => new Promise(async resolve => {
        const walletId = await _appkitScaffoldUtilsReactNative.StorageUtil.getItem(_appkitScaffoldUtilsReactNative.EthersConstantsUtil.WALLET_ID);
        const walletChoice = _appkitCommonReactNative.PresetsUtil.ConnectorTypesMap[walletId ?? ''];
        const walletConnectType = _appkitCommonReactNative.PresetsUtil.ConnectorTypesMap[_appkitCommonReactNative.ConstantsUtil.WALLET_CONNECT_CONNECTOR_ID];
        const authType = _appkitCommonReactNative.PresetsUtil.ConnectorTypesMap[_appkitCommonReactNative.ConstantsUtil.AUTH_CONNECTOR_ID];
        if (walletChoice === walletConnectType) {
          const provider = await this.getWalletConnectProvider();
          const result = (0, _helpers.getWalletConnectCaipNetworks)(provider);
          resolve(result);
        } else if (walletChoice === authType) {
          const result = (0, _helpers.getAuthCaipNetworks)();
          resolve(result);
        } else {
          const result = {
            approvedCaipNetworkIds: undefined,
            supportsAllNetworks: true
          };
          resolve(result);
        }
      })
    };
    const connectionControllerClient = {
      connectWalletConnect: async onUri => {
        const WalletConnectProvider = await this.getWalletConnectProvider();
        if (!WalletConnectProvider) {
          throw new Error('connectionControllerClient:getWalletConnectUri - provider is undefined');
        }
        WalletConnectProvider.on('display_uri', uri => {
          onUri(uri);
        });

        // When connecting through walletconnect, we need to set the clientId in the store
        const clientId = await WalletConnectProvider.signer?.client?.core?.crypto?.getClientId();
        if (clientId) {
          this.setClientId(clientId);
        }

        // SIWE
        const params = await siweConfig?.getMessageParams?.();
        if (siweConfig?.options?.enabled && params && Object.keys(params).length > 0) {
          const result = await WalletConnectProvider.authenticate({
            nonce: await siweConfig.getNonce(),
            methods: _ethereumProvider.OPTIONAL_METHODS,
            ...params
          });
          // Auths is an array of signed CACAO objects https://github.com/ChainAgnostic/CAIPs/blob/main/CAIPs/caip-74.md
          const signedCacao = result?.auths?.[0];
          if (signedCacao) {
            const {
              p,
              s
            } = signedCacao;
            const chainId = (0, _appkitSiweReactNative.getDidChainId)(p.iss);
            const address = (0, _appkitSiweReactNative.getDidAddress)(p.iss);
            try {
              // Kicks off verifyMessage and populates external states
              const message = WalletConnectProvider.signer.client.formatAuthMessage({
                request: p,
                iss: p.iss
              });
              await _appkitSiweReactNative.SIWEController.verifyMessage({
                message,
                signature: s.s,
                cacao: signedCacao
              });
              if (address && chainId) {
                const session = {
                  address,
                  chainId: parseInt(chainId, 10)
                };
                _appkitSiweReactNative.SIWEController.setSession(session);
                _appkitSiweReactNative.SIWEController.onSignIn?.(session);
              }
            } catch (error) {
              // eslint-disable-next-line no-console
              console.error('Error verifying message', error);
              // eslint-disable-next-line no-console
              await WalletConnectProvider.disconnect().catch(console.error);
              // eslint-disable-next-line no-console
              await _appkitSiweReactNative.SIWEController.signOut().catch(console.error);
              throw error;
            }
          }
        } else {
          await WalletConnectProvider.connect({
            chains: [],
            optionalChains: [...this.chains.map(chain => chain.chainId)]
          });
        }
        await this.setWalletConnectProvider();
      },
      //  @ts-expect-error TODO expected types in arguments are incomplete
      connectExternal: async ({
        id
      }) => {
        // If connecting with something else than walletconnect, we need to clear the clientId in the store
        this.setClientId(null);
        if (id === _appkitCommonReactNative.ConstantsUtil.COINBASE_CONNECTOR_ID) {
          const coinbaseProvider = config.extraConnectors?.find(connector => connector.id === id);
          if (!coinbaseProvider) {
            throw new Error('connectionControllerClient:connectCoinbase - connector is undefined');
          }
          try {
            await coinbaseProvider.request({
              method: 'eth_requestAccounts'
            });
            await this.setCoinbaseProvider(coinbaseProvider);
          } catch (error) {
            _appkitScaffoldUtilsReactNative.EthersStoreUtil.setError(error);
          }
        } else if (id === _appkitCommonReactNative.ConstantsUtil.AUTH_CONNECTOR_ID) {
          await this.setAuthProvider();
        }
      },
      disconnect: async () => {
        const provider = _appkitScaffoldUtilsReactNative.EthersStoreUtil.state.provider;
        const providerType = _appkitScaffoldUtilsReactNative.EthersStoreUtil.state.providerType;
        const walletConnectType = _appkitCommonReactNative.PresetsUtil.ConnectorTypesMap[_appkitCommonReactNative.ConstantsUtil.WALLET_CONNECT_CONNECTOR_ID];
        const authType = _appkitCommonReactNative.PresetsUtil.ConnectorTypesMap[_appkitCommonReactNative.ConstantsUtil.AUTH_CONNECTOR_ID];
        if (siweConfig?.options?.signOutOnDisconnect) {
          await _appkitSiweReactNative.SIWEController.signOut();
        }
        if (providerType === walletConnectType) {
          const WalletConnectProvider = provider;
          await WalletConnectProvider.disconnect();
        } else if (providerType === authType) {
          await this.authProvider?.disconnect();
        } else if (provider) {
          provider.emit('disconnect');
        }
        _appkitScaffoldUtilsReactNative.StorageUtil.removeItem(_appkitScaffoldUtilsReactNative.EthersConstantsUtil.WALLET_ID);
        _appkitScaffoldUtilsReactNative.EthersStoreUtil.reset();
        this.setClientId(null);
      },
      signMessage: async message => {
        const provider = _appkitScaffoldUtilsReactNative.EthersStoreUtil.state.provider;
        if (!provider) {
          throw new Error('connectionControllerClient:signMessage - provider is undefined');
        }
        const hexMessage = _ethers.utils.isHexString(message) ? message : _ethers.utils.hexlify(_ethers.utils.toUtf8Bytes(message));
        const signature = await provider.request({
          method: 'personal_sign',
          params: [hexMessage, this.getAddress()]
        });
        return signature;
      },
      estimateGas: async ({
        address,
        to,
        data,
        chainNamespace
      }) => {
        const networkId = _appkitScaffoldUtilsReactNative.EthersStoreUtil.state.chainId;
        const provider = _appkitScaffoldUtilsReactNative.EthersStoreUtil.state.provider;
        if (!provider) {
          throw new Error('Provider is undefined');
        }
        try {
          if (!provider) {
            throw new Error('estimateGas - provider is undefined');
          }
          if (!address) {
            throw new Error('estimateGas - address is undefined');
          }
          if (chainNamespace && chainNamespace !== 'eip155') {
            throw new Error('estimateGas - chainNamespace is not eip155');
          }
          const txParams = {
            from: address,
            to,
            data,
            type: 0
          };
          const browserProvider = new _ethers.ethers.providers.Web3Provider(provider, networkId);
          const signer = browserProvider.getSigner(address);
          return (await signer.estimateGas(txParams)).toBigInt();
        } catch (error) {
          throw new Error('Ethers: estimateGas - Estimate gas failed');
        }
      },
      parseUnits: (value, decimals) => _ethers.ethers.utils.parseUnits(value, decimals).toBigInt(),
      formatUnits: (value, decimals) => _ethers.ethers.utils.formatUnits(value, decimals),
      sendTransaction: async data => {
        const provider = _appkitScaffoldUtilsReactNative.EthersStoreUtil.state.provider;
        const address = _appkitScaffoldUtilsReactNative.EthersStoreUtil.state.address;
        if (!provider) {
          throw new Error('connectionControllerClient:sendTransaction - provider is undefined');
        }
        if (!address) {
          throw new Error('connectionControllerClient:sendTransaction - address is undefined');
        }
        const txParams = {
          to: data.to,
          value: data.value,
          gasLimit: data.gas,
          gasPrice: data.gasPrice,
          data: data.data,
          type: 0
        };
        const browserProvider = new _ethers.ethers.providers.Web3Provider(provider);
        const signer = browserProvider.getSigner();
        const txResponse = await signer.sendTransaction(txParams);
        const txReceipt = await txResponse.wait();
        return txReceipt?.blockHash || null;
      },
      writeContract: async data => {
        const {
          chainId,
          provider,
          address
        } = _appkitScaffoldUtilsReactNative.EthersStoreUtil.state;
        if (!provider) {
          throw new Error('writeContract - provider is undefined');
        }
        if (!address) {
          throw new Error('writeContract - address is undefined');
        }
        const browserProvider = new _ethers.ethers.providers.Web3Provider(provider, chainId);
        const signer = browserProvider.getSigner(address);
        const contract = new _ethers.Contract(data.tokenAddress, data.abi, signer);
        if (!contract || !data.method) {
          throw new Error('Contract method is undefined');
        }
        const method = contract[data.method];
        if (method) {
          return await method(data.receiverAddress, data.tokenAmount);
        }
        throw new Error('Contract method is undefined');
      },
      getEnsAddress: async value => {
        try {
          const {
            chainId
          } = _appkitScaffoldUtilsReactNative.EthersStoreUtil.state;
          let ensName = null;
          let wcName = false;
          if (_appkitCommonReactNative.NamesUtil.isReownName(value)) {
            wcName = (await this.resolveReownName(value)) || false;
          }
          if (chainId === 1) {
            const ensProvider = new _ethers.ethers.providers.InfuraProvider('mainnet');
            ensName = await ensProvider.resolveName(value);
          }
          return ensName || wcName || false;
        } catch {
          return false;
        }
      },
      getEnsAvatar: async value => {
        const {
          chainId
        } = _appkitScaffoldUtilsReactNative.EthersStoreUtil.state;
        if (chainId === 1) {
          const ensProvider = new _ethers.ethers.providers.InfuraProvider('mainnet');
          const avatar = await ensProvider.getAvatar(value);
          return avatar || false;
        }
        return false;
      }
    };
    super({
      networkControllerClient,
      connectionControllerClient,
      siweControllerClient: siweConfig,
      defaultChain: _appkitScaffoldUtilsReactNative.EthersHelpersUtil.getCaipDefaultChain(defaultChain),
      tokens: _appkitScaffoldUtilsReactNative.HelpersUtil.getCaipTokens(tokens),
      _sdkVersion: _sdkVersion ?? `react-native-ethers5-${_appkitCommonReactNative.ConstantsUtil.VERSION}`,
      ...appKitOptions
    });
    this.options = options;
    this.metadata = config.metadata;
    this.projectId = appKitOptions.projectId;
    this.chains = chains;
    this.createProvider();
    _appkitScaffoldUtilsReactNative.EthersStoreUtil.subscribeKey('address', address => {
      this.syncAccount({
        address
      });
    });
    _appkitScaffoldUtilsReactNative.EthersStoreUtil.subscribeKey('chainId', () => {
      this.syncNetwork(chainImages);
    });
    _appkitScaffoldUtilsReactNative.EthersStoreUtil.subscribeKey('provider', provider => {
      this.syncConnectedWalletInfo(provider);
    });
    this.syncRequestedNetworks(chains, chainImages);
    this.syncConnectors(config);
    this.syncAuthConnector(config);
  }

  // -- Public ------------------------------------------------------------------

  // @ts-expect-error: Overriden state type is correct
  getState() {
    const state = super.getState();
    return {
      ...state,
      selectedNetworkId: _appkitCommonReactNative.NetworkUtil.caipNetworkIdToNumber(state.selectedNetworkId)
    };
  }

  // @ts-expect-error: Overriden state type is correct
  subscribeState(callback) {
    return super.subscribeState(state => callback({
      ...state,
      selectedNetworkId: _appkitCommonReactNative.NetworkUtil.caipNetworkIdToNumber(state.selectedNetworkId)
    }));
  }
  setAddress(address) {
    const originalAddress = address ? _ethers.utils.getAddress(address) : undefined;
    _appkitScaffoldUtilsReactNative.EthersStoreUtil.setAddress(originalAddress);
  }
  getAddress() {
    const {
      address
    } = _appkitScaffoldUtilsReactNative.EthersStoreUtil.state;
    return address ? _ethers.utils.getAddress(address) : address;
  }
  getError() {
    return _appkitScaffoldUtilsReactNative.EthersStoreUtil.state.error;
  }
  getChainId() {
    return _appkitScaffoldUtilsReactNative.EthersStoreUtil.state.chainId;
  }
  getIsConnected() {
    return _appkitScaffoldUtilsReactNative.EthersStoreUtil.state.isConnected;
  }
  getWalletProvider() {
    return _appkitScaffoldUtilsReactNative.EthersStoreUtil.state.provider;
  }
  getWalletProviderType() {
    return _appkitScaffoldUtilsReactNative.EthersStoreUtil.state.providerType;
  }
  subscribeProvider(callback) {
    return _appkitScaffoldUtilsReactNative.EthersStoreUtil.subscribe(callback);
  }
  async disconnect() {
    const {
      provider
    } = _appkitScaffoldUtilsReactNative.EthersStoreUtil.state;
    _appkitScaffoldUtilsReactNative.StorageUtil.removeItem(_appkitScaffoldUtilsReactNative.EthersConstantsUtil.WALLET_ID);
    _appkitScaffoldUtilsReactNative.EthersStoreUtil.reset();
    this.setClientId(null);
    await provider.disconnect();
  }

  // -- Private -----------------------------------------------------------------
  createProvider() {
    if (!this.walletConnectProviderInitPromise) {
      this.walletConnectProviderInitPromise = this.initWalletConnectProvider();
    }
    return this.walletConnectProviderInitPromise;
  }
  async initWalletConnectProvider() {
    const walletConnectProviderOptions = {
      projectId: this.projectId,
      showQrModal: false,
      rpcMap: this.chains ? this.chains.reduce((map, chain) => {
        map[chain.chainId] = chain.rpcUrl;
        return map;
      }, {}) : {},
      chains: [],
      optionalChains: [...this.chains.map(chain => chain.chainId)],
      metadata: this.metadata
    };
    this.walletConnectProvider = await _ethereumProvider.default.init(walletConnectProviderOptions);
    this.addWalletConnectListeners(this.walletConnectProvider);
    await this.checkActiveWalletConnectProvider();
  }
  async getWalletConnectProvider() {
    if (!this.walletConnectProvider) {
      try {
        await this.createProvider();
      } catch (error) {
        _appkitScaffoldUtilsReactNative.EthersStoreUtil.setError(error);
      }
    }
    return this.walletConnectProvider;
  }
  syncRequestedNetworks(chains, chainImages) {
    const requestedCaipNetworks = chains?.map(chain => ({
      id: `${_appkitCommonReactNative.ConstantsUtil.EIP155}:${chain.chainId}`,
      name: chain.name,
      imageId: _appkitCommonReactNative.PresetsUtil.EIP155NetworkImageIds[chain.chainId],
      imageUrl: chainImages?.[chain.chainId]
    }));
    this.setRequestedCaipNetworks(requestedCaipNetworks ?? []);
  }
  async checkActiveWalletConnectProvider() {
    const WalletConnectProvider = await this.getWalletConnectProvider();
    const walletId = await _appkitScaffoldUtilsReactNative.StorageUtil.getItem(_appkitScaffoldUtilsReactNative.EthersConstantsUtil.WALLET_ID);
    if (WalletConnectProvider) {
      if (walletId === _appkitCommonReactNative.ConstantsUtil.WALLET_CONNECT_CONNECTOR_ID) {
        await this.setWalletConnectProvider();
      }
    }
  }
  async checkActiveCoinbaseProvider(provider) {
    const CoinbaseProvider = provider;
    const walletId = await _appkitScaffoldUtilsReactNative.StorageUtil.getItem(_appkitScaffoldUtilsReactNative.EthersConstantsUtil.WALLET_ID);
    if (CoinbaseProvider) {
      if (walletId === _appkitCommonReactNative.ConstantsUtil.COINBASE_CONNECTOR_ID) {
        if (CoinbaseProvider.address) {
          await this.setCoinbaseProvider(provider);
          await this.watchCoinbase(provider);
        } else {
          await _appkitScaffoldUtilsReactNative.StorageUtil.removeItem(_appkitScaffoldUtilsReactNative.EthersConstantsUtil.WALLET_ID);
          _appkitScaffoldUtilsReactNative.EthersStoreUtil.reset();
        }
      }
    }
  }
  async setWalletConnectProvider() {
    _appkitScaffoldUtilsReactNative.StorageUtil.setItem(_appkitScaffoldUtilsReactNative.EthersConstantsUtil.WALLET_ID, _appkitCommonReactNative.ConstantsUtil.WALLET_CONNECT_CONNECTOR_ID);
    const WalletConnectProvider = await this.getWalletConnectProvider();
    if (WalletConnectProvider) {
      const providerType = _appkitCommonReactNative.PresetsUtil.ConnectorTypesMap[_appkitCommonReactNative.ConstantsUtil.WALLET_CONNECT_CONNECTOR_ID];
      _appkitScaffoldUtilsReactNative.EthersStoreUtil.setChainId(WalletConnectProvider.chainId);
      _appkitScaffoldUtilsReactNative.EthersStoreUtil.setProviderType(providerType);
      _appkitScaffoldUtilsReactNative.EthersStoreUtil.setProvider(WalletConnectProvider);
      _appkitScaffoldUtilsReactNative.EthersStoreUtil.setIsConnected(true);
      this.setAddress(WalletConnectProvider.accounts?.[0]);
      await this.watchWalletConnect();
    }
  }
  async setCoinbaseProvider(provider) {
    await _appkitScaffoldUtilsReactNative.StorageUtil.setItem(_appkitScaffoldUtilsReactNative.EthersConstantsUtil.WALLET_ID, _appkitCommonReactNative.ConstantsUtil.COINBASE_CONNECTOR_ID);
    if (provider) {
      const {
        address,
        chainId
      } = await _appkitScaffoldUtilsReactNative.EthersHelpersUtil.getUserInfo(provider);
      if (address && chainId) {
        const providerType = _appkitCommonReactNative.PresetsUtil.ConnectorTypesMap[_appkitCommonReactNative.ConstantsUtil.COINBASE_CONNECTOR_ID];
        _appkitScaffoldUtilsReactNative.EthersStoreUtil.setChainId(chainId);
        _appkitScaffoldUtilsReactNative.EthersStoreUtil.setProviderType(providerType);
        _appkitScaffoldUtilsReactNative.EthersStoreUtil.setProvider(provider);
        _appkitScaffoldUtilsReactNative.EthersStoreUtil.setIsConnected(true);
        this.setAddress(address);
        await this.watchCoinbase(provider);
      }
    }
  }
  async setAuthProvider() {
    _appkitScaffoldUtilsReactNative.StorageUtil.setItem(_appkitScaffoldUtilsReactNative.EthersConstantsUtil.WALLET_ID, _appkitCommonReactNative.ConstantsUtil.AUTH_CONNECTOR_ID);
    if (this.authProvider) {
      const {
        address,
        chainId
      } = await this.authProvider.connect();
      super.setLoading(false);
      if (address && chainId) {
        _appkitScaffoldUtilsReactNative.EthersStoreUtil.setChainId(chainId);
        _appkitScaffoldUtilsReactNative.EthersStoreUtil.setProviderType(_appkitCommonReactNative.PresetsUtil.ConnectorTypesMap[_appkitCommonReactNative.ConstantsUtil.AUTH_CONNECTOR_ID]);
        _appkitScaffoldUtilsReactNative.EthersStoreUtil.setProvider(this.authProvider);
        _appkitScaffoldUtilsReactNative.EthersStoreUtil.setIsConnected(true);
        _appkitScaffoldUtilsReactNative.EthersStoreUtil.setAddress(address);
      }
    }
  }
  async watchWalletConnect() {
    const WalletConnectProvider = await this.getWalletConnectProvider();
    function disconnectHandler() {
      _appkitScaffoldUtilsReactNative.StorageUtil.removeItem(_appkitScaffoldUtilsReactNative.EthersConstantsUtil.WALLET_ID);
      _appkitScaffoldUtilsReactNative.EthersStoreUtil.reset();
      WalletConnectProvider?.removeListener('disconnect', disconnectHandler);
      WalletConnectProvider?.removeListener('accountsChanged', accountsChangedHandler);
      WalletConnectProvider?.removeListener('chainChanged', chainChangedHandler);
    }
    function chainChangedHandler(chainId) {
      if (chainId) {
        const chain = _appkitScaffoldUtilsReactNative.EthersHelpersUtil.hexStringToNumber(chainId);
        _appkitScaffoldUtilsReactNative.EthersStoreUtil.setChainId(chain);
      }
    }
    const accountsChangedHandler = async accounts => {
      if (accounts.length > 0) {
        await this.setWalletConnectProvider();
      }
    };
    if (WalletConnectProvider) {
      WalletConnectProvider.on('disconnect', disconnectHandler);
      WalletConnectProvider.on('accountsChanged', accountsChangedHandler);
      WalletConnectProvider.on('chainChanged', chainChangedHandler);
    }
  }
  async watchCoinbase(provider) {
    const walletId = await _appkitScaffoldUtilsReactNative.StorageUtil.getItem(_appkitScaffoldUtilsReactNative.EthersConstantsUtil.WALLET_ID);
    function disconnectHandler() {
      _appkitScaffoldUtilsReactNative.StorageUtil.removeItem(_appkitScaffoldUtilsReactNative.EthersConstantsUtil.WALLET_ID);
      _appkitScaffoldUtilsReactNative.EthersStoreUtil.reset();
      provider?.removeListener('disconnect', disconnectHandler);
      provider?.removeListener('accountsChanged', accountsChangedHandler);
      provider?.removeListener('chainChanged', chainChangedHandler);
    }
    function accountsChangedHandler(accounts) {
      if (accounts.length === 0) {
        _appkitScaffoldUtilsReactNative.StorageUtil.removeItem(_appkitScaffoldUtilsReactNative.EthersConstantsUtil.WALLET_ID);
        _appkitScaffoldUtilsReactNative.EthersStoreUtil.reset();
      } else {
        _appkitScaffoldUtilsReactNative.EthersStoreUtil.setAddress(accounts[0]);
      }
    }
    function chainChangedHandler(chainId) {
      if (chainId && walletId === _appkitCommonReactNative.ConstantsUtil.COINBASE_CONNECTOR_ID) {
        const chain = Number(chainId);
        _appkitScaffoldUtilsReactNative.EthersStoreUtil.setChainId(chain);
      }
    }
    if (provider) {
      provider.on('disconnect', disconnectHandler);
      provider.on('accountsChanged', accountsChangedHandler);
      provider.on('chainChanged', chainChangedHandler);
    }
  }
  async syncAccount({
    address
  }) {
    const chainId = _appkitScaffoldUtilsReactNative.EthersStoreUtil.state.chainId;
    const isConnected = _appkitScaffoldUtilsReactNative.EthersStoreUtil.state.isConnected;
    if (isConnected && address && chainId) {
      const caipAddress = `${_appkitCommonReactNative.ConstantsUtil.EIP155}:${chainId}:${address}`;
      this.setIsConnected(isConnected);
      this.setCaipAddress(caipAddress);
      await Promise.all([this.syncProfile(address), this.syncBalance(address), this.getApprovedCaipNetworksData()]);
      this.hasSyncedConnectedAccount = true;
    } else if (!isConnected && this.hasSyncedConnectedAccount) {
      this.close();
      this.resetAccount();
      this.resetWcConnection();
      this.resetNetwork();
    }
  }
  async syncNetwork(chainImages) {
    const address = _appkitScaffoldUtilsReactNative.EthersStoreUtil.state.address;
    const chainId = _appkitScaffoldUtilsReactNative.EthersStoreUtil.state.chainId;
    const isConnected = _appkitScaffoldUtilsReactNative.EthersStoreUtil.state.isConnected;
    if (this.chains) {
      const chain = this.chains.find(c => c.chainId === chainId);
      if (chain) {
        const caipChainId = `${_appkitCommonReactNative.ConstantsUtil.EIP155}:${chain.chainId}`;
        this.setCaipNetwork({
          id: caipChainId,
          name: chain.name,
          imageId: _appkitCommonReactNative.PresetsUtil.EIP155NetworkImageIds[chain.chainId],
          imageUrl: chainImages?.[chain.chainId]
        });
        if (isConnected && address) {
          const caipAddress = `${_appkitCommonReactNative.ConstantsUtil.EIP155}:${chainId}:${address}`;
          this.setCaipAddress(caipAddress);
          if (chain.explorerUrl) {
            const url = `${chain.explorerUrl}/address/${address}`;
            this.setAddressExplorerUrl(url);
          } else {
            this.setAddressExplorerUrl(undefined);
          }
          if (this.hasSyncedConnectedAccount) {
            await this.syncBalance(address);
          }
        }
      }
    }
  }
  async syncProfile(address) {
    const chainId = _appkitScaffoldUtilsReactNative.EthersStoreUtil.state.chainId;
    try {
      const response = await this.fetchIdentity({
        address
      });
      if (!response) {
        throw new Error('Couldnt fetch idendity');
      }
      this.setProfileName(response.name);
      this.setProfileImage(response.avatar);
    } catch (error) {
      if (chainId === 1) {
        const ensProvider = new _ethers.ethers.providers.InfuraProvider('mainnet');
        const name = await ensProvider.lookupAddress(address);
        const avatar = await ensProvider.getAvatar(address);
        if (name) {
          this.setProfileName(name);
        }
        if (avatar) {
          this.setProfileImage(avatar);
        }
      } else {
        this.setProfileName(undefined);
        this.setProfileImage(undefined);
      }
    }
  }
  async syncBalance(address) {
    const chainId = _appkitScaffoldUtilsReactNative.EthersStoreUtil.state.chainId;
    if (chainId && this.chains) {
      const chain = this.chains.find(c => c.chainId === chainId);
      const token = this.options?.tokens?.[chainId];
      try {
        if (chain) {
          const jsonRpcProvider = new _ethers.ethers.providers.JsonRpcProvider(chain.rpcUrl, {
            chainId,
            name: chain.name
          });
          if (jsonRpcProvider) {
            if (token) {
              // Get balance from custom token address
              const erc20 = new _ethers.Contract(token.address, _appkitCommonReactNative.erc20ABI, jsonRpcProvider);
              // @ts-expect-error
              const decimals = await erc20.decimals();
              // @ts-expect-error
              const symbol = await erc20.symbol();
              // @ts-expect-error
              const balanceOf = await erc20.balanceOf(address);
              this.setBalance(_ethers.utils.formatUnits(balanceOf, decimals), symbol);
            } else {
              const balance = await jsonRpcProvider.getBalance(address);
              const formattedBalance = _ethers.utils.formatEther(balance);
              this.setBalance(formattedBalance, chain.currency);
            }
          }
        }
      } catch {
        this.setBalance(undefined, undefined);
      }
    }
  }
  async switchNetwork(chainId) {
    const provider = _appkitScaffoldUtilsReactNative.EthersStoreUtil.state.provider;
    const providerType = _appkitScaffoldUtilsReactNative.EthersStoreUtil.state.providerType;
    if (this.chains) {
      const chain = this.chains.find(c => c.chainId === chainId);
      const walletConnectType = _appkitCommonReactNative.PresetsUtil.ConnectorTypesMap[_appkitCommonReactNative.ConstantsUtil.WALLET_CONNECT_CONNECTOR_ID];
      const coinbaseType = _appkitCommonReactNative.PresetsUtil.ConnectorTypesMap[_appkitCommonReactNative.ConstantsUtil.COINBASE_CONNECTOR_ID];
      const authType = _appkitCommonReactNative.PresetsUtil.ConnectorTypesMap[_appkitCommonReactNative.ConstantsUtil.AUTH_CONNECTOR_ID];
      if (providerType === walletConnectType && chain) {
        const WalletConnectProvider = provider;
        if (WalletConnectProvider) {
          try {
            await WalletConnectProvider.request({
              method: 'wallet_switchEthereumChain',
              params: [{
                chainId: _appkitScaffoldUtilsReactNative.EthersHelpersUtil.numberToHexString(chain.chainId)
              }]
            });
            _appkitScaffoldUtilsReactNative.EthersStoreUtil.setChainId(chainId);
          } catch (switchError) {
            const message = switchError?.message;
            if (/(?<temp1>user rejected)/u.test(message?.toLowerCase())) {
              throw new Error('Chain is not supported');
            }
            await _appkitScaffoldUtilsReactNative.EthersHelpersUtil.addEthereumChain(WalletConnectProvider, chain);
          }
        }
      } else if (providerType === coinbaseType && chain) {
        const CoinbaseProvider = provider;
        if (CoinbaseProvider) {
          try {
            await CoinbaseProvider.request({
              method: 'wallet_switchEthereumChain',
              params: [{
                chainId: _appkitScaffoldUtilsReactNative.EthersHelpersUtil.numberToHexString(chain.chainId)
              }]
            });
            _appkitScaffoldUtilsReactNative.EthersStoreUtil.setChainId(chain.chainId);
          } catch (switchError) {
            if (switchError.code === _appkitScaffoldUtilsReactNative.EthersConstantsUtil.ERROR_CODE_UNRECOGNIZED_CHAIN_ID || switchError.code === _appkitScaffoldUtilsReactNative.EthersConstantsUtil.ERROR_CODE_DEFAULT || switchError?.data?.originalError?.code === _appkitScaffoldUtilsReactNative.EthersConstantsUtil.ERROR_CODE_UNRECOGNIZED_CHAIN_ID) {
              await _appkitScaffoldUtilsReactNative.EthersHelpersUtil.addEthereumChain(CoinbaseProvider, chain);
            } else {
              throw new Error('Error switching network');
            }
          }
        }
      } else if (providerType === authType) {
        if (this.authProvider && chain?.chainId) {
          try {
            await this.authProvider?.switchNetwork(chain?.chainId);
            _appkitScaffoldUtilsReactNative.EthersStoreUtil.setChainId(chain.chainId);
          } catch {
            throw new Error('Switching chain failed');
          }
        }
      }
    }
  }
  async handleAuthSetPreferredAccount(address, type) {
    if (!address) {
      return;
    }
    const chainId = this.getCaipNetwork()?.id;
    const caipAddress = `${_appkitCommonReactNative.ConstantsUtil.EIP155}:${chainId}:${address}`;
    this.setCaipAddress(caipAddress);
    this.setPreferredAccountType(type);
    await this.syncAccount({
      address: address
    });
    this.setLoading(false);
  }
  syncConnectors(config) {
    const _connectors = [];
    const EXCLUDED_CONNECTORS = [_appkitCommonReactNative.ConstantsUtil.AUTH_CONNECTOR_ID];
    _connectors.push({
      id: _appkitCommonReactNative.ConstantsUtil.WALLET_CONNECT_CONNECTOR_ID,
      explorerId: _appkitCommonReactNative.PresetsUtil.ConnectorExplorerIds[_appkitCommonReactNative.ConstantsUtil.WALLET_CONNECT_CONNECTOR_ID],
      imageId: _appkitCommonReactNative.PresetsUtil.ConnectorImageIds[_appkitCommonReactNative.ConstantsUtil.WALLET_CONNECT_CONNECTOR_ID],
      imageUrl: this.options?.connectorImages?.[_appkitCommonReactNative.ConstantsUtil.WALLET_CONNECT_CONNECTOR_ID],
      name: _appkitCommonReactNative.PresetsUtil.ConnectorNamesMap[_appkitCommonReactNative.ConstantsUtil.WALLET_CONNECT_CONNECTOR_ID],
      type: _appkitCommonReactNative.PresetsUtil.ConnectorTypesMap[_appkitCommonReactNative.ConstantsUtil.WALLET_CONNECT_CONNECTOR_ID]
    });
    config.extraConnectors?.forEach(connector => {
      if (!EXCLUDED_CONNECTORS.includes(connector.id)) {
        if (connector.id === _appkitCommonReactNative.ConstantsUtil.COINBASE_CONNECTOR_ID) {
          _connectors.push({
            id: _appkitCommonReactNative.ConstantsUtil.COINBASE_CONNECTOR_ID,
            explorerId: _appkitCommonReactNative.PresetsUtil.ConnectorExplorerIds[_appkitCommonReactNative.ConstantsUtil.COINBASE_CONNECTOR_ID],
            imageId: _appkitCommonReactNative.PresetsUtil.ConnectorImageIds[_appkitCommonReactNative.ConstantsUtil.COINBASE_CONNECTOR_ID],
            imageUrl: this.options?.connectorImages?.[_appkitCommonReactNative.ConstantsUtil.COINBASE_CONNECTOR_ID],
            name: _appkitCommonReactNative.PresetsUtil.ConnectorNamesMap[_appkitCommonReactNative.ConstantsUtil.COINBASE_CONNECTOR_ID],
            type: _appkitCommonReactNative.PresetsUtil.ConnectorTypesMap[_appkitCommonReactNative.ConstantsUtil.COINBASE_CONNECTOR_ID]
          });
          this.checkActiveCoinbaseProvider(connector);
        } else {
          _connectors.push({
            id: connector.id,
            name: connector.name ?? _appkitCommonReactNative.PresetsUtil.ConnectorNamesMap[connector.id],
            type: 'EXTERNAL'
          });
        }
      }
    });
    this.setConnectors(_connectors);
  }
  async syncAuthConnector(config) {
    const authConnector = config.extraConnectors?.find(connector => connector.id === _appkitCommonReactNative.ConstantsUtil.AUTH_CONNECTOR_ID);
    if (!authConnector) {
      return;
    }
    this.authProvider = authConnector;
    this.addConnector({
      id: _appkitCommonReactNative.ConstantsUtil.AUTH_CONNECTOR_ID,
      name: _appkitCommonReactNative.PresetsUtil.ConnectorNamesMap[_appkitCommonReactNative.ConstantsUtil.AUTH_CONNECTOR_ID],
      type: _appkitCommonReactNative.PresetsUtil.ConnectorTypesMap[_appkitCommonReactNative.ConstantsUtil.AUTH_CONNECTOR_ID],
      provider: authConnector
    });
    const connectedConnector = await _appkitScaffoldUtilsReactNative.StorageUtil.getItem('@w3m/connected_connector');
    if (connectedConnector === 'AUTH') {
      // Set loader until it reconnects
      this.setLoading(true);
    }
    const {
      isConnected
    } = await this.authProvider.isConnected();
    if (isConnected) {
      this.setAuthProvider();
    }
    this.addAuthListeners(this.authProvider);
  }
  async syncConnectedWalletInfo(provider) {
    if (!provider) {
      this.setConnectedWalletInfo(undefined);
      return;
    }
    if (provider?.session?.peer?.metadata) {
      const metadata = provider?.session?.peer.metadata;
      if (metadata) {
        this.setConnectedWalletInfo({
          ...metadata,
          name: metadata.name,
          icon: metadata.icons?.[0]
        });
      }
    } else if (provider?.id) {
      this.setConnectedWalletInfo({
        id: provider.id,
        name: provider?.name ?? _appkitCommonReactNative.PresetsUtil.ConnectorNamesMap[provider.id],
        icon: this.options?.connectorImages?.[provider.id]
      });
    } else {
      this.setConnectedWalletInfo(undefined);
    }
  }
  async addAuthListeners(authProvider) {
    authProvider.onSetPreferredAccount(async ({
      address,
      type
    }) => {
      if (address) {
        await this.handleAuthSetPreferredAccount(address, type);
      }
      this.setLoading(false);
    });
    authProvider.setOnTimeout(async () => {
      this.handleAlertError(_appkitCommonReactNative.ErrorUtil.ALERT_ERRORS.SOCIALS_TIMEOUT);
      this.setLoading(false);
    });
  }
  async addWalletConnectListeners(provider) {
    if (provider) {
      provider.signer.client.core.relayer.on('relayer_connect', () => {
        provider.signer.client.core.relayer?.provider?.on('payload', payload => {
          if (payload?.error) {
            this.handleAlertError(payload?.error.message);
          }
        });
      });
    }
  }
}
exports.AppKit = AppKit;
//# sourceMappingURL=client.js.map