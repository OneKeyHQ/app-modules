"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.StorageUtil = void 0;
var _asyncStorage = _interopRequireDefault(require("@react-native-async-storage/async-storage"));
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
const StorageUtil = exports.StorageUtil = {
  async getItem(key) {
    const item = await _asyncStorage.default.getItem(key);
    return item ? JSON.parse(item) : undefined;
  },
  async setItem(key, value) {
    await _asyncStorage.default.setItem(key, JSON.stringify(value));
  },
  async removeItem(key) {
    await _asyncStorage.default.removeItem(key);
  },
  async getConnectedConnector() {
    return _appkitCoreReactNative.StorageUtil.getConnectedConnector();
  }
};
//# sourceMappingURL=StorageUtil.js.map