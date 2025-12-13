#!/usr/bin/env node

/**
 * React Native Nitro Module Setup Script
 * 根据 README.md 的描述自动配置 Nitro Module
 *
 * 用法: node setup-nitro-module.js <module-directory>
 * 示例: node setup-nitro-module.js native-modules/react-native-cloud-kit
 */

const fs = require('fs');
const path = require('path');

// 检查参数
if (process.argv.length < 3) {
    console.log("错误: 请提供模块目录路径");
    console.log(`用法: ${path.basename(process.argv[1])} <module-directory>`);
    console.log(`示例: ${path.basename(process.argv[1])} native-modules/react-native-cloud-kit`);
    process.exit(1);
}

const moduleDir = process.argv[2];
const scriptDir = __dirname;
const workspaceRoot = path.dirname(scriptDir);

// 检查模块目录是否存在
if (!fs.existsSync(moduleDir)) {
    console.log(`错误: 目录 '${moduleDir}' 不存在`);
    process.exit(1);
}

// 转换为绝对路径
let absModuleDir;
if (path.isAbsolute(moduleDir)) {
    absModuleDir = moduleDir;
} else {
    absModuleDir = path.join(workspaceRoot, moduleDir);
}

console.log(`正在设置 Nitro Module: ${absModuleDir}`);

// 辅助函数：检查文件内容是否包含指定字符串
function fileContains(filePath, searchString) {
    try {
        const content = fs.readFileSync(filePath, 'utf8');
        return content.includes(searchString);
    } catch (error) {
        return false;
    }
}

// 辅助函数：备份文件
function backupFile(filePath) {
    try {
        fs.copyFileSync(filePath, `${filePath}.backup`);
    } catch (error) {
        console.log(`  - 警告: 无法备份文件 ${filePath}: ${error.message}`);
    }
}

// 步骤 1: 检查并删除 package.json 中的 packageManager 字段
console.log("步骤 1: 检查 package.json 中的 packageManager 字段...");
const packageJsonPath = path.join(absModuleDir, 'package.json');

if (fs.existsSync(packageJsonPath)) {
    if (fileContains(packageJsonPath, '"packageManager"')) {
        console.log("  - 发现 packageManager 字段，建议手动删除以避免冲突");
        console.log(`  - 位置: ${packageJsonPath}`);
        console.log("  - 请删除类似这样的行: \"packageManager\": \"yarn@x.x.x\",");
    } else {
        console.log("  - ✓ 未发现 packageManager 字段");
    }
} else {
    console.log("  - 警告: 未找到 package.json 文件");
}

// 步骤 2: 修改 react-native.config.js
console.log("步骤 2: 配置 react-native.config.js...");
const rnConfigFile = path.join(absModuleDir, 'example', 'react-native.config.js');

if (fs.existsSync(rnConfigFile)) {
    if (fileContains(rnConfigFile, 'react-native.base.config')) {
        console.log("  - ✓ react-native.config.js 已包含 baseConfig 配置");
    } else {
        console.log("  - 更新 react-native.config.js...");
        backupFile(rnConfigFile);
        
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
        console.log("  - ✓ react-native.config.js 已更新");
    }
} else {
    console.log("  - 警告: 未找到 react-native.config.js 文件");
}

// 步骤 3: 修改 metro.config.js
console.log("步骤 3: 配置 metro.config.js...");
const metroConfigFile = path.join(absModuleDir, 'example', 'metro.config.js');

if (fs.existsSync(metroConfigFile)) {
    if (fileContains(metroConfigFile, 'workspaceRoot')) {
        console.log("  - ✓ metro.config.js 已包含 monorepo 配置");
    } else {
        console.log("  - 更新 metro.config.js...");
        backupFile(metroConfigFile);
        
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
        console.log("  - ✓ metro.config.js 已更新");
    }
} else {
    console.log("  - 警告: 未找到 metro.config.js 文件");
}

