import React from 'react';
import { Platform, View } from 'react-native';
import ReactTestRenderer, { act } from 'react-test-renderer';

jest.mock(
  '../../../native-views/react-native-bottom-tabs/src/BottomTabsNativeComponent',
  () => ({
    __esModule: true,
    default: 'NativeBottomTabs',
  }),
);
jest.mock(
  '../../../native-views/react-native-bottom-tabs/src/BottomAccessoryView',
  () => ({
    BottomAccessoryView: 'BottomAccessoryView',
  }),
);

import BottomTabs from '../../../native-views/react-native-bottom-tabs/src/BottomTabs';

describe('Android bottom blur public option', () => {
  const originalOS = Platform.OS;
  const originalVersion = Object.getOwnPropertyDescriptor(Platform, 'Version');
  let renderer: ReactTestRenderer.ReactTestRenderer | undefined;

  afterEach(async () => {
    await act(() => renderer?.unmount());
    renderer = undefined;
    Object.defineProperty(Platform, 'OS', {
      configurable: true,
      value: originalOS,
    });
    if (originalVersion)
      Object.defineProperty(Platform, 'Version', originalVersion);
  });

  const renderTabs = (enabled?: boolean, custom = false) => (
    <BottomTabs
      navigationState={{ index: 0, routes: [{ key: 'home', title: 'Home' }] }}
      renderScene={() => <View />}
      onIndexChange={() => {}}
      androidBottomBlurEnabled={enabled}
      tabBar={custom ? () => <View /> : undefined}
    />
  );

  it.each([
    ['android', 35, undefined, false],
    ['android', 36, undefined, true],
    ['android', 37, undefined, true],
    ['android', 36, false, false],
    ['android', 35, true, true],
    ['ios', '26.0', undefined, false],
    ['ios', '26.0', true, false],
  ] as const)(
    '%s %s with override %s forwards %s',
    async (os, version, enabled, expected) => {
      Object.defineProperty(Platform, 'OS', { configurable: true, value: os });
      Object.defineProperty(Platform, 'Version', {
        configurable: true,
        value: version,
      });
      await act(() => {
        renderer = ReactTestRenderer.create(renderTabs(enabled));
      });
      expect(
        renderer!.root.findByType('NativeBottomTabs' as any).props
          .androidBottomBlurEnabled,
      ).toBe(expected);
    },
  );

  it('updates the native option without remounting scenes and ignores custom tab bars', async () => {
    Object.defineProperty(Platform, 'OS', {
      configurable: true,
      value: 'android',
    });
    Object.defineProperty(Platform, 'Version', {
      configurable: true,
      value: 36,
    });
    await act(() => {
      renderer = ReactTestRenderer.create(renderTabs());
    });
    const scene = renderer!.root.findByType(View);
    await act(() => {
      renderer!.update(renderTabs(false));
    });
    expect(
      renderer!.root.findByType('NativeBottomTabs' as any).props
        .androidBottomBlurEnabled,
    ).toBe(false);
    expect(renderer!.root.findByType(View)).toBe(scene);
    await act(() => {
      renderer!.update(renderTabs(true, true));
    });
    expect(
      renderer!.root.findByType('NativeBottomTabs' as any).props
        .androidBottomBlurEnabled,
    ).toBe(false);
  });
});
