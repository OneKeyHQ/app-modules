# Nitro View Template System

This directory contains templates for creating new React Native Nitro Views using the `create-nitro-view.js` script.

## Template Structure

```
template/
├── package.json                          # Package configuration with dependencies
├── nitro.json                           # Nitro configuration 
├── src/
│   ├── index.tsx                        # Main export file
│   └── ViewName.nitro.ts               # TypeScript interface definitions
├── ios/
│   └── ViewName.swift                  # iOS Swift implementation
├── android/
│   ├── build.gradle                    # Android build configuration
│   ├── CMakeLists.txt                  # CMake build configuration
│   └── src/main/
│       ├── AndroidManifest.xml         # Android manifest
│       ├── cpp/
│       │   └── cpp-adapter.cpp         # C++ JNI adapter
│       └── java/com/margelo/nitro/viewname/
│           ├── ViewName.kt             # Android Kotlin implementation
│           └── ViewNamePackage.kt      # React Native package
├── config/
│   ├── babel.config.js                 # Babel configuration
│   ├── eslint.config.mjs               # ESLint configuration
│   ├── lefthook.yml                    # Git hooks configuration
│   ├── tsconfig.json                   # TypeScript configuration
│   ├── tsconfig.build.json             # TypeScript build configuration
│   └── turbo.json                      # Turbo build configuration
├── docs/
│   ├── README.md                       # Project README template
│   └── LICENSE                         # MIT license template
└── ViewName.podspec                    # iOS CocoaPods specification
```

## Template Variables

The templates use the following variables that are replaced during generation:

- `{{viewName}}` - Original view name (e.g., "loading-spinner")
- `{{viewDirectory}}` - Directory name (e.g., "react-native-loading-spinner")
- `{{viewPascalCase}}` - PascalCase name (e.g., "LoadingSpinner")
- `{{viewCamelCase}}` - camelCase name (e.g., "loadingSpinner")
- `{{viewKebabCase}}` - kebab-case name (e.g., "loading-spinner")
- `{{cxxNamespace}}` - C++ namespace (e.g., "loadingspinner")
- `{{viewVersion}}` - Version number (inherited from skeleton project)

## Usage

```bash
# Create a new Nitro View
node scripts/create-nitro-view.js loading-spinner

# This will create native-views/react-native-loading-spinner/
# with all files populated from these templates
```

## Customizing Templates

To customize the generated views:

1. Edit the template files in this directory
2. Use `{{variableName}}` syntax for dynamic content
3. The script will automatically replace placeholders when generating new views

## Based on

These templates are based on the structure of `native-views/react-native-skeleton/` and follow the same patterns and conventions.
