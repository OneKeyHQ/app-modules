<div align="center">

# @onekeyfe/react-native-sni-connect

**HTTPS requests to a caller-supplied IP while TLS still validates the real hostname.**

[![npm](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-sni-connect?label=npm&color=4D6BFE)](https://www.npmjs.com/package/@onekeyfe/react-native-sni-connect)
[![license](https://img.shields.io/npm/l/%40onekeyfe%2Freact-native-sni-connect?label=license&color=4D6BFE)](LICENSE)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![TurboModule](https://img.shields.io/badge/TurboModule-4D6BFE)

</div>

Connects to a literal IP address you already resolved while preserving the real
hostname's TLS SNI, `Host` header and certificate validation, so the certificate
chain is checked against the hostname rather than the IP. Requests are text-only:
send a UTF-8 body or stream an app-sandbox file through `uploadFile()`. The
transport is EMASCurl (libcurl) on iOS and OkHttp on Android; there is no Web
implementation, every request is `https` on port 443, and each connection is
pinned to a validated public destination.

## Features

- **Pinned destination, real TLS identity** — SNI, `Host` and certificate
  verification use `hostname`; `ip` only chooses the socket target.
- **Strict request validation** — public IP literals only, relative paths only,
  control characters and module-owned routing headers rejected before I/O.
- **Bounded concurrency** — 64 active requests per process, 16 per
  `(hostname, ip)` pair, a 256-request queue and `SNI_RESOURCE_LIMIT` on overflow.
- **Request-id cancellation** — cancel one in-flight request or every request
  owned by the current runtime.
- **Binary uploads** — `uploadFile()` streams a local file up to 20 MiB;
  `isUploadFileSupported()` feature-detects older native binaries.
- **Operational hooks** — `clearDNSCache()`, per-pair `getDebugSnapshot()` and a
  `isProxyActiveForUrl()` preflight for adapters.

## Installation

```sh
yarn add @onekeyfe/react-native-sni-connect
```

Rebuild the native application after installing; the TurboModule is resolved at
import time and throws a linking error when the pods or Android native build are
missing. Android links through autolinking.

**iOS.** The pod depends on `EMASCurl`, which lives in Aliyun's spec repo. Add that source
and keep `EMASCurl` modular in the `Podfile`, then run `pod install`:

```ruby
source 'https://github.com/CocoaPods/Specs.git'
source 'https://github.com/aliyun/aliyun-specs.git'

target 'YourApp' do
  # React Native autolinks SniConnect. This explicit pod keeps Swift
  # `import EMASCurl` visible when building static libraries.
  pod 'EMASCurl', :modular_headers => true
end
```

**Android.** The application must declare the normal `INTERNET` permission:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

## Usage

```ts
import {
  request,
  cancelRequest,
  isProxyActiveForUrl,
} from '@onekeyfe/react-native-sni-connect';

const proxyActive = await isProxyActiveForUrl('https://example.com/api/v1/ping');
// Adapters should avoid SNI mode while a per-URL/system proxy is active.

const res = await request({
  requestId: 'health-check-1', // required only if you want to cancel it
  ip: '93.184.216.34', // public IP literal; private/loopback/metadata are rejected
  hostname: 'example.com', // SNI, Host header and certificate validation
  method: 'GET',
  path: '/api/v1/ping', // relative path only; absolute URLs are rejected
  headers: { 'Content-Type': 'application/json' },
  timeout: 30_000,
});

console.log(res.status, res.headers, res.data);

await cancelRequest('health-check-1');
```

HTTP `4xx` and `5xx` resolve as normal responses; inspect `status`. The caller
owns the target IP, the request id lifecycle and higher-level retry policy.

## API

| Export | Signature | Platforms | Description |
| --- | --- | :-: | --- |
| `request` | `(config: SniConnectRequest) => Promise<SniConnectResponse>` | iOS, Android | Sends one HTTPS request over the pinned SNI transport. |
| `uploadFile` | `(config: SniConnectUploadFileRequest) => Promise<SniConnectResponse>` | iOS, Android | Sends a local file body (`POST`/`PUT`, 1 byte–20 MiB). |
| `isUploadFileSupported` | `() => boolean` | iOS, Android | Reports whether the loaded native module exposes `uploadFile`. |
| `cancelRequest` | `(requestId: string) => Promise<{ success: boolean }>` | iOS, Android | Cancels the active request with that id. |
| `cancelAllRequests` | `() => Promise<{ success: boolean }>` | iOS, Android | Cancels every request owned by the current runtime. |
| `clearDNSCache` | `() => Promise<{ success: boolean }>` | iOS, Android | Evicts cached pinned destinations, clients and idle connections. |
| `getDebugSnapshot` | `({ hostname, ip }) => Promise<SniConnectDebugSnapshot>` | iOS, Android | Reads active/pending counters and request ids for one pair. |
| `isProxyActiveForUrl` | `(url: string) => Promise<boolean>` | iOS, Android | Preflight proxy probe; does not enable proxying for SNI requests. |

`SniConnectResponse` contains `data` (string), `status`, `statusText`, `headers`
and an optional `multiValueHeaders`. Limits: 1 MiB body, 8 KiB path/query, 64
headers / 32 KiB total, 10 MiB response. Exported types include
`SniConnectRequest`, `SniConnectResponse` and `SniConnectUploadFileRequest`; see
[`src/NativeSniConnect.ts`](src/NativeSniConnect.ts).

## Platform support

| Capability | iOS | Android | Web |
| --- | :-: | :-: | :-: |
| Pinned-IP request with hostname TLS | ✅ | ✅ | — |
| String request body | ✅ | ✅ | — |
| Binary file upload (`uploadFile`) | ✅ | ✅ | — |
| Per-request and bulk cancellation | ✅ | ✅ | — |
| Cache clear and debug snapshot | ✅ | ✅ | — |
| Proxy preflight | ✅ | ✅ | — |
| Repeated response headers (`multiValueHeaders`) | ⚠️ may collapse | ✅ | — |

## Caveats

> [!IMPORTANT]
> `ip` must be a public IP literal — loopback, private, link-local (including
> cloud metadata), CGNAT, multicast and reserved ranges are rejected. `path`
> cannot carry a scheme, host or port, and `Host` / `Content-Length` stay
> module-owned.

> [!WARNING]
> This is a text-response API with a 10 MiB response cap; binary response
> transport is out of scope. Send binary request bodies with `uploadFile()` and
> feature-detect it with `isUploadFileSupported()` before use.

> [!NOTE]
> On iOS the EMASCurl version used here exposes response headers as a dictionary
> view, so repeated headers may already be collapsed before `multiValueHeaders`
> is built. `isProxyActiveForUrl()` only reports proxy state; SNI transport
> always bypasses system proxy configuration.

## References

- [OSCS standard](SPEC.md)
- [Native upload contract](docs/SPEC.md)
- [Native interface](src/NativeSniConnect.ts)
- [npm package](https://www.npmjs.com/package/@onekeyfe/react-native-sni-connect)
- [Changelog](../../CHANGELOG.md)

## License

MIT. See [LICENSE](LICENSE).
