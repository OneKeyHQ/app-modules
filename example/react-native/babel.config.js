const { pathToFileURL } = require('node:url');

function resolveTestImportMetaUrl({ types }) {
  return {
    visitor: {
      MemberExpression(path, state) {
        const { object, property } = path.node;
        if (
          types.isMetaProperty(object) &&
          object.meta.name === 'import' &&
          object.property.name === 'meta' &&
          types.isIdentifier(property, { name: 'url' })
        ) {
          path.replaceWith(types.stringLiteral(pathToFileURL(state.filename).href));
        }
      },
    },
  };
}

module.exports = function (api) {
  const isTest = api.env('test');
  return {
    presets: ['module:@react-native/babel-preset'],
    ...(isTest ? { plugins: [resolveTestImportMetaUrl] } : {}),
  };
};
