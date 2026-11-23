/* cspell:ignore dasharray dashoffset */
import { getOverlayLayerRoot, syncOverlayInert } from '../web/overlayLayers';

import { buildToastContent, layoutMessageAction } from './toastDomContent.web';
import { TOAST_GEOMETRY, TOAST_MOTION } from './toastMotion';
import { stopStatusTile } from './toastStatusTile.web';
import { ensureOverlayToastStyles } from './toastStyles.web';

import type {
  IOverlayToastDismissReason,
  IOverlayToastEvent,
  IOverlayToastItem,
  IOverlayToastPosition,
  IOverlayToastRenderer,
  IOverlayToastSpec,
  IOverlayToasterConfig,
} from './types';

const VISIBLE_TOASTS = 3;
const VIEWPORT_OFFSET = 32;
const TOAST_WIDTH = 356;
const GAP = TOAST_GEOMETRY.stackGap;
const SWIPE_THRESHOLD = 20;
const SWIPE_VELOCITY = 0.11; // px per ms
const TOUCH_START_THRESHOLD = 10;
const POINTER_START_THRESHOLD = 2;
const TIME_BEFORE_UNMOUNT = TOAST_MOTION.cardExitMs;
const DEFAULT_POSITION: IOverlayToastPosition = 'bottom-right';
const DEFAULT_OBSTRUCTION_GAP = 8;
const DEFAULT_OBSTRUCTION_ROOM = 88;
const OBSTRUCTION_SHIFT_MS = 220;
const SVG_NS = 'http://www.w3.org/2000/svg';

interface IToastNode {
  id: string;
  spec: IOverlayToastSpec;
  revision: number;
  generation: number;
  position: IOverlayToastPosition;
  li: HTMLLIElement;
  shell: HTMLElement;
  surface: HTMLElement;
  body: HTMLElement;
  content: HTMLElement;
  mounted: boolean;
  removed: boolean;
  swiping: boolean;
  swipeOut: boolean;
  offsetBeforeRemove: number;
  initialHeight: number;
  closeTimer?: ReturnType<typeof setTimeout>;
  unmountTimer?: ReturnType<typeof setTimeout>;
  swipePointerId?: number;
}

interface IToastPress {
  node: IToastNode;
  x: number;
  y: number;
  startedAt: number;
  moved: boolean;
}

interface IToastGroup {
  position: IOverlayToastPosition;
  ol: HTMLOListElement;
  /** Newest first, leaving cards included until they unmount. */
  toasts: IToastNode[];
  /** Newest first, without leaving cards. */
  heights: { id: string; height: number }[];
}

function getDocumentDirection(): 'ltr' | 'rtl' {
  const attribute = document.documentElement.getAttribute('dir');
  if (attribute === 'rtl' || attribute === 'ltr') {
    return attribute;
  }
  return globalThis.getComputedStyle(document.documentElement).direction ===
    'rtl'
    ? 'rtl'
    : 'ltr';
}

function createCloseButton(label: string): HTMLButtonElement {
  const button = document.createElement('button');
  button.type = 'button';
  button.setAttribute('aria-label', label);
  button.setAttribute('data-close-button', '');
  const svg = document.createElementNS(SVG_NS, 'svg');
  const attributes: Record<string, string> = {
    'width': '10',
    'height': '10',
    'viewBox': '0 0 24 24',
    'fill': 'none',
    'stroke': 'currentColor',
    'stroke-width': '3',
    'stroke-linecap': 'round',
    'aria-hidden': 'true',
  };
  Object.entries(attributes).forEach(([name, value]) =>
    svg.setAttribute(name, value)
  );
  const path = document.createElementNS(SVG_NS, 'path');
  path.setAttribute('d', 'M7 7l10 10M17 7L7 17');
  svg.append(path);
  button.append(svg);
  return button;
}

/**
 * Draws message toasts with plain DOM in the `toast` overlay layer. No React:
 * the cards, their ring/shadow chrome, stacking, timers, swipe and spring
 * transitions live here.
 */
