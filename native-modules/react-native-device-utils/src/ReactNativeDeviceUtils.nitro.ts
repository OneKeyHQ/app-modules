import type { HybridObject } from 'react-native-nitro-modules';


export type UserInterfaceStyle = 'light' | 'dark' | 'unspecified';

export type AndroidChannel =
  | 'direct'
  | 'google'
  | 'huawei'
  | 'unknown';

export type InstallerPackageName =
  | 'appStore'
  | 'testFlight'
  | 'other'
  | 'playStore'
  | 'huaweiAppGallery'
  | 'unknown';

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

export interface WebViewPackageInfo {
  packageName: string;
  versionName: string;
  versionCode: number;
}

export interface GooglePlayServicesStatus {
  status: number;
  isAvailable: boolean;
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
  saveDeviceToken(token: string): Promise<void>;
  registerDeviceToken(): Promise<boolean>;
  getStartupTime(): Promise<number>;
  // Returns the JSON userInfo of a LOCAL notification the user tapped to launch
  // the (killed) app, then clears it so it is delivered exactly once. Empty
  // string when there is none. iOS-only meaningful; Android returns "".
  getAndClearColdStartLocalNotification(): Promise<string>;

  // ExitModule
  exitApp(): void;

  // WebView & Play Services
  getCurrentWebViewPackageInfo(): Promise<WebViewPackageInfo>;
  isGooglePlayServicesAvailable(): Promise<GooglePlayServicesStatus>;

  // Boot Recovery
  markBootSuccess(): void;
  getConsecutiveBootFailCount(): Promise<number>;
  incrementConsecutiveBootFailCount(): void;
  setConsecutiveBootFailCount(count: number): void;
  getAndClearRecoveryAction(): Promise<string>;

  // Android Channel & Installer
  getAndroidChannel(): AndroidChannel;
  getInstallerPackageName(): InstallerPackageName;
}
