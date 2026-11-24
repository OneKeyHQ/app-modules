/** @jest-environment jsdom */

import { act, createElement } from 'react';
import { createRoot } from 'react-dom/client';

import { NATIVE_MOTION_PRESETS } from '../animation/motionPresets';
import { OverlayView } from '../OverlayView';

import type { IResolvedOverlayAnimation } from '../animation/resolveAnimation';

const mockNativeCommit = jest.fn<
  void,
  [visible: boolean, animation: IResolvedOverlayAnimation]
>();

jest.mock('react-native', () => ({
  Platform: { OS: 'ios' },
  PlatformColor: (name: string) => name,
  StyleSheet: { create: (styles: unknown) => styles },
  View: () => null,
  useWindowDimensions: () => ({ width: 951, height: 669 }),
}));

jest.mock('../NativeOverlayNativeComponent', () => {
  const { useLayoutEffect } =
    jest.requireActual<typeof import('react')>('react');
  return {
    __esModule: true,
    default: function NativeOverlayMock({
      visible,
      animationConfig,
    }: {
      visible: boolean;
      animationConfig: string;
    }) {
      useLayoutEffect(() => {
        mockNativeCommit(visible, JSON.parse(animationConfig));
      }, [visible, animationConfig]);
      return null;
    },
  };
});

it('commits the synchronized iOS exit with visible=false and restores the spring on reopen', async () => {
  (
    globalThis as { IS_REACT_ACT_ENVIRONMENT?: boolean }
  ).IS_REACT_ACT_ENVIRONMENT = true;
  const root = createRoot(document.createElement('div'));
  const render = async (visible: boolean) =>
    act(async () =>
      root.render(createElement(OverlayView, { visible, backdrop: {} }))
    );
  const quick = NATIVE_MOTION_PRESETS.quick;
  const motion = {
    type: 'timing',
    timing: { durationMs: 150, easing: [0.42, 0, 1, 1] },
  };

  try {
    await render(true);
    await render(false);
    await render(true);

    expect(mockNativeCommit).toHaveBeenCalledTimes(3);
    expect(mockNativeCommit).toHaveBeenNthCalledWith(
      1,
      true,
      expect.objectContaining({ backdrop: { motion: quick } })
    );
    expect(mockNativeCommit).toHaveBeenNthCalledWith(
      2,
      false,
      expect.objectContaining({
        exit: { type: 'scale', scale: 0.9, fade: false, motion },
        backdrop: { motion },
      })
    );
    expect(mockNativeCommit.mock.calls[2]).toEqual(
      mockNativeCommit.mock.calls[0]
    );
  } finally {
    await act(async () => root.unmount());
  }
});
