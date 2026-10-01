import { useEffect, useRef } from 'react';

import { useSuspendedPageOwners } from './useSuspendedPageOwners';
import { registerOverlayPageHost, syncOverlayInert } from './web/overlayLayers';

const OWNER_ATTRIBUTE = 'data-overlay-owner';

/**
 * Web page host: an absolutely positioned layer inside the root-route card.
 * Page overlays portal into it; entries of covered owners are hidden here
 * because their own (possibly inactive) page does not re-render.
 */
export function OverlayPageHost({ hostKey }: { hostKey: string }) {
  const ref = useRef<HTMLDivElement>(null);
  const suspendedOwners = useSuspendedPageOwners(hostKey);

  useEffect(() => {
    const element = ref.current;
    if (!element) {
      return;
    }
    return registerOverlayPageHost(hostKey, element);
  }, [hostKey]);

  useEffect(() => {
    const element = ref.current;
    if (!element) {
      return;
    }
    const suspended = new Set(suspendedOwners);
    const applySuspension = () => {
      element
        .querySelectorAll<HTMLElement>(`[${OWNER_ATTRIBUTE}]`)
        .forEach((node) => {
          const hidden =
            suspended.has(node.getAttribute(OWNER_ATTRIBUTE) ?? '') ||
            node.getAttribute('aria-hidden') === 'true';
          node.style.visibility = hidden ? 'hidden' : '';
        });
      // Combine suspension with blocking-level isolation; resuming an owner
      // must not make an entry below a blocker interactive.
      syncOverlayInert();
    };
    applySuspension();
    const observer = new MutationObserver(applySuspension);
    observer.observe(element, {
      childList: true,
      subtree: true,
      attributes: true,
      attributeFilter: [OWNER_ATTRIBUTE, 'aria-hidden'],
    });
    return () => observer.disconnect();
  }, [hostKey, suspendedOwners]);

  return (
    <div
      ref={ref}
      style={{
        position: 'absolute',
        inset: 0,
        pointerEvents: 'none',
        zIndex: 1,
      }}
    />
  );
}

export { OWNER_ATTRIBUTE as OVERLAY_OWNER_ATTRIBUTE };
