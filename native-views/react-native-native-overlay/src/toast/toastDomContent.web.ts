/* cspell:ignore dasharray dashoffset */
import { TOAST_GEOMETRY } from './toastMotion';
import { createStatusTile, updateStatusTile } from './toastStatusTile.web';

import type {
  IOverlayMessageToast,
  IOverlayToastAction,
  IOverlayToastIcon,
  IOverlayToastSpec,
  IOverlayToastText,
  IOverlayToastTextRun,
} from './types';

const SVG_NS = 'http://www.w3.org/2000/svg';

const MESSAGE_ICON_GAP = TOAST_GEOMETRY.contentGap;
const MESSAGE_TEXT_GAP = TOAST_GEOMETRY.textGap;
const NOTIFICATION_GAP = 10;
const NOTIFICATION_TEXT_GAP = 2;
const ACTIONS_PADDING_TOP = 10;
const ACTIONS_PADDING_END = 2;
const ACTIONS_PADDING_BOTTOM = 2;
const INLINE_ACTION_MAX_LABEL = 16;
const INLINE_ACTION_MAX_MESSAGE = 72;

export type IToastContentEvent =
  | { type: 'action'; actionId: string }
  | { type: 'link'; link: string }
  | { type: 'press' };

type IEmit = (event: IToastContentEvent) => void;

function element(tag: string, marker: string): HTMLElement {
  const node = document.createElement(tag);
  node.setAttribute(marker, '');
  return node;
}

function textLength(text: IOverlayToastText | undefined): number {
  if (!text) {
    return 0;
  }
  return text.paragraphs.reduce(
    (sum, runs) =>
      sum + runs.reduce((runSum, run) => runSum + run.text.length, 0),
    0
  );
}

function shouldUseInlineAction(spec: IOverlayMessageToast): boolean {
  if (spec.actions?.length !== 1) {
    return false;
  }
  if ((spec.actionsAlign ?? 'right') !== 'right') {
    return false;
  }
  const [action] = spec.actions;
  if (!action || action.label.length > INLINE_ACTION_MAX_LABEL) {
    return false;
  }
  if (action.marginHorizontal || action.marginVertical) {
    return false;
  }
  if ((spec.message?.paragraphs.length ?? 0) > 1) {
    return false;
  }
  return textLength(spec.message) <= INLINE_ACTION_MAX_MESSAGE;
}

function createIcon(icon: IOverlayToastIcon): Element {
  if (icon.kind === 'status') {
    return createStatusTile(icon);
  }
  if (icon.kind === 'image') {
    const image = document.createElement('img');
    image.setAttribute('data-okt-image', '');
    image.src = icon.uri;
    image.alt = '';
    image.draggable = false;
    image.width = icon.size;
    image.height = icon.size;
    image.style.width = `${icon.size}px`;
    image.style.height = `${icon.size}px`;
    if (icon.borderRadius) {
      image.style.borderRadius = `${icon.borderRadius}px`;
    }
    return image;
  }
  const svg = document.createElementNS(SVG_NS, 'svg');
  svg.setAttribute('width', String(icon.size));
  svg.setAttribute('height', String(icon.size));
  svg.setAttribute('aria-hidden', 'true');
  svg.setAttribute('focusable', 'false');
  svg.style.color = icon.color;
  if (icon.kind === 'spinner') {
    svg.setAttribute('data-okt-spinner', '');
    svg.style.color = '';
    svg.setAttribute('viewBox', '0 0 32 32');
    const track = document.createElementNS(SVG_NS, 'circle');
    const arc = document.createElementNS(SVG_NS, 'circle');
    [track, arc].forEach((circle) => {
      circle.setAttribute('cx', '16');
      circle.setAttribute('cy', '16');
      circle.setAttribute('r', '14');
      circle.setAttribute('fill', 'none');
      circle.setAttribute('stroke-width', '4');
      circle.style.stroke = icon.color;
    });
    track.style.opacity = '0.2';
    arc.style.strokeDasharray = '80';
    arc.style.strokeDashoffset = '60';
    svg.append(track, arc);
    return svg;
  }
  svg.setAttribute('data-okt-icon', '');
  if (icon.href) {
    const use = document.createElementNS(SVG_NS, 'use');
    use.setAttribute('href', icon.href);
    svg.append(use);
  }
  return svg;
}

