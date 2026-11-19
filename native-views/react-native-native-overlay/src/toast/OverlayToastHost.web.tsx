import { useEffect } from 'react';

import { overlayToastStore } from './OverlayToastStore';
import { createToastDomRenderer } from './toastDomRenderer.web';

/**
 * Attaches the DOM toast renderer for as long as it is mounted. It renders no
 * React output: the renderer draws straight into the `toast` overlay layer.
 */
export function OverlayToastHost() {
  useEffect(() => {
    const renderer = createToastDomRenderer((event) =>
      overlayToastStore.handleEvent(event)
    );
    const detach = overlayToastStore.attachRenderer();
    const sync = () => {
      const { items, config } = overlayToastStore.getSnapshot();
      renderer.sync(items, config);
    };
    sync();
    const unsubscribe = overlayToastStore.subscribe(sync);
    return () => {
      unsubscribe();
      renderer.destroy();
      detach();
    };
  }, []);
  return null;
}
