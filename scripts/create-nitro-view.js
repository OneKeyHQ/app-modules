#!/usr/bin/env node

/**
 * React Native Nitro View Creation Script
 * Creates a new Nitro View with all necessary files and structure
 * Based on react-native-skeleton template
 *
 * Usage: node create-nitro-view.js <view-name>
 * Example: node create-nitro-view.js loading-spinner
 */

const fs = require('fs');
const path = require('path');

// Check arguments
if (process.argv.length < 3) {
    console.log("Error: Please provide view name");
    console.log(`Usage: ${path.basename(process.argv[1])} <view-name>`);
    console.log(`Example: ${path.basename(process.argv[1])} loading-spinner`);
    process.exit(1);
}

const scriptDir = __dirname;
const workspaceRoot = path.dirname(scriptDir);
const templateDir = path.join(scriptDir, 'nitro-view', 'template');
const viewName = process.argv[2];
const viewDirectory = `react-native-${viewName}`;
const viewDir = path.join(workspaceRoot, 'native-views', viewDirectory);

// Check if view directory already exists
if (fs.existsSync(viewDir)) {
    console.log(`Error: Directory '${viewDir}' already exists`);
    process.exit(1);
}

console.log(`Creating Nitro View: ${viewDirectory}`);

// Helper functions
function ensureDirectoryExists(dirPath) {
    if (!fs.existsSync(dirPath)) {
        fs.mkdirSync(dirPath, { recursive: true });
    }
}

function toPascalCase(str) {
    return str.split('-').map(word => 
        word.charAt(0).toUpperCase() + word.slice(1).toLowerCase()
    ).join('');
}

function toCamelCase(str) {
    const pascal = toPascalCase(str);
    return pascal.charAt(0).toLowerCase() + pascal.slice(1);
}

function toKebabCase(str) {
    return str.toLowerCase().replace(/[^a-z0-9]+/g, '-');
}

// Template processing function
function processTemplate(templatePath, variables) {
    if (!fs.existsSync(templatePath)) {
        throw new Error(`Template file not found: ${templatePath}`);
    }
    
    let content = fs.readFileSync(templatePath, 'utf8');
    
    // Replace all {{variableName}} with actual values
    Object.keys(variables).forEach(key => {
        const regex = new RegExp(`{{${key}}}`, 'g');
        content = content.replace(regex, variables[key]);
    });
    
    return content;
}

function writeFileFromTemplate(templatePath, outputPath, variables) {
    const content = processTemplate(templatePath, variables);
    ensureDirectoryExists(path.dirname(outputPath));
    fs.writeFileSync(outputPath, content);
    console.log(`  - Created: ${path.relative(viewDir, outputPath)}`);
}

// Generate names
const viewPascalCase = toPascalCase(viewName);
const viewCamelCase = toCamelCase(viewName);
const viewKebabCase = toKebabCase(viewName);
const cxxNamespace = viewKebabCase.replace(/-/g, '');

// Read version from skeleton
const skeletonPackageJsonPath = path.join(workspaceRoot, 'native-views', 'react-native-skeleton', 'package.json');
let viewVersion = '1.0.0'; // Default version
if (fs.existsSync(skeletonPackageJsonPath)) {
    try {
        const skeletonPackageJson = JSON.parse(fs.readFileSync(skeletonPackageJsonPath, 'utf8'));
        viewVersion = skeletonPackageJson.version || '1.0.0';
    } catch (error) {
        console.warn(`Warning: Could not read version from skeleton package.json: ${error.message}`);
    }
}

// Template variables
const templateVars = {
    viewName,
    viewDirectory,
    viewPascalCase,
    viewCamelCase,
    viewKebabCase,
    cxxNamespace,
    viewVersion
};

console.log(`View names:`);
console.log(`  - Directory: ${viewDirectory}`);
console.log(`  - PascalCase: ${viewPascalCase}`);
console.log(`  - camelCase: ${viewCamelCase}`);
console.log(`  - kebab-case: ${viewKebabCase}`);
console.log(`  - cxxNamespace: ${cxxNamespace}`);
console.log(`  - version: ${viewVersion}`);

// Create main directory
ensureDirectoryExists(viewDir);

// Step 1: Create package.json
console.log("Step 1: Creating package.json...");
writeFileFromTemplate(
    path.join(templateDir, 'package.json'),
    path.join(viewDir, 'package.json'),
    templateVars
);

// Step 2: Create nitro.json
console.log("Step 2: Creating nitro.json...");
writeFileFromTemplate(
    path.join(templateDir, 'nitro.json'),
    path.join(viewDir, 'nitro.json'),
    templateVars
);

// Step 3: Create source files
console.log("Step 3: Creating source files...");

// src/index.tsx
writeFileFromTemplate(
    path.join(templateDir, 'src', 'index.tsx'),
    path.join(viewDir, 'src', 'index.tsx'),
    templateVars
);

// src/ViewName.nitro.ts
writeFileFromTemplate(
    path.join(templateDir, 'src', 'ViewName.nitro.ts'),
    path.join(viewDir, 'src', `${viewPascalCase}.nitro.ts`),
    templateVars
);

