declare module 'react-native/Libraries/Types/CodegenTypes' {
  import type { NativeSyntheticEvent } from 'react-native';

  export type Double = number;
  export type WithDefault<T, V> = [V] extends [never] ? T : T;
  export type DirectEventHandler<T> = (
    event: NativeSyntheticEvent<T>
  ) => void | Promise<void>;
}
