# Nitro Module Templates

This directory contains templates for creating React Native Nitro Modules using the `yarn module:nitro:create` command.

## Directory Structure

```
template/
├── package.json              # npm package configuration
├── nitro.json               # Nitro module configuration  
├── ModuleName.podspec       # iOS CocoaPods specification
├── src/                     # TypeScript source files
│   ├── index.tsx            # Main entry point
│   ├── ModuleName.nitro.ts  # Nitro interface definitions
│   └── __tests__/           # Test files
├── ios/                     # iOS native implementation
│   └── ModuleName.swift     # Swift implementation
├── android/                 # Android native implementation
│   ├── build.gradle         # Android build configuration
│   ├── CMakeLists.txt       # CMake configuration
│   ├── gradle.properties    # Gradle properties
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── cpp/             # C++ adapter
│       └── java/            # Kotlin implementation
├── config/                  # Configuration files
│   ├── tsconfig.json        # TypeScript configuration
│   ├── tsconfig.build.json  # TypeScript build configuration
│   ├── babel.config.js      # Babel configuration
│   ├── eslint.config.mjs    # ESLint configuration
│   ├── lefthook.yml         # Git hooks configuration
│   └── turbo.json           # Turbo configuration
└── docs/                    # Documentation templates
    ├── README.md            # Project README
    ├── LICENSE              # MIT License
    ├── CODE_OF_CONDUCT.md   # Code of Conduct
    └── CONTRIBUTING.md      # Contribution guidelines
```

## Template Variables

Templates use `{{variableName}}` syntax for variable substitution. Available variables:

- `{{moduleName}}` - The raw module name (e.g., "test-lib")
- `{{moduleDirectory}}` - Full module directory name (e.g., "react-native-test-lib")
- `{{modulePascalCase}}` - PascalCase module name (e.g., "TestLib")
- `{{moduleCamelCase}}` - camelCase module name (e.g., "testLib")
- `{{moduleKebabCase}}` - kebab-case module name (e.g., "test-lib")
- `{{cxxNamespace}}` - C++ namespace (e.g., "testlib")

## Usage

To create a new Nitro module:

```bash
yarn module:nitro:create my-new-module
```

This will:
1. Create a new directory `native-modules/react-native-my-new-module`
2. Process all template files, replacing variables with actual values
3. Generate a complete, ready-to-use Nitro module structure

## Customizing Templates

To modify the generated modules:

1. Edit the appropriate template file in this directory
2. Use the `{{variableName}}` syntax for dynamic values
3. Test your changes by creating a new module

## Template Files

### Core Files
- `package.json` - npm package configuration with scripts, dependencies
- `nitro.json` - Nitro module configuration for iOS/Android autolinking

### Source Code
- `src/index.tsx` - Main entry point that exports the hybrid object
- `src/ModuleName.nitro.ts` - TypeScript interface definitions for the native bridge
- `src/__tests__/index.test.tsx` - Basic test template

### Native Implementations
- `ios/ModuleName.swift` - iOS Swift implementation template
- `android/src/main/java/.../ModuleName.kt` - Android Kotlin implementation template

### Build Configuration
- `android/build.gradle` - Android build configuration
- `android/CMakeLists.txt` - CMake configuration for native code
- `ModuleName.podspec` - iOS CocoaPods specification

### Development Configuration
- `config/tsconfig.json` - TypeScript compiler configuration
- `config/babel.config.js` - Babel transpilation configuration
- `config/eslint.config.mjs` - ESLint linting configuration
- `config/lefthook.yml` - Git hooks for pre-commit checks
- `config/turbo.json` - Turbo build system configuration

### Documentation
- `docs/README.md` - Project documentation template
- `docs/LICENSE` - MIT license template
- `docs/CODE_OF_CONDUCT.md` - Code of conduct template
- `docs/CONTRIBUTING.md` - Contribution guidelines template