// Step 4: Create podspec
console.log("Step 4: Creating podspec...");
writeFileFromTemplate(
    path.join(templateDir, 'ViewName.podspec'),
    path.join(viewDir, `${viewPascalCase}.podspec`),
    templateVars
);

// Step 5: Create iOS files
console.log("Step 5: Creating iOS files...");
writeFileFromTemplate(
    path.join(templateDir, 'ios', 'ViewName.swift'),
    path.join(viewDir, 'ios', `${viewPascalCase}.swift`),
    templateVars
);

// Step 6: Create Android files
console.log("Step 6: Creating Android files...");

// android/build.gradle
writeFileFromTemplate(
    path.join(templateDir, 'android', 'build.gradle'),
    path.join(viewDir, 'android', 'build.gradle'),
    templateVars
);

// android/CMakeLists.txt
writeFileFromTemplate(
    path.join(templateDir, 'android', 'CMakeLists.txt'),
    path.join(viewDir, 'android', 'CMakeLists.txt'),
    templateVars
);

// android/src/main/AndroidManifest.xml
writeFileFromTemplate(
    path.join(templateDir, 'android', 'src', 'main', 'AndroidManifest.xml'),
    path.join(viewDir, 'android', 'src', 'main', 'AndroidManifest.xml'),
    templateVars
);

// android/src/main/cpp/cpp-adapter.cpp
writeFileFromTemplate(
    path.join(templateDir, 'android', 'src', 'main', 'cpp', 'cpp-adapter.cpp'),
    path.join(viewDir, 'android', 'src', 'main', 'cpp', 'cpp-adapter.cpp'),
    templateVars
);

// android/src/main/java/.../ViewName.kt
writeFileFromTemplate(
    path.join(templateDir, 'android', 'src', 'main', 'java', 'com', 'margelo', 'nitro', 'viewname', 'ViewName.kt'),
    path.join(viewDir, 'android', 'src', 'main', 'java', 'com', 'margelo', 'nitro', cxxNamespace, `${viewPascalCase}.kt`),
    templateVars
);

// android/src/main/java/.../ViewNamePackage.kt
writeFileFromTemplate(
    path.join(templateDir, 'android', 'src', 'main', 'java', 'com', 'margelo', 'nitro', 'viewname', 'ViewNamePackage.kt'),
    path.join(viewDir, 'android', 'src', 'main', 'java', 'com', 'margelo', 'nitro', cxxNamespace, `${viewPascalCase}Package.kt`),
    templateVars
);

// Step 7: Create TypeScript configuration files
console.log("Step 7: Creating TypeScript configuration files...");

// tsconfig.json
writeFileFromTemplate(
    path.join(templateDir, 'config', 'tsconfig.json'),
    path.join(viewDir, 'tsconfig.json'),
    templateVars
);

// tsconfig.build.json
writeFileFromTemplate(
    path.join(templateDir, 'config', 'tsconfig.build.json'),
    path.join(viewDir, 'tsconfig.build.json'),
    templateVars
);

// Step 8: Create other configuration files
console.log("Step 8: Creating configuration files...");

// babel.config.js
writeFileFromTemplate(
    path.join(templateDir, 'config', 'babel.config.js'),
    path.join(viewDir, 'babel.config.js'),
    templateVars
);

// eslint.config.mjs
writeFileFromTemplate(
    path.join(templateDir, 'config', 'eslint.config.mjs'),
    path.join(viewDir, 'eslint.config.mjs'),
    templateVars
);

// lefthook.yml
writeFileFromTemplate(
    path.join(templateDir, 'config', 'lefthook.yml'),
    path.join(viewDir, 'lefthook.yml'),
    templateVars
);

// turbo.json
writeFileFromTemplate(
    path.join(templateDir, 'config', 'turbo.json'),
    path.join(viewDir, 'turbo.json'),
    templateVars
);

// Step 9: Create documentation files
console.log("Step 9: Creating documentation files...");

// README.md
writeFileFromTemplate(
    path.join(templateDir, 'docs', 'README.md'),
    path.join(viewDir, 'README.md'),
    templateVars
);

// LICENSE
writeFileFromTemplate(
    path.join(templateDir, 'docs', 'LICENSE'),
    path.join(viewDir, 'LICENSE'),
    templateVars
);

console.log("");
console.log("🎉 Nitro View created successfully!");
console.log("");
console.log("Next steps:");
console.log(`1. cd ${path.relative(workspaceRoot, viewDir)}`);
console.log("2. yarn");
console.log("3. yarn nitrogen");
console.log("4. Start implementing your view logic in the iOS and Android files");
console.log("");
console.log("View structure created:");
console.log(`  - Package: @onekeyfe/react-native-${viewKebabCase}`);
console.log(`  - View Class: ${viewPascalCase}`);
console.log(`  - iOS: ios/${viewPascalCase}.swift`);
console.log(`  - Android: android/src/main/java/com/margelo/nitro/${cxxNamespace}/${viewPascalCase}.kt`);
console.log("");
