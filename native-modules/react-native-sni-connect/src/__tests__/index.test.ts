jest.mock('../NativeSniConnect', () => ({
  __esModule: true,
  default: {
    request: jest.fn(),
    cancelRequest: jest.fn(),
    cancelAllRequests: jest.fn(),
    clearDNSCache: jest.fn(),
    getDebugSnapshot: jest.fn(),
    isProxyActiveForUrl: jest.fn(),
  },
}));

import {
  cancelAllRequests,
  cancelRequest,
  clearDNSCache,
  getDebugSnapshot,
  isProxyActiveForUrl,
  request,
} from '../index';
import NativeSniConnect, { type SniConnectRequest } from '../NativeSniConnect';

const mockNativeModule = NativeSniConnect as unknown as {
  request: jest.Mock;
  cancelRequest: jest.Mock;
  cancelAllRequests: jest.Mock;
  clearDNSCache: jest.Mock;
  getDebugSnapshot: jest.Mock;
  isProxyActiveForUrl: jest.Mock;
};

describe('react-native-sni-connect public API', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('forwards request() to the native module and returns its result', async () => {
    const config: SniConnectRequest = {
      ip: '93.184.216.34',
      hostname: 'example.com',
      method: 'GET',
      path: '/',
      headers: {},
      timeout: 30_000,
    };
    const response = { data: '{}', status: 200, statusText: 'OK', headers: {} };
    mockNativeModule.request.mockResolvedValue(response);

    await expect(request(config)).resolves.toBe(response);
    expect(mockNativeModule.request).toHaveBeenCalledWith(config);
  });

  it('forwards cancelRequest() with the request id', async () => {
    mockNativeModule.cancelRequest.mockResolvedValue({ success: true });
    await expect(cancelRequest('req-1')).resolves.toEqual({ success: true });
    expect(mockNativeModule.cancelRequest).toHaveBeenCalledWith('req-1');
  });

  it('forwards cancelAllRequests()', async () => {
    mockNativeModule.cancelAllRequests.mockResolvedValue({ success: true });
    await expect(cancelAllRequests()).resolves.toEqual({ success: true });
    expect(mockNativeModule.cancelAllRequests).toHaveBeenCalledTimes(1);
  });

  it('forwards clearDNSCache()', async () => {
    mockNativeModule.clearDNSCache.mockResolvedValue({ success: true });
    await expect(clearDNSCache()).resolves.toEqual({ success: true });
    expect(mockNativeModule.clearDNSCache).toHaveBeenCalledTimes(1);
  });

  it('forwards getDebugSnapshot() with the target pair', async () => {
    const target = { hostname: 'example.com', ip: '93.184.216.34' };
    const snapshot = {
      activeRequests: 16,
      activeRequestsForPair: 16,
      pendingRequests: 4,
      pendingRequestsForPair: 4,
      activeRequestIdsForPair: ['req-1'],
      pendingRequestIdsForPair: ['req-17'],
    };
    mockNativeModule.getDebugSnapshot.mockResolvedValue(snapshot);

    await expect(getDebugSnapshot(target)).resolves.toBe(snapshot);
    expect(mockNativeModule.getDebugSnapshot).toHaveBeenCalledWith(target);
  });

  it('forwards isProxyActiveForUrl()', async () => {
    mockNativeModule.isProxyActiveForUrl.mockResolvedValue(false);
    await expect(isProxyActiveForUrl('https://example.com')).resolves.toBe(
      false
    );
    expect(mockNativeModule.isProxyActiveForUrl).toHaveBeenCalledWith(
      'https://example.com'
    );
  });
});

const validPostRequest: SniConnectRequest = {
  ip: '93.184.216.34',
  hostname: 'example.com',
  method: 'POST',
  path: '/',
  headers: {},
  body: '',
  timeout: 30_000,
};
void validPostRequest;

// @ts-expect-error POST requests must carry an explicit string body.
const invalidPostWithoutBody: SniConnectRequest = {
  ip: '93.184.216.34',
  hostname: 'example.com',
  method: 'POST',
  path: '/',
  headers: {},
  timeout: 30_000,
};
void invalidPostWithoutBody;

// @ts-expect-error GET requests must not carry a body.
const invalidGetWithBody: SniConnectRequest = {
  ip: '93.184.216.34',
  hostname: 'example.com',
  method: 'GET',
  path: '/',
  headers: {},
  body: '',
  timeout: 30_000,
};
void invalidGetWithBody;
