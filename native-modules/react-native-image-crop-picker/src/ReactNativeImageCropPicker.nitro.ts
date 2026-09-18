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

export type ImageCropperColorScheme = 'light' | 'dark';

// Colors and fonts of the cropper screen, which is identical on iOS and
// Android. Colors are CSS hex strings (#RGB, #RRGGBB or #RRGGBBAA). Anything
// left out falls back to OneKey's own palette for `colorScheme`.
export interface ImageCropperAppearance {
  // Defaults to the system appearance.
  colorScheme?: ImageCropperColorScheme;
  // Page background. The area outside the crop box is this color, 70% opaque.
  backgroundColor?: string;
  // Title, and the crop box border.
  titleColor?: string;
  // The rotate button.
  iconColor?: string;
  cancelButtonColor?: string;
  cancelButtonPressedColor?: string;
  cancelButtonTextColor?: string;
  confirmButtonColor?: string;
  confirmButtonPressedColor?: string;
  confirmButtonTextColor?: string;
  titleFontFamily?: string;
  buttonFontFamily?: string;
  // Multiplies every size and spacing, for apps that scale their UI.
  scale?: number;
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
  // Lets the user resize the crop box to any aspect ratio.
  freeStyleCropEnabled?: boolean;
  cropperCircleOverlay?: boolean;
  cropperToolbarTitle?: string;
  cropperChooseText?: string;
  cropperCancelText?: string;
  cropperRotateButtonsHidden?: boolean;
  // Shows the rule-of-thirds grid inside the crop box.
  showCropGuidelines?: boolean;
  cropperAppearance?: ImageCropperAppearance;
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
