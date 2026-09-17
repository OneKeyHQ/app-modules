import type { HybridObject } from 'react-native-nitro-modules';

export interface CropRect {
  x: number;
  y: number;
  width: number;
  height: number;
}

export interface PickedImage {
  // file:// URI of the processed image inside the module's temporary directory.
  path: string;
  size: number;
  width: number;
  height: number;
  mime: string;
  // Base64 encoded image data (without a data: prefix) when `includeBase64` is set.
  data?: string;
  cropRect?: CropRect;
  filename?: string;
}

export interface ImageCropPickerOptions {
  // Target size of the cropped image. Also defines the crop aspect ratio.
  width?: number;
  height?: number;
  cropping?: boolean;
  includeBase64?: boolean;
  compressImageQuality?: number;
  compressImageMaxWidth?: number;
  compressImageMaxHeight?: number;
  freeStyleCropEnabled?: boolean;
  cropperCircleOverlay?: boolean;
  cropperToolbarTitle?: string;
  cropperChooseText?: string;
  cropperCancelText?: string;
  // iOS only
  cropperChooseColor?: string;
  cropperCancelColor?: string;
  cropperRotateButtonsHidden?: boolean;
  // Android only
  cropperActiveWidgetColor?: string;
  cropperToolbarColor?: string;
  cropperToolbarWidgetColor?: string;
  cropperStatusBarLight?: boolean;
  cropperNavigationBarLight?: boolean;
  showCropGuidelines?: boolean;
  showCropFrame?: boolean;
  enableRotationGesture?: boolean;
  hideBottomControls?: boolean;
  disableCropperColorSetters?: boolean;
}

export interface ReactNativeImageCropPicker
  extends HybridObject<{ ios: 'swift'; android: 'kotlin' }> {
  // Pick a single photo from the system photo picker, optionally cropping it.
  // Neither platform asks for photo library permission.
  openPicker(options: ImageCropPickerOptions): Promise<PickedImage>;

  // Crop an existing image. `path` accepts file:// URIs, absolute paths,
  // http(s):// URLs, data: URIs and, on Android, content:// URIs.
  openCropper(
    path: string,
    options: ImageCropPickerOptions
  ): Promise<PickedImage>;

  // Delete every file this module wrote to its temporary directory.
  clean(): Promise<void>;

  // Delete a single file returned by this module.
  cleanSingle(path: string): Promise<void>;
}
