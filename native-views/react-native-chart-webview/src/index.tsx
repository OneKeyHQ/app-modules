import { createElement, useCallback, useMemo, useRef } from 'react';
import { type ViewProps } from 'react-native';
import {
  callback,
  getHostComponent,
  type NitroViewWrappedCallback,
} from 'react-native-nitro-modules';
const ChartWebviewConfig = require('../nitrogen/generated/shared/json/ChartWebviewConfig.json');
import type { ChartWebviewMethods, ChartWebviewProps } from './ChartWebview.nitro';
import { CHART_BRIDGE_JS } from './bridge';

export type { ChartWebviewMethods, ChartWebviewProps } from './ChartWebview.nitro';
export { CHART_BRIDGE_JS } from './bridge';

const NativeChartWebviewView = getHostComponent<ChartWebviewProps, ChartWebviewMethods>(
  'ChartWebview',
  () => ChartWebviewConfig
);

type MaybeWrappedCallback<T extends (...args: any[]) => void> =
  | T
  | NitroViewWrappedCallback<T>;

type ChartWebviewViewProps = Omit<
  ChartWebviewProps,
  'onMessage' | 'onLoadEnd' | 'onError'
> &
  ViewProps & {
    hybridRef?: MaybeWrappedCallback<(ref: any) => void>;
    onMessage?: MaybeWrappedCallback<(message: string) => void>;
    onLoadEnd?: MaybeWrappedCallback<() => void>;
    onError?: MaybeWrappedCallback<(message: string) => void>;
  };

function unwrapCallback<T extends (...args: any[]) => void>(
  wrapped: MaybeWrappedCallback<T> | undefined
): T | undefined {
  if (typeof wrapped === 'function') {
    return wrapped;
  }
  return wrapped?.f;
}

// Default the document-start bridge script so consumers never have to know about
// it (and it's never absent — an optional string flipping to null is rejected by
// the native binding). Callers can still override `bridgeScript` to customize.
export function ChartWebviewView({
  bridgeScript,
  hybridRef,
  onMessage,
  onLoadEnd,
  onError,
  ...props
}: ChartWebviewViewProps) {
  const hybridRefRef = useRef(unwrapCallback(hybridRef));
  hybridRefRef.current = unwrapCallback(hybridRef);
  const onMessageRef = useRef(unwrapCallback(onMessage));
  onMessageRef.current = unwrapCallback(onMessage);
  const onLoadEndRef = useRef(unwrapCallback(onLoadEnd));
  onLoadEndRef.current = unwrapCallback(onLoadEnd);
  const onErrorRef = useRef(unwrapCallback(onError));
  onErrorRef.current = unwrapCallback(onError);

  const stableHybridRef = useCallback((ref: any) => {
    hybridRefRef.current?.(ref);
  }, []);
  const stableOnMessage = useCallback((message: string) => {
    onMessageRef.current?.(message);
  }, []);
  const stableOnLoadEnd = useCallback(() => {
    onLoadEndRef.current?.();
  }, []);
  const stableOnError = useCallback((message: string) => {
    onErrorRef.current?.(message);
  }, []);

  const hasHybridRef = hybridRefRef.current != null;
  const hasOnMessage = onMessageRef.current != null;
  const hasOnLoadEnd = onLoadEndRef.current != null;
  const hasOnError = onErrorRef.current != null;

  const hybridRefProp = useMemo(
    () => (hasHybridRef ? callback(stableHybridRef) : undefined),
    [hasHybridRef, stableHybridRef]
  );
  const onMessageProp = useMemo(
    () => (hasOnMessage ? callback(stableOnMessage) : undefined),
    [hasOnMessage, stableOnMessage]
  );
  const onLoadEndProp = useMemo(
    () => (hasOnLoadEnd ? callback(stableOnLoadEnd) : undefined),
    [hasOnLoadEnd, stableOnLoadEnd]
  );
  const onErrorProp = useMemo(
    () => (hasOnError ? callback(stableOnError) : undefined),
    [hasOnError, stableOnError]
  );

  return createElement(NativeChartWebviewView, {
    ...props,
    hybridRef: hybridRefProp,
    onMessage: onMessageProp,
    onLoadEnd: onLoadEndProp,
    onError: onErrorProp,
    bridgeScript: bridgeScript ?? CHART_BRIDGE_JS,
  });
}
