import { getAnchoredTransformOrigin } from '../animation/anchoredOrigin';

describe('getAnchoredTransformOrigin', () => {
  it.each([
    ['bottom-end', 'right top'],
    ['bottom-start', 'left top'],
    ['bottom', 'center top'],
    ['top-end', 'right bottom'],
    ['top-start', 'left bottom'],
    ['top', 'center bottom'],
    ['left-start', 'right top'],
    ['left-end', 'right bottom'],
    ['left', 'right center'],
    ['right-start', 'left top'],
    ['right-end', 'left bottom'],
    ['right', 'left center'],
  ])('scales a %s panel from %s', (placement, origin) => {
    expect(getAnchoredTransformOrigin(placement)).toBe(origin);
  });

  it('returns undefined without a known placement', () => {
    expect(getAnchoredTransformOrigin(undefined)).toBeUndefined();
    expect(getAnchoredTransformOrigin('')).toBeUndefined();
    expect(getAnchoredTransformOrigin('middle')).toBeUndefined();
  });
});
