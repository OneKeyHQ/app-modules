/** @jest-environment jsdom */

import {
  act,
  createContext,
  createElement,
  useContext,
  useEffect,
  useRef,
  useState,
} from 'react';
import type { ReactNode } from 'react';
import { createRoot } from 'react-dom/client';
import type { Root } from 'react-dom/client';

import { OverlayView } from '../OverlayView.web';
import { OverlayPageHost } from '../OverlayPageHost.web';
import { overlayStore } from '../OverlayStore';

jest.mock('react-native', () => {
  const React = jest.requireActual<typeof import('react')>('react');
  return {
    Platform: { OS: 'web' },
    StyleSheet: { absoluteFill: {} },
    View: React.forwardRef<HTMLDivElement, { children?: ReactNode }>(
      function ViewMock({ children }, ref) {
        return React.createElement('div', { ref }, children);
      }
    ),
  };
});

type Model = { value: string };
const EntityContext = createContext('entity-a');

function Draft({
  field,
  initial,
  onModel,
  validate = () => true,
  onSubmit,
}: {
  field: string;
  initial: string;
  onModel: (model: Model) => void;
  validate?: (value: string) => boolean;
  onSubmit?: (value: string, entity: string) => void;
}) {
  const [value, setValue] = useState(initial);
  const entity = useContext(EntityContext);
  const model = useRef({ value: initial }).current;
  model.value = value;
  onModel(model);
  useEffect(() => setValue(initial), [initial]);
  return createElement(
    'div',
    null,
    createElement('input', {
      'aria-label': field,
      value,
      'onInput': (event: React.FormEvent<HTMLInputElement>) =>
        setValue(event.currentTarget.value),
    }),
    createElement(
      'button',
      {
        type: 'button',
        onClick: () => {
          if (validate(value)) onSubmit?.(value, entity);
        },
      },
      'Submit'
    )
  );
}

let root: Root;
beforeEach(() => {
  document.body.innerHTML = '<div id="root"></div>';
  (
    globalThis as { IS_REACT_ACT_ENVIRONMENT?: boolean }
  ).IS_REACT_ACT_ENVIRONMENT = true;
  Element.prototype.getAnimations = () => [];
  Element.prototype.animate = jest.fn(() => ({
    finished: Promise.resolve(),
    cancel: jest.fn(),
  })) as unknown as typeof Element.prototype.animate;
  root = createRoot(document.getElementById('root')!);
});
afterEach(async () => {
  await act(async () => root.unmount());
  for (const entry of [
    ...overlayStore.getSnapshot().entries,
    ...overlayStore.getSnapshot().queued,
  ])
    overlayStore.finalize(entry.id);
  jest.restoreAllMocks();
});
const render = async (content: ReactNode) =>
  act(async () => root.render(content));
const inputFor = (field: string) =>
  document.querySelector<HTMLInputElement>(`input[aria-label="${field}"]`)!;
const edit = async (input: HTMLInputElement, value: string) =>
  act(async () => {
    input.value = value;
    input.dispatchEvent(new Event('input', { bubbles: true }));
  });

const drafts = [
  { field: 'name', initial: 'Original QA Name', edited: 'Edited QA Name' },
  { field: 'count', initial: '3', edited: '7' },
  { field: 'passphrase', initial: '', edited: 'QA-NOT-A-WALLET-50653' },
];
const sequences = [
  [400, 500, 400],
  [800, 900, 800],
  [767, 768, 767],
  [768, 767, 768],
];
it.each(
  drafts.flatMap((draft) =>
    sequences.map((sequence) => ({ ...draft, sequence }))
  )
)(
  'preserves $field through $sequence and resets it only after close',
  async ({ field, initial, edited, sequence }) => {
    let model: Model | undefined;
    const content = createElement(Draft, {
      field,
      initial,
      onModel: (current: Model) => {
        model = current;
      },
    });
    const overlay = (width: number, visible = true) =>
      createElement(
        OverlayView,
        {
          visible,
          presentation: width <= 767 ? 'sheet' : 'center',
          sheet:
            width <= 767
              ? { showHandle: true, dismissOnPanDown: false }
              : undefined,
        },
        content
      );
    await render(overlay(sequence[0]!));
    const input = inputFor(field);
    await edit(input, edited);
    const original = model;
    for (const width of sequence.slice(1)) {
      await render(overlay(width));
      expect(inputFor(field)).toBe(input);
      expect(input.value).toBe(edited);
      expect(model).toBe(original);
      expect(model?.value).toBe(edited);
    }
    await render(overlay(sequence.at(-1)!, false));
    expect(inputFor(field)).toBeNull();
    await render(overlay(sequence[0]!));
    expect(inputFor(field).value).toBe(initial);
    expect(model).not.toBe(original);
  }
);

