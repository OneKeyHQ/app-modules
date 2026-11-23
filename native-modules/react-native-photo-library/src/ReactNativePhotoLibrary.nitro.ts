import type { HybridObject } from 'react-native-nitro-modules';

export type PhotoSavePermissionStatus = 'granted' | 'denied' | 'undetermined';

export interface PhotoSavePermission {
  status: PhotoSavePermissionStatus;
  canAskAgain: boolean;
}

export interface ReactNativePhotoLibrary
  extends HybridObject<{ ios: 'swift'; android: 'kotlin' }> {
  getSavePermission(): Promise<PhotoSavePermission>;
  requestSavePermission(): Promise<PhotoSavePermission>;
  saveToLibrary(path: string): Promise<void>;
}
