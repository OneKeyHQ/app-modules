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

export type SniConnectRequest = {
  requestId?: string;
  ip: string;
  hostname: string;
  method: string;
  path: string;
  headers: HeaderMap;
  body?: string | null;
  timeout: Double;
};

export type SniConnectResponse = {
  data: string;
  status: Int32;
  statusText: string;
  headers: HeaderMap;
};

export interface Spec extends TurboModule {
  request(config: SniConnectRequest): Promise<SniConnectResponse>;
  cancelRequest(requestId: string): Promise<{ success: boolean }>;
  cancelAllRequests(): Promise<{ success: boolean }>;
  clearDNSCache(): Promise<{ success: boolean }>;
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
