#!/usr/bin/env node

/**
 * React Native Nitro Module Setup Script
 * Automatically configure Nitro Module based on README.md instructions
 *
 * Usage: node setup-nitro-module.js <module-directory>
 * Example: node setup-nitro-module.js example/react-native-cloud-kit
 */

const fs = require('fs');
const path = require('path');

// Check arguments
if (process.argv.length < 3) {
    console.log("Error: Please provide module directory name");
    console.log(`Usage: ${path.basename(process.argv[1])} <module-directory-name>`);
    console.log(`Example: ${path.basename(process.argv[1])} react-native-cloud-kit-module`);
    process.exit(1);
}

const scriptDir = __dirname;
const workspaceRoot = path.dirname(scriptDir);
const moduleDir = path.join(workspaceRoot, 'example', process.argv[2]);

// Check if module directory exists
if (!fs.existsSync(moduleDir)) {
    console.log(`Error: Directory '${moduleDir}' does not exist`);
    process.exit(1);
}

const absModuleDir = moduleDir;

console.log(`Setting up Nitro Module: ${absModuleDir}`);

// Helper function: Check if file contains specified string
function fileContains(filePath, searchString) {
    try {
        const content = fs.readFileSync(filePath, 'utf8');
        return content.includes(searchString);
    } catch (error) {
        return false;
    }
}

// Step 0: Sync version from react-native-lite-card
console.log("Step 0: Syncing version from react-native-lite-card...");
const liteCardPackageJsonPath = path.join(workspaceRoot, 'example', 'react-native-lite-card', 'package.json');
const packageJsonPath = path.join(absModuleDir, 'package.json');

if (fs.existsSync(liteCardPackageJsonPath) && fs.existsSync(packageJsonPath)) {
    try {
        const liteCardPackageJson = JSON.parse(fs.readFileSync(liteCardPackageJsonPath, 'utf8'));
        const packageJson = JSON.parse(fs.readFileSync(packageJsonPath, 'utf8'));
        
        if (liteCardPackageJson.version) {
            packageJson.version = liteCardPackageJson.version;
            fs.writeFileSync(packageJsonPath, JSON.stringify(packageJson, null, 2) + '\n');
            console.log(`  - ✓ Version synced to ${liteCardPackageJson.version}`);
        } else {
            console.log("  - Warning: Version field not found in react-native-lite-card/package.json");
        }
    } catch (error) {
        console.log(`  - Warning: Failed to sync version: ${error.message}`);
    }
} else {
    if (!fs.existsSync(liteCardPackageJsonPath)) {
        console.log("  - Warning: react-native-lite-card/package.json not found");
    }
    if (!fs.existsSync(packageJsonPath)) {
        console.log("  - Warning: package.json file not found");
    }
}

// Step 1: Check and remove packageManager field from package.json
console.log("Step 1: Checking packageManager field in package.json...");

if (fs.existsSync(packageJsonPath)) {
    if (fileContains(packageJsonPath, '"packageManager"')) {
        console.log("  - Found packageManager field, recommend manual removal to avoid conflicts");
        console.log(`  - Location: ${packageJsonPath}`);
        console.log("  - Please remove lines like: \"packageManager\": \"yarn@x.x.x\",");
    } else {
        console.log("  - ✓ No packageManager field found");
    }
} else {
    console.log("  - Warning: package.json file not found");
}

// Step 2: Modify react-native.config.js
console.log("Step 2: Configuring react-native.config.js...");
const rnConfigFile = path.join(absModuleDir, 'example', 'react-native.config.js');

if (fs.existsSync(rnConfigFile)) {
    if (fileContains(rnConfigFile, 'react-native.base.config')) {
        console.log("  - ✓ react-native.config.js already contains baseConfig configuration");
    } else {
        console.log("  - Updating react-native.config.js...");
        
        const newConfig = `const path = require('path');
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
`;
        fs.writeFileSync(rnConfigFile, newConfig);
        console.log("  - ✓ react-native.config.js updated");
    }
} else {
    console.log("  - Warning: react-native.config.js file not found");
}

// Step 3: Modify metro.config.js
console.log("Step 3: Configuring metro.config.js...");
const metroConfigFile = path.join(absModuleDir, 'example', 'metro.config.js');

if (fs.existsSync(metroConfigFile)) {
    if (fileContains(metroConfigFile, 'workspaceRoot')) {
        console.log("  - ✓ metro.config.js already contains monorepo configuration");
    } else {
        console.log("  - Updating metro.config.js...");
        
        const newMetroConfig = `const path = require('path');
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
`;
        fs.writeFileSync(metroConfigFile, newMetroConfig);
        console.log("  - ✓ metro.config.js updated");
    }
} else {
    console.log("  - Warning: metro.config.js file not found");
}

