declare module 'react-native/Libraries/Types/CodegenTypes' {
  export type Int32 = number;
  export type Double = number;
  export type Float = number;
  export type UnsafeObject = Record<string, unknown>;
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  export type WithDefault<Type, Value> = Type;
}
