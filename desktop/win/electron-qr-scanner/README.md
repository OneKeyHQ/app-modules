# electron-qr-scanner (Windows)

In-development native module for Windows Electron: camera enumeration and
switching, live camera QR capture, local image decoding, and D3D11 shared
texture preview (Media Foundation + WIC + D3D11 + ZXing-C++). Not released yet;
see [SPEC.md](SPEC.md) for the current contract and status.

## Build and test

Requires Rust 1.88+, Visual Studio 2022 C++ Build Tools, CMake, and Node 20+.

```bash
npm run build
npm test
npm run test:types
npm run test:electron
npm run test:preview
```
