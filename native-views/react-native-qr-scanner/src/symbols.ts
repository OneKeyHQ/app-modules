export type QrSymbolCoordinateSpace = 'view' | 'frame';

export interface QrSymbolBounds {
  x: number;
  y: number;
  width: number;
  height: number;
}

export interface QrDetectedSymbol {
  /** Decoded raw QR payload text. */
  text: string;
  /** Normalized center X in [0, 1], top-left origin. */
  centerX: number;
  /** Normalized center Y in [0, 1], top-left origin. */
  centerY: number;
  /** Optional normalized bounding box in [0, 1], top-left origin. */
  bounds?: QrSymbolBounds;
}

export interface QrFrameSymbolsEvent {
  symbols: QrDetectedSymbol[];
  /**
   * 'view'  : coordinates already map to visible preview bounds (iOS/Android).
   * 'frame' : coordinates map to raw frame pixels (Web/Desktop; map through object-fit: cover).
   */
  coordinateSpace: QrSymbolCoordinateSpace;
  frameWidth?: number;
  frameHeight?: number;
  generation: number;
}

export interface QrImageSymbolsResult {
  /** EXIF-oriented decoded image dimensions. */
  width: number;
  height: number;
  symbols: QrDetectedSymbol[];
}

export interface NormalizedPoint {
  x: number;
  y: number;
}

export interface Dimensions2D {
  width: number;
  height: number;
}

const UR_PREFIX = /^ur:/i;

/** Returns true when frame text uses the BC-UR scheme (`ur:...`). */
export function isUrFrameText(text: string): boolean {
  return UR_PREFIX.test(text.trim());
}

/**
 * Maps a normalized point in raw frame space (`[0, 1]`, top-left origin) into
 * visible container space (`[0, 1]`, top-left origin) under `object-fit: cover`.
 */
export function mapFramePointToCoverView(
  point: NormalizedPoint,
  frame: Dimensions2D,
  view: Dimensions2D,
  mirrored = false
): NormalizedPoint {
  if (
    frame.width <= 0 ||
    frame.height <= 0 ||
    view.width <= 0 ||
    view.height <= 0
  ) {
    return { x: mirrored ? 1 - point.x : point.x, y: point.y };
  }
  const scale = Math.max(view.width / frame.width, view.height / frame.height);
  const renderedWidth = frame.width * scale;
  const renderedHeight = frame.height * scale;
  const offsetX = (renderedWidth - view.width) / 2;
  const offsetY = (renderedHeight - view.height) / 2;

  const rawX = (point.x * renderedWidth - offsetX) / view.width;
  const rawY = (point.y * renderedHeight - offsetY) / view.height;
  return {
    x: mirrored ? 1 - rawX : rawX,
    y: rawY,
  };
}

/**
 * Maps a normalized point in image space (`[0, 1]`, top-left origin) into
 * container space (`[0, 1]`, top-left origin) under `object-fit: contain`.
 */
export function mapImagePointToContainView(
  point: NormalizedPoint,
  image: Dimensions2D,
  view: Dimensions2D
): NormalizedPoint {
  if (
    image.width <= 0 ||
    image.height <= 0 ||
    view.width <= 0 ||
    view.height <= 0
  ) {
    return { x: point.x, y: point.y };
  }
  const scale = Math.min(view.width / image.width, view.height / image.height);
  const renderedWidth = image.width * scale;
  const renderedHeight = image.height * scale;
  const offsetX = (view.width - renderedWidth) / 2;
  const offsetY = (view.height - renderedHeight) / 2;

  return {
    x: (offsetX + point.x * renderedWidth) / view.width,
    y: (offsetY + point.y * renderedHeight) / view.height,
  };
}

/**
 * Deduplicates symbols by trimmed text, keeping the occurrence closest to the
 * viewport center `(0.5, 0.5)`.
 */