// 步骤 4: 修改 Android settings.gradle
console.log("步骤 4: 配置 Android settings.gradle...");
const androidSettingsFile = path.join(absModuleDir, 'example', 'android', 'settings.gradle');

if (fs.existsSync(androidSettingsFile)) {
    const settingsContent = fs.readFileSync(androidSettingsFile, 'utf8');
    if (settingsContent.includes('pluginManagement') && settingsContent.includes('commandLine.*node.*--print')) {
        console.log("  - ✓ Android settings.gradle 已包含正确配置");
    } else {
        console.log("  - 更新 Android settings.gradle...");
        backupFile(androidSettingsFile);
        
        // 获取项目名称
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
        console.log("  - ✓ Android settings.gradle 已更新");
    }
} else {
    console.log("  - 警告: 未找到 Android settings.gradle 文件");
}

// 步骤 5: 修改 Android app/build.gradle
console.log("步骤 5: 配置 Android app/build.gradle...");
const androidBuildFile = path.join(absModuleDir, 'example', 'android', 'app', 'build.gradle');

if (fs.existsSync(androidBuildFile)) {
    if (fileContains(androidBuildFile, 'reactNativeDir.*node.*--print')) {
        console.log("  - ✓ Android app/build.gradle 已包含正确的 react 配置");
    } else {
        console.log("  - 更新 Android app/build.gradle...");
        backupFile(androidBuildFile);
        
        let buildContent = fs.readFileSync(androidBuildFile, 'utf8');
        
        // 在 react { 块中添加配置
        const reactBlockRegex = /(react\s*\{)/;
        const additionalConfig = `    reactNativeDir = new File(["node", "--print", "require.resolve('react-native/package.json')"].execute(null, rootDir).text.trim()).getParentFile().getAbsoluteFile()
    hermesCommand = new File(["node", "--print", "require.resolve('react-native/package.json')"].execute(null, rootDir).text.trim()).getParentFile().getAbsolutePath() + "/sdks/hermesc/%OS-BIN%/hermesc"
    codegenDir = new File(["node", "--print", "require.resolve('@react-native/codegen/package.json')"].execute(null, rootDir).text.trim()).getParentFile().getAbsoluteFile()
    enableBundleCompression = (findProperty('android.enableBundleCompression') ?: false).toBoolean()
`;
        
        if (reactBlockRegex.test(buildContent)) {
            buildContent = buildContent.replace(reactBlockRegex, `$1\n${additionalConfig}`);
            fs.writeFileSync(androidBuildFile, buildContent);
            console.log("  - ✓ Android app/build.gradle 已更新");
        } else {
            console.log("  - 警告: 未找到 react 配置块");
        }
    }
} else {
    console.log("  - 警告: 未找到 Android app/build.gradle 文件");
}

// 步骤 6: 更新 package.json 中的 release 脚本
console.log("步骤 6: 配置 release 脚本...");
if (fs.existsSync(packageJsonPath)) {
    if (fileContains(packageJsonPath, '"release".*nitrogen')) {
        console.log("  - ✓ release 脚本已包含 nitrogen 命令");
    } else {
        console.log("  - 建议手动更新 package.json 中的 release 脚本：");
        console.log("    \"release\": \"yarn nitrogen && yarn prepare && release-it --only-version\"");
        console.log(`  - 位置: ${packageJsonPath}`);
    }
}

console.log("");
console.log("🎉 Nitro Module 配置完成！");
console.log("");
console.log("接下来的步骤：");
console.log("1. 运行 'yarn' 安装依赖");
console.log(`2. 在 ${absModuleDir} 目录运行 'yarn nitrogen' 生成必要文件`);
console.log(`3. 在 ${absModuleDir}/example/ios 目录运行 'pod install'`);
console.log(`4. 启动 Metro 服务器: cd ${absModuleDir}/example && yarn start`);
console.log("5. 构建并运行 iOS/Android 应用进行测试");
console.log("");
console.log("备份文件已保存为 *.backup，如有问题可以恢复");
