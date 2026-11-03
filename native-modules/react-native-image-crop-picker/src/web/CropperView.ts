/// <reference lib="dom" />
import { ImageCropPickerError } from '../shared';
import {
  INITIAL_STATE,
  cropAspectRatio,
  MAX_ZOOM,
  MIN_ZOOM,
  fitCropBox,
  isSameState,
  pan,
  rescale,
  rotateCounterclockwise,
  scaleOf,
  zoomAt,
} from './geometry';
import { resolveTheme } from './theme';

import type { ImageCropperAppearance } from '../ReactNativeImageCropPicker.nitro';
import type { CropState, Point, Rotation, Size } from './geometry';

// The web counterpart of ImageCropperViewController / ImageCropperActivity:
// the same header, crop area and Cancel / Confirm footer, drawn with the DOM.
// It adds a zoom toolbar, wheel and keyboard control for mouse users.
// Full screen on narrow or short windows, a centered panel otherwise.

export interface CropperViewOptions {
  image: HTMLImageElement;
  // Width / height of the crop box. Undefined keeps the image's own ratio,
  // swapped on every quarter turn, like native without a target size.
  aspectRatio?: number;
  title?: string;
  cancelText?: string;
  confirmText?: string;
  rotateButtonHidden?: boolean;
  showGuidelines?: boolean;
  circular?: boolean;
  appearance?: ImageCropperAppearance;
}

export type CropperConfirmHandler<T> = (
  state: CropState,
  box: Size
) => Promise<T>;

const STYLE_ELEMENT_ID = 'onekey-image-cropper-style';
const ZOOM_STEP = 1.25;
const SLIDER_STEPS = 1000;
const KEYBOARD_PAN = 10;
const KEYBOARD_PAN_FAST = 50;
const GUIDE_LINGER_MS = 400;
const ANIMATION_MS = 220;
// Above app overlays, which stay below 2^31.
const Z_INDEX = 2147483000;

const ICONS = {
  // OneKey's RotateCounterclockwise, the same path the native screens draw.
  rotate:
    'M6 5.426C7.628 3.919 9.484 3 12.028 3a9 9 0 1 1-8.487 12l-.334-.943 1.886-.666.333.942A7 7 0 1 0 12.029 5c-1.982 0-3.407.686-4.785 2H10v2H4V3h2v2.426Z',
  zoomIn:
    'M12 10h3v2h-3v3h-2v-3H7v-2h3V7h2v3Z M11 3a8 8 0 0 1 8 8c0 1.849-.63 3.549-1.683 4.903L21.414 20 20 21.414l-4.097-4.097A7.962 7.962 0 0 1 11 19a8 8 0 1 1 0-16Zm0 2a6 6 0 1 0 0 12 6 6 0 0 0 0-12Z',
  zoomOut:
    'M15 12H7v-2h8v2Z M11 3a8 8 0 0 1 8 8c0 1.849-.63 3.549-1.683 4.903L21.414 20 20 21.414l-4.097-4.097A7.962 7.962 0 0 1 11 19a8 8 0 1 1 0-16Zm0 2a6 6 0 1 0 0 12 6 6 0 0 0 0-12Z',
  reset:
    'M5 12a7 7 0 0 0 7 7 7.15 7.15 0 0 0 4.955-2h-2.943v-2h6v6h-2v-2.257A9.148 9.148 0 0 1 12 21a9 9 0 0 1-9-9v-1h2v1Zm1-6.754A9.148 9.148 0 0 1 12 3a9 9 0 0 1 9 9v1h-2v-1a7 7 0 0 0-7-7 7.15 7.15 0 0 0-4.955 2H10v2H4V3h2v2.246Z',
} as const;

