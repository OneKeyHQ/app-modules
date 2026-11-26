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

export function initFirstLaunch(options?: FirstLaunchInitOptions): void {
  ReactNativeDeviceUtils.isFirstLaunch();
  ReactNativeDeviceUtils.isFirstLaunchForCurrentAppVersion();
  if (options?.bundleVersion !== undefined) {
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
  return ReactNativeDeviceUtils.isFirstLaunchForCurrentBundle(bundleVersion);
}
