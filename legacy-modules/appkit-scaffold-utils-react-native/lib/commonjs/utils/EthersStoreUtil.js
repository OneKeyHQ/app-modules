"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.EthersStoreUtil = void 0;
var _utils = require("valtio/utils");
var _valtio = require("valtio");
// -- Types --------------------------------------------- //

// -- State --------------------------------------------- //
const state = (0, _valtio.proxy)({
  provider: undefined,
  providerType: undefined,
  address: undefined,
  chainId: undefined,
  isConnected: false
});

// -- StoreUtil ---------------------------------------- //
const EthersStoreUtil = exports.EthersStoreUtil = {
  state,
  subscribeKey(key, callback) {
    return (0, _utils.subscribeKey)(state, key, callback);
  },
  subscribe(callback) {
    return (0, _valtio.subscribe)(state, () => callback(state));
  },
  setProvider(provider) {
    if (provider) {
      state.provider = (0, _valtio.ref)(provider);
    }
  },
  setProviderType(providerType) {
    state.providerType = providerType;
  },
  setAddress(address) {
    state.address = address;
  },
  setChainId(chainId) {
    state.chainId = chainId;
  },
  setIsConnected(isConnected) {
    state.isConnected = isConnected;
  },
  setError(error) {
    state.error = error;
  },
  reset() {
    state.provider = undefined;
    state.address = undefined;
    state.chainId = undefined;
    state.providerType = undefined;
    state.isConnected = false;
    state.error = undefined;
  }
};
//# sourceMappingURL=EthersStoreUtil.js.map