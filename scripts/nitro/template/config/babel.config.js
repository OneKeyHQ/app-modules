module.exports = {
  presets: ['@react-native/babel-preset'],
  plugins: [
    [
      'module-resolver',
      {
        alias: {
          '{{moduleDirectory}}': './src/index',
        },
      },
    ],
  ],
};
