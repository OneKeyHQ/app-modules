/** @jest-environment jsdom */

import { overlayStore } from '../OverlayStore';
import { OverlayToastStore } from '../toast/OverlayToastStore';
import { createToastDomRenderer } from '../toast/toastDomRenderer.web';
import { serializeToastItems, toToastEvent } from '../toast/toastBridge';
import type {
  IOverlayMessageToast,
  IOverlayToastEvent,
  IOverlayToastRenderer,
  IOverlayToastText,
} from '../toast/types';

function text(value: string): IOverlayToastText {
  return {
    paragraphs: value.split('\n').map((line) => [{ text: line }]),
    color: '#111',
    fontSize: 14,
    lineHeight: 20,
    fontWeight: '600',
  };
}

function message(
  id: string,
  overrides: Partial<IOverlayMessageToast> = {}
): IOverlayMessageToast {
  return {
    id,
    layout: 'message',
    duration: 5000,
    title: text(`Title ${id}`),
    ...overrides,
  };
}

let store: OverlayToastStore;
let renderer: IOverlayToastRenderer;
let events: IOverlayToastEvent[];
let detach: () => void;

function cards(): HTMLElement[] {
  return [...document.querySelectorAll<HTMLElement>('[data-onekey-toast]')];
}

function attach() {
  store = new OverlayToastStore();
  events = [];
  renderer = createToastDomRenderer((event) => {
    events.push(event);
    store.handleEvent(event);
  });
  detach = store.attachRenderer();
  const sync = () => {
    const { items, config } = store.getSnapshot();
    renderer.sync(items, config);
  };
  store.subscribe(sync);
  sync();
}

beforeEach(() => {
  jest.useFakeTimers();
  document.body.innerHTML = '<div id="root"></div>';
  attach();
});

afterEach(() => {
  renderer.destroy();
  detach();
  delete (document as { elementFromPoint?: unknown }).elementFromPoint;
  jest.useRealTimers();
});

test('draws a card in the toast layer without a React root', () => {
  store.show(message('a', { message: text('Body') }));
  const [card] = cards();
  expect(card?.closest('[data-onekey-overlay-layer="toast"]')).not.toBeNull();
  expect(card?.textContent).toContain('Title a');
  expect(card?.textContent).toContain('Body');
  expect(card?.getAttribute('data-mounted')).toBe('true');
});

test('stacks newest first and shows at most three', () => {
  ['a', 'b', 'c', 'd'].forEach((id) => store.show(message(id)));
  const list = cards();
  expect(list.map((card) => card.textContent)).toEqual([
    'Title d',
    'Title c',
    'Title b',
    'Title a',
  ]);
  expect(list.map((card) => card.getAttribute('data-front'))).toEqual([
    'true',
    'false',
    'false',
    'false',
  ]);
  expect(list.map((card) => card.getAttribute('data-visible'))).toEqual([
    'true',
    'true',
    'true',
    'false',
  ]);
});

test('times out, plays the exit, then leaves the list', () => {
  const onDismiss = jest.fn();
  const onClose = jest.fn();
  store.show(message('a', { duration: 1000 }), { onDismiss, onClose });
  jest.advanceTimersByTime(999);
  expect(onDismiss).not.toHaveBeenCalled();
  jest.advanceTimersByTime(1);
  expect(onDismiss).toHaveBeenCalledWith('timeout');
  expect(cards()[0]?.getAttribute('data-removed')).toBe('true');
  expect(onClose).not.toHaveBeenCalled();
  jest.advanceTimersByTime(200);
  expect(onClose).toHaveBeenCalledWith('timeout');
  expect(cards()).toHaveLength(0);
  expect(store.getSnapshot().items).toHaveLength(0);
});

test('an infinite duration never times out', () => {
  store.show(message('a', { duration: Infinity }));
  jest.advanceTimersByTime(60_000);
  expect(cards()).toHaveLength(1);
});

test('hovering the stack pauses every timer and leaving restarts them', () => {
  store.show(message('a', { duration: 1000 }));
  store.show(message('b', { duration: 1000 }));
  jest.advanceTimersByTime(900);
  const list = document.querySelector('[data-onekey-toast-toaster]');
  dispatchPointer(list!, 'pointerenter');
  expect(cards()[0]?.getAttribute('data-expanded')).toBe('true');
  jest.advanceTimersByTime(5000);
  expect(cards()).toHaveLength(2);
  dispatchPointer(list!, 'pointerleave');
  jest.advanceTimersByTime(999);
  expect(cards().every((card) => card.dataset.removed === 'false')).toBe(true);
  jest.advanceTimersByTime(1);
  expect(cards().every((card) => card.dataset.removed === 'true')).toBe(true);
});

test('hovering a single toast pauses its timer without fanning out', () => {
  store.show(message('a', { duration: 1000 }));
  jest.advanceTimersByTime(900);
  const list = document.querySelector('[data-onekey-toast-toaster]');
  dispatchPointer(list!, 'pointerenter');
  expect(cards()[0]?.getAttribute('data-expanded')).toBe('false');
  jest.advanceTimersByTime(5000);
  expect(cards()[0]?.getAttribute('data-removed')).toBe('false');
  dispatchPointer(list!, 'pointerleave');
  jest.advanceTimersByTime(999);
  expect(cards()[0]?.getAttribute('data-removed')).toBe('false');
  jest.advanceTimersByTime(1);
  expect(cards()[0]?.getAttribute('data-removed')).toBe('true');
});

