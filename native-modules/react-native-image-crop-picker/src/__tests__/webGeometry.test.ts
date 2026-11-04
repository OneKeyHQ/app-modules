import {
  INITIAL_STATE,
  MAX_OUTPUT_LONG_SIDE,
  MAX_ZOOM,
  constrain,
  coverScale,
  cropAspectRatio,
  cropRectOf,
  fitCropBox,
  outputSizeOf,
  pan,
  rescale,
  rotateCounterclockwise,
  zoomAt,
} from '../web/geometry';

const image = { width: 4000, height: 3000 };

describe('cropAspectRatio', () => {
  it('is 1:1 in circular mode for non-square images and targets', () => {
    for (const rotation of [0, 90] as const) {
      expect(
        cropAspectRatio({
          image,
          rotation,
          targetAspectRatio: 300 / 200,
          circular: true,
        })
      ).toBe(1);
      expect(cropAspectRatio({ image, rotation, circular: true })).toBe(1);
    }
  });

  it('uses the target ratio, else the rotated image ratio', () => {
    expect(
      cropAspectRatio({ image, rotation: 90, targetAspectRatio: 1.5 })
    ).toBe(1.5);
    expect(cropAspectRatio({ image, rotation: 0 })).toBeCloseTo(4 / 3);
    expect(cropAspectRatio({ image, rotation: 90 })).toBeCloseTo(3 / 4);
  });
});

describe('fitCropBox', () => {
  it('fits the ratio inside the area less 20px on every side', () => {
    expect(fitCropBox({ width: 440, height: 840 }, 1)).toEqual({
      width: 400,
      height: 400,
    });
    expect(fitCropBox({ width: 840, height: 440 }, 2)).toEqual({
      width: 800,
      height: 400,
    });
  });

  it('collapses when there is no room', () => {
    expect(fitCropBox({ width: 30, height: 300 }, 1)).toEqual({
      width: 0,
      height: 0,
    });
  });
});

describe('crop state', () => {
  const box = { width: 300, height: 300 };

  it('covers the crop box at zoom 1', () => {
    expect(coverScale(image, box, 0)).toBeCloseTo(0.1);
    const rect = cropRectOf(INITIAL_STATE, image, box);
    expect(rect).toEqual({ x: 500, y: 0, width: 3000, height: 3000 });
  });

  it('never lets the crop box leave the image', () => {
    const moved = pan(INITIAL_STATE, 10_000, 10_000, image, box);
    // 4000 * 0.1 = 400 wide, 100px of slack, 50px each way.
    expect(moved.offsetX).toBe(50);
    expect(moved.offsetY).toBe(0);
    expect(cropRectOf(moved, image, box).x).toBe(0);
  });

  it('keeps the anchor point still while zooming', () => {
    const anchor = { x: 100, y: 50 };
    const zoomed = zoomAt(INITIAL_STATE, 2, anchor, image, box);
    expect(zoomed.zoom).toBe(2);
    // The image point under the anchor: (anchor - offset) / zoom.
    expect((anchor.x - zoomed.offsetX) / 2).toBeCloseTo(anchor.x);
    expect((anchor.y - zoomed.offsetY) / 2).toBeCloseTo(anchor.y);
  });

  it('clamps zoom to its range', () => {
    expect(constrain({ ...INITIAL_STATE, zoom: 99 }, image, box).zoom).toBe(
      MAX_ZOOM
    );
    expect(constrain({ ...INITIAL_STATE, zoom: 0.1 }, image, box).zoom).toBe(1);
  });

  it('rotates counterclockwise around the box center', () => {
    const zoomed = { ...INITIAL_STATE, zoom: 2, offsetX: 40, offsetY: 20 };
    const rotated = rotateCounterclockwise(zoomed, image, box);
    expect(rotated.rotation).toBe(90);
    expect(rotated.offsetX).toBe(20);
    expect(rotated.offsetY).toBe(-40);
    let state = rotated;
    for (let i = 0; i < 3; i += 1) {
      state = rotateCounterclockwise(state, image, box);
    }
    expect(state.rotation).toBe(0);
  });

  it('reports cropRect in the rotated image', () => {
    const portraitBox = { width: 300, height: 400 };
    const rotated = rotateCounterclockwise(INITIAL_STATE, image, portraitBox);
    expect(cropRectOf(rotated, image, portraitBox)).toEqual({
      x: 0,
      y: 0,
      width: 3000,
      height: 4000,
    });
  });

  it('keeps the same crop when the box is resized', () => {
    const state = { ...INITIAL_STATE, zoom: 3, offsetX: 120, offsetY: -60 };
    const before = cropRectOf(state, image, box);
    const bigger = { width: 450, height: 450 };
    const after = cropRectOf(rescale(state, box, bigger, image), image, bigger);
    expect(after).toEqual(before);
  });
});

describe('outputSizeOf', () => {
  it('keeps a small image crop at high zoom at least one output pixel per side', () => {
    const smallImage = { width: 2, height: 2 };
    const box = { width: 300, height: 300 };
    const state = constrain(
      { ...INITIAL_STATE, zoom: MAX_ZOOM },
      smallImage,
      box
    );
    const crop = cropRectOf(state, smallImage, box);

    expect(outputSizeOf(crop, {})).toEqual({ width: 1, height: 1 });
    expect(outputSizeOf(crop, { maxWidth: 100, maxHeight: 100 })).toEqual({
      width: 1,
      height: 1,
    });
  });

  it.each([
    { width: 16384, height: 1 },
    { width: 1, height: 16384 },
  ])('keeps the short side above zero when capping %o', (crop) => {
    expect(outputSizeOf(crop, {})).toEqual({
      width: crop.width > crop.height ? MAX_OUTPUT_LONG_SIDE : 1,
      height: crop.height > crop.width ? MAX_OUTPUT_LONG_SIDE : 1,
    });
  });

  it('produces the target size exactly, scaling small crops up', () => {
    expect(
      outputSizeOf(
        { width: 120, height: 79 },
        { targetSize: { width: 480, height: 320 } }
      )
    ).toEqual({ width: 480, height: 320 });
  });

  it('keeps the crop pixels without a target, capped on the long side', () => {
    expect(outputSizeOf({ width: 300, height: 200 }, {})).toEqual({
      width: 300,
      height: 200,
    });
    expect(outputSizeOf({ width: 8192, height: 4096 }, {})).toEqual({
      width: MAX_OUTPUT_LONG_SIDE,
      height: 2048,
    });
  });

  it('applies compressImageMaxWidth / Height after cropping', () => {
    expect(
      outputSizeOf(
        { width: 1000, height: 1000 },
        { targetSize: { width: 800, height: 400 }, maxWidth: 400 }
      )
    ).toEqual({ width: 400, height: 200 });
  });
});
