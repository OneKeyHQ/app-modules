module.exports = {
  reactNativePath: '../../node_modules/react-native',
  // TEMPORARY (capability-extraction validation): bundle-update and the new
  // bundle-crypto both vendor Gopenpgp.xcframework, which CocoaPods rejects as a
  // duplicate-named framework in one target. Until P3 makes bundle-update delegate
  // to bundle-crypto (and drop its own Gopenpgp), exclude bundle-update's iOS
  // autolink so the new modules can be validated. Remove this block after P3.
  // (The BundleUpdate test page will not work while this is in place.)
  dependencies: {
    '@onekeyfe/react-native-bundle-update': {
      platforms: { ios: null },
    },
  },
};
