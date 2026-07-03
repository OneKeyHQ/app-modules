import NativeModule from './NativeAsyncStorage';
import { forwardAsyncStorageWriteIfNeeded } from './runtimeConfig';
import type { AsyncStorageStatic } from './types';

function createAsyncStorage(): AsyncStorageStatic {
  const getItem: AsyncStorageStatic['getItem'] = async (key, callback) => {
    try {
      const result = await NativeModule.multiGet([key]);
      const value = result?.[0]?.[1] ?? null;
      callback?.(null, value);
      return value;
    } catch (e) {
      const error = e instanceof Error ? e : new Error(String(e));
      callback?.(error);
      throw error;
    }
  };

  const setItem: AsyncStorageStatic['setItem'] = async (
    key,
    value,
    callback
  ) => {
    try {
      if (
        !(await forwardAsyncStorageWriteIfNeeded('multiSet', [[[key, value]]]))
      ) {
        await NativeModule.multiSet([[key, value]]);
      }
      callback?.(null);
    } catch (e) {
      const error = e instanceof Error ? e : new Error(String(e));
      callback?.(error);
      throw error;
    }
  };

  const removeItem: AsyncStorageStatic['removeItem'] = async (
    key,
    callback
  ) => {
    try {
      if (!(await forwardAsyncStorageWriteIfNeeded('multiRemove', [[key]]))) {
        await NativeModule.multiRemove([key]);
      }
      callback?.(null);
    } catch (e) {
      const error = e instanceof Error ? e : new Error(String(e));
      callback?.(error);
      throw error;
    }
  };

  const mergeItem: AsyncStorageStatic['mergeItem'] = async (
    key,
    value,
    callback
  ) => {
    try {
      if (
        !(await forwardAsyncStorageWriteIfNeeded('multiMerge', [
          [[key, value]],
        ]))
      ) {
        await NativeModule.multiMerge([[key, value]]);
      }
      callback?.(null);
    } catch (e) {
      const error = e instanceof Error ? e : new Error(String(e));
      callback?.(error);
      throw error;
    }
  };

  const clear: AsyncStorageStatic['clear'] = async (callback) => {
    try {
      if (!(await forwardAsyncStorageWriteIfNeeded('clear', []))) {
        await NativeModule.clear();
      }
      callback?.(null);
    } catch (e) {
      const error = e instanceof Error ? e : new Error(String(e));
      callback?.(error);
      throw error;
    }
  };

  const getAllKeys: AsyncStorageStatic['getAllKeys'] = async (callback) => {
    try {
      const keys = await NativeModule.getAllKeys();
      callback?.(null, keys);
      return keys;
    } catch (e) {
      const error = e instanceof Error ? e : new Error(String(e));
      callback?.(error);
      throw error;
    }
  };

  const flushGetRequests: AsyncStorageStatic['flushGetRequests'] = () => {
    // No-op: legacy batching API, not needed with TurboModules
  };

  const multiGet: AsyncStorageStatic['multiGet'] = async (keys, callback) => {
    try {
      const result = await NativeModule.multiGet([...keys]);
      callback?.(null, result);
      return result;
    } catch (e) {
      const error = e instanceof Error ? e : new Error(String(e));
      callback?.([error]);
      throw error;
    }
  };

  const multiSet: AsyncStorageStatic['multiSet'] = async (
    keyValuePairs,
    callback
  ) => {
    try {
      const mutablePairs = keyValuePairs.map(
        ([k, v]) => [k, v] as [string, string]
      );
      if (
        !(await forwardAsyncStorageWriteIfNeeded('multiSet', [mutablePairs]))
      ) {
        await NativeModule.multiSet(mutablePairs);
      }
      callback?.(null);
    } catch (e) {
      const error = e instanceof Error ? e : new Error(String(e));
      callback?.([error]);
      throw error;
    }
  };

  const multiRemove: AsyncStorageStatic['multiRemove'] = async (
    keys,
    callback
  ) => {
    try {
      const mutableKeys = [...keys];
      if (
        !(await forwardAsyncStorageWriteIfNeeded('multiRemove', [mutableKeys]))
      ) {
        await NativeModule.multiRemove(mutableKeys);
      }
      callback?.(null);
    } catch (e) {
      const error = e instanceof Error ? e : new Error(String(e));
      callback?.([error]);
      throw error;
    }
  };

  const multiMerge: AsyncStorageStatic['multiMerge'] = async (
    keyValuePairs,
    callback
  ) => {
    try {
      if (
        !(await forwardAsyncStorageWriteIfNeeded('multiMerge', [keyValuePairs]))
      ) {
        await NativeModule.multiMerge(keyValuePairs);
      }
      callback?.(null);
    } catch (e) {
      const error = e instanceof Error ? e : new Error(String(e));
      callback?.([error]);
      throw error;
    }
  };

  return {
    getItem,
    setItem,
    removeItem,
    mergeItem,
    clear,
    getAllKeys,
    flushGetRequests,
    multiGet,
    multiSet,
    multiRemove,
    multiMerge,
  };
}

const AsyncStorage = createAsyncStorage();

export default AsyncStorage;

export type {
  AsyncStorageShouldForwardWriteGetter,
  AsyncStorageWriteArgs,
  AsyncStorageWriteArgsByMethod,
  AsyncStorageWriteForwarder,
  AsyncStorageWriteMethod,
} from './runtimeConfig';
export type {
  AsyncStorageStatic,
  Callback,
  CallbackWithResult,
  KeyValuePair,
  MultiCallback,
  MultiGetCallback,
} from './types';
export {
  setAsyncStorageShouldForwardWriteGetter,
  setAsyncStorageWriteForwarder,
} from './runtimeConfig';
