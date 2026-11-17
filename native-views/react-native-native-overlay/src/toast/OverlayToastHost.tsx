import { useCallback, useEffect, useMemo, useSyncExternalStore } from 'react';

import { StyleSheet } from 'react-native';

import NativeOverlayToaster from '../NativeOverlayToasterNativeComponent';

import { overlayToastStore } from './OverlayToastStore';
import { serializeToastItems, toToastEvent } from './toastBridge';

import type { NativeSyntheticEvent } from 'react-native';

const styles = StyleSheet.create({
  // The host only carries data to the platform; the cards are drawn in the
  // native `toast` overlay level, not inside this view.
  host: { position: 'absolute', width: 0, height: 0 },
});

/**
 * Hands the toast list to the platform renderer for as long as it is mounted.
 * It draws nothing itself.
 */
export function OverlayToastHost() {
  const snapshot = useSyncExternalStore(
    overlayToastStore.subscribe,
    overlayToastStore.getSnapshot,
    overlayToastStore.getSnapshot
  );
  useEffect(() => overlayToastStore.attachRenderer(), []);
  const toasts = useMemo(
    () => serializeToastItems(snapshot.items),
    [snapshot.items]
  );
  const config = useMemo(
    () => JSON.stringify(snapshot.config),
    [snapshot.config]
  );
  const handleToastEvent = useCallback(
    (
      event: NativeSyntheticEvent<{
        toastId: string;
        generation: number;
        type: string;
        value: string;
      }>
    ) => {
      const toastEvent = toToastEvent(event.nativeEvent);
      if (toastEvent) {
        overlayToastStore.handleEvent(toastEvent);
      }
    },
    []
  );
  return (
    <NativeOverlayToaster
      toasts={toasts}
      config={config}
      onToastEvent={handleToastEvent}
      pointerEvents="none"
      style={styles.host}
    />
  );
}