export function createToastDomRenderer(
  report: (event: IOverlayToastEvent) => void
): IOverlayToastRenderer {
  ensureOverlayToastStyles();

  let syncing = false;
  const pendingEvents: IOverlayToastEvent[] = [];
  const onEvent = (event: IOverlayToastEvent) => {
    if (syncing) {
      pendingEvents.push(event);
      return;
    }
    report(event);
  };

  const nodes = new Map<string, IToastNode>();
  const groups = new Map<IOverlayToastPosition, IToastGroup>();
  const presses = new Map<number, IToastPress>();
  const hoveredGroups = new Map<number, IToastGroup>();
  let config: IOverlayToasterConfig = {};
  let section: HTMLElement | null = null;
  let keyboardExpanded = false;
  let touchExpandedGroup: IToastGroup | undefined;
  let destroyed = false;

  const isExpanded = (): boolean =>
    nodes.size > 1 &&
    (keyboardExpanded || !!touchExpandedGroup || hoveredGroups.size > 0);

  const positionOf = (spec: IOverlayToastSpec): IOverlayToastPosition =>
    spec.position ?? config.position ?? DEFAULT_POSITION;

  const motionPreference = globalThis.matchMedia?.(
    '(prefers-reduced-motion: reduce)'
  );
  const reduceMotion = () =>
    config.respectReducedMotion !== false && motionPreference?.matches;

  function ensureSection(): HTMLElement {
    const root = getOverlayLayerRoot('toast');
    if (!section) {
      section = document.createElement('section');
      section.setAttribute('aria-label', 'Notifications alt+T');
      section.setAttribute('data-onekey-toast-section', '');
      section.tabIndex = -1;
    }
    if (config.testID) {
      section.setAttribute('data-testid', config.testID);
    }
    if (section.parentElement !== root) {
      root.prepend(section);
      syncOverlayInert();
    }
    return section;
  }

  function groupContaining(target: Node | null): IToastGroup | undefined {
    return [...groups.values()].find((group) => group.ol.contains(target));
  }

  function pointerHit(event: PointerEvent): Node | null {
    return typeof document.elementFromPoint === 'function'
      ? document.elementFromPoint(event.clientX, event.clientY)
      : (event.target as Node | null);
  }

  function ensureGroup(position: IOverlayToastPosition): IToastGroup {
    let group = groups.get(position);
    if (!group) {
      const ol = document.createElement('ol');
      ol.tabIndex = -1;
      ol.setAttribute('data-onekey-toast-toaster', '');
      const nextGroup: IToastGroup = {
        position,
        ol,
        toasts: [],
        heights: [],
      };
      const hover = (event: PointerEvent) => {
        if (
          event.pointerType !== 'touch' &&
          !presses.has(event.pointerId) &&
          hoveredGroups.get(event.pointerId) !== nextGroup
        ) {
          hoveredGroups.set(event.pointerId, nextGroup);
          update();
        }
      };
      ol.addEventListener('pointerenter', hover);
      ol.addEventListener('pointermove', hover);
      ol.addEventListener('pointerleave', (event) => {
        if (
          !presses.has(event.pointerId) &&
          hoveredGroups.get(event.pointerId) === nextGroup
        ) {
          hoveredGroups.delete(event.pointerId);
          update();
        }
      });
      group = nextGroup;
      groups.set(position, group);
    }
    if (group.ol.parentElement !== ensureSection()) {
      ensureSection().append(group.ol);
    }
    return group;
  }

  function offsetOf(group: IToastGroup, node: IToastNode): number {
    const heightIndex = Math.max(
      group.heights.findIndex((height) => height.id === node.id),
      0
    );
    const heightsBefore = group.heights
      .slice(0, heightIndex)
      .reduce((sum, height) => sum + height.height, 0);
    return heightIndex * GAP + heightsBefore;
  }

  function measure(node: IToastNode) {
    const group = groups.get(node.position);
    if (!group || node.removed) {
      return;
    }
    const { style } = node.li;
    const originalHeight = style.height;
    style.height = 'auto';
    layoutMessageAction(node.content);
    const { offsetHeight: height } = node.li;
    style.height = originalHeight;
    node.initialHeight = height;
    const existing = group.heights.find((entry) => entry.id === node.id);
    if (existing) {
      existing.height = height;
    } else {
      group.heights.unshift({ id: node.id, height });
    }
  }

  function clearCloseTimer(node: IToastNode) {
    if (node.closeTimer !== undefined) {
      clearTimeout(node.closeTimer);
      node.closeTimer = undefined;
    }
  }

  function syncTimers() {
    const paused = isExpanded() || hoveredGroups.size > 0 || presses.size > 0;
    nodes.forEach((node) => {
      if (node.removed || !Number.isFinite(node.spec.duration)) {
        clearCloseTimer(node);
        return;
      }
      if (paused) {
        clearCloseTimer(node);
        return;
      }
      if (node.closeTimer === undefined) {
        node.closeTimer = setTimeout(() => {
          node.closeTimer = undefined;
          deleteToast(node, 'timeout', true);
        }, node.spec.duration);
      }
    });
  }

  function unmount(node: IToastNode, reason: IOverlayToastDismissReason) {
    clearNodePresses(node);
    if (node.unmountTimer !== undefined) {
      clearTimeout(node.unmountTimer);
      node.unmountTimer = undefined;
    }
    clearCloseTimer(node);
    node.li.remove();
    const group = groups.get(node.position);
    if (group) {
      group.toasts = group.toasts.filter((entry) => entry !== node);
      group.heights = group.heights.filter((entry) => entry.id !== node.id);
    }
    if (nodes.get(node.id) === node) {
      nodes.delete(node.id);
    }
    onEvent({
      id: node.id,
      generation: node.generation,
      type: 'close',
      reason,
    });
  }

  function deleteToast(
    node: IToastNode,
    reason: IOverlayToastDismissReason,
    notify: boolean
  ) {
    if (node.removed) {
      return;
    }
    clearNodePresses(node);
    const group = groups.get(node.position);
    node.removed = true;
    const statusTile = node.content.querySelector<HTMLElement>(
      ':scope > [data-okt-row] > [data-okt-icon-box] > [data-okt-status-tile]'
    );
    if (statusTile) {
      stopStatusTile(statusTile);
    }
    node.offsetBeforeRemove = group ? offsetOf(group, node) : 0;
    if (group) {
      group.heights = group.heights.filter((entry) => entry.id !== node.id);
    }
    clearCloseTimer(node);
    if (notify) {
      onEvent({
        id: node.id,
        generation: node.generation,
        type: 'dismiss',
        reason,
      });
    }
    node.unmountTimer = setTimeout(() => {
      node.unmountTimer = undefined;
      unmount(node, reason);
      update();
    }, TIME_BEFORE_UNMOUNT);
    update();
  }

  function clearPress(pointerId: number, { node }: IToastPress) {
    presses.delete(pointerId);
    if (node.swipePointerId === pointerId) {
      node.swipePointerId = undefined;
      node.swiping = false;
      node.li.setAttribute('data-swiping', 'false');
      node.li.style.setProperty('--swipe-amount', '0px');
      if (node.li.hasPointerCapture?.(pointerId)) {
        node.li.releasePointerCapture(pointerId);
      }
    }
  }

  function clearNodePresses(node: IToastNode) {
    presses.forEach((press, pointerId) => {
      if (press.node === node) {
        clearPress(pointerId, press);
        hoveredGroups.delete(pointerId);
      }
    });
  }

  function swipeAmount(node: IToastNode, deltaY: number): number {
    return node.position.startsWith('top')
      ? Math.min(0, deltaY)
      : Math.max(0, deltaY);
  }

  function onGlobalPointerMove(event: PointerEvent) {
    const press = presses.get(event.pointerId);
    if (!press) {
      return;
    }
    const { node } = press;
    const dx = Math.abs(event.clientX - press.x);
    const dy = event.clientY - press.y;
    const threshold =
      event.pointerType === 'touch'
        ? TOUCH_START_THRESHOLD
        : POINTER_START_THRESHOLD;
    press.moved ||= Math.max(dx, Math.abs(dy)) > threshold;
    if (node.swipePointerId !== event.pointerId) {
      return;
    }
    const amount = swipeAmount(node, dy);
    if (!node.swiping) {
      if (Math.abs(amount) <= threshold || Math.abs(amount) < dx) {
        return;
      }
      node.li.setPointerCapture?.(event.pointerId);
      node.swiping = true;
      node.li.setAttribute('data-swiping', 'true');
    }
    node.li.style.setProperty('--swipe-amount', `${amount}px`);
  }

  function onGlobalPointerEnd(event: PointerEvent) {
    const { pointerId, pointerType } = event;
    const press = presses.get(pointerId);
    if (!press) {
      return;
    }
    const { node } = press;
    const released = event.type === 'pointerup';
    const amount =
      released && node.swipePointerId === pointerId && node.swiping
        ? swipeAmount(node, event.clientY - press.y)
        : 0;
    const velocity =
      Math.abs(amount) / Math.max(1, Date.now() - press.startedAt);
    const dismissed =
      Math.abs(amount) >= SWIPE_THRESHOLD || velocity > SWIPE_VELOCITY;
    clearPress(pointerId, press);

    const hit = released ? groupContaining(pointerHit(event)) : undefined;
    if (pointerType === 'touch') {
      if (
        hit &&
        nodes.size > 1 &&
        hit === groups.get(node.position) &&
        !press.moved &&
        Math.max(
          Math.abs(event.clientX - press.x),
          Math.abs(event.clientY - press.y)
        ) <= TOUCH_START_THRESHOLD
      ) {
        touchExpandedGroup = hit;
      }
    } else if (hit) {
      hoveredGroups.set(pointerId, hit);
    } else {
      hoveredGroups.delete(pointerId);
    }

    if (dismissed) {
      node.swipeOut = true;
      node.li.style.setProperty('--swipe-amount', `${amount}px`);
      deleteToast(node, 'swipe', true);
    } else {
      update();
    }
  }

  function attachPointerHandlers(node: IToastNode) {
    const { li } = node;
    li.addEventListener('dragstart', (event) => {
      event.preventDefault();
    });
    li.addEventListener('pointerdown', (event) => {
      if (node.removed || event.button !== 0) {
        return;
      }
      const target = event.target as HTMLElement | null;
      if (
        node.swipePointerId === undefined &&
        !target?.closest?.('button, [data-okt-link]')
      ) {
        node.swipePointerId = event.pointerId;
      }
      presses.set(event.pointerId, {
        node,
        x: event.clientX,
        y: event.clientY,
        startedAt: Date.now(),
        moved: false,
      });
      update();
    });
    li.addEventListener('lostpointercapture', (event) => {
      if (
        event.target === li &&
        node.swipePointerId === event.pointerId &&
        !li.hasPointerCapture?.(event.pointerId)
      ) {
        onGlobalPointerEnd(event);
      }
    });
  }

  function renderContent(node: IToastNode, statusMotion?: 'enter' | 'settle') {
    const next = buildToastContent(
      node.spec,
      (event) => {
        if (!node.removed) {
          onEvent({
            ...event,
            id: node.id,
            generation: node.generation,
          } as IOverlayToastEvent);
        }
      },
      node.content,
      reduceMotion() ? undefined : statusMotion
    );
    if (node.content !== next) {
      if (node.content.parentElement === node.body) {
        node.content.replaceWith(next);
      } else {
        node.body.append(next);
      }
      node.content = next;
    }
    if (node.spec.testID) {
      node.li.setAttribute('data-testid', node.spec.testID);
    } else {
      node.li.removeAttribute('data-testid');
    }
  }

  function createNode(item: IOverlayToastItem): IToastNode {
    const li = document.createElement('li');
    li.setAttribute('aria-live', 'polite');
    li.setAttribute('aria-atomic', 'true');
    li.setAttribute('role', 'status');
    li.tabIndex = 0;
    li.setAttribute('data-onekey-toast', '');
    li.setAttribute('data-styled', 'true');
    li.setAttribute('data-dismissible', 'true');
    li.setAttribute('data-invert', 'false');

    const shell = document.createElement('div');
    shell.setAttribute('data-okt-shell', '');
    const surface = document.createElement('div');
    surface.setAttribute('data-okt-surface', '');
    const body = document.createElement('div');
    body.setAttribute('data-okt-body', '');
    surface.append(body);

    const closeButton = createCloseButton(
      config.closeButtonLabel ?? 'Close toast'
    );
    shell.append(surface, closeButton);
    li.append(shell);

    const node: IToastNode = {
      id: item.spec.id,
      spec: item.spec,
      revision: item.revision,
      generation: item.generation,
      position: positionOf(item.spec),
      li,
      shell,
      surface,
      body,
      content: document.createElement('div'),
      mounted: false,
      removed: false,
      swiping: false,
      swipeOut: false,
      offsetBeforeRemove: 0,
      initialHeight: 0,
    };
    const revealClose = (event: PointerEvent) => {
      if (
        event.buttons === 0 &&
        (event.pointerType === 'mouse' || event.pointerType === 'pen')
      ) {
        li.setAttribute('data-okt-close-hover', '');
      }
    };
    li.addEventListener('pointerenter', revealClose);
    li.addEventListener('pointermove', revealClose);
    li.addEventListener('pointerleave', () =>
      li.removeAttribute('data-okt-close-hover')
    );
    closeButton.addEventListener('click', () =>
      deleteToast(node, 'close', true)
    );
    li.addEventListener('keydown', (event) => {
      if (
        !node.removed &&
        node.spec.layout === 'notification' &&
        node.spec.pressable &&
        event.target === li &&
        (event.key === 'Enter' || event.key === ' ')
      ) {
        event.preventDefault();
        onEvent({
          id: node.id,
          generation: node.generation,
          type: 'press',
        });
      }
    });
    renderContent(node, 'enter');
    attachPointerHandlers(node);
    return node;
  }

  function mountNode(node: IToastNode) {
    const group = ensureGroup(node.position);
    group.toasts.unshift(node);
    group.ol.prepend(node.li);
    nodes.set(node.id, node);
    applyGroup(group);
    measure(node);
    node.mounted = true;
    update();
  }

  function obstructionTop(): number {
    const bottom = config.topObstruction ?? 0;
    if (bottom <= 0) {
      return 0;
    }
    const gap = config.topObstructionGap ?? DEFAULT_OBSTRUCTION_GAP;
    const room = config.topObstructionRoom ?? DEFAULT_OBSTRUCTION_ROOM;
    return Math.max(0, Math.min(bottom + gap, globalThis.innerHeight - room));
  }

  function applyGroup(group: IToastGroup) {
    const { ol } = group;
    const [yPosition, xPosition] = group.position.split('-');
    ol.dir = getDocumentDirection();
    ol.setAttribute('data-theme', config.theme ?? 'light');
    ol.setAttribute('data-y-position', yPosition ?? 'bottom');
    ol.setAttribute('data-x-position', xPosition ?? 'right');
    ol.style.setProperty(
      '--front-toast-height',
      `${group.heights[0]?.height ?? 0}px`
    );
    ol.style.setProperty('--offset', `${VIEWPORT_OFFSET}px`);
    ol.style.setProperty('--width', `${TOAST_WIDTH}px`);
    ol.style.setProperty('--gap', `${GAP}px`);
    if (config.cardColor) {
      ol.style.setProperty('--okt-card-bg', config.cardColor);
    } else {
      ol.style.removeProperty('--okt-card-bg');
    }
    if (config.cardBorderColor) {
      ol.style.setProperty('--okt-card-border', config.cardBorderColor);
    } else {
      ol.style.removeProperty('--okt-card-border');
    }
    if (config.webContentMaxHeight !== undefined) {
      ol.style.setProperty(
        '--okt-max-height',
        `${config.webContentMaxHeight}px`
      );
    } else {
      ol.style.removeProperty('--okt-max-height');
    }
    const top = yPosition === 'top' ? obstructionTop() : 0;
    ol.setAttribute(
      'data-okt-respect-reduced-motion',
      String(config.respectReducedMotion !== false)
    );
    ol.style.transition = reduceMotion()
      ? ''
      : `top ${OBSTRUCTION_SHIFT_MS}ms ease`;
    ol.style.top = top > 0 ? `${top}px` : '';

    group.toasts.forEach((node, index) => {
      const { li } = node;
      li.setAttribute('data-mounted', String(node.mounted));
      li.setAttribute('data-removed', String(node.removed));
      li.setAttribute('data-visible', String(index + 1 <= VISIBLE_TOASTS));
      li.setAttribute('data-y-position', yPosition ?? 'bottom');
      li.setAttribute('data-x-position', xPosition ?? 'right');
      li.setAttribute('data-index', String(index));
      li.setAttribute('data-front', String(index === 0));
      li.setAttribute('data-swiping', String(node.swiping));
      li.setAttribute('data-swipe-out', String(node.swipeOut));
      li.setAttribute('data-expanded', String(isExpanded()));
      li.style.setProperty('--index', String(index));
      li.style.setProperty('--toasts-before', String(index));
      li.style.setProperty('--z-index', String(group.toasts.length - index));
      li.style.setProperty(
        '--offset',
        `${node.removed ? node.offsetBeforeRemove : offsetOf(group, node)}px`
      );
      li.style.setProperty('--initial-height', `${node.initialHeight}px`);
    });
  }

  function update() {
    if (destroyed) {
      return;
    }
    groups.forEach((group, position) => {
      if (group.toasts.length === 0) {
        hoveredGroups.forEach((hoveredGroup, pointerId) => {
          if (hoveredGroup === group) {
            hoveredGroups.delete(pointerId);
          }
        });
        if (touchExpandedGroup === group) {
          touchExpandedGroup = undefined;
        }
        group.ol.remove();
        groups.delete(position);
      }
    });
    if (nodes.size <= 1) {
      keyboardExpanded = false;
      touchExpandedGroup = undefined;
    }
    groups.forEach((group) => {
      applyGroup(group);
    });
    if (nodes.size === 0) {
      section?.remove();
    }
    syncTimers();
  }

  function moveNode(
    node: IToastNode,
    position: IOverlayToastPosition
  ): IToastGroup {
    clearNodePresses(node);
    const previous = groups.get(node.position);
    if (previous) {
      previous.toasts = previous.toasts.filter((entry) => entry !== node);
      previous.heights = previous.heights.filter(
        (entry) => entry.id !== node.id
      );
    }
    node.position = position;
    const group = ensureGroup(position);
    group.toasts.push(node);
    group.ol.append(node.li);
    if (!node.removed) {
      group.heights.push({ id: node.id, height: node.initialHeight });
    }
    return group;
  }

  function reconcileGroupOrder(
    group: IToastGroup,
    order: ReadonlyMap<string, number>
  ) {
    group.toasts.sort(
      (a, b) => (order.get(a.id) ?? 0) - (order.get(b.id) ?? 0)
    );
    group.toasts.forEach((node) => group.ol.append(node.li));
    group.heights.sort(
      (a, b) => (order.get(a.id) ?? 0) - (order.get(b.id) ?? 0)
    );
  }

  function onKeyDown(event: KeyboardEvent) {
    if (nodes.size === 0 || section?.inert || section?.parentElement?.inert) {
      return;
    }
    const lists = [...groups.values()].map((group) => group.ol);
    if (event.altKey && event.code === 'KeyT') {
      keyboardExpanded = true;
      update();
      lists[0]?.focus();
    }
    if (
      event.code === 'Escape' &&
      lists.some(
        (list) =>
          document.activeElement === list ||
          list.contains(document.activeElement)
      )
    ) {
      keyboardExpanded = false;
      touchExpandedGroup = undefined;
      update();
    }
  }

  function onResize() {
    nodes.forEach(measure);
    update();
  }

  function onGlobalPointerDown(event: PointerEvent) {
    if (event.pointerType === 'touch') {
      nodes.forEach(({ li }) => li.removeAttribute('data-okt-close-hover'));
    }
    if (
      (touchExpandedGroup || keyboardExpanded) &&
      !groupContaining(event.target as Node | null)
    ) {
      touchExpandedGroup = undefined;
      keyboardExpanded = false;
      update();
    }
  }

  function onWindowBlur() {
    presses.forEach((press, pointerId) => clearPress(pointerId, press));
    hoveredGroups.clear();
    keyboardExpanded = false;
    touchExpandedGroup = undefined;
    update();
  }

  function finalizeReducedMotion() {
    if (!reduceMotion()) {
      return;
    }
    nodes.forEach(({ content }) => {
      content
        .querySelectorAll<HTMLElement>('[data-okt-status-motion]')
        .forEach(stopStatusTile);
      content
        .querySelector('[data-okt-content-swap]')
        ?.removeAttribute('data-okt-content-swap');
    });
  }

  function onMotionPreferenceChange() {
    finalizeReducedMotion();
    update();
  }

  motionPreference?.addEventListener?.('change', onMotionPreferenceChange);
  document.addEventListener('keydown', onKeyDown);
  document.addEventListener('pointerdown', onGlobalPointerDown, true);
  document.addEventListener('pointermove', onGlobalPointerMove, true);
  document.addEventListener('pointerup', onGlobalPointerEnd, true);
  document.addEventListener('pointercancel', onGlobalPointerEnd, true);
  globalThis.addEventListener('resize', onResize);
  globalThis.addEventListener('blur', onWindowBlur);

  return {
    sync(items, nextConfig) {
      if (destroyed) {
        return;
      }
      syncing = true;
      const shouldReduceBeforeSync = reduceMotion();
      config = nextConfig;
      if (!shouldReduceBeforeSync && reduceMotion()) {
        finalizeReducedMotion();
      }
      const present = new Set(items.map((item) => item.spec.id));
      [...nodes.values()].forEach((node) => {
        if (!present.has(node.id)) {
          unmount(node, 'programmatic');
        }
      });
      const movedGroups = new Set<IToastGroup>();
      [...items].reverse().forEach((item) => {
        let node = nodes.get(item.spec.id);
        if (node?.removed && item.visible) {
          unmount(node, 'programmatic');
          node = undefined;
        }
        if (!node) {
          if (item.visible) {
            mountNode(createNode(item));
          } else {
            onEvent({
              id: item.spec.id,
              generation: item.generation,
              type: 'close',
              reason: 'programmatic',
            });
          }
          return;
        }
        if (node.revision !== item.revision) {
          const nextIcon =
            item.spec.layout === 'message'
              ? item.spec.statusIcon ?? item.spec.icon
              : undefined;
          const settling =
            item.visible &&
            node.generation !== item.generation &&
            nextIcon?.kind === 'status' &&
            nextIcon.status !== 'loading';
          node.revision = item.revision;
          node.spec = item.spec;
          renderContent(node, settling ? 'settle' : undefined);
          measure(node);
        }
        if (node.generation !== item.generation) {
          clearNodePresses(node);
          node.generation = item.generation;
          clearCloseTimer(node);
        }
        const position = positionOf(node.spec);
        if (position !== node.position) {
          movedGroups.add(moveNode(node, position));
        }
        if (!item.visible) {
          deleteToast(node, 'programmatic', false);
        }
      });
      if (movedGroups.size > 0) {
        const order = new Map(
          items.map((item, index) => [item.spec.id, index])
        );
        movedGroups.forEach((group) => reconcileGroupOrder(group, order));
      }
      update();
      syncing = false;
      pendingEvents.splice(0).forEach(report);
    },
    destroy() {
      destroyed = true;
      motionPreference?.removeEventListener?.(
        'change',
        onMotionPreferenceChange
      );
      document.removeEventListener('keydown', onKeyDown);
      document.removeEventListener('pointerdown', onGlobalPointerDown, true);
      document.removeEventListener('pointermove', onGlobalPointerMove, true);
      document.removeEventListener('pointerup', onGlobalPointerEnd, true);
      document.removeEventListener('pointercancel', onGlobalPointerEnd, true);
      globalThis.removeEventListener('resize', onResize);
      globalThis.removeEventListener('blur', onWindowBlur);
      presses.forEach((press, pointerId) => clearPress(pointerId, press));
      hoveredGroups.clear();
      nodes.forEach((node) => {
        clearCloseTimer(node);
        if (node.unmountTimer !== undefined) {
          clearTimeout(node.unmountTimer);
        }
      });
      nodes.clear();
      groups.clear();
      section?.remove();
      section = null;
    },
  };
}