export function dedupeSymbolsByText(
  symbols: readonly QrDetectedSymbol[]
): QrDetectedSymbol[] {
  const byText = new Map<string, QrDetectedSymbol>();
  for (const symbol of symbols) {
    const key = symbol.text;
    if (!key) continue;
    const existing = byText.get(key);
    if (!existing) {
      byText.set(key, symbol);
      continue;
    }
    const existingDist = Math.hypot(
      existing.centerX - 0.5,
      existing.centerY - 0.5
    );
    const nextDist = Math.hypot(symbol.centerX - 0.5, symbol.centerY - 0.5);
    if (nextDist < existingDist) {
      byText.set(key, symbol);
    }
  }
  return Array.from(byText.values());
}

export type MultiSymbolPhase =
  | 'normal'
  | 'aggregating'
  | 'selecting'
  | 'ur-streaming'
  | 'resolved';

export interface MultiSymbolCoordinatorOptions {
  /** Short anti-race window after a single non-UR symbol (default 140 ms). */
  aggregationWindowMs?: number;
  /** Minimum normalized center distance to merge cross-frame symbols (default 0.08). */
  minSpatialSeparation?: number;
  onFrozenChange: (frozen: boolean) => void;
  onMultiSymbolChange: (enabled: boolean) => void;
  onSelectingChange: (symbols: QrDetectedSymbol[]) => void;
  onCommitText: (text: string, mode: 'static' | 'ur') => void;
}

export interface MultiSymbolCoordinator {
  readonly phase: MultiSymbolPhase;
  receiveFrame(symbols: readonly QrDetectedSymbol[]): void;
  selectSymbol(symbol: QrDetectedSymbol): void;
  cancelSelection(): void;
  reset(): void;
  dispose(): void;
}

const MULTIPART_UR_PATTERN = /^ur:([a-z0-9-]+)\/(\d+)-(\d+)\/([a-z]+)$/i;

function extractMultipartUrKey(text: string): string | null {
  const match = MULTIPART_UR_PATTERN.exec(text.trim());
  if (!match) return null;
  const [, type, seqNumStr, seqLenStr] = match;
  const seqNum = Number(seqNumStr);
  const seqLen = Number(seqLenStr);
  if (!type || !Number.isInteger(seqNum) || !Number.isInteger(seqLen) || seqNum < 1 || seqLen < 1) {
    return null;
  }
  return `${type.toLowerCase()}/${seqLen}`;
}

/**
 * Cross-platform state machine balancing multi-QR selection with animated
 * BC-UR streaming (SPEC section 3.4).
 */
