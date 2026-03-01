module.exports = {
  presets: ['@react-native/babel-preset'],
  plugins: [
    [
      'module-resolver',
      {
        alias: {
          'react-native-perf-memory': './src/index',
        },
      },
    ],
  ],
};
