module.exports = {
  dependency: {
    platforms: {
      android: {
        componentDescriptors: [
          'RNBottomTabsViewComponentDescriptor',
          'RNBottomTabsDrawerComponentDescriptor',
        ],
        cmakeListsPath: undefined,
        packageImportPath:
          'import com.onekey.bottomtabs.BottomTabsPackage;',
        packageInstance: 'new BottomTabsPackage()',
      },
    },
  },
};
