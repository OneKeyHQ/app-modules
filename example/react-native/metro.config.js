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

const expoCryptoMock = path.resolve(projectRoot, 'mocks/expo-crypto.js')

config.resolver.extraNodeModules = {
  modules: workspaceRoot,
  'expo-crypto': expoCryptoMock,
}

// extraNodeModules is only a fallback, and the real expo-crypto is hoisted to
// the workspace root (it needs expo-modules-core, which the example does not
// install), so force the mock.
config.resolver.resolveRequest = (context, moduleName, platform) => {
  if (moduleName === 'expo-crypto') {
    return { type: 'sourceFile', filePath: expoCryptoMock }
  }
  return context.resolveRequest(context, moduleName, platform)
}

module.exports = config