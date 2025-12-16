/**
 * @type {import('expo/metro-config')}
 */
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
}

console.log('config', config)

module.exports = config