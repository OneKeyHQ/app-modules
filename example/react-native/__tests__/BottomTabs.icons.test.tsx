import { Image } from 'react-native';

jest.mock(
  '../../../native-views/react-native-bottom-tabs/src/BottomTabsNativeComponent',
  () => ({ __esModule: true, default: 'NativeBottomTabs' }),
);
jest.mock(
  '../../../native-views/react-native-bottom-tabs/src/BottomAccessoryView',
  () => ({ BottomAccessoryView: 'BottomAccessoryView' }),
);

import { resolveTabIconSource } from '../../../native-views/react-native-bottom-tabs/src/BottomTabs';

describe('tab icon source resolution', () => {
  afterEach(() => jest.restoreAllMocks());

  it.each([
    'https://example.com/icon.svg',
    'http://example.com/icon.png',
    'file:///tmp/icon.png',
    'content://media/icon/1',
    'data:image/png;base64,AA==',
    'asset:/icon.png',
    '//example.com/icon.svg',
    '/icons/icon.svg',
    './icons/icon.svg',
    'icons/icon.svg',
  ])('resolves ordinary URI string %s as an image source', uri => {
    const resolve = jest.spyOn(Image, 'resolveAssetSource');
    const result = resolveTabIconSource(`  ${uri}  `);
    expect(resolve).toHaveBeenCalledWith({ uri });
    expect(result.uri).toBe(uri);
    expect(result.scale).toBe(1);
  });

  it.each(['WalletOutline', 'Wallet4Solid', 'onekey-icon://WalletOutline'])(
    'resolves named string %s without the ordinary image loader',
    name => {
      const resolve = jest.spyOn(Image, 'resolveAssetSource');
      expect(resolveTabIconSource(`  ${name}  `)).toEqual({
        uri: name.startsWith('onekey-icon://') ? name : `onekey-icon://${name}`,
        width: 24,
        height: 24,
        scale: 1,
      });
      expect(resolve).not.toHaveBeenCalled();
    },
  );

  it('preserves explicit named sources and their dimensions', () => {
    expect(resolveTabIconSource({ iconName: ' WalletOutline ' })).toEqual({
      uri: 'onekey-icon://WalletOutline',
      width: 24,
      height: 24,
      scale: 1,
    });
    expect(
      resolveTabIconSource({
        uri: 'onekey-icon://WalletOutline',
        width: 32,
        height: 20,
        scale: 2,
      }),
    ).toEqual({
      uri: 'onekey-icon://WalletOutline',
      width: 32,
      height: 20,
      scale: 2,
    });
  });

  it('keeps URI objects, numeric assets, SF Symbols and empty sources working', () => {
    const source = {
      uri: 'https://example.com/icon.png',
      width: 32,
      height: 20,
      scale: 2,
    };
    expect(resolveTabIconSource(source)).toEqual(source);
    const resolve = jest
      .spyOn(Image, 'resolveAssetSource')
      .mockReturnValue(source);
    expect(resolveTabIconSource(1)).toEqual(source);
    expect(resolve).toHaveBeenCalledWith(1);
    for (const empty of [
      undefined,
      null,
      '',
      '  ',
      { iconName: '  ' },
      { sfSymbol: 'house' },
    ] as const) {
      expect(resolveTabIconSource(empty)).toEqual({
        uri: '',
        width: 0,
        height: 0,
        scale: 1,
      });
    }
  });
});
