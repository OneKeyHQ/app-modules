import { resolveContentSize } from '../contentSize';

it('follows the window size by default', () => {
  expect(resolveContentSize({ windowWidth: 360, windowHeight: 640 })).toEqual({
    width: 360,
    height: 640,
  });
});

it('keeps a caller size while the window has not rotated yet', () => {
  // Prepared for the landscape window the portrait one is rotating into.
  expect(
    resolveContentSize({
      windowWidth: 360,
      windowHeight: 640,
      contentSize: { width: 640, height: 360 },
    })
  ).toEqual({ width: 640, height: 360 });
});

it('keeps a caller size after the window size reaches JS', () => {
  expect(
    resolveContentSize({
      windowWidth: 640,
      windowHeight: 360,
      contentSize: { width: 640, height: 360 },
    })
  ).toEqual({ width: 640, height: 360 });
});