it('delivers new props, context, validation and submit callbacks to preserved content', async () => {
  let model: Model | undefined;
  const onModel = (current: Model) => {
    model = current;
  };
  const oldSubmit = jest.fn();
  const newSubmit = jest.fn();
  const overlay = (
    sheet: boolean,
    entity: string,
    initial: string,
    min: number,
    onSubmit: typeof oldSubmit
  ) =>
    createElement(
      OverlayView,
      { visible: true, presentation: sheet ? 'sheet' : 'center' },
      createElement(
        EntityContext.Provider,
        { value: entity },
        createElement(Draft, {
          field: 'live',
          initial,
          onModel,
          validate: (value) => value.length >= min,
          onSubmit,
        })
      )
    );
  await render(overlay(true, 'entity-a', 'Original', 2, oldSubmit));
  const input = inputFor('live');
  await edit(input, 'ABC');
  const original = model;
  await render(overlay(false, 'entity-b', 'Original', 5, newSubmit));
  await act(async () =>
    document
      .querySelector<HTMLButtonElement>('[data-onekey-overlay-entry] button')!
      .click()
  );
  expect(oldSubmit).not.toHaveBeenCalled();
  expect(newSubmit).not.toHaveBeenCalled();
  expect(inputFor('live')).toBe(input);
  expect(model).toBe(original);
  await edit(input, 'Updated draft');
  await act(async () =>
    document
      .querySelector<HTMLButtonElement>('[data-onekey-overlay-entry] button')!
      .click()
  );
  expect(newSubmit).toHaveBeenCalledWith('Updated draft', 'entity-b');
  await render(overlay(true, 'entity-c', 'Entity C default', 5, newSubmit));
  expect(inputFor('live')).toBe(input);
  expect(input.value).toBe('Entity C default');
  expect(model).toBe(original);
});

it('measures the centered card for enter and exit animation origins', async () => {
  jest
    .spyOn(HTMLElement.prototype, 'getBoundingClientRect')
    .mockImplementation(function boundsMock(this: HTMLElement) {
      const [left, top, width, height] =
        this.dataset.testid === 'card'
          ? [200, 200, 400, 300]
          : [0, 0, 1024, 768];
      return {
        left: left!,
        top: top!,
        right: left! + width!,
        bottom: top! + height!,
        width: width!,
        height: height!,
        x: left!,
        y: top!,
        toJSON: () => ({}),
      };
    });
  const content = createElement('div', { 'data-testid': 'card' }, 'Draft');
  await render(createElement(OverlayView, { visible: true }, content));
  const card = document.querySelector<HTMLElement>('[data-testid="card"]')!;
  const animationLayer = card.parentElement!.parentElement!.parentElement!;
  expect(animationLayer.style.transformOrigin).toBe('400px 350px');
  await render(createElement(OverlayView, { visible: false }, content));
  expect(animationLayer.style.transformOrigin).toBe('400px 350px');
  expect(document.querySelector('[data-testid="card"]')).toBeNull();
});

