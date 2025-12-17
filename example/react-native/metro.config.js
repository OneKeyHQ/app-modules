/**
 * @type {import('expo/metro-config')}
 */

// https://github.com/facebook/react-native/issues/27712#issuecomment-1518279571

const { getDefaultConfig, mergeConfig } = require('@react-native/metro-config');
const path = require('path')

const projectRoot = __dirname
const workspaceRoot = path.resolve(__dirname, '../..')

const config = getDefaultConfig(projectRoot)

config.watchFolders = [workspaceRoot]
config.resolver.nodeModulesPaths = [
  path.resolve(projectRoot, 'node_modules'),
  path.resolve(workspaceRoot, 'node_modules'),
]

config.resolver.extraNodeModules = {
  modules: workspaceRoot,
  'expo-crypto': path.resolve(projectRoot, 'mocks/expo-crypto.js'),
}

module.exports = config