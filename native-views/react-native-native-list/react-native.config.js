const { android } = require('./nitro.json');

const androidPackage = [
  'com',
  'margelo',
  'nitro',
  ...android.androidNamespace,
].join('.');

module.exports = {
  dependency: {
    platforms: {
      android: {
        packageImportPath: `import ${androidPackage}.NativeListPackage;`,
        packageInstance: 'new NativeListPackage()',
      },
    },
  },
};
