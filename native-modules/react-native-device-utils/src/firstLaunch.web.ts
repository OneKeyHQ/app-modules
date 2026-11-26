export interface FirstLaunchInitOptions {
  appVersion?: string;
  bundleVersion?: string;
  /**
   * Host-provided signal indicating this storage area already existed
   * in a legacy version before first-launch tracking was introduced.
   * Ignored once persisted state already exists.
   */
  isLegacyInstall?: boolean;
  /**
   * Optional previous app version known to the host from legacy state.
   * Ignored once persisted state already exists.
   */
  legacyAppVersion?: string;
}

export const FIRST_LAUNCH_KEYS = {
  launched: '1k_fl_launched',
  appVersion: '1k_fl_app_ver',
  bundleVersion: '1k_fl_bundle_ver',
} as const;

const validVersionPattern =
  /^[0-9]+(?:\.[0-9]+)*(?:-[0-9A-Za-z-]+(?:\.[0-9A-Za-z-]+)*)?(?:\+[0-9A-Za-z-]+(?:\.[0-9A-Za-z-]+)*)?$/;
const digitsOnly = /^[0-9]+$/;

let initialized = false;
let cachedFirstLaunch = false;
let startupAppVersion: string | undefined;
let startupBundleVersion: string | undefined;
const cachedAppResults = new Map<string, boolean>();
const cachedBundleResults = new Map<string, boolean>();

function isValidVersion(version?: unknown): version is string {
  return (
    typeof version === 'string' &&
    version.length > 0 &&
    version.length <= 120 &&
    validVersionPattern.test(version)
  );
}

function compareUnsignedIntStrings(a: string, b: string): number {
  const normA = a.replace(/^0+/, '') || '0';
  const normB = b.replace(/^0+/, '') || '0';
  if (normA.length !== normB.length) {
    return normA.length < normB.length ? -1 : 1;
  }
  if (normA === normB) {
    return 0;
  }
  return normA < normB ? -1 : 1;
}

function compareIdentifiers(a: string, b: string): number {
  const aNum = digitsOnly.test(a);
  const bNum = digitsOnly.test(b);
  if (aNum && bNum) {
    return compareUnsignedIntStrings(a, b);
  }
  if (aNum !== bNum) {
    return aNum ? -1 : 1;
  }
  if (a === b) {
    return 0;
  }
  return a < b ? -1 : 1;
}

export function compareVersions(a: string, b: string): number {
  const stripBuild = (v: string) => {
    const plusIndex = v.indexOf('+');
    return plusIndex >= 0 ? v.slice(0, plusIndex) : v;
  };
  const splitPre = (v: string): [string, string | null] => {
    const dashIndex = v.indexOf('-');
    return dashIndex >= 0
      ? [v.slice(0, dashIndex), v.slice(dashIndex + 1)]
      : [v, null];
  };

  const [mainA, preA] = splitPre(stripBuild(a));
  const [mainB, preB] = splitPre(stripBuild(b));
  const segA = mainA.split('.');
  const segB = mainB.split('.');
  const maxLen = Math.max(segA.length, segB.length);

  for (let i = 0; i < maxLen; i += 1) {
    const partA = segA[i] ?? '0';
    const partB = segB[i] ?? '0';
    const cmp = compareIdentifiers(partA, partB);
    if (cmp !== 0) {
      return cmp;
    }
  }

  if (preA === null && preB === null) {
    return 0;
  }
  if (preA === null) {
    return 1;
  }
  if (preB === null) {
    return -1;
  }

  const preSegA = preA.split('.');
  const preSegB = preB.split('.');
  const maxPreLen = Math.max(preSegA.length, preSegB.length);
  for (let i = 0; i < maxPreLen; i += 1) {
    if (i >= preSegA.length) {
      return -1;
    }
    if (i >= preSegB.length) {
      return 1;
    }
    const cmp = compareIdentifiers(preSegA[i]!, preSegB[i]!);
    if (cmp !== 0) {
      return cmp;
    }
  }
  return 0;
}

function getStorage(): Storage | null {
  if (typeof window === 'undefined') {
    return null;
  }
  try {
    return window.localStorage ?? null;
  } catch {
    return null;
  }
}

function safeGet(storage: Storage, key: string): { ok: true; value: string | null } | { ok: false } {
  try {
    return { ok: true, value: storage.getItem(key) };
  } catch {
    return { ok: false };
  }
}

function safeSet(storage: Storage, key: string, value: string): boolean {
  try {
    storage.setItem(key, value);
    return true;
  } catch {
    return false;
  }
}

function evaluateVersionKey(
  storage: Storage,
  key: string,
  currentVersion: string,
  legacyBaseline?: string
): boolean {
  const existing = safeGet(storage, key);
  if (!existing.ok) {
    return false;
  }
  if (existing.value !== null && !isValidVersion(existing.value)) {
    // Corrupted stored value: fail closed without overwriting.
    return false;
  }
  const baseline =
    existing.value ?? (isValidVersion(legacyBaseline) ? legacyBaseline : null);
  const isHigher =
    baseline === null || compareVersions(currentVersion, baseline) > 0;
  if (isHigher) {
    return safeSet(storage, key, currentVersion);
  }
  if (existing.value === null && baseline !== null) {
    // Persist the legacy baseline so future launches don't need legacy input.
    safeSet(storage, key, baseline);
  }
  return false;
}

