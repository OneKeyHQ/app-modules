// Writable directory for the capability-extraction green-field demos.
//
// Resolved LAZILY at first use from the app sandbox via range-downloader's
// getDownloadsDir() (the app cache dir). There are NO hardcoded machine paths,
// usernames, or simulator UUIDs in source, and it works on both iOS and Android.
//
// IMPORTANT: do not resolve this at module-eval time. Touching a Nitro hybrid
// object during early bundle evaluation races Nitro's own dispatcher install
// ("__nitroDispatcher already exists"). Call this from effects/handlers instead,
// after the app (and Nitro) have finished initializing.
let cached: string | null = null;

export function getDemoWritableDir(): string {
  if (cached === null) {
    try {
      const { ReactNativeRangeDownloader } = require(
        '@onekeyfe/react-native-range-downloader',
      ) as typeof import('@onekeyfe/react-native-range-downloader');
      cached = ReactNativeRangeDownloader.getDownloadsDir() || '';
    } catch {
      cached = '';
    }
  }
  return cached;
}
