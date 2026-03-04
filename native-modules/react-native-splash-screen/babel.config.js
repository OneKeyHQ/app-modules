module.exports = {
  presets: ['@react-native/babel-preset'],
  plugins: [
    [
      'module-resolver',
      {
        alias: {
          'react-native-splash-screen': './src/index',
        },
      },
    ],
  ],
};