test('pointercancel clears interaction state and resumes timers without swipe dismissal', () => {
  store.show(message('a', { duration: 1000 }));
  const card = cards()[0]!;
  dispatchPointer(card, 'pointerdown', { clientX: 200, clientY: 200 });
  dispatchPointer(card, 'pointermove', { clientX: 270, clientY: 200 });
  jest.advanceTimersByTime(2000);
  expect(card.getAttribute('data-removed')).toBe('false');
  dispatchPointer(card, 'pointercancel', { clientX: 270, clientY: 200 });
  expect(card.getAttribute('data-removed')).toBe('false');
  expect(card.getAttribute('data-swiping')).toBe('false');
  jest.advanceTimersByTime(999);
  expect(card.getAttribute('data-removed')).toBe('false');
  jest.advanceTimersByTime(1);
  expect(card.getAttribute('data-removed')).toBe('true');
});

test('the close button dismisses with the close reason', () => {
  const onClose = jest.fn();
  store.show(message('a'), { onClose });
  cards()[0]?.querySelector<HTMLButtonElement>('[data-close-button]')?.click();
  jest.advanceTimersByTime(200);
  expect(onClose).toHaveBeenCalledWith('close');
});

test('a programmatic dismiss animates out and reports once', () => {
  const onDismiss = jest.fn();
  const onClose = jest.fn();
  const handle = store.show(message('a'), { onDismiss, onClose });
  handle.dismiss();
  handle.dismiss();
  expect(onDismiss).toHaveBeenCalledTimes(1);
  expect(cards()[0]?.getAttribute('data-removed')).toBe('true');
  jest.advanceTimersByTime(200);
  expect(onClose).toHaveBeenCalledTimes(1);
  expect(onClose).toHaveBeenCalledWith('programmatic');
});

test('showing an id that is on screen updates the card in place', () => {
  store.show(message('a', { duration: 1000 }));
  jest.advanceTimersByTime(900);
  store.show(message('a', { duration: 1000, title: text('Changed') }));
  expect(cards()).toHaveLength(1);
  expect(cards()[0]?.textContent).toBe('Changed');
  jest.advanceTimersByTime(900);
  expect(cards()[0]?.getAttribute('data-removed')).toBe('false');
});

test('actions and links report their ids and do not dismiss', () => {
  const onAction = jest.fn();
  const onLink = jest.fn();
  store.show(
    message('a', {
      message: {
        ...text(''),
        paragraphs: [[{ text: 'See ' }, { text: 'docs', link: 'https://x' }]],
      },
      actions: [
        {
          id: 'retry',
          label: 'Retry',
          testID: 'retry-btn',
          color: '#fff',
          backgroundColor: '#000',
          fontSize: 14,
          lineHeight: 20,
          fontWeight: '500',
          paddingHorizontal: 10,
          paddingVertical: 4,
          borderRadius: 8,
        },
      ],
    }),
    { onAction, onLink }
  );
  document.querySelector<HTMLElement>('[data-testid="retry-btn"]')?.click();
  document.querySelector<HTMLElement>('[data-okt-link]')?.click();
  expect(onAction).toHaveBeenCalledWith('retry');
  expect(onLink).toHaveBeenCalledWith('https://x');
  expect(cards()[0]?.getAttribute('data-removed')).toBe('false');
});

test('a toast with its own position gets its own group', () => {
  store.configure({ position: 'top-center' });
  store.show(message('a'));
  store.show(message('b', { position: 'bottom-right' }));
  const groups = [
    ...document.querySelectorAll<HTMLElement>('[data-onekey-toast-toaster]'),
  ];
  expect(
    groups.map((group) => [
      group.dataset.yPosition,
      group.dataset.xPosition,
      group.children.length,
    ])
  ).toEqual([
    ['top', 'center', 1],
    ['bottom', 'right', 1],
  ]);
});

test('changing position keeps newest-first order across configuration and per-card moves', () => {
  ['a', 'b', 'c', 'd'].forEach((id) => store.show(message(id)));
  store.configure({ position: 'top-center' });
  expect(cards().map((card) => card.textContent)).toEqual([
    'Title d',
    'Title c',
    'Title b',
    'Title a',
  ]);
  expect(cards().map((card) => card.getAttribute('data-front'))).toEqual([
    'true',
    'false',
    'false',
    'false',
  ]);
  expect(cards().map((card) => card.getAttribute('data-visible'))).toEqual([
    'true',
    'true',
    'true',
    'false',
  ]);
  store.update(message('c', { position: 'bottom-right' }));
  store.update(message('c', { position: 'top-center' }));
  expect(cards().map((card) => card.textContent)).toEqual([
    'Title d',
    'Title c',
    'Title b',
    'Title a',
  ]);
});

test('a toast mounted after a lock overlay inherits inert isolation', () => {
  overlayStore.request({ id: 'lock-entry', level: 'lock', blocking: true });
  try {
    store.show(message('a'));
    const layer = document.querySelector<HTMLElement>(
      '[data-onekey-overlay-layer="toast"]'
    );
    expect(layer?.inert).toBe(true);
    document.dispatchEvent(
      new KeyboardEvent('keydown', { altKey: true, code: 'KeyT' })
    );
    expect(cards()[0]?.getAttribute('data-expanded')).toBe('false');
  } finally {
    overlayStore.dismiss('lock-entry', 'programmatic');
    overlayStore.finalize('lock-entry');
  }
  const layer = document.querySelector<HTMLElement>(
    '[data-onekey-overlay-layer="toast"]'
  );
  expect(layer?.inert).toBe(false);
});

