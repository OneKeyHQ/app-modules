/* cspell:ignore dasharray dashoffset */
import { getOverlayLayerRoot, syncOverlayInert } from '../web/overlayLayers';

import { buildToastContent } from './toastDomContent.web';
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

// The chrome constants of the renderer this replaces (sonner 1.4.41).
const VISIBLE_TOASTS = 3;
const VIEWPORT_OFFSET = 32;
const TOAST_WIDTH = 356;
const GAP = 14;
const SWIPE_THRESHOLD = 20;
const SWIPE_VELOCITY = 0.11; // px per ms
// Equal to the exit transition.
const TIME_BEFORE_UNMOUNT = 200;
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
  content: HTMLElement;
  mounted: boolean;
  removed: boolean;
  swiping: boolean;
  swipeOut: boolean;
  offsetBeforeRemove: number;
  initialHeight: number;
  closeTimer?: ReturnType<typeof setTimeout>;
  unmountTimer?: ReturnType<typeof setTimeout>;
  pointerStart: { x: number; y: number } | null;
  dragStartedAt: number;
}

interface IToastGroup {
  position: IOverlayToastPosition;
  ol: HTMLOListElement;
  hovered: boolean;
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
    'width': '12',
    'height': '12',
    'viewBox': '0 0 24 24',
    'fill': 'none',
    'stroke': 'currentColor',
    'stroke-width': '1.5',
    'stroke-linecap': 'round',
    'stroke-linejoin': 'round',
  };
  Object.entries(attributes).forEach(([name, value]) =>
    svg.setAttribute(name, value)
  );
  [
    ['18', '6', '6', '18'],
    ['6', '6', '18', '18'],
  ].forEach(([x1, y1, x2, y2]) => {
    const line = document.createElementNS(SVG_NS, 'line');
    line.setAttribute('x1', x1 ?? '');
    line.setAttribute('y1', y1 ?? '');
    line.setAttribute('x2', x2 ?? '');
    line.setAttribute('y2', y2 ?? '');
    svg.append(line);
  });
  button.append(svg);
  return button;
}

/**
 * Draws message toasts with plain DOM in the `toast` overlay layer. No React:
 * the cards, their stacking, timers, swipe and transitions live here, and the
 * stylesheet reproduces the previous renderer's chrome rule for rule.
 */
