module.exports = {
  dependency: {
    platforms: {
      android: {
        componentDescriptors: ['RNBottomTabsViewComponentDescriptor'],
        cmakeListsPath: undefined,
        packageImportPath:
          'import com.onekey.bottomtabs.BottomTabsPackage;',
        packageInstance: 'new BottomTabsPackage()',
      },
    },
  },
};
