import { NitroModules } from 'react-native-nitro-modules';

import type { ReactNativeZipArchive as ReactNativeZipArchiveType } from './ReactNativeZipArchive.nitro';

const ReactNativeZipArchiveHybridObject =
  NitroModules.createHybridObject<ReactNativeZipArchiveType>(
    'ReactNativeZipArchive',
  );

// Raw HybridObject. Kept exported for compatibility with the previous
// `export const ZipArchive = NativeZipArchive` shape.
export const ZipArchive = ReactNativeZipArchiveHybridObject;

// Backwards-compatible named function wrappers. These preserve the exact
// public API (names + signatures) of the previous TurboModule-based release so
// consumers (e.g. monorepo logger using `import { zip }`) require zero changes.

export const isPasswordProtected = (file: string): Promise<boolean> =>
  ReactNativeZipArchiveHybridObject.isPasswordProtected(file);

export const unzip = (from: string, to: string): Promise<string> =>
  ReactNativeZipArchiveHybridObject.unzip(from, to);

export const unzipWithPassword = (
  from: string,
  to: string,
  password: string,
): Promise<string> =>
  ReactNativeZipArchiveHybridObject.unzipWithPassword(from, to, password);

export const zipFolder = (from: string, to: string): Promise<string> =>
  ReactNativeZipArchiveHybridObject.zipFolder(from, to);

export const zipFiles = (files: string[], to: string): Promise<string> =>
  ReactNativeZipArchiveHybridObject.zipFiles(files, to);

export const getUncompressedSize = (path: string): Promise<number> =>
  ReactNativeZipArchiveHybridObject.getUncompressedSize(path);

// `zip` is an alias for `zipFolder` and matches the previous public signature
// `(source, target) => Promise<string>`. This is the only API the monorepo
// logger consumes.
export const zip = (source: string, target: string): Promise<string> =>
  ReactNativeZipArchiveHybridObject.zipFolder(source, target);

export type * from './ReactNativeZipArchive.nitro';

// Backwards-compatible alias for the previously exported `ZipArchiveSpec` type.
export type { ReactNativeZipArchive as ZipArchiveSpec } from './ReactNativeZipArchive.nitro';
