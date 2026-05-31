import type { HybridObject } from 'react-native-nitro-modules';

export interface VerifyGpgCleartextResult {
  valid: boolean;
  sha256?: string; // taken from the signed JSON body when valid (mirrors bundle behavior)
  reason?: string;
}

export interface VerifyDetachedAscResult {
  valid: boolean;
  sha256?: string; // mirrors APK behavior: taken from the detached SHA256SUMS.asc cleartext
  reason?: string;
}

export interface Sha256Result {
  sha256?: string;
  failureReason?: string; // FILE_NOT_FOUND / IO_<code> / MISMATCH ... (mirrors current sub-types)
}

export interface DirHashEntry {
  relativePath: string;
  sha256: string;
}

export interface ReactNativeBundleCrypto
  extends HybridObject<{ ios: 'swift'; android: 'kotlin' }> {
  // ---- GPG / ASC (public key embedded, single source; never exposed to JS) ----
  // bundle form: PGP CLEARTEXT SIGNED MESSAGE whose body is JSON containing sha256
  verifyGpgCleartext(signedMessage: string): Promise<VerifyGpgCleartextResult>;
  // APK form: detached SHA256SUMS.asc (cleartext-signed SHA256SUMS text)
  verifyDetachedAsc(ascContent: string): Promise<VerifyDetachedAscResult>;

  // ---- sha256 ----
  sha256OfFile(filePath: string): Promise<Sha256Result>;
  // constant-time comparison of two hex strings (mirrors current secureCompare semantics)
  secureEqualHex(a: string, b: string): boolean;

  // ---- per-entry directory hash verification (mirrors validateAllFilesInDir) ----
  // verify every file under dirPath has sha256 == expected[relativePath], plus completeness.
  verifyDirAgainstHashes(
    dirPath: string,
    entries: DirHashEntry[]
  ): Promise<boolean>;
  // compute relativePath -> sha256 for all files under dirPath (for chart to build its metadata)
  hashDir(dirPath: string): Promise<DirHashEntry[]>;

  // ---- safe file operations (verbs) ----
  // guard against symlink / path traversal (mirrors validateExtractedPathSafety)
  validateExtractedPathSafety(destination: string): Promise<boolean>;
  // atomic write: temp file + rename
  atomicWriteFile(filePath: string, contentUtf8: string): Promise<void>;
  // safe rename (same-volume move, removes existing target first)
  safeRename(fromPath: string, toPath: string): Promise<void>;
  // enumerate version sub-directory names under a parent dir (e.g. "<appVersion>-<bundleVersion>")
  listVersionDirs(parentDir: string): Promise<string[]>;
}
