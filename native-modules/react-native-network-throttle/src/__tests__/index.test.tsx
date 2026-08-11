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
      bypassUrlOrigins: [],
    });
  });

  it('forwards exact bypass origins with a complete native config', async () => {
    const bypassUrlOrigins = ['http://localhost:8081'];
    const expectedConfig: NetworkThrottleConfig = {
      ...baseConfig,
      bypassUrlOrigins,
    };
    mockGetConfig.mockResolvedValue({
      ...baseConfig,
      bypassUrlOrigins: [],
    });
    mockSetConfig.mockResolvedValue(expectedConfig);

    await expect(
      NetworkThrottle.setConfig({ bypassUrlOrigins })
    ).resolves.toEqual(expectedConfig);
    expect(mockSetConfig).toHaveBeenCalledWith(expectedConfig);
  });

  it('preserves registered origins for unrelated config updates', async () => {
    const currentConfig: NetworkThrottleConfig = {
      ...baseConfig,
      bypassUrlOrigins: ['http://10.0.2.2:8081'],
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
