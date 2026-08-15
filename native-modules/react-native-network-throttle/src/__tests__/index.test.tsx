jest.mock('react-native', () => ({
  NativeModules: {
    OneKeyNetworkThrottle: {
      getConfig: jest.fn(),
      setConfig: jest.fn(),
    },
  },
  Platform: {
    select: (options: { default: string }) => options.default,
  },
}));

import { NativeModules } from 'react-native';

import { NetworkThrottle, type NetworkThrottleConfig } from '../index';

const { getConfig: mockGetConfig, setConfig: mockSetConfig } =
  NativeModules.OneKeyNetworkThrottle as {
    getConfig: jest.Mock;
    setConfig: jest.Mock;
  };

const baseConfig = {
  enabled: true,
  profile: 'slow4g' as const,
  latencyMs: 562.5,
  downloadBps: 102 * 1024,
  uploadBps: 102 * 1024,
};

describe('NetworkThrottle', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('normalizes config from an older native binary', async () => {
    mockGetConfig.mockResolvedValue(baseConfig);

    await expect(NetworkThrottle.getConfig()).resolves.toEqual({
      ...baseConfig,
      throttleUrlHosts: [],
    });
  });

  it('forwards the throttle allowlist with a complete native config', async () => {
    const throttleUrlHosts = ['*.onekeycn.com'];
    const expectedConfig: NetworkThrottleConfig = {
      ...baseConfig,
      throttleUrlHosts,
    };
    mockGetConfig.mockResolvedValue({
      ...baseConfig,
      throttleUrlHosts: [],
    });
    mockSetConfig.mockResolvedValue(expectedConfig);

    await expect(
      NetworkThrottle.setConfig({ throttleUrlHosts })
    ).resolves.toEqual(expectedConfig);
    expect(mockSetConfig).toHaveBeenCalledWith(expectedConfig);
  });

  it('preserves the registered allowlist for unrelated config updates', async () => {
    const currentConfig: NetworkThrottleConfig = {
      ...baseConfig,
      throttleUrlHosts: ['*.onekeycn.com'],
    };
    const expectedConfig = {
      ...currentConfig,
      enabled: false,
    };
    mockGetConfig.mockResolvedValue(currentConfig);
    mockSetConfig.mockResolvedValue(expectedConfig);

    await NetworkThrottle.setConfig({ enabled: false });

    expect(mockSetConfig).toHaveBeenCalledWith(expectedConfig);
  });
});

// The native side implements this matching in Kotlin and Objective-C; this
// pins the contract both must satisfy, and mirrors the desktop URL patterns.
describe('throttle host matching contract', () => {
  const matches = (host: string, pattern: string) =>
    pattern.startsWith('*.')
      ? host.endsWith(pattern.slice(1))
      : host === pattern;

  it.each([
    ['wallet.onekeycn.com', '*.onekeycn.com', true],
    ['a.b.onekeycn.com', '*.onekeycn.com', true],
    ['uni.onekey-asset.com', '*.onekey-asset.com', true],
    ['app-assets.onekey.so', 'app-assets.onekey.so', true],
    // the bare apex is not a sub-domain
    ['onekeycn.com', '*.onekeycn.com', false],
    // look-alike hosts must not match
    ['evil-onekeycn.com', '*.onekeycn.com', false],
    ['mainnet.infura.io', '*.onekeycn.com', false],
    ['localhost', '*.onekeycn.com', false],
  ])('%s vs %s -> %s', (host, pattern, expected) => {
    expect(matches(host, pattern)).toBe(expected);
  });
});
