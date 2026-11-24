/** @jest-environment jsdom */

import { act, createElement, Fragment } from 'react';
import { createPortal } from 'react-dom';
import { createRoot } from 'react-dom/client';

import { OverlayPageHost } from '../OverlayPageHost.web';
import { overlayStore } from '../OverlayStore';
import {
  ENTRY_ATTRIBUTE,
  getOverlayPageHost,
  registerOverlayDismissRequester,
  registerOverlayPageHost,
  scheduleOverlayInertSync,
  syncOverlayInert,
} from '../web/overlayLayers';

function entry(level: number, seq: number) {
  const element = document.createElement('div');
  element.setAttribute(ENTRY_ATTRIBUTE, '');
  element.dataset.overlayLevelOrder = String(level);
  element.dataset.stackOrder = String(seq);
  element.appendChild(document.createElement('button'));
  return element;
}

beforeEach(() => {
  document.body.innerHTML = '<div id="root"></div>';
  (
    globalThis as { IS_REACT_ACT_ENVIRONMENT?: boolean }
  ).IS_REACT_ACT_ENVIRONMENT = true;
  jest.spyOn(window, 'requestAnimationFrame').mockImplementation((callback) => {
    callback(0);
    return 1;
  });
});

afterEach(() => {
  for (const item of [
    ...overlayStore.getSnapshot().entries,
    ...overlayStore.getSnapshot().queued,
  ]) {
    overlayStore.finalize(item.id);
  }
  overlayStore.setFocusedPageHost(undefined);
  jest.restoreAllMocks();
});

it('hides a late portal child whose owner was already suspended, then restores it', async () => {
  overlayStore.request({
    id: 'late-page',
    scope: 'page',
    hostKey: 'late-host',
    ownerKey: 'covered-owner',
  });
  overlayStore.setPageVisible('covered-owner', false);
  const root = createRoot(document.getElementById('root')!);
  await act(async () => {
    root.render(
      createElement(
        Fragment,
        null,
        createElement(OverlayPageHost, { hostKey: 'late-host' })
      )
    );
  });
  const host = getOverlayPageHost('late-host')!;
  await act(async () => {
    root.render(
      createElement(
        Fragment,
        null,
        createElement(OverlayPageHost, { hostKey: 'late-host' }),
        createPortal(
          createElement(
            'div',
            { 'data-overlay-owner': 'covered-owner', [ENTRY_ATTRIBUTE]: '' },
            createElement('button', null, 'Late page action')
          ),
          host
        )
      )
    );
  });
  const late = host.querySelector<HTMLElement>(`[${ENTRY_ATTRIBUTE}]`)!;
  expect(late.style.visibility).toBe('hidden');
  expect(late.inert).toBe(true);
  await act(async () => overlayStore.setPageVisible('covered-owner', true));
  expect(late.style.visibility).toBe('');
  expect(late.inert).toBe(false);
  await act(async () => root.unmount());
});

it('isolates a newer modal below an older secure page entry by level', () => {
  const app = document.getElementById('root')!;
  const content = document.createElement('button');
  const host = document.createElement('div');
  app.append(content, host);
  const unregister = registerOverlayPageHost('level-host', host);
  const secure = overlayStore.request({
    id: 'page-secure',
    scope: 'page',
    hostKey: 'level-host',
    ownerKey: 'page',
    level: 'secure',
  });
  const modal = overlayStore.request({
    id: 'newer-modal',
    scope: 'page',
    hostKey: 'level-host',
    ownerKey: 'page',
    level: 'modal',
  });
  const secureElement = entry(300, secure.seq);
  const modalElement = entry(100, modal.seq);
  host.append(modalElement, secureElement);
  scheduleOverlayInertSync();
  expect(modalElement.inert).toBe(true);
  expect(secureElement.inert).toBe(false);
  expect(content.inert).toBe(true);
  expect(host.inert).not.toBe(true);
  overlayStore.finalize(secure.id);
  expect(modalElement.inert).toBe(false);
  unregister();
});