it('keeps anchored positioning ancestors unscaled while preserving enter and exit motion', async () => {
  jest
    .spyOn(HTMLElement.prototype, 'getBoundingClientRect')
    .mockImplementation(function boundsMock(this: HTMLElement) {
      const [left, top, width, height] =
        this.dataset.testid === 'anchored-panel' ||
        this.dataset.testid === 'anchored-surface'
          ? [608, 331, 224, 104]
          : [0, 0, 1024, 768];
      return {
        left: left!,
        top: top!,
        right: left! + width!,
        bottom: top! + height!,
        width: width!,
        height: height!,
        x: left!,
        y: top!,
        toJSON: () => ({}),
      };
    });
  const cancel = jest.fn();
  const animate = jest.fn(function animateMock(
    this: HTMLElement,
    _keyframes: Keyframe[],
    _options: KeyframeAnimationOptions
  ) {
    return { finished: Promise.resolve(), cancel };
  });
  Element.prototype.animate =
    animate as unknown as typeof Element.prototype.animate;
  const content = createElement(
    'div',
    {
      'data-testid': 'anchored-panel',
      'style': { position: 'absolute', left: 608, top: 331 },
    },
    createElement('div', { 'data-testid': 'anchored-surface' }, 'Alpha')
  );
  const props = { presentation: 'anchored' as const, blocking: false };
  await render(
    createElement(OverlayView, { ...props, visible: true }, content)
  );
  const panel = document.querySelector<HTMLElement>(
    '[data-testid="anchored-panel"]'
  )!;
  expect(animate.mock.contexts).toEqual([panel]);
  expect(panel.style.transformOrigin).toBe('112px 52px');
  const enter = animate.mock.calls[0]!;
  expect(enter[0]).toEqual([
    { opacity: 0, transform: 'translate3d(0, 0px, 0) scale(0.95)' },
    { opacity: 1, transform: 'translate3d(0, 0, 0) scale(1)' },
  ]);
  expect(enter[1]).toMatchObject({
    duration: 150,
    easing: 'cubic-bezier(0.215, 0.61, 0.355, 1)',
  });
  await render(
    createElement(OverlayView, { ...props, visible: false }, content)
  );
  expect(animate.mock.contexts).toEqual([panel, panel]);
  expect(cancel).toHaveBeenCalledTimes(1);
  const exit = animate.mock.calls[1]!;
  expect(exit[0]).toEqual([...enter[0]].reverse());
  expect(exit[1]).toEqual(enter[1]);
});

it.each([false, true])(
  'only intercepts a non-blocking backdrop when dismissal is enabled: %s',
  async (dismissOnPress) => {
    const onRequestDismiss = jest.fn();
    const onPageClick = jest.fn();
    const outside = document.createElement('input');
    document.body.append(outside);
    outside.focus();
    await render(
      createElement(
        'div',
        { onClick: onPageClick },
        createElement(
          OverlayView,
          {
            visible: true,
            presentation: 'anchored',
            blocking: false,
            backdrop: { color: 'transparent', dismissOnPress },
            onRequestDismiss,
          },
          createElement('button', { type: 'button' }, 'Panel action')
        )
      )
    );
    const entry = document.querySelector<HTMLElement>(
      '[data-onekey-overlay-entry]'
    )!;
    const backdrop = entry.firstElementChild as HTMLElement;
    expect(backdrop.style.pointerEvents).toBe(dismissOnPress ? 'auto' : 'none');
    expect(backdrop.style.background).toBe('transparent');
    expect(entry.hasAttribute('aria-modal')).toBe(false);
    expect(document.activeElement).toBe(outside);
    expect(outside.closest('[inert]')).toBeNull();
    if (dismissOnPress) {
      await act(async () => backdrop.click());
      expect(onRequestDismiss).toHaveBeenCalledTimes(1);
      expect(onRequestDismiss).toHaveBeenCalledWith('backdrop');
      expect(onPageClick).not.toHaveBeenCalled();
    }
  }
);

