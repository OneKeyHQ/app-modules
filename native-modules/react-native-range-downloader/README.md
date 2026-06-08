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

## Contributing

- [Development workflow](CONTRIBUTING.md#development-workflow)
- [Sending a pull request](CONTRIBUTING.md#sending-a-pull-request)
- [Code of conduct](CODE_OF_CONDUCT.md)

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
