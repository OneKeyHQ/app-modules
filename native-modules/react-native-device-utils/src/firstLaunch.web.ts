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

interface PersistedFirstLaunchState {
  hasLaunched: boolean;
  highestAppVersion: string | null;
  highestBundleVersion: string | null;
}

export const FIRST_LAUNCH_STORAGE_KEY = 'onekey:device-utils:first-launch';
const invalidVersionCharacter = /[^0-9A-Za-z._+-]/;
const digitsOnly = /^[0-9]+$/;

let cachedFirstLaunch: boolean | undefined;
let cachedAppVersionResult: boolean | undefined;
let cachedAppVersion: string | undefined;
const cachedBundleResults = new Map<string, boolean>();
let lastBundleVersion: string | undefined;

function isValidVersion(version?: string | null): version is string {
  return (
    typeof version === 'string' &&
    version.length > 0 &&
    version.length <= 120 &&
    !invalidVersionCharacter.test(version)
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

function readState(
  storage: Storage,
  legacyAppVersion?: string
): {
  state: PersistedFirstLaunchState;
  existed: boolean;
} | null {
  try {
    const raw = storage.getItem(FIRST_LAUNCH_STORAGE_KEY);
    if (raw === null) {
      return {
        state: {
          hasLaunched: false,
          highestAppVersion: isValidVersion(legacyAppVersion)
            ? legacyAppVersion
            : null,
          highestBundleVersion: null,
        },
        existed: false,
      };
    }
    const parsed = JSON.parse(raw) as Partial<PersistedFirstLaunchState> | null;
    return {
      state: {
        hasLaunched: parsed?.hasLaunched === true,
        highestAppVersion: isValidVersion(parsed?.highestAppVersion)
          ? parsed.highestAppVersion
          : null,
        highestBundleVersion: isValidVersion(parsed?.highestBundleVersion)
          ? parsed.highestBundleVersion
          : null,
      },
      existed: true,
    };
  } catch {
    return null;
  }
}

function writeState(
  storage: Storage,
  state: PersistedFirstLaunchState
): boolean {
  try {
    storage.setItem(FIRST_LAUNCH_STORAGE_KEY, JSON.stringify(state));
    return true;
  } catch {
    return false;
  }
}

function ensureFirstLaunch(
  isLegacyInstall = false,
  legacyAppVersion?: string
): boolean {
  if (cachedFirstLaunch !== undefined) {
    return cachedFirstLaunch;
  }
  const storage = getStorage();
  if (!storage) {
    cachedFirstLaunch = false;
    return false;
  }
  const loaded = readState(storage, legacyAppVersion);
  if (!loaded) {
    cachedFirstLaunch = false;
    return false;
  }
  const { state, existed } = loaded;
  if (state.hasLaunched) {
    cachedFirstLaunch = false;
    return false;
  }
  const nextState: PersistedFirstLaunchState = {
    ...state,
    hasLaunched: true,
  };
  if (!writeState(storage, nextState)) {
    cachedFirstLaunch = false;
    return false;
  }
  cachedFirstLaunch = existed ? false : !isLegacyInstall;
  return cachedFirstLaunch;
}

function resolveAppVersion(
  appVersion?: string,
  isLegacyInstall = false,
  legacyAppVersion?: string
): boolean {
  if (appVersion === undefined) {
    return cachedAppVersionResult ?? false;
  }
  if (!isValidVersion(appVersion)) {
    return false;
  }
  if (cachedAppVersionResult !== undefined && cachedAppVersion === appVersion) {
    return cachedAppVersionResult;
  }
  ensureFirstLaunch(isLegacyInstall, legacyAppVersion);
  const storage = getStorage();
  if (!storage) {
    cachedAppVersion = appVersion;
    cachedAppVersionResult = false;
    return false;
  }
  const loaded = readState(storage, legacyAppVersion);
  if (!loaded) {
    cachedAppVersion = appVersion;
    cachedAppVersionResult = false;
    return false;
  }
  const { state } = loaded;
  const isHigher =
    state.highestAppVersion === null ||
    compareVersions(appVersion, state.highestAppVersion) > 0;
  if (isHigher) {
    const nextState: PersistedFirstLaunchState = {
      ...state,
      hasLaunched: true,
      highestAppVersion: appVersion,
    };
    if (!writeState(storage, nextState)) {
      cachedAppVersion = appVersion;
      cachedAppVersionResult = false;
      return false;
    }
  }
  cachedAppVersion = appVersion;
  cachedAppVersionResult = isHigher;
  return isHigher;
}

function resolveBundleVersion(
  bundleVersion?: string,
  isLegacyInstall = false,
  legacyAppVersion?: string
): boolean {
  if (bundleVersion === undefined) {
    return lastBundleVersion !== undefined
      ? (cachedBundleResults.get(lastBundleVersion) ?? false)
      : false;
  }
  if (!isValidVersion(bundleVersion)) {
    return false;
  }
  const cached = cachedBundleResults.get(bundleVersion);
  if (cached !== undefined) {
    lastBundleVersion = bundleVersion;
    return cached;
  }
  ensureFirstLaunch(isLegacyInstall, legacyAppVersion);
  const storage = getStorage();
  if (!storage) {
    cachedBundleResults.set(bundleVersion, false);
    lastBundleVersion = bundleVersion;
    return false;
  }
  const loaded = readState(storage, legacyAppVersion);
  if (!loaded) {
    cachedBundleResults.set(bundleVersion, false);
    lastBundleVersion = bundleVersion;
    return false;
  }
  const { state } = loaded;
  const isHigher =
    state.highestBundleVersion === null ||
    compareVersions(bundleVersion, state.highestBundleVersion) > 0;
  if (isHigher) {
    const nextState: PersistedFirstLaunchState = {
      ...state,
      hasLaunched: true,
      highestBundleVersion: bundleVersion,
    };
    if (!writeState(storage, nextState)) {
      cachedBundleResults.set(bundleVersion, false);
      lastBundleVersion = bundleVersion;
      return false;
    }
  }
  cachedBundleResults.set(bundleVersion, isHigher);
  lastBundleVersion = bundleVersion;
  return isHigher;
}

export function initFirstLaunch(options?: FirstLaunchInitOptions): void {
  const isLegacyInstall = options?.isLegacyInstall === true;
  const legacyAppVersion = options?.legacyAppVersion;
  ensureFirstLaunch(isLegacyInstall, legacyAppVersion);
  if (options?.appVersion !== undefined) {
    resolveAppVersion(options.appVersion, isLegacyInstall, legacyAppVersion);
  }
  if (options?.bundleVersion !== undefined) {
    resolveBundleVersion(
      options.bundleVersion,
      isLegacyInstall,
      legacyAppVersion
    );
  }
}

export function isFirstLaunch(): boolean {
  return ensureFirstLaunch();
}

export function isFirstLaunchForCurrentAppVersion(
  appVersion?: string
): boolean {
  return resolveAppVersion(appVersion);
}

export function isFirstLaunchForCurrentBundle(
  bundleVersion?: string
): boolean {
  return resolveBundleVersion(bundleVersion);
}
