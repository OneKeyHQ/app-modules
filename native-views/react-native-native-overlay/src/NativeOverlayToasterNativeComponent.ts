import { codegenNativeComponent } from 'react-native';

import type { CodegenTypes, HostComponent, ViewProps } from 'react-native';

type INativeOverlayToastEvent = Readonly<{
  toastId: string;
  /** `dismiss` | `close` | `action` | `link` | `press`. */
  type: string;
  /** The dismiss reason, action id or link of the event; empty otherwise. */
  value: string;
}>;

export interface INativeOverlayToasterNativeProps extends ViewProps {
  /** JSON of `IOverlayToastItem[]`, newest first. */
  toasts?: CodegenTypes.WithDefault<string, ''>;
  /** JSON of `IOverlayToasterConfig`. */
  config?: CodegenTypes.WithDefault<string, ''>;
  onToastEvent?: CodegenTypes.DirectEventHandler<INativeOverlayToastEvent>;
}

export default codegenNativeComponent<INativeOverlayToasterNativeProps>(
  'RNCNativeOverlayToaster'
) as HostComponent<INativeOverlayToasterNativeProps>;
