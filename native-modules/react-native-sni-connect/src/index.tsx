import NativeSniConnect, {
  type SniConnectDebugSnapshot,
  type SniConnectDebugTarget,
  type SniConnectRequest,
  type SniConnectResponse,
  type SniConnectUploadFileRequest,
  type SniConnectUploadProgress,
} from './NativeSniConnect';

export function request(
  config: SniConnectRequest
): Promise<SniConnectResponse> {
  return NativeSniConnect.request(config);
}

export function isUploadFileSupported(): boolean {
  return typeof NativeSniConnect.uploadFile === 'function';
}

export async function uploadFile(
  config: SniConnectUploadFileRequest
): Promise<SniConnectResponse> {
  if (!isUploadFileSupported()) {
    throw Object.assign(new Error('Native SNI file upload is not supported'), {
      code: 'SNI_UNSUPPORTED',
    });
  }
  return NativeSniConnect.uploadFile(config);
}

export async function getUploadProgress(
  requestId: string
): Promise<SniConnectUploadProgress | null> {
  if (typeof NativeSniConnect.getUploadProgress !== 'function') {
    return null;
  }
  return NativeSniConnect.getUploadProgress(requestId);
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

export function getDebugSnapshot(
  target: SniConnectDebugTarget
): Promise<SniConnectDebugSnapshot> {
  return NativeSniConnect.getDebugSnapshot(target);
}

export function isProxyActiveForUrl(url: string): Promise<boolean> {
  return NativeSniConnect.isProxyActiveForUrl(url);
}

export type {
  SniConnectBodylessMethod,
  SniConnectDebugSnapshot,
  SniConnectDebugTarget,
  SniConnectMethod,
  SniConnectOptionalBodyMethod,
  SniConnectRequest,
  SniConnectRequiredBodyMethod,
  SniConnectResponse,
  SniConnectUploadFileMethod,
  SniConnectUploadFileRequest,
  SniConnectUploadProgress,
} from './NativeSniConnect';
