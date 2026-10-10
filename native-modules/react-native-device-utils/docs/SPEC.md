# DeviceUtils: InMemoryStore contract

## Status and scope

This specification covers the new InMemoryStore API in the existing DeviceUtils
package. Other DeviceUtils APIs retain their existing TypeScript/native
contracts. The bounded primitive store is Implemented. Production Swift/Kotlin
stores and generated union types passed standalone compiled tests in two fresh
processes per platform, including type preservation, special numeric values,
byte limits, and concurrent capacity claims. Native-host integration and device
reload acceptance remain unverified.

The store holds small string, boolean, and number values for Android/iOS callers.
It does not persist data, deliver analytics, identify devices, or implement
desktop/web/extension
storage. Consumers own key namespaces and their business-specific stage rules.

## Ownership and boundaries

One native singleton owns the Map in each OS process. Main and background JS
runtimes have separate heaps and initialize independently; their DeviceUtils
HybridObjects access that shared singleton. Values are native primitive copies,
never retained JS objects. Reads return a value copy to the calling runtime.
Separate OS processes have separate stores.

## Public API and defaults

All four methods are synchronous:

```ts
type InMemoryValue = string | boolean | number;

getInMemoryValue(key: string): InMemoryValue | undefined;
setInMemoryValue(key: string, value: InMemoryValue): void;
removeInMemoryValue(key: string): boolean;
setInMemoryValueIfAbsent(key: string, value: InMemoryValue): boolean;
```

Keys and string values may be empty strings. Reads preserve the stored primitive
type; false, 0, and empty string are present values, not missing keys. Number
values use IEEE-754 doubles, including negative zero, NaN, and infinities.
Objects, arrays, null, undefined, bigint, and functions are outside the Nitro
contract and are rejected by the bridge before storage mutation. Get returns
undefined for a missing key; set replaces an existing value; remove returns whether a key was present.
Set-if-absent returns true only when it inserts, otherwise false. It validates
both arguments before checking whether the key exists.

## Lifecycle and concurrency

All operations use the same native lock. Set-if-absent checks and inserts under
that lock, so concurrent claims on a missing key have exactly one winner.
JS reload, React remount, HybridObject disposal, and runtime recreation MUST NOT
clear the store. Explicit removal releases a key; OS process exit releases all
state. There are no callbacks, async tasks, cancellation, or implicit eviction.

## Data and identity

Keys use the exact UTF-8 bytes of the native Unicode string; neither platform
normalizes or case-folds them. For example, U+00E9 and U+0065 U+0301 are distinct
keys. There is no disk cache, TTL, migration, or automatic invalidation.

## Platform contract

| Concern | iOS | Android |
| --- | --- | --- |
| Process owner | Static singleton with private initializer | Kotlin object |
| Serialization | NSLock | Synchronized methods |
| Exact key identity | UTF-8 Data dictionary keys | Kotlin String keys |
| Retention and rejection | Shared contract below | Shared contract below |

The API requires a rebuilt native host containing these bindings. Installing
JS code onto an older host is unsupported; this is not an OTA-only API.

## Failure and retention budget

The same fixed limits apply on both platforms:

- At most 128 entries.
- Every key: at most 256 UTF-8 bytes, including keys passed to get/remove.
- Every string value: at most 4096 UTF-8 bytes. Boolean and number values use
  fixed-size native primitive payloads and are not subject to string byte limits.

An overlong key or value throws a synchronous native error. At capacity, writes
to a new key throw; replacing an existing key is allowed, and a valid
set-if-absent call on an existing key returns false. Rejected operations leave
the Map unchanged. Remove frees a slot. No entry is evicted to satisfy a write.
Error messages identify key length, value length, or entry capacity; native
exception types are platform-specific. General allocation/bridge failures may
also propagate to callers.

## Performance and resource budget

Calls perform only bounded key/value validation, a Map operation, and locking;
they do no I/O. Maximum retained logical UTF-8 payload is 557056 bytes
(128 × (256 + 4096)); native string, dictionary, and allocator overhead is extra.
Validation rejects oversized inputs without constructing an unbounded UTF-8
buffer. Bridge conversion and caller-side string allocation occur before these
native limits and are outside the retained-store budget. There is no measured
latency claim.

## Consumer handoff

Startup consumers claim independent namespaced stages only in the main UI
runtime. Native claim failure must not abort subsequent application work.
Release only a successfully claimed stage whose synchronous local enqueue
failed. Retain the claim after the logger accepts the entry; network delivery
is outside this contract. A pending JS logger queue is not durable across
runtime loss, eviction, or later processing errors.

## Conformance and acceptance

- TypeScript: `src/ReactNativeDeviceUtils.nitro.ts` and generated Nitro bindings.
- iOS: `ios/InMemoryStore.swift`, forwarded by `ios/ReactNativeDeviceUtils.swift`.
- Android: `android/src/main/java/com/margelo/nitro/reactnativedeviceutils/`
  `InMemoryStore.kt` and `ReactNativeDeviceUtils.kt`.
- After `yarn nitrogen`, `node tests/in-memory-store.cjs` compiles the production
  stores with the generated union types and exercises primitive type
  preservation, empty/missing values, removal/retry, Unicode key identity, byte limits,
  capacity/rejection atomicity, concurrent claims, and fresh processes.

The Kotlin runner supplies only a marker annotation for the generated shrinker
metadata; it does not substitute the union or storage implementation.

Standalone store tests cannot establish Nitro host integration or real JS
reload behavior. Remaining device acceptance on both Android and iOS: rebuild
the host, claim from main/bg HybridObjects, reload/recreate each runtime,
confirm retained state and independent keys, exercise a rejected write through
the bridge, then restart the OS process and confirm an empty store.
