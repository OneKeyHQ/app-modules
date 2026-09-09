module.exports = {
  dependency: {
    platforms: {
      android: {
        cmakeListsPath: 'src/main/jni/CMakeLists.txt',
        componentDescriptors: ['OneKeyTextComponentDescriptor'],
        packageImportPath: 'import com.onekey.text.OneKeyTextPackage;',
        packageInstance: 'new OneKeyTextPackage()',
      },
      ios: null,
    },
  },
};
