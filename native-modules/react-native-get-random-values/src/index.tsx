const base64Decode = require('fast-base64-decode')
const ExpoCrypto = require('expo-crypto');
const NativeReactNativeGetRandomValues = require('./module').ReactNativeGetRandomValues;

class TypeMismatchError extends Error {}
class QuotaExceededError extends Error {}

let warned = false
function insecureRandomValues (array: Int8Array | Uint8Array | Int16Array | Uint16Array | Int32Array | Uint32Array | Uint8ClampedArray) {
  if (!warned) {
    console.warn('Using an insecure random number generator, this should only happen when running in a debugger without support for crypto.getRandomValues')
    warned = true
  }

  for (let i = 0, r; i < array.length; i++) {
    if ((i & 0x03) === 0) r = Math.random() * 0x100000000
    if (r !== null && r !== undefined) {
      array[i] = (r >>> ((i & 0x03) << 3)) & 0xff
    }
  }

  return array
}

function markNativeGetRandomValuesCall(message: string, isCallNativeModule: boolean) {
  if (__DEV__) {
    console.log('------------ call crypto.getRandomValues(): ' + message);
  }
  if(isCallNativeModule){
    (global as any).$$onekeyCryptoGetRandomValuesCalls = ((global as any).$$onekeyCryptoGetRandomValuesCalls || 0) + 1;
    (global as any).$$onekeyCryptoGetRandomValuesCallsLastMessage = message;
  }
}

/**
 * @param {number} byteLength
 * @returns {string}
 */
function getRandomBase64 (byteLength: number) {
  if (NativeReactNativeGetRandomValues.getRandomBase64) {
    markNativeGetRandomValuesCall('NativeModules.RNGetRandomValues.getRandomBase64(byteLength)', true);
    return NativeReactNativeGetRandomValues.getRandomBase64(byteLength)
  } else {
    markNativeGetRandomValuesCall('throw new Error("Native module not found")', false);
    throw new Error('Native module not found')
  }
}

/**
 * @param {Int8Array|Uint8Array|Int16Array|Uint16Array|Int32Array|Uint32Array|Uint8ClampedArray} array
 */
function getRandomValues (array: Int8Array | Uint8Array | Int16Array | Uint16Array | Int32Array | Uint32Array | Uint8ClampedArray) {
  if (!(array instanceof Int8Array || array instanceof Uint8Array || array instanceof Int16Array || array instanceof Uint16Array || array instanceof Int32Array || array instanceof Uint32Array || array instanceof Uint8ClampedArray)) {
    markNativeGetRandomValuesCall('throw new TypeMismatchError("Expected an integer array")', false);
    throw new TypeMismatchError('Expected an integer array')
  }

  if (array.byteLength > 65536) {
    markNativeGetRandomValuesCall('throw new QuotaExceededError("Can only request a maximum of 65536 bytes")', false);
    throw new QuotaExceededError('Can only request a maximum of 65536 bytes')
  }

  // Expo SDK 48+
  if (ExpoCrypto.getRandomValues) {
    markNativeGetRandomValuesCall('Expo SDK 48+ global.expo.modules.ExpoCrypto.getRandomValues(array)', true);
    // ExpoCrypto.getRandomValues doesn't return the array
    ExpoCrypto.getRandomValues(array)
    return array
  }

  // Calling getRandomBase64 in debug mode leads to the error
  // "Calling synchronous methods on native modules is not supported in Chrome".
  // So in that specific case we fall back to just using Math.random.
  if (__DEV__) {
    if (typeof (global as any).nativeCallSyncHook === 'undefined') {
      markNativeGetRandomValuesCall('insecureRandomValues(array)', false);
      return insecureRandomValues(array)
    }
  }

  base64Decode(getRandomBase64(array.byteLength), new Uint8Array(array.buffer, array.byteOffset, array.byteLength))

  return array
}

if (typeof global.crypto !== 'object') {
  (global as any).crypto = {}
}

if (typeof global.crypto.getRandomValues !== 'function') {
  (global as any).crypto.getRandomValues = getRandomValues
}


export { ReactNativeGetRandomValues } from './module';