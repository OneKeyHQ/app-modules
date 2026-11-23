// The template's `module-resolver` plugin is not installed in this repository;
// use the builder-bob preset like the other packages so jest can run.
module.exports = {
  overrides: [
    {
      exclude: /\/node_modules\//,
      presets: ['module:react-native-builder-bob/babel-preset'],
    },
    {
      include: /\/node_modules\//,
      presets: ['module:@react-native/babel-preset'],
    },
  ],
};
