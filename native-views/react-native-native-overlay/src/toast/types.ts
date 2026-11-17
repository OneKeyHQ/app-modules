/**
 * The message-toast contract. A toast is plain data: the platform draws the
 * card (DOM on web, UIKit on iOS, Android views on Android) and owns its
 * stacking, timers, gestures and motion. Nothing here is a React node.
 *
 * Every colour is a resolved CSS colour string and every length is in points
 * (CSS px on web), so the package carries no design tokens of its own.
 */

/** Where a toast group is anchored. */
export type IOverlayToastPosition =
  | 'top-left'
  | 'top-center'
  | 'top-right'
  | 'bottom-left'
  | 'bottom-center'
  | 'bottom-right';

/** Why a toast left. `programmatic` is a caller's `dismiss`. */
export type IOverlayToastDismissReason =
  | 'timeout'
  | 'swipe'
  | 'close'
  | 'programmatic';

export type IOverlayToastFontWeight = '400' | '500' | '600' | '700';

/** One inline run of a paragraph. Unset fields inherit the paragraph. */
export interface IOverlayToastTextRun {
  text: string;
  color?: string;
  fontWeight?: IOverlayToastFontWeight;
  fontSize?: number;
  underline?: boolean;
  /**
   * Makes the run tappable. The value is opaque to the platform and comes
   * back through `onLink`.
   */
  link?: string;
}

export interface IOverlayToastText {
  /** Paragraphs, each laid out as its own block. */
  paragraphs: IOverlayToastTextRun[][];
  color: string;
  fontSize: number;
  lineHeight: number;
  fontWeight: IOverlayToastFontWeight;
  letterSpacing?: number;
  /** A CSS font-family list on web, a font family name on native. */
  fontFamily?: string;
  /**
   * Native: the font name for each weight, for a typeface that ships one
   * face per weight. A weight listed here selects that face.
   */
  fontFaces?: Partial<Record<IOverlayToastFontWeight, string>>;
  /** Equal-width digits. */
  tabularNumbers?: boolean;
  /** Web: let the text be selected. Off by default. */
  selectable?: boolean;
  /** Clamp per paragraph; unset means no clamp. */
  numberOfLines?: number;
  /** Web: background of a link run under the pointer and while pressed. */
  linkHoverColor?: string;
  linkPressedColor?: string;
}

export type IOverlayToastIcon =
  | {
      kind: 'icon';
      /** Icon name for the native icon registry. */
      name: string;
      /** Target of an SVG `<use>`; web draws this with `currentColor`. */
      href?: string;
      color: string;
      size: number;
    }
  | { kind: 'spinner'; color: string; size: number }
  | { kind: 'image'; uri: string; size: number; borderRadius?: number };

export interface IOverlayToastAction {
  /** Opaque id reported through `onAction`. */
  id: string;
  label: string;
  disabled?: boolean;
  testID?: string;
  /** Drawn before the label. */
  icon?: IOverlayToastIcon;
  /** Space between the icon and the label. */
  iconGap?: number;
  color: string;
  backgroundColor: string;
  hoverBackgroundColor?: string;
  pressedBackgroundColor?: string;
  focusRingColor?: string;
  fontSize: number;
  lineHeight: number;
  fontWeight: IOverlayToastFontWeight;
  fontFamily?: string;
  /** Native: the font name for each weight. */
  fontFaces?: Partial<Record<IOverlayToastFontWeight, string>>;
  paddingHorizontal: number;
  paddingVertical: number;
  /** Outer margins; negative values pull the button over its neighbours. */
  marginHorizontal?: number;
  marginVertical?: number;
  /** A transparent border that only adds to the button's size. */
  borderWidth?: number;
  borderRadius: number;
  /** Opacity while `disabled`. */
  disabledOpacity?: number;
}

interface IOverlayToastBase {
  /** Stable id. Showing an id that is on screen updates that toast. */
  id: string;
  /** Auto-dismiss delay in ms. `Infinity` keeps it until dismissed. */
  duration: number;
  /** Defaults to the toaster's position. */
  position?: IOverlayToastPosition;
  testID?: string;
}

