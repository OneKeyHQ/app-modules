declare module 'react-native/Libraries/Types/CodegenTypes' {
  import type { NativeSyntheticEvent } from 'react-native';

  export type DirectEventHandler<T> = (
    event: NativeSyntheticEvent<T>
  ) => void | Promise<void>;
  export type Float = number;
  export type Int32 = number;
  export type WithDefault<T, V extends T> = V extends T ? T : never;
}

declare module 'react-native/Libraries/Pressability/PressabilityDebug' {
  export function isEnabled(): boolean;
}

declare module 'react-native/src/private/featureflags/ReactNativeFeatureFlags' {
  export function defaultTextToOverflowHidden(): boolean;
}

declare module 'react-native/Libraries/NativeComponent/ViewConfig' {
  export type PartialViewConfig = Readonly<{
    directEventTypes?: Readonly<Record<string, unknown>>;
    uiViewClassName: string;
    validAttributes?: Readonly<Record<string, unknown>>;
  }>;

  export type ViewConfig = Readonly<Record<string, unknown>>;

  export function createViewConfig(config: PartialViewConfig): ViewConfig;
}

declare module 'react-native/Libraries/Renderer/shims/createReactNativeComponentClass' {
  import type { ViewConfig } from 'react-native/Libraries/NativeComponent/ViewConfig';

  export default function createReactNativeComponentClass(
    name: string,
    callback: () => ViewConfig
  ): unknown;
}
