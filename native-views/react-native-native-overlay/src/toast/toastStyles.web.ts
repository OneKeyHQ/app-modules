/* cspell:ignore Neue Noto */
// The web toaster chrome. These declarations replicate sonner@1.4.41's
// default stylesheet (the renderer this replaces) one for one: same custom
// properties, same values, same transitions and breakpoints, so the migration
// is visually a no-op. Attribute names are ours so nothing else can target the
// old library's nodes.
const STYLE_ID = 'onekey-overlay-toast-styles';

const CSS = `
:where(html[dir="ltr"]),:where([data-onekey-toast-toaster][dir="ltr"]){--toast-close-button-start:0;--toast-close-button-end:unset;--toast-close-button-transform:translate(-35%, -35%)}
:where(html[dir="rtl"]),:where([data-onekey-toast-toaster][dir="rtl"]){--toast-close-button-start:unset;--toast-close-button-end:0;--toast-close-button-transform:translate(35%, -35%)}
:where([data-onekey-toast-toaster]){position:fixed;width:var(--width);font-family:ui-sans-serif,system-ui,-apple-system,BlinkMacSystemFont,Segoe UI,Roboto,Helvetica Neue,Arial,Noto Sans,sans-serif,Apple Color Emoji,Segoe UI Emoji,Segoe UI Symbol,Noto Color Emoji;--gray1:hsl(0, 0%, 99%);--gray2:hsl(0, 0%, 97.3%);--gray3:hsl(0, 0%, 95.1%);--gray4:hsl(0, 0%, 93%);--gray5:hsl(0, 0%, 90.9%);--gray6:hsl(0, 0%, 88.7%);--gray7:hsl(0, 0%, 85.8%);--gray8:hsl(0, 0%, 78%);--gray9:hsl(0, 0%, 56.1%);--gray10:hsl(0, 0%, 52.3%);--gray11:hsl(0, 0%, 43.5%);--gray12:hsl(0, 0%, 9%);--border-radius:8px;box-sizing:border-box;padding:0;margin:0;list-style:none;outline:none;z-index:0}
:where([data-onekey-toast-toaster][data-x-position="right"]){right:max(var(--offset),env(safe-area-inset-right))}
:where([data-onekey-toast-toaster][data-x-position="left"]){left:max(var(--offset),env(safe-area-inset-left))}
:where([data-onekey-toast-toaster][data-x-position="center"]){left:50%;transform:translate(-50%)}
:where([data-onekey-toast-toaster][data-y-position="top"]){top:max(var(--offset),env(safe-area-inset-top))}
:where([data-onekey-toast-toaster][data-y-position="bottom"]){bottom:max(var(--offset),env(safe-area-inset-bottom))}
:where([data-onekey-toast]){--y:translateY(100%);--lift-amount:calc(var(--lift) * var(--gap));z-index:var(--z-index);position:absolute;opacity:0;transform:var(--y);filter:blur(0);touch-action:none;transition:transform .4s,opacity .4s,height .4s,box-shadow .2s;box-sizing:border-box;outline:none;overflow-wrap:anywhere}
:where([data-onekey-toast][data-styled="true"]){padding:16px;background:var(--normal-bg);border:1px solid var(--normal-border);color:var(--normal-text);border-radius:var(--border-radius);box-shadow:0 4px 12px #0000001a;width:var(--width);font-size:13px;display:flex;align-items:center;gap:6px}
:where([data-onekey-toast]:focus-visible){box-shadow:0 4px 12px #0000001a,0 0 0 2px #0003}
:where([data-onekey-toast][data-y-position="top"]){top:0;--y:translateY(-100%);--lift:1;--lift-amount:calc(1 * var(--gap))}
:where([data-onekey-toast][data-y-position="bottom"]){bottom:0;--y:translateY(100%);--lift:-1;--lift-amount:calc(var(--lift) * var(--gap))}
:where([data-onekey-toast]) :where([data-close-button]){position:absolute;left:var(--toast-close-button-start);right:var(--toast-close-button-end);top:0;height:20px;width:20px;display:flex;justify-content:center;align-items:center;padding:0;background:var(--gray1);color:var(--gray12);border:1px solid var(--gray4);transform:var(--toast-close-button-transform);border-radius:50%;cursor:pointer;z-index:1;transition:opacity .1s,background .2s,border-color .2s}
:where([data-onekey-toast]) :where([data-close-button]):focus-visible{box-shadow:0 4px 12px #0000001a,0 0 0 2px #0003}
:where([data-onekey-toast]):hover :where([data-close-button]):hover{background:var(--gray2);border-color:var(--gray5)}
:where([data-onekey-toast][data-swiping="true"]):before{content:"";position:absolute;left:0;right:0;height:100%;z-index:-1}
:where([data-onekey-toast][data-y-position="top"][data-swiping="true"]):before{bottom:50%;transform:scaleY(3) translateY(50%)}
:where([data-onekey-toast][data-y-position="bottom"][data-swiping="true"]):before{top:50%;transform:scaleY(3) translateY(-50%)}
:where([data-onekey-toast][data-swiping="false"][data-removed="true"]):before{content:"";position:absolute;inset:0;transform:scaleY(2)}
:where([data-onekey-toast]):after{content:"";position:absolute;left:0;height:calc(var(--gap) + 1px);bottom:100%;width:100%}
:where([data-onekey-toast][data-mounted="true"]){--y:translateY(0);opacity:1}
:where([data-onekey-toast][data-expanded="false"][data-front="false"]){--scale: var(--toasts-before) * .05 + 1;--y:translateY(calc(var(--lift-amount) * var(--toasts-before))) scale(calc(-1 * var(--scale)));height:var(--front-toast-height)}
:where([data-onekey-toast])>*{transition:opacity .4s}
:where([data-onekey-toast][data-expanded="false"][data-front="false"][data-styled="true"])>*{opacity:0}
:where([data-onekey-toast][data-visible="false"]){opacity:0;pointer-events:none}
:where([data-onekey-toast][data-mounted="true"][data-expanded="true"]){--y:translateY(calc(var(--lift) * var(--offset)));height:var(--initial-height)}
:where([data-onekey-toast][data-removed="true"][data-front="true"][data-swipe-out="false"]){--y:translateY(calc(var(--lift) * -100%));opacity:0}
:where([data-onekey-toast][data-removed="true"][data-front="false"][data-swipe-out="false"][data-expanded="true"]){--y:translateY(calc(var(--lift) * var(--offset) + var(--lift) * -100%));opacity:0}
:where([data-onekey-toast][data-removed="true"][data-front="false"][data-swipe-out="false"][data-expanded="false"]){--y:translateY(40%);opacity:0;transition:transform .5s,opacity .2s}
:where([data-onekey-toast][data-removed="true"][data-front="false"]):before{height:calc(var(--initial-height) + 20%)}
[data-onekey-toast][data-swiping="true"]{transform:var(--y) translateY(var(--swipe-amount,0px));transition:none}
[data-onekey-toast][data-swipe-out="true"][data-y-position="bottom"],[data-onekey-toast][data-swipe-out="true"][data-y-position="top"]{animation:onekey-toast-swipe-out .2s ease-out forwards}
@keyframes onekey-toast-swipe-out{0%{transform:translateY(calc(var(--lift) * var(--offset) + var(--swipe-amount)));opacity:1}to{transform:translateY(calc(var(--lift) * var(--offset) + var(--swipe-amount) + var(--lift) * -100%));opacity:0}}
@media (max-width:600px){[data-onekey-toast-toaster]{position:fixed;--mobile-offset:16px;right:var(--mobile-offset);left:var(--mobile-offset);width:100%}[data-onekey-toast-toaster] [data-onekey-toast]{left:0;right:0;width:calc(100% - var(--mobile-offset) * 2)}[data-onekey-toast-toaster][data-x-position="left"]{left:var(--mobile-offset)}[data-onekey-toast-toaster][data-y-position="bottom"]{bottom:20px}[data-onekey-toast-toaster][data-y-position="top"]{top:20px}[data-onekey-toast-toaster][data-x-position="center"]{left:var(--mobile-offset);right:var(--mobile-offset);transform:none}}
[data-onekey-toast-toaster][data-theme="light"]{--normal-bg:#fff;--normal-border:var(--gray4);--normal-text:var(--gray12);--success-bg:hsl(143, 85%, 96%);--success-border:hsl(145, 92%, 91%);--success-text:hsl(140, 100%, 27%);--info-bg:hsl(208, 100%, 97%);--info-border:hsl(221, 91%, 91%);--info-text:hsl(210, 92%, 45%);--warning-bg:hsl(49, 100%, 97%);--warning-border:hsl(49, 91%, 91%);--warning-text:hsl(31, 92%, 45%);--error-bg:hsl(359, 100%, 97%);--error-border:hsl(359, 100%, 94%);--error-text:hsl(360, 100%, 45%)}
[data-onekey-toast-toaster][data-theme="light"] [data-onekey-toast][data-invert="true"]{--normal-bg:#000;--normal-border:hsl(0, 0%, 20%);--normal-text:var(--gray1)}
[data-onekey-toast-toaster][data-theme="dark"] [data-onekey-toast][data-invert="true"]{--normal-bg:#fff;--normal-border:var(--gray3);--normal-text:var(--gray12)}
[data-onekey-toast-toaster][data-theme="dark"]{--normal-bg:#000;--normal-border:hsl(0, 0%, 20%);--normal-text:var(--gray1);--success-bg:hsl(150, 100%, 6%);--success-border:hsl(147, 100%, 12%);--success-text:hsl(150, 86%, 65%);--info-bg:hsl(215, 100%, 6%);--info-border:hsl(223, 100%, 12%);--info-text:hsl(216, 87%, 65%);--warning-bg:hsl(64, 100%, 6%);--warning-border:hsl(60, 100%, 12%);--warning-text:hsl(46, 87%, 65%);--error-bg:hsl(358, 76%, 10%);--error-border:hsl(357, 89%, 16%);--error-text:hsl(358, 100%, 81%)}
[data-rich-colors="true"] [data-onekey-toast][data-type="success"],[data-rich-colors="true"] [data-onekey-toast][data-type="success"] [data-close-button]{background:var(--success-bg);border-color:var(--success-border);color:var(--success-text)}
[data-rich-colors="true"] [data-onekey-toast][data-type="info"],[data-rich-colors="true"] [data-onekey-toast][data-type="info"] [data-close-button]{background:var(--info-bg);border-color:var(--info-border);color:var(--info-text)}
[data-rich-colors="true"] [data-onekey-toast][data-type="warning"],[data-rich-colors="true"] [data-onekey-toast][data-type="warning"] [data-close-button]{background:var(--warning-bg);border-color:var(--warning-border);color:var(--warning-text)}
[data-rich-colors="true"] [data-onekey-toast][data-type="error"],[data-rich-colors="true"] [data-onekey-toast][data-type="error"] [data-close-button]{background:var(--error-bg);border-color:var(--error-border);color:var(--error-text)}
@media (prefers-reduced-motion){[data-onekey-toast],[data-onekey-toast]>*{transition:none!important;animation:none!important}}

[data-onekey-toast-section]{pointer-events:bounding-box}
[data-okt-root]{display:flex;flex-direction:column;align-items:stretch;flex:1 1 auto;min-width:0;min-height:0;position:relative;box-sizing:border-box;max-height:var(--okt-max-height,none);overflow:hidden}
[data-okt-row]{display:flex;flex-direction:row;align-items:stretch;flex-shrink:0;min-width:0;min-height:0;position:relative;box-sizing:border-box}
[data-okt-col]{display:flex;flex-direction:column;align-items:stretch;flex:1 1 auto;min-width:0;min-height:0;position:relative;box-sizing:border-box}
[data-okt-icon-box]{display:flex;flex-direction:column;align-items:stretch;flex-shrink:0;position:relative;box-sizing:border-box}
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
[data-okt-actions]{display:flex;flex-direction:row;flex-shrink:0;gap:8px;box-sizing:border-box}
[data-okt-action]{display:flex;flex-direction:row;align-items:center;justify-content:center;flex-shrink:0;box-sizing:border-box;margin:0;border:0 solid rgba(170,170,170,0);cursor:default;font:inherit;font-variant-numeric:normal;white-space:pre-wrap;outline:none;appearance:none;-webkit-appearance:none;background-color:var(--okt-action-bg);transition:none}
[data-okt-action]:hover{background-color:var(--okt-action-hover,var(--okt-action-bg))}
[data-okt-action]:active{background-color:var(--okt-action-active,var(--okt-action-bg))}
[data-okt-action]:focus-visible{outline:2px solid var(--okt-action-ring,transparent);outline-offset:2px}
[data-okt-action]:disabled{background-color:var(--okt-action-bg)}
[data-okt-image]{display:block;flex-shrink:0;object-fit:cover}
[data-okt-spinner]{display:block;flex-shrink:0;animation:onekey-toast-spin .75s linear infinite}
@keyframes onekey-toast-spin{0%{transform:rotate(0deg)}to{transform:rotate(360deg)}}
[data-okt-pressable]{cursor:pointer}
@media (prefers-reduced-motion){[data-okt-spinner]{animation:none!important}}
`;

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
  style.appendChild(document.createTextNode(CSS));
  (document.head ?? document.documentElement).appendChild(style);
}
