/** @jest-environment jsdom */

import { act, createElement, Fragment } from 'react';
import type { ReactNode } from 'react';
import { createRoot } from 'react-dom/client';
import type { Root } from 'react-dom/client';

import { OverlayPageHost } from '../OverlayPageHost.web';
import { overlayStore } from '../OverlayStore';
import { OverlayView } from '../OverlayView.web';
import type { IOverlayViewProps } from '../OverlayViewTypes';

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

let root: Root;
beforeEach(() => {
  // The layer manager retains its roots; keep their DOM nodes connected
  // between cases, while each React root is fully unmounted below.
  if (!document.getElementById('root')) {
    document.body.innerHTML =
      '<button id="opener">Open</button><div id="root"></div>';
  }
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
  ]) {
    overlayStore.finalize(entry.id);
  }
  jest.restoreAllMocks();
});

const buttons = () =>
  ['first', 'middle', 'last'].map((id) =>
    createElement('button', { 'key': id, 'data-action': id }, id)
  );
const overlay = (
  props: Partial<IOverlayViewProps> = {},
  content: ReactNode = buttons()
) =>
  createElement(
    OverlayView,
    { visible: true, testID: 'overlay', ...props },
    content
  );
const render = async (content: ReactNode) =>
  act(async () => root.render(content));
const entry = (id = 'overlay') =>
  document.querySelector<HTMLElement>(`[data-testid="${id}"]`)!;
const action = (id: string, testID = 'overlay') =>
  entry(testID).querySelector<HTMLElement>(`[data-action="${id}"]`)!;
const tab = (target: HTMLElement, shiftKey = false) => {
  const event = new KeyboardEvent('keydown', {
    key: 'Tab',
    shiftKey,
    bubbles: true,
    cancelable: true,
  });
  target.dispatchEvent(event);
  return event.defaultPrevented;
};

it('wraps Tab and Shift+Tab at the boundaries and leaves interior Tab native', async () => {
  await render(overlay());
  action('last').focus();
  expect(tab(action('last'))).toBe(true);
  expect(document.activeElement).toBe(action('first'));
  expect(tab(action('first'), true)).toBe(true);
  expect(document.activeElement).toBe(action('last'));
  action('middle').focus();
  expect(tab(action('middle'))).toBe(false);
  expect(tab(action('middle'), true)).toBe(false);
});

it.each([false, true])(
  'enters the children from the root (shift=%s)',
  async (shift) => {
    await render(overlay());
    expect(document.activeElement).toBe(entry());
    expect(tab(entry(), shift)).toBe(true);
    expect(document.activeElement).toBe(action(shift ? 'last' : 'first'));
  }
);

it.each([false, true])(
  'keeps focus on an empty dialog root (shift=%s)',
  async (shift) => {
    await render(overlay({}, []));
    expect(tab(entry(), shift)).toBe(true);
    expect(document.activeElement).toBe(entry());
  }
);

it('loops a single tabbable child in both directions', async () => {
  await render(
    overlay({}, [
      createElement('input', { 'key': 'only', 'data-action': 'only' }),
    ])
  );
  action('only').focus();
  expect(tab(action('only'))).toBe(true);
  expect(document.activeElement).toBe(action('only'));
  expect(tab(action('only'), true)).toBe(true);
  expect(document.activeElement).toBe(action('only'));
});

it('excludes disabled, negative-tabindex, hidden and inert descendants', async () => {
  await render(
    overlay({}, [
      createElement('button', { key: 'disabled', disabled: true }, 'Disabled'),
      createElement('button', { key: 'negative', tabIndex: -1 }, 'Negative'),
      createElement(
        'div',
        { key: 'hidden', hidden: true },
        createElement('button', null, 'Hidden')
      ),
      createElement(
        'div',
        { key: 'inert', inert: true },
        createElement('button', null, 'Inert')
      ),
      createElement(
        'div',
        { 'key': 'aria', 'aria-hidden': true },
        createElement('button', null, 'Aria hidden')
      ),
      createElement(
        'div',
        { key: 'display', style: { display: 'none' } },
        createElement('button', null, 'Display none')
      ),
      createElement(
        'div',
        { key: 'visibility', style: { visibility: 'hidden' } },
        createElement('button', null, 'Visibility hidden')
      ),
      createElement(
        'fieldset',
        { key: 'fieldset', disabled: true },
        createElement('button', null, 'Disabled fieldset')
      ),
      createElement(
        'button',
        { 'key': 'valid', 'data-action': 'valid' },
        'Valid'
      ),
      createElement('input', { key: 'hidden-input', type: 'hidden' }),
    ])
  );
  expect(tab(entry())).toBe(true);
  expect(document.activeElement).toBe(action('valid'));
  expect(tab(action('valid'), true)).toBe(true);
  expect(document.activeElement).toBe(action('valid'));
});

