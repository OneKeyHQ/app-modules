import { ReactNativeDeviceUtils } from './index';

export interface FirstLaunchInitOptions {
  appVersion?: string;
  bundleVersion?: string;
  /**
   * Used on Web/Desktop/Extension only; on iOS/Android pass legacy signals to
   * the native `DeviceUtilsFirstLaunch.initialize(...)` singleton at app launch.
   */
  isLegacyInstall?: boolean;
  /**
   * Used on Web/Desktop/Extension only; on iOS/Android pass legacy signals to
   * the native `DeviceUtilsFirstLaunch.initialize(...)` singleton at app launch.
   */
  legacyAppVersion?: string;
}

// Per-JS-runtime executing bundle identity: stays pinned during explicit lookups
// and automatically refreshes when a JS reload boots a new bundle version.
let startupBundleVersion: string | undefined;

export function initFirstLaunch(options?: FirstLaunchInitOptions): void {
  ReactNativeDeviceUtils.isFirstLaunch();
  ReactNativeDeviceUtils.isFirstLaunchForCurrentAppVersion();
  if (options?.bundleVersion !== undefined) {
    startupBundleVersion = options.bundleVersion;
    ReactNativeDeviceUtils.isFirstLaunchForCurrentBundle(options.bundleVersion);
  }
}

export function isFirstLaunch(): boolean {
  return ReactNativeDeviceUtils.isFirstLaunch();
}

export function isFirstLaunchForCurrentAppVersion(
  _appVersion?: string
): boolean {
  return ReactNativeDeviceUtils.isFirstLaunchForCurrentAppVersion();
}

export function isFirstLaunchForCurrentBundle(
  bundleVersion?: string
): boolean {
  return ReactNativeDeviceUtils.isFirstLaunchForCurrentBundle(
    bundleVersion ?? startupBundleVersion
  );
}

export function resetFirstLaunch(): boolean {
  const removed = ReactNativeDeviceUtils.resetFirstLaunch();
  if (removed) {
    startupBundleVersion = undefined;
  }
  return removed;
}
