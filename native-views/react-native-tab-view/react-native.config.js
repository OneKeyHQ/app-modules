module.exports = {
  dependency: {
    platforms: {
      android: {
        componentDescriptors: ['RNCTabViewComponentDescriptor'],
        cmakeListsPath: undefined,
        packageImportPath:
          'import com.rcttabview.RCTTabViewPackage;',
        packageInstance: 'new RCTTabViewPackage()',
      },
    },
  },
};
