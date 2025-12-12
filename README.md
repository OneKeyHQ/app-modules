# app-modules

## Create new package

### Create Nitro Module

> **Important**: Avoid generating a new `node_modules` folder. All dependencies should be managed from the monorepo root to avoid conflicts and ensure proper workspace linking.


1. Create project with `npx create-react-native-library@latest` then select turbo module or turbo view

2. Remove `packageManager` field from `package.json` in the new library, then run `yarn` to install dependencies

3. Generate Nitro module code. Run the following command in the module directory to generate `./nitrogen/generated` files, which are required by both iOS and Android.

4. Modify the `react-native.config.js` file in the example directory of the current project to configure the module dependency.

```Javascript
const path = require('path');
const pkg = require('../package.json');
const baseConfig = require('../../../react-native.base.config');

module.exports = {
  ...baseConfig,
  dependencies: {
    [pkg.name]: {
      root: path.join(__dirname, '..'),
      platforms: {
        // Codegen script incorrectly fails without this
        // So we explicitly specify the platforms with empty object
        ios: {},
        android: {},
      },
    },
  },
};
```
5. Run `pod install` in the `example/ios` directory of the current module to install dependencies.

6. Now you can start the example app in iOS Simulator to test the module.

7. Modify the `metro.config.js` file in the example directory to configure the monorepo setup:

```
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
const workspaceRoot = path.resolve(__dirname, '../../../');
const metroConfig = withMetroConfig(getDefaultConfig(__dirname), {
  root,
  dirname: __dirname,
});

metroConfig.watchFolders = [workspaceRoot];

metroConfig.resolver.nodeModulesPaths = [
  path.resolve(root, 'node_modules'),
  path.resolve(workspaceRoot, 'node_modules'),
];

module.exports = metroConfig;

```

8. start metro server in `example` folder with `yarn start`

9. Let's talk about Android now. Change `new-library/example/android/settings.gradle`:

```
pluginManagement {
  def reactNativeGradlePlugin = new File(
    providers.exec {
      workingDir(rootDir)
      commandLine("node", "--print", "require.resolve('@react-native/gradle-plugin/package.json', { paths: [require.resolve('react-native/package.json')] })")
    }.standardOutput.asText.get().trim()
  ).getParentFile().absolutePath
  includeBuild(reactNativeGradlePlugin)
}
plugins { id("com.facebook.react.settings") }
extensions.configure(com.facebook.react.ReactSettingsExtension){ ex -> ex.autolinkLibrariesFromCommand() }
rootProject.name = 'onekeyfe.reactnativecheckbiometricauthchanged.example'
include ':app'

def reactNativeGradlePlugin = new File(
providers.exec {
    workingDir(rootDir)
    commandLine("node", "--print", "require.resolve('@react-native/gradle-plugin/package.json', { paths: [require.resolve('react-native/package.json')] })")
}.standardOutput.asText.get().trim()
).getParentFile().absolutePath
includeBuild(reactNativeGradlePlugin)
```

10.  Add the following variables at the top of the react block in `new-library/example/android/app/build.gradle`: 

```
react {
    reactNativeDir = new File(["node", "--print", "require.resolve('react-native/package.json')"].execute(null, rootDir).text.trim()).getParentFile().getAbsoluteFile()
    hermesCommand = new File(["node", "--print", "require.resolve('react-native/package.json')"].execute(null, rootDir).text.trim()).getParentFile().getAbsolutePath() + "/sdks/hermesc/%OS-BIN%/hermesc"
    codegenDir = new File(["node", "--print", "require.resolve('@react-native/codegen/package.json')"].execute(null, rootDir).text.trim()).getParentFile().getAbsoluteFile()
    enableBundleCompression = (findProperty('android.enableBundleCompression') ?: false).toBoolean()
    /* Folders */
    //   The root of your project, i.e. where "package.json" lives. Default is '../..'
    // root = file("../../")
    // ...
}
```

11. Now you can build and run the Android version.


## Publish all package

To update the versions of all workspace packages, run the following command in the project root directory:

```shell
yarn version:bump
yarn version:apply
```
Commit version changes and push to GitHub.

Run publish package actions on GitHub.