export function createToastDomRenderer(
  report: (event: IOverlayToastEvent) => void
): IOverlayToastRenderer {
  ensureOverlayToastStyles();

  // Events raised while a sync is applying wait for it to finish: the store
  // answers an event by syncing again.
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
  let config: IOverlayToasterConfig = {};
  let section: HTMLElement | null = null;
  let expanded = false;
  let interacting = false;
  let destroyed = false;

  const isHovered = (): boolean =>
    [...groups.values()].some((group) => group.hovered);

  const positionOf = (spec: IOverlayToastSpec): IOverlayToastPosition =>
    spec.position ?? config.position ?? DEFAULT_POSITION;

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

  function setExpanded(value: boolean) {
    if (expanded !== value) {
      expanded = value;
      update();
    }
  }

  function setInteracting(value: boolean) {
    if (interacting !== value) {
      interacting = value;
      if (!value && !isHovered()) {
        expanded = false;
      }
      update();
    }
  }

  function syncHoverAfterPointer(event?: PointerEvent) {
    if (event && typeof document.elementFromPoint === 'function') {
      const hit = document.elementFromPoint(event.clientX, event.clientY);
      if (hit) {
        groups.forEach((group) => {
          group.hovered = group.ol.contains(hit);
        });
      } else if (
        event.clientX < 0 ||
        event.clientY < 0 ||
        event.clientX > globalThis.innerWidth ||
        event.clientY > globalThis.innerHeight
      ) {
        groups.forEach((group) => {
          group.hovered = false;
        });
      }
    }
    if (!isHovered()) {
      expanded = false;
    }
  }

  function endInteraction(event?: PointerEvent) {
    const wasInteracting = interacting;
    interacting = false;
    syncHoverAfterPointer(event);
    if (wasInteracting || !isHovered()) {
      update();
    }
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
        hovered: false,
        toasts: [],
        heights: [],
      };
      ol.addEventListener('mouseenter', () => {
        if (interacting) {
          return;
        }
        nextGroup.hovered = true;
        expanded = nodes.size > 1;
        update();
      });
      ol.addEventListener('mousemove', () => {
        if (interacting) {
          return;
        }
        if (!nextGroup.hovered || (nodes.size > 1 && !expanded)) {
          nextGroup.hovered = true;
          expanded = nodes.size > 1;
          update();
        }
      });
      ol.addEventListener('mouseleave', () => {
        nextGroup.hovered = false;
        // Keep the stack open while a card is being dragged.
        if (!interacting && !isHovered()) {
          expanded = false;
        }
        update();
      });
      ol.addEventListener('pointerdown', (event) => {
        if (event.pointerType === 'touch') {
          nextGroup.hovered = true;
          expanded = nodes.size > 1;
        }
        setInteracting(true);
      });
      ol.addEventListener('pointerup', (event) => endInteraction(event));
      ol.addEventListener('pointercancel', (event) => endInteraction(event));
      group = nextGroup;
      groups.set(position, group);
    }
    if (group.ol.parentElement !== ensureSection()) {
      ensureSection().append(group.ol);
    }
    return group;
  }

  function offsetOf(group: IToastGroup, node: IToastNode): number {
    // The height index moves before the card list does, so the layout of the
    // cards behind a leaving one starts closing at once.
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
    // The card's own height: a card further back in the stack is scaled
    // and pinned to the front card's height, neither of which is its size.
    const { style } = node.li;
    const originalHeight = style.height;
    const originalTransform = style.transform;
    style.height = 'auto';
    style.transform = 'none';
    const { height } = node.li.getBoundingClientRect();
    style.height = originalHeight;
    style.transform = originalTransform;
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
    const paused = expanded || isHovered() || interacting;
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
        // A resumed toast starts over with its whole lifetime, as before.
        node.closeTimer = setTimeout(() => {
          node.closeTimer = undefined;
          deleteToast(node, 'timeout', true);
        }, node.spec.duration);
      }
    });
  }

  function unmount(node: IToastNode, reason: IOverlayToastDismissReason) {
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
    const group = groups.get(node.position);
    node.removed = true;
    // Keep the offset the card had, for the exit transition.
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

  function resetSwipe(node: IToastNode, event?: PointerEvent) {
    if (node.swipeOut || node.removed) {
      return;
    }
    node.pointerStart = null;
    node.swiping = false;
    node.li.setAttribute('data-swiping', 'false');
    node.li.style.setProperty('--swipe-amount', '0px');
    syncHoverAfterPointer(event);
    update();
  }

  function attachSwipe(node: IToastNode) {
    const { li } = node;
    li.addEventListener('dragstart', (event) => {
      event.preventDefault();
    });
    li.addEventListener('pointerdown', (event) => {
      if (node.removed) {
        return;
      }
      node.dragStartedAt = Date.now();
      const group = groups.get(node.position);
      node.offsetBeforeRemove = group ? offsetOf(group, node) : 0;
      const target = event.target as HTMLElement | null;
      if (target?.closest?.('button, [data-okt-link]')) {
        return;
      }
      // Keep the pointer on the card root while it travels outside.
      li.setPointerCapture?.(event.pointerId);
      node.swiping = true;
      node.pointerStart = { x: event.clientX, y: event.clientY };
      update();
    });
    li.addEventListener('pointerup', (event) => {
      if (li.hasPointerCapture?.(event.pointerId)) {
        li.releasePointerCapture(event.pointerId);
      }
      if (node.swipeOut || node.removed) {
        return;
      }
      node.pointerStart = null;
      const swipeAmount = Number(
        li.style.getPropertyValue('--swipe-amount').replace('px', '') || 0
      );
      const timeTaken = Math.max(1, Date.now() - node.dragStartedAt);
      const velocity = Math.abs(swipeAmount) / timeTaken;
      if (
        Math.abs(swipeAmount) >= SWIPE_THRESHOLD ||
        velocity > SWIPE_VELOCITY
      ) {
        node.swipeOut = true;
        node.swiping = false;
        li.setAttribute('data-swiping', 'false');
        syncHoverAfterPointer(event);
        deleteToast(node, 'swipe', true);
        return;
      }
      resetSwipe(node, event);
    });
    li.addEventListener('pointercancel', (event) => {
      if (li.hasPointerCapture?.(event.pointerId)) {
        li.releasePointerCapture(event.pointerId);
      }
      resetSwipe(node, event);
    });
    li.addEventListener('lostpointercapture', (event) => {
      if (node.swiping && !node.swipeOut && !node.removed) {
        resetSwipe(node, event);
      }
    });
    li.addEventListener('pointermove', (event) => {
      if (!node.pointerStart) {
        return;
      }
      const yPosition = event.clientY - node.pointerStart.y;
      const xPosition = event.clientX - node.pointerStart.x;
      const isTop = node.position.startsWith('top');
      // Top toasts leave upwards, bottom toasts downwards.
      const clampedY = isTop ? Math.min(0, yPosition) : Math.max(0, yPosition);
      const swipeStartThreshold = event.pointerType === 'touch' ? 10 : 2;
      if (
        Math.abs(clampedY) > swipeStartThreshold &&
        Math.abs(clampedY) >= Math.abs(xPosition)
      ) {
        li.style.setProperty('--swipe-amount', `${clampedY}px`);
      } else {
        li.style.setProperty('--swipe-amount', '0px');
      }
    });
  }

  function renderContent(node: IToastNode) {
    const next = buildToastContent(node.spec, (event) => {
      if (!node.removed) {
        onEvent({
          ...event,
          id: node.id,
          generation: node.generation,
        } as IOverlayToastEvent);
      }
    });
    if (node.content.parentElement === node.li) {
      node.content.replaceWith(next);
    } else {
      node.li.append(next);
    }
    node.content = next;
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
    const closeButton = createCloseButton(
      config.closeButtonLabel ?? 'Close toast'
    );
    li.append(closeButton);
    const node: IToastNode = {
      id: item.spec.id,
      spec: item.spec,
      revision: item.revision,
      generation: item.generation,
      position: positionOf(item.spec),
      li,
      content: document.createElement('div'),
      mounted: false,
      removed: false,
      swiping: false,
      swipeOut: false,
      offsetBeforeRemove: 0,
      initialHeight: 0,
      pointerStart: null,
      dragStartedAt: 0,
    };
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
    renderContent(node);
    attachSwipe(node);
    return node;
  }

  function mountNode(node: IToastNode) {
    const group = ensureGroup(node.position);
    group.toasts.unshift(node);
    group.ol.prepend(node.li);
    nodes.set(node.id, node);
    applyGroup(group);
    // Measuring lays the card out in its resting-out state, so switching it
    // to mounted right after starts the enter transition from there. No frame
    // callback: those do not run while the page is hidden.
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
    if (config.webContentMaxHeight !== undefined) {
      ol.style.setProperty(
        '--okt-max-height',
        `${config.webContentMaxHeight}px`
      );
    } else {
      ol.style.removeProperty('--okt-max-height');
    }
    // Top toasts rest under whatever hangs from the top of the window. An
    // inline `top`: the narrow-window rule pins the toaster otherwise.
    const top = yPosition === 'top' ? obstructionTop() : 0;
    const reducedMotion =
      config.respectReducedMotion !== false &&
      globalThis.matchMedia?.('(prefers-reduced-motion: reduce)').matches;
    ol.style.transition = reducedMotion
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
      li.setAttribute('data-expanded', String(expanded));
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
    let releasedHover = false;
    groups.forEach((group, position) => {
      if (group.toasts.length === 0) {
        if (group.hovered) {
          releasedHover = true;
        }
        group.ol.remove();
        groups.delete(position);
      }
    });
    const hovered = isHovered();
    if (nodes.size <= 1 && expanded) {
      // One card has nothing to fan out.
      expanded = false;
    } else if (releasedHover && !hovered && !interacting) {
      expanded = false;
    } else if (nodes.size > 1 && hovered && !expanded) {
      expanded = true;
    }
    groups.forEach((group) => {
      applyGroup(group);
    });
    if (nodes.size === 0) {
      section?.remove();
      interacting = false;
    }
    syncTimers();
  }

  function moveNode(
    node: IToastNode,
    position: IOverlayToastPosition
  ): IToastGroup {
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
      setExpanded(true);
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
      setExpanded(false);
    }
  }

  function onResize() {
    if ((config.topObstruction ?? 0) > 0) {
      update();
    }
  }

  function onGlobalPointerDown(event: PointerEvent) {
    const target = event.target as Node | null;
    let insideAnyGroup = false;
    let changed = false;
    groups.forEach((group) => {
      const inside = !!target && group.ol.contains(target);
      if (inside) {
        insideAnyGroup = true;
      } else if (group.hovered) {
        group.hovered = false;
        changed = true;
      }
    });
    if (!insideAnyGroup && expanded) {
      expanded = false;
      changed = true;
    }
    if (changed) {
      update();
    }
  }

  function onGlobalPointerEnd(event: PointerEvent) {
    if (!interacting) {
      return;
    }
    endInteraction(event);
  }

  function onWindowBlur() {
    let changed = false;
    nodes.forEach((node) => {
      if (node.swiping && !node.swipeOut && !node.removed) {
        node.pointerStart = null;
        node.li.style.setProperty('--swipe-amount', '0px');
        node.swiping = false;
        changed = true;
      }
    });
    groups.forEach((group) => {
      if (group.hovered) {
        group.hovered = false;
        changed = true;
      }
    });
    if (interacting || expanded) {
      interacting = false;
      expanded = false;
      changed = true;
    }
    if (changed) {
      update();
    }
  }

  document.addEventListener('keydown', onKeyDown);
  document.addEventListener('pointerdown', onGlobalPointerDown);
  document.addEventListener('pointerup', onGlobalPointerEnd);
  document.addEventListener('pointercancel', onGlobalPointerEnd);
  globalThis.addEventListener('resize', onResize);
  globalThis.addEventListener('blur', onWindowBlur);

  return {
    sync(items, nextConfig) {
      if (destroyed) {
        return;
      }
      syncing = true;
      config = nextConfig;
      const present = new Set(items.map((item) => item.spec.id));
      // Cards the list no longer knows leave without ceremony.
      [...nodes.values()].forEach((node) => {
        if (!present.has(node.id)) {
          unmount(node, 'programmatic');
        }
      });
      const movedGroups = new Set<IToastGroup>();
      // Oldest first, so each new card lands in front of the previous one.
      [...items].reverse().forEach((item) => {
        let node = nodes.get(item.spec.id);
        if (node?.removed && item.visible) {
          // Shown again while the previous card was leaving.
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
          node.revision = item.revision;
          node.spec = item.spec;
          renderContent(node);
          measure(node);
        }
        if (node.generation !== item.generation) {
          // Shown again: the toast starts its lifetime over.
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
      document.removeEventListener('keydown', onKeyDown);
      document.removeEventListener('pointerdown', onGlobalPointerDown);
      document.removeEventListener('pointerup', onGlobalPointerEnd);
      document.removeEventListener('pointercancel', onGlobalPointerEnd);
      globalThis.removeEventListener('resize', onResize);
      globalThis.removeEventListener('blur', onWindowBlur);
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
