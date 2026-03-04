module.exports = {
  presets: ['@react-native/babel-preset'],
  plugins: [
    [
      'module-resolver',
      {
        alias: {
          'react-native-bundle-update': './src/index',
        },
      },
    ],
  ],
};
