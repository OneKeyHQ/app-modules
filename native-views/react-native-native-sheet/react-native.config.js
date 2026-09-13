module.exports = {
  dependency: {
    platforms: {
      android: {
        componentDescriptors: ['RNCNativeSheetComponentDescriptor'],
        cmakeListsPath: undefined,
        packageImportPath: 'import com.onekey.nativesheet.NativeSheetPackage;',
        packageInstance: 'new NativeSheetPackage()',
      },
    },
  },
};
