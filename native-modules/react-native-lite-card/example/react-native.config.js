const path = require('path');
const pkg = require('../package.json');

module.exports = {
  reactNativePath: path.join(__dirname, '..', '..', '..', 'node_modules', 'react-native'),
  project: {
    ios: {
      automaticPodsInstallation: true,
    },
  },
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
