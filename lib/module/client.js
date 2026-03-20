import { Contract, ethers, utils } from 'ethers';
import { AppKitScaffold } from '@reown/appkit-scaffold-react-native';
import { StorageUtil, HelpersUtil, EthersConstantsUtil, EthersHelpersUtil, EthersStoreUtil } from '@reown/appkit-scaffold-utils-react-native';
import { SIWEController, getDidChainId, getDidAddress } from '@reown/appkit-siwe-react-native';
import { erc20ABI, ErrorUtil, NamesUtil, NetworkUtil, ConstantsUtil, PresetsUtil } from '@reown/appkit-common-react-native';
import EthereumProvider, { OPTIONAL_METHODS } from '@walletconnect/ethereum-provider';
import { getAuthCaipNetworks, getWalletConnectCaipNetworks } from './utils/helpers';

// -- Types ---------------------------------------------------------------------

// @ts-expect-error: Overriden state type is correct

// -- Client --------------------------------------------------------------------
export class AppKit extends AppKitScaffold {
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
      throw new Error(ErrorUtil.ALERT_ERRORS.PROJECT_ID_NOT_CONFIGURED.shortMessage);
    }
    const networkControllerClient = {
      switchCaipNetwork: async caipNetwork => {
        const chainId = NetworkUtil.caipNetworkIdToNumber(caipNetwork?.id);
        if (chainId) {
          try {
            await this.switchNetwork(chainId);
          } catch (error) {
            EthersStoreUtil.setError(error);
          }
        }
      },
      getApprovedCaipNetworksData: async () => new Promise(async resolve => {
        const walletId = await StorageUtil.getItem(EthersConstantsUtil.WALLET_ID);
        const walletChoice = PresetsUtil.ConnectorTypesMap[walletId ?? ''];
        const walletConnectType = PresetsUtil.ConnectorTypesMap[ConstantsUtil.WALLET_CONNECT_CONNECTOR_ID];
        const authType = PresetsUtil.ConnectorTypesMap[ConstantsUtil.AUTH_CONNECTOR_ID];
        if (walletChoice === walletConnectType) {
          const provider = await this.getWalletConnectProvider();
          const result = getWalletConnectCaipNetworks(provider);
          resolve(result);
        } else if (walletChoice === authType) {
          const result = getAuthCaipNetworks();
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
            methods: OPTIONAL_METHODS,
            ...params
          });
          // Auths is an array of signed CACAO objects https://github.com/ChainAgnostic/CAIPs/blob/main/CAIPs/caip-74.md
          const signedCacao = result?.auths?.[0];
          if (signedCacao) {
            const {
              p,
              s
            } = signedCacao;
            const chainId = getDidChainId(p.iss);
            const address = getDidAddress(p.iss);
            try {
              // Kicks off verifyMessage and populates external states
              const message = WalletConnectProvider.signer.client.formatAuthMessage({
                request: p,
                iss: p.iss
              });
              await SIWEController.verifyMessage({
                message,
                signature: s.s,
                cacao: signedCacao
              });
              if (address && chainId) {
                const session = {
                  address,
                  chainId: parseInt(chainId, 10)
                };
                SIWEController.setSession(session);
                SIWEController.onSignIn?.(session);
              }
            } catch (error) {
              // eslint-disable-next-line no-console
              console.error('Error verifying message', error);
              // eslint-disable-next-line no-console
              await WalletConnectProvider.disconnect().catch(console.error);
              // eslint-disable-next-line no-console
              await SIWEController.signOut().catch(console.error);
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
        if (id === ConstantsUtil.COINBASE_CONNECTOR_ID) {
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
            EthersStoreUtil.setError(error);
          }
        } else if (id === ConstantsUtil.AUTH_CONNECTOR_ID) {
          await this.setAuthProvider();
        }
      },
      disconnect: async () => {
        const provider = EthersStoreUtil.state.provider;
        const providerType = EthersStoreUtil.state.providerType;
        const walletConnectType = PresetsUtil.ConnectorTypesMap[ConstantsUtil.WALLET_CONNECT_CONNECTOR_ID];
        const authType = PresetsUtil.ConnectorTypesMap[ConstantsUtil.AUTH_CONNECTOR_ID];
        if (siweConfig?.options?.signOutOnDisconnect) {
          await SIWEController.signOut();
        }
        if (providerType === walletConnectType) {
          const WalletConnectProvider = provider;
          await WalletConnectProvider.disconnect();
        } else if (providerType === authType) {
          await this.authProvider?.disconnect();
        } else if (provider) {
          provider.emit('disconnect');
        }
        StorageUtil.removeItem(EthersConstantsUtil.WALLET_ID);
        EthersStoreUtil.reset();
        this.setClientId(null);
      },
      signMessage: async message => {
        const provider = EthersStoreUtil.state.provider;
        if (!provider) {
          throw new Error('connectionControllerClient:signMessage - provider is undefined');
        }
        const hexMessage = utils.isHexString(message) ? message : utils.hexlify(utils.toUtf8Bytes(message));
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
        const networkId = EthersStoreUtil.state.chainId;
        const provider = EthersStoreUtil.state.provider;
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
          const browserProvider = new ethers.providers.Web3Provider(provider, networkId);
          const signer = browserProvider.getSigner(address);
          return (await signer.estimateGas(txParams)).toBigInt();
        } catch (error) {
          throw new Error('Ethers: estimateGas - Estimate gas failed');
        }
      },
      parseUnits: (value, decimals) => ethers.utils.parseUnits(value, decimals).toBigInt(),
      formatUnits: (value, decimals) => ethers.utils.formatUnits(value, decimals),
      sendTransaction: async data => {
        const provider = EthersStoreUtil.state.provider;
        const address = EthersStoreUtil.state.address;
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
        const browserProvider = new ethers.providers.Web3Provider(provider);
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
        } = EthersStoreUtil.state;
        if (!provider) {
          throw new Error('writeContract - provider is undefined');
        }
        if (!address) {
          throw new Error('writeContract - address is undefined');
        }
        const browserProvider = new ethers.providers.Web3Provider(provider, chainId);
        const signer = browserProvider.getSigner(address);
        const contract = new Contract(data.tokenAddress, data.abi, signer);
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
          } = EthersStoreUtil.state;
          let ensName = null;
          let wcName = false;
          if (NamesUtil.isReownName(value)) {
            wcName = (await this.resolveReownName(value)) || false;
          }
          if (chainId === 1) {
            const ensProvider = new ethers.providers.InfuraProvider('mainnet');
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
        } = EthersStoreUtil.state;
        if (chainId === 1) {
          const ensProvider = new ethers.providers.InfuraProvider('mainnet');
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
      defaultChain: EthersHelpersUtil.getCaipDefaultChain(defaultChain),
      tokens: HelpersUtil.getCaipTokens(tokens),
      _sdkVersion: _sdkVersion ?? `react-native-ethers5-${ConstantsUtil.VERSION}`,
      ...appKitOptions
    });
    this.options = options;
    this.metadata = config.metadata;
    this.projectId = appKitOptions.projectId;
    this.chains = chains;
    this.createProvider();
    EthersStoreUtil.subscribeKey('address', address => {
      this.syncAccount({
        address
      });
    });
    EthersStoreUtil.subscribeKey('chainId', () => {
      this.syncNetwork(chainImages);
    });
    EthersStoreUtil.subscribeKey('provider', provider => {
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
      selectedNetworkId: NetworkUtil.caipNetworkIdToNumber(state.selectedNetworkId)
    };
  }

  // @ts-expect-error: Overriden state type is correct
  subscribeState(callback) {
    return super.subscribeState(state => callback({
      ...state,
      selectedNetworkId: NetworkUtil.caipNetworkIdToNumber(state.selectedNetworkId)
    }));
  }
  setAddress(address) {
    const originalAddress = address ? utils.getAddress(address) : undefined;
    EthersStoreUtil.setAddress(originalAddress);
  }
  getAddress() {
    const {
      address
    } = EthersStoreUtil.state;
    return address ? utils.getAddress(address) : address;
  }
  getError() {
    return EthersStoreUtil.state.error;
  }
  getChainId() {
    return EthersStoreUtil.state.chainId;
  }
  getIsConnected() {
    return EthersStoreUtil.state.isConnected;
  }
  getWalletProvider() {
    return EthersStoreUtil.state.provider;
  }
  getWalletProviderType() {
    return EthersStoreUtil.state.providerType;
  }
  subscribeProvider(callback) {
    return EthersStoreUtil.subscribe(callback);
  }
  async disconnect() {
    const {
      provider
    } = EthersStoreUtil.state;
    StorageUtil.removeItem(EthersConstantsUtil.WALLET_ID);
    EthersStoreUtil.reset();
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
    this.walletConnectProvider = await EthereumProvider.init(walletConnectProviderOptions);
    this.addWalletConnectListeners(this.walletConnectProvider);
    await this.checkActiveWalletConnectProvider();
  }
  async getWalletConnectProvider() {
    if (!this.walletConnectProvider) {
      try {
        await this.createProvider();
      } catch (error) {
        EthersStoreUtil.setError(error);
      }
    }
    return this.walletConnectProvider;
  }
  syncRequestedNetworks(chains, chainImages) {
    const requestedCaipNetworks = chains?.map(chain => ({
      id: `${ConstantsUtil.EIP155}:${chain.chainId}`,
      name: chain.name,
      imageId: PresetsUtil.EIP155NetworkImageIds[chain.chainId],
      imageUrl: chainImages?.[chain.chainId]
    }));
    this.setRequestedCaipNetworks(requestedCaipNetworks ?? []);
  }
  async checkActiveWalletConnectProvider() {
    const WalletConnectProvider = await this.getWalletConnectProvider();
    const walletId = await StorageUtil.getItem(EthersConstantsUtil.WALLET_ID);
    if (WalletConnectProvider) {
      if (walletId === ConstantsUtil.WALLET_CONNECT_CONNECTOR_ID) {
        await this.setWalletConnectProvider();
      }
    }
  }
  async checkActiveCoinbaseProvider(provider) {
    const CoinbaseProvider = provider;
    const walletId = await StorageUtil.getItem(EthersConstantsUtil.WALLET_ID);
    if (CoinbaseProvider) {
      if (walletId === ConstantsUtil.COINBASE_CONNECTOR_ID) {
        if (CoinbaseProvider.address) {
          await this.setCoinbaseProvider(provider);
          await this.watchCoinbase(provider);
        } else {
          await StorageUtil.removeItem(EthersConstantsUtil.WALLET_ID);
          EthersStoreUtil.reset();
        }
      }
    }
  }
  async setWalletConnectProvider() {
    StorageUtil.setItem(EthersConstantsUtil.WALLET_ID, ConstantsUtil.WALLET_CONNECT_CONNECTOR_ID);
    const WalletConnectProvider = await this.getWalletConnectProvider();
    if (WalletConnectProvider) {
      const providerType = PresetsUtil.ConnectorTypesMap[ConstantsUtil.WALLET_CONNECT_CONNECTOR_ID];
      EthersStoreUtil.setChainId(WalletConnectProvider.chainId);
      EthersStoreUtil.setProviderType(providerType);
      EthersStoreUtil.setProvider(WalletConnectProvider);
      EthersStoreUtil.setIsConnected(true);
      this.setAddress(WalletConnectProvider.accounts?.[0]);
      await this.watchWalletConnect();
    }
  }
  async setCoinbaseProvider(provider) {
    await StorageUtil.setItem(EthersConstantsUtil.WALLET_ID, ConstantsUtil.COINBASE_CONNECTOR_ID);
    if (provider) {
      const {
        address,
        chainId
      } = await EthersHelpersUtil.getUserInfo(provider);
      if (address && chainId) {
        const providerType = PresetsUtil.ConnectorTypesMap[ConstantsUtil.COINBASE_CONNECTOR_ID];
        EthersStoreUtil.setChainId(chainId);
        EthersStoreUtil.setProviderType(providerType);
        EthersStoreUtil.setProvider(provider);
        EthersStoreUtil.setIsConnected(true);
        this.setAddress(address);
        await this.watchCoinbase(provider);
      }
    }
  }
  async setAuthProvider() {
    StorageUtil.setItem(EthersConstantsUtil.WALLET_ID, ConstantsUtil.AUTH_CONNECTOR_ID);
    if (this.authProvider) {
      const {
        address,
        chainId
      } = await this.authProvider.connect();
      super.setLoading(false);
      if (address && chainId) {
        EthersStoreUtil.setChainId(chainId);
        EthersStoreUtil.setProviderType(PresetsUtil.ConnectorTypesMap[ConstantsUtil.AUTH_CONNECTOR_ID]);
        EthersStoreUtil.setProvider(this.authProvider);
        EthersStoreUtil.setIsConnected(true);
        EthersStoreUtil.setAddress(address);
      }
    }
  }
  async watchWalletConnect() {
    const WalletConnectProvider = await this.getWalletConnectProvider();
    function disconnectHandler() {
      StorageUtil.removeItem(EthersConstantsUtil.WALLET_ID);
      EthersStoreUtil.reset();
      WalletConnectProvider?.removeListener('disconnect', disconnectHandler);
      WalletConnectProvider?.removeListener('accountsChanged', accountsChangedHandler);
      WalletConnectProvider?.removeListener('chainChanged', chainChangedHandler);
    }
    function chainChangedHandler(chainId) {
      if (chainId) {
        const chain = EthersHelpersUtil.hexStringToNumber(chainId);
        EthersStoreUtil.setChainId(chain);
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
    const walletId = await StorageUtil.getItem(EthersConstantsUtil.WALLET_ID);
    function disconnectHandler() {
      StorageUtil.removeItem(EthersConstantsUtil.WALLET_ID);
      EthersStoreUtil.reset();
      provider?.removeListener('disconnect', disconnectHandler);
      provider?.removeListener('accountsChanged', accountsChangedHandler);
      provider?.removeListener('chainChanged', chainChangedHandler);
    }
    function accountsChangedHandler(accounts) {
      if (accounts.length === 0) {
        StorageUtil.removeItem(EthersConstantsUtil.WALLET_ID);
        EthersStoreUtil.reset();
      } else {
        EthersStoreUtil.setAddress(accounts[0]);
      }
    }
    function chainChangedHandler(chainId) {
      if (chainId && walletId === ConstantsUtil.COINBASE_CONNECTOR_ID) {
        const chain = Number(chainId);
        EthersStoreUtil.setChainId(chain);
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
    const chainId = EthersStoreUtil.state.chainId;
    const isConnected = EthersStoreUtil.state.isConnected;
    if (isConnected && address && chainId) {
      const caipAddress = `${ConstantsUtil.EIP155}:${chainId}:${address}`;
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
    const address = EthersStoreUtil.state.address;
    const chainId = EthersStoreUtil.state.chainId;
    const isConnected = EthersStoreUtil.state.isConnected;
    if (this.chains) {
      const chain = this.chains.find(c => c.chainId === chainId);
      if (chain) {
        const caipChainId = `${ConstantsUtil.EIP155}:${chain.chainId}`;
        this.setCaipNetwork({
          id: caipChainId,
          name: chain.name,
          imageId: PresetsUtil.EIP155NetworkImageIds[chain.chainId],
          imageUrl: chainImages?.[chain.chainId]
        });
        if (isConnected && address) {
          const caipAddress = `${ConstantsUtil.EIP155}:${chainId}:${address}`;
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
    const chainId = EthersStoreUtil.state.chainId;
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
        const ensProvider = new ethers.providers.InfuraProvider('mainnet');
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
    const chainId = EthersStoreUtil.state.chainId;
    if (chainId && this.chains) {
      const chain = this.chains.find(c => c.chainId === chainId);
      const token = this.options?.tokens?.[chainId];
      try {
        if (chain) {
          const jsonRpcProvider = new ethers.providers.JsonRpcProvider(chain.rpcUrl, {
            chainId,
            name: chain.name
          });
          if (jsonRpcProvider) {
            if (token) {
              // Get balance from custom token address
              const erc20 = new Contract(token.address, erc20ABI, jsonRpcProvider);
              // @ts-expect-error
              const decimals = await erc20.decimals();
              // @ts-expect-error
              const symbol = await erc20.symbol();
              // @ts-expect-error
              const balanceOf = await erc20.balanceOf(address);
              this.setBalance(utils.formatUnits(balanceOf, decimals), symbol);
            } else {
              const balance = await jsonRpcProvider.getBalance(address);
              const formattedBalance = utils.formatEther(balance);
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
    const provider = EthersStoreUtil.state.provider;
    const providerType = EthersStoreUtil.state.providerType;
    if (this.chains) {
      const chain = this.chains.find(c => c.chainId === chainId);
      const walletConnectType = PresetsUtil.ConnectorTypesMap[ConstantsUtil.WALLET_CONNECT_CONNECTOR_ID];
      const coinbaseType = PresetsUtil.ConnectorTypesMap[ConstantsUtil.COINBASE_CONNECTOR_ID];
      const authType = PresetsUtil.ConnectorTypesMap[ConstantsUtil.AUTH_CONNECTOR_ID];
      if (providerType === walletConnectType && chain) {
        const WalletConnectProvider = provider;
        if (WalletConnectProvider) {
          try {
            await WalletConnectProvider.request({
              method: 'wallet_switchEthereumChain',
              params: [{
                chainId: EthersHelpersUtil.numberToHexString(chain.chainId)
              }]
            });
            EthersStoreUtil.setChainId(chainId);
          } catch (switchError) {
            const message = switchError?.message;
            if (/(?<temp1>user rejected)/u.test(message?.toLowerCase())) {
              throw new Error('Chain is not supported');
            }
            await EthersHelpersUtil.addEthereumChain(WalletConnectProvider, chain);
          }
        }
      } else if (providerType === coinbaseType && chain) {
        const CoinbaseProvider = provider;
        if (CoinbaseProvider) {
          try {
            await CoinbaseProvider.request({
              method: 'wallet_switchEthereumChain',
              params: [{
                chainId: EthersHelpersUtil.numberToHexString(chain.chainId)
              }]
            });
            EthersStoreUtil.setChainId(chain.chainId);
          } catch (switchError) {
            if (switchError.code === EthersConstantsUtil.ERROR_CODE_UNRECOGNIZED_CHAIN_ID || switchError.code === EthersConstantsUtil.ERROR_CODE_DEFAULT || switchError?.data?.originalError?.code === EthersConstantsUtil.ERROR_CODE_UNRECOGNIZED_CHAIN_ID) {
              await EthersHelpersUtil.addEthereumChain(CoinbaseProvider, chain);
            } else {
              throw new Error('Error switching network');
            }
          }
        }
      } else if (providerType === authType) {
        if (this.authProvider && chain?.chainId) {
          try {
            await this.authProvider?.switchNetwork(chain?.chainId);
            EthersStoreUtil.setChainId(chain.chainId);
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
    const caipAddress = `${ConstantsUtil.EIP155}:${chainId}:${address}`;
    this.setCaipAddress(caipAddress);
    this.setPreferredAccountType(type);
    await this.syncAccount({
      address: address
    });
    this.setLoading(false);
  }
  syncConnectors(config) {
    const _connectors = [];
    const EXCLUDED_CONNECTORS = [ConstantsUtil.AUTH_CONNECTOR_ID];
    _connectors.push({
      id: ConstantsUtil.WALLET_CONNECT_CONNECTOR_ID,
      explorerId: PresetsUtil.ConnectorExplorerIds[ConstantsUtil.WALLET_CONNECT_CONNECTOR_ID],
      imageId: PresetsUtil.ConnectorImageIds[ConstantsUtil.WALLET_CONNECT_CONNECTOR_ID],
      imageUrl: this.options?.connectorImages?.[ConstantsUtil.WALLET_CONNECT_CONNECTOR_ID],
      name: PresetsUtil.ConnectorNamesMap[ConstantsUtil.WALLET_CONNECT_CONNECTOR_ID],
      type: PresetsUtil.ConnectorTypesMap[ConstantsUtil.WALLET_CONNECT_CONNECTOR_ID]
    });
    config.extraConnectors?.forEach(connector => {
      if (!EXCLUDED_CONNECTORS.includes(connector.id)) {
        if (connector.id === ConstantsUtil.COINBASE_CONNECTOR_ID) {
          _connectors.push({
            id: ConstantsUtil.COINBASE_CONNECTOR_ID,
            explorerId: PresetsUtil.ConnectorExplorerIds[ConstantsUtil.COINBASE_CONNECTOR_ID],
            imageId: PresetsUtil.ConnectorImageIds[ConstantsUtil.COINBASE_CONNECTOR_ID],
            imageUrl: this.options?.connectorImages?.[ConstantsUtil.COINBASE_CONNECTOR_ID],
            name: PresetsUtil.ConnectorNamesMap[ConstantsUtil.COINBASE_CONNECTOR_ID],
            type: PresetsUtil.ConnectorTypesMap[ConstantsUtil.COINBASE_CONNECTOR_ID]
          });
          this.checkActiveCoinbaseProvider(connector);
        } else {
          _connectors.push({
            id: connector.id,
            name: connector.name ?? PresetsUtil.ConnectorNamesMap[connector.id],
            type: 'EXTERNAL'
          });
        }
      }
    });
    this.setConnectors(_connectors);
  }
  async syncAuthConnector(config) {
    const authConnector = config.extraConnectors?.find(connector => connector.id === ConstantsUtil.AUTH_CONNECTOR_ID);
    if (!authConnector) {
      return;
    }
    this.authProvider = authConnector;
    this.addConnector({
      id: ConstantsUtil.AUTH_CONNECTOR_ID,
      name: PresetsUtil.ConnectorNamesMap[ConstantsUtil.AUTH_CONNECTOR_ID],
      type: PresetsUtil.ConnectorTypesMap[ConstantsUtil.AUTH_CONNECTOR_ID],
      provider: authConnector
    });
    const connectedConnector = await StorageUtil.getItem('@w3m/connected_connector');
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
        name: provider?.name ?? PresetsUtil.ConnectorNamesMap[provider.id],
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
      this.handleAlertError(ErrorUtil.ALERT_ERRORS.SOCIALS_TIMEOUT);
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
//# sourceMappingURL=client.js.map