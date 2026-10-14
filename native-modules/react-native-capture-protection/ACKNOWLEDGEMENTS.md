# Acknowledgements

This module is adapted from [react-native-capture-protection](https://github.com/wn-na/react-native-capture-protection)
version **2.3.0**, authored by **lethe / wn-na**. Thank you for the upstream
screenshot, recording and app-switcher protection implementation.

The migration adapts the public event values, iOS secure-text-field layer
technique and opaque protection windows, and Android FLAG_SECURE and virtual
display detection approach. It replaces the React Native bridge with Nitro,
scopes subscriptions by native owner, and removes media-library screenshot
observation, storage permission requests, custom overlays, providers and hooks.
OneKey's prior fixes for per-runtime listener ownership and private virtual
display events are preserved in the adapted implementation.

The upstream code is distributed under the MIT license. Its copyright and full
license are retained in [LICENSE.upstream](LICENSE.upstream). The main [LICENSE](LICENSE)
also retains the upstream copyright for CocoaPods acknowledgements. Both are included
in published package contents. Adapted source files identify this notice.
