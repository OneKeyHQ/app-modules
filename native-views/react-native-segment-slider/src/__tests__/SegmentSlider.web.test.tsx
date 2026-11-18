/**
 * @jest-environment jsdom
 */
import { act } from 'react';
import { createRoot } from 'react-dom/client';
import type { Root } from 'react-dom/client';

import { SegmentSlider } from '../SegmentSlider.web';
import type { ISegmentSliderProps } from '../types';

(
  globalThis as { IS_REACT_ACT_ENVIRONMENT?: boolean }
).IS_REACT_ACT_ENVIRONMENT = true;

const TRACK_LEFT = 100;
const TRACK_WIDTH = 200;

let container: HTMLDivElement;
let root: Root;

beforeEach(() => {
  container = document.createElement('div');
  document.body.appendChild(container);
  root = createRoot(container);
  // jsdom has no layout: give every element the same 200-px-wide track box.
  jest.spyOn(HTMLElement.prototype, 'getBoundingClientRect').mockReturnValue({
    left: TRACK_LEFT,
    width: TRACK_WIDTH,
    top: 0,
    right: TRACK_LEFT + TRACK_WIDTH,
    bottom: 24,
    height: 24,
    x: TRACK_LEFT,
    y: 0,
    toJSON: () => ({}),
  });
  // jsdom lacks pointer capture.
  HTMLElement.prototype.setPointerCapture = jest.fn();
  HTMLElement.prototype.releasePointerCapture = jest.fn();
});

afterEach(() => {
  act(() => root.unmount());
  container.remove();
  jest.restoreAllMocks();
});

type IRenderProps = Partial<Omit<ISegmentSliderProps, 'onChange'>>;

function render(props: IRenderProps = {}) {
  const onChange = jest.fn<void, [number]>();
  const all: ISegmentSliderProps = {
    value: 0,
    segments: 4,
    ...props,
    onChange,
  };
  act(() => root.render(<SegmentSlider {...all} />));
  const slider = container.querySelector<HTMLDivElement>('[role="slider"]')!;
  return {
    slider,
    onChange,
    rerender: (next: IRenderProps) =>
      act(() => root.render(<SegmentSlider {...all} {...next} />)),
  };
}

function pointer(
  el: Element,
  type: 'pointerdown' | 'pointermove' | 'pointerup',
  clientX: number,
  pointerId = 1,
  button = 0
) {
  // jsdom has no PointerEvent constructor; React listens by event name.
  const event = new MouseEvent(type, { bubbles: true, clientX, button });
  Object.defineProperty(event, 'pointerId', { value: pointerId });
  act(() => {
    el.dispatchEvent(event);
  });
}

function key(el: Element, k: string) {
  act(() => {
    el.dispatchEvent(new KeyboardEvent('keydown', { key: k, bubbles: true }));
  });
}

const thumb = () =>
  container.querySelector<HTMLDivElement>('[data-segment-thumb]')!;
const mark = (i: number) =>
  container.querySelector<HTMLDivElement>(`[data-segment-mark="${i}"]`)!;
const markDot = (i: number) => mark(i).firstElementChild as HTMLDivElement;

it('moves through the integer values used by integer-aligned marks', () => {
  const { slider, onChange } = render({
    value: 10,
    min: 10,
    max: 100,
    alignSegmentMarksToIntegerValues: true,
  });
  ['ArrowRight', 'ArrowRight', 'ArrowRight', 'ArrowRight'].forEach((k) =>
    key(slider, k)
  );
  expect(onChange.mock.calls.map(([v]) => v)).toEqual([32, 55, 78, 100]);
});

it('draws the thumb above the marks', () => {
  render({ value: 50 });
  expect(Number(thumb().style.zIndex)).toBeGreaterThan(
    Number(mark(2).style.zIndex)
  );
  expect(thumb().style.pointerEvents).toBe('none');
});

