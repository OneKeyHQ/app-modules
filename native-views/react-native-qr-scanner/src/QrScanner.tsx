import { useCallback, useLayoutEffect, useMemo, useRef } from 'react';

import { callback, getHostComponent } from 'react-native-nitro-modules';

import { QrScannerError, isQrScannerErrorCode } from './errors';
import { createScanSession } from './ur';

import type { QrScannerMethods, QrScannerProps } from './QrScanner.nitro';
import type { QrDetectedSymbol } from './symbols';
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
  /**
   * `QrCameraInfo.id` from `listCameras()`. Unset or disconnected uses the
   * back camera. Switching keeps the scan session (collected UR parts).
   */
  cameraId?: string;
  /** Enable whole-frame multi-symbol reporting via `onFrameSymbols`. Default false. */
  multiSymbol?: boolean;
  /** Freeze the last preview frame and pause detection without releasing the camera. */
  frozen?: boolean;
  mode: ScanMode;
  allowPlainText?: boolean;
  limits?: Partial<UrLimits>;
  /**
   * Changing this key starts a new scan session (drops collected UR parts).
   * Pausing via `active` keeps them.
   */
  sessionKey?: string | number;
  style?: StyleProp<ViewStyle>;
  onReady?: (cameraId: string) => void;
  /** Whole-frame symbols in normalized visible view coordinates (`[0, 1]`). */
  onFrameSymbols?: (symbols: QrDetectedSymbol[]) => void;
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
  cameraId,
  multiSymbol,
  frozen,
  mode,
  allowPlainText,
  limits,
  sessionKey,
  style,
  onReady,
  onFrameSymbols,
  onProgress,
  onResult,
  onError,
}: QrScannerComponentProps) {
  const handlers = useRef({
    onReady,
    onFrameSymbols,
    onProgress,
    onResult,
    onError,
  });
  useLayoutEffect(() => {
    handlers.current = {
      onReady,
      onFrameSymbols,
      onProgress,
      onResult,
      onError,
    };
  }, [onReady, onFrameSymbols, onProgress, onResult, onError]);

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

  // Events from an older capture generation arrive late; drop them. Native
  // generations only grow for the lifetime of the view, across sessions.
  const latestGeneration = useRef(0);
  const acceptGeneration = useCallback((generation: number) => {
    if (generation < latestGeneration.current) return false;
    latestGeneration.current = generation;
    return true;
  }, []);

  const captureIdentity = useMemo(() => ({}), [active, cameraId]);
  const sessionIdentity = useMemo(() => ({}), [captureIdentity, session]);
  const committed = useRef<{ capture: object; session: object } | undefined>(undefined);
  useLayoutEffect(() => {
    committed.current = active ? { capture: captureIdentity, session: sessionIdentity } : undefined;
    return () => { committed.current = undefined; };
  }, [active, captureIdentity, sessionIdentity]);

  const nativeOnReady = useMemo(
    () => callback((generation: number, boundCameraId: string) => {
      if (committed.current?.capture !== captureIdentity || !acceptGeneration(generation)) return;
      handlers.current.onReady?.(boundCameraId);
    }),
    [acceptGeneration, captureIdentity]
  );

  const sessionCallbacks = useMemo(() => {
    const accept = (generation: number) =>
      committed.current?.session === sessionIdentity && acceptGeneration(generation);
    return {
      onFrameText: callback((text: string, generation: number) => {
        if (!accept(generation)) return;
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
      onFrameSymbols: callback((symbols: QrDetectedSymbol[], generation: number) => {
        if (!accept(generation)) return;
        handlers.current.onFrameSymbols?.(symbols);
      }),
      onError: callback((code: string, message: string, generation: number) => {
        if (!accept(generation)) return;
        handlers.current.onError?.(
          new QrScannerError(
            isQrScannerErrorCode(code) ? code : 'decoder-unavailable',
            message
          )
        );
      }),
    };
  }, [acceptGeneration, session, sessionIdentity]);

  return (
    <QrScannerView
      style={style}
      active={active}
      cameraId={cameraId}
      multiSymbol={multiSymbol}
      frozen={frozen}
      onReady={nativeOnReady}
      {...sessionCallbacks}
    />
  );
}
