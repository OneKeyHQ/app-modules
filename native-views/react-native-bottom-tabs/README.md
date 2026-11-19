<div align="center">

# @onekeyfe/react-native-bottom-tabs

**Native bottom-tab navigation for OneKey apps on iOS and Android — the platform owns the tab bar, the application owns routing.**

[![npm](https://img.shields.io/npm/v/%40onekeyfe%2Freact-native-bottom-tabs?label=npm&color=4D6BFE)](https://www.npmjs.com/package/@onekeyfe/react-native-bottom-tabs)
[![license](https://img.shields.io/npm/l/%40onekeyfe%2Freact-native-bottom-tabs?label=license&color=4D6BFE)](LICENSE)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![Fabric](https://img.shields.io/badge/Fabric-4D6BFE)

</div>

A controlled tab container that renders the real `UITabBarController` on iOS and a
Material or floating native bar on Android, while the application supplies the
navigation state and scene components. Tab selection, badges, icons, lazy scene
mounting, double-press detection and the optional left drawer are handled by the
native view. The package targets iOS and Android only; it has no Web
implementation and no route definitions of its own.

## Features

- **Platform-native tab bars** — UIKit tab bar on iOS, Material
  `BottomNavigationView` or a floating capsule bar on Android, with the system's
  own gestures and insets.
- **Controlled navigation state** — pass `{ index, routes }`, render scenes from
  stable route keys, and update your own state in `onIndexChange`.
- **Lazy and frozen scenes** — `lazy`/`getLazy` and `freezeOnBlur` control when a
  scene mounts and whether it keeps rendering in the background.
- **Rich tab metadata** — labels, badges, tints, SF Symbols, named icons, image
  sources, hidden tabs, test IDs and roles.
- **Native drawer and Android options** — an optional physical-left menu, a
  floating bar with live blur on Android 16+, ripple and inset controls.

## Installation

```sh
yarn add @onekeyfe/react-native-bottom-tabs @onekeyfe/react-native-image
```

Peer dependencies: `react`, `react-native` and `@onekeyfe/react-native-image`,
which provides the native SVG decoder for named and SVG tab icons. The package
registers native views, so install the app's iOS pods and rebuild the native
binary; a JavaScript-only install does not register the tab bar:

```sh
cd ios && pod install
```

Android links through autolinking; deployment floors are iOS 15.1 and Android
API 24.

## Usage

```tsx
import { useState } from 'react';
import { Text } from 'react-native';
import BottomTabs, { SceneMap } from '@onekeyfe/react-native-bottom-tabs';

const HomeScene = () => <Text>Home</Text>;
const SettingsScene = () => <Text>Settings</Text>;

const renderScene = SceneMap({ home: HomeScene, settings: SettingsScene });

const routes = [
  { key: 'home', title: 'Home' },
  { key: 'settings', title: 'Settings', badge: '3' },
];

export function AppTabs() {
  const [index, setIndex] = useState(0);
  return (
    <BottomTabs
      navigationState={{ index, routes }}
      onIndexChange={setIndex}
      renderScene={renderScene}
    />
  );
}
```

The application owns routes, selection state and scene content; the native view
owns the tab bar, gestures and scene freezing. Keep the `routes` array stable
unless the route definitions actually change.

## API

### Exports

| Export | Platforms | Description |
| --- | :-: | --- |
| `BottomTabs` (default) | iOS, Android | Controlled native tab container and scene host. |
| `SceneMap` | iOS, Android | Builds a `renderScene` function from a route-key → component map. |
| `useBottomTabBarHeight()` | iOS, Android | Native tab-bar height in logical units (`0` while hidden); must be inside the tab container. |
| `BottomTabBarHeightContext` | iOS, Android | Context that provides the same height to scene content. |
| `AppleIcon`, `NamedIcon`, `BottomTabIconSource`, `TabRole`, `TabBarPlacement` | iOS, Android | Icon and tab typing helpers, including `{ sfSymbol }` and `{ iconName }`. |

### `BottomTabs` props

| Prop | Type | Platforms | Description |
| --- | --- | :-: | --- |
| `navigationState`, `renderScene` | `{ index, routes }`, function | iOS, Android | Required selected index, keyed routes and scene renderer. |
| `onIndexChange`, `onTabDoublePress` | callbacks | iOS, Android | Tab change; second press on the selected tab within the platform double-tap timeout. |
| `renderLazyPlaceholder`, `getLazy`, `getFreezeOnBlur`, `getSceneStyle` | functions | iOS, Android | Lazy placeholder, per-route lazy/freeze and scene style. |
| `getLabelText`, `getBadge`, `getIcon`, `getHidden`, `getTestID`, `getActiveTintColor` | functions | iOS, Android | Per-route metadata and tint resolvers; each defaults to the matching route field. |
| `getRole` | function | iOS | System search-tab role. |
| `getBadgeBackgroundColor`, `getBadgeTextColor` | functions | Android | Badge background and text colors. |
| `tabBar`, `tabBarStyle`, `tabLabelStyle`, `tabBarActiveTintColor`, `tabBarInactiveTintColor` | component/props | iOS, Android | Custom JS bar replacing the native bar, plus bar background, label font and tints. |
| `tabBarHidden`, `labeled`, `hapticFeedbackEnabled`, `delayedFreeze` | `boolean` | iOS, Android | Visibility, labels, haptics and delayed freeze/unfreeze. |
| `translucent`, `disablePageAnimations`, `sidebarAdaptable`, `scrollEdgeAppearance` | platform options | iOS | Translucency, page animations, adaptable/sidebar style and scroll-edge appearance. |
| `minimizeBehavior`, `renderBottomAccessoryView` | options | iOS 26+ | Tab-bar minimizing behavior and an accessory view above the bar. |
| `onTabBarPlacementChange`, `sideAccessoryIcon`, `onSideAccessoryPress` | event/style props | iOS | Vertical placement (iPhone Duo) and its side accessory button. |
| `androidTabBarStyle`, `androidBottomBlurEnabled` | platform props | Android | Material docked or floating capsule bar; live blur defaults on for Android 16+. |
| `rippleColor`, `activeIndicatorColor`, `ignoreBottomInsets`, `ignoreKeyboardInsets` | platform props | Android | Ripple, indicator, navigation-bar and keyboard inset controls. |
| `renderDrawerContent`, `drawerOpen`, `drawerSwipeEnabled`, `onDrawerOpenChange` | drawer props | iOS, Android | Optional native left drawer and its controlled state. |

Route fields such as `title`, `badge`, `focusedIcon`, `unfocusedIcon`, `lazy`,
`hidden`, `role`, `preventsDefault` and `freezeOnBlur` mirror the resolvers above;
see [`src/types.ts`](src/types.ts) and [`src/BottomTabs.tsx`](src/BottomTabs.tsx).

## Platform support

| Capability | iOS | Android |
| --- | :-: | :-: |
| Native tab bar, selection and scenes | ✅ | ✅ |
| Lazy scenes and freeze-on-blur | ✅ | ✅ |
| Labels, badges and icon sources | ✅ | ✅ |
| Native left drawer | ✅ | ✅ |
| Double press with scroll-to-top | ✅ | ✅ |
| Floating capsule bar and live background blur | — | ✅ |
| Bottom accessory view, minimize behavior, vertical placement | ✅ iOS 26+ | — |

## Caveats

> [!WARNING]
> This package has no Web implementation. It targets the iOS and Android native
> tab bar only; do not mount it in a Web bundle.

> [!IMPORTANT]
> Android supports at most 100 tabs. Routes beyond the first 100 are dropped with
> a console warning, so keep the navigation state within that bound.

> [!NOTE]
> Tab long press is not supported: `onTabLongPress` and the native long-press
> recognizers were removed. Use `onTabDoublePress` for a second press on the
> already selected tab.

## References

- [Behavioral contract](docs/SPEC.md)
- Example pages: [test page](../../example/react-native/pages/BottomTabsTestPage.tsx)
  and [drawer page](../../example/react-native/pages/BottomTabsHomeDrawerExample.tsx)
- [npm package](https://www.npmjs.com/package/@onekeyfe/react-native-bottom-tabs)
- [Changelog](../../CHANGELOG.md)

## Upstream attribution

First, a sincere thank-you to Oskar Kwaśniewski and the
`react-native-bottom-tabs` maintainers for their excellent work 🙏

This package is built on, and inspired by,
[okwasniewski/react-native-bottom-tabs](https://github.com/okwasniewski/react-native-bottom-tabs).

Our original plan was to keep our customizations as patches on top of upstream
`react-native-bottom-tabs`.

As our product requirements evolved, the scope of those changes outgrew what we
could reasonably maintain as an upstream patch set. We regret that we were
unable to keep this work in patch form.

As the gap grew, we ultimately forked `react-native-bottom-tabs` to keep
development and delivery stable.

- Upstream repository:
  [okwasniewski/react-native-bottom-tabs](https://github.com/okwasniewski/react-native-bottom-tabs)
- License: MIT
- This fork includes OneKey-specific adaptations for our product requirements.
- For original behavior and full documentation, refer to the upstream repository.

Thank you again to Oskar Kwaśniewski and everyone who contributes to
`react-native-bottom-tabs` 💙

## License

MIT. See [LICENSE](LICENSE).
