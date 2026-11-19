import type {
  IOverlayToastDismissReason,
  IOverlayToastEvent,
  IOverlayToastHandle,
  IOverlayToastHandlers,
  IOverlayToastItem,
  IOverlayToastSpec,
  IOverlayToasterConfig,
} from './types';

export interface IOverlayToastSnapshot {
  /** Newest first. */
  items: readonly IOverlayToastItem[];
  config: IOverlayToasterConfig;
}

/** Toasts beyond this many are dropped oldest first. */
const TOAST_LIMIT = 20;

/**
 * The single source of truth for message toasts. Callers push data in; a
 * platform renderer (attached by `OverlayToastHost`) draws it and reports
 * what happened to each card through `handleEvent`.
 *
 * A toast leaves the list only when its renderer reports `close`, so the exit
 * animation always has a card to play on. With no renderer attached there is
 * no animation to wait for and a dismissal removes the toast at once.
 */
export class OverlayToastStore {
  private items: IOverlayToastItem[] = [];

  private config: IOverlayToasterConfig = {};

  private snapshot: IOverlayToastSnapshot = { items: [], config: {} };

  private readonly handlers = new Map<string, IOverlayToastHandlers>();

  private readonly dismissReasons = new Map<
    string,
    IOverlayToastDismissReason
  >();

  private readonly listeners = new Set<() => void>();

  private renderers = 0;

  private nextId = 0;

  private nextGeneration = 0;

  getSnapshot = (): IOverlayToastSnapshot => this.snapshot;

  subscribe = (listener: () => void): (() => void) => {
    this.listeners.add(listener);
    return () => {
      this.listeners.delete(listener);
    };
  };

  /** A renderer is drawing the list; returns its detach function. */
  attachRenderer(): () => void {
    this.renderers += 1;
    let attached = true;
    return () => {
      if (!attached) {
        return;
      }
      attached = false;
      this.renderers -= 1;
      if (this.renderers === 0) {
        // Nothing can report `close` any more: finish pending exits.
        this.items
          .filter((item) => !item.visible)
          .forEach((item) =>
            this.remove(
              item.spec.id,
              this.dismissReasons.get(item.spec.id) ?? 'programmatic'
            )
          );
      }
    };
  }

  createId(): string {
    this.nextId += 1;
    return `overlay-toast-${this.nextId}`;
  }

  show(
    spec: IOverlayToastSpec,
    handlers: IOverlayToastHandlers = {}
  ): IOverlayToastHandle {
    const { id } = spec;
    const generation = this.nextGeneration;
    this.nextGeneration += 1;
    const existing = this.items.find((item) => item.spec.id === id);
    this.handlers.set(id, handlers);
    this.dismissReasons.delete(id);
    if (existing) {
      this.items = this.items.map((item) =>
        item === existing
          ? {
              spec,
              visible: true,
              revision: item.revision + 1,
              generation,
            }
          : item
      );
    } else {
      this.items = [
        { spec, visible: true, revision: 0, generation },
        ...this.items,
      ];
      // Past the limit the oldest toasts leave without an animation, as the
      // list they came from did.
      this.items.slice(TOAST_LIMIT).forEach((item) => {
        this.handlers.delete(item.spec.id);
        this.dismissReasons.delete(item.spec.id);
      });
      this.items = this.items.slice(0, TOAST_LIMIT);
    }
    this.emit();
    return { id, dismiss: () => this.dismiss(id) };
  }

  /**
   * Redraws a toast that is on screen with a new spec (a theme or layout
   * change) without restarting its lifetime. Unknown or leaving ids are
   * ignored.
   */
  update(spec: IOverlayToastSpec): void {
    const existing = this.items.find((item) => item.spec.id === spec.id);
    if (!existing || !existing.visible) {
      return;
    }
    this.items = this.items.map((item) =>
      item === existing ? { ...item, spec, revision: item.revision + 1 } : item
    );
    this.emit();
  }

  dismiss(id: string): void {
    const item = this.items.find((entry) => entry.spec.id === id);
    if (!item || !item.visible) {
      return;
    }
    this.items = this.items.map((entry) =>
      entry === item ? { ...entry, visible: false } : entry
    );
    this.dismissReasons.set(id, 'programmatic');
    this.handlers.get(id)?.onDismiss?.('programmatic');
    if (this.renderers === 0) {
      this.remove(id, 'programmatic');
      return;
    }
    this.emit();
  }

  dismissAll(): void {
    this.items.map((item) => item.spec.id).forEach((id) => this.dismiss(id));
  }

  configure(config: IOverlayToasterConfig): void {
    const next = { ...this.config, ...config };
    const keys = Object.keys(next) as (keyof IOverlayToasterConfig)[];
    if (keys.every((key) => next[key] === this.config[key])) {
      return;
    }
    this.config = next;
    this.emit();
  }

  /** Renderer → store. */
  handleEvent(event: IOverlayToastEvent): void {
    const item = this.items.find((entry) => entry.spec.id === event.id);
    if (
      !item ||
      (event.generation !== undefined && event.generation !== item.generation)
    ) {
      return;
    }
    const handlers = this.handlers.get(event.id);
    switch (event.type) {
      case 'dismiss': {
        if (!item.visible) {
          // A caller already dismissed it and was told so.
          return;
        }
        this.items = this.items.map((entry) =>
          entry === item ? { ...entry, visible: false } : entry
        );
        this.dismissReasons.set(event.id, event.reason);
        handlers?.onDismiss?.(event.reason);
        this.emit();
        return;
      }
      case 'close':
        this.remove(event.id, event.reason, event.generation);
        return;
      case 'action':
        if (!item.visible) {
          return;
        }
        handlers?.onAction?.(event.actionId);
        return;
      case 'link':
        if (!item.visible) {
          return;
        }
        handlers?.onLink?.(event.link);
        return;
      case 'press':
        if (!item.visible) {
          return;
        }
        handlers?.onPress?.();
        return;
      default:
    }
  }

  private remove(
    id: string,
    reason: IOverlayToastDismissReason,
    generation?: number
  ): void {
    const item = this.items.find((entry) => entry.spec.id === id);
    if (!item) {
      return;
    }
    // A toast shown again under the same id while its predecessor was
    // leaving stays: the renderer's `close` belongs to the old card.
    if (
      item.visible ||
      (generation !== undefined && generation !== item.generation)
    ) {
      return;
    }
    const handlers = this.handlers.get(id);
    this.handlers.delete(id);
    this.dismissReasons.delete(id);
    this.items = this.items.filter((entry) => entry !== item);
    this.emit();
    handlers?.onClose?.(reason);
  }

  private emit(): void {
    this.snapshot = { items: this.items, config: this.config };
    this.listeners.forEach((listener) => listener());
  }
}

export const overlayToastStore = new OverlayToastStore();
