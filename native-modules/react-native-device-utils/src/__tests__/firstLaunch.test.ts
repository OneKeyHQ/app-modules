const originalWindow = Object.getOwnPropertyDescriptor(globalThis, 'window');

function openDocument(
  localStorage?: Pick<Storage, 'getItem' | 'setItem' | 'removeItem' | 'length'>
) {
  Object.defineProperty(globalThis, 'window', {
    configurable: true,
    value: localStorage ? { localStorage } : undefined,
  });
  return loadModule();
}

function loadModule() {
  let result!: typeof import('../firstLaunch.web');
  jest.isolateModules(() => {
    result = jest.requireActual<typeof import('../firstLaunch.web')>(
      '../firstLaunch.web'
    );
  });
  return result;
}

function createStorage(initialEntries?: Record<string, string>) {
  const values = new Map<string, string>(
    initialEntries ? Object.entries(initialEntries) : []
  );
  return {
    get length() {
      return values.size;
    },
    getItem: jest.fn((key: string) => values.get(key) ?? null),
    setItem: jest.fn((key: string, value: string) => {
      values.set(key, value);
    }),
    removeItem: jest.fn((key: string) => {
      values.delete(key);
    }),
  };
}

afterEach(() => {
  if (originalWindow) {
    Object.defineProperty(globalThis, 'window', originalWindow);
  } else {
    Reflect.deleteProperty(globalThis, 'window');
  }
});

it('records installation, highest app version, and highest bundle version in a single write', () => {
  const storage = createStorage();
  const startup = openDocument(storage);

  startup.initFirstLaunch({ appVersion: '6.5.0', bundleVersion: '1000' });
  expect(storage.setItem).toHaveBeenCalledTimes(1);
  expect(startup.isFirstLaunch()).toBe(true);
  expect(startup.isFirstLaunchForCurrentAppVersion()).toBe(true);
  expect(startup.isFirstLaunchForCurrentBundle()).toBe(true);
  // Repeated calls during the same launch stay stable and read from memory.
  expect(startup.isFirstLaunch()).toBe(true);
  expect(startup.isFirstLaunchForCurrentAppVersion('6.5.0')).toBe(true);
  expect(startup.isFirstLaunchForCurrentBundle('1000')).toBe(true);
  expect(storage.setItem).toHaveBeenCalledTimes(1);

  // Explicit lookup of another identity must not overwrite startup zero-arg results or evict startup cache.
  expect(startup.isFirstLaunchForCurrentAppVersion('6.6.0')).toBe(true);
  expect(startup.isFirstLaunchForCurrentAppVersion()).toBe(true);
  expect(startup.isFirstLaunchForCurrentAppVersion('6.5.0')).toBe(true);
  expect(startup.isFirstLaunchForCurrentBundle('999')).toBe(false);
  expect(startup.isFirstLaunchForCurrentBundle()).toBe(true);
  expect(startup.isFirstLaunchForCurrentBundle('1000')).toBe(true);

  const reload = openDocument(storage);
  reload.initFirstLaunch({ appVersion: '6.6.0', bundleVersion: '1000' });
  expect(reload.isFirstLaunch()).toBe(false);
  expect(reload.isFirstLaunchForCurrentAppVersion()).toBe(false);
  expect(reload.isFirstLaunchForCurrentBundle()).toBe(false);

  // Higher bundle only
  const ota = openDocument(storage);
  ota.initFirstLaunch({ appVersion: '6.6.0', bundleVersion: '1001' });
  expect(ota.isFirstLaunch()).toBe(false);
  expect(ota.isFirstLaunchForCurrentAppVersion()).toBe(false);
  expect(ota.isFirstLaunchForCurrentBundle()).toBe(true);
  expect(ota.isFirstLaunchForCurrentBundle()).toBe(true);

  // Rollback to lower bundle returns false and preserves highestBundleVersion
  const rollback = openDocument(storage);
  rollback.initFirstLaunch({ appVersion: '6.6.0', bundleVersion: '1000' });
  expect(rollback.isFirstLaunch()).toBe(false);
  expect(rollback.isFirstLaunchForCurrentAppVersion()).toBe(false);
  expect(rollback.isFirstLaunchForCurrentBundle()).toBe(false);

  // Upgrade app version (6.10.0 > 6.6.0 numerically) with same bundle
  const upgrade = openDocument(storage);
  upgrade.initFirstLaunch({ appVersion: '6.10.0', bundleVersion: '1001' });
  expect(upgrade.isFirstLaunch()).toBe(false);
  expect(upgrade.isFirstLaunchForCurrentAppVersion()).toBe(true);
  expect(upgrade.isFirstLaunchForCurrentBundle()).toBe(false);

  // Downgrade app version returns false
  const downgrade = openDocument(storage);
  downgrade.initFirstLaunch({ appVersion: '6.9.0', bundleVersion: '999' });
  expect(downgrade.isFirstLaunch()).toBe(false);
  expect(downgrade.isFirstLaunchForCurrentAppVersion()).toBe(false);
  expect(downgrade.isFirstLaunchForCurrentBundle()).toBe(false);
});

