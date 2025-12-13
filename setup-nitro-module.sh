#!/bin/bash

# React Native Nitro Module Setup Script
# 根据 README.md 的描述自动配置 Nitro Module
#
# 用法: ./setup-nitro-module.sh <module-directory>
# 示例: ./setup-nitro-module.sh native-modules/react-native-cloud-kit

set -e

# 检查参数
if [ $# -eq 0 ]; then
    echo "错误: 请提供模块目录路径"
    echo "用法: $0 <module-directory>"
    echo "示例: $0 native-modules/react-native-cloud-kit"
    exit 1
fi

MODULE_DIR="$1"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
WORKSPACE_ROOT="$SCRIPT_DIR"

# 检查模块目录是否存在
if [ ! -d "$MODULE_DIR" ]; then
    echo "错误: 目录 '$MODULE_DIR' 不存在"
    exit 1
fi

# 转换为绝对路径
if [[ "$MODULE_DIR" = /* ]]; then
    ABS_MODULE_DIR="$MODULE_DIR"
else
    ABS_MODULE_DIR="$WORKSPACE_ROOT/$MODULE_DIR"
fi

echo "正在设置 Nitro Module: $ABS_MODULE_DIR"

# 步骤 1: 检查并删除 package.json 中的 packageManager 字段
echo "步骤 1: 检查 package.json 中的 packageManager 字段..."
PACKAGE_JSON="$ABS_MODULE_DIR/package.json"

if [ -f "$PACKAGE_JSON" ]; then
    if grep -q '"packageManager"' "$PACKAGE_JSON"; then
        echo "  - 发现 packageManager 字段，建议手动删除以避免冲突"
        echo "  - 位置: $PACKAGE_JSON"
        echo "  - 请删除类似这样的行: \"packageManager\": \"yarn@x.x.x\","
    else
        echo "  - ✓ 未发现 packageManager 字段"
    fi
else
    echo "  - 警告: 未找到 package.json 文件"
fi

# 步骤 2: 修改 react-native.config.js
echo "步骤 2: 配置 react-native.config.js..."
RN_CONFIG_FILE="$ABS_MODULE_DIR/example/react-native.config.js"

if [ -f "$RN_CONFIG_FILE" ]; then
    # 检查是否已经包含 baseConfig
    if grep -q "react-native.base.config" "$RN_CONFIG_FILE"; then
        echo "  - ✓ react-native.config.js 已包含 baseConfig 配置"
    else
        echo "  - 更新 react-native.config.js..."
        # 备份原文件
        cp "$RN_CONFIG_FILE" "$RN_CONFIG_FILE.backup"
        
        # 创建新的配置文件
        cat > "$RN_CONFIG_FILE" << 'EOF'
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
EOF
        echo "  - ✓ react-native.config.js 已更新"
    fi
else
    echo "  - 警告: 未找到 react-native.config.js 文件"
fi

# 步骤 3: 修改 metro.config.js
echo "步骤 3: 配置 metro.config.js..."
METRO_CONFIG_FILE="$ABS_MODULE_DIR/example/metro.config.js"

if [ -f "$METRO_CONFIG_FILE" ]; then
    # 检查是否已经包含 workspaceRoot 配置
    if grep -q "workspaceRoot" "$METRO_CONFIG_FILE"; then
        echo "  - ✓ metro.config.js 已包含 monorepo 配置"
    else
        echo "  - 更新 metro.config.js..."
        # 备份原文件
        cp "$METRO_CONFIG_FILE" "$METRO_CONFIG_FILE.backup"
        
        # 创建新的配置文件
        cat > "$METRO_CONFIG_FILE" << 'EOF'
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
EOF
        echo "  - ✓ metro.config.js 已更新"
    fi
else
    echo "  - 警告: 未找到 metro.config.js 文件"
fi

# 步骤 4: 修改 Android settings.gradle
echo "步骤 4: 配置 Android settings.gradle..."
ANDROID_SETTINGS_FILE="$ABS_MODULE_DIR/example/android/settings.gradle"

if [ -f "$ANDROID_SETTINGS_FILE" ]; then
    # 检查是否已经包含新格式的配置
    if grep -q "pluginManagement" "$ANDROID_SETTINGS_FILE" && grep -q "commandLine.*node.*--print" "$ANDROID_SETTINGS_FILE"; then
        echo "  - ✓ Android settings.gradle 已包含正确配置"
    else
        echo "  - 更新 Android settings.gradle..."
        # 备份原文件
        cp "$ANDROID_SETTINGS_FILE" "$ANDROID_SETTINGS_FILE.backup"
        
        # 获取项目名称（从当前文件中提取或使用默认值）
        PROJECT_NAME=$(grep "rootProject.name" "$ANDROID_SETTINGS_FILE" | sed "s/.*= *['\"]\\(.*\\)['\"].*/\\1/" || echo "example")
        
        # 创建新的配置文件
        cat > "$ANDROID_SETTINGS_FILE" << EOF
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
rootProject.name = '$PROJECT_NAME'
include ':app'

def reactNativeGradlePlugin = new File(
providers.exec {
    workingDir(rootDir)
    commandLine("node", "--print", "require.resolve('@react-native/gradle-plugin/package.json', { paths: [require.resolve('react-native/package.json')] })")
}.standardOutput.asText.get().trim()
).getParentFile().absolutePath
includeBuild(reactNativeGradlePlugin)
EOF
        echo "  - ✓ Android settings.gradle 已更新"
    fi
else
    echo "  - 警告: 未找到 Android settings.gradle 文件"
fi

# 步骤 5: 修改 Android app/build.gradle
echo "步骤 5: 配置 Android app/build.gradle..."
ANDROID_BUILD_FILE="$ABS_MODULE_DIR/example/android/app/build.gradle"

if [ -f "$ANDROID_BUILD_FILE" ]; then
    # 检查是否已经包含必要的配置
    if grep -q "reactNativeDir.*node.*--print" "$ANDROID_BUILD_FILE"; then
        echo "  - ✓ Android app/build.gradle 已包含正确的 react 配置"
    else
        echo "  - 更新 Android app/build.gradle..."
        # 备份原文件
        cp "$ANDROID_BUILD_FILE" "$ANDROID_BUILD_FILE.backup"
        
        # 使用 sed 在 react { 块的开始处添加配置
        sed -i.tmp '/^react {/,/^}/ {
            /^react {/a\
    reactNativeDir = new File(["node", "--print", "require.resolve('\''react-native/package.json'\'')"].execute(null, rootDir).text.trim()).getParentFile().getAbsoluteFile()\
    hermesCommand = new File(["node", "--print", "require.resolve('\''react-native/package.json'\'')"].execute(null, rootDir).text.trim()).getParentFile().getAbsolutePath() + "/sdks/hermesc/%OS-BIN%/hermesc"\
    codegenDir = new File(["node", "--print", "require.resolve('\''@react-native/codegen/package.json'\'')"].execute(null, rootDir).text.trim()).getParentFile().getAbsoluteFile()\
    enableBundleCompression = (findProperty('\''android.enableBundleCompression'\'') ?: false).toBoolean()
        }' "$ANDROID_BUILD_FILE"
        
        # 删除临时文件
        rm -f "$ANDROID_BUILD_FILE.tmp"
        echo "  - ✓ Android app/build.gradle 已更新"
    fi
else
    echo "  - 警告: 未找到 Android app/build.gradle 文件"
fi

# 步骤 6: 更新 package.json 中的 release 脚本
echo "步骤 6: 配置 release 脚本..."
if [ -f "$PACKAGE_JSON" ]; then
    # 检查 release 脚本是否已包含 nitrogen 命令
    if grep -q '"release".*nitrogen' "$PACKAGE_JSON"; then
        echo "  - ✓ release 脚本已包含 nitrogen 命令"
    else
        echo "  - 建议手动更新 package.json 中的 release 脚本："
        echo "    \"release\": \"yarn nitrogen && yarn prepare && release-it --only-version\""
        echo "  - 位置: $PACKAGE_JSON"
    fi
fi

echo ""
echo "🎉 Nitro Module 配置完成！"
echo ""
echo "接下来的步骤："
echo "1. 运行 'yarn' 安装依赖"
echo "2. 在 $ABS_MODULE_DIR 目录运行 'yarn nitrogen' 生成必要文件"
echo "3. 在 $ABS_MODULE_DIR/example/ios 目录运行 'pod install'"
echo "4. 启动 Metro 服务器: cd $ABS_MODULE_DIR/example && yarn start"
echo "5. 构建并运行 iOS/Android 应用进行测试"
echo ""
echo "备份文件已保存为 *.backup，如有问题可以恢复"
