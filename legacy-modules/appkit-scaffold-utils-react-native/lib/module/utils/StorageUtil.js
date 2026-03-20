import AsyncStorage from '@react-native-async-storage/async-storage';
import { StorageUtil as CoreStorageUtil } from '@reown/appkit-core-react-native';
export const StorageUtil = {
  async getItem(key) {
    const item = await AsyncStorage.getItem(key);
    return item ? JSON.parse(item) : undefined;
  },
  async setItem(key, value) {
    await AsyncStorage.setItem(key, JSON.stringify(value));
  },
  async removeItem(key) {
    await AsyncStorage.removeItem(key);
  },
  async getConnectedConnector() {
    return CoreStorageUtil.getConnectedConnector();
  }
};
//# sourceMappingURL=StorageUtil.js.map