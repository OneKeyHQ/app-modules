module.exports = {
  preset: '@react-native/jest-preset',
  moduleNameMapper: {
    '^@onekeyfe/react-native-image/icon/dom$': '<rootDir>/../../native-views/react-native-image/src/icon/dom.ts',
    '^#onekey-icon-assets$': '<rootDir>/icon-fixtures/web/assetUrls.ts',
  },
};