export function initFirstLaunch(options?: FirstLaunchInitOptions): void {
  if (initialized) {
    return;
  }
  initialized = true;

  const validAppVersion = isValidVersion(options?.appVersion)
    ? options.appVersion
    : undefined;
  const validBundleVersion = isValidVersion(options?.bundleVersion)
    ? options.bundleVersion
    : undefined;
  startupAppVersion = validAppVersion;
  startupBundleVersion = validBundleVersion;

  const storage = getStorage();
  if (!storage) {
    cachedFirstLaunch = false;
    if (validAppVersion !== undefined) {
      cachedAppResults.set(validAppVersion, false);
    }
    if (validBundleVersion !== undefined) {
      cachedBundleResults.set(validBundleVersion, false);
    }
    return;
  }

  const isLegacyInstall = options?.isLegacyInstall === true;
  const launchedRead = safeGet(storage, FIRST_LAUNCH_KEYS.launched);
  const isFreshStorage = launchedRead.ok && launchedRead.value === null;
  const legacyBaseline = isFreshStorage ? options?.legacyAppVersion : undefined;

  // Seed or evaluate app_ver BEFORE writing `launched`, so a legacy baseline is
  // never lost even when `appVersion` is omitted or startup is interrupted.
  if (validAppVersion !== undefined) {
    cachedAppResults.set(
      validAppVersion,
      evaluateVersionKey(
        storage,
        FIRST_LAUNCH_KEYS.appVersion,
        validAppVersion,
        legacyBaseline
      )
    );
  } else if (isValidVersion(legacyBaseline)) {
    const existingApp = safeGet(storage, FIRST_LAUNCH_KEYS.appVersion);
    if (existingApp.ok && existingApp.value === null) {
      safeSet(storage, FIRST_LAUNCH_KEYS.appVersion, legacyBaseline);
    }
  }

  if (validBundleVersion !== undefined) {
    cachedBundleResults.set(
      validBundleVersion,
      evaluateVersionKey(
        storage,
        FIRST_LAUNCH_KEYS.bundleVersion,
        validBundleVersion
      )
    );
  }

  if (!launchedRead.ok) {
    cachedFirstLaunch = false;
  } else if (launchedRead.value === null) {
    const wrote = safeSet(storage, FIRST_LAUNCH_KEYS.launched, '1');
    cachedFirstLaunch = wrote && !isLegacyInstall;
  } else {
    // Present ("1" or corrupted): not a first launch, do not overwrite.
    cachedFirstLaunch = false;
  }
}

export function isFirstLaunch(): boolean {
  if (!initialized) {
    return false;
  }
  return cachedFirstLaunch;
}

export function isFirstLaunchForCurrentAppVersion(
  appVersion?: string
): boolean {
  if (!initialized) {
    return false;
  }
  if (appVersion === undefined) {
    return startupAppVersion !== undefined
      ? (cachedAppResults.get(startupAppVersion) ?? false)
      : false;
  }
  if (!isValidVersion(appVersion)) {
    return false;
  }
  const cached = cachedAppResults.get(appVersion);
  if (cached !== undefined) {
    return cached;
  }
  const storage = getStorage();
  const result = storage
    ? evaluateVersionKey(storage, FIRST_LAUNCH_KEYS.appVersion, appVersion)
    : false;
  cachedAppResults.set(appVersion, result);
  return result;
}

export function isFirstLaunchForCurrentBundle(
  bundleVersion?: string
): boolean {
  if (!initialized) {
    return false;
  }
  if (bundleVersion === undefined) {
    return startupBundleVersion !== undefined
      ? (cachedBundleResults.get(startupBundleVersion) ?? false)
      : false;
  }
  if (!isValidVersion(bundleVersion)) {
    return false;
  }
  const cached = cachedBundleResults.get(bundleVersion);
  if (cached !== undefined) {
    return cached;
  }
  const storage = getStorage();
  const result = storage
    ? evaluateVersionKey(storage, FIRST_LAUNCH_KEYS.bundleVersion, bundleVersion)
    : false;
  cachedBundleResults.set(bundleVersion, result);
  return result;
}

export function resetFirstLaunch(): boolean {
  const storage = getStorage();
  if (!storage) {
    return false;
  }
  try {
    storage.removeItem(FIRST_LAUNCH_KEYS.launched);
    storage.removeItem(FIRST_LAUNCH_KEYS.appVersion);
    storage.removeItem(FIRST_LAUNCH_KEYS.bundleVersion);
  } catch {
    return false;
  }
  initialized = false;
  cachedFirstLaunch = false;
  startupAppVersion = undefined;
  startupBundleVersion = undefined;
  cachedAppResults.clear();
  cachedBundleResults.clear();
  return true;
}