// Every size is `calc(N * var(--okicp-s))`, so `appearance.scale` scales the
// whole screen the way it scales the native metrics.
const STYLES = `
.okicp-root{position:fixed;inset:0;z-index:${Z_INDEX};display:flex;align-items:center;justify-content:center;background:rgba(0,0,0,.5);-webkit-tap-highlight-color:transparent;overscroll-behavior:contain;animation:okicp-fade .15s ease-out}
.okicp-panel{position:relative;display:flex;flex-direction:column;box-sizing:border-box;width:min(calc(560px * var(--okicp-s)),calc(100% - 48px));height:min(calc(760px * var(--okicp-s)),calc(100% - 48px));background:var(--okicp-bg);color:var(--okicp-title);border-radius:calc(16px * var(--okicp-s));overflow:hidden;outline:none;box-shadow:0 0 0 1px rgba(0,0,0,.06),0 24px 48px rgba(0,0,0,.24);animation:okicp-pop .18s cubic-bezier(.2,.9,.3,1)}
@media (max-width:639px),(max-height:559px){.okicp-panel{width:100%;height:100%;border-radius:0;box-shadow:none;padding:env(safe-area-inset-top) env(safe-area-inset-right) 0 env(safe-area-inset-left)}.okicp-footer{padding-bottom:max(calc(20px * var(--okicp-s)),calc(env(safe-area-inset-bottom) + 10px))}}
.okicp-header{position:relative;flex:none;display:flex;align-items:center;justify-content:center;height:calc(56px * var(--okicp-s));padding:0 calc(60px * var(--okicp-s))}
.okicp-title{margin:0;font-family:var(--okicp-title-font);font-size:calc(18px * var(--okicp-s));line-height:calc(28px * var(--okicp-s));font-weight:600;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.okicp-icon{display:inline-flex;align-items:center;justify-content:center;flex:none;width:calc(40px * var(--okicp-s));height:calc(40px * var(--okicp-s));padding:0;border:0;border-radius:9999px;background:transparent;color:var(--okicp-icon);cursor:pointer;transition:background-color .12s,opacity .12s}
.okicp-icon svg{width:calc(24px * var(--okicp-s));height:calc(24px * var(--okicp-s));fill:currentColor}
.okicp-icon:hover{background:var(--okicp-cancel)}
.okicp-icon:active{background:var(--okicp-cancel-pressed)}
.okicp-icon:disabled{opacity:.3;cursor:default;background:transparent}
.okicp-header .okicp-icon{position:absolute;right:calc(12px * var(--okicp-s));top:50%;transform:translateY(-50%)}
.okicp-stage{position:relative;flex:1 1 auto;min-height:0;overflow:hidden;touch-action:none;user-select:none;-webkit-user-select:none;cursor:grab;outline:none}
.okicp-stage.okicp-dragging{cursor:grabbing}
.okicp-stage:focus-visible .okicp-box{outline:2px solid var(--okicp-title);outline-offset:3px}
.okicp-image{position:absolute;left:50%;top:50%;max-width:none;max-height:none;transform-origin:center;pointer-events:none;-webkit-user-drag:none;will-change:transform}
.okicp-animate .okicp-image{transition:transform ${ANIMATION_MS}ms cubic-bezier(.2,.8,.2,1)}
.okicp-box{position:absolute;left:50%;top:50%;box-sizing:border-box;border:1px solid var(--okicp-title);box-shadow:0 0 0 9999px var(--okicp-dim);pointer-events:none}
.okicp-circle{border-radius:50%;border-color:transparent}
.okicp-grid{position:absolute;inset:0;opacity:0;transition:opacity .2s;background-image:linear-gradient(rgba(255,255,255,.8),rgba(255,255,255,.8)),linear-gradient(rgba(255,255,255,.8),rgba(255,255,255,.8)),linear-gradient(rgba(255,255,255,.8),rgba(255,255,255,.8)),linear-gradient(rgba(255,255,255,.8),rgba(255,255,255,.8));background-size:1px 100%,1px 100%,100% 1px,100% 1px;background-position:33.333% 0,66.667% 0,0 33.333%,0 66.667%;background-repeat:no-repeat}
.okicp-guides .okicp-grid{opacity:1}
.okicp-toolbar{flex:none;display:flex;align-items:center;gap:calc(4px * var(--okicp-s));padding:calc(12px * var(--okicp-s)) calc(16px * var(--okicp-s)) 0}
.okicp-slider{flex:1;min-width:0;height:calc(40px * var(--okicp-s));margin:0 calc(4px * var(--okicp-s));accent-color:var(--okicp-confirm);cursor:pointer;background:transparent}
.okicp-footer{flex:none;display:flex;gap:calc(10px * var(--okicp-s));padding:calc(20px * var(--okicp-s))}
.okicp-button{position:relative;flex:1 1 0;min-width:0;display:inline-flex;align-items:center;justify-content:center;gap:calc(8px * var(--okicp-s));height:calc(50px * var(--okicp-s));padding:0 calc(21px * var(--okicp-s));border:0;border-radius:9999px;font-family:var(--okicp-button-font);font-size:calc(16px * var(--okicp-s));line-height:calc(24px * var(--okicp-s));font-weight:500;cursor:pointer;transition:background-color .12s,opacity .12s;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.okicp-cancel{background:var(--okicp-cancel);color:var(--okicp-cancel-text)}
.okicp-cancel:active{background:var(--okicp-cancel-pressed)}
.okicp-confirm{background:var(--okicp-confirm);color:var(--okicp-confirm-text)}
.okicp-confirm:active{background:var(--okicp-confirm-pressed)}
.okicp-button:disabled{cursor:default}
.okicp-cancel:disabled{opacity:.5}
.okicp-button:focus-visible,.okicp-icon:focus-visible{outline:2px solid var(--okicp-title);outline-offset:2px}
.okicp-spinner{display:none;flex:none;width:calc(16px * var(--okicp-s));height:calc(16px * var(--okicp-s));border:2px solid currentColor;border-right-color:transparent;border-radius:50%;animation:okicp-spin .7s linear infinite}
.okicp-processing .okicp-spinner{display:inline-block}
.okicp-processing .okicp-stage{pointer-events:none;cursor:default}
@keyframes okicp-spin{to{transform:rotate(360deg)}}
@keyframes okicp-fade{from{opacity:0}}
@keyframes okicp-pop{from{opacity:0;transform:scale(.96)}}
@media (prefers-reduced-motion:reduce){.okicp-root,.okicp-panel{animation:none}.okicp-animate .okicp-image{transition:none}}
`;

