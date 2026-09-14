import type { ReactNode } from 'react';

import type { NativeSheetDismissReason, NativeSheetShowOptions } from './index';

export interface NativeSheetRegistryEntry {
  id: number;
  open: boolean;
  presentationRequested: boolean;
  content: ReactNode;
  options: NativeSheetShowOptions;
}

type RegistryListener = () => void;

const PRESENTATION_CLOSE_FALLBACK_MS = 5_000;

let nextId = 1;
let entries: readonly NativeSheetRegistryEntry[] = [];
const listeners = new Set<RegistryListener>();
let securityFallbackTimer: ReturnType<typeof setTimeout> | undefined;
const closeFallbackTimers = new Map<number, ReturnType<typeof setTimeout>>();
const registryBlockers = new Set<symbol>();

function clearCloseFallbackTimer(id: number) {
  const timer = closeFallbackTimers.get(id);
  if (timer) {
    clearTimeout(timer);
    closeFallbackTimers.delete(id);
  }
}

function emitChange() {
  listeners.forEach((listener) => listener());
}

export function subscribeNativeSheetRegistry(listener: RegistryListener) {
  listeners.add(listener);
  return () => listeners.delete(listener);
}

export function getNativeSheetRegistrySnapshot() {
  return entries;
}

export function addNativeSheetRegistryEntry(
  options: NativeSheetShowOptions,
  content: ReactNode
) {
  const id = nextId;
  nextId += 1;
  if (registryBlockers.size > 0) {
    options.onOpenChange?.(true);
    options.onOpenChange?.(false);
    options.onDismiss?.('security');
    options.onAnimationComplete?.({ open: false });
    return id;
  }
  const entry: NativeSheetRegistryEntry = {
    id,
    open: true,
    presentationRequested: false,
    content,
    options,
  };
  entries = [...entries, entry];
  options.onOpenChange?.(true);
  emitChange();
  return id;
}

export function closeNativeSheetRegistryEntry(id: number) {
  const entry = entries.find((item) => item.id === id);
  if (!entry || !entry.open) {
    return;
  }
  entry.options.onOpenChange?.(false);
  if (!entry.presentationRequested) {
    entries = entries.filter((item) => item.id !== id);
    entry.options.onDismiss?.('programmatic');
    entry.options.onAnimationComplete?.({ open: false });
  } else {
    entries = entries.map((item) =>
      item.id === id ? { ...item, open: false } : item
    );
    if (!closeFallbackTimers.has(id)) {
      closeFallbackTimers.set(
        id,
        setTimeout(() => {
          closeFallbackTimers.delete(id);
          const pendingEntry = entries.find((item) => item.id === id);
          if (!pendingEntry) {
            return;
          }
          finishNativeSheetRegistryEntry(id, 'programmatic');
          pendingEntry.options.onAnimationComplete?.({ open: false });
        }, PRESENTATION_CLOSE_FALLBACK_MS)
      );
    }
  }
  emitChange();
}

export function markNativeSheetRegistryEntryPresentationRequested(id: number) {
  entries = entries.map((entry) =>
    entry.id === id && !entry.presentationRequested
      ? { ...entry, presentationRequested: true }
      : entry
  );
}

export function finishNativeSheetRegistryEntry(
  id: number,
  reason: NativeSheetDismissReason
) {
  const entry = entries.find((item) => item.id === id);
  if (!entry) {
    return;
  }
  clearCloseFallbackTimer(id);
  if (entry.open) {
    entry.options.onOpenChange?.(false);
  }
  entries = entries.filter((item) => item.id !== id);
  entry.options.onDismiss?.(reason);
  emitChange();
}

export function finishAllNativeSheetRegistryEntries(
  reason: NativeSheetDismissReason
) {
  const currentEntries = entries;
  if (!currentEntries.length) {
    return;
  }
  entries = [];
  currentEntries.forEach((entry) => {
    clearCloseFallbackTimer(entry.id);
    if (entry.open) {
      entry.options.onOpenChange?.(false);
    }
    entry.options.onDismiss?.(reason);
    entry.options.onAnimationComplete?.({ open: false });
  });
  emitChange();
}

export function requestSecurityDismissAllNativeSheets() {
  if (!entries.length) {
    return;
  }
  const unpresentedEntries = entries.filter(
    (entry) => !entry.presentationRequested
  );
  const presentedEntries = entries.filter(
    (entry) => entry.presentationRequested
  );
  unpresentedEntries.forEach((entry) => {
    if (entry.open) {
      entry.options.onOpenChange?.(false);
    }
    entry.options.onDismiss?.('security');
    entry.options.onAnimationComplete?.({ open: false });
  });
  entries = presentedEntries.map((entry) => {
    if (entry.open) {
      entry.options.onOpenChange?.(false);
      return { ...entry, open: false };
    }
    return entry;
  });
  emitChange();
  if (!entries.length) {
    return;
  }
  if (securityFallbackTimer) {
    clearTimeout(securityFallbackTimer);
  }
  securityFallbackTimer = setTimeout(() => {
    securityFallbackTimer = undefined;
    finishAllNativeSheetRegistryEntries('security');
  }, 500);
}

export function setNativeSheetRegistryBlocked(
  blockerId: symbol,
  blocked: boolean
) {
  const wasBlocked = registryBlockers.size > 0;
  if (blocked) {
    registryBlockers.add(blockerId);
  } else {
    registryBlockers.delete(blockerId);
  }
  if (!wasBlocked && registryBlockers.size > 0) {
    requestSecurityDismissAllNativeSheets();
  }
}

export function resetNativeSheetRegistryForTests() {
  if (securityFallbackTimer) {
    clearTimeout(securityFallbackTimer);
    securityFallbackTimer = undefined;
  }
  closeFallbackTimers.forEach(clearTimeout);
  closeFallbackTimers.clear();
  entries = [];
  nextId = 1;
  registryBlockers.clear();
  listeners.clear();
}
