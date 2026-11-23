// Hosts provide named artwork through a static build alias.
let warnedMissingHostAlias = false;

export function iconResourceHref(name: string): string | undefined {
  // Plain web bundlers may not define __DEV__; treat it as production there.
  if (typeof __DEV__ !== 'undefined' && __DEV__ && !warnedMissingHostAlias) {
    warnedMissingHostAlias = true;
    console.warn(
      `[OneKeyIcon] "#onekey-icon-assets" resolved to the SDK fallback, so named icons such as "${name}" render blank. Alias it to the host's generated icon table in the web build.`
    );
  }
  return undefined;
}
