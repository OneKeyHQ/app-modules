import type { HybridObject } from 'react-native-nitro-modules';


export type InMemoryValue = string | boolean | number;

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

/**
 * Extra room, in points beyond the safe area, that content near the window
 * corners must leave for system window controls (the iPadOS window controls in
 * the top-leading corner, for example). All zero when no controls are shown.
 */
export interface WindowControlsInsets {
  top: number;
  left: number;
  bottom: number;
  right: number;
}

/**
 * Orientation of the app's window scene, named as UIKit names it.
 * `landscapeRight` turns the top of the device, and an iPhone's sensor
 * housing, to the left edge of the screen; `landscapeLeft` to the right edge.
 */
export type InterfaceOrientation =
  | 'portrait'
  | 'portraitUpsideDown'
  | 'landscapeLeft'
  | 'landscapeRight'
  | 'unknown';

/** App commands in the iPadOS menu bar that JS performs. */
export type MainMenuCommand = 'about' | 'preferences' | 'lockNow';

export interface MainMenuLink {
  title: string;
  url: string;
}

/**
 * Localized content of the custom main menu (the iPadOS menu bar). Help links
 * open in the system browser natively; the other items notify JS. An empty
 * `aboutTitle` removes the About item and empty `helpLinks` remove the Help
 * menu.
 */
export interface MainMenuConfig {
  aboutTitle: string;
  preferencesTitle: string;
  lockNowTitle: string;
  helpLinks: MainMenuLink[];
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
  /**
   * Updates the Android system navigation bar from the UI runtime.
   * Color channels use the [0, 255] range. `useDarkIcons` should be true for
   * light backgrounds. Android API 24-25 cannot render dark navigation icons,
   * so those versions use an opaque black background fallback when requested.
   * This is a no-op on iOS.
   */
  setNavigationBarAppearance(
    r: number,
    g: number,
    b: number,
    a: number,
    useDarkIcons: boolean
  ): void;
  addSpanningChangedListener(callback: (isSpanning: boolean) => void): number;
  removeSpanningChangedListener(id: number): void;
  setUserInterfaceStyle(style: UserInterfaceStyle): void;
  /**
   * Window-controls insets of the app's main window, for content that adapts
   * horizontally (a header row beside the controls). iOS 26+ reads UIKit's
   * safe area with horizontal corner adaptation; Android and older iOS return
   * all zeros. Listeners fire on the main thread when the value changes.
   */
  getWindowControlsInsets(): WindowControlsInsets;
  addWindowControlsInsetsChangedListener(
    callback: (insets: WindowControlsInsets) => void
  ): number;
  removeWindowControlsInsetsChangedListener(id: number): void;
  /**
   * Interface orientation of the key window's scene. iOS 16+ reads the
   * scene's geometry, so the value follows rotations that a presented view
   * controller forces; iOS before 16 and Android return `unknown`. Listeners
   * fire on the main thread when the value changes.
   */
  getInterfaceOrientation(): InterfaceOrientation;
  addInterfaceOrientationChangedListener(
    callback: (orientation: InterfaceOrientation) => void
  ): number;
  removeInterfaceOrientationChangedListener(id: number): void;
  /**
   * Replaces the custom main menu content and rebuilds the menu bar. The menu
   * only shows on iPadOS (and in windows with a menu bar); Android ignores it.
   */
  setMainMenuConfig(config: MainMenuConfig): void;
  /** Listeners fire on the main thread when the user picks an app command. */
  addMainMenuCommandListener(
    callback: (command: MainMenuCommand) => void
  ): number;
  removeMainMenuCommandListener(id: number): void;

  // LaunchOptionsManager
  getLaunchOptions(): Promise<LaunchOptions>;
  clearLaunchOptions(): Promise<boolean>;
  getDeviceToken(): Promise<string>;
  saveDeviceToken(token: string): Promise<void>;
  registerDeviceToken(): Promise<boolean>;
  getStartupTime(): Promise<number>;
  /** Whether the current native process is the first launch of this local installation. */
  isFirstLaunch(): boolean;
  /** First launch of a native release version higher than any previously launched version. */
  isFirstLaunchForCurrentAppVersion(): boolean;
  /** First launch of a bundle version higher than any previously launched bundle. */
  isFirstLaunchForCurrentBundle(bundleVersion?: string): boolean;
  /** Clears persisted first-launch files and in-memory state (e.g. on Reset App). */
  resetFirstLaunch(): void;
  // Process-owned primitive Map, shared by all runtimes and never persisted.
  getInMemoryValue(key: string): InMemoryValue | undefined;
  setInMemoryValue(key: string, value: InMemoryValue): void;
  removeInMemoryValue(key: string): boolean;
  setInMemoryValueIfAbsent(key: string, value: InMemoryValue): boolean;
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

  /** Returns an AdServices token on iOS 14.3+, or undefined on unsupported platforms. */
  getAppleAdsAttributionToken(): Promise<string | undefined>;
}
