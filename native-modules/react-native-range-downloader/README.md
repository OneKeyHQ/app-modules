# react-native-range-downloader

react-native-range-downloader

## Installation

```sh
npm install @onekeyfe/react-native-range-downloader react-native-nitro-modules

> `react-native-nitro-modules` is required as this library relies on [Nitro Modules](https://nitro.margelo.com/).
```

## Usage

```ts
import { ReactNativeRangeDownloader } from '@onekeyfe/react-native-range-downloader';

// ...

const taskId = 'chart-assets';
const destFilePath = `${ReactNativeRangeDownloader.getDownloadsDir()}/chart-assets.zip`;

const listenerId = ReactNativeRangeDownloader.addDownloadListener((event) => {
  if (event.channel === 'chart' && event.taskId === taskId) {
    console.log(event.type, event.progress, event.message);
  }
});

try {
  const result = await ReactNativeRangeDownloader.download({
    channel: 'chart',
    taskId,
    url: 'https://example.com/chart-assets.zip',
    destFilePath,
  });
  console.log(result.outcome, result.filePath, result.fallbackReason);
} finally {
  ReactNativeRangeDownloader.removeDownloadListener(listenerId);
}
```

## Specification

The behavior of the concurrent downloader is governed by a normative,
platform-agnostic standard that **all** implementations (iOS, Android, Desktop,
and any future platform) MUST conform to:

**→ [OneKey Concurrent Download Standard (OCDS)](./SPEC.md)**

Any change to download behavior on any platform must be checked against OCDS.
When an implementation and the standard disagree, the implementation is wrong.

How each platform (Node / Android / iOS) is verified against OCDS — and the
runnable verification code to re-run after any downloader change — lives in
**→ [`conformance/`](./conformance/README.md)**.

## Contributing

- [Development workflow](CONTRIBUTING.md#development-workflow)
- [Sending a pull request](CONTRIBUTING.md#sending-a-pull-request)
- [Code of conduct](CODE_OF_CONDUCT.md)

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