test('top toasts rest under a top obstruction', () => {
  store.configure({ position: 'top-center', topObstruction: 120 });
  store.show(message('a'));
  const list = document.querySelector<HTMLElement>(
    '[data-onekey-toast-toaster]'
  );
  expect(list?.style.getPropertyValue('--toast-obstruction-top')).toBe('128px');
  store.configure({ topObstruction: 0 });
  expect(list?.style.getPropertyValue('--toast-obstruction-top')).toBe('');
});

test('with no renderer a dismiss removes the toast at once', () => {
  renderer.destroy();
  detach();
  const lone = new OverlayToastStore();
  const onClose = jest.fn();
  lone.show(message('a'), { onClose }).dismiss();
  expect(onClose).toHaveBeenCalledWith('programmatic');
  expect(lone.getSnapshot().items).toHaveLength(0);
});

test('the bridge encodes infinite durations and decodes events', () => {
  const lone = new OverlayToastStore();
  lone.show(message('a', { duration: Infinity }));
  const [item] = JSON.parse(serializeToastItems(lone.getSnapshot().items));
  expect(item.spec.duration).toBe(-1);
  expect(
    toToastEvent({ toastId: 'a', type: 'dismiss', value: 'swipe' })
  ).toEqual({ id: 'a', type: 'dismiss', reason: 'swipe' });
  expect(
    toToastEvent({
      toastId: 'a',
      generation: 3,
      type: 'dismiss',
      value: 'swipe',
    })
  ).toEqual({ id: 'a', generation: 3, type: 'dismiss', reason: 'swipe' });
  expect(
    toToastEvent({ toastId: 'a', type: 'nope', value: '' })
  ).toBeUndefined();
});

test('stale events from a previous generation are ignored after showing the same id again', () => {
  const lone = new OverlayToastStore();
  const detachLone = lone.attachRenderer();
  const firstDismiss = jest.fn();
  const firstClose = jest.fn();
  const firstAction = jest.fn();
  lone.show(message('a'), {
    onDismiss: firstDismiss,
    onClose: firstClose,
    onAction: firstAction,
  });
  const firstGen = lone.getSnapshot().items[0]!.generation;

  const secondDismiss = jest.fn();
  const secondClose = jest.fn();
  const secondAction = jest.fn();
  lone.show(message('a', { title: text('Second') }), {
    onDismiss: secondDismiss,
    onClose: secondClose,
    onAction: secondAction,
  });
  const secondGen = lone.getSnapshot().items[0]!.generation;
  expect(secondGen).toBeGreaterThan(firstGen);

  lone.handleEvent({
    id: 'a',
    generation: firstGen,
    type: 'action',
    actionId: 'retry',
  });
  lone.handleEvent({
    id: 'a',
    generation: firstGen,
    type: 'dismiss',
    reason: 'timeout',
  });
  lone.handleEvent({
    id: 'a',
    generation: firstGen,
    type: 'close',
    reason: 'timeout',
  });

  expect(firstAction).not.toHaveBeenCalled();
  expect(secondAction).not.toHaveBeenCalled();
  expect(secondDismiss).not.toHaveBeenCalled();
  expect(secondClose).not.toHaveBeenCalled();
  expect(lone.getSnapshot().items).toHaveLength(1);
  expect(lone.getSnapshot().items[0]?.visible).toBe(true);

  lone.handleEvent({
    id: 'a',
    generation: secondGen,
    type: 'dismiss',
    reason: 'swipe',
  });
  expect(secondDismiss).toHaveBeenCalledWith('swipe');
  lone.handleEvent({
    id: 'a',
    generation: firstGen,
    type: 'close',
    reason: 'timeout',
  });
  expect(secondClose).not.toHaveBeenCalled();
  expect(lone.getSnapshot().items).toHaveLength(1);

  lone.handleEvent({
    id: 'a',
    generation: secondGen,
    type: 'close',
    reason: 'swipe',
  });
  expect(secondClose).toHaveBeenCalledWith('swipe');
  expect(lone.getSnapshot().items).toHaveLength(0);
  detachLone();
});

test('detaching the last renderer during exit preserves the dismiss reason in onClose', () => {
  const lone = new OverlayToastStore();
  const detachLone = lone.attachRenderer();
  const onDismiss = jest.fn();
  const onClose = jest.fn();
  lone.show(message('a'), { onDismiss, onClose });
  const generation = lone.getSnapshot().items[0]!.generation;

  lone.handleEvent({
    id: 'a',
    generation,
    type: 'dismiss',
    reason: 'timeout',
  });
  expect(onDismiss).toHaveBeenCalledWith('timeout');
  expect(onClose).not.toHaveBeenCalled();

  detachLone();
  expect(onClose).toHaveBeenCalledWith('timeout');
  expect(lone.getSnapshot().items).toHaveLength(0);
});

test('removing a hovered position group releases hover and resumes remaining group timers', () => {
  const onBottomClose = jest.fn();
  store.show(message('top', { position: 'top-center', duration: Infinity }));
  store.show(message('bottom', { position: 'bottom-right', duration: 1000 }), {
    onClose: onBottomClose,
  });

  const topList = document.querySelector<HTMLElement>(
    '[data-onekey-toast-toaster][data-y-position="top"]'
  );
  expect(topList).not.toBeNull();
  dispatchPointer(topList!, 'pointerenter');
  expect(
    cards().every((card) => card.getAttribute('data-expanded') === 'true')
  ).toBe(true);

  jest.advanceTimersByTime(2000);
  expect(onBottomClose).not.toHaveBeenCalled();

  store.dismiss('top');
  jest.advanceTimersByTime(200);
  expect(
    document.querySelector('[data-onekey-toast-toaster][data-y-position="top"]')
  ).toBeNull();
  expect(cards()).toHaveLength(1);
  expect(cards()[0]?.getAttribute('data-expanded')).toBe('false');

  jest.advanceTimersByTime(1000);
  jest.advanceTimersByTime(200);
  expect(onBottomClose).toHaveBeenCalledWith('timeout');
  expect(cards()).toHaveLength(0);
});