function applyRunStyle(node: HTMLElement, run: IOverlayToastTextRun) {
  if (run.color) {
    node.style.color = run.color;
  }
  if (run.fontWeight) {
    node.style.fontWeight = run.fontWeight;
  }
  if (run.fontSize !== undefined) {
    node.style.fontSize = `${run.fontSize}px`;
  }
  if (run.underline) {
    node.style.textDecorationLine = 'underline';
  }
}

function isPlainRun(run: IOverlayToastTextRun): boolean {
  return (
    !run.color &&
    !run.fontWeight &&
    run.fontSize === undefined &&
    !run.underline &&
    run.link === undefined
  );
}

function createParagraph(
  text: IOverlayToastText,
  runs: IOverlayToastTextRun[],
  emit: IEmit
): HTMLElement {
  const paragraph = element('span', 'data-okt-text');
  paragraph.style.color = text.color;
  paragraph.style.fontSize = `${text.fontSize}px`;
  paragraph.style.lineHeight = `${text.lineHeight}px`;
  paragraph.style.fontWeight = text.fontWeight;
  if (text.fontFamily) {
    paragraph.style.fontFamily = text.fontFamily;
  }
  if (text.letterSpacing !== undefined) {
    paragraph.style.letterSpacing = `${text.letterSpacing}px`;
  }
  if (text.selectable) {
    paragraph.setAttribute('data-okt-selectable', '');
  }
  if (text.tabularNumbers) {
    paragraph.style.fontVariantNumeric = 'tabular-nums';
  }
  if (text.linkHoverColor) {
    paragraph.style.setProperty('--okt-link-hover', text.linkHoverColor);
  }
  if (text.linkPressedColor) {
    paragraph.style.setProperty('--okt-link-active', text.linkPressedColor);
  }
  if (text.numberOfLines) {
    paragraph.setAttribute('data-okt-clamp', '');
    paragraph.style.setProperty(
      '-webkit-line-clamp',
      String(text.numberOfLines)
    );
  }
  runs.forEach((run) => {
    if (isPlainRun(run)) {
      paragraph.append(document.createTextNode(run.text));
      return;
    }
    const span = element('span', 'data-okt-text');
    span.textContent = run.text;
    applyRunStyle(span, run);
    const { link } = run;
    if (link !== undefined) {
      span.setAttribute('data-okt-link', '');
      span.setAttribute('role', 'link');
      span.tabIndex = 0;
      const open = (event: Event) => {
        event.stopPropagation();
        emit({ type: 'link', link });
      };
      span.addEventListener('click', open);
      span.addEventListener('keydown', (event) => {
        if (event.key === 'Enter') {
          open(event);
        }
      });
    }
    paragraph.append(span);
  });
  return paragraph;
}

function createParagraphs(text: IOverlayToastText, emit: IEmit): HTMLElement {
  const container = element('div', 'data-okt-paragraphs');
  text.paragraphs.forEach((runs) =>
    container.append(createParagraph(text, runs, emit))
  );
  return container;
}

