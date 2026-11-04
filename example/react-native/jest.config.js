module.exports = {
  preset: 'react-native',
  moduleNameMapper: {
    '^@onekeyfe/react-native-image/icon/dom$': '<rootDir>/../../native-views/react-native-image/src/icon/dom.ts',
    '^#onekey-icon-assets$': '<rootDir>/icon-fixtures/web/assetUrls.ts',
  },
};
