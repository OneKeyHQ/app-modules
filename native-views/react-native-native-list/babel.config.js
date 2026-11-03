const importMetaUrlPlugin = ({ types: t }) => ({
  visitor: {
    MetaProperty(path) {
      if (
        path.node.meta.name === 'import' &&
        path.node.property.name === 'meta' &&
        path.parentPath.isMemberExpression() &&
        path.parentPath.get('property').isIdentifier({ name: 'url' })
      ) {
        path.parentPath.replaceWith(
          t.stringLiteral('https://fixture.invalid/assetUrls.ts')
        );
      }
    },
  },
});

module.exports = {
  overrides: [
    {
      exclude: /\/node_modules\//,
      presets: ['module:react-native-builder-bob/babel-preset'],
      plugins: process.env.NODE_ENV === 'test' ? [importMetaUrlPlugin] : [],
    },
    {
      include: /\/node_modules\//,
      presets: ['module:@react-native/babel-preset'],
    },
  ],
};
