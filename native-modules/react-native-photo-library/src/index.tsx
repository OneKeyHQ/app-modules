import { NitroModules } from 'react-native-nitro-modules';

import type {
  PhotoSavePermission,
  ReactNativePhotoLibrary,
} from './ReactNativePhotoLibrary.nitro';

export type {
  PhotoSavePermission,
  PhotoSavePermissionStatus,
} from './ReactNativePhotoLibrary.nitro';

let hybridObject: ReactNativePhotoLibrary | undefined;

function native(): ReactNativePhotoLibrary {
  if (!hybridObject) {
    hybridObject = NitroModules.createHybridObject<ReactNativePhotoLibrary>(
      'ReactNativePhotoLibrary'
    );
  }
  return hybridObject;
}

const ERROR_CODES = [
  'E_NO_LIBRARY_PERMISSION',
  'E_NO_IMAGE_DATA_FOUND',
  'E_CANNOT_SAVE_IMAGE',
  'E_PERMISSION_IN_PROGRESS',
  'E_ACTIVITY_DOES_NOT_EXIST',
] as const;

export type PhotoLibraryErrorCode = (typeof ERROR_CODES)[number] | 'E_UNKNOWN';

export class PhotoLibraryError extends Error {
  readonly code: PhotoLibraryErrorCode;

  constructor(code: PhotoLibraryErrorCode, message: string) {
    super(message);
    this.name = 'PhotoLibraryError';
    this.code = code;
  }
}

function toError(error: unknown): PhotoLibraryError {
  if (error instanceof PhotoLibraryError) return error;
  const message = error instanceof Error ? error.message : String(error);
  const match = /\b(E_[A-Z_]+): (.*)/.exec(message);
  const code = ERROR_CODES.find((value) => value === match?.[1]);
  return new PhotoLibraryError(
    code ?? 'E_UNKNOWN',
    code ? match?.[2]?.trim() ?? '' : 'Photo library operation failed'
  );
}

export async function getSavePermission(): Promise<PhotoSavePermission> {
  try {
    return await native().getSavePermission();
  } catch (error) {
    throw toError(error);
  }
}

export async function requestSavePermission(): Promise<PhotoSavePermission> {
  try {
    return await native().requestSavePermission();
  } catch (error) {
    throw toError(error);
  }
}

export async function saveToLibrary(path: string): Promise<void> {
  try {
    await native().saveToLibrary(path);
  } catch (error) {
    throw toError(error);
  }
}

export default { getSavePermission, requestSavePermission, saveToLibrary };