it('does not touch storage before initFirstLaunch so legacy init is never consumed early', () => {
  const storage = createStorage();
  const doc = openDocument(storage);

  expect(doc.isFirstLaunch()).toBe(false);
  expect(doc.isFirstLaunchForCurrentAppVersion('6.5.0')).toBe(false);
  expect(doc.isFirstLaunchForCurrentBundle('1000')).toBe(false);
  expect(storage.getItem).not.toHaveBeenCalled();
  expect(storage.setItem).not.toHaveBeenCalled();

  doc.initFirstLaunch({
    appVersion: '6.5.0',
    bundleVersion: '1000',
    isLegacyInstall: true,
    legacyAppVersion: '6.5.0',
  });
  expect(doc.isFirstLaunch()).toBe(false);
  expect(doc.isFirstLaunchForCurrentAppVersion()).toBe(false);
  expect(doc.isFirstLaunchForCurrentBundle()).toBe(true);
});

it('honors host-provided legacy upgrade flags without guessing from unrelated keys', () => {
  const thirdPartyStorage = createStorage({ 'third-party-sdk': '1' });
  const freshWithNoise = openDocument(thirdPartyStorage);
  freshWithNoise.initFirstLaunch({
    appVersion: '6.5.0',
    bundleVersion: '1000',
  });
  expect(freshWithNoise.isFirstLaunch()).toBe(true);

  const legacyUpgradeStorage = createStorage();
  const upgradedHigher = openDocument(legacyUpgradeStorage);
  upgradedHigher.initFirstLaunch({
    appVersion: '6.6.0',
    bundleVersion: '1001',
    isLegacyInstall: true,
    legacyAppVersion: '6.5.0',
  });
  expect(upgradedHigher.isFirstLaunch()).toBe(false);
  expect(upgradedHigher.isFirstLaunchForCurrentAppVersion()).toBe(true);
  expect(upgradedHigher.isFirstLaunchForCurrentBundle()).toBe(true);
});

it('fails closed and preserves storage when persisted history is corrupted', () => {
  const corruptPayloads = [
    '',
    'not-json',
    'null',
    '{}',
    '[]',
    JSON.stringify({ hasLaunched: 'yes' }),
    JSON.stringify({ hasLaunched: true, highestAppVersion: 23 }),
    JSON.stringify({ hasLaunched: true, highestAppVersion: '6.6.0!' }),
    JSON.stringify({ hasLaunched: true, highestBundleVersion: 'v1000' }),
  ];

  for (const payload of corruptPayloads) {
    const storage = createStorage({
      'onekey:device-utils:first-launch': payload,
    });
    const doc = openDocument(storage);
    doc.initFirstLaunch({ appVersion: '6.5.0', bundleVersion: '1000' });
    expect(doc.isFirstLaunch()).toBe(false);
    expect(doc.isFirstLaunchForCurrentAppVersion()).toBe(false);
    expect(doc.isFirstLaunchForCurrentBundle()).toBe(false);
    expect(storage.setItem).not.toHaveBeenCalled();
  }
});

it('validates numeric version core and handles prerelease comparisons accurately', () => {
  const storage = createStorage();
  const doc = openDocument(storage);
  doc.initFirstLaunch({ appVersion: 'v6.5.0', bundleVersion: '../1000' });
  expect(doc.isFirstLaunch()).toBe(true);
  expect(doc.isFirstLaunchForCurrentAppVersion()).toBe(false);
  expect(doc.isFirstLaunchForCurrentBundle()).toBe(false);
  expect(doc.isFirstLaunchForCurrentAppVersion('v6.5.0')).toBe(false);
  expect(doc.isFirstLaunchForCurrentAppVersion('6.5.0')).toBe(true);

  expect(doc.compareVersions('6.10.0', '6.5.0')).toBeGreaterThan(0);
  expect(doc.compareVersions('6.5.0', '6.5')).toBe(0);
  expect(doc.compareVersions('6.5.0', '6.5.0-beta.1')).toBeGreaterThan(0);
  expect(doc.compareVersions('6.5.0-beta.2', '6.5.0-beta.1')).toBeGreaterThan(
    0
  );
  expect(doc.compareVersions('1000', '999')).toBeGreaterThan(0);
});

it('returns false without browser storage and fails closed on write errors', () => {
  const server = openDocument();
  server.initFirstLaunch({ appVersion: '2.0', bundleVersion: '1000' });
  expect(server.isFirstLaunch()).toBe(false);
  expect(server.isFirstLaunchForCurrentAppVersion()).toBe(false);
  expect(server.isFirstLaunchForCurrentBundle()).toBe(false);

  Object.defineProperty(globalThis, 'window', {
    configurable: true,
    value: {
      get localStorage() {
        throw new Error('Storage is blocked');
      },
    },
  });
  const blocked = loadModule();
  blocked.initFirstLaunch({ appVersion: '2.0', bundleVersion: '1000' });
  expect(blocked.isFirstLaunch()).toBe(false);

  // If the single startup commit throws, all three startup flags must be false.
  const storage = createStorage();
  storage.setItem.mockImplementationOnce(() => {
    throw new Error('Storage is full');
  });
  const startup = openDocument(storage);
  startup.initFirstLaunch({ appVersion: '6.5.0', bundleVersion: '1000' });
  expect(startup.isFirstLaunch()).toBe(false);
  expect(startup.isFirstLaunchForCurrentAppVersion()).toBe(false);
  expect(startup.isFirstLaunchForCurrentBundle()).toBe(false);
  expect(storage.setItem).toHaveBeenCalledTimes(1);
});