function createAction(action: IOverlayToastAction, emit: IEmit): HTMLElement {
  const button = document.createElement('button');
  button.type = 'button';
  button.setAttribute('data-okt-action', '');
  if (action.testID) {
    button.setAttribute('data-testid', action.testID);
  }
  if (action.icon) {
    const icon = createIcon(action.icon) as SVGElement | HTMLElement;
    icon.style.marginRight = `${action.iconGap ?? 0}px`;
    button.append(icon);
  }
  button.append(document.createTextNode(action.label));
  button.disabled = !!action.disabled;
  const { style } = button;
  style.setProperty('--okt-action-bg', action.backgroundColor);
  if (action.hoverBackgroundColor) {
    style.setProperty('--okt-action-hover', action.hoverBackgroundColor);
  }
  if (action.pressedBackgroundColor) {
    style.setProperty('--okt-action-active', action.pressedBackgroundColor);
  }
  if (action.focusRingColor) {
    style.setProperty('--okt-action-ring', action.focusRingColor);
  }
  style.color = action.color;
  style.fontSize = `${action.fontSize}px`;
  style.lineHeight = `${action.lineHeight}px`;
  style.fontWeight = action.fontWeight;
  if (action.fontFamily) {
    style.fontFamily = action.fontFamily;
  }
  style.setProperty(
    '--okt-action-padding',
    `${action.paddingVertical}px ${action.paddingHorizontal}px`
  );
  style.setProperty('--okt-action-radius', `${action.borderRadius}px`);
  style.borderWidth = `${action.borderWidth ?? 0}px`;
  if (action.marginHorizontal || action.marginVertical) {
    style.margin = `${action.marginVertical ?? 0}px ${
      action.marginHorizontal ?? 0
    }px`;
  }
  if (action.disabled && action.disabledOpacity !== undefined) {
    style.opacity = String(action.disabledOpacity);
  }
  button.addEventListener('click', (event) => {
    event.stopPropagation();
    if (!action.disabled) {
      emit({ type: 'action', actionId: action.id });
    }
  });
  return button;
}

function createActions(
  actions: IOverlayToastAction[],
  align: 'left' | 'right',
  padding: string,
  emit: IEmit
): HTMLElement {
  const row = element('div', 'data-okt-actions');
  row.style.justifyContent = align === 'left' ? 'flex-start' : 'flex-end';
  row.style.padding = padding;
  actions.forEach((action) => row.append(createAction(action, emit)));
  return row;
}

