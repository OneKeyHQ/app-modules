#!/usr/bin/env node

/**
 * React Native Nitro Module Creation Script
 * Creates a new Nitro Module with all necessary files and structure
 * Based on react-native-keychain-module template
 *
 * Usage: node create-nitro-module.js <module-name>
 * Example: node create-nitro-module.js new-lib
 */

const fs = require('fs');
const path = require('path');

// Check arguments
if (process.argv.length < 3) {
    console.log("Error: Please provide module name");
    console.log(`Usage: ${path.basename(process.argv[1])} <module-name>`);
    console.log(`Example: ${path.basename(process.argv[1])} new-lib`);
    process.exit(1);
}

const scriptDir = __dirname;
const workspaceRoot = path.dirname(scriptDir);
const templateDir = path.join(scriptDir, 'nitro', 'template');
const moduleName = process.argv[2];
const moduleDirectory = `${moduleName}`;
const moduleDir = path.join(workspaceRoot, 'native-modules', moduleDirectory);

// Check if module directory already exists
if (fs.existsSync(moduleDir)) {
    console.log(`Error: Directory '${moduleDir}' already exists`);
    process.exit(1);
}

console.log(`Creating Nitro Module: ${moduleDirectory}`);

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
    console.log(`  - Created: ${path.relative(moduleDir, outputPath)}`);
}

// Generate names
const modulePascalCase = toPascalCase(moduleName);
const moduleCamelCase = toCamelCase(moduleName);
const moduleKebabCase = toKebabCase(moduleName);
const cxxNamespace = moduleKebabCase.replace(/-/g, '');

// Template variables
const templateVars = {
    moduleName,
    moduleDirectory,
    modulePascalCase,
    moduleCamelCase,
    moduleKebabCase,
    cxxNamespace
};

console.log(`Module names:`);
console.log(`  - Directory: ${moduleDirectory}`);
console.log(`  - PascalCase: ${modulePascalCase}`);
console.log(`  - camelCase: ${moduleCamelCase}`);
console.log(`  - kebab-case: ${moduleKebabCase}`);
console.log(`  - cxxNamespace: ${cxxNamespace}`);

// Create main directory
ensureDirectoryExists(moduleDir);

// Step 1: Create package.json
console.log("Step 1: Creating package.json...");
writeFileFromTemplate(
    path.join(templateDir, 'package.json'),
    path.join(moduleDir, 'package.json'),
    templateVars
);

// Step 2: Create nitro.json
console.log("Step 2: Creating nitro.json...");
writeFileFromTemplate(
    path.join(templateDir, 'nitro.json'),
    path.join(moduleDir, 'nitro.json'),
    templateVars
);

// Step 3: Create source files
console.log("Step 3: Creating source files...");

// src/index.tsx
writeFileFromTemplate(
    path.join(templateDir, 'src', 'index.tsx'),
    path.join(moduleDir, 'src', 'index.tsx'),
    templateVars
);

// src/ModuleName.nitro.ts
writeFileFromTemplate(
    path.join(templateDir, 'src', 'ModuleName.nitro.ts'),
    path.join(moduleDir, 'src', `${modulePascalCase}.nitro.ts`),
    templateVars
);

// Step 4: Create podspec
console.log("Step 4: Creating podspec...");
writeFileFromTemplate(
    path.join(templateDir, 'ModuleName.podspec'),
    path.join(moduleDir, `${modulePascalCase}.podspec`),
    templateVars
);

// Step 5: Create iOS files
console.log("Step 5: Creating iOS files...");
writeFileFromTemplate(
    path.join(templateDir, 'ios', 'ModuleName.swift'),
    path.join(moduleDir, 'ios', `${modulePascalCase}.swift`),
    templateVars
);

// Step 6: Create Android files
console.log("Step 6: Creating Android files...");

// android/build.gradle
writeFileFromTemplate(
    path.join(templateDir, 'android', 'build.gradle'),
    path.join(moduleDir, 'android', 'build.gradle'),
    templateVars
);