// Step 4: Modify Android settings.gradle
console.log("Step 4: Configuring Android settings.gradle...");
const androidSettingsFile = path.join(absModuleDir, 'example', 'android', 'settings.gradle');

if (fs.existsSync(androidSettingsFile)) {
    const settingsContent = fs.readFileSync(androidSettingsFile, 'utf8');
    if (settingsContent.includes('pluginManagement') && settingsContent.includes('commandLine.*node.*--print')) {
        console.log("  - ✓ Android settings.gradle already contains correct configuration");
    } else {
        console.log("  - Updating Android settings.gradle...");
        
        // Get project name
        let projectName = 'example';
        const projectNameMatch = settingsContent.match(/rootProject\.name\s*=\s*['"](.*)['"]/);
        if (projectNameMatch) {
            projectName = projectNameMatch[1];
        }
        
        const newSettingsConfig = `pluginManagement {
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
rootProject.name = '${projectName}'
include ':app'

def reactNativeGradlePlugin = new File(
providers.exec {
    workingDir(rootDir)
    commandLine("node", "--print", "require.resolve('@react-native/gradle-plugin/package.json', { paths: [require.resolve('react-native/package.json')] })")
}.standardOutput.asText.get().trim()
).getParentFile().absolutePath
includeBuild(reactNativeGradlePlugin)
`;
        fs.writeFileSync(androidSettingsFile, newSettingsConfig);
        console.log("  - ✓ Android settings.gradle updated");
    }
} else {
    console.log("  - Warning: Android settings.gradle file not found");
}

// Step 5: Modify Android app/build.gradle
console.log("Step 5: Configuring Android app/build.gradle...");
const androidBuildFile = path.join(absModuleDir, 'example', 'android', 'app', 'build.gradle');

if (fs.existsSync(androidBuildFile)) {
    if (fileContains(androidBuildFile, 'reactNativeDir.*node.*--print')) {
        console.log("  - ✓ Android app/build.gradle already contains correct react configuration");
    } else {
        console.log("  - Updating Android app/build.gradle...");
        
        let buildContent = fs.readFileSync(androidBuildFile, 'utf8');
        
        // Add configuration to react { block
        const reactBlockRegex = /(react\s*\{)/;
        const additionalConfig = `    reactNativeDir = new File(["node", "--print", "require.resolve('react-native/package.json')"].execute(null, rootDir).text.trim()).getParentFile().getAbsoluteFile()
    hermesCommand = new File(["node", "--print", "require.resolve('react-native/package.json')"].execute(null, rootDir).text.trim()).getParentFile().getAbsolutePath() + "/sdks/hermesc/%OS-BIN%/hermesc"
    codegenDir = new File(["node", "--print", "require.resolve('@react-native/codegen/package.json')"].execute(null, rootDir).text.trim()).getParentFile().getAbsoluteFile()
    enableBundleCompression = (findProperty('android.enableBundleCompression') ?: false).toBoolean()
`;
        
        if (reactBlockRegex.test(buildContent)) {
            buildContent = buildContent.replace(reactBlockRegex, `$1\n${additionalConfig}`);
            fs.writeFileSync(androidBuildFile, buildContent);
            console.log("  - ✓ Android app/build.gradle updated");
        } else {
            console.log("  - Warning: react configuration block not found");
        }
    }
} else {
    console.log("  - Warning: Android app/build.gradle file not found");
}

// Step 6: Update release script in package.json
console.log("Step 6: Configuring release script...");
if (fs.existsSync(packageJsonPath)) {
    if (fileContains(packageJsonPath, '"release".*nitrogen')) {
        console.log("  - ✓ release script already contains nitrogen command");
    } else {
        console.log("  - Recommend manually updating release script in package.json:");
        console.log("    \"release\": \"yarn nitrogen && yarn prepare && release-it --only-version\"");
        console.log(`  - Location: ${packageJsonPath}`);
    }
}

console.log("");
console.log("🎉 Nitro Module configuration completed!");
console.log("");
console.log("Next steps:");
console.log("1. Run 'yarn' to install dependencies");
console.log(`2. Run 'yarn nitrogen' in ${absModuleDir} directory to generate necessary files`);
console.log(`3. Run 'pod install' in ${absModuleDir}/example/ios directory`);
console.log(`4. Start Metro server: cd ${absModuleDir}/example && yarn start`);
console.log("5. Build and run iOS/Android app for testing");
