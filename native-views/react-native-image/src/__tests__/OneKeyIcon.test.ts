import { isIconName, normalizeIconColor } from '../icon/props';

describe('named icon boundary', () => {
  it('normalizes supported colors including alpha without exponent output', () => {
    expect(normalizeIconColor()).toBe('rgba(0,0,0,1)');
    expect(normalizeIconColor('#f08')).toBe('rgba(255,0,136,1)');
    expect(normalizeIconColor('#f008')).toBe(
      'rgba(255,0,0,0.5333333333333333)'
    );
    expect(normalizeIconColor('#ff000080')).toBe(
      'rgba(255,0,0,0.5019607843137255)'
    );
    expect(normalizeIconColor('rgba(255,0,0,0.0000001)')).toBe(
      'rgba(255,0,0,0)'
    );
    expect(normalizeIconColor('rgb(0, 128, 255)')).toBe('rgba(0,128,255,1)');
    for (const color of [
      'red',
      '$text',
      '#12',
      'rgba(1,2,3,2)',
      'rgb(256,0,0)',
      'rgba(0,0,0,NaN)',
      'rgb(1,2,3,0.5)',
      'rgba(1,2,3)',
      'rgb(10%,0%,0%)',
    ]) {
      expect(normalizeIconColor(color)).toBeUndefined();
    }
  });

  it('accepts only a bundled name identifier, with unknown identifiers left to resource lookup', () => {
    expect(isIconName('DotVerSolid')).toBe(true);
    expect(isIconName('MissingIcon')).toBe(true);
    for (const name of [
      '',
      '../BotIllus',
      'https://example.com/icon.svg',
      'Icon#other',
    ]) {
      expect(isIconName(name)).toBe(false);
    }
  });
});
