module.exports = {
  reactNativePath: '../../node_modules/react-native',
  dependencies: {
    // The module also contains JPush's vendor ReactPackage classes; autolinking
    // picks one of those under the wrong namespace. Same override as
    // app-monorepo's apps/mobile/react-native.config.js.
    '@onekeyfe/react-native-jpush': {
      platforms: {
        android: {
          packageImportPath:
            'import com.margelo.nitro.reactnativejpush.ReactNativeJpushPackage;',
          packageInstance: 'new ReactNativeJpushPackage()',
        },
      },
    },
  },
};