test('pressable notification row is keyboard-focusable and activates on Enter and Space without intercepting links or buttons', () => {
  const onPress = jest.fn();
  const onAction = jest.fn();
  const onLink = jest.fn();
  store.show(
    {
      id: 'notif',
      layout: 'notification',
      duration: Infinity,
      pressable: true,
      badgeSize: 36,
      badgeColor: '#eee',
      icon: {
        kind: 'icon',
        name: 'BellOutline',
        color: '#111',
        size: 20,
      },
      title: text('Notification title'),
      message: {
        ...text(''),
        paragraphs: [[{ text: 'Read ' }, { text: 'more', link: 'https://x' }]],
      },
      actions: [
        {
          id: 'open',
          label: 'Open',
          testID: 'open-btn',
          color: '#fff',
          backgroundColor: '#000',
          fontSize: 14,
          lineHeight: 20,
          fontWeight: '500',
          paddingHorizontal: 10,
          paddingVertical: 4,
          borderRadius: 8,
        },
      ],
    },
    { onPress, onAction, onLink }
  );

  const row = document.querySelector<HTMLElement>('[data-okt-pressable]');
  expect(row).not.toBeNull();
  expect(row?.getAttribute('role')).toBe('button');
  expect(row?.tabIndex).toBe(0);

  row?.dispatchEvent(
    new KeyboardEvent('keydown', { key: 'Enter', bubbles: true })
  );
  row?.dispatchEvent(new KeyboardEvent('keydown', { key: ' ', bubbles: true }));
  expect(onPress).toHaveBeenCalledTimes(2);

  const link = document.querySelector<HTMLElement>('[data-okt-link]');
  link?.dispatchEvent(
    new KeyboardEvent('keydown', { key: 'Enter', bubbles: true })
  );
  expect(onLink).toHaveBeenCalledWith('https://x');
  expect(onPress).toHaveBeenCalledTimes(2);

  const button = document.querySelector<HTMLElement>(
    '[data-testid="open-btn"]'
  );
  button?.click();
  expect(onAction).toHaveBeenCalledWith('open');
  expect(onPress).toHaveBeenCalledTimes(2);
});

function dispatchPointer(
  target: EventTarget,
  type: string,
  init: Record<string, unknown> = {}
) {
  const { timeStamp = performance.now(), ...pointer } = init;
  const event = new Event(type, { bubbles: true, cancelable: true });
  Object.defineProperty(event, 'timeStamp', { value: timeStamp });
  Object.assign(event, {
    pointerId: 1,
    pointerType: 'mouse',
    button: 0,
    clientX: 0,
    clientY: 0,
    ...pointer,
  });
  target.dispatchEvent(event);
}

test('dragging out of a toast and releasing clears hover after resetting data-swiping so timers resume', () => {
  const onClose = jest.fn();
  store.show(message('a', { duration: 1000 }), { onClose });
  const list = document.querySelector<HTMLElement>(
    '[data-onekey-toast-toaster]'
  )!;
  const card = cards()[0]!;
  const outside = document.createElement('div');
  document.body.appendChild(outside);
  // While data-swiping="true", the scaled ::before pseudo-element covers 3x
  // the card height; once data-swiping="false" is restored on release, hit
  // testing lands on the page behind the card.
  document.elementFromPoint = jest.fn(() =>
    card.getAttribute('data-swiping') === 'true' ? card : outside
  );

  dispatchPointer(list, 'pointerenter');
  dispatchPointer(card, 'pointerdown', { clientX: 200, clientY: 200 });
  dispatchPointer(card, 'pointermove', { clientX: 215, clientY: 200 });
  expect(card.getAttribute('data-swiping')).toBe('true');
  jest.advanceTimersByTime(200);
  dispatchPointer(card, 'pointerup', { clientX: 215, clientY: 200 });

  expect(card.getAttribute('data-swiping')).toBe('false');
  expect(card.style.getPropertyValue('--swipe-amount')).toBe('0px');

  jest.advanceTimersByTime(1000);
  expect(card.getAttribute('data-removed')).toBe('true');
  jest.advanceTimersByTime(200);
  expect(onClose).toHaveBeenCalledWith('timeout');
});

