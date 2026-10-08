import { fixupConfigRules } from '@eslint/compat';
import { FlatCompat } from '@eslint/eslintrc';
import js from '@eslint/js';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const compat = new FlatCompat({
  baseDirectory: path.dirname(fileURLToPath(import.meta.url)),
  recommendedConfig: js.configs.recommended,
  allConfig: js.configs.all,
});

export default [
  {
    ignores: [
      '**/node_modules',
      '**/android/build',
      '**/ios/build',
      '**/lib',
      '**/nitrogen',
      '**/*.config.js',
      '**/*.config.mjs',
    ],
  },
  ...fixupConfigRules(compat.extends('@react-native', 'prettier')),
];
