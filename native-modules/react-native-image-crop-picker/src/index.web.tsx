/// <reference lib="dom" />
import { ImageCropPickerError } from './shared';
import { cropRectOf, outputSizeOf, shrinkToFit } from './web/geometry';
import {
  MAX_INPUT_BYTES,
  RESULT_MIME,
  blobToBase64,
  createResultUrl,
  imageSizeOf,
  loadImage,
  normalizeQuality,
  releaseAllResultUrls,
  releaseResultUrl,
  renderCrop,
  renderWhole,
} from './web/imageIO';
import { presentCropper } from './web/CropperView';

import type { ReactNativeImageCropPicker as ReactNativeImageCropPickerSpec } from './ReactNativeImageCropPicker.nitro';
import type { CropperOptions, Image, Options } from './shared';
import type { Size } from './web/geometry';

export type * from './ReactNativeImageCropPicker.nitro';
export { ImageCropPickerError } from './shared';
export type {
  CropperOptions,
  Image,
  ImageCropPickerErrorCode,
  Options,
} from './shared';

// Web entry: the same API and results as the native module. The picker is a
// file input, the cropper is drawn with the DOM (web/CropperView.ts), and
// result paths are blob: URLs that clean/cleanSingle revoke.

export function getReactNativeImageCropPicker(): ReactNativeImageCropPickerSpec {
  throw new ImageCropPickerError(
    'E_UNKNOWN',
    'The Nitro hybrid object is not available on the web'
  );
}

// One picker or cropper at a time, like native.
let busy = false;

async function exclusive<T>(task: () => Promise<T>): Promise<T> {
  if (busy) {
    throw new ImageCropPickerError(
      'E_PICKER_IN_PROGRESS',
      'Another picker or cropper is still open'
    );
  }
  busy = true;
  try {
    return await task();
  } finally {
    busy = false;
  }
}

function assertDocument(): void {
  if (typeof document === 'undefined' || !document.body) {
    throw new ImageCropPickerError(
      'E_FAILED_TO_SHOW_PICKER',
      'No document to show the picker in'
    );
  }
}

function cancelledError(): ImageCropPickerError {
  return new ImageCropPickerError(
    'E_PICKER_CANCELLED',
    'User cancelled image selection'
  );
}

function targetSizeOf(options: Options): Size | undefined {
  const { width, height } = options;
  if (width && height && width > 0 && height > 0) {
    return { width, height };
  }
  return undefined;
}

function chooseFile(): Promise<File> {
  return new Promise((resolve, reject) => {
    const input = document.createElement('input');
    input.type = 'file';
    input.accept = 'image/*';
    input.style.display = 'none';
    let settled = false;
    const finish = (file: File | undefined) => {
      if (settled) {
        return;
      }
      settled = true;
      window.removeEventListener('focus', onWindowFocus);
      input.remove();
      if (file) {
        resolve(file);
      } else {
        reject(cancelledError());
      }
    };
    // Browsers without the input `cancel` event only report a dismissed
    // chooser by giving focus back; `change` arrives first when a file was
    // picked.
    const onWindowFocus = () => {
      setTimeout(() => finish(input.files?.[0]), 1000);
    };
    input.addEventListener('change', () => finish(input.files?.[0]));
    input.addEventListener('cancel', () => finish(undefined));
    if (!('oncancel' in input)) {
      window.addEventListener('focus', onWindowFocus);
    }
    document.body.appendChild(input);
    input.click();
  });
}

async function decodeFile(file: File): Promise<HTMLImageElement> {
  if (file.size > MAX_INPUT_BYTES) {
    throw new ImageCropPickerError(
      'E_NO_IMAGE_DATA_FOUND',
      'The selected image is larger than 64 MiB'
    );
  }
  const url = URL.createObjectURL(file);
  try {
    return await loadImage(url);
  } catch {
    URL.revokeObjectURL(url);
    throw new ImageCropPickerError(
      'E_NO_IMAGE_DATA_FOUND',
      'The selected file is not a readable image'
    );
  }
}

async function makeResult(
  blob: Blob,
  size: Size,
  options: Options,
  extra: Pick<Image, 'cropRect' | 'filename'>
): Promise<Image> {
  return {
    path: createResultUrl(blob),
    size: blob.size,
    width: size.width,
    height: size.height,
    mime: blob.type || RESULT_MIME,
    data: options.includeBase64 ? await blobToBase64(blob) : undefined,
    ...extra,
  };
}

function crop(
  image: HTMLImageElement,
  options: Options,
  filename: string | undefined
): Promise<Image> {
  const targetSize = targetSizeOf(options);
  const quality = normalizeQuality(options.compressImageQuality);
  return presentCropper(
    {
      image,
      aspectRatio: targetSize
        ? targetSize.width / targetSize.height
        : undefined,
      title: options.cropperToolbarTitle,
      cancelText: options.cropperCancelText,
      confirmText: options.cropperChooseText,
      rotateButtonHidden: options.cropperRotateButtonsHidden,
      showGuidelines: options.showCropGuidelines,
      circular: options.cropperCircleOverlay,
      appearance: options.cropperAppearance,
    },
    async (state, box) => {
      const cropRect = cropRectOf(state, imageSizeOf(image), box);
      const output = outputSizeOf(cropRect, {
        targetSize,
        maxWidth: options.compressImageMaxWidth,
        maxHeight: options.compressImageMaxHeight,
      });
      const blob = await renderCrop(image, state, box, output, quality);
      return makeResult(blob, output, options, { cropRect, filename });
    }
  );
}

async function pickWithoutCropping(
  file: File,
  image: HTMLImageElement,
  options: Options
): Promise<Image> {
  const size = imageSizeOf(image);
  if (options.preserveOriginal) {
    // The selected file as is; compression options are ignored.
    return makeResult(file, size, options, { filename: file.name });
  }
  const output = shrinkToFit(
    outputSizeOf(size, {}),
    options.compressImageMaxWidth,
    options.compressImageMaxHeight
  );
  const blob = await renderWhole(
    image,
    output,
    normalizeQuality(options.compressImageQuality)
  );
  return makeResult(blob, output, options, { filename: file.name });
}

export function openPicker(options: Options = {}): Promise<Image> {
  return exclusive(async () => {
    assertDocument();
    const file = await chooseFile();
    const image = await decodeFile(file);
    try {
      return options.cropping
        ? await crop(image, options, file.name)
        : await pickWithoutCropping(file, image, options);
    } finally {
      URL.revokeObjectURL(image.src);
    }
  });
}

export function openCropper({
  path,
  ...options
}: CropperOptions): Promise<Image> {
  return exclusive(async () => {
    assertDocument();
    if (!path) {
      throw new ImageCropPickerError(
        'E_CROPPER_IMAGE_NOT_FOUND',
        'No image path to crop'
      );
    }
    let image: HTMLImageElement;
    try {
      image = await loadImage(path);
    } catch {
      throw new ImageCropPickerError(
        'E_CROPPER_IMAGE_NOT_FOUND',
        'Cannot load the image to crop'
      );
    }
    return crop(image, options, undefined);
  });
}

export async function clean(): Promise<void> {
  releaseAllResultUrls();
}

export async function cleanSingle(path: string): Promise<void> {
  releaseResultUrl(path);
}

const ImageCropPicker = {
  openPicker,
  openCropper,
  clean,
  cleanSingle,
};

export default ImageCropPicker;