test('horizontal swipe dismisses left or right on wide and narrow positions while mouse vertical drags do not', () => {
  const onWideClose = jest.fn();
  store.configure({ position: 'bottom-right' });
  store.show(message('wide', { duration: 5000 }), { onClose: onWideClose });
  const wideCard = cards()[0]!;

  // Vertical drag on bottom-right does not start a swipe or dismiss.
  dispatchPointer(wideCard, 'pointerdown', { clientX: 200, clientY: 200 });
  dispatchPointer(wideCard, 'pointermove', { clientX: 200, clientY: 280 });
  expect(wideCard.getAttribute('data-swiping')).toBe('false');
  dispatchPointer(wideCard, 'pointerup', { clientX: 200, clientY: 280 });
  expect(wideCard.getAttribute('data-removed')).toBe('false');

  // Horizontal swipe right after slight vertical jitter dismisses to the right.
  dispatchPointer(wideCard, 'pointerdown', { clientX: 200, clientY: 200 });
  dispatchPointer(wideCard, 'pointermove', { clientX: 201, clientY: 204 });
  expect(wideCard.style.getPropertyValue('--swipe-amount')).toBe('0px');
  dispatchPointer(wideCard, 'pointermove', { clientX: 268, clientY: 204 });
  expect(wideCard.style.getPropertyValue('--swipe-amount')).toBe('68px');
  dispatchPointer(wideCard, 'pointermove', { clientX: 272, clientY: 240 });
  expect(wideCard.style.getPropertyValue('--swipe-amount')).toBe('72px');
  dispatchPointer(wideCard, 'pointerup', { clientX: 272, clientY: 240 });
  expect(
    parseFloat(wideCard.style.getPropertyValue('--swipe-exit-x'))
  ).toBeGreaterThan(0);
  jest.advanceTimersByTime(200);
  expect(onWideClose).toHaveBeenCalledWith('swipe');

  const onNarrowClose = jest.fn();
  store.configure({ position: 'top-center' });
  store.show(message('narrow-1', { duration: 1000 }));
  store.show(message('narrow-2', { duration: 1000 }), {
    onClose: onNarrowClose,
  });
  const narrowFront = cards()[0]!;

  // Touch tap expands a multi-card stack on narrow screen and tapping outside collapses it.
  dispatchPointer(narrowFront, 'pointerdown', {
    pointerType: 'touch',
    clientX: 180,
    clientY: 40,
  });
  dispatchPointer(narrowFront, 'pointerup', {
    pointerType: 'touch',
    clientX: 180,
    clientY: 40,
  });
  expect(narrowFront.getAttribute('data-expanded')).toBe('true');
  dispatchPointer(document.body, 'pointerdown', {
    pointerType: 'touch',
    clientX: 10,
    clientY: 500,
  });
  expect(narrowFront.getAttribute('data-expanded')).toBe('false');

  // A short touch push toward the top edge moves the stack, then settles back.
  dispatchPointer(narrowFront, 'pointerdown', {
    pointerType: 'touch',
    clientX: 180,
    clientY: 90,
    timeStamp: 0,
  });
  dispatchPointer(narrowFront, 'pointermove', {
    pointerType: 'touch',
    clientX: 180,
    clientY: 78,
    timeStamp: 100,
  });
  expect(narrowFront.style.getPropertyValue('--swipe-amount')).toBe('0px');
  expect(narrowFront.getAttribute('data-stack-drag')).toBe('true');
  expect(narrowFront.style.getPropertyValue('--drag-y')).toBe('-12px');
  dispatchPointer(narrowFront, 'pointerup', {
    pointerType: 'touch',
    clientX: 180,
    clientY: 78,
    timeStamp: 200,
  });
  expect(narrowFront.getAttribute('data-removed')).toBe('false');
  expect(narrowFront.getAttribute('data-expanded')).toBe('false');
  expect(narrowFront.hasAttribute('data-stack-drag')).toBe(false);
  expect(narrowFront.style.getPropertyValue('--drag-y')).toBe('');

  // Horizontal swipe left dismisses to the left.
  dispatchPointer(narrowFront, 'pointerdown', {
    pointerType: 'touch',
    clientX: 180,
    clientY: 60,
  });
  dispatchPointer(narrowFront, 'pointermove', {
    pointerType: 'touch',
    clientX: 110,
    clientY: 60,
  });
  expect(narrowFront.style.getPropertyValue('--swipe-amount')).toBe('-70px');
  dispatchPointer(narrowFront, 'pointerup', {
    pointerType: 'touch',
    clientX: 110,
    clientY: 60,
  });
  expect(
    parseFloat(narrowFront.style.getPropertyValue('--swipe-exit-x'))
  ).toBeLessThan(0);
  jest.advanceTimersByTime(200);
  expect(onNarrowClose).toHaveBeenCalledWith('swipe');
});

function dispatchTouch(
  target: EventTarget,
  type: string,
  clientY: number,
  timeStamp: number
) {
  dispatchPointer(target, type, {
    pointerType: 'touch',
    clientX: 180,
    clientY,
    timeStamp,
  });
}