function ensureStyles(): void {
  if (document.getElementById(STYLE_ELEMENT_ID)) {
    return;
  }
  const style = document.createElement('style');
  style.id = STYLE_ELEMENT_ID;
  style.textContent = STYLES;
  document.head.appendChild(style);
}

function element<K extends keyof HTMLElementTagNameMap>(
  tag: K,
  className?: string
): HTMLElementTagNameMap[K] {
  const node = document.createElement(tag);
  if (className) {
    node.className = className;
  }
  return node;
}

function iconButton(path: string, label: string): HTMLButtonElement {
  const button = element('button', 'okicp-icon');
  button.type = 'button';
  button.setAttribute('aria-label', label);
  button.title = label;
  const svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
  svg.setAttribute('viewBox', '0 0 24 24');
  svg.setAttribute('aria-hidden', 'true');
  const shape = document.createElementNS('http://www.w3.org/2000/svg', 'path');
  shape.setAttribute('d', path);
  shape.setAttribute('fill-rule', 'evenodd');
  svg.appendChild(shape);
  button.appendChild(svg);
  return button;
}

function textButton(className: string, text: string): HTMLButtonElement {
  const button = element('button', `okicp-button ${className}`);
  button.type = 'button';
  const label = element('span');
  label.textContent = text;
  button.append(element('span', 'okicp-spinner'), label);
  return button;
}

function zoomToSlider(zoom: number): number {
  return Math.round(
    (Math.log(zoom / MIN_ZOOM) / Math.log(MAX_ZOOM / MIN_ZOOM)) * SLIDER_STEPS
  );
}

function sliderToZoom(value: number): number {
  return MIN_ZOOM * (MAX_ZOOM / MIN_ZOOM) ** (value / SLIDER_STEPS);
}

let titleIdCounter = 0;

