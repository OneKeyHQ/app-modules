import React from 'react';
import { Image, Platform, View } from 'react-native';
import ReactTestRenderer, { act } from 'react-test-renderer';

jest.mock(
  '../../../native-views/react-native-bottom-tabs/src/BottomTabsNativeComponent',
  () => ({ __esModule: true, default: 'NativeBottomTabs' }),
);
jest.mock(
  '../../../native-views/react-native-bottom-tabs/src/BottomAccessoryView',
  () => ({ BottomAccessoryView: 'BottomAccessoryView' }),
);

import BottomTabs, {
  resolveTabIconSource,
} from '../../../native-views/react-native-bottom-tabs/src/BottomTabs';
import type { BaseRoute } from '../../../native-views/react-native-bottom-tabs/src/types';

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

describe.each(['ios', 'android'] as const)(
  '%s tab icon and items array identity',
  platformOS => {
    const originalOS = Platform.OS;
    let renderer: ReactTestRenderer.ReactTestRenderer | undefined;
    const routes: BaseRoute[] = [
      {
        key: 'home',
        unfocusedIcon: 'WalletOutline',
        focusedIcon: 'Wallet4Solid',
      },
      { key: 'search', focusedIcon: 'PlusLargeOutline' },
    ];
    const renderScene = () => <View />;
    const onIndexChange = () => {};
    const tabs = (
      index = 0,
      labeled = true,
      nextRoutes = routes,
      getIcon?: React.ComponentProps<typeof BottomTabs>['getIcon'],
    ) => (
      <BottomTabs
        navigationState={{ index, routes: nextRoutes }}
        renderScene={renderScene}
        onIndexChange={onIndexChange}
        labeled={labeled}
        getIcon={getIcon}
      />
    );
    const nativeProps = () =>
      renderer!.root.findByType('NativeBottomTabs' as any).props;

    beforeEach(() => {
      Object.defineProperty(Platform, 'OS', {
        configurable: true,
        value: platformOS,
      });
    });

    afterEach(async () => {
      await act(() => renderer?.unmount());
      renderer = undefined;
      Object.defineProperty(Platform, 'OS', {
        configurable: true,
        value: originalOS,
      });
    });

    it('retains default icon and items arrays on selection and unrelated renders', async () => {
      await act(() => {
        renderer = ReactTestRenderer.create(tabs());
      });
      const { icons, selectedIcons, items } = nativeProps();
      expect(icons.map((icon: { uri: string }) => icon.uri)).toEqual([
        'onekey-icon://WalletOutline',
        'onekey-icon://PlusLargeOutline',
      ]);
      expect(selectedIcons.map((icon: { uri: string }) => icon.uri)).toEqual([
        'onekey-icon://Wallet4Solid',
        'onekey-icon://PlusLargeOutline',
      ]);
      await act(() => {
        renderer!.update(tabs(1));
      });
      expect(nativeProps().selectedPage).toBe('search');
      expect(nativeProps().icons).toBe(icons);
      expect(nativeProps().selectedIcons).toBe(selectedIcons);
      expect(nativeProps().items).toBe(items);
      await act(() => {
        renderer!.update(tabs(1, false));
      });
      expect(nativeProps().labeled).toBe(false);
      expect(nativeProps().icons).toBe(icons);
      expect(nativeProps().selectedIcons).toBe(selectedIcons);
      expect(nativeProps().items).toBe(items);
    });

    it('refreshes default arrays when routes change', async () => {
      await act(() => {
        renderer = ReactTestRenderer.create(tabs());
      });
      const { icons, selectedIcons } = nativeProps();
      const changed = [
        {
          ...routes[0],
          unfocusedIcon: 'CoinsOutline',
          focusedIcon: 'AppleBrand',
        },
        routes[1],
      ];
      await act(() => {
        renderer!.update(tabs(0, true, changed));
      });
      expect(nativeProps().icons).not.toBe(icons);
      expect(nativeProps().selectedIcons).not.toBe(selectedIcons);
      expect(nativeProps().icons[0].uri).toBe('onekey-icon://CoinsOutline');
      expect(nativeProps().selectedIcons[0].uri).toBe(
        'onekey-icon://AppleBrand',
      );
    });

    it('retains custom resolver arrays until the resolver changes', async () => {
      const first = ({ focused }: { focused: boolean }) =>
        focused ? 'AppleBrand' : 'CoinsOutline';
      const second = ({ focused }: { focused: boolean }) =>
        focused ? 'Wallet4Solid' : 'WalletOutline';
      await act(() => {
        renderer = ReactTestRenderer.create(tabs(0, true, routes, first));
      });
      const { icons, selectedIcons } = nativeProps();
      await act(() => {
        renderer!.update(tabs(1, false, routes, first));
      });
      expect(nativeProps().icons).toBe(icons);
      expect(nativeProps().selectedIcons).toBe(selectedIcons);
      await act(() => {
        renderer!.update(tabs(1, false, routes, second));
      });
      expect(nativeProps().icons).not.toBe(icons);
      expect(nativeProps().selectedIcons).not.toBe(selectedIcons);
      expect(nativeProps().icons[0].uri).toBe('onekey-icon://WalletOutline');
      expect(nativeProps().selectedIcons[0].uri).toBe(
        'onekey-icon://Wallet4Solid',
      );
    });
  },
);