test('touch stack gestures follow the expanded state, not the toast count', () => {
  const closes: Record<string, string> = {};
  store.configure({ position: 'top-center' });
  ['s-1', 's-2', 's-3'].forEach((id) =>
    store.show(message(id, { duration: 1000 }), {
      onClose: (reason) => {
        closes[id] = reason;
      },
    })
  );
  const front = cards()[0]!;
  const back = cards()[1]!;

  // Mouse keeps the horizontal swipe only.
  dispatchPointer(front, 'pointerdown', { clientX: 180, clientY: 90 });
  dispatchPointer(front, 'pointermove', { clientX: 180, clientY: 10 });
  expect(front.hasAttribute('data-stack-drag')).toBe(false);
  dispatchPointer(front, 'pointerup', { clientX: 180, clientY: 10 });
  expect(front.getAttribute('data-removed')).toBe('false');
  // The mouse leaves, so hover no longer expands the stack.
  dispatchPointer(
    document.querySelector('[data-onekey-toast-toaster]')!,
    'pointerleave'
  );
  expect(front.getAttribute('data-expanded')).toBe('false');

  // Pulling a collapsed stack away from the top edge fans it out and expands it.
  dispatchTouch(front, 'pointerdown', 60, 1000);
  dispatchTouch(front, 'pointermove', 140, 1100);
  const frontPull = parseFloat(front.style.getPropertyValue('--drag-y'));
  const backPull = parseFloat(back.style.getPropertyValue('--drag-y'));
  expect(frontPull).toBeGreaterThan(0);
  expect(frontPull).toBeLessThan(80);
  expect(backPull).toBeGreaterThan(frontPull);
  dispatchTouch(front, 'pointerup', 140, 1300);
  expect(front.getAttribute('data-expanded')).toBe('true');
  expect(front.hasAttribute('data-stack-drag')).toBe(false);

  // Pushing an expanded stack toward the top edge collapses it.
  dispatchTouch(front, 'pointerdown', 60, 2000);
  dispatchTouch(front, 'pointermove', -20, 2100);
  expect(front.style.getPropertyValue('--drag-y')).toBe('-40px');
  dispatchTouch(front, 'pointerup', -20, 2300);
  expect(front.getAttribute('data-expanded')).toBe('false');
  expect(
    cards().every((card) => card.getAttribute('data-removed') === 'false')
  ).toBe(true);

  // A toast shown mid-drag waits; the drag dismisses only the cards it began with.
  dispatchTouch(front, 'pointerdown', 60, 3000);
  dispatchTouch(front, 'pointermove', 40, 3100);
  store.show(message('s-4', { duration: 1000 }));
  expect(cards()).toHaveLength(3);
  dispatchTouch(front, 'pointermove', -30, 3150);
  expect(back.style.getPropertyValue('--drag-y')).toBe('-90px');
  dispatchTouch(front, 'pointerup', -30, 3400);
  ['s-1', 's-2', 's-3'].forEach((id) => {
    const card = cards().find((entry) =>
      entry.textContent?.includes(`Title ${id}`)
    );
    expect(card?.getAttribute('data-removed')).toBe('true');
    expect(card?.getAttribute('data-swipe-axis')).toBe('y');
    expect(
      parseFloat(card?.style.getPropertyValue('--stack-exit-y') ?? '0')
    ).toBeLessThan(-90);
  });
  // Back cards keep their depth and hidden body while they leave.
  expect(back.getAttribute('data-front')).toBe('false');
  expect(back.getAttribute('data-expanded')).toBe('false');
  const added = cards().find((entry) =>
    entry.textContent?.includes('Title s-4')
  );
  expect(added?.getAttribute('data-removed')).toBe('false');
  expect(
    events
      .filter((event) => event.type === 'dismiss')
      .map((event) => [event.id, event.type === 'dismiss' && event.reason])
  ).toEqual([
    ['s-3', 'swipe'],
    ['s-2', 'swipe'],
    ['s-1', 'swipe'],
  ]);
  jest.advanceTimersByTime(200);
  expect(closes).toEqual({ 's-1': 'swipe', 's-2': 'swipe', 's-3': 'swipe' });
});

test('a single touch toast dismisses upward and a pull away from the edge settles back', () => {
  const onClose = jest.fn();
  store.configure({ position: 'top-center' });
  store.show(message('only', { duration: 1000 }), { onClose });
  const card = cards()[0]!;

  dispatchTouch(card, 'pointerdown', 60, 0);
  dispatchTouch(card, 'pointermove', 120, 100);
  expect(parseFloat(card.style.getPropertyValue('--drag-y'))).toBeLessThan(30);
  dispatchTouch(card, 'pointerup', 120, 400);
  expect(card.getAttribute('data-removed')).toBe('false');
  expect(card.getAttribute('data-expanded')).toBe('false');

  // A short flick toward the edge is enough.
  dispatchTouch(card, 'pointerdown', 60, 1000);
  dispatchTouch(card, 'pointermove', 48, 1010);
  dispatchTouch(card, 'pointermove', 36, 1020);
  dispatchTouch(card, 'pointerup', 36, 1030);
  expect(card.getAttribute('data-removed')).toBe('true');
  jest.advanceTimersByTime(200);
  expect(onClose).toHaveBeenCalledWith('swipe');
});

test('a bottom lane dismisses its touch stack downward', () => {
  store.configure({ position: 'bottom-center' });
  store.show(message('b-1', { duration: 1000 }));
  store.show(message('b-2', { duration: 1000 }));
  const front = cards()[0]!;

  dispatchTouch(front, 'pointerdown', 500, 0);
  dispatchTouch(front, 'pointermove', 580, 100);
  expect(front.style.getPropertyValue('--drag-y')).toBe('80px');
  dispatchTouch(front, 'pointerup', 580, 300);
  expect(
    cards().every((card) => card.getAttribute('data-removed') === 'true')
  ).toBe(true);
  expect(
    parseFloat(front.style.getPropertyValue('--stack-exit-y'))
  ).toBeGreaterThan(80);
});

test('only the last tracked press can resume timers', () => {
  const onClose = jest.fn();
  store.show(message('held', { duration: 1000 }), { onClose });
  const card = cards()[0]!;
  const firstTouch = { pointerId: 2, pointerType: 'touch' };
  const secondTouch = { pointerId: 3, pointerType: 'touch' };
  dispatchPointer(card, 'pointerdown', firstTouch);
  dispatchPointer(card, 'pointerdown', secondTouch);
  dispatchPointer(document.body, 'pointerup');
  dispatchPointer(card, 'pointercancel', secondTouch);
  jest.advanceTimersByTime(1200);
  expect(onClose).not.toHaveBeenCalled();

  dispatchPointer(card, 'pointerup', firstTouch);
  jest.advanceTimersByTime(1200);
  expect(onClose).toHaveBeenCalledWith('timeout');
});

