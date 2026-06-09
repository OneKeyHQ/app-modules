import type { HybridObject } from 'react-native-nitro-modules';

export interface ReactNativeZipArchive
  extends HybridObject<{ ios: 'swift'; android: 'kotlin' }> {
  // Whether the archive at `file` is password protected.
  isPasswordProtected(file: string): Promise<boolean>;

  // Extract the archive at `from` into directory `to`. Resolves with `to`.
  unzip(from: string, to: string): Promise<string>;

  // Extract a password-protected archive at `from` into directory `to`.
  unzipWithPassword(
    from: string,
    to: string,
    password: string,
  ): Promise<string>;

  // Zip the contents of directory `from` into archive `to`. Resolves with `to`.
  zipFolder(from: string, to: string): Promise<string>;

  // Zip the given list of files into archive `to`. Resolves with `to`.
  zipFiles(files: string[], to: string): Promise<string>;

  // Total uncompressed size (bytes) of the archive at `path`. -1 on failure.
  getUncompressedSize(path: string): Promise<number>;
}
