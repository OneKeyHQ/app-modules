# @onekeyfe/react-native-native-overlay

Native and Web overlay hosts with ordered levels, page scope, lifecycle events,
keyboard avoidance and Fabric measurement. See [SPEC](docs/SPEC.md) for the
behavioral contract, platform differences and outstanding runtime acceptance.

```tsx
import { OverlayView } from '@onekeyfe/react-native-native-overlay';

<OverlayView open={open} presentation="sheet" level="modal">
  {content}
</OverlayView>;
```

Global levels are ordered modal, hardware, secure, toast, lock, then debug.
React context stays attached to the original subtree while the native host
reparents its contents. Page overlays use `OverlayPageHost` and
`OverlayPageHostScope` / `OverlayPageOwnerScope`. Android and iOS require the React Native new
architecture; Web additionally requires `react-dom`.

## Development

Run `yarn typecheck`, `yarn lint`, `yarn test --runInBand`, and `yarn prepare`.
This package was initialized from the repository view template and retains
Fabric rather than Nitro under the explicit exception recorded in the SPEC.

## Release

Use the repository `package-publish` CI workflow. This branch's synchronized
42-package batch is `3.0.162-alpha.268`, published under `next`; no PR merge or
stable-tag change is required. Consumers must pin the exact verified version.

## License

MIT
