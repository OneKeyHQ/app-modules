/* cspell:ignore Neue Noto */
import {
  getToastWebSprings,
  TOAST_GEOMETRY,
  TOAST_MOTION,
} from './toastMotion';

const STYLE_ID = 'onekey-overlay-toast-styles';

function cubic(tuple: readonly [number, number, number, number]): string {
  return `cubic-bezier(${tuple.join(',')})`;
}

function buildOverlayToastCss(): string {
  const springs = getToastWebSprings();
  const tileDelayMs = TOAST_MOTION.arrivalDelayMs - TOAST_MOTION.tileLeadMs;
  const glyphDelayMs = TOAST_MOTION.arrivalDelayMs;
  const drawDelayMs =
    TOAST_MOTION.arrivalDelayMs + TOAST_MOTION.strokeDelayOffsetMs;
  const settleDrawDelayMs =
    TOAST_MOTION.glyphLeaveMs + TOAST_MOTION.strokeDelayOffsetMs;
  const cardExitEase = cubic(TOAST_MOTION.cardExitEase);
  const cardAutoExitEase = cubic(TOAST_MOTION.cardAutoExitEase);
  const strokeEase = cubic(TOAST_MOTION.strokeEase);
  const leaveEase = cubic(TOAST_MOTION.glyphLeaveEase);
  const bumpEase = cubic(TOAST_MOTION.tileBumpEase);
  const tintEase = cubic(TOAST_MOTION.tileTintEase);

  return `
:where(html[dir="ltr"]),:where([data-onekey-toast-toaster][dir="ltr"]){--toast-close-button-start:-6px;--toast-close-button-end:unset}
:where(html[dir="rtl"]),:where([data-onekey-toast-toaster][dir="rtl"]){--toast-close-button-start:unset;--toast-close-button-end:-6px}
:where([data-onekey-toast-toaster]){
  position:fixed;
  --toast-inset:max(16px,env(safe-area-inset-left),env(safe-area-inset-right));
  --toast-vertical-offset:var(--offset);
  --toast-segment-width:100vw;
  --toast-segment-top:0px;
  --toast-segment-bottom:100vh;
  width:min(var(--width),max(0px,calc(var(--toast-segment-width) - var(--toast-inset) * 2)));
  font-family:ui-sans-serif,system-ui,-apple-system,BlinkMacSystemFont,Segoe UI,Roboto,Helvetica Neue,Arial,Noto Sans,sans-serif,Apple Color Emoji,Segoe UI Emoji,Segoe UI Symbol,Noto Color Emoji;
  --okt-card-radius:${TOAST_GEOMETRY.cardRadius}px;
  --okt-card-padding:${TOAST_GEOMETRY.cardPadding}px;
  --okt-card-spring-duration:${springs.card.durationMs}ms;
  --okt-card-spring-ease:cubic-bezier(.22,1,.36,1);
  --okt-card-exit-duration:${TOAST_MOTION.cardExitMs}ms;
  --okt-card-exit-ease:${cardExitEase};
  --okt-card-auto-exit-duration:${TOAST_MOTION.cardAutoExitMs}ms;
  --okt-card-auto-exit-ease:${cardAutoExitEase};
  box-sizing:border-box;
  padding:0;
  margin:0;
  list-style:none;
  outline:none;
  z-index:0;
}
:where([data-onekey-toast-toaster][data-x-position="right"]){right:max(var(--offset),env(safe-area-inset-right))}
:where([data-onekey-toast-toaster][data-x-position="left"]){left:max(var(--offset),env(safe-area-inset-left))}
:where([data-onekey-toast-toaster][data-x-position="center"]){left:50%;transform:translate(-50%)}
:where([data-onekey-toast-toaster][data-y-position="top"]){top:max(var(--toast-obstruction-top,0px),calc(var(--toast-segment-top) + max(var(--toast-vertical-offset),env(safe-area-inset-top))))}
:where([data-onekey-toast-toaster][data-y-position="bottom"]){bottom:calc(100vh - var(--toast-segment-bottom) + max(var(--toast-vertical-offset),env(safe-area-inset-bottom)))}
:where([data-onekey-toast]){
  --y:translateY(calc(100% + 16px)) scale(.96);
  --lift-amount:calc(var(--lift) * var(--gap) * var(--toast-pane-scale,1));
  z-index:var(--z-index);
  position:absolute;
  width:100%;
  inset-inline:0;
  opacity:0;
  transform:translateY(var(--drag-y,0px)) var(--y) scale(var(--toast-pane-scale,1));
  touch-action:none;
  transition:transform var(--okt-card-spring-duration) var(--okt-card-spring-ease),opacity 220ms ease-out,height var(--okt-card-spring-duration) var(--okt-card-spring-ease);
  box-sizing:border-box;
  outline:none;
  overflow-wrap:anywhere;
}
:where([data-onekey-toast][data-y-position="top"]){top:0;--y:translateY(calc(-100% - 16px)) scale(.96);--lift:1}
:where([data-onekey-toast][data-y-position="bottom"]){bottom:0;--y:translateY(calc(100% + 16px)) scale(.96);--lift:-1}
:where([data-onekey-toast][data-pane-constrained][data-y-position="top"]){transform-origin:center top}
:where([data-onekey-toast][data-pane-constrained][data-y-position="bottom"]){transform-origin:center bottom}
:where([data-onekey-toast][data-swiping="true"]):before{content:"";position:absolute;left:0;right:0;height:100%;z-index:-1}
:where([data-onekey-toast][data-y-position="top"][data-swiping="true"]):before{bottom:50%;transform:scaleY(3) translateY(50%)}
:where([data-onekey-toast][data-y-position="bottom"][data-swiping="true"]):before{top:50%;transform:scaleY(3) translateY(-50%)}
:where([data-onekey-toast][data-swiping="false"][data-removed="true"]):before{content:"";position:absolute;inset:0;transform:scaleY(2)}
:where([data-onekey-toast]):after{content:"";position:absolute;left:0;height:calc(var(--gap) + 1px);bottom:100%;width:100%}
:where([data-onekey-toast][data-mounted="true"]){--y:translateY(0) scale(1);opacity:1}
:where([data-onekey-toast][data-expanded="false"][data-front="false"]){
  --scale:calc(1 - var(--toasts-before) * .05);
  --y:translateY(calc(var(--lift-amount) * var(--toasts-before))) scale(var(--scale));
  height:var(--front-toast-height);
}
:where([data-onekey-toast][data-visible="false"]){opacity:0;pointer-events:none}
:where([data-onekey-toast][data-mounted="true"][data-expanded="true"]){--y:translateY(calc(var(--lift) * var(--offset))) scale(1);height:var(--initial-height)}
:where([data-onekey-toast][data-removed="true"][data-swipe-out="false"]){
  transition:transform var(--okt-card-auto-exit-duration) var(--okt-card-auto-exit-ease),opacity var(--okt-card-auto-exit-duration) var(--okt-card-auto-exit-ease),height var(--okt-card-auto-exit-duration) var(--okt-card-auto-exit-ease);
}
:where([data-onekey-toast][data-removed="true"][data-front="true"][data-swipe-out="false"]){--y:translateY(calc(var(--lift) * -${TOAST_MOTION.cardAutoExitOffset}px)) scale(${TOAST_MOTION.cardAutoExitScale});opacity:0}
:where([data-onekey-toast][data-removed="true"][data-front="false"][data-swipe-out="false"][data-expanded="true"]){--y:translateY(calc(var(--lift) * var(--offset) + var(--lift) * -${TOAST_MOTION.cardAutoExitOffset}px)) scale(${TOAST_MOTION.cardAutoExitScale});opacity:0}
:where([data-onekey-toast][data-removed="true"][data-front="false"][data-swipe-out="false"][data-expanded="false"]){--y:translateY(calc(var(--lift-amount) * var(--toasts-before) + var(--lift) * -${TOAST_MOTION.collapsedBackExitOffset}px)) scale(${TOAST_MOTION.collapsedBackExitScale});opacity:0}
:where([data-onekey-toast][data-removed="true"][data-front="false"]):before{height:calc(var(--initial-height) + 20%)}
[data-onekey-toast][data-swiping="true"]{transform:translateX(var(--swipe-amount,0px)) var(--y) scale(var(--toast-pane-scale,1));opacity:var(--swipe-opacity,1);transition:none}
[data-onekey-toast][data-swipe-out="true"][data-y-position="bottom"],[data-onekey-toast][data-swipe-out="true"][data-y-position="top"]{animation:onekey-toast-swipe-out ${TOAST_MOTION.cardExitMs}ms ${cardExitEase} forwards}
/* A touch stack drag moves every card of the stack with the finger; a stack dismissal leaves past the anchored edge. */
[data-onekey-toast][data-stack-drag="true"]{transition:none}
[data-onekey-toast][data-removed="true"][data-swipe-out="true"][data-swipe-axis="y"]{animation:onekey-toast-stack-swipe-out ${TOAST_MOTION.cardExitMs}ms ${cardExitEase} forwards}
@keyframes onekey-toast-stack-swipe-out{
  0%{transform:translateY(var(--drag-y,0px)) var(--y) scale(var(--toast-pane-scale,1));opacity:1}
  to{transform:translateY(var(--stack-exit-y,0px)) var(--y) scale(var(--toast-pane-scale,1));opacity:0}
}
@keyframes onekey-toast-swipe-out{
  0%{transform:translateY(calc(var(--lift) * var(--offset))) translateX(var(--swipe-amount,0px)) scale(var(--toast-pane-scale,1));opacity:var(--swipe-opacity,1)}
  to{transform:translateY(calc(var(--lift) * var(--offset))) translateX(var(--swipe-exit-x)) scale(calc(var(--toast-pane-scale,1) * ${TOAST_MOTION.swipeExitScale}));opacity:0}
}

/* Raised concentric card with outer 1px ring + layered box-shadow (no clip-path/filter haze) */
[data-onekey-toast-toaster][data-theme="light"]{
  --okt-surface-bg:#fff;
  --okt-surface-border:rgba(0,0,0,.06);
  --okt-surface-text:#111;
  --okt-surface-shadow:0 1px 3px rgba(0,0,0,.05),0 4px 10px -4px rgba(0,0,0,.06);
  --okt-close-bg:#fff;
  --okt-close-text:#868593;
  --okt-close-hover-text:#000;
  --okt-close-ring:0 0 0 1px rgba(0,0,0,.08);
  --okt-focus-ring:rgba(0,0,0,.22);
}
[data-onekey-toast-toaster][data-theme="dark"]{
  --okt-surface-bg:#1c1c1e;
  --okt-surface-border:rgba(255,255,255,.09);
  --okt-surface-text:#fff;
  --okt-surface-shadow:0 4px 12px -2px rgba(0,0,0,.36);
  --okt-close-bg:#2c2c2e;
  --okt-close-text:#9b9aa7;
  --okt-close-hover-text:#fff;
  --okt-close-ring:0 0 0 1px rgba(255,255,255,.1);
  --okt-focus-ring:rgba(255,255,255,.32);
}
[data-okt-shell]{
  position:relative;
  width:100%;
  height:100%;
  box-sizing:border-box;
}
[data-okt-surface]{
  position:relative;
  display:flex;
  align-items:center;
  width:100%;
  height:100%;
  padding:var(--okt-card-padding);
  border-radius:var(--okt-card-radius);
  background:var(--okt-card-bg,var(--okt-surface-bg));
  color:var(--okt-surface-text);
  box-shadow:0 0 0 1px var(--okt-card-border,var(--okt-surface-border)),var(--okt-surface-shadow);
  box-sizing:border-box;
  overflow:hidden;
}
:where([data-onekey-toast]:focus-visible) [data-okt-surface]{
  box-shadow:0 0 0 1px var(--okt-card-border,var(--okt-surface-border)),var(--okt-surface-shadow),0 0 0 2px var(--okt-focus-ring);
}
[data-okt-body]{
  display:flex;
  align-items:center;
  min-width:0;
  flex:1 1 auto;
  transition:opacity 300ms ease;
}
:where([data-onekey-toast][data-expanded="false"][data-front="false"]) [data-okt-body]{
  opacity:0;
}

/* Corner close button outside the squircle clip */
:where([data-onekey-toast]) :where([data-close-button]){
  position:absolute;
  left:var(--toast-close-button-start);
  right:var(--toast-close-button-end);
  top:-6px;
  height:20px;
  width:20px;
  display:grid;
  place-items:center;
  padding:0;
  border:0;
  background:var(--okt-close-bg);
  color:var(--okt-close-text);
  box-shadow:var(--okt-close-ring);
  border-radius:50%;
  cursor:pointer;
  z-index:2;
  pointer-events:none;
  opacity:0;
  outline:none;
  transition:opacity 160ms ease,color 150ms ease,transform 120ms ease;
}
:where([data-onekey-toast][data-okt-close-hover]) :where([data-close-button]){
  pointer-events:auto;
  opacity:1;
}
:where([data-onekey-toast]) :where([data-close-button]):focus-visible{
  opacity:1;
}
:where([data-onekey-toast]) :where([data-close-button]):hover{
  color:var(--okt-close-hover-text);
}
:where([data-onekey-toast]) :where([data-close-button]):active{
  transform:scale(.92);
}
:where([data-onekey-toast]) :where([data-close-button]):focus-visible{
  box-shadow:var(--okt-close-ring),0 0 0 2px var(--okt-focus-ring);
}

@media (max-width:600px){
  [data-onekey-toast-toaster]{--toast-vertical-offset:20px;left:50%;right:auto;transform:translateX(-50%)}
}

@media (horizontal-viewport-segments:2){
  [data-onekey-toast-toaster]{
    --toast-segment-width:env(viewport-segment-width 0 0);
    left:calc(env(viewport-segment-left 0 0) + var(--toast-segment-width) / 2);
    right:auto;
    transform:translateX(-50%);
  }
  [data-onekey-toast-toaster]:is([data-x-position="right"],[data-x-position="center"][dir="rtl"]){
    --toast-segment-width:env(viewport-segment-width 1 0);
    left:calc(env(viewport-segment-left 1 0) + var(--toast-segment-width) / 2);
  }
}

@media (vertical-viewport-segments:2){
  [data-onekey-toast-toaster][data-y-position="top"]{
    --toast-segment-top:env(viewport-segment-top 0 0);
    --toast-segment-bottom:env(viewport-segment-bottom 0 0);
    --toast-obstruction-top:0px;
    top:max(min(var(--toast-obstruction-top),calc(var(--toast-segment-bottom) - var(--toast-obstruction-room,88px))),calc(var(--toast-segment-top) + max(var(--toast-vertical-offset),env(safe-area-inset-top))));
  }
  [data-onekey-toast-toaster][data-y-position="bottom"]{
    --toast-segment-top:env(viewport-segment-top 0 1);
    --toast-segment-bottom:env(viewport-segment-bottom 0 1);
  }
}

[data-onekey-toast-section]{pointer-events:bounding-box}
[data-okt-root]{display:flex;flex-direction:column;align-items:stretch;flex:1 1 auto;min-width:0;min-height:0;position:relative;box-sizing:border-box;max-height:var(--okt-max-height,none);overflow:hidden}
[data-okt-row]{display:flex;flex-direction:row;align-items:center;flex-shrink:0;min-width:0;min-height:0;position:relative;box-sizing:border-box}
[data-okt-row][data-okt-layout="notification"]{align-items:flex-start}
[data-okt-col]{display:flex;flex-direction:column;align-items:stretch;justify-content:center;flex:1 1 auto;min-width:0;min-height:0;position:relative;box-sizing:border-box}
[data-okt-col][data-okt-message-col]{min-height:${TOAST_GEOMETRY.tileSize}px}
[data-okt-col][data-okt-no-icon]{padding-inline-start:${TOAST_GEOMETRY.noIconPaddingStart}px}
[data-okt-col][data-okt-content-swap]{animation:onekey-toast-content-swap ${TOAST_MOTION.contentSwapMs}ms cubic-bezier(.2,0,0,1) both}
@keyframes onekey-toast-content-swap{
  from{opacity:0;transform:translateY(3px)}
  to{opacity:1;transform:translateY(0)}
}
[data-okt-icon-box]{display:flex;flex-direction:column;align-items:center;justify-content:center;flex-shrink:0;position:relative;box-sizing:border-box}
[data-okt-status-tile]{
  display:block;position:relative;flex-shrink:0;overflow:hidden;
  --okt-status-ease-in:${leaveEase};
  --okt-status-ease-out:${strokeEase};
  --okt-status-tint-ease:${tintEase};
}
[data-okt-status-glyph]{position:absolute;inset:0;display:grid;place-items:center}
[data-okt-status-glyph] svg{display:block;width:50%;height:50%}
[data-okt-status-outgoing]{display:none}
[data-onekey-toast][data-mounted="true"][data-removed="false"] [data-okt-status-motion]{
  --okt-status-pop:${glyphDelayMs}ms;--okt-status-draw:${drawDelayMs}ms;
}
[data-onekey-toast][data-mounted="true"][data-removed="false"] [data-okt-status-motion="enter"]{
  animation:onekey-status-tile-in ${springs.tile.durationMs}ms ${tileDelayMs}ms linear both;
}
[data-onekey-toast][data-mounted="true"][data-removed="false"] [data-okt-status-motion="settle"]{
  --okt-status-pop:${TOAST_MOTION.glyphLeaveMs}ms;--okt-status-draw:${settleDrawDelayMs}ms;
  transition:background-color ${TOAST_MOTION.tileTintMs}ms var(--okt-status-tint-ease),color ${TOAST_MOTION.tileTintMs}ms var(--okt-status-tint-ease);
  animation:onekey-status-tile-bump ${TOAST_MOTION.tileBumpMs}ms ${TOAST_MOTION.glyphLeaveMs}ms ${bumpEase} both;
}
[data-onekey-toast][data-mounted="true"][data-removed="false"] [data-okt-status-motion="immediate"]{
  --okt-status-pop:0ms;--okt-status-draw:${TOAST_MOTION.strokeDelayOffsetMs}ms;
  transition:background-color ${TOAST_MOTION.tileTintMs}ms var(--okt-status-tint-ease),color ${TOAST_MOTION.tileTintMs}ms var(--okt-status-tint-ease);
  animation:onekey-status-tile-bump ${TOAST_MOTION.tileBumpMs}ms ${bumpEase} both;
}
[data-onekey-toast][data-mounted="true"][data-removed="false"] [data-okt-status-motion] [data-okt-status-glyph]:not([data-okt-status-outgoing]){
  animation:onekey-status-pop ${springs.glyph.durationMs}ms var(--okt-status-pop) linear both;
}
[data-onekey-toast][data-mounted="true"][data-removed="false"] [data-okt-status-motion] [data-okt-status-outgoing]{
  display:grid;animation:onekey-status-leave ${TOAST_MOTION.glyphLeaveMs}ms var(--okt-status-ease-in) both;
}
[data-onekey-toast][data-mounted="true"][data-removed="false"] [data-okt-status-motion] [data-okt-status-stroke]{
  stroke-dasharray:1 1;animation:onekey-status-draw ${TOAST_MOTION.strokeDrawMs}ms var(--okt-status-draw) var(--okt-status-ease-out) both;
}
[data-onekey-toast][data-mounted="true"][data-removed="false"] [data-okt-icon-box] [data-okt-status-ring]{
  animation:onekey-toast-spin ${TOAST_MOTION.spinnerPeriodMs}ms linear infinite;
}
/* Fallback keyframes for browsers without CSS linear() easing. */
@keyframes onekey-status-tile-in{
  from{opacity:0;transform:scale(.85)}
  30%{opacity:.63;transform:scale(.945)}
  60%{opacity:.93;transform:scale(.99)}
  to{opacity:1;transform:scale(1)}
}
@keyframes onekey-status-pop{
  from{opacity:0;transform:scale(.4)}
  35%{opacity:1;transform:scale(1.0386)}
  65%{opacity:1;transform:scale(1.007)}
  to{opacity:1;transform:scale(1)}
}
@supports (animation-timing-function:linear(0,1)){
  [data-onekey-toast-toaster]{
    --okt-card-spring-ease:${springs.card.easing};
  }
  [data-okt-status-tile]{
    --okt-tile-spring:${springs.tile.easing};
    --okt-glyph-spring:${springs.glyph.easing};
  }
  [data-onekey-toast][data-mounted="true"][data-removed="false"] [data-okt-status-motion="enter"]{
    animation:onekey-status-tile-scale ${springs.tile.durationMs}ms ${tileDelayMs}ms var(--okt-tile-spring) both,
      onekey-status-opacity-in ${springs.tile.durationMs}ms ${tileDelayMs}ms var(--okt-tile-spring) both;
  }
  [data-onekey-toast][data-mounted="true"][data-removed="false"] [data-okt-status-motion] [data-okt-status-glyph]:not([data-okt-status-outgoing]){
    animation:onekey-status-glyph-scale ${springs.glyph.durationMs}ms var(--okt-status-pop) var(--okt-glyph-spring) both,
      onekey-status-opacity-in ${springs.glyph.durationMs}ms var(--okt-status-pop) var(--okt-glyph-spring) both;
  }
}
@keyframes onekey-status-tile-scale{
  from{transform:scale(.85)}
  to{transform:scale(1)}
}
@keyframes onekey-status-glyph-scale{
  from{transform:scale(.4)}
  to{transform:scale(1)}
}
@keyframes onekey-status-opacity-in{
  from{opacity:0}
  to{opacity:1}
}
@keyframes onekey-status-draw{
  from{opacity:0;stroke-dashoffset:1}
  .01%{opacity:1;stroke-dashoffset:1}
  to{opacity:1;stroke-dashoffset:0}
}
@keyframes onekey-status-leave{
  from{opacity:var(--okt-glyph-from-opacity);transform:var(--okt-glyph-from-transform)}
  to{opacity:0;transform:scale(.5)}
}
@keyframes onekey-status-tile-bump{
  from{opacity:var(--okt-tile-from-opacity);transform:var(--okt-tile-from-scale)}
  40%{opacity:1;transform:scale(${TOAST_MOTION.tileBumpPeakScale})}
  to{opacity:1;transform:scale(1)}
}
@media (prefers-reduced-motion:reduce){
  [data-okt-respect-reduced-motion="true"] [data-onekey-toast],
  [data-okt-respect-reduced-motion="true"] [data-onekey-toast] *,
  [data-okt-respect-reduced-motion="true"] [data-okt-status-tile],
  [data-okt-respect-reduced-motion="true"] [data-okt-status-tile] *{animation:none!important;transition:none!important}
  [data-okt-respect-reduced-motion="true"] [data-okt-status-stroke]{stroke-dasharray:none!important}
  [data-okt-respect-reduced-motion="true"] [data-okt-status-outgoing]{display:none!important}
  [data-okt-respect-reduced-motion="true"] [data-okt-action]:active{transform:none}
}
[data-okt-icon]{display:block;flex-shrink:0}
[data-okt-badge]{display:flex;flex-direction:row;align-items:center;justify-content:center;flex-shrink:0;border-radius:9999px;box-sizing:border-box}
[data-okt-paragraphs]{display:flex;flex-direction:column;align-items:stretch;flex-shrink:0;min-width:0;position:relative;box-sizing:border-box}
[data-okt-text]{display:inline;box-sizing:border-box;margin:0;padding:0;word-wrap:break-word;overflow-wrap:break-word;white-space:pre-wrap;text-transform:none;user-select:none;-webkit-user-select:none}
[data-okt-paragraphs]>[data-okt-text],[data-okt-col]>[data-okt-text]{display:block}
[data-okt-text][data-okt-selectable]{user-select:auto;-webkit-user-select:auto}
[data-okt-text][data-okt-clamp]{display:-webkit-box;-webkit-box-orient:vertical;overflow:hidden;flex-shrink:1}
[data-okt-link]{cursor:pointer;text-decoration-line:underline}
[data-okt-link]:hover{background-color:var(--okt-link-hover,transparent)}
[data-okt-link]:active{background-color:var(--okt-link-active,transparent)}
[data-okt-actions][hidden]{display:none}
[data-okt-actions]{display:flex;flex-direction:row;flex-shrink:0;gap:8px;box-sizing:border-box}
[data-okt-action]{
  display:flex;flex-direction:row;align-items:center;justify-content:center;flex-shrink:0;
  box-sizing:border-box;margin:0;border:0 solid rgba(170,170,170,0);
  cursor:pointer;font:inherit;font-variant-numeric:normal;white-space:nowrap;
  outline:none;appearance:none;-webkit-appearance:none;
  background-color:var(--okt-action-bg);
  padding:var(--okt-action-padding);border-radius:var(--okt-action-radius);
  transition:background-color 150ms ease,transform 120ms ease;
}
[data-okt-action][data-okt-inline-action]{
  min-height:${TOAST_GEOMETRY.actionHeight}px;
  padding:0 var(--okt-inline-padding);
  border-radius:${TOAST_GEOMETRY.actionRadius}px;
}
[data-okt-action]:hover{background-color:var(--okt-action-hover,var(--okt-action-bg))}
[data-okt-action]:active:not(:disabled){background-color:var(--okt-action-active,var(--okt-action-bg));transform:scale(.95)}
[data-okt-action]:focus-visible{outline:2px solid var(--okt-action-ring,transparent);outline-offset:2px}
[data-okt-action]:disabled{background-color:var(--okt-action-bg);cursor:default}
[data-okt-image]{display:block;flex-shrink:0;object-fit:cover}
[data-okt-spinner]{display:block;flex-shrink:0;animation:onekey-toast-spin .75s linear infinite}
@keyframes onekey-toast-spin{0%{transform:rotate(0deg)}to{transform:rotate(360deg)}}
[data-okt-pressable]{cursor:pointer}
@media (prefers-reduced-motion){[data-okt-spinner]{animation:none!important}}
`;
}

let injected = false;

export function ensureOverlayToastStyles() {
  if (injected || typeof document === 'undefined') {
    return;
  }
  injected = true;
  if (document.getElementById(STYLE_ID)) {
    return;
  }
  const style = document.createElement('style');
  style.id = STYLE_ID;
  style.appendChild(document.createTextNode(buildOverlayToastCss()));
  (document.head ?? document.documentElement).appendChild(style);
}
