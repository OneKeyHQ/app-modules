import { NitroModules } from 'react-native-nitro-modules';

import type {
  ImageCropPickerOptions,
  PickedImage,
  ReactNativeImageCropPicker as ReactNativeImageCropPickerSpec,
} from './ReactNativeImageCropPicker.nitro';

export type * from './ReactNativeImageCropPicker.nitro';

// Created on first use so importing this module costs nothing at startup.
let hybridObject: ReactNativeImageCropPickerSpec | undefined;

export function getReactNativeImageCropPicker(): ReactNativeImageCropPickerSpec {
  if (!hybridObject) {
    hybridObject =
      NitroModules.createHybridObject<ReactNativeImageCropPickerSpec>(
        'ReactNativeImageCropPicker'
      );
  }
  return hybridObject;
}

const ERROR_CODES = [
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

function isErrorCode(value: string): value is (typeof ERROR_CODES)[number] {
  return (ERROR_CODES as readonly string[]).includes(value);
}

// Native errors reach JS as "<code>: <message>". Android prefixes the message
// with the exception class name and appends the Java stack trace, so only the
// first line is kept.
const NATIVE_ERROR_PATTERN = /\b(E_[A-Z_]+): (.*)/;

function toImageCropPickerError(error: unknown): ImageCropPickerError {
  if (error instanceof ImageCropPickerError) {
    return error;
  }
  const message = error instanceof Error ? error.message : String(error);
  const match = NATIVE_ERROR_PATTERN.exec(message);
  if (match?.[1] && isErrorCode(match[1])) {
    return new ImageCropPickerError(match[1], match[2]?.trim() ?? '');
  }
  return new ImageCropPickerError(
    'E_UNKNOWN',
    message.split('\n')[0] ?? message
  );
}

// Options accepted for source compatibility with react-native-image-crop-picker.
// Only single photos are supported, and results are always JPEG.
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

export async function openPicker(options: Options = {}): Promise<Image> {
  try {
    return await getReactNativeImageCropPicker().openPicker(options);
  } catch (error) {
    throw toImageCropPickerError(error);
  }
}

export async function openCropper({
  path,
  ...options
}: CropperOptions): Promise<Image> {
  try {
    return await getReactNativeImageCropPicker().openCropper(path, options);
  } catch (error) {
    throw toImageCropPickerError(error);
  }
}

export async function clean(): Promise<void> {
  try {
    await getReactNativeImageCropPicker().clean();
  } catch (error) {
    throw toImageCropPickerError(error);
  }
}

export async function cleanSingle(path: string): Promise<void> {
  try {
    await getReactNativeImageCropPicker().cleanSingle(path);
  } catch (error) {
    throw toImageCropPickerError(error);
  }
}

const ImageCropPicker = {
  openPicker,
  openCropper,
  clean,
  cleanSingle,
};

export default ImageCropPicker;
