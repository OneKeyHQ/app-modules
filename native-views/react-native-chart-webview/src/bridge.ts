// The bridge script injected at document-start into the chart WebView (before any
// page JS runs). It wires the chart's outbound channels to a single native
// transport hook, `window.__chartNativePost(payloadString)`, which each platform
// defines in a tiny shim (Android -> AndroidChartBridge, iOS -> WKScriptMessage).
//
// This is the single source of truth shared by iOS and Android — the native
// layers receive it via the `bridgeScript` prop instead of each hardcoding their
// own copy (which previously drifted between platforms).
//
// Outbound channels wired:
//   - window.$onekey.$private.request(payload)   (native platform path)
//   - window.ReactNativeWebView.postMessage(str)
//   - window 'message' events scoped to `$private`
export const CHART_BRIDGE_JS = `(function(){
  if (window.__onekeyChartBridge) return; window.__onekeyChartBridge = true;
  var fwd = function(m){
    try { window.__chartNativePost(typeof m === 'string' ? m : JSON.stringify(m)); } catch (e) {}
  };
  window.$onekey = window.$onekey || {};
  window.$onekey.$private = window.$onekey.$private || {};
  window.$onekey.$private.request = function(m){ fwd(m); };
  window.ReactNativeWebView = window.ReactNativeWebView || {};
  window.ReactNativeWebView.postMessage = function(s){ fwd(String(s)); };
  window.addEventListener('message', function(e){
    try { var d = e && e.data; if (d && d.scope === '$private') fwd(d); } catch (err) {}
  });
})();`;
