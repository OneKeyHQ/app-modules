import NitroModules

class ReactNativePhotoLibrary: HybridReactNativePhotoLibrarySpec {
  func getSavePermission() throws -> Promise<PhotoSavePermission> {
    return Promise.parallel { ImagePhotoLibrary.permission() }
  }

  func requestSavePermission() throws -> Promise<PhotoSavePermission> {
    return ImagePhotoLibrary.requestPermission()
  }

  func saveToLibrary(path: String) throws -> Promise<Void> {
    return ImagePhotoLibrary.save(path: path)
  }
}
