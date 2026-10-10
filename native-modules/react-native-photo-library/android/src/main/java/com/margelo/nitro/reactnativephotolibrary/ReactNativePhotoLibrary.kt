package com.margelo.nitro.reactnativephotolibrary

import com.facebook.proguard.annotations.DoNotStrip
import com.margelo.nitro.core.Promise

@DoNotStrip
class ReactNativePhotoLibrary : HybridReactNativePhotoLibrarySpec() {
  override fun getSavePermission(): Promise<PhotoSavePermission> = ImagePhotoLibrary.getPermission()
  override fun requestSavePermission(): Promise<PhotoSavePermission> = ImagePhotoLibrary.requestPermission()
  override fun saveToLibrary(path: String): Promise<Unit> = ImagePhotoLibrary.save(path)
}
