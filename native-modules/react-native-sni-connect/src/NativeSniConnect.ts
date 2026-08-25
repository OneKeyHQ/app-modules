import {
  NativeModules,
  Platform,
  TurboModuleRegistry,
  type TurboModule,
} from 'react-native';
import type { Double, Int32 } from 'react-native/Libraries/Types/CodegenTypes';

type HeaderMap = {
  [key: string]: string;
};

type MultiValueHeaderMap = {
  [key: string]: string[];
};

type SniConnectRequestBase = {
  requestId?: string;
  ip: string;
  hostname: string;
  path: string;
  headers: HeaderMap;
  timeout: Double;
};

type NativeSniConnectRequest = SniConnectRequestBase & {
  method: string;
  body?: string | null;
};

export type SniConnectBodylessMethod = 'GET' | 'HEAD';
export type SniConnectRequiredBodyMethod = 'POST' | 'PUT' | 'PATCH';
export type SniConnectOptionalBodyMethod = 'DELETE' | 'OPTIONS';
export type SniConnectMethod =
  | SniConnectBodylessMethod
  | SniConnectRequiredBodyMethod
  | SniConnectOptionalBodyMethod;

export type SniConnectRequest =
  | (SniConnectRequestBase & {
      method: SniConnectBodylessMethod;
      body?: never;
    })
  | (SniConnectRequestBase & {
      method: SniConnectRequiredBodyMethod;
      body: string;
    })
  | (SniConnectRequestBase & {
      method: SniConnectOptionalBodyMethod;
      body?: string | null;
    });

export type SniConnectResponse = {
  data: string;
  status: Int32;
  statusText: string;
  headers: HeaderMap;
  multiValueHeaders?: MultiValueHeaderMap;
};

export type SniConnectDebugTarget = {
  ip: string;
  hostname: string;
};

export type SniConnectDebugSnapshot = {
  activeRequests: Int32;
  activeRequestsForPair: Int32;
  pendingRequests: Int32;
  pendingRequestsForPair: Int32;
  activeRequestIdsForPair: string[];
  pendingRequestIdsForPair: string[];
};

export interface Spec extends TurboModule {
  request(config: NativeSniConnectRequest): Promise<SniConnectResponse>;
  cancelRequest(requestId: string): Promise<{ success: boolean }>;
  cancelAllRequests(): Promise<{ success: boolean }>;
  clearDNSCache(): Promise<{ success: boolean }>;
  getDebugSnapshot(
    target: SniConnectDebugTarget
  ): Promise<SniConnectDebugSnapshot>;
  isProxyActiveForUrl(url: string): Promise<boolean>;
}

const LINKING_ERROR =
  `The package '@onekeyfe/react-native-sni-connect' doesn't seem to be linked. Make sure:\n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go; create a custom dev client instead\n';

export type SniConnectModule = Spec;

const turboModuleResult = TurboModuleRegistry.get<Spec>('SniConnect');

const turboModule: Spec | null = turboModuleResult ?? null;

const bridgeModule: Spec | null =
  (NativeModules.SniConnect as Spec | undefined) ?? null;

const nativeModule = (turboModule ?? bridgeModule) as SniConnectModule | null;

if (nativeModule == null) {
  throw new Error(LINKING_ERROR);
}

const NativeSniConnect: SniConnectModule = nativeModule;

export default NativeSniConnect;
