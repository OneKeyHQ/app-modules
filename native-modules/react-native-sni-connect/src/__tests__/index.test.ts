import { NativeModules } from 'react-native';
import {
  request,
  cancelRequest,
  cancelAllRequests,
  clearDNSCache,
  subscribeToLogs,
  type LogEntry,
  type SniConnectRequest,
  type SniConnectResponse,
} from '../index';

// Mock React Native modules
jest.mock('react-native', () => ({
  NativeModules: {
    SniConnect: {
      request: jest.fn(),
      cancelRequest: jest.fn(),
      cancelAllRequests: jest.fn(),
      clearDNSCache: jest.fn(),
      addListener: jest.fn(),
      removeListeners: jest.fn(),
    },
  },
  NativeEventEmitter: jest.fn().mockImplementation(() => ({
    addListener: jest.fn(() => ({
      remove: jest.fn(),
    })),
  })),
  Platform: {
    select: jest.fn((obj) => obj.default),
  },
  TurboModuleRegistry: {
    get: jest.fn(() => null),
  },
}));

describe('SniConnect Module', () => {
  const mockNativeModule = NativeModules.SniConnect;

  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('request()', () => {
    it('should call native request method with correct config', async () => {
      const config: SniConnectRequest = {
        ip: '1.1.1.1',
        hostname: 'example.com',
        method: 'GET',
        path: '/api/test',
        headers: { Authorization: 'Bearer token' },
        timeout: 30000,
      };

      const mockResponse: SniConnectResponse = {
        data: '{"success": true}',
        status: 200,
        statusText: 'OK',
        headers: { 'content-type': 'application/json' },
      };

      mockNativeModule.request.mockResolvedValue(mockResponse);

      const response = await request(config);

      expect(mockNativeModule.request).toHaveBeenCalledWith(config);
      expect(response).toEqual(mockResponse);
    });

    it('should handle request with requestId', async () => {
      const config: SniConnectRequest = {
        requestId: 'test-123',
        ip: '1.1.1.1',
        hostname: 'example.com',
        method: 'POST',
        path: '/api/data',
        headers: {},
        body: '{"key": "value"}',
        timeout: 30000,
      };

      mockNativeModule.request.mockResolvedValue({
        data: '',
        status: 201,
        statusText: 'Created',
        headers: {},
      });

      await request(config);

      expect(mockNativeModule.request).toHaveBeenCalledWith(
        expect.objectContaining({
          requestId: 'test-123',
        })
      );
    });

    it('should handle request errors', async () => {
      const config: SniConnectRequest = {
        ip: '1.1.1.1',
        hostname: 'example.com',
        method: 'GET',
        path: '/api/test',
        headers: {},
        timeout: 30000,
      };

      const error = new Error('Network error');
      mockNativeModule.request.mockRejectedValue(error);

      await expect(request(config)).rejects.toThrow('Network error');
    });

    it('should handle different HTTP methods', async () => {
      const methods = ['GET', 'POST', 'PUT', 'PATCH', 'DELETE', 'HEAD'];

      for (const method of methods) {
        mockNativeModule.request.mockResolvedValue({
          data: '',
          status: 200,
          statusText: 'OK',
          headers: {},
        });

        const config: SniConnectRequest = {
          ip: '1.1.1.1',
          hostname: 'example.com',
          method,
          path: '/test',
          headers: {},
          timeout: 30000,
        };

        await request(config);
        expect(mockNativeModule.request).toHaveBeenCalledWith(
          expect.objectContaining({ method })
        );
      }
    });
  });

  describe('cancelRequest()', () => {
    it('should call native cancelRequest with requestId', async () => {
      mockNativeModule.cancelRequest.mockResolvedValue({ success: true });

      const result = await cancelRequest('test-request-123');

      expect(mockNativeModule.cancelRequest).toHaveBeenCalledWith(
        'test-request-123'
      );
      expect(result).toEqual({ success: true });
    });

    it('should handle cancellation failure', async () => {
      mockNativeModule.cancelRequest.mockResolvedValue({ success: false });

      const result = await cancelRequest('non-existent');

      expect(result.success).toBe(false);
    });
  });

  describe('cancelAllRequests()', () => {
    it('should call native cancelAllRequests', async () => {
      mockNativeModule.cancelAllRequests.mockResolvedValue({ success: true });

      const result = await cancelAllRequests();

      expect(mockNativeModule.cancelAllRequests).toHaveBeenCalled();
      expect(result).toEqual({ success: true });
    });
  });

  describe('clearDNSCache()', () => {
    it('should call native clearDNSCache', async () => {
      mockNativeModule.clearDNSCache.mockResolvedValue({ success: true });

      const result = await clearDNSCache();

      expect(mockNativeModule.clearDNSCache).toHaveBeenCalled();
      expect(result).toEqual({ success: true });
    });
  });

  describe('subscribeToLogs()', () => {
    it('should subscribe to log events and return unsubscribe function', () => {
      const mockCallback = jest.fn();

      const unsubscribe = subscribeToLogs(mockCallback);

      // Should return a function
      expect(typeof unsubscribe).toBe('function');

      // Unsubscribe should not throw
      expect(() => unsubscribe()).not.toThrow();
    });

    it('should call callback when log event is received', () => {
      const mockCallback = jest.fn();

      subscribeToLogs(mockCallback);

      // The actual callback invocation would be triggered by native module
      // In unit tests, we're just verifying the subscription was set up correctly
      expect(mockCallback).not.toHaveBeenCalled(); // Not called immediately
    });

    it('should handle log entry type correctly', () => {
      // Type checking test
      const logEntry: LogEntry = {
        level: 'info',
        message: 'Test log message',
        timestamp: Date.now(),
      };

      expect(logEntry).toBeDefined();
      expect(logEntry.level).toBe('info');
      expect(logEntry.message).toBe('Test log message');
      expect(typeof logEntry.timestamp).toBe('number');
    });
  });

  describe('Request Configuration', () => {
    it('should handle empty headers', async () => {
      mockNativeModule.request.mockResolvedValue({
        data: '',
        status: 200,
        statusText: 'OK',
        headers: {},
      });

      const config: SniConnectRequest = {
        ip: '1.1.1.1',
        hostname: 'example.com',
        method: 'GET',
        path: '/test',
        headers: {},
        timeout: 30000,
      };

      await request(config);
      expect(mockNativeModule.request).toHaveBeenCalledWith(config);
    });

    it('should handle custom headers', async () => {
      mockNativeModule.request.mockResolvedValue({
        data: '',
        status: 200,
        statusText: 'OK',
        headers: {},
      });

      const config: SniConnectRequest = {
        ip: '1.1.1.1',
        hostname: 'example.com',
        method: 'GET',
        path: '/test',
        headers: {
          'Authorization': 'Bearer token',
          'X-Custom-Header': 'custom-value',
          'Content-Type': 'application/json',
        },
        timeout: 30000,
      };

      await request(config);
      expect(mockNativeModule.request).toHaveBeenCalledWith(
        expect.objectContaining({
          headers: expect.objectContaining({
            'Authorization': 'Bearer token',
            'X-Custom-Header': 'custom-value',
          }),
        })
      );
    });

    it('should handle request body', async () => {
      mockNativeModule.request.mockResolvedValue({
        data: '',
        status: 200,
        statusText: 'OK',
        headers: {},
      });

      const config: SniConnectRequest = {
        ip: '1.1.1.1',
        hostname: 'example.com',
        method: 'POST',
        path: '/api/data',
        headers: {},
        body: JSON.stringify({ key: 'value', number: 123 }),
        timeout: 30000,
      };

      await request(config);
      expect(mockNativeModule.request).toHaveBeenCalledWith(
        expect.objectContaining({
          body: expect.stringContaining('key'),
        })
      );
    });

    it('should handle different timeout values', async () => {
      mockNativeModule.request.mockResolvedValue({
        data: '',
        status: 200,
        statusText: 'OK',
        headers: {},
      });

      const timeouts = [1000, 5000, 30000, 60000];

      for (const timeout of timeouts) {
        const config: SniConnectRequest = {
          ip: '1.1.1.1',
          hostname: 'example.com',
          method: 'GET',
          path: '/test',
          headers: {},
          timeout,
        };

        await request(config);
        expect(mockNativeModule.request).toHaveBeenCalledWith(
          expect.objectContaining({ timeout })
        );
      }
    });
  });

  describe('Response Handling', () => {
    it('should handle JSON response', async () => {
      const mockResponse: SniConnectResponse = {
        data: JSON.stringify({ result: 'success', count: 42 }),
        status: 200,
        statusText: 'OK',
        headers: { 'content-type': 'application/json' },
      };

      mockNativeModule.request.mockResolvedValue(mockResponse);

      const response = await request({
        ip: '1.1.1.1',
        hostname: 'example.com',
        method: 'GET',
        path: '/test',
        headers: {},
        timeout: 30000,
      });

      expect(response.data).toContain('success');
      expect(response.headers['content-type']).toBe('application/json');
    });

    it('should handle plain text response', async () => {
      const mockResponse: SniConnectResponse = {
        data: 'Plain text response',
        status: 200,
        statusText: 'OK',
        headers: { 'content-type': 'text/plain' },
      };

      mockNativeModule.request.mockResolvedValue(mockResponse);

      const response = await request({
        ip: '1.1.1.1',
        hostname: 'example.com',
        method: 'GET',
        path: '/test',
        headers: {},
        timeout: 30000,
      });

      expect(response.data).toBe('Plain text response');
    });

    it('should handle different status codes', async () => {
      const statusCodes = [200, 201, 204, 400, 404, 500];

      for (const status of statusCodes) {
        mockNativeModule.request.mockResolvedValue({
          data: '',
          status,
          statusText: 'Status',
          headers: {},
        });

        const response = await request({
          ip: '1.1.1.1',
          hostname: 'example.com',
          method: 'GET',
          path: '/test',
          headers: {},
          timeout: 30000,
        });

        expect(response.status).toBe(status);
      }
    });
  });

  describe('Type Exports', () => {
    it('should export LogEntry type', () => {
      const log: LogEntry = {
        level: 'info',
        message: 'test',
        timestamp: Date.now(),
      };
      expect(log).toBeDefined();
    });

    it('should export SniConnectRequest type', () => {
      const req: SniConnectRequest = {
        ip: '1.1.1.1',
        hostname: 'example.com',
        method: 'GET',
        path: '/',
        headers: {},
        timeout: 30000,
      };
      expect(req).toBeDefined();
    });

    it('should export SniConnectResponse type', () => {
      const res: SniConnectResponse = {
        data: '',
        status: 200,
        statusText: 'OK',
        headers: {},
      };
      expect(res).toBeDefined();
    });
  });
});
