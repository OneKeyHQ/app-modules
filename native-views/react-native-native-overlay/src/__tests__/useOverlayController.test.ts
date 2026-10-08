/** @jest-environment jsdom */

import { act, createElement } from 'react';
import type { ReactNode } from 'react';
import { createRoot } from 'react-dom/client';
import type { Root } from 'react-dom/client';

import {
  OverlayPageHostScope,
  OverlayPageOwnerScope,
} from '../OverlayPageScope';
import { overlayStore } from '../OverlayStore';
import { useOverlayController } from '../useOverlayController';
import type { IOverlayController } from '../useOverlayController';
import type { IOverlayViewProps } from '../OverlayViewTypes';

let root: Root;
let controller: IOverlayController;

function Probe(props: IOverlayViewProps) {
  controller = useOverlayController(props);
  return null;
}

beforeEach(() => {
  (
    globalThis as { IS_REACT_ACT_ENVIRONMENT?: boolean }
  ).IS_REACT_ACT_ENVIRONMENT = true;
  document.body.innerHTML = '<div id="root"></div>';
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
});

const render = async (node: ReactNode) => act(async () => root.render(node));

it.each([
  {},
  { hostKey: 'host' },
  { ownerKey: 'owner' },
  { hostKey: '', ownerKey: 'owner' },
  { hostKey: 'host', ownerKey: '' },
])('waits inside page scope when keys are unavailable: %j', async (keys) => {
  await render(createElement(Probe, { visible: true, scope: 'page', ...keys }));
  expect(controller.scope).toBe('page');
  expect(controller.mounted).toBe(false);
  expect(controller.presented).toBe(false);
  expect(overlayStore.getSnapshot().entries).toEqual([]);
  expect(overlayStore.getSnapshot().queued).toEqual([]);
});

it('requests once when explicit page keys become ready while visible', async () => {
  await render(
    createElement(Probe, { visible: true, scope: 'page', hostKey: 'host' })
  );
  await render(
    createElement(Probe, {
      visible: true,
      scope: 'page',
      hostKey: 'host',
      ownerKey: 'owner',
    })
  );
  const id = controller.entry?.id;
  expect(controller.entry).toMatchObject({
    scope: 'page',
    hostKey: 'host',
    ownerKey: 'owner',
  });
  expect(controller.presented).toBe(true);
  await render(
    createElement(Probe, {
      visible: true,
      scope: 'page',
      hostKey: 'host',
      ownerKey: 'owner',
    })
  );
  expect(controller.entry?.id).toBe(id);
  expect(overlayStore.getSnapshot().entries).toHaveLength(1);
  await act(async () => overlayStore.setPageVisible('owner', false));
  expect(controller.entry?.suspended).toBe(true);
  await act(async () => overlayStore.removePage('owner'));
  expect(controller.entry).toMatchObject({
    phase: 'closing',
    dismissReason: 'page-removed',
  });
  expect(controller.presented).toBe(false);
  await act(async () => controller.onHostDismissed());
  expect(controller.entry).toBeUndefined();
});

it('waits for page keys supplied by the actual scope providers', async () => {
  const tree = (hostKey: string, ownerKey: string) =>
    createElement(
      OverlayPageHostScope,
      { hostKey },
      createElement(
        OverlayPageOwnerScope,
        { ownerKey },
        createElement(Probe, { visible: true, scope: 'page' })
      )
    );
  await render(tree('', ''));
  expect(overlayStore.getSnapshot().entries).toEqual([]);
  await render(tree('host', ''));
  expect(overlayStore.getSnapshot().entries).toEqual([]);
  await render(tree('host', 'owner'));
  expect(controller.entry).toMatchObject({
    scope: 'page',
    hostKey: 'host',
    ownerKey: 'owner',
  });
});

it('does not open or close an entry when hidden before page readiness', async () => {
  const onClose = jest.fn();
  await render(createElement(Probe, { visible: true, scope: 'page', onClose }));
  await render(
    createElement(Probe, {
      visible: false,
      scope: 'page',
      hostKey: 'host',
      ownerKey: 'owner',
      onClose,
    })
  );
  expect(overlayStore.getSnapshot().entries).toEqual([]);
  expect(onClose).not.toHaveBeenCalled();
});

it('does not create an entry or call onClose on unmount before readiness', async () => {
  const onClose = jest.fn();
  await render(createElement(Probe, { visible: true, scope: 'page', onClose }));
  await render(null);
  expect(overlayStore.getSnapshot().entries).toEqual([]);
  expect(onClose).not.toHaveBeenCalled();
});

it('still presents a default global overlay without page keys', async () => {
  await render(createElement(Probe, { visible: true }));
  expect(controller.scope).toBe('global');
  expect(controller.presented).toBe(true);
  expect(controller.entry?.scope).toBe('global');
});
