import { useMemo, useRef } from 'react';

import { callback, getHostComponent } from 'react-native-nitro-modules';

import { QrScannerError, isQrScannerErrorCode } from './errors';
import { createScanSession } from './ur';

import type { QrScannerMethods, QrScannerProps } from './QrScanner.nitro';
import type { ScanError, ScanMode, ScanResult, UrLimits } from './ur';
import type { StyleProp, ViewStyle } from 'react-native';

// eslint-disable-next-line @typescript-eslint/no-var-requires
const QrScannerConfig = require('../nitrogen/generated/shared/json/QrScannerConfig.json');

export const QrScannerView = getHostComponent<QrScannerProps, QrScannerMethods>(
  'QrScanner',
  () => QrScannerConfig
);

export interface QrScannerComponentProps {
  /** Runs the camera. */
  active: boolean;
  mode: ScanMode;
  allowPlainText?: boolean;
  limits?: Partial<UrLimits>;
  /**
   * Changing this key starts a new scan session (drops collected UR parts).
   * Pausing via `active` keeps them.
   */
  sessionKey?: string | number;
  style?: StyleProp<ViewStyle>;
  onReady?: () => void;
  /** UR assembly progress, 0..1 (below 1 until complete). */
  onProgress?: (progress: number) => void;
  /** Fired once per session. */
  onResult?: (result: ScanResult) => void;
  /** Camera errors, and terminal UR errors (`too-large` etc.). */
  onError?: (error: QrScannerError | ScanError) => void;
}

/**
 * Camera preview wired to a shared `./ur` scan session. Native code reports
 * raw text only; this component owns mode handling and UR assembly.
 */
export function QrScanner({
  active,
  mode,
  allowPlainText,
  limits,
  sessionKey,
  style,
  onReady,
  onProgress,
  onResult,
  onError,
}: QrScannerComponentProps) {
  const handlers = useRef({ onReady, onProgress, onResult, onError });
  handlers.current = { onReady, onProgress, onResult, onError };

  // Limits are read once per session; callers pass a constant object.
  const limitsRef = useRef(limits);
  limitsRef.current = limits;

  const session = useMemo(
    () =>
      createScanSession({ mode, allowPlainText, limits: limitsRef.current }),
    // A new key, mode or plain-text rule is a new scan session.
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [mode, allowPlainText, sessionKey]
  );

  // Frames from an older capture generation arrive late; drop them. Native
  // generations only grow for the lifetime of the view, across sessions.
  const latestGeneration = useRef(0);

  const nativeCallbacks = useMemo(
    () => ({
      onReady: callback((generation: number) => {
        latestGeneration.current = Math.max(latestGeneration.current, generation);
        handlers.current.onReady?.();
      }),
      onFrameText: callback((text: string, generation: number) => {
        if (generation < latestGeneration.current) return;
        latestGeneration.current = generation;
        const outcome = session.receive(text);
        if (outcome.status === 'progress') {
          handlers.current.onProgress?.(outcome.progress);
        } else if (outcome.status === 'complete') {
          if (outcome.result.kind === 'ur') handlers.current.onProgress?.(1);
          handlers.current.onResult?.(outcome.result);
        } else if (outcome.status === 'rejected') {
          handlers.current.onError?.(outcome.error);
        }
      }),
      onError: callback((code: string, message: string, generation: number) => {
        if (generation < latestGeneration.current) return;
        handlers.current.onError?.(
          new QrScannerError(
            isQrScannerErrorCode(code) ? code : 'decoder-unavailable',
            message
          )
        );
      }),
    }),
    [session]
  );

  return <QrScannerView style={style} active={active} {...nativeCallbacks} />;
}
