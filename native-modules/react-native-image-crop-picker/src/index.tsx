import { NitroModules } from 'react-native-nitro-modules';

import type { ReactNativeImageCropPicker as ReactNativeImageCropPickerSpec } from './ReactNativeImageCropPicker.nitro';

import {
  ERROR_CODES,
  ImageCropPickerError,
  type CropperOptions,
  type Image,
  type Options,
} from './shared';

export type * from './ReactNativeImageCropPicker.nitro';
export { ImageCropPickerError } from './shared';
export type {
  CropperOptions,
  Image,
  ImageCropPickerErrorCode,
  Options,
} from './shared';

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
