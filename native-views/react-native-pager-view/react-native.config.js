module.exports = {
  dependency: {
    platforms: {
      android: {
        libraryName: 'pagerview',
        componentDescriptors: [
          'RNCViewPagerComponentDescriptor',
          'RNCCollapsiblePagerViewComponentDescriptor',
          'RNCNativeScrollerComponentDescriptor',
        ],
        cmakeListsPath: 'src/main/jni/CMakeLists.txt',
      },
    },
  },
};