it('paints the caller colors', () => {
  render({
    value: 50,
    fillColor: '#00ff00',
    trackColor: '#111111',
    markActiveColor: '#00aa00',
    markInactiveColor: '#ffffff',
    markBorderColor: 'rgb(34, 34, 34)',
    thumbColor: '#fafafa',
    thumbBorderColor: 'rgb(0, 187, 0)',
  });
  const track = mark(0).parentElement!;
  const fill = track.firstElementChild as HTMLDivElement;
  expect(track.style.background).toBe('rgb(17, 17, 17)');
  expect(fill.style.background).toBe('rgb(0, 255, 0)');
  expect(markDot(2).style.background).toBe('rgb(0, 170, 0)');
  expect(markDot(3).style.background).toBe('rgb(255, 255, 255)');
  expect(markDot(3).style.borderColor).toBe('rgb(34, 34, 34)');
  const thumbDot = thumb().firstElementChild as HTMLDivElement;
  expect(thumbDot.style.background).toBe('rgb(250, 250, 250)');
  expect(thumbDot.style.border).toBe('1px solid rgb(0, 187, 0)');
});

it('follows a controlled value without emitting onChange', () => {
  const { onChange, rerender } = render({ value: 0 });
  rerender({ value: 75 });
  expect(thumb().style.left).toBe('75%');
  expect(onChange).not.toHaveBeenCalled();
});

it('emits rounded values while dragging and ignores value echoes', () => {
  const { slider, onChange, rerender } = render({ value: 0, segments: 0 });
  pointer(slider, 'pointerdown', TRACK_LEFT + 20);
  pointer(slider, 'pointermove', TRACK_LEFT + 101);
  // A stale parent value mid-drag must not move the thumb.
  rerender({ value: 3 });
  expect(thumb().style.left).toBe('51%');
  pointer(slider, 'pointerup', TRACK_LEFT + 101);
  expect(onChange.mock.calls.map(([v]) => v)).toEqual([10, 51]);
});

it('snaps a quick tap to the nearest mark with snapTapToSegment', () => {
  const { slider, onChange } = render({ value: 0, snapTapToSegment: true });
  pointer(slider, 'pointerdown', TRACK_LEFT + 60);
  pointer(slider, 'pointerup', TRACK_LEFT + 60);
  expect(onChange.mock.calls.map(([v]) => v)).toEqual([25]);
});

it('lets a drag stay free with snapTapToSegment', () => {
  const { slider, onChange } = render({ value: 0, snapTapToSegment: true });
  pointer(slider, 'pointerdown', TRACK_LEFT + 20);
  pointer(slider, 'pointermove', TRACK_LEFT + 66);
  pointer(slider, 'pointerup', TRACK_LEFT + 66);
  expect(onChange.mock.calls.map(([v]) => v)).toEqual([33]);
});

it('applies a value the parent changed during the drag once it ends', () => {
  const onSlideComplete = jest.fn();
  const { slider, onChange, rerender } = render({
    value: 0,
    segments: 0,
    onSlideComplete,
  });
  pointer(slider, 'pointerdown', TRACK_LEFT + 120);
  // The parent clamps the emitted 60 to 40 while the pointer is still down.
  rerender({ value: 40, onSlideComplete });
  expect(thumb().style.left).toBe('60%');
  pointer(slider, 'pointerup', TRACK_LEFT + 120);
  expect(thumb().style.left).toBe('40%');
  expect(onChange.mock.calls.map(([v]) => v)).toEqual([60]);
  expect(onSlideComplete).toHaveBeenCalledTimes(1);
});

it('steps the keyboard through the mark values without drifting', () => {
  const { slider, onChange } = render({ value: 0, segments: 3 });
  ['ArrowRight', 'ArrowRight', 'ArrowRight', 'ArrowRight'].forEach((k) =>
    key(slider, k)
  );
  ['ArrowLeft', 'ArrowLeft', 'ArrowLeft'].forEach((k) => key(slider, k));
  expect(onChange.mock.calls.map(([v]) => v)).toEqual([33, 67, 100, 67, 33, 0]);
});

it('steps one unit by keyboard without marks', () => {
  const { slider, onChange } = render({ value: 5, segments: 0 });
  key(slider, 'ArrowRight');
  key(slider, 'ArrowLeft');
  key(slider, 'ArrowLeft');
  expect(onChange.mock.calls.map(([v]) => v)).toEqual([6, 5, 4]);
});

it('treats each keyboard action as one complete interaction', () => {
  const onSlideStart = jest.fn();
  const onSlideComplete = jest.fn();
  const { slider, onChange } = render({
    value: 0,
    onSlideStart,
    onSlideComplete,
  });
  key(slider, 'ArrowRight');
  key(slider, 'End');
  key(slider, 'Home');
  key(slider, 'a');
  expect(onChange.mock.calls.map(([v]) => v)).toEqual([25, 100, 0]);
  expect(onSlideStart).toHaveBeenCalledTimes(3);
  expect(onSlideComplete).toHaveBeenCalledTimes(3);
});

