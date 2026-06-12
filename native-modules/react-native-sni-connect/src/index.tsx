import { NativeEventEmitter } from 'react-native';
import NativeSniConnect, {
  type SniConnectRequest,
  type SniConnectResponse,
} from './NativeSniConnect';

// Log entry type definition
export type LogEntry = {
  level: string;
  message: string;
  timestamp: number;
};

// Create event emitter instance
const eventEmitter = new NativeEventEmitter(NativeSniConnect as any);

// Simple log subscription function
export function subscribeToLogs(callback: (log: LogEntry) => void): () => void {
  const subscription = eventEmitter.addListener('SniConnectLog', (log: any) => {
    // Type assertion since we know the structure
    callback(log as LogEntry);
  });
  return () => subscription.remove();
}

export function request(
  config: SniConnectRequest
): Promise<SniConnectResponse> {
  return NativeSniConnect.request(config);
}

export function cancelRequest(
  requestId: string
): Promise<{ success: boolean }> {
  return NativeSniConnect.cancelRequest(requestId);
}

export function cancelAllRequests(): Promise<{ success: boolean }> {
  return NativeSniConnect.cancelAllRequests();
}

export function clearDNSCache(): Promise<{ success: boolean }> {
  return NativeSniConnect.clearDNSCache();
}

export type { SniConnectRequest, SniConnectResponse } from './NativeSniConnect';
