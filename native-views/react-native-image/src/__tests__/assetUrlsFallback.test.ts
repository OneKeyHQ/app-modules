import { iconResourceHref } from '../icon/assetUrls';

describe('host icon table fallback', () => {
  it('returns no artwork and warns once in development', () => {
    const warn = jest.spyOn(console, 'warn').mockImplementation(() => {});
    expect(iconResourceHref('DotVerSolid')).toBeUndefined();
    expect(iconResourceHref('StarSolid')).toBeUndefined();
    expect(warn).toHaveBeenCalledTimes(1);
    expect(warn.mock.calls[0]?.[0]).toContain('#onekey-icon-assets');
    warn.mockRestore();
  });
});
