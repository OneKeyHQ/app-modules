import { NativeModules, Platform } from 'react-native';

export type NetworkThrottleProfile = 'slow4g';

export type NetworkThrottleConfig = {
  enabled: boolean;
  profile: NetworkThrottleProfile;
  latencyMs: number;
  downloadBps: number;
  uploadBps: number;
  throttleUrlHosts: string[];
};

export const NETWORK_THROTTLE_SLOW_4G_LATENCY_MS = 562.5;
export const NETWORK_THROTTLE_102_KIB_BPS = 102 * 1024;
export const NETWORK_THROTTLE_SLOW_4G_DOWNLOAD_BPS =
  NETWORK_THROTTLE_102_KIB_BPS;
export const NETWORK_THROTTLE_SLOW_4G_UPLOAD_BPS = NETWORK_THROTTLE_102_KIB_BPS;

type NativeNetworkThrottleConfig = Omit<
  NetworkThrottleConfig,
  'throttleUrlHosts'
> & {
  throttleUrlHosts?: string[];
};

type NativeNetworkThrottleModule = {
  getConfig: () => Promise<NativeNetworkThrottleConfig>;
  setConfig: (
    config: NativeNetworkThrottleConfig
  ) => Promise<NativeNetworkThrottleConfig>;
};

export type NetworkThrottleModule = {
  getConfig: () => Promise<NetworkThrottleConfig>;
  setConfig: (
    config: Partial<NetworkThrottleConfig>
  ) => Promise<NetworkThrottleConfig>;
};

const LINKING_ERROR =
  `The package '@onekeyfe/react-native-network-throttle' doesn't seem to be linked. ` +
  Platform.select({ ios: "- run 'pod install'\n", default: '' }) +
  '- rebuild the app after installing the package';

const nativeModule = NativeModules.OneKeyNetworkThrottle as
  | NativeNetworkThrottleModule
  | undefined;

function normalizeNativeConfig(
  config: NativeNetworkThrottleConfig
): NetworkThrottleConfig {
  return {
    ...config,
    throttleUrlHosts: config.throttleUrlHosts ?? [],
  };
}

export const NetworkThrottle: NetworkThrottleModule = nativeModule
  ? {
      getConfig: async () =>
        normalizeNativeConfig(await nativeModule.getConfig()),
      setConfig: async (config) => {
        const currentConfig = normalizeNativeConfig(
          await nativeModule.getConfig()
        );
        const nativeConfig = await nativeModule.setConfig({
          enabled: config.enabled ?? currentConfig.enabled,
          profile: config.profile ?? currentConfig.profile,
          latencyMs: config.latencyMs ?? currentConfig.latencyMs,
          downloadBps: config.downloadBps ?? currentConfig.downloadBps,
          uploadBps: config.uploadBps ?? currentConfig.uploadBps,
          throttleUrlHosts:
            config.throttleUrlHosts ?? currentConfig.throttleUrlHosts ?? [],
        });
        return normalizeNativeConfig(nativeConfig);
      },
    }
  : (new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    ) as NetworkThrottleModule);

export default NetworkThrottle;
