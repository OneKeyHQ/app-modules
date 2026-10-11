import { useSyncExternalStore } from 'react';

export type NativeListFontFamilies = Readonly<{
  regular?: string;
  medium?: string;
  semibold?: string;
  bold?: string;
}>;

const weights = ['regular', 'medium', 'semibold', 'bold'] as const;
let configuration = '{}';
const listeners = new Set<() => void>();

/** Configure names already provided by the host in this JavaScript runtime. */
export function configureNativeListFonts(
  families: NativeListFontFamilies
): void {
  if (!families || typeof families !== 'object' || Array.isArray(families)) {
    throw new TypeError('NativeList font families must be an object');
  }
  if (
    Object.keys(families).some(
      (key) => !weights.includes(key as (typeof weights)[number])
    )
  ) {
    throw new TypeError('Unknown NativeList font weight');
  }
  const normalized: Record<string, string> = {};
  for (const weight of weights) {
    const name = families[weight];
    if (name === undefined) continue;
    if (
      typeof name !== 'string' ||
      Array.from(name).some(
        (character) =>
          character.charCodeAt(0) < 32 || character.charCodeAt(0) === 127
      )
    ) {
      throw new TypeError(`Invalid NativeList ${weight} font family`);
    }
    const trimmed = name.trim();
    if (trimmed.length === 0 || trimmed.length > 128) {
      throw new TypeError(`Invalid NativeList ${weight} font family`);
    }
    normalized[weight] = trimmed;
  }
  const next = JSON.stringify(normalized);
  if (next === configuration) return;
  configuration = next;
  listeners.forEach((listener) => listener());
}

function subscribe(listener: () => void): () => void {
  listeners.add(listener);
  return () => listeners.delete(listener);
}

function getSnapshot(): string {
  return configuration;
}

export function useNativeListFontFamiliesJson(): string {
  return useSyncExternalStore(subscribe, getSnapshot, getSnapshot);
}