/**
 * Status icon, title, message and an optional action row:
 *
 * ```
 * [icon] title
 *        message
 *        [action] [action]
 * ```
 */
export interface IOverlayMessageToast extends IOverlayToastBase {
  layout: 'message';
  icon?: IOverlayToastIcon;
  title?: IOverlayToastText;
  message?: IOverlayToastText;
  actions?: IOverlayToastAction[];
  actionsAlign?: 'left' | 'right';
}

/**
 * A pressable notification row:
 *
 * ```
 * (badge) title          [image]
 *         message
 * ```
 */
export interface IOverlayNotificationToast extends IOverlayToastBase {
  layout: 'notification';
  /** Drawn centred inside a round badge. */
  icon: IOverlayToastIcon;
  badgeColor: string;
  badgeSize: number;
  title?: IOverlayToastText;
  message?: IOverlayToastText;
  /** Trailing thumbnail. */
  image?: { uri: string; size: number; borderRadius?: number };
  actions?: IOverlayToastAction[];
  actionsAlign?: 'left' | 'right';
  /** Report presses on the row through `onPress`. */
  pressable?: boolean;
}

export type IOverlayToastSpec =
  | IOverlayMessageToast
  | IOverlayNotificationToast;

export interface IOverlayToastHandlers {
  /** The toast started leaving. */
  onDismiss?: (reason: IOverlayToastDismissReason) => void;
  /** The exit animation finished and the card is gone. */
  onClose?: (reason: IOverlayToastDismissReason) => void;
  onAction?: (actionId: string) => void;
  onLink?: (link: string) => void;
  onPress?: () => void;
}

/** Toaster-wide settings. Every field has a platform default. */
export interface IOverlayToasterConfig {
  position?: IOverlayToastPosition;
  /** Web chrome follows this; native reads `cardColor`. */
  theme?: 'light' | 'dark';
  /** Native card surface. */
  cardColor?: string;
  /**
   * Native: the width of the card's content box. The card adds its own
   * padding around it.
   */
  contentWidth?: number;
  /** Native: the tallest a card's content may grow. */
  contentMaxHeight?: number;
  /** Web: the tallest a card's content may grow. */
  webContentMaxHeight?: number;
  /**
   * Bottom edge, in window points, of something hanging from the top of the
   * window where top toasts land. Top toasts rest under it. 0 clears it.
   */
  topObstruction?: number;
  /** Air between the obstruction and the first toast under it. */
  topObstructionGap?: number;
  /** Room a toast keeps above the usable area's bottom edge. */
  topObstructionRoom?: number;
  /** Follow the system reduced-motion setting. Defaults to true. */
  respectReducedMotion?: boolean;
  /** Web: accessible label of the close button. */
  closeButtonLabel?: string;
  testID?: string;
}

export interface IOverlayToastHandle {
  id: string;
  dismiss: () => void;
}

/** What a platform renderer receives: the active toasts, newest first. */
export interface IOverlayToastItem {
  spec: IOverlayToastSpec;
  /** False once a caller dismissed it; the renderer plays the exit. */
  visible: boolean;
  /** Bumped whenever the spec changes, so the card is redrawn. */
  revision: number;
  /** Bumped by `show` only: a new generation restarts the toast's lifetime. */
  generation: number;
}

export type IOverlayToastEvent =
  | { id: string; type: 'dismiss'; reason: IOverlayToastDismissReason }
  | { id: string; type: 'close'; reason: IOverlayToastDismissReason }
  | { id: string; type: 'action'; actionId: string }
  | { id: string; type: 'link'; link: string }
  | { id: string; type: 'press' };

export interface IOverlayToastRenderer {
  sync: (
    items: readonly IOverlayToastItem[],
    config: IOverlayToasterConfig
  ) => void;
  destroy: () => void;
}
