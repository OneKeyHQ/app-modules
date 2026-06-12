import type {
  HybridView,
  HybridViewMethods,
  HybridViewProps,
} from 'react-native-nitro-modules';

// A dumb WebView host + message pipe. It knows nothing about TradingView: it
// loads a `source`, injects messages into the page, and surfaces messages from
// the page. All protocol/data logic lives in the JS business layer.
export interface ChartWebviewProps extends HybridViewProps {
  // --- Source (exactly one mode) ---
  // Remote: load this URL directly.
  uri?: string;
  // Offline: serve `localBundle` (a folder bundled in the app) from a virtual
  // same-origin (iOS onekey-chart:// / Android appassets https) and navigate to
  // `entry` (default "index.html") with `paramsJson` as the query string.
  localBundle?: string;
  entry?: string;
  // Query params for the entry, as a JSON object string (e.g.
  // '{"symbol":"BTC","type":"market","theme":"dark"}'). Kept as a string so the
  // native side stays free of a structured-prop dependency.
  paramsJson?: string;
  // ANDROID ONLY: the host the offline `localBundle` is served under, so Android
  // can reuse the real TradingView origin (e.g. "tradingview.onekey.so") and read
  // the existing same-origin WebView storage with zero migration. When omitted,
  // Android falls back to the built-in "appassets.androidplatform.net" host
  // (identical to the old behavior). IGNORED on iOS, which always serves offline
  // content from the onekey-chart:// custom scheme (https can't be intercepted
  // there).
  assetHost?: string;

  // JS injected into the page at document-start (before any page JS), wiring the
  // page's outbound channels to the native transport. Single source of truth in
  // the TS layer (see CHART_BRIDGE_JS); the native side only adds a tiny
  // platform transport shim. Defaulted by the JS wrapper so it's always present.
  bridgeScript?: string;

  // --- Singleton ---
  // Reuse group: same key shares one live WebView (market / perps each one).
  reuseKey?: string;
  // Opt into the shared singleton pool. Off -> every view owns its WebView.
  pooled?: boolean;
  // Arbitration signal for who holds the shared WebView when multiple hosts
  // share a key (from useIsFocused). Native attach claims first; this resolves
  // ambiguity. NOTE: not named `isActive` — an `is`-prefixed boolean prop makes
  // Kotlin emit `setActive`/`isActive` accessors while Nitro's JNI binding
  // hardcodes `setIsActive`/`getIsActive`, causing a NoSuchMethodError at runtime.
  active?: boolean;

  // --- Debugging ---
  // Make the backing WebView inspectable (Safari Web Inspector on iOS,
  // chrome://inspect on Android). Driven by the app's "Enable Native Webview
  // Debugging" dev-mode toggle, mirroring how the main react-native-webview
  // honors it. When omitted, the native side defaults to its dev-build default
  // (iOS DEBUG / Android leaves it off). NOTE (Android): the underlying call
  // WebView.setWebContentsDebuggingEnabled is PROCESS-GLOBAL — once any WebView
  // enables it, it stays enabled process-wide until the app is killed.
  webviewDebuggingEnabled?: boolean;

  // --- Events ---
  // page -> JS, raw JSON string (the JS layer parses the $private payload).
  onMessage?: (message: string) => void;
  onLoadEnd?: () => void;
  onError?: (message: string) => void;
}

export interface ChartWebviewMethods extends HybridViewMethods {
  // JS -> page: injects `window.postMessage(message)`. `message` is a raw JSON
  // string built by the JS business layer.
  postMessage(message: string): void;
  reload(): void;
  // Drop the cached frame snapshot used to mask the reparent (move) flash, and
  // remove any visible overlay. Call to free the bitmap or force a fresh capture.
  clearSnapshot(): void;
}

export type ChartWebview = HybridView<ChartWebviewProps, ChartWebviewMethods>;
