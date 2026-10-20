# @onekeyfe/react-native-tab-view

Native bottom-tab navigation for OneKey React Native applications on iOS and
Android. The controlled React component receives route state from the caller
and renders scenes through the application's supplied renderer.

## Installation and usage

```sh
yarn add @onekeyfe/react-native-tab-view
```

Install the application's iOS pods and rebuild the native application to register
the tab view. The application owns route definitions and selection state.

```tsx
import { useState } from 'react';
import { Text } from 'react-native';
import TabView from '@onekeyfe/react-native-tab-view';

const routes = [{ key: 'home', title: 'Home' }, { key: 'settings', title: 'Settings' }];

function Tabs() {
  const [index, setIndex] = useState(0);
  return (
    <TabView
      navigationState={{ index, routes }}
      onIndexChange={setIndex}
      renderScene={({ route }) => <Text>{route.title}</Text>}
    />
  );
}
```

## State, scenes and platform options

`navigationState` contains the selected index and keyed routes. `onIndexChange`
reports requested tab changes; update state in the caller. Route metadata can
supply labels, badges and icons. `SceneMap` builds a renderer from stable scene
components; `useBottomTabBarHeight` and `BottomTabBarHeightContext` expose tab-bar
height to content under the provider.

The default export is `TabView`; `AppleIcon` and `TabRole` are exported types.
Some appearance and interaction options are platform-specific, including Apple
symbols and newer iOS tab-bar presentation. Check those options against the
consumer's supported OS versions rather than assuming identical native layouts.

See [src/TabView.tsx](src/TabView.tsx) for component props and
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
