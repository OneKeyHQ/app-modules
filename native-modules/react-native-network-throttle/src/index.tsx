import { NativeModules, Platform } from 'react-native';

export type NetworkThrottleProfile = 'slow4g';

export type NetworkThrottleConfig = {
  enabled: boolean;
  profile: NetworkThrottleProfile;
  latencyMs: number;
};

export const NETWORK_THROTTLE_SLOW_4G_LATENCY_MS = 562.5;

type NativeNetworkThrottleModule = {
  getConfig: () => Promise<NetworkThrottleConfig>;
  setConfig: (config: NetworkThrottleConfig) => Promise<NetworkThrottleConfig>;
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

export const NetworkThrottle: NetworkThrottleModule = nativeModule
  ? {
      getConfig: () => nativeModule.getConfig(),
      setConfig: async (config) => {
        const currentConfig = await nativeModule.getConfig();
        return nativeModule.setConfig({
          enabled: config.enabled ?? currentConfig.enabled,
          profile: config.profile ?? currentConfig.profile,
          latencyMs: config.latencyMs ?? currentConfig.latencyMs,
        });
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