it('lifts the centered card after switching from a sheet with the keyboard open', async () => {
  const previousViewport = Object.getOwnPropertyDescriptor(
    globalThis,
    'visualViewport'
  );
  const previousHeight = Object.getOwnPropertyDescriptor(
    globalThis,
    'innerHeight'
  );
  const viewport = new EventTarget();
  Object.defineProperties(viewport, {
    height: { value: 850, writable: true },
    offsetTop: { value: 0 },
  });
  Object.defineProperty(globalThis, 'visualViewport', {
    configurable: true,
    value: viewport,
  });
  Object.defineProperty(globalThis, 'innerHeight', {
    configurable: true,
    value: 850,
  });
  try {
    const content = createElement(
      'div',
      { 'data-testid': 'keyboard-card' },
      'Draft'
    );
    await render(
      createElement(
        OverlayView,
        { visible: true, presentation: 'sheet' },
        content
      )
    );
    const card = document.querySelector<HTMLElement>(
      '[data-testid="keyboard-card"]'
    )!;
    const host = card.parentElement!;
    const shell = host.parentElement!;
    const keyboardLayer = shell.parentElement!.parentElement!;
    Object.defineProperties(card, {
      offsetTop: { value: 350 },
      offsetHeight: { value: 300 },
    });
    Object.defineProperties(host, {
      offsetTop: { value: 0 },
      offsetHeight: { value: 850 },
    });
    Object.defineProperties(shell, {
      offsetTop: { value: 0 },
      offsetHeight: { value: 850 },
    });
    await render(
      createElement(
        OverlayView,
        { visible: true, presentation: 'center' },
        content
      )
    );
    Object.assign(viewport, { height: 600 });
    viewport.dispatchEvent(new Event('resize'));
    expect(keyboardLayer.style.transform).toBe('translate3d(0, -66px, 0)');
    Object.assign(viewport, { height: 850 });
    viewport.dispatchEvent(new Event('resize'));
    expect(keyboardLayer.style.transform).toBe('');
  } finally {
    if (previousViewport)
      Object.defineProperty(globalThis, 'visualViewport', previousViewport);
    else Reflect.deleteProperty(globalThis, 'visualViewport');
    if (previousHeight)
      Object.defineProperty(globalThis, 'innerHeight', previousHeight);
  }
});

it('clears an interrupted sheet pan and pointer capture when switching to center', async () => {
  const dismiss = jest.fn();
  const content = createElement('input', {
    'aria-label': 'pan',
    'defaultValue': 'Edited QA Name',
  });
  const props = {
    visible: true,
    backdrop: { color: 'black' },
    onRequestDismiss: dismiss,
  };
  await render(
    createElement(OverlayView, { ...props, presentation: 'sheet' }, content)
  );
  const input = inputFor('pan');
  const shell = input.parentElement!.parentElement!;
  const backdrop = input.closest('[data-onekey-overlay-entry]')!
    .firstElementChild as HTMLElement;
  Object.defineProperty(shell, 'offsetHeight', { value: 200 });
  shell.setPointerCapture = jest.fn();
  shell.hasPointerCapture = jest.fn(() => true);
  const release = jest.fn();
  shell.releasePointerCapture = release;
  const pointer = async (type: string, clientY: number) =>
    act(async () => {
      const event = new Event(type, { bubbles: true });
      Object.assign(event, { button: 0, pointerId: 1, clientY });
      input.dispatchEvent(event);
    });
  await pointer('pointerdown', 100);
  await pointer('pointermove', 120);
  expect(shell.style.transform).toBe('translate3d(0, 20px, 0)');
  expect(backdrop.style.opacity).toBe('0.9');
  await render(
    createElement(OverlayView, { ...props, presentation: 'center' }, content)
  );
  expect(shell.style.transform).toBe('');
  expect(shell.style.transition).toBe('');
  expect(backdrop.style.opacity).toBe('1');
  expect(release).toHaveBeenCalledWith(1);
  await pointer('pointerup', 200);
  expect(dismiss).not.toHaveBeenCalled();
  expect(inputFor('pan')).toBe(input);
});