it('ignores keyboard input while disabled', () => {
  const onSlideStart = jest.fn();
  const { slider, onChange } = render({
    value: 0,
    disabled: true,
    onSlideStart,
  });
  key(slider, 'ArrowRight');
  key(slider, 'End');
  expect(onChange).not.toHaveBeenCalled();
  expect(onSlideStart).not.toHaveBeenCalled();
});

it('cancels an active drag when the slider becomes disabled', () => {
  const onSlideComplete = jest.fn();
  const { slider, onChange, rerender } = render({
    value: 0,
    segments: 0,
    onSlideComplete,
  });
  pointer(slider, 'pointerdown', TRACK_LEFT + 20);
  rerender({ value: 10, disabled: true, onSlideComplete });
  expect(onSlideComplete).toHaveBeenCalledTimes(1);
  pointer(slider, 'pointermove', TRACK_LEFT + 150);
  pointer(slider, 'pointerup', TRACK_LEFT + 150);
  expect(onChange.mock.calls.map(([v]) => v)).toEqual([10]);
  expect(onSlideComplete).toHaveBeenCalledTimes(1);
});

it('keeps a drag bound to the pointer that started it', () => {
  const onSlideStart = jest.fn();
  const onSlideComplete = jest.fn();
  const { slider, onChange } = render({
    value: 0,
    segments: 0,
    onSlideStart,
    onSlideComplete,
  });
  pointer(slider, 'pointerdown', TRACK_LEFT + 20, 1);
  pointer(slider, 'pointerdown', TRACK_LEFT + 180, 2);
  pointer(slider, 'pointermove', TRACK_LEFT + 160, 2);
  pointer(slider, 'pointerup', TRACK_LEFT + 160, 2);
  pointer(slider, 'pointermove', TRACK_LEFT + 40, 1);
  pointer(slider, 'pointerup', TRACK_LEFT + 40, 1);
  expect(onChange.mock.calls.map(([v]) => v)).toEqual([10, 20]);
  expect(onSlideStart).toHaveBeenCalledTimes(1);
  expect(onSlideComplete).toHaveBeenCalledTimes(1);
});

it('keeps a tap on a mark on that mark with snapTapToSegment', () => {
  const { onChange } = render({ value: 0, snapTapToSegment: true });
  // The press lands in mark 1's hit area but nearer mark 2 (40% vs 25%/50%
  // would pick 50 if recomputed from the position).
  pointer(mark(1), 'pointerdown', TRACK_LEFT + 76);
  pointer(mark(1), 'pointerup', TRACK_LEFT + 76);
  expect(onChange.mock.calls.map(([v]) => v)).toEqual([25]);
});

it('ignores secondary mouse buttons on the track and on marks', () => {
  const onSlideStart = jest.fn();
  const { slider, onChange } = render({ value: 0, onSlideStart });
  pointer(slider, 'pointerdown', TRACK_LEFT + 160, 1, 2);
  pointer(slider, 'pointerup', TRACK_LEFT + 160, 1, 2);
  pointer(mark(2), 'pointerdown', TRACK_LEFT + 100, 1, 1);
  pointer(mark(2), 'pointerup', TRACK_LEFT + 100, 1, 1);
  expect(onChange).not.toHaveBeenCalled();
  expect(onSlideStart).not.toHaveBeenCalled();
  expect(thumb().style.left).toBe('0%');
});

it('exposes the accessible name', () => {
  const { slider } = render({
    'aria-label': 'Amount',
    'aria-labelledby': 'amount-title',
  });
  expect(slider.getAttribute('aria-label')).toBe('Amount');
  expect(slider.getAttribute('aria-labelledby')).toBe('amount-title');
});

it('never emits a value past fractional bounds', () => {
  const { slider, onChange } = render({
    value: 0.5,
    min: 0.5,
    max: 10.5,
    segments: 0,
  });
  key(slider, 'End');
  pointer(slider, 'pointerdown', TRACK_LEFT);
  pointer(slider, 'pointerup', TRACK_LEFT);
  expect(onChange.mock.calls.map(([v]) => v)).toEqual([10, 1]);
});
