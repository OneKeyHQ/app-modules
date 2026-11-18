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
  clientX: number
) {
  // jsdom has no PointerEvent constructor; React listens by event name.
  const event = new MouseEvent(type, { bubbles: true, clientX });
  Object.defineProperty(event, 'pointerId', { value: 1 });
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
