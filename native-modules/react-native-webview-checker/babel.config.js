module.exports = {
  presets: ['@react-native/babel-preset'],
  plugins: [
    [
      'module-resolver',
      {
        alias: {
          'react-native-webview-checker': './src/index',
        },
      },
    ],
  ],
};
