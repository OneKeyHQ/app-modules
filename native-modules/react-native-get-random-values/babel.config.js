module.exports = {
  presets: ['@react-native/babel-preset'],
  plugins: [
    [
      'module-resolver',
      {
        alias: {
          'react-native-get-random-values': './src/index',
        },
      },
    ],
  ],
};
