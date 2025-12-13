import type { HybridObject } from 'react-native-nitro-modules';

// Parameter types
export interface SaveRecordParams {
  recordType: string;
  recordID: string;
  data: string;
  meta: string;
}

export interface FetchRecordParams {
  recordType: string;
  recordID: string;
}

export interface DeleteRecordParams {
  recordType: string;
  recordID: string;
}

export interface RecordExistsParams {
  recordType: string;
  recordID: string;
}

export interface QueryRecordsParams {
  recordType: string;
}

// Return types
export interface AccountInfoResult {
  status: number;
  statusName: string;
  containerUserId?: string;
}

export interface SaveRecordResult {
  recordID: string;
  createdAt: number;
}

export interface RecordResult {
  recordID: string;
  recordType: string;
  data: string;
  meta: string;
  createdAt: number;
  modifiedAt: number;
}

export interface QueryRecordsResult {
  records: RecordResult[];
}

export interface CloudKitModule
  extends HybridObject<{ ios: 'swift'; android: 'kotlin' }> {
  multiply(a: number, b: number): number;
  isAvailable(): Promise<boolean>;
  getAccountInfo(): Promise<AccountInfoResult>;
  saveRecord(params: SaveRecordParams): Promise<SaveRecordResult>;
  fetchRecord(params: FetchRecordParams): Promise<RecordResult | null>;
  deleteRecord(params: DeleteRecordParams): Promise<boolean>;
  recordExists(params: RecordExistsParams): Promise<boolean>;
  queryRecords(params: QueryRecordsParams): Promise<QueryRecordsResult>;
}