export function presentCropper<T>(
  options: CropperViewOptions,
  onConfirm: CropperConfirmHandler<T>
): Promise<T> {
  return new Promise<T>((resolve, reject) => {
    ensureStyles();
    const theme = resolveTheme(options.appearance);
    const imageSize: Size = {
      width: options.image.naturalWidth,
      height: options.image.naturalHeight,
    };
    const aspectFor = (rotation: Rotation) =>
      cropAspectRatio({
        image: imageSize,
        rotation,
        targetAspectRatio: options.aspectRatio,
        circular: options.circular,
      });
    // Native hides the grid in circular mode.
    const showGuidelines =
      options.showGuidelines !== false && !options.circular;

    let state: CropState = INITIAL_STATE;
    // Cumulative CSS angle, so a turn from 270° to 0° keeps going
    // counterclockwise instead of spinning back.
    let visualRotation = 0;
    let stageSize: Size = { width: 0, height: 0 };
    let box: Size = { width: 0, height: 0 };
    let processing = false;
    let closed = false;
    let animationTimer: ReturnType<typeof setTimeout> | undefined;
    let guideTimer: ReturnType<typeof setTimeout> | undefined;
    const pointers = new Map<number, Point>();
    const previousFocus =
      document.activeElement instanceof HTMLElement
        ? document.activeElement
        : null;

    // DOM
    const root = element('div', 'okicp-root');
    const titleId = `okicp-title-${(titleIdCounter += 1)}`;
    root.setAttribute('role', 'dialog');
    root.setAttribute('aria-modal', 'true');
    root.setAttribute('aria-labelledby', titleId);
    root.style.setProperty('--okicp-s', String(theme.scale));
    root.style.setProperty('--okicp-bg', theme.backgroundColor);
    root.style.setProperty('--okicp-dim', theme.dimmedColor);
    root.style.setProperty('--okicp-title', theme.titleColor);
    root.style.setProperty('--okicp-icon', theme.iconColor);
    root.style.setProperty('--okicp-cancel', theme.cancelButtonColor);
    root.style.setProperty(
      '--okicp-cancel-pressed',
      theme.cancelButtonPressedColor
    );
    root.style.setProperty('--okicp-cancel-text', theme.cancelButtonTextColor);
    root.style.setProperty('--okicp-confirm', theme.confirmButtonColor);
    root.style.setProperty(
      '--okicp-confirm-pressed',
      theme.confirmButtonPressedColor
    );
    root.style.setProperty(
      '--okicp-confirm-text',
      theme.confirmButtonTextColor
    );
    root.style.setProperty(
      '--okicp-title-font',
      theme.titleFontFamily
        ? `"${theme.titleFontFamily}", system-ui`
        : 'inherit'
    );
    root.style.setProperty(
      '--okicp-button-font',
      theme.buttonFontFamily
        ? `"${theme.buttonFontFamily}", system-ui`
        : 'inherit'
    );
    root.style.colorScheme = theme.colorScheme;

    const panel = element('div', 'okicp-panel');
    panel.tabIndex = -1;

    const header = element('header', 'okicp-header');
    const title = element('h2', 'okicp-title');
    title.id = titleId;
    title.textContent = options.title || 'Crop image';
    header.appendChild(title);
    const rotateButton = iconButton(ICONS.rotate, 'Rotate');
    if (!options.rotateButtonHidden) {
      header.appendChild(rotateButton);
    }

    const stage = element('div', 'okicp-stage');
    stage.tabIndex = 0;
    stage.setAttribute('role', 'application');
    stage.setAttribute(
      'aria-label',
      'Crop area. Drag or use arrow keys to move, scroll or + and - to zoom.'
    );
    const image = options.image;
    image.className = 'okicp-image';
    image.alt = '';
    image.draggable = false;
    image.style.width = `${imageSize.width}px`;
    image.style.height = `${imageSize.height}px`;
    image.style.marginLeft = `${-imageSize.width / 2}px`;
    image.style.marginTop = `${-imageSize.height / 2}px`;
    const cropBox = element(
      'div',
      options.circular ? 'okicp-box okicp-circle' : 'okicp-box'
    );
    if (showGuidelines) {
      cropBox.appendChild(element('div', 'okicp-grid'));
    }
    stage.append(image, cropBox);

    const toolbar = element('div', 'okicp-toolbar');
    const zoomOutButton = iconButton(ICONS.zoomOut, 'Zoom out');
    const zoomInButton = iconButton(ICONS.zoomIn, 'Zoom in');
    const resetButton = iconButton(ICONS.reset, 'Reset');
    const slider = element('input', 'okicp-slider');
    slider.type = 'range';
    slider.min = '0';
    slider.max = String(SLIDER_STEPS);
    slider.step = '1';
    slider.setAttribute('aria-label', 'Zoom');
    toolbar.append(zoomOutButton, slider, zoomInButton, resetButton);

    const footer = element('footer', 'okicp-footer');
    const cancelButton = textButton(
      'okicp-cancel',
      options.cancelText || 'Cancel'
    );
    const confirmButton = textButton(
      'okicp-confirm',
      options.confirmText || 'Confirm'
    );
    footer.append(cancelButton, confirmButton);

    panel.append(header, stage, toolbar, footer);
    root.appendChild(panel);

    // Rendering
    const render = () => {
      cropBox.style.width = `${box.width}px`;
      cropBox.style.height = `${box.height}px`;
      cropBox.style.marginLeft = `${-box.width / 2}px`;
      cropBox.style.marginTop = `${-box.height / 2}px`;
      const scale = box.width > 0 ? scaleOf(state, imageSize, box) : 0;
      image.style.transform = `translate(${state.offsetX}px, ${
        state.offsetY
      }px) rotate(${-visualRotation}deg) scale(${scale})`;
      slider.value = String(zoomToSlider(state.zoom));
      zoomOutButton.disabled = processing || state.zoom <= MIN_ZOOM + 0.001;
      zoomInButton.disabled = processing || state.zoom >= MAX_ZOOM - 0.001;
      resetButton.disabled = processing || isSameState(state, INITIAL_STATE);
      rotateButton.disabled = processing;
      slider.disabled = processing;
      cancelButton.disabled = processing;
      confirmButton.disabled = processing || box.width === 0;
      confirmButton.setAttribute('aria-busy', String(processing));
    };

    const animateNext = () => {
      stage.classList.add('okicp-animate');
      if (animationTimer) {
        clearTimeout(animationTimer);
      }
      animationTimer = setTimeout(() => {
        stage.classList.remove('okicp-animate');
      }, ANIMATION_MS + 30);
    };

    const showGuides = (linger: boolean) => {
      if (!showGuidelines) {
        return;
      }
      stage.classList.add('okicp-guides');
      if (guideTimer) {
        clearTimeout(guideTimer);
        guideTimer = undefined;
      }
      if (linger) {
        guideTimer = setTimeout(() => {
          stage.classList.remove('okicp-guides');
        }, GUIDE_LINGER_MS);
      }
    };

    const layout = () => {
      const rect = stage.getBoundingClientRect();
      const nextStage = { width: rect.width, height: rect.height };
      if (
        nextStage.width === stageSize.width &&
        nextStage.height === stageSize.height
      ) {
        return;
      }
      stageSize = nextStage;
      const nextBox = fitCropBox(stageSize, aspectFor(state.rotation));
      state = rescale(state, box, nextBox, imageSize);
      box = nextBox;
      render();
    };

    const update = (next: CropState, animated = false) => {
      if (animated) {
        animateNext();
      }
      state = next;
      render();
    };

    const anchorOf = (clientX: number, clientY: number): Point => {
      const rect = stage.getBoundingClientRect();
      return {
        x: clientX - rect.left - rect.width / 2,
        y: clientY - rect.top - rect.height / 2,
      };
    };

    const zoomBy = (factor: number, anchor: Point = { x: 0, y: 0 }) => {
      update(zoomAt(state, state.zoom * factor, anchor, imageSize, box), true);
    };

    const rotate = () => {
      if (processing) {
        return;
      }
      visualRotation += 90;
      // An unlocked crop box swaps its ratio with the image.
      const nextBox = fitCropBox(
        stageSize,
        aspectFor(((state.rotation + 90) % 360) as Rotation)
      );
      const rotated = rotateCounterclockwise(state, imageSize, nextBox);
      box = nextBox;
      update(rotated, true);
    };

    const reset = () => {
      // Equivalent angle, so only position and zoom animate back.
      visualRotation -= state.rotation;
      if (visualRotation % 360 !== 0) {
        visualRotation = Math.round(visualRotation / 360) * 360;
      }
      box = fitCropBox(stageSize, aspectFor(0));
      update(INITIAL_STATE, true);
    };

    // Teardown
    const resizeObserver =
      typeof ResizeObserver === 'function'
        ? new ResizeObserver(() => layout())
        : undefined;

    const close = () => {
      if (closed) {
        return;
      }
      closed = true;
      resizeObserver?.disconnect();
      window.removeEventListener('resize', layout);
      if (animationTimer) {
        clearTimeout(animationTimer);
      }
      if (guideTimer) {
        clearTimeout(guideTimer);
      }
      root.remove();
      previousFocus?.focus?.();
    };

    const cancel = () => {
      if (processing || closed) {
        return;
      }
      close();
      reject(
        new ImageCropPickerError(
          'E_PICKER_CANCELLED',
          'User cancelled image selection'
        )
      );
    };

    const confirm = () => {
      if (processing || closed || box.width === 0) {
        return;
      }
      processing = true;
      panel.classList.add('okicp-processing');
      render();
      const finalState = state;
      const finalBox = box;
      // Let the spinner paint before the synchronous canvas work.
      requestAnimationFrame(() => {
        setTimeout(() => {
          onConfirm(finalState, finalBox).then(
            (result) => {
              close();
              resolve(result);
            },
            (error: unknown) => {
              close();
              reject(error);
            }
          );
        }, 0);
      });
    };

    // Pointer: one pointer pans, two pinch around their midpoint.
    let lastMidpoint: Point | undefined;
    let lastDistance = 0;
    const pinchMetrics = () => {
      const [a, b] = [...pointers.values()];
      if (!a || !b) {
        return undefined;
      }
      return {
        midpoint: { x: (a.x + b.x) / 2, y: (a.y + b.y) / 2 },
        distance: Math.hypot(a.x - b.x, a.y - b.y),
      };
    };

    stage.addEventListener('pointerdown', (event) => {
      if (processing || (event.pointerType === 'mouse' && event.button !== 0)) {
        return;
      }
      stage.setPointerCapture(event.pointerId);
      pointers.set(event.pointerId, { x: event.clientX, y: event.clientY });
      stage.classList.add('okicp-dragging');
      stage.classList.remove('okicp-animate');
      showGuides(false);
      const pinch = pinchMetrics();
      lastMidpoint = pinch?.midpoint;
      lastDistance = pinch?.distance ?? 0;
    });

    stage.addEventListener('pointermove', (event) => {
      const previous = pointers.get(event.pointerId);
      if (!previous) {
        return;
      }
      const current = { x: event.clientX, y: event.clientY };
      pointers.set(event.pointerId, current);
      if (pointers.size === 1) {
        update(
          pan(
            state,
            current.x - previous.x,
            current.y - previous.y,
            imageSize,
            box
          )
        );
        return;
      }
      const pinch = pinchMetrics();
      if (!pinch || !lastMidpoint || lastDistance === 0) {
        return;
      }
      let next = zoomAt(
        state,
        state.zoom * (pinch.distance / lastDistance),
        anchorOf(pinch.midpoint.x, pinch.midpoint.y),
        imageSize,
        box
      );
      next = pan(
        next,
        pinch.midpoint.x - lastMidpoint.x,
        pinch.midpoint.y - lastMidpoint.y,
        imageSize,
        box
      );
      lastMidpoint = pinch.midpoint;
      lastDistance = pinch.distance;
      update(next);
    });

    const endPointer = (event: PointerEvent) => {
      if (!pointers.delete(event.pointerId)) {
        return;
      }
      const pinch = pinchMetrics();
      lastMidpoint = pinch?.midpoint;
      lastDistance = pinch?.distance ?? 0;
      if (pointers.size === 0) {
        stage.classList.remove('okicp-dragging');
        showGuides(true);
      }
    };
    stage.addEventListener('pointerup', endPointer);
    stage.addEventListener('pointercancel', endPointer);

    // Trackpad pinch arrives as a ctrl+wheel event with small deltas.
    stage.addEventListener(
      'wheel',
      (event) => {
        event.preventDefault();
        if (processing) {
          return;
        }
        const lineHeight = event.deltaMode === 1 ? 16 : 1;
        const delta = event.deltaY * lineHeight;
        const factor = Math.exp(-delta * (event.ctrlKey ? 0.01 : 0.0015));
        stage.classList.remove('okicp-animate');
        update(
          zoomAt(
            state,
            state.zoom * factor,
            anchorOf(event.clientX, event.clientY),
            imageSize,
            box
          )
        );
        showGuides(true);
      },
      { passive: false }
    );

    stage.addEventListener('dblclick', (event) => {
      if (processing) {
        return;
      }
      if (state.zoom < 1.5) {
        update(
          zoomAt(
            state,
            2,
            anchorOf(event.clientX, event.clientY),
            imageSize,
            box
          ),
          true
        );
      } else {
        update(zoomAt(state, MIN_ZOOM, { x: 0, y: 0 }, imageSize, box), true);
      }
    });

    slider.addEventListener('input', () => {
      stage.classList.remove('okicp-animate');
      update(
        zoomAt(
          state,
          sliderToZoom(Number(slider.value)),
          { x: 0, y: 0 },
          imageSize,
          box
        )
      );
    });

    rotateButton.addEventListener('click', rotate);
    zoomInButton.addEventListener('click', () => zoomBy(ZOOM_STEP));
    zoomOutButton.addEventListener('click', () => zoomBy(1 / ZOOM_STEP));
    resetButton.addEventListener('click', reset);
    cancelButton.addEventListener('click', cancel);
    confirmButton.addEventListener('click', confirm);

    root.addEventListener('keydown', (event) => {
      if (event.key === 'Escape') {
        event.preventDefault();
        cancel();
        return;
      }
      if (event.key === 'Tab') {
        // Keep focus inside the cropper.
        const focusable = [
          stage,
          ...panel.querySelectorAll<HTMLElement>(
            'button:not(:disabled), input:not(:disabled)'
          ),
        ].filter((node) => node.isConnected && node.offsetParent !== null);
        const first = focusable[0];
        const last = focusable[focusable.length - 1];
        if (!first || !last) {
          return;
        }
        if (event.shiftKey && document.activeElement === first) {
          event.preventDefault();
          last.focus();
        } else if (!event.shiftKey && document.activeElement === last) {
          event.preventDefault();
          first.focus();
        } else if (!panel.contains(document.activeElement)) {
          event.preventDefault();
          first.focus();
        }
        return;
      }
      const target = event.target as HTMLElement | null;
      if (target?.tagName === 'BUTTON' || target === slider) {
        // Buttons and the slider handle their own keys.
        return;
      }
      if (event.metaKey || event.ctrlKey || event.altKey) {
        return;
      }
      const step = event.shiftKey ? KEYBOARD_PAN_FAST : KEYBOARD_PAN;
      switch (event.key) {
        case 'Enter':
          event.preventDefault();
          confirm();
          break;
        case 'ArrowLeft':
          event.preventDefault();
          update(pan(state, -step, 0, imageSize, box));
          break;
        case 'ArrowRight':
          event.preventDefault();
          update(pan(state, step, 0, imageSize, box));
          break;
        case 'ArrowUp':
          event.preventDefault();
          update(pan(state, 0, -step, imageSize, box));
          break;
        case 'ArrowDown':
          event.preventDefault();
          update(pan(state, 0, step, imageSize, box));
          break;
        case '+':
        case '=':
          event.preventDefault();
          zoomBy(ZOOM_STEP);
          break;
        case '-':
        case '_':
          event.preventDefault();
          zoomBy(1 / ZOOM_STEP);
          break;
        case 'r':
        case 'R':
          if (!options.rotateButtonHidden) {
            event.preventDefault();
            rotate();
          }
          break;
        default:
          break;
      }
    });

    // Mount
    document.body.appendChild(root);
    if (resizeObserver) {
      resizeObserver.observe(stage);
    } else {
      window.addEventListener('resize', layout);
    }
    layout();
    stage.focus({ preventScroll: true });
  });
}
