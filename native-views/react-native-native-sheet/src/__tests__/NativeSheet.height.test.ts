import {
  resolveNativeSheetHeight,
  resolveNativeSheetLayoutConstraints,
} from '../NativeSheetHeight';

describe('NativeSheet height updates', () => {
  it('grows and shrinks from new intrinsic measurements while auto-sized', () => {
    expect(
      resolveNativeSheetHeight({
        currentHeight: 160,
        measuredHeight: 240,
        maxHeight: 600,
        shouldAutoMeasure: true,
      })
    ).toBe(240);
    expect(
      resolveNativeSheetHeight({
        currentHeight: 240,
        measuredHeight: 120,
        maxHeight: 600,
        shouldAutoMeasure: true,
      })
    ).toBe(120);
  });

  it('keeps an explicit height stable while a new measurement is pending', () => {
    expect(
      resolveNativeSheetHeight({
        currentHeight: 160,
        maxHeight: 600,
        shouldAutoMeasure: false,
      })
    ).toBe(160);
    expect(
      resolveNativeSheetHeight({
        currentHeight: 160,
        explicitHeight: 240,
        maxHeight: 200,
        shouldAutoMeasure: false,
      })
    ).toBe(200);
  });

  it('keeps auto-sized content intrinsically measurable after presentation', () => {
    expect(
      resolveNativeSheetLayoutConstraints({
        lockedHeight: 220,
        maxHeight: 600,
        shouldAutoMeasure: true,
      })
    ).toEqual({
      hostMaxHeight: 600,
      shouldFillHost: false,
    });
  });

  it('fills a fixed host only when height is explicit', () => {
    expect(
      resolveNativeSheetLayoutConstraints({
        lockedHeight: 220,
        maxHeight: 600,
        shouldAutoMeasure: false,
      })
    ).toEqual({
      hostHeight: 220,
      shouldFillHost: true,
    });
  });
});
