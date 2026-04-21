import NativeZipArchive from './NativeZipArchive';

export const ZipArchive = NativeZipArchive;
export const zip = (source: string, target: string): Promise<string> =>
  NativeZipArchive.zipFolder(source, target);
export type { Spec as ZipArchiveSpec } from './NativeZipArchive';
