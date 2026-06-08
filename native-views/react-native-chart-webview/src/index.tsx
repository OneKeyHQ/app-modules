import { createElement, type ComponentProps } from 'react';
import { getHostComponent } from 'react-native-nitro-modules';
const ChartWebviewConfig = require('../nitrogen/generated/shared/json/ChartWebviewConfig.json');
import type { ChartWebviewMethods, ChartWebviewProps } from './ChartWebview.nitro';
import { CHART_BRIDGE_JS } from './bridge';

export type { ChartWebviewMethods, ChartWebviewProps } from './ChartWebview.nitro';
export { CHART_BRIDGE_JS } from './bridge';

const NativeChartWebviewView = getHostComponent<ChartWebviewProps, ChartWebviewMethods>(
  'ChartWebview',
  () => ChartWebviewConfig
);

// Default the document-start bridge script so consumers never have to know about
// it (and it's never absent — an optional string flipping to null is rejected by
// the native binding). Callers can still override `bridgeScript` to customize.
export function ChartWebviewView({
  bridgeScript,
  ...props
}: ComponentProps<typeof NativeChartWebviewView>) {
  return createElement(NativeChartWebviewView, {
    ...props,
    bridgeScript: bridgeScript ?? CHART_BRIDGE_JS,
  });
}