export function createMultiSymbolCoordinator(
  options: MultiSymbolCoordinatorOptions
): MultiSymbolCoordinator {
  const aggregationWindowMs = options.aggregationWindowMs ?? 140;
  const minSpatialSeparation = options.minSpatialSeparation ?? 0.08;

  let phase: MultiSymbolPhase = 'normal';
  let pendingSymbol: QrDetectedSymbol | undefined;
  let selectedUrKey: string | null = null;
  let timer: ReturnType<typeof setTimeout> | undefined;

  function clearTimer() {
    if (timer !== undefined) {
      clearTimeout(timer);
      timer = undefined;
    }
  }

  function enterSelecting(symbols: QrDetectedSymbol[]) {
    clearTimer();
    pendingSymbol = undefined;
    selectedUrKey = null;
    phase = 'selecting';
    options.onFrozenChange(true);
    options.onSelectingChange(symbols);
  }

  function enterUrStreaming(firstPart: string, keepMultiSymbol: boolean) {
    clearTimer();
    pendingSymbol = undefined;
    selectedUrKey = extractMultipartUrKey(firstPart);
    phase = 'ur-streaming';
    options.onSelectingChange([]);
    options.onFrozenChange(false);
    options.onMultiSymbolChange(keepMultiSymbol);
    options.onCommitText(firstPart, 'ur');
  }

  function commitStatic(text: string) {
    clearTimer();
    pendingSymbol = undefined;
    selectedUrKey = null;
    phase = 'resolved';
    options.onSelectingChange([]);
    options.onCommitText(text, 'static');
  }

  function commitSingleOrInvalidUr(text: string) {
    clearTimer();
    pendingSymbol = undefined;
    selectedUrKey = null;
    phase = 'resolved';
    options.onSelectingChange([]);
    options.onCommitText(text, 'ur');
  }

  function isVisible(symbol: QrDetectedSymbol): boolean {
    return (
      symbol.centerX >= 0 &&
      symbol.centerX <= 1 &&
      symbol.centerY >= 0 &&
      symbol.centerY <= 1
    );
  }

  return {
    get phase() {
      return phase;
    },
    receiveFrame(rawSymbols: readonly QrDetectedSymbol[]) {
      if (phase === 'selecting' || phase === 'resolved') return;

      const visible = dedupeSymbolsByText(rawSymbols.filter(isVisible));
      if (visible.length === 0) return;

      if (phase === 'ur-streaming') {
        const urSymbols = visible.filter((item) => {
          if (!isUrFrameText(item.text)) return false;
          if (selectedUrKey === null) return true;
          return extractMultipartUrKey(item.text) === selectedUrKey;
        });
        if (urSymbols.length > 0) {
          // Forward matching UR candidates so the bound UrAssembler/ScanSession
          // receives fragments belonging to the selected message even when another
          // UR appears earlier in the frame's symbol array.
          for (const ur of urSymbols) {
            options.onCommitText(ur.text, 'ur');
          }
        } else {
          // A simpler competing static QR can win maxNumberOfSymbols=1 and starve
          // the animated UR; re-enable multiSymbol so the UR stays visible.
          options.onMultiSymbolChange(true);
        }
        return;
      }

      if (visible.length >= 2) {
        enterSelecting(visible);
        return;
      }

      const [single] = visible;
      if (!single) return;

      if (isUrFrameText(single.text)) {
        enterUrStreaming(single.text, false);
        return;
      }

      if (phase === 'aggregating' && pendingSymbol) {
        if (single.text !== pendingSymbol.text) {
          const distance = Math.hypot(
            single.centerX - pendingSymbol.centerX,
            single.centerY - pendingSymbol.centerY
          );
          if (distance > minSpatialSeparation) {
            enterSelecting([pendingSymbol, single]);
            return;
          }
        }
        return;
      }

      if (aggregationWindowMs <= 0) {
        commitStatic(single.text);
        return;
      }

      phase = 'aggregating';
      pendingSymbol = single;
      clearTimer();
      timer = setTimeout(() => {
        timer = undefined;
        if (phase === 'aggregating' && pendingSymbol) {
          commitStatic(pendingSymbol.text);
        }
      }, aggregationWindowMs);
    },
    selectSymbol(symbol: QrDetectedSymbol) {
      if (phase !== 'selecting') return;
      if (isUrFrameText(symbol.text)) {
        if (extractMultipartUrKey(symbol.text) === null) {
          // Single-part or malformed UR: commit once and do not stream other
          // unselected URs from subsequent frames.
          commitSingleOrInvalidUr(symbol.text);
          return;
        }
        // Competing static symbols are already in view; keep multiSymbol on so
        // an easier static QR cannot starve the denser animated UR frames.
        enterUrStreaming(symbol.text, true);
      } else {
        commitStatic(symbol.text);
      }
    },
    cancelSelection() {
      if (phase !== 'selecting') return;
      clearTimer();
      pendingSymbol = undefined;
      selectedUrKey = null;
      phase = 'normal';
      options.onSelectingChange([]);
      options.onFrozenChange(false);
    },
    reset() {
      clearTimer();
      pendingSymbol = undefined;
      selectedUrKey = null;
      phase = 'normal';
      options.onSelectingChange([]);
      options.onFrozenChange(false);
      options.onMultiSymbolChange(true);
    },
    dispose() {
      clearTimer();
      pendingSymbol = undefined;
      selectedUrKey = null;
    },
  };
}
