import TestRenderer, { act } from 'react-test-renderer';

import { SegmentSlider } from '../SegmentSlider';
import type { ISegmentSliderProps } from '../types';

// Capture the props the wrapper hands to the Nitro view.
let viewProps: Record<string, any> = {};
jest.mock('../SegmentSliderView', () => ({
  SegmentSliderView: (props: Record<string, any>) => {
    viewProps = props;
    return null;
  },
}));
jest.mock('react-native-nitro-modules', () => ({
  callback: (fn: unknown) => fn,
}));
jest.mock('react-native', () => ({
  StyleSheet: { create: (styles: unknown) => styles },
}));

function setup(initial: Partial<ISegmentSliderProps> = {}) {
  const onChange = jest.fn<void, [number]>();
  const onSlideComplete = jest.fn();
  const node = { setValue: jest.fn<void, [number]>() };
  const base: ISegmentSliderProps = {
    value: 0,
    segments: 4,
    onChange,
    onSlideComplete,
    ...initial,
  };
  let renderer: TestRenderer.ReactTestRenderer;
  act(() => {
    renderer = TestRenderer.create(<SegmentSlider {...base} />);
  });
  act(() => viewProps.hybridRef(node));
  node.setValue.mockClear();
  return {
    node,
    onChange,
    onSlideComplete,
    rerender: (next: Partial<ISegmentSliderProps>) =>
      act(() => renderer.update(<SegmentSlider {...base} {...next} />)),
  };
}

it('applies a value the parent changed during the drag once it ends', () => {
  const { node, onSlideComplete, rerender } = setup();
  act(() => viewProps.onSlideStart());
  act(() => viewProps.onChange(60));
  // The parent clamps the emitted 60 to 40 mid-drag; the sync waits.
  rerender({ value: 40 });
  expect(node.setValue).not.toHaveBeenCalled();
  act(() => viewProps.onSlideComplete());
  expect(node.setValue).toHaveBeenCalledWith(40);
  expect(onSlideComplete).toHaveBeenCalledTimes(1);
});

it('does not push the echoed drag value back after the drag', () => {
  const { node, rerender } = setup();
  act(() => viewProps.onSlideStart());
  act(() => viewProps.onChange(60));
  rerender({ value: 60 });
  act(() => viewProps.onSlideComplete());
  expect(node.setValue).not.toHaveBeenCalled();
});

it('pushes external value changes outside a drag', () => {
  const { node, onChange, rerender } = setup();
  rerender({ value: 75 });
  expect(node.setValue).toHaveBeenCalledWith(75);
  expect(onChange).not.toHaveBeenCalled();
});

it('passes hapticStep and colors through to the native view', () => {
  setup({ hapticStep: 1, fillColor: '#00ff00' });
  expect(viewProps.hapticStep).toBe(1);
  expect(viewProps.fillColor).toBe('#00ff00');
  expect(viewProps.trackColor).toBe('#E3E3E3');
});
