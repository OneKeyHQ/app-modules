const path = require('path');
const { getDefaultConfig } = require('@react-native/metro-config');
const { withMetroConfig } = require('react-native-monorepo-config');

const root = path.resolve(__dirname, '..');

/**
 * Metro configuration
 * https://facebook.github.io/metro/docs/configuration
 *
 * @type {import('metro-config').MetroConfig}
 */
const metroConfig = withMetroConfig(getDefaultConfig(__dirname), {
  root,
  dirname: __dirname,
});

const workspaceRoot = path.resolve(__dirname, '../../../');

metroConfig.watchFolders = [workspaceRoot];

metroConfig.resolver.nodeModulesPaths = [
  path.resolve(root, 'node_modules'),
  path.resolve(workspaceRoot, 'node_modules'),
];

return metroConfig;