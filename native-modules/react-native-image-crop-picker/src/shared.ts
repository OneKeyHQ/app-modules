import type {
  ImageCropPickerOptions,
  PickedImage,
} from './ReactNativeImageCropPicker.nitro';

// Shared by the native and web entry points, so both reject with the same
// error class and accept the same options.

export const ERROR_CODES = [
  'E_PICKER_CANCELLED',
  'E_PICKER_IN_PROGRESS',
  'E_ACTIVITY_DOES_NOT_EXIST',
  'E_FAILED_TO_SHOW_PICKER',
  'E_NO_IMAGE_DATA_FOUND',
  'E_CROPPER_IMAGE_NOT_FOUND',
  'E_CANNOT_SAVE_IMAGE',
  'E_LOW_MEMORY_ERROR',
  'E_ERROR_WHILE_CLEANING_FILES',
] as const;

export type ImageCropPickerErrorCode =
  | (typeof ERROR_CODES)[number]
  | 'E_UNKNOWN';

// Rejections carry the same `code` values as react-native-image-crop-picker,
// e.g. `E_PICKER_CANCELLED` when the user dismisses the picker or cropper.
export class ImageCropPickerError extends Error {
  readonly code: ImageCropPickerErrorCode;

  constructor(code: ImageCropPickerErrorCode, message: string) {
    super(message);
    this.name = 'ImageCropPickerError';
    this.code = code;
  }
}

// Options accepted for source compatibility with react-native-image-crop-picker.
// Only single photos are supported; processed results are JPEG.
export interface Options extends ImageCropPickerOptions {
  mediaType?: 'photo';
  multiple?: false;
  forceJpg?: boolean;
  // The system photo picker controls ordering.
  sortOrder?: 'none' | 'asc' | 'desc';
}

export interface CropperOptions extends Options {
  path: string;
}

export type Image = PickedImage;
