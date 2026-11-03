// Crop math for the web cropper. Pure functions, no DOM.
//
// Coordinates are CSS pixels relative to the center of the crop area. The
// crop box is fixed and centered; the image moves under it. `offsetX/Y` is the
// image center, `zoom` multiplies the scale at which the image just covers the
// crop box, and `rotation` counts quarter turns counterclockwise, like the
// native rotate button.

export interface Size {
  width: number;
  height: number;
}

export interface Point {
  x: number;
  y: number;
}

export type Rotation = 0 | 90 | 180 | 270;

export interface CropState {
  rotation: Rotation;
  zoom: number;
  offsetX: number;
  offsetY: number;
}

export interface Rect {
  x: number;
  y: number;
  width: number;
  height: number;
}

export const MIN_ZOOM = 1;
export const MAX_ZOOM = 8;
// Matches the native crop view padding.
export const CROP_BOX_PADDING = 20;
// Processed images are capped like native decoding.
export const MAX_OUTPUT_LONG_SIDE = 4096;

export const INITIAL_STATE: CropState = {
  rotation: 0,
  zoom: MIN_ZOOM,
  offsetX: 0,
  offsetY: 0,
};

export function clamp(value: number, min: number, max: number): number {
  return Math.min(Math.max(value, min), max);
}

export function rotatedSize(image: Size, rotation: Rotation): Size {
  return rotation % 180 === 0
    ? image
    : { width: image.height, height: image.width };
}

// The largest box of `aspectRatio` (width / height) inside `area`, less the
// crop box padding on every side.
export function fitCropBox(area: Size, aspectRatio: number): Size {
  const width = Math.max(area.width - CROP_BOX_PADDING * 2, 0);
  const height = Math.max(area.height - CROP_BOX_PADDING * 2, 0);
  if (width === 0 || height === 0 || !(aspectRatio > 0)) {
    return { width: 0, height: 0 };
  }
  return width / height > aspectRatio
    ? { width: height * aspectRatio, height }
    : { width, height: width / aspectRatio };
}

// Screen pixels per image pixel at zoom 1: the image just covers the box.
export function coverScale(image: Size, box: Size, rotation: Rotation): number {
  const size = rotatedSize(image, rotation);
  if (size.width === 0 || size.height === 0) {
    return 1;
  }
  return Math.max(box.width / size.width, box.height / size.height);
}

export function scaleOf(state: CropState, image: Size, box: Size): number {
  return coverScale(image, box, state.rotation) * state.zoom;
}

// Keeps the zoom in range and the crop box inside the image.
export function constrain(state: CropState, image: Size, box: Size): CropState {
  const zoom = clamp(state.zoom, MIN_ZOOM, MAX_ZOOM);
  const scale = coverScale(image, box, state.rotation) * zoom;
  const size = rotatedSize(image, state.rotation);
  const maxX = Math.max((size.width * scale - box.width) / 2, 0);
  const maxY = Math.max((size.height * scale - box.height) / 2, 0);
  return {
    rotation: state.rotation,
    zoom,
    offsetX: clamp(state.offsetX, -maxX, maxX),
    offsetY: clamp(state.offsetY, -maxY, maxY),
  };
}

export function pan(
  state: CropState,
  dx: number,
  dy: number,
  image: Size,
  box: Size
): CropState {
  return constrain(
    { ...state, offsetX: state.offsetX + dx, offsetY: state.offsetY + dy },
    image,
    box
  );
}

// Zooms so the image point under `anchor` stays under it.
export function zoomAt(
  state: CropState,
  zoom: number,
  anchor: Point,
  image: Size,
  box: Size
): CropState {
  const nextZoom = clamp(zoom, MIN_ZOOM, MAX_ZOOM);
  const ratio = nextZoom / state.zoom;
  return constrain(
    {
      rotation: state.rotation,
      zoom: nextZoom,
      offsetX: anchor.x - (anchor.x - state.offsetX) * ratio,
      offsetY: anchor.y - (anchor.y - state.offsetY) * ratio,
    },
    image,
    box
  );
}

// Turns the image 90° counterclockwise around the crop box center. `box` is
// the crop box after the turn, which differs only when the aspect ratio is
// not locked.
export function rotateCounterclockwise(
  state: CropState,
  image: Size,
  box: Size
): CropState {
  // A counterclockwise quarter turn on a y-down screen maps (x, y) to (y, -x).
  return constrain(
    {
      rotation: ((state.rotation + 90) % 360) as Rotation,
      zoom: state.zoom,
      offsetX: state.offsetY,
      offsetY: -state.offsetX,
    },
    image,
    box
  );
}

// Keeps the same part of the image in the crop box when the box is resized.
export function rescale(
  state: CropState,
  previousBox: Size,
  box: Size,
  image: Size
): CropState {
  if (previousBox.width === 0) {
    return constrain(state, image, box);
  }
  const ratio = box.width / previousBox.width;
  return constrain(
    {
      ...state,
      offsetX: state.offsetX * ratio,
      offsetY: state.offsetY * ratio,
    },
    image,
    box
  );
}

// The crop box in the rotated image's pixels, like the native `cropRect`.
export function cropRectOf(state: CropState, image: Size, box: Size): Rect {
  const scale = scaleOf(state, image, box);
  const size = rotatedSize(image, state.rotation);
  return {
    x: Math.round(size.width / 2 + (-box.width / 2 - state.offsetX) / scale),
    y: Math.round(size.height / 2 + (-box.height / 2 - state.offsetY) / scale),
    width: Math.round(box.width / scale),
    height: Math.round(box.height / scale),
  };
}

export interface OutputLimits {
  targetSize?: Size;
  maxWidth?: number;
  maxHeight?: number;
}

// Native semantics: a target size is produced exactly, smaller crops are
// scaled up; without one the crop keeps its own pixels, capped at
// MAX_OUTPUT_LONG_SIDE. compressImageMaxWidth/Height then only shrink.
export function outputSizeOf(crop: Size, limits: OutputLimits): Size {
  let width: number;
  let height: number;
  if (limits.targetSize) {
    width = Math.round(limits.targetSize.width);
    height = Math.round(limits.targetSize.height);
  } else {
    const fit = Math.min(
      1,
      MAX_OUTPUT_LONG_SIDE / Math.max(crop.width, crop.height, 1)
    );
    width = Math.round(crop.width * fit);
    height = Math.round(crop.height * fit);
  }
  return shrinkToFit({ width, height }, limits.maxWidth, limits.maxHeight);
}

export function shrinkToFit(
  size: Size,
  maxWidth?: number,
  maxHeight?: number
): Size {
  const scale = Math.min(
    maxWidth !== undefined && maxWidth > 0 ? maxWidth / size.width : 1,
    maxHeight !== undefined && maxHeight > 0 ? maxHeight / size.height : 1,
    1
  );
  if (scale >= 1) {
    return size;
  }
  return {
    width: Math.max(Math.floor(size.width * scale), 1),
    height: Math.max(Math.floor(size.height * scale), 1),
  };
}

export function isSameState(a: CropState, b: CropState): boolean {
  const epsilon = 0.5;
  return (
    a.rotation === b.rotation &&
    Math.abs(a.zoom - b.zoom) < 0.001 &&
    Math.abs(a.offsetX - b.offsetX) < epsilon &&
    Math.abs(a.offsetY - b.offsetY) < epsilon
  );
}