/** Builds the body of one card. The caller owns the card element. */
export function buildToastContent(
  spec: IOverlayToastSpec,
  emit: IEmit,
  previous?: HTMLElement,
  statusMotion?: 'enter' | 'settle'
): HTMLElement {
  if (spec.layout === 'notification') {
    const row = element('div', 'data-okt-row');
    row.setAttribute('data-okt-layout', 'notification');
    row.style.flex = '1 1 auto';
    row.style.gap = `${NOTIFICATION_GAP}px`;
    if (spec.pressable) {
      row.setAttribute('data-okt-pressable', '');
      row.setAttribute('role', 'button');
      row.tabIndex = 0;
      row.addEventListener('click', (event) => {
        const target = event.target as HTMLElement | null;
        if (target?.closest?.('button, [data-okt-link]')) {
          return;
        }
        emit({ type: 'press' });
      });
      row.addEventListener('keydown', (event) => {
        const target = event.target as HTMLElement | null;
        if (
          target &&
          target !== row &&
          target.closest?.('button, [data-okt-link]')
        ) {
          return;
        }
        if (event.key === 'Enter' || event.key === ' ') {
          event.preventDefault();
          emit({ type: 'press' });
        }
      });
    }
    const badge = element('div', 'data-okt-badge');
    badge.style.width = `${spec.badgeSize}px`;
    badge.style.height = `${spec.badgeSize}px`;
    badge.style.backgroundColor = spec.badgeColor;
    badge.append(createIcon(spec.icon));
    const column = element('div', 'data-okt-col');
    column.style.gap = `${NOTIFICATION_TEXT_GAP}px`;
    [spec.title, spec.message].forEach((text) => {
      if (text) {
        text.paragraphs.forEach((runs) =>
          column.append(createParagraph(text, runs, emit))
        );
      }
    });
    if (spec.actions?.length) {
      column.append(
        createActions(
          spec.actions,
          spec.actionsAlign ?? 'right',
          `${ACTIONS_PADDING_TOP}px 0 0 0`,
          emit
        )
      );
    }
    row.append(badge, column);
    if (spec.image) {
      row.append(
        createIcon({
          kind: 'image',
          uri: spec.image.uri,
          size: spec.image.size,
          borderRadius: spec.image.borderRadius,
        })
      );
    }
    return row;
  }

  const icon = spec.statusIcon ?? spec.icon;
  const column = element('div', 'data-okt-col');
  column.setAttribute('data-okt-message-col', '');
  column.style.gap = `${MESSAGE_TEXT_GAP}px`;
  if (!icon) {
    column.setAttribute('data-okt-no-icon', '');
  }
  if (spec.title) {
    column.append(createParagraphs(spec.title, emit));
  }
  if (spec.message) {
    column.append(createParagraphs(spec.message, emit));
  }
  if (spec.actions?.length) {
    const actions = createActions(
      spec.actions,
      spec.actionsAlign ?? 'right',
      `${ACTIONS_PADDING_TOP}px ${ACTIONS_PADDING_END}px ${ACTIONS_PADDING_BOTTOM}px 0`,
      emit
    );
    const button = actions.firstElementChild;
    const [action] = spec.actions;
    if (
      action &&
      shouldUseInlineAction(spec) &&
      button instanceof HTMLElement
    ) {
      button.setAttribute('data-okt-inline-candidate', '');
      button.style.setProperty(
        '--okt-inline-padding',
        `${Math.max(action.paddingHorizontal, 14)}px`
      );
    }
    column.append(actions);
  }

  if (icon?.kind === 'status' && previous?.hasAttribute('data-okt-root')) {
    const row = previous.querySelector<HTMLElement>(':scope > [data-okt-row]');
    const tile = row?.querySelector<HTMLElement>(
      ':scope > [data-okt-icon-box] > [data-okt-status-tile]'
    );
    const iconBox = tile?.parentElement;
    if (row && tile && iconBox) {
      updateStatusTile(tile, icon, statusMotion === 'settle');
      if (statusMotion === 'settle') {
        column.setAttribute('data-okt-content-swap', '');
      }
      while (iconBox.nextSibling) {
        iconBox.nextSibling.remove();
      }
      row.style.gap = `${MESSAGE_ICON_GAP}px`;
      row.append(column);
      return previous;
    }
  }
  const root = element('div', 'data-okt-root');
  const row = element('div', 'data-okt-row');
  row.style.gap = `${MESSAGE_ICON_GAP}px`;
  if (icon) {
    const box = element('div', 'data-okt-icon-box');
    box.append(
      icon.kind === 'status'
        ? createStatusTile(icon, statusMotion === 'enter')
        : createIcon(icon)
    );
    row.append(box);
  }
  row.append(column);
  root.append(row);
  return root;
}

export function layoutMessageAction(content: HTMLElement) {
  const button = content.querySelector<HTMLElement>(
    '[data-okt-inline-candidate]'
  );
  const row = button?.closest<HTMLElement>('[data-okt-row]');
  const actions = row?.querySelector<HTMLElement>('[data-okt-actions]');
  if (!button || !row || !actions) {
    return;
  }
  const icon = row.querySelector<HTMLElement>('[data-okt-icon-box]');
  const textStart = icon
    ? icon.offsetWidth + MESSAGE_ICON_GAP
    : TOAST_GEOMETRY.noIconPaddingStart;
  const minTextWidth = row.querySelector('[data-okt-paragraphs]')
    ? TOAST_GEOMETRY.inlineTextMinWidth
    : 0;

  button.setAttribute('data-okt-inline-action', '');
  const fitsInline =
    row.clientWidth - textStart - MESSAGE_ICON_GAP - button.offsetWidth >=
    minTextWidth;
  const focused = document.activeElement === button;
  if (fitsInline) {
    if (button.parentElement !== row) {
      row.append(button);
      actions.hidden = true;
    }
  } else {
    button.removeAttribute('data-okt-inline-action');
    if (button.parentElement !== actions) {
      actions.hidden = false;
      actions.append(button);
    }
  }
  if (focused && document.activeElement !== button) {
    button.focus({ preventScroll: true });
  }
}
