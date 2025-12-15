import type { HybridObject } from 'react-native-nitro-modules';

export interface SetItemParams {
  key: string;
  value: string;
  enableSync?: boolean;
  label?: string;
  description?: string;
}

export interface GetItemParams {
  key: string;
}

export interface RemoveItemParams {
  key: string;
}

export interface HasItemParams {
  key: string;
}

export interface GetItemResult {
  key: string;
  value: string;
}

export interface KeychainModule
  extends HybridObject<{ ios: 'swift'; android: 'kotlin' }> {
  setItem(params: SetItemParams): Promise<void>;
  getItem(params: GetItemParams): Promise<GetItemResult | null>;
  removeItem(params: RemoveItemParams): Promise<void>;
  hasItem(params: HasItemParams): Promise<boolean>;
  isICloudSyncEnabled(): Promise<boolean>;
}
