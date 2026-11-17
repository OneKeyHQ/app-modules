/** @jest-environment jsdom */

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
  list?.dispatchEvent(new MouseEvent('mouseenter'));
  expect(cards()[0]?.getAttribute('data-expanded')).toBe('true');
  jest.advanceTimersByTime(5000);
  expect(cards()).toHaveLength(2);
  list?.dispatchEvent(new MouseEvent('mouseleave'));
  jest.advanceTimersByTime(999);
  expect(cards().every((card) => card.dataset.removed === 'false')).toBe(true);
  jest.advanceTimersByTime(1);
  expect(cards().every((card) => card.dataset.removed === 'true')).toBe(true);
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

test('top toasts rest under a top obstruction', () => {
  store.configure({ position: 'top-center', topObstruction: 120 });
  store.show(message('a'));
  const list = document.querySelector<HTMLElement>(
    '[data-onekey-toast-toaster]'
  );
  expect(list?.style.top).toBe('128px');
  store.configure({ topObstruction: 0 });
  expect(list?.style.top).toBe('');
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
    toToastEvent({ toastId: 'a', type: 'nope', value: '' })
  ).toBeUndefined();
});
