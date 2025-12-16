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