it('cancels a sheet drag without dismissing or swallowing the next click', async () => {
  const dismiss = jest.fn();
  const click = jest.fn();
  await render(
    createElement(
      OverlayView,
      {
        visible: true,
        presentation: 'sheet',
        backdrop: { color: 'black' },
        onRequestDismiss: dismiss,
      },
      createElement('button', { type: 'button', onClick: click }, 'Tap')
    )
  );
  const button = document.querySelector<HTMLButtonElement>('button')!;
  const sheet = button.parentElement!.parentElement!;
  const backdrop = button.closest('[data-onekey-overlay-entry]')!
    .firstElementChild as HTMLElement;
  Object.defineProperty(sheet, 'offsetHeight', { value: 200 });
  sheet.setPointerCapture = jest.fn();
  sheet.hasPointerCapture = jest.fn(() => true);
  sheet.releasePointerCapture = jest.fn();
  const pointer = async (type: string, clientY: number, pointerId = 1) =>
    act(async () => {
      const event = new Event(type, { bubbles: true });
      Object.assign(event, { button: 0, pointerId, clientY });
      button.dispatchEvent(event);
    });

  await pointer('pointerdown', 100);
  await pointer('pointermove', 200);
  expect(sheet.style.transform).toBe('translate3d(0, 100px, 0)');
  await pointer('pointercancel', 200, 2);
  expect(sheet.style.transform).toBe('translate3d(0, 100px, 0)');
  await pointer('pointercancel', 200);
  expect(sheet.style.transform).toBe('');
  expect(sheet.style.transition).toBe('');
  expect(backdrop.style.opacity).toBe('1');
  expect(sheet.releasePointerCapture).toHaveBeenCalledWith(1);
  expect(dismiss).not.toHaveBeenCalled();
  await act(async () => button.click());
  expect(click).toHaveBeenCalledTimes(1);

  await pointer('pointerdown', 100);
  await pointer('pointercancel', 100);
  await pointer('pointerup', 200);
  expect(dismiss).not.toHaveBeenCalled();
  await act(async () => button.click());
  expect(click).toHaveBeenCalledTimes(2);

  await pointer('pointerdown', 100);
  await pointer('pointermove', 200);
  await pointer('pointerup', 200);
  expect(dismiss).toHaveBeenCalledWith('pan');
});

it('resets a panned sheet when kept content reopens and restores focus on close', async () => {
  const finishes: Array<() => void> = [];
  const cancel = jest.fn();
  Element.prototype.animate = jest.fn(function animateMock(this: HTMLElement) {
    let finish!: () => void;
    const finished = new Promise<void>((resolve) => {
      finish = resolve;
    });
    finishes.push(finish);
    return {
      finished,
      cancel,
      playState: 'running',
      effect: { target: this },
    };
  }) as unknown as typeof Element.prototype.animate;
  const outside = document.createElement('button');
  document.body.append(outside);
  outside.focus();
  const onRequestDismiss = jest.fn();
  const onClose = jest.fn();
  const onPresented = jest.fn();
  const content = createElement('input', { 'aria-label': 'panned draft' });
  const show = (visible: boolean) =>
    createElement(
      OverlayView,
      {
        visible,
        keepContentMounted: true,
        presentation: 'sheet',
        backdrop: { color: 'black' },
        onRequestDismiss,
        onClose,
        onPresented,
      },
      content
    );
  await render(show(true));
  const input = inputFor('panned draft');
  await edit(input, 'saved draft');
  const sheet = input.parentElement!.parentElement!;
  const backdrop = input.closest('[data-onekey-overlay-entry]')!
    .firstElementChild as HTMLElement;
  Object.defineProperty(sheet, 'offsetHeight', { value: 200 });
  sheet.setPointerCapture = jest.fn();
  sheet.hasPointerCapture = jest.fn(() => true);
  sheet.releasePointerCapture = jest.fn();
  const pointer = async (type: string, clientY: number) =>
    act(async () => {
      const event = new Event(type, { bubbles: true });
      Object.assign(event, { button: 0, pointerId: 1, clientY });
      input.dispatchEvent(event);
    });
  await pointer('pointerdown', 100);
  await pointer('pointermove', 120);
  await pointer('pointerup', 200);
  expect(onRequestDismiss).toHaveBeenCalledWith('pan');
  expect(sheet.style.transform).toBe('translate3d(0, 200px, 0)');
  await render(show(false));
  expect(cancel).toHaveBeenCalled();
  expect(document.activeElement).toBe(outside);
  expect(onClose).toHaveBeenCalledTimes(1);
  await act(async () => finishes.splice(0).forEach((finish) => finish()));
  expect(onPresented).not.toHaveBeenCalled();
  await render(show(true));
  expect(inputFor('panned draft')).toBe(input);
  expect(input.value).toBe('saved draft');
  expect(sheet.style.transform).toBe('');
  expect(sheet.style.transition).toBe('');
  expect(backdrop.style.opacity).toBe('1');
  await render(show(false));
  await act(async () => finishes.splice(0).forEach((finish) => finish()));
  expect(onClose).toHaveBeenCalledTimes(2);
  expect(document.activeElement).toBe(outside);
});

