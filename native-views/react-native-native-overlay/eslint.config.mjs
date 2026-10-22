import reactNativeConfig from '@react-native/eslint-config/flat';

export default [
  {
    ignores: [
      '**/node_modules/**',
      '**/lib/**',
      '**/*.config.js',
      '**/*.config.mjs',
    ],
  },
  ...reactNativeConfig,
];
