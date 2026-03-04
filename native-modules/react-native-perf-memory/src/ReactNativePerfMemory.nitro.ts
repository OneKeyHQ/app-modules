import type { HybridObject } from 'react-native-nitro-modules';

export interface MemoryUsage {
  rss: number;
}

export interface ReactNativePerfMemory
  extends HybridObject<{ ios: 'swift'; android: 'kotlin' }> {
  getMemoryUsage(): Promise<MemoryUsage>;
}
