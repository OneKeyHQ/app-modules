# @onekeyfe/react-native-sni-connect

OneKey SNI HTTP client for React Native. Performs HTTPS requests to a caller-supplied
IP address while preserving the original TLS SNI / `Host` of a hostname, so certificate
chain and hostname verification are still enforced against the real hostname (not the IP).

Backed by EMASCurl (libcurl) on iOS and OkHttp on Android.

## Installation

This package is published as part of the OneKey `app-modules` workspace:

```sh
yarn add @onekeyfe/react-native-sni-connect
```

iOS: add Aliyun's spec repo and make `EMASCurl` modular in your app Podfile,
then run `pod install`:

```ruby
source 'https://github.com/CocoaPods/Specs.git'
source 'https://github.com/aliyun/aliyun-specs.git'

target 'YourApp' do
  # React Native autolinks SniConnect. This explicit pod keeps Swift
  # `import EMASCurl` visible when building static libraries.
  pod 'EMASCurl', :modular_headers => true
end
```

Android autolinks.

## Usage

```ts
import {
  request,
  cancelRequest,
  cancelAllRequests,
  clearDNSCache,
  isProxyActiveForUrl,
} from '@onekeyfe/react-native-sni-connect';

const proxyActive = await isProxyActiveForUrl('https://example.com/api/v1/ping');
if (proxyActive) {
  // Product adapters should avoid entering SNI mode when a per-URL/system proxy
  // is active. Low-level SNI requests still bypass proxies directly.
}

const res = await request({
  // requestId is optional; required only if you want to cancel the request.
  requestId: 'health-check-1',
  ip: '93.184.216.34', // must be a public IP literal (private/loopback/metadata are rejected)
  hostname: 'example.com', // used for SNI, Host header and certificate validation
  method: 'GET',
  path: '/api/v1/ping', // relative path only — absolute URLs are rejected
  headers: { 'Content-Type': 'application/json' },
  timeout: 30_000,
});

console.log(res.status, res.headers, res.data);

// Cancellation (requires requestId on the request)
await cancelRequest('health-check-1');
await cancelAllRequests();

// Drop pinned-IP connections / cached clients
await clearDNSCache();
```

`multiValueHeaders` preserves repeated response headers when the native transport
exposes them as raw entries. On iOS, EMASCurl 1.5.5 currently hands this module a
`HTTPURLResponse` dictionary view, so duplicate header names may already be
collapsed by the transport before this adapter can observe them. Full duplicate
header preservation on iOS requires an EMASCurl version that exposes the raw
response header list.

## Specification

The behavior of the SNI connect module is governed by a normative,
platform-agnostic standard that all implementations (iOS, Android, Node/Desktop,
shared JS adapters, and any future platform) must conform to:

**-> [OneKey SNI Connect Standard (OSCS)](./SPEC.md)**

Any change to request validation, destination pinning, TLS validation, redirect
handling, cancellation, response shape, or cache behavior must be checked
against OSCS.
When an implementation and the standard disagree, the implementation is wrong.

### Security notes

- The request scheme is always `https` on port `443`; `path` cannot override scheme, host or port.
- `isProxyActiveForUrl(url)` is a preflight probe for adapters. It does not enable proxying;
  native SNI transport still bypasses system proxy configuration.
- `ip` must be an IPv4/IPv6 literal that routes to a public destination; loopback, private,
  link-local (incl. cloud metadata), CGNAT, multicast and reserved ranges are rejected.
- Header names/values containing CR/LF/control characters are rejected; the `Host` header is
  managed by the module.
- Native logs go through OneKey's native logger (with sensitive-data redaction); there is no
  JS log event channel.

## License

MIT