it('keeps a closed page overlay in its host and never falls back to a global layer', async () => {
  const content = createElement('input', { 'aria-label': 'page draft' });
  const view = (visible: boolean, withHost: boolean) =>
    createElement(
      'div',
      null,
      withHost && createElement(OverlayPageHost, { hostKey: 'draft-host' }),
      createElement(
        OverlayView,
        {
          visible,
          keepContentMounted: true,
          scope: 'page',
          hostKey: 'draft-host',
          ownerKey: 'draft-owner',
        },
        content
      )
    );
  await render(view(false, false));
  expect(inputFor('page draft')).toBeNull();
  await render(view(false, true));
  const input = inputFor('page draft');
  expect(input.closest('[data-onekey-overlay-layer]')).toBeNull();
  await render(view(true, true));
  await edit(input, 'page value');
  await render(view(false, true));
  await render(view(true, true));
  expect(inputFor('page draft')).toBe(input);
  expect(input.value).toBe('page value');
  await render(view(true, false));
  expect(inputFor('page draft')).toBeNull();
  expect(
    document.querySelector(
      '[data-onekey-overlay-layer] input[aria-label="page draft"]'
    )
  ).toBeNull();
});

it('retargets an unfinished web exit without unmounting content or reporting close', async () => {
  const finishes: Array<() => void> = [];
  const cancel = jest.fn();
  const targets: HTMLElement[] = [];
  const starts: Keyframe[] = [];
  Element.prototype.animate = jest.fn(function animateMock(
    this: HTMLElement,
    frames: Keyframe[]
  ) {
    targets.push(this);
    starts.push(frames[0]!);
    let finish!: () => void;
    let reject!: (reason: Error) => void;
    const finished = new Promise<void>((resolve, fail) => {
      finish = resolve;
      reject = fail;
    });
    const animation = {
      finished,
      playState: 'running',
      effect: { target: this },
      cancel: () => {
        cancel();
        animation.playState = 'idle';
        this.style.opacity = '1';
        this.style.transform = 'none';
        reject(new Error('cancelled'));
      },
    };
    finishes.push(() => {
      animation.playState = 'finished';
      finish();
    });
    return animation;
  }) as unknown as typeof Element.prototype.animate;
  const onClose = jest.fn();
  const show = (visible: boolean) =>
    createElement(OverlayView, {
      visible,
      onClose,
      children: createElement('input', { 'aria-label': 'Reopen draft' }),
    });
  await act(async () => root.render(show(true)));
  const input = document.querySelector('input')!;
  input.value = 'keep draft';
  const id = overlayStore.getSnapshot().entries[0]!.id;
  await act(async () => finishes.splice(0).forEach((finish) => finish()));
  await act(async () => root.render(show(false)));
  expect(overlayStore.getEntry(id)?.phase).toBe('closing');
  const contentTarget = targets[targets.length - 2]!;
  contentTarget.style.opacity = '0.4';
  contentTarget.style.transform = 'matrix(0.9, 0, 0, 0.9, 0, 0)';
  await act(async () => root.render(show(true)));
  expect(starts[starts.length - 2]?.opacity).toBe('0.4');
  expect(starts[starts.length - 2]?.transform).toContain('0.9');
  expect(cancel).toHaveBeenCalled();
  expect(overlayStore.getEntry(id)?.phase).toBe('active');
  expect(document.querySelector('input')).toBe(input);
  expect(input.value).toBe('keep draft');
  await act(async () => finishes.splice(0).forEach((finish) => finish()));
  expect(onClose).not.toHaveBeenCalled();
  await act(async () => root.render(show(false)));
  await act(async () => finishes.splice(0).forEach((finish) => finish()));
  expect(onClose).toHaveBeenCalledTimes(1);
});
