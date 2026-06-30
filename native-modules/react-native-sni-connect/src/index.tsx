import NativeSniConnect, {
  type SniConnectRequest,
  type SniConnectResponse,
} from './NativeSniConnect';

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

export function isProxyActiveForUrl(url: string): Promise<boolean> {
  return NativeSniConnect.isProxyActiveForUrl(url);
}

export type {
  SniConnectBodylessMethod,
  SniConnectMethod,
  SniConnectOptionalBodyMethod,
  SniConnectRequest,
  SniConnectRequiredBodyMethod,
  SniConnectResponse,
} from './NativeSniConnect';
