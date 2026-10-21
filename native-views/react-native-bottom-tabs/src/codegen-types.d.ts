declare module 'react-native/Libraries/Types/CodegenTypes' {
  import type { NativeSyntheticEvent } from 'react-native';

  export type Double = number;
  export type Float = number;
  export type Int32 = number;
  export type WithDefault<T, V> = T;
  export type DirectEventHandler<T> = (
    event: NativeSyntheticEvent<T>
  ) => void;
  export type BubblingEventHandler<T> = (
    event: NativeSyntheticEvent<T>
  ) => void;
}

declare module 'react-native/Libraries/Utilities/codegenNativeComponent' {
  import type { HostComponent } from 'react-native';

  export default function codegenNativeComponent<P>(
    componentName: string,
    options?: {
      interfaceOnly?: boolean;
      paperComponentName?: string;
      paperComponentNameDeprecated?: string;
      excludedPlatforms?: ReadonlyArray<'iOS' | 'android'>;
    }
  ): HostComponent<P>;
}
