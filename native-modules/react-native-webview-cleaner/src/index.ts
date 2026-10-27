import { NativeModules } from 'react-native';

export default {
  clearAll: () => {
    NativeModules.RNWebViewCleaner.clearAll();
  },
};
