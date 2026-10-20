import type { HybridObject } from 'react-native-nitro-modules';

export interface ReactNativeCaptureProtection
  extends HybridObject<{ ios: 'swift'; android: 'kotlin' }> {
  prevent(): Promise<void>;
  allow(): Promise<void>;
  setListener(callback: ((event: number) => void) | undefined): void;
}
