import type { HybridObject } from 'react-native-nitro-modules';

export interface NativeLogger
  extends HybridObject<{ ios: 'swift'; android: 'kotlin' }> {
  write(level: number, msg: string): void;
  getLogDirectory(): string;
  getLogFilePaths(): Promise<string[]>;
  deleteLogFiles(): Promise<void>;
}
