import BackgroundThread from './NativeBackgroundThread';

export function multiply(a: number, b: number): number {
  return BackgroundThread.multiply(a, b);
}
