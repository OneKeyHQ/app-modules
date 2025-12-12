const path = require('path');

module.exports = {
  reactNativePath: path.join(__dirname, 'node_modules', 'react-native'),
  project: {
    ios: {
      automaticPodsInstallation: true,
    },
  },
};