test('an unfinished touch drag does not pause a single toast after release', () => {
  const onClose = jest.fn();
  store.show(message('held', { position: 'top-center', duration: 1000 }), {
    onClose,
  });
  const card = cards()[0]!;
  document.elementFromPoint = jest.fn(() => card);
  const touch = { pointerId: 2, pointerType: 'touch' };
  dispatchPointer(card, 'pointerenter', touch);
  dispatchPointer(card, 'pointerdown', touch);
  dispatchPointer(card, 'pointermove', { ...touch, clientY: 64 });
  dispatchPointer(card, 'pointerup', { ...touch, clientY: 64 });
  jest.advanceTimersByTime(1200);
  expect(onClose).toHaveBeenCalledWith('timeout');
});

test('dragging back within threshold cancels a recognized horizontal swipe', () => {
  store.show(message('a', { position: 'top-center' }));
  const card = cards()[0]!;
  dispatchPointer(card, 'pointerdown', { clientX: 200, clientY: 100 });
  dispatchPointer(card, 'pointermove', { clientX: 160, clientY: 100 });
  expect(card.getAttribute('data-swiping')).toBe('true');
  jest.advanceTimersByTime(200);
  dispatchPointer(card, 'pointermove', { clientX: 196, clientY: 100 });
  expect(card.style.getPropertyValue('--swipe-amount')).toBe('-4px');
  dispatchPointer(card, 'pointerup', { clientX: 196, clientY: 100 });
  expect(card.getAttribute('data-removed')).toBe('false');
  expect(card.style.getPropertyValue('--swipe-amount')).toBe('0px');
});

test('touch jitter on a collapsed back card still expands the stack and resumes timers', () => {
  const onBackClose = jest.fn();
  store.configure({ position: 'top-center' });
  store.show(message('back', { duration: 1000 }), { onClose: onBackClose });
  store.show(message('front', { duration: 1000 }));
  const backCard = cards()[1]!;
  document.elementFromPoint = jest.fn(() => backCard);

  dispatchPointer(backCard, 'pointerdown', {
    pointerType: 'touch',
    clientX: 180,
    clientY: 60,
  });
  dispatchPointer(backCard, 'pointermove', {
    pointerType: 'touch',
    clientX: 181,
    clientY: 60,
  });
  dispatchPointer(backCard, 'pointerup', {
    pointerType: 'touch',
    clientX: 181,
    clientY: 60,
  });

  expect(backCard.getAttribute('data-expanded')).toBe('true');
  // Collapse by tapping outside, then confirm timers are not frozen.
  dispatchPointer(document.body, 'pointerdown', {
    pointerType: 'touch',
    clientX: 10,
    clientY: 500,
  });
  expect(backCard.getAttribute('data-expanded')).toBe('false');
  jest.advanceTimersByTime(1200);
  expect(onBackClose).toHaveBeenCalledWith('timeout');
});

test('short horizontal flick dismisses even after a stationary hold', () => {
  const onClose = jest.fn();
  store.configure({ position: 'top-center' });
  store.show(message('held-flick', { duration: 5000 }), { onClose });
  const card = cards()[0]!;

  dispatchPointer(card, 'pointerdown', {
    pointerType: 'touch',
    clientX: 200,
    clientY: 60,
  });
  jest.advanceTimersByTime(600);
  dispatchPointer(card, 'pointermove', {
    pointerType: 'touch',
    clientX: 222,
    clientY: 60,
  });
  jest.advanceTimersByTime(15);
  dispatchPointer(card, 'pointermove', {
    pointerType: 'touch',
    clientX: 240,
    clientY: 60,
  });
  jest.advanceTimersByTime(15);
  dispatchPointer(card, 'pointerup', {
    pointerType: 'touch',
    clientX: 240,
    clientY: 60,
  });

  jest.advanceTimersByTime(200);
  expect(onClose).toHaveBeenCalledWith('swipe');
});

test.each([
  ['slow events delivered together', 108, 130, 660, false],
  ['fast reversal toward the origin', 149, 121, 620, false],
  ['fast motion toward the exit', 111, 138, 620, true],
] as const)(
  'short swipe uses event time and signed velocity for %s',
  (_, firstX, lastX, lastAt, dismisses) => {
    const onDismiss = jest.fn();
    store.show(message('timed-flick', { duration: 5000 }), { onDismiss });
    const card = cards()[0]!;
    dispatchPointer(card, 'pointerdown', { clientX: 100, timeStamp: 1 });
    dispatchPointer(card, 'pointermove', { clientX: firstX, timeStamp: 600 });
    dispatchPointer(card, 'pointermove', { clientX: lastX, timeStamp: lastAt });
    dispatchPointer(card, 'pointerup', {
      clientX: lastX,
      timeStamp: lastAt + 5,
    });
    expect(onDismiss.mock.calls).toEqual(dismisses ? [['swipe']] : []);
  }
);

test('a slow short drag after a stationary hold settles without dismissing', () => {
  const onDismiss = jest.fn();
  store.show(message('held-slow-drag'), { onDismiss });
  const card = cards()[0]!;

  dispatchPointer(card, 'pointerdown', {
    pointerType: 'touch',
    clientX: 200,
    clientY: 60,
  });
  jest.advanceTimersByTime(600);
  for (const clientX of [203, 206, 209, 230]) {
    jest.advanceTimersByTime(80);
    dispatchPointer(card, 'pointermove', {
      pointerType: 'touch',
      clientX,
      clientY: 60,
    });
  }
  expect(card.dataset.swiping).toBe('true');
  jest.advanceTimersByTime(10);
  dispatchPointer(card, 'pointerup', {
    pointerType: 'touch',
    clientX: 230,
    clientY: 60,
  });

  expect(onDismiss).not.toHaveBeenCalled();
  expect(card.dataset.removed).toBe('false');
  expect(card.style.getPropertyValue('--swipe-amount')).toBe('0px');
});

