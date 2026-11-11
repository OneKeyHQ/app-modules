<div align="center">

# @onekeyfe/react-native-native-overlay

**Overlay hosts that draw above the app on iOS, Android and Web while keeping the caller's React subtree and hit testing intact.**

[![npm](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-native-overlay?label=npm&color=4D6BFE)](https://www.npmjs.com/package/@onekeyfe/react-native-native-overlay)
[![license](https://img.shields.io/npm/l/%40onekeyfe%2Freact-native-native-overlay?label=license&color=4D6BFE)](LICENSE)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![Web](https://img.shields.io/badge/Web-4285F4?logo=googlechrome&logoColor=white)
![Fabric](https://img.shields.io/badge/Fabric-4D6BFE)

</div>

Owns where an overlay is drawn, which overlay is on top and how it enters and
exits. Levels are fixed (`modal`, `hardware`, `secure`, `toast`, `lock`,
`debug`), each entry picks a `global` or `page` scope and a lane strategy, and
the native host reparents the caller's content so React context, refs and
`measureInWindow` keep working. Sheet, center, fullscreen, toast and anchored
presentations are available; the application owns the content and business UI.

## Features

- **Predictable ordering** — every page entry renders below every global entry,
  and inside a scope entries sort by level and then by request order.
- **Lane strategies** — `stack`, `queue` and `replace` apply per
  (scope, host, level) lane, with `priority`, `replaceKey` and de-duplicated ids.
- **Preserved React identity** — the overlay view stays where the caller
  rendered it; native code moves its content into the host, so there is no
  portal that drops context.
- **Page scope that covers chrome** — `OverlayPageHost` as the last child of a
  root route lets page overlays cover native headers and the tab bar.
- **Native presentation and motion** — sheet drag, back handling, keyboard
  avoidance, status bar styling and configurable enter/exit animations.
- **Web parity** — the Web implementation portals into z-indexed level layers
  with a focus trap and page isolation.

## Installation

```sh
yarn add @onekeyfe/react-native-native-overlay
```

The native hosts are React Native Fabric codegen components, not Nitro views.
Install the app's iOS pods and rebuild the native application after adding the
package. Web additionally needs `react-dom`; it is declared as an optional peer
dependency.

## Usage

```tsx
import { useState } from 'react';
import {
  OverlayPageHost,
  OverlayView,
} from '@onekeyfe/react-native-native-overlay';

function MainRoute() {
  const [visible, setVisible] = useState(false);
  return (
    <>
      {screen}
      <OverlayView
        visible={visible}
        presentation="sheet"
        level="modal"
        sheet={{ dismissOnPanDown: true, cornerRadius: 24 }}
        onRequestDismiss={() => setVisible(false)}
      >
        {content}
      </OverlayView>
      {/* Last child of the root route, after its navigator. */}
      <OverlayPageHost hostKey="main" />
    </>
  );
}
```

The application still owns `visible` and the content. `scope="page"` resolves
`hostKey`/`ownerKey` from the nearest `OverlayPageHostScope` and
`OverlayPageOwnerScope` providers.

## API

| Export | Shape | Description | Platforms |
| --- | --- | --- | :-: |
| `OverlayView` | component | Declarative overlay: `visible`, `scope`, `level`, `strategy`, `priority`, `replaceKey`, `presentation`, `sheet`, `animation`, `backdrop`, `blocking`, `keepContentMounted`, `statusBarStyle`, `contentSize`, dismiss/present callbacks. | iOS, Android, Web |
| `OverlayPageHost` | component | Renders the page-scope overlays of one root route from `hostKey`. | iOS, Android, Web |
| `OverlayPageHostScope`, `OverlayPageOwnerScope` | providers | Supply `hostKey` per root route and `ownerKey` per page. | iOS, Android, Web |
| `overlayStore` | store | Imperative `request`, `dismiss`, `finalize`, `reopen`, `dismissAll`, `setPageVisible`, `removePage`, `resolveBack`. | iOS, Android, Web |
| `OverlayRequestError` | error class | Thrown for invalid requests such as a page entry without keys. | iOS, Android, Web |
| `OVERLAY_LEVELS`, `OVERLAY_LEVEL_ORDER` | constants | The level list and their numeric order. | iOS, Android, Web |
| `resolveOverlayAnimation`, `OVERLAY_MOTION_PRESETS`, `NATIVE_MOTION_PRESETS`, `WEB_MOTION_PRESETS` | helpers | Resolve animation configs per platform. | iOS, Android, Web |

`presentation` accepts `sheet`, `center`, `fullscreen`, `toast` and `anchored`.
`strategy` accepts `stack`, `queue` and `replace`. See
[`docs/SPEC.md`](docs/SPEC.md) for the ownership model, platform differences and
acceptance record.

## Platform support

| Capability | iOS | Android | Web |
| --- | :-: | :-: | :-: |
| Global overlays and level ordering | ✅ | ✅ | ✅ |
| Page scope via `OverlayPageHost` | ✅ | ✅ | ✅ |
| Sheet presentation | ✅ | ✅ | ✅ |
| Keyboard avoidance | ✅ | ✅ | ✅ |
| `windowOffsetRequest` for modal-relative anchors | ✅ | — | — |
| `contentSize` for pre-rotation layout | ✅ | ✅ | — |
| Status bar styling | ✅ | ✅ | — |

## Caveats

> [!IMPORTANT]
> Page-scope entries need both `hostKey` and `ownerKey`. A declarative
> `OverlayView` waits until the nearest scope providers supply them, but a
> direct `overlayStore.request` with a missing key throws `OverlayRequestError`.

> [!NOTE]
> The `windowOffsetRequest`/`onWindowOffset` and `contentSize` props are
> native-only layout aids; they do nothing on Web. On Web the overlay portals
> into a level layer and relies on `react-dom`.

## References

- [Package spec](docs/SPEC.md)
- [Public exports](src/index.ts)
- [Changelog](../../CHANGELOG.md)

## License

MIT. See [LICENSE](LICENSE).