// android/CMakeLists.txt
writeFileFromTemplate(
    path.join(templateDir, 'android', 'CMakeLists.txt'),
    path.join(moduleDir, 'android', 'CMakeLists.txt'),
    templateVars
);

// android/gradle.properties
writeFileFromTemplate(
    path.join(templateDir, 'android', 'gradle.properties'),
    path.join(moduleDir, 'android', 'gradle.properties'),
    templateVars
);

// android/src/main/AndroidManifest.xml
writeFileFromTemplate(
    path.join(templateDir, 'android', 'src', 'main', 'AndroidManifest.xml'),
    path.join(moduleDir, 'android', 'src', 'main', 'AndroidManifest.xml'),
    templateVars
);

// android/src/main/cpp/cpp-adapter.cpp
writeFileFromTemplate(
    path.join(templateDir, 'android', 'src', 'main', 'cpp', 'cpp-adapter.cpp'),
    path.join(moduleDir, 'android', 'src', 'main', 'cpp', 'cpp-adapter.cpp'),
    templateVars
);

// android/src/main/java/com/margelo/nitro/modulename/ModuleName.kt
writeFileFromTemplate(
    path.join(templateDir, 'android', 'src', 'main', 'java', 'com', 'margelo', 'nitro', 'modulename', 'ModuleName.kt'),
    path.join(moduleDir, 'android', 'src', 'main', 'java', 'com', 'margelo', 'nitro', cxxNamespace, `${modulePascalCase}.kt`),
    templateVars
);

// Step 7: Create TypeScript configuration files
console.log("Step 7: Creating TypeScript configuration files...");

// tsconfig.json
writeFileFromTemplate(
    path.join(templateDir, 'config', 'tsconfig.json'),
    path.join(moduleDir, 'tsconfig.json'),
    templateVars
);

// tsconfig.build.json
writeFileFromTemplate(
    path.join(templateDir, 'config', 'tsconfig.build.json'),
    path.join(moduleDir, 'tsconfig.build.json'),
    templateVars
);

// Step 8: Create other configuration files
console.log("Step 8: Creating configuration files...");

// .gitignore
writeFileFromTemplate(
    path.join(templateDir, '.gitignore'),
    path.join(moduleDir, '.gitignore'),
    templateVars
);

// babel.config.js
writeFileFromTemplate(
    path.join(templateDir, 'config', 'babel.config.js'),
    path.join(moduleDir, 'babel.config.js'),
    templateVars
);

// eslint.config.mjs
writeFileFromTemplate(
    path.join(templateDir, 'config', 'eslint.config.mjs'),
    path.join(moduleDir, 'eslint.config.mjs'),
    templateVars
);

// lefthook.yml
writeFileFromTemplate(
    path.join(templateDir, 'config', 'lefthook.yml'),
    path.join(moduleDir, 'lefthook.yml'),
    templateVars
);

// turbo.json
writeFileFromTemplate(
    path.join(templateDir, 'config', 'turbo.json'),
    path.join(moduleDir, 'turbo.json'),
    templateVars
);

// Step 9: Create documentation files
console.log("Step 9: Creating documentation files...");

// README.md
writeFileFromTemplate(
    path.join(templateDir, 'docs', 'README.md'),
    path.join(moduleDir, 'README.md'),
    templateVars
);

// LICENSE
writeFileFromTemplate(
    path.join(templateDir, 'docs', 'LICENSE'),
    path.join(moduleDir, 'LICENSE'),
    templateVars
);

console.log("");
console.log("🎉 Nitro Module created successfully!");
console.log("");
console.log("Next steps:");
console.log(`1. cd ${path.relative(workspaceRoot, moduleDir)}`);
console.log("2. yarn");
console.log("3. yarn nitrogen");
console.log("4. Start implementing your module logic in the iOS and Android files");
console.log("");
console.log("Module structure created:");
console.log(`  - Package: @onekeyfe/${moduleDirectory}`);
console.log(`  - Module Class: ${modulePascalCase}`);
console.log(`  - iOS: ios/${modulePascalCase}.swift`);
console.log(`  - Android: android/src/main/java/com/margelo/nitro/${cxxNamespace}/${modulePascalCase}.kt`);
console.log("");