test('mouse drag inside selectable text preserves text selection without swiping the toast away', () => {
  const onClose = jest.fn();
  store.configure({ position: 'bottom-right' });
  store.show(
    message('selectable-toast', {
      duration: 1000,
      message: {
        ...text('0x71C...89A'),
        selectable: true,
      },
    }),
    { onClose }
  );
  const card = cards()[0]!;
  const selectable = card.querySelector<HTMLElement>('[data-okt-selectable]')!;
  expect(selectable).toBeTruthy();

  dispatchPointer(selectable, 'pointerdown', {
    pointerType: 'mouse',
    clientX: 100,
    clientY: 100,
  });
  dispatchPointer(selectable, 'pointermove', {
    pointerType: 'mouse',
    clientX: 190,
    clientY: 100,
  });
  expect(card.getAttribute('data-swiping')).toBe('false');
  // Press still pauses timer while selecting.
  jest.advanceTimersByTime(1500);
  expect(card.getAttribute('data-removed')).toBe('false');

  dispatchPointer(selectable, 'pointerup', {
    pointerType: 'mouse',
    clientX: 190,
    clientY: 100,
  });
  expect(card.getAttribute('data-removed')).toBe('false');
  expect(onClose).not.toHaveBeenCalled();

  // Timers resume once the mouse leaves the toast after releasing selection.
  dispatchPointer(card.parentElement!, 'pointerleave', {
    pointerType: 'mouse',
  });
  jest.advanceTimersByTime(1200);
  expect(onClose).toHaveBeenCalledWith('timeout');
});

test('vertical drag on a pressable notification suppresses the synthesized row click', () => {
  const onPress = jest.fn();
  store.configure({ position: 'top-center' });
  store.show(
    {
      id: 'notif-drag',
      layout: 'notification',
      pressable: true,
      duration: 5000,
      badgeSize: 36,
      badgeColor: '#eee',
      icon: { kind: 'icon', name: 'Bell', size: 18, color: '#111' },
      title: text('Transfer received'),
      message: text('1.0 ETH'),
    },
    { onPress }
  );
  const card = cards()[0]!;
  const row = card.querySelector<HTMLElement>('[data-okt-pressable]')!;

  // Vertical drag followed by synthesized click should not trigger onPress.
  dispatchPointer(row, 'pointerdown', {
    pointerType: 'mouse',
    clientX: 200,
    clientY: 80,
  });
  dispatchPointer(row, 'pointermove', {
    pointerType: 'mouse',
    clientX: 200,
    clientY: 50,
  });
  dispatchPointer(row, 'pointerup', {
    pointerType: 'mouse',
    clientX: 200,
    clientY: 50,
  });
  row.click();
  expect(onPress).not.toHaveBeenCalled();

  // A normal click afterwards still triggers onPress.
  dispatchPointer(row, 'pointerdown', {
    pointerType: 'mouse',
    clientX: 200,
    clientY: 80,
  });
  dispatchPointer(row, 'pointerup', {
    pointerType: 'mouse',
    clientX: 200,
    clientY: 80,
  });
  row.click();
  expect(onPress).toHaveBeenCalledTimes(1);
});

test('a leaving front card keeps its exit slot while survivors promote immediately', () => {
  store.show(message('oldest'));
  store.show(message('middle'));
  store.show(message('front'));
  const [front, middle, oldest] = cards();

  dispatchPointer(front!, 'pointerdown', { clientX: 120 });
  dispatchPointer(front!, 'pointermove', { clientX: 200 });
  dispatchPointer(front!, 'pointerup', { clientX: 200 });

  expect(front?.dataset.removed).toBe('true');
  expect(front?.dataset.index).toBe('0');
  expect(middle?.dataset.front).toBe('true');
  expect(middle?.dataset.index).toBe('0');
  expect(oldest?.dataset.index).toBe('1');
  expect(events.map(({ type }) => type)).toEqual(['dismiss']);

  jest.advanceTimersByTime(200);
  expect(events.map(({ type }) => type)).toEqual(['dismiss', 'close']);
});

test('collapsing during a middle-card drag cancels dismissal on release', () => {
  ['oldest', 'middle', 'front'].forEach((id) => store.show(message(id)));
  document.dispatchEvent(
    new KeyboardEvent('keydown', { altKey: true, code: 'KeyT' })
  );

  const middle = cards()[1]!;
  dispatchPointer(middle, 'pointerdown', {
    pointerType: 'touch',
    clientX: 120,
  });
  dispatchPointer(middle, 'pointermove', {
    pointerType: 'touch',
    clientX: 200,
  });
  expect(middle.dataset.swiping).toBe('true');
  dispatchPointer(document.body, 'pointerdown', {
    pointerType: 'touch',
    pointerId: 2,
  });
  expect(middle.dataset.expanded).toBe('false');
  expect(middle.dataset.swiping).toBe('false');
  dispatchPointer(middle, 'pointerup', {
    pointerType: 'touch',
    clientX: 200,
  });

  expect(middle.dataset.removed).toBe('false');
  expect(middle.style.getPropertyValue('--swipe-amount')).toBe('0px');
  expect(events).toEqual([]);
});
