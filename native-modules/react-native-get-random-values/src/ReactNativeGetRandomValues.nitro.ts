import type { HybridObject } from 'react-native-nitro-modules';

export interface ReactNativeGetRandomValuesParams {
  message: string;
}

export interface ReactNativeGetRandomValuesResult {
  success: boolean;
  data: string;
}

export interface ReactNativeGetRandomValues
  extends HybridObject<{ ios: 'swift'; android: 'kotlin' }> {
  getRandomBase64(byteLength: number): string;
}
