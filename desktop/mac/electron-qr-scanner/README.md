# @onekeyfe/electron-qr-scanner (macOS)

QR decoding for OneKey desktop on macOS: local images and camera frames via
AVFoundation and Vision, for Electron's main process. Not released yet.

Contract and status: [SPEC.md](SPEC.md).

```sh
npm run build        # cargo + swiftc, writes electron-qr-scanner.darwin-<arch>.node
npm test             # Node tests (image fixtures, animated UR video)
npx electron test/electron.cjs
```
