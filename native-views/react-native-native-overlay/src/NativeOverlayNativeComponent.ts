import { codegenNativeComponent } from 'react-native';

import type {
  CodegenTypes,
  ColorValue,
  HostComponent,
  ViewProps,
} from 'react-native';
type INativeOverlayDismissedEvent = Readonly<{
  reason: string;
}>;

type INativeOverlayRequestDismissEvent = Readonly<{
  reason: string;
}>;

type INativeOverlayPresentedEvent = Readonly<{
  stackOrder: CodegenTypes.Int32;
}>;

export interface INativeOverlayNativeProps extends ViewProps {
  /** Present when true; play the exit animation and emit onDismissed when it turns false. */
  visible: boolean;
  level?: CodegenTypes.WithDefault<
    'modal' | 'hardware' | 'secure' | 'toast' | 'lock' | 'debug',
    'modal'
  >;
  presentation?: CodegenTypes.WithDefault<
    'center' | 'toast' | 'fullscreen' | 'sheet' | 'anchored',
    'center'
  >;
  scope?: CodegenTypes.WithDefault<'global' | 'page', 'global'>;
  /** `page` scope: the `OverlayPageHost` to render in and the owning page. */
  hostKey?: CodegenTypes.WithDefault<string, ''>;
  ownerKey?: CodegenTypes.WithDefault<string, ''>;
  /** Order inside the level; higher renders above. */
  stackOrder?: CodegenTypes.WithDefault<CodegenTypes.Int32, 0>;
  /** Swallow touches that miss the content instead of passing them through. */
  blocking?: CodegenTypes.WithDefault<boolean, true>;
  dismissOnBackPress?: CodegenTypes.WithDefault<boolean, true>;
  dismissOnBackdropPress?: CodegenTypes.WithDefault<boolean, false>;
  backdropColor?: ColorValue;
  /** `sheet` presentation: resolved content height in points. */
  sheetHeight?: CodegenTypes.WithDefault<CodegenTypes.Double, 0>;
  sheetCornerRadius?: CodegenTypes.WithDefault<CodegenTypes.Double, 24>;
  showHandle?: CodegenTypes.WithDefault<boolean, false>;
  sheetBackgroundColor?: ColorValue;
  dismissOnPanDown?: CodegenTypes.WithDefault<boolean, true>;
  /** JSON of the resolved animation (`IResolvedOverlayAnimation`). */
  animationConfig?: CodegenTypes.WithDefault<string, ''>;
  onPresented?: CodegenTypes.DirectEventHandler<INativeOverlayPresentedEvent>;
  onDismissed?: CodegenTypes.DirectEventHandler<INativeOverlayDismissedEvent>;
  onRequestDismiss?: CodegenTypes.DirectEventHandler<INativeOverlayRequestDismissEvent>;
}

// The shadow node is hand-written (common/cpp): it carries the on-screen
// content offset so `measure` reports where native presented the content.
export default codegenNativeComponent<INativeOverlayNativeProps>(
  'RNCNativeOverlay',
  { interfaceOnly: true }
) as HostComponent<INativeOverlayNativeProps>;
