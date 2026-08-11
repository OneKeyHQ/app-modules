# @onekeyfe/react-native-network-throttle

React Native native network throttle for OneKey iOS and Android development settings.

Current scope is RN HTTP(S) latency and upload/download throughput. It does not
emulate offline mode, WebView traffic, or third-party native networking stacks.

`bypassUrlOrigins` excludes exact HTTP(S) origins from all throttling. Origins
are canonicalized with their effective port and registered additively for the
lifetime of the native process. This allows independently initialized React
Native runtimes to register local development servers without clearing each
other's configuration.

This package only owns native request throttling. Product settings, persistence, and UI controls should remain in the host app.