it('restores a non-blocking page entry when the blocker is removed', () => {
  const app = document.getElementById('root')!;
  const content = document.createElement('button');
  const host = document.createElement('div');
  app.append(content, host);
  const unregister = registerOverlayPageHost('restore-host', host);
  const passive = overlayStore.request({
    id: 'passive',
    scope: 'page',
    hostKey: 'restore-host',
    ownerKey: 'page',
    blocking: false,
  });
  const blocker = overlayStore.request({
    id: 'blocker',
    scope: 'page',
    hostKey: 'restore-host',
    ownerKey: 'page',
  });
  const passiveElement = entry(100, passive.seq);
  host.append(passiveElement, entry(100, blocker.seq));
  scheduleOverlayInertSync();
  expect(passiveElement.inert).toBe(true);
  overlayStore.finalize(blocker.id);
  expect(passiveElement.inert).toBe(false);
  expect(content.inert).toBe(false);
  unregister();
});

it('skips a disconnected top host in favor of the next connected blocker', () => {
  const app = document.getElementById('root')!;
  const content = document.createElement('button');
  const lowerHost = document.createElement('div');
  const upperHost = document.createElement('div');
  app.append(content, lowerHost, upperHost);
  const unregisterLower = registerOverlayPageHost('lower', lowerHost);
  const unregisterUpper = registerOverlayPageHost('upper', upperHost);
  const lower = overlayStore.request({
    id: 'lower-dialog',
    scope: 'page',
    hostKey: 'lower',
    ownerKey: 'lower-page',
  });
  const upper = overlayStore.request({
    id: 'upper-dialog',
    scope: 'page',
    hostKey: 'upper',
    ownerKey: 'upper-page',
    level: 'secure',
  });
  lowerHost.append(entry(100, lower.seq));
  upperHost.append(entry(300, upper.seq));
  expect(lowerHost.inert).toBe(true);
  expect(overlayStore.getBlockingTop()?.id).toBe(upper.id);

  upperHost.remove();
  syncOverlayInert();
  expect(overlayStore.getBlockingTop()?.id).toBe(lower.id);
  expect(overlayStore.resolveBack()).toEqual({ kind: 'dismiss', id: lower.id });
  expect(app.inert).toBe(false);
  expect(content.inert).toBe(true);
  expect(lowerHost.inert).toBe(false);

  unregisterUpper();
  unregisterLower();
  expect(overlayStore.resolveBack()).toEqual({ kind: 'pass' });
  expect(content.inert).toBe(false);
});

it('keeps a covered root overlay visible and restores isolation and Escape when its route regains focus', () => {
  const app = document.getElementById('root')!;
  const mainHost = document.createElement('div');
  const modalHost = document.createElement('div');
  app.append(mainHost, modalHost);
  overlayStore.setFocusedPageHost('modal');
  const unregisterMain = registerOverlayPageHost('main', mainHost);
  const unregisterModal = registerOverlayPageHost('modal', modalHost);
  const dialog = overlayStore.request({
    id: 'main-dialog',
    scope: 'page',
    hostKey: 'main',
    ownerKey: 'main-screen',
  });
  const dialogElement = entry(100, dialog.seq);
  mainHost.append(dialogElement);
  const dismiss = jest.fn();
  const unregisterDismiss = registerOverlayDismissRequester(dialog.id, dismiss);
  const pressEscape = () =>
    document.dispatchEvent(
      new KeyboardEvent('keydown', {
        key: 'Escape',
        cancelable: true,
      })
    );

  expect(mainHost.inert).toBe(true);
  expect(modalHost.inert).toBe(false);
  expect(dialogElement.style.visibility).toBe('');
  expect(pressEscape()).toBe(true);
  expect(dismiss).not.toHaveBeenCalled();

  overlayStore.setFocusedPageHost('main');
  expect(mainHost.inert).toBe(false);
  expect(modalHost.inert).toBe(true);
  expect(pressEscape()).toBe(false);
  expect(dismiss).toHaveBeenCalledWith('back');

  overlayStore.setFocusedPageHost(null);
  expect(mainHost.inert).toBe(true);
  expect(modalHost.inert).toBe(true);
  expect(pressEscape()).toBe(true);

  overlayStore.setFocusedPageHost(undefined);
  expect(mainHost.inert).toBe(false);
  expect(modalHost.inert).toBe(true);
  unregisterDismiss();
  overlayStore.finalize(dialog.id);
  unregisterModal();
  unregisterMain();
});
