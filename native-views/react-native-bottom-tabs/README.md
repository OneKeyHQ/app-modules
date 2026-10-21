# @onekeyfe/react-native-bottom-tabs

Native bottom-tab navigation for OneKey React Native applications on iOS and
Android. The controlled React component receives route state from the caller
and renders scenes through the application's supplied renderer.

## Installation and usage

```sh
yarn add @onekeyfe/react-native-bottom-tabs
```

Install the application's iOS pods and rebuild the native application to register
the bottom tabs. The application owns route definitions and selection state.

```tsx
import { useState } from 'react';
import { Text } from 'react-native';
import BottomTabs from '@onekeyfe/react-native-bottom-tabs';

const routes = [{ key: 'home', title: 'Home' }, { key: 'settings', title: 'Settings' }];

function Tabs() {
  const [index, setIndex] = useState(0);
  return (
    <BottomTabs
      navigationState={{ index, routes }}
      onIndexChange={setIndex}
      renderScene={({ route }) => <Text>{route.title}</Text>}
    />
  );
}
```

Earlier releases were published as `@onekeyfe/react-native-tab-view`, where
the default export was `TabView` and `onTabLongPress` existed.

## State, scenes and platform options

`navigationState` contains the selected index and keyed routes. `onIndexChange`
reports requested tab changes; update state in the caller. Route metadata can
supply labels, badges and icons. `SceneMap` builds a renderer from stable scene
components; `useBottomTabBarHeight` and `BottomTabBarHeightContext` expose tab-bar
height to content under the provider.

The default export is `BottomTabs`; `AppleIcon` and `TabRole` are exported types.
Some appearance and interaction options are platform-specific, including Apple
symbols and newer iOS tab-bar presentation. Check those options against the
consumer's supported OS versions rather than assuming identical native layouts.

On Android, `androidBottomBlurEnabled` controls live background blur, translucent
tint, and a gradient above the native bar. It defaults to enabled on Android 16+
(API 36); pass `false` to disable it or `true` to enable it on Android 12–15.
Earlier Android versions retain the standard bar. It is ignored on iOS and with
a custom `tabBar`. When enabled, scenes extend behind the bar: use
`useBottomTabBarHeight()` as scroll-content bottom padding so the last row can
scroll above it. `tabBarStyle.backgroundColor` sets the surface tint.

See [src/BottomTabs.tsx](src/BottomTabs.tsx) for component props and
[src/types.ts](src/types.ts) for route definitions.

See [docs/SPEC.md](docs/SPEC.md) for native touch-selection contracts and
acceptance boundaries.

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

## Upstream Project

- Repository: [okwasniewski/react-native-bottom-tabs](https://github.com/okwasniewski/react-native-bottom-tabs)
- License: MIT

## Notes

- This fork includes OneKey-specific adaptations for our product requirements.
- If you are looking for original behavior and full documentation, please refer
  to the upstream repository.

Thank you again to Oskar Kwaśniewski and everyone who contributes to
`react-native-bottom-tabs` 💙