it('orders positive tabindex before ordinary DOM-order tab stops', async () => {
  await render(
    overlay({}, [
      createElement('button', { 'key': 'zero', 'data-action': 'zero' }, 'Zero'),
      createElement(
        'button',
        { 'key': 'two', 'tabIndex': 2, 'data-action': 'two' },
        'Two'
      ),
      createElement(
        'button',
        { 'key': 'one', 'tabIndex': 1, 'data-action': 'one' },
        'One'
      ),
    ])
  );
  expect(tab(entry())).toBe(true);
  expect(document.activeElement).toBe(action('one'));
  expect(tab(action('one'), true)).toBe(true);
  expect(document.activeElement).toBe(action('zero'));
  expect(tab(action('zero'))).toBe(true);
  expect(document.activeElement).toBe(action('one'));
});

it('only traps the top blocking entry and leaves a non-blocking overlay alone', async () => {
  await render(
    createElement(
      Fragment,
      null,
      overlay({ testID: 'lower' }),
      overlay({ testID: 'upper', level: 'secure' }),
      overlay({ testID: 'passive', level: 'toast', blocking: false })
    )
  );
  action('last', 'lower').focus();
  expect(tab(action('last', 'lower'))).toBe(false);
  action('last', 'passive').focus();
  expect(tab(action('last', 'passive'))).toBe(false);
  action('last', 'upper').focus();
  expect(tab(action('last', 'upper'))).toBe(true);
  expect(document.activeElement).toBe(action('first', 'upper'));
});

it('traps a page overlay only while its owner is visible', async () => {
  const ownerKey = 'focus-owner';
  await render(
    createElement(
      Fragment,
      null,
      createElement(OverlayPageHost, { hostKey: 'focus-host' }),
      overlay({ scope: 'page', hostKey: 'focus-host', ownerKey })
    )
  );
  action('last').focus();
  expect(tab(action('last'))).toBe(true);
  await act(async () => overlayStore.setPageVisible(ownerKey, false));
  expect(tab(action('last'))).toBe(false);
  await act(async () => overlayStore.setPageVisible(ownerKey, true));
  action('last').focus();
  expect(tab(action('last'))).toBe(true);
  expect(document.activeElement).toBe(action('first'));
});

it('ignores a closing entry before its exit animation finalizes it', async () => {
  await render(overlay());
  const node = entry();
  const id = overlayStore.getBlockingTop()!.id;
  await act(async () => {
    overlayStore.dismiss(id, 'back');
    expect(tab(node)).toBe(false);
  });
});

it('leaves parked content inert and restores focus after closing', async () => {
  const opener = document.getElementById('opener')!;
  opener.focus();
  await render(overlay({ keepContentMounted: true }));
  expect(document.activeElement).toBe(entry());
  await render(overlay({ keepContentMounted: true, visible: false }));
  expect(document.activeElement).toBe(opener);
  expect(entry().inert).toBe(true);
  expect(tab(entry())).toBe(false);
});

it('preserves Escape dismissal', async () => {
  const onRequestDismiss = jest.fn();
  await render(overlay({ onRequestDismiss }));
  await act(async () => {
    action('first').dispatchEvent(
      new KeyboardEvent('keydown', {
        key: 'Escape',
        bubbles: true,
        cancelable: true,
      })
    );
  });
  expect(onRequestDismiss).toHaveBeenCalledWith('back');
});
