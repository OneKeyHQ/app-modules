import fs from 'node:fs';
import path from 'node:path';

describe('NativeSheet iOS dimming', () => {
  const source = fs.readFileSync(
    path.join(__dirname, '../../ios/NativeSheetContainerView.swift'),
    'utf8'
  );

  it('applies a clamped non-default dimAmount to the presentation container', () => {
    expect(source).toContain('self.dimAmountValue = min(max(dimAmount, 0), 1)');
    expect(source).toContain(
      'sheet.largestUndimmedDetentIdentifier = identifier'
    );
    expect(source).toContain(
      'UIColor.black.withAlphaComponent(dimAmountValue)'
    );
    expect(source).toMatch(
      /dismissOnBackdropPress: dismissOnBackdropPress,\s+dimAmount: dimAmount/
    );
  });
});
