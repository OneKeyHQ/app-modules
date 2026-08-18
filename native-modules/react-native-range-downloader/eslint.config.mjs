import { fixupConfigRules } from '@eslint/compat';
import { FlatCompat } from '@eslint/eslintrc';
import js from '@eslint/js';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const compat = new FlatCompat({
  baseDirectory: __dirname,
  recommendedConfig: js.configs.recommended,
  allConfig: js.configs.all,
});

export default [
  {
    ignores: [
      '**/node_modules',
      '**/android/build',
      '**/conformance',
      '**/ios/build',
      '**/lib',
      '**/nitrogen',
      '**/*.config.js',
      '**/*.config.mjs',
    ],
  },
  ...fixupConfigRules(compat.extends('@react-native', 'prettier')),
];
