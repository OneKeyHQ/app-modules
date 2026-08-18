# @onekeyfe/react-native-network-throttle

React Native native network throttle for OneKey iOS and Android development settings.

Current scope is RN HTTP(S) latency and upload/download throughput. It does not
emulate offline mode, WebView traffic, or third-party native networking stacks.

`throttleUrlHosts` is an allowlist: when it is non-empty, only requests whose
host matches are throttled, and everything else is left untouched. An entry is
either an exact host or `*.example.com`, which matches sub-domains at any depth
but not the bare apex. An empty allowlist throttles nothing.

Hosts are registered additively for the lifetime of the native process, so
independently initialized React Native runtimes cannot clear each other's
configuration.

This package only owns native request throttling. Product settings, persistence, and UI controls should remain in the host app.
