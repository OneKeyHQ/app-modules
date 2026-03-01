import type { HybridObject } from 'react-native-nitro-modules';


export type UserInterfaceStyle = 'light' | 'dark' | 'unspecified';

export interface DualScreenInfoRect {
  x: number;
  y: number;
  width: number;
  height: number;
}

export interface LaunchOptions {
  launchType: string;
  deepLink?: string;
}

export interface ReactNativeDeviceUtils
  extends HybridObject<{ ios: 'swift'; android: 'kotlin' }> {
  initEventListeners(): void;
  isDualScreenDevice(): boolean;
  isSpanning(): boolean;
  getWindowRects(): Promise<DualScreenInfoRect[]>;
  getHingeBounds(): Promise<DualScreenInfoRect>;
  changeBackgroundColor(r: number, g: number, b: number, a: number): void;
  addSpanningChangedListener(callback: (isSpanning: boolean) => void): number;
  removeSpanningChangedListener(id: number): void;
  setUserInterfaceStyle(style: UserInterfaceStyle): void;

  // LaunchOptionsManager
  getLaunchOptions(): Promise<LaunchOptions>;
  clearLaunchOptions(): Promise<boolean>;
  getDeviceToken(): Promise<string>;
  registerDeviceToken(): Promise<boolean>;
  getStartupTime(): Promise<number>;

  // ExitModule
  exitApp(): void;
}
