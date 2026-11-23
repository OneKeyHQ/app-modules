# @onekeyfe/react-native-qr-scanner

Camera QR scanning, local image decoding and BC-UR assembly for OneKey.

Behavior contract: [docs/SPEC.md](docs/SPEC.md).

## Installation

```sh
yarn add @onekeyfe/react-native-qr-scanner
```

iOS: add to the app Podfile (CocoaPods needs a module map for the ZXing
fallback when building static libraries), and declare
`NSCameraUsageDescription` in `Info.plist`.

```ruby
pod 'zxing-cpp', '2.2.0', :modular_headers => true

# in post_install, inside the per-target build_configurations loop:
build_config.build_settings['USE_HEADERMAP'] = 'NO' if target.name == 'zxing-cpp'
```

See [docs/SPEC.md](docs/SPEC.md) "Integration requirement" for why.

## Usage

```tsx
import { QrScanner, decodeQrImage } from '@onekeyfe/react-native-qr-scanner';

<QrScanner
  style={{ flex: 1 }}
  active
  mode="auto"
  onResult={(result) => {
    // { kind: 'text', text } or { kind: 'ur', type, cbor }
  }}
  onError={(error) => {}}
/>;

const text = await decodeQrImage(pickedImagePath); // string | null
```

`@onekeyfe/react-native-qr-scanner/ur` exposes the platform-independent UR
assembler and scan session.

Web, extension and Linux Electron use `@onekeyfe/react-native-qr-scanner/web`
(no React Native imports):

```ts
import {
  configureWebQrScanner,
  createWebQrScanner,
  decodeQrImage,
} from '@onekeyfe/react-native-qr-scanner/web';

// Pass your bundled zxing-wasm 3.1.3 reader binary; the SDK never fetches it.
// createZXingWorker is optional and moves zxing off the page thread.
configureWebQrScanner({
  loadZXingWasm: () => loadBundledWasm(),
  createZXingWorker: () =>
    new Worker(
      new URL('@onekeyfe/react-native-qr-scanner/web/zxing-worker', import.meta.url),
      { type: 'module' }
    ),
});

const scanner = createWebQrScanner({ video, mode: 'auto', onResult });
await scanner.start();
const text = await decodeQrImage(fileFromInput); // string | null
```

## License

MIT
