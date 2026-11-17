/* cspell:ignore dasharray dashoffset */
import type {
  IOverlayToastAction,
  IOverlayToastIcon,
  IOverlayToastSpec,
  IOverlayToastText,
  IOverlayToastTextRun,
} from './types';

const SVG_NS = 'http://www.w3.org/2000/svg';

// The card geometry of the layouts this replaces.
const MESSAGE_ICON_GAP = 8;
const MESSAGE_TEXT_GAP = 4;
const NOTIFICATION_GAP = 8;
const NOTIFICATION_TEXT_GAP = 2;
const ACTIONS_PADDING_TOP = 12;
const ACTIONS_PADDING_END = 2;
const ACTIONS_PADDING_BOTTOM = 2;

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

function createIcon(icon: IOverlayToastIcon): Element {
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
    // The indeterminate ring of the activity indicator this replaces.
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
  style.padding = `${action.paddingVertical}px ${action.paddingHorizontal}px`;
  style.borderRadius = `${action.borderRadius}px`;
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
    // A press on an action never reaches the row under it.
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
  emit: IEmit
): HTMLElement {
  if (spec.layout === 'notification') {
    const row = element('div', 'data-okt-row');
    row.style.flex = '1 1 auto';
    row.style.gap = `${NOTIFICATION_GAP}px`;
    if (spec.pressable) {
      row.setAttribute('data-okt-pressable', '');
      row.addEventListener('click', () => emit({ type: 'press' }));
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

  const root = element('div', 'data-okt-root');
  const row = element('div', 'data-okt-row');
  if (spec.icon) {
    row.style.gap = `${MESSAGE_ICON_GAP}px`;
    const box = element('div', 'data-okt-icon-box');
    box.append(createIcon(spec.icon));
    row.append(box);
  }
  const column = element('div', 'data-okt-col');
  column.style.gap = `${MESSAGE_TEXT_GAP}px`;
  if (spec.title) {
    column.append(createParagraphs(spec.title, emit));
  }
  if (spec.message) {
    column.append(createParagraphs(spec.message, emit));
  }
  if (spec.actions?.length) {
    column.append(
      createActions(
        spec.actions,
        spec.actionsAlign ?? 'right',
        `${ACTIONS_PADDING_TOP}px ${ACTIONS_PADDING_END}px ${ACTIONS_PADDING_BOTTOM}px 0`,
        emit
      )
    );
  }
  row.append(column);
  root.append(row);
  return root;
}
