module.exports = {
  dependency: {
    platforms: {
      android: {
        componentDescriptors: ['RCTTabViewComponentDescriptor'],
        cmakeListsPath: undefined,
        packageImportPath:
          'import com.rcttabview.RCTTabViewPackage;',
        packageInstance: 'new RCTTabViewPackage()',
      },
    },
  },
};
