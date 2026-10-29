/** @jest-environment jsdom */
import { act, createElement } from 'react';
import type { ReactNode } from 'react';
import { createRoot } from 'react-dom/client';

import { Platform } from 'react-native';
import { OverlayView } from '../OverlayView';

jest.mock('react-native', () => ({
  Platform: { OS: 'ios' },
  PlatformColor: (name: string) => name,
  useWindowDimensions: () => ({ width: 951, height: 669 }),
  StyleSheet: { create: (styles: unknown) => styles },
  View: ({
    children,
    style,
  }: {
    children?: ReactNode;
    style?: object | object[];
  }) =>
    jest.requireActual<typeof import('react')>('react').createElement(
      'div',
      {
        'data-background': Object.assign(
          {},
          ...(Array.isArray(style) ? style : [style])
        ).backgroundColor,
      },
      children
    ),
  Pressable: () => null,
  StatusBar: () => null,
}));
jest.mock('../NativeOverlayNativeComponent', () => ({
  __esModule: true,
  default: ({
    children,
    sheetBackgroundColor,
  }: {
    children?: ReactNode;
    sheetBackgroundColor?: string;
  }) =>
    jest
      .requireActual<typeof import('react')>('react')
      .createElement(
        'div',
        { 'data-native-background': sheetBackgroundColor },
        children
      ),
}));
jest.mock('../useOverlayController', () => ({
  useOverlayController: () => ({
    mounted: true,
    presented: true,
    entry: { seq: 1 },
  }),
}));

it.each([
  ['ios', undefined, 'systemBackground'],
  ['android', undefined, '?android:attr/colorBackground'],
  ['ios', '#123456', '#123456'],
  ['ios', 'transparent', 'transparent'],
])(
  'keeps the sheet surface and tail background on %s with %s',
  async (os, color, expected) => {
    Platform.OS = os as typeof Platform.OS;
    (
      globalThis as { IS_REACT_ACT_ENVIRONMENT?: boolean }
    ).IS_REACT_ACT_ENVIRONMENT = true;
    const container = document.createElement('div');
    const root = createRoot(container);
    try {
      await act(async () =>
        root.render(
          createElement(OverlayView, {
            visible: true,
            presentation: 'sheet',
            sheet: { height: 200, maxWidth: 540, backgroundColor: color },
          })
        )
      );
      expect(
        container
          .querySelector('[data-native-background]')
          ?.getAttribute('data-native-background')
      ).toBe('transparent');
      expect(
        [...container.querySelectorAll('[data-background]')].map((node) =>
          node.getAttribute('data-background')
        )
      ).toEqual([expected, expected]);
    } finally {
      await act(async () => root.unmount());
    }
  }
);
