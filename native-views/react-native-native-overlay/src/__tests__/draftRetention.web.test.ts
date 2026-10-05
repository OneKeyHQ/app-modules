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
