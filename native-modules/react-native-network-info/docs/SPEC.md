# NetworkInfo contract

## Purpose and scope

Expose read-only network/interface queries through the existing RNNetworkInfo
TurboModule. This change adds Mac Catalyst compilation compatibility for the
IPv4 gateway helper; it does not change public APIs or select a new interface.

## Definitions and ownership

The OS owns interfaces, routing state, and Wi-Fi permissions. The module reads
that state. Each JS runtime owns its imported wrapper; main and background
heaps are independent. The application owns native module registration.

## Public API and defaults

`src/NativeNetworkInfo.ts` defines the existing Promise methods: `getSSID`,
`getBSSID`, `getBroadcast`, `getIPAddress`, `getIPV6Address`,
`getGatewayIPAddress`, `getIPV4Address`, `getWIFIIPV4Address`, `getSubnet`, and
`getFrequency`. No arguments, defaults, return types, or bridge names change.
No new fallback or sentinel values are introduced by this compatibility fix.

## Lifecycle and concurrency

Queries do not require mounting a view or opening a camera. Existing native
queues and Promise completion remain unchanged. Gateway query buffers remain
local to each invocation; the fix does not introduce shared mutable state.

## Data and persistence

No new cache, persistence, network request, or user-data migration is added.
Network details may be sensitive; acceptance evidence must not publish actual
IP addresses, gateways, SSIDs, or BSSIDs.

## Platform contract

- iOS: retain the existing inline routing definitions because its SDK does not
  provide the routing header used here.
- Android: implementation unchanged.
- Mac Catalyst: use `<net/route.h>` for the actual system routing structures;
  do not redeclare the iOS structures over the Catalyst SDK definitions.
- Web: not supported by this native package.

The existing `en0`/simulator interface selection is preserved. Availability of
Wi-Fi names and routing information remains subject to OS permissions and
network configuration. No full method/platform parity claim is made.

## Failure and cleanup

Preserve existing native Promise/error/empty-value behavior for unavailable
interfaces, permissions, or route queries. The routing helper still returns its
existing failure status and releases its per-call allocation on completed reads.
No new retry or background task is introduced.

## Performance and limits

The same OS interface/routing query is used with system-compatible structures.
No performance improvement, larger workload, or additional allocation is claimed.

## Conformance and acceptance

- Source: `src/NativeNetworkInfo.ts`, `ios/NetworkInfo.mm`, and `ios/getgateway.c`.
- Implemented: Catalyst selects the system routing definitions; ordinary iOS
  keeps the previous preprocessor branch.
- Runtime verified with the local source fix: signed ARM64 Catalyst App builds,
  and main/background queries for IP, gateway, subnet, and broadcast return
  strings. Actual addresses are omitted from evidence.
- Required before consumer delivery: compile both Catalyst and iOS helper
  branches, verify the published package contains the fix, and rebuild/recheck
  the consumer using that package.
- Not verified here: Android device queries, Wi-Fi permissions/SSID/BSSID,
  offline/interface switching, IPv6, and physical-device iOS acceptance.
