module.exports = {
  presets: ['@react-native/babel-preset'],
  plugins: [
    [
      'module-resolver',
      {
        alias: {
          'native-logger': './src/index',
        },
      },
    ],
  ],
};
