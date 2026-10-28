import { resolveSheetWidth } from '../sheetWidth';

it('fills the window until native reports a width', () => {
  expect(resolveSheetWidth({ windowWidth: 951 })).toEqual({
    containerWidth: 951,
    contentWidth: 951,
    inset: 0,
  });
});

it('follows the narrower width native gave the sheet', () => {
  expect(resolveSheetWidth({ windowWidth: 951, reportedWidth: 652 })).toEqual({
    containerWidth: 652,
    contentWidth: 652,
    inset: 0,
  });
});

it('never lays out wider than the window', () => {
  expect(
    resolveSheetWidth({ windowWidth: 466, reportedWidth: 669 }).containerWidth
  ).toBe(466);
});

it('centers content capped by maxWidth', () => {
  expect(
    resolveSheetWidth({ windowWidth: 951, reportedWidth: 951, maxWidth: 640 })
  ).toEqual({ containerWidth: 951, contentWidth: 640, inset: 155.5 });
});

it('ignores a maxWidth wider than the sheet', () => {
  expect(
    resolveSheetWidth({ windowWidth: 951, reportedWidth: 652, maxWidth: 640 })
      .inset
  ).toBe(6);
});
