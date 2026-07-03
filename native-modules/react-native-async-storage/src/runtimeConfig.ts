export type AsyncStorageWriteArgsByMethod = {
  clear: [];
  multiSet: [[string, string][]];
  multiRemove: [string[]];
  multiMerge: [[string, string][]];
};

export type AsyncStorageWriteMethod = keyof AsyncStorageWriteArgsByMethod;

export type AsyncStorageWriteArgs<
  T extends AsyncStorageWriteMethod = AsyncStorageWriteMethod
> = AsyncStorageWriteArgsByMethod[T];

export type AsyncStorageShouldForwardWriteGetter = () => boolean;

export type AsyncStorageWriteForwarder = <T extends AsyncStorageWriteMethod>(
  method: T,
  args: AsyncStorageWriteArgs<T>
) => Promise<void>;

type AsyncStorageRuntimeGlobal = typeof globalThis & {
  __onekeyAsyncStorageShouldForwardWriteGetter?: AsyncStorageShouldForwardWriteGetter;
  __onekeyAsyncStorageWriteForwarder?: AsyncStorageWriteForwarder;
};

const runtimeGlobal = globalThis as AsyncStorageRuntimeGlobal;

let shouldForwardWriteGetter: AsyncStorageShouldForwardWriteGetter | undefined =
  runtimeGlobal.__onekeyAsyncStorageShouldForwardWriteGetter;
let writeForwarder: AsyncStorageWriteForwarder | undefined =
  runtimeGlobal.__onekeyAsyncStorageWriteForwarder;

export function setAsyncStorageShouldForwardWriteGetter(
  getter: AsyncStorageShouldForwardWriteGetter
) {
  shouldForwardWriteGetter = getter;
  runtimeGlobal.__onekeyAsyncStorageShouldForwardWriteGetter = getter;
}

export function setAsyncStorageWriteForwarder(
  forwarder: AsyncStorageWriteForwarder
) {
  writeForwarder = forwarder;
  runtimeGlobal.__onekeyAsyncStorageWriteForwarder = forwarder;
}

function getShouldForwardWriteGetter() {
  return (
    shouldForwardWriteGetter ??
    runtimeGlobal.__onekeyAsyncStorageShouldForwardWriteGetter
  );
}

function getWriteForwarder() {
  return writeForwarder ?? runtimeGlobal.__onekeyAsyncStorageWriteForwarder;
}

function shouldForwardWriteToBackground() {
  return getShouldForwardWriteGetter()?.() ?? false;
}

export async function forwardAsyncStorageWriteIfNeeded<
  T extends AsyncStorageWriteMethod
>(method: T, args: AsyncStorageWriteArgs<T>): Promise<boolean> {
  if (!shouldForwardWriteToBackground()) {
    return false;
  }

  const forwarder = getWriteForwarder();
  if (!forwarder) {
    throw new Error('AsyncStorage write forwarder is not configured.');
  }

  await forwarder(method, args);
  return true;
}
