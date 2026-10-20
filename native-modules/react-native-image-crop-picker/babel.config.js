module.exports = {
  presets: ['@react-native/babel-preset'],
  plugins: [
    [
      'module-resolver',
      {
        alias: {
          'react-native-image-crop-picker': './src/index',
        },
      },
    ],
  ],
};
