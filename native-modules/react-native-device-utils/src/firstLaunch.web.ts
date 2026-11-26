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
const validVersionPattern =
  /^[0-9]+(?:\.[0-9]+)*(?:-[0-9A-Za-z-]+(?:\.[0-9A-Za-z-]+)*)?(?:\+[0-9A-Za-z-]+(?:\.[0-9A-Za-z-]+)*)?$/;
const digitsOnly = /^[0-9]+$/;

let initialized = false;
let storageAvailable = false;
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

function parseOptionalVersionField(
  value: unknown
): { ok: true; value: string | null } | { ok: false } {
  if (value === undefined || value === null) {
    return { ok: true, value: null };
  }
  if (isValidVersion(value)) {
    return { ok: true, value };
  }
  return { ok: false };
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
    const parsed: unknown = JSON.parse(raw);
    if (
      typeof parsed !== 'object' ||
      parsed === null ||
      Array.isArray(parsed)
    ) {
      return null;
    }
    const record = parsed as Record<string, unknown>;
    if (typeof record.hasLaunched !== 'boolean') {
      return null;
    }
    const appField = parseOptionalVersionField(record.highestAppVersion);
    const bundleField = parseOptionalVersionField(record.highestBundleVersion);
    if (!appField.ok || !bundleField.ok) {
      return null;
    }
    return {
      state: {
        hasLaunched: record.hasLaunched,
        highestAppVersion: appField.value,
        highestBundleVersion: bundleField.value,
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

  const failClosed = () => {
    storageAvailable = false;
    cachedFirstLaunch = false;
    if (validAppVersion !== undefined) {
      cachedAppResults.set(validAppVersion, false);
    }
    if (validBundleVersion !== undefined) {
      cachedBundleResults.set(validBundleVersion, false);
    }
  };

  const storage = getStorage();
  if (!storage) {
    failClosed();
    return;
  }

  const isLegacyInstall = options?.isLegacyInstall === true;
  const loaded = readState(storage, options?.legacyAppVersion);
  if (!loaded) {
    failClosed();
    return;
  }

  const { state, existed } = loaded;
  const nextState: PersistedFirstLaunchState = { ...state };
  let needsWrite = !existed;

  const computedFirstLaunch = !state.hasLaunched && !isLegacyInstall;
  if (!state.hasLaunched) {
    nextState.hasLaunched = true;
    needsWrite = true;
  }

  let computedAppVersion = false;
  if (validAppVersion !== undefined) {
    computedAppVersion =
      nextState.highestAppVersion === null ||
      compareVersions(validAppVersion, nextState.highestAppVersion) > 0;
    if (computedAppVersion) {
      nextState.highestAppVersion = validAppVersion;
      needsWrite = true;
    }
  }

  let computedBundleVersion = false;
  if (validBundleVersion !== undefined) {
    computedBundleVersion =
      nextState.highestBundleVersion === null ||
      compareVersions(validBundleVersion, nextState.highestBundleVersion) > 0;
    if (computedBundleVersion) {
      nextState.highestBundleVersion = validBundleVersion;
      needsWrite = true;
    }
  }

  if (needsWrite && !writeState(storage, nextState)) {
    failClosed();
    return;
  }

  storageAvailable = true;
  cachedFirstLaunch = computedFirstLaunch;
  if (validAppVersion !== undefined) {
    cachedAppResults.set(validAppVersion, computedAppVersion);
  }
  if (validBundleVersion !== undefined) {
    cachedBundleResults.set(validBundleVersion, computedBundleVersion);
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
  if (!storageAvailable) {
    cachedAppResults.set(appVersion, false);
    return false;
  }
  const storage = getStorage();
  if (!storage) {
    cachedAppResults.set(appVersion, false);
    return false;
  }
  const loaded = readState(storage);
  if (!loaded) {
    storageAvailable = false;
    cachedAppResults.set(appVersion, false);
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
      cachedAppResults.set(appVersion, false);
      return false;
    }
  }
  cachedAppResults.set(appVersion, isHigher);
  return isHigher;
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
  if (!storageAvailable) {
    cachedBundleResults.set(bundleVersion, false);
    return false;
  }
  const storage = getStorage();
  if (!storage) {
    cachedBundleResults.set(bundleVersion, false);
    return false;
  }
  const loaded = readState(storage);
  if (!loaded) {
    storageAvailable = false;
    cachedBundleResults.set(bundleVersion, false);
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
      return false;
    }
  }
  cachedBundleResults.set(bundleVersion, isHigher);
  return isHigher;
}
