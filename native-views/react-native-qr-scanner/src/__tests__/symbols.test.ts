import {
  createMultiSymbolCoordinator,
  dedupeSymbolsByText,
  isUrFrameText,
  mapFramePointToCoverView,
  mapImagePointToContainView,
} from '../symbols';

describe('symbols coordinate mapping and deduplication', () => {
  test('mapFramePointToCoverView accounts for cover horizontal crop and mirroring', () => {
    // 16:9 landscape frame (1600x900) inside a 400x400 square viewport:
    // scale = 400/900 = 0.4444, renderedWidth = 711.11, offsetX = 155.56
    const center = mapFramePointToCoverView(
      { x: 0.5, y: 0.5 },
      { width: 1600, height: 900 },
      { width: 400, height: 400 }
    );
    expect(center.x).toBeCloseTo(0.5, 4);
    expect(center.y).toBeCloseTo(0.5, 4);

    // Point near left cropped edge (x = 0.1) falls outside [0, 1]
    const cropped = mapFramePointToCoverView(
      { x: 0.1, y: 0.5 },
      { width: 1600, height: 900 },
      { width: 400, height: 400 }
    );
    expect(cropped.x).toBeLessThan(0);

    // Mirrored front camera flips X around 0.5
    const normal = mapFramePointToCoverView(
      { x: 0.65, y: 0.25 },
      { width: 1280, height: 720 },
      { width: 400, height: 400 },
      false
    );
    const mirrored = mapFramePointToCoverView(
      { x: 0.65, y: 0.25 },
      { width: 1280, height: 720 },
      { width: 400, height: 400 },
      true
    );
    expect(mirrored.x).toBeCloseTo(1 - normal.x, 4);
    expect(mirrored.y).toBeCloseTo(normal.y, 4);
  });

  test('mapImagePointToContainView accounts for letterbox/pillarbox offset', () => {
    // 2:1 wide image (800x400) inside a 400x400 square viewport:
    // rendered 400x200, offsetY = 100
    const topEdge = mapImagePointToContainView(
      { x: 0.5, y: 0 },
      { width: 800, height: 400 },
      { width: 400, height: 400 }
    );
    expect(topEdge.x).toBeCloseTo(0.5, 4);
    expect(topEdge.y).toBeCloseTo(0.25, 4);
  });

  test('dedupeSymbolsByText keeps the occurrence closest to center', () => {
    const deduped = dedupeSymbolsByText([
      { text: 'dup', centerX: 0.1, centerY: 0.1 },
      { text: 'dup', centerX: 0.48, centerY: 0.52 },
      { text: 'other', centerX: 0.8, centerY: 0.8 },
    ]);
    expect(deduped).toEqual([
      { text: 'dup', centerX: 0.48, centerY: 0.52 },
      { text: 'other', centerX: 0.8, centerY: 0.8 },
    ]);
  });

  test('isUrFrameText matches ur: case-insensitively', () => {
    expect(isUrFrameText('ur:crypto-psbt/1-17/lpadao')).toBe(true);
    expect(isUrFrameText('  UR:BYTES/1-2/LPADAO')).toBe(true);
    expect(isUrFrameText('ethereum:0x123')).toBe(false);
  });
});

describe('createMultiSymbolCoordinator', () => {
  beforeEach(() => {
    jest.useFakeTimers();
  });

  afterEach(() => {
    jest.useRealTimers();
  });

  function setup(aggregationWindowMs = 140) {
    const onFrozenChange = jest.fn();
    const onMultiSymbolChange = jest.fn();
    const onSelectingChange = jest.fn();
    const onCommitText = jest.fn((_text: string, mode: 'static' | 'ur') =>
      mode === 'ur' ? ({ status: 'progress' as const }) : undefined
    );
    const coordinator = createMultiSymbolCoordinator({
      aggregationWindowMs,
      onFrozenChange,
      onMultiSymbolChange,
      onSelectingChange,
      onCommitText,
    });
    return {
      coordinator,
      onFrozenChange,
      onMultiSymbolChange,
      onSelectingChange,
      onCommitText,
    };
  }

  test('same-frame >=2 visible symbols freeze immediately and allow selection or cancel', () => {
    const { coordinator, onFrozenChange, onSelectingChange, onCommitText } = setup();
    const a = { text: 'wc:abc', centerX: 0.25, centerY: 0.3 };
    const b = { text: 'ethereum:0x1', centerX: 0.75, centerY: 0.7 };

    coordinator.receiveFrame([a, b]);
    expect(coordinator.phase).toBe('selecting');
    expect(onFrozenChange).toHaveBeenCalledWith(true);
    expect(onSelectingChange).toHaveBeenCalledWith([a, b]);

    coordinator.cancelSelection();
    expect(coordinator.phase).toBe('normal');
    expect(onFrozenChange).toHaveBeenLastCalledWith(false);
    expect(onSelectingChange).toHaveBeenLastCalledWith([]);

    coordinator.receiveFrame([a, b]);
    coordinator.selectSymbol(b);
    expect(coordinator.phase).toBe('resolved');
    expect(onCommitText).toHaveBeenCalledWith('ethereum:0x1', 'static');
  });

  test('140ms aggregation window avoids cross-frame ghost markers and ignores same-spot changes', () => {
    const { coordinator, onFrozenChange, onCommitText } = setup(140);
    coordinator.receiveFrame([{ text: 'frame-1', centerX: 0.5, centerY: 0.5 }]);
    expect(coordinator.phase).toBe('aggregating');

    // Same-spot dynamic change (distance 0.01 <= 0.08) must NOT freeze
    coordinator.receiveFrame([{ text: 'frame-2', centerX: 0.51, centerY: 0.5 }]);
    expect(coordinator.phase).toBe('aggregating');
    expect(onFrozenChange).not.toHaveBeenCalled();

    // Window expires -> commits the single code
    jest.advanceTimersByTime(140);
    expect(coordinator.phase).toBe('resolved');
    expect(onCommitText).toHaveBeenCalledWith('frame-1', 'static');

    // Spatially separated single code in a later frame does NOT freeze with
    // the older frame's marker; it tracks the newer visible code until either
    // both appear in the same frame or the window settles on the new code.
    coordinator.reset();
    coordinator.receiveFrame([{ text: 'left', centerX: 0.2, centerY: 0.5 }]);
    coordinator.receiveFrame([{ text: 'right', centerX: 0.8, centerY: 0.5 }]);
    expect(coordinator.phase).toBe('aggregating');
    jest.advanceTimersByTime(140);
    expect(coordinator.phase).toBe('resolved');
    expect(onCommitText).toHaveBeenLastCalledWith('right', 'static');
  });

  test('single ur: code latches into ur-streaming immediately with 0ms delay and disables multiSymbol', () => {
    const {
      coordinator,
      onFrozenChange,
      onMultiSymbolChange,
      onCommitText,
    } = setup(140);

    coordinator.receiveFrame([
      { text: 'UR:CRYPTO-PSBT/1-17/LPAD', centerX: 0.5, centerY: 0.5 },
    ]);
    expect(coordinator.phase).toBe('ur-streaming');
    expect(onMultiSymbolChange).toHaveBeenCalledWith(false);
    expect(onFrozenChange).toHaveBeenCalledWith(false);
    expect(onCommitText).toHaveBeenCalledWith('UR:CRYPTO-PSBT/1-17/LPAD', 'ur');

    // Stray static codes during ur-streaming are ignored and auto-heal
    // multiSymbol=true so a simpler static QR cannot starve the UR stream.
    coordinator.receiveFrame([
      { text: 'ethereum:0xstray', centerX: 0.8, centerY: 0.8 },
    ]);
    expect(onCommitText).toHaveBeenCalledTimes(1);
    expect(onMultiSymbolChange).toHaveBeenLastCalledWith(true);

    coordinator.receiveFrame([
      { text: 'ethereum:0xstray', centerX: 0.8, centerY: 0.8 },
      { text: 'UR:CRYPTO-PSBT/2-17/LPAE', centerX: 0.3, centerY: 0.5 },
    ]);
    expect(coordinator.phase).toBe('ur-streaming');
    expect(onCommitText).toHaveBeenLastCalledWith('UR:CRYPTO-PSBT/2-17/LPAE', 'ur');
  });

  test('mixed ur: + static in same frame freezes; selecting ur: unfreezes and keeps multiSymbol=true', () => {
    const {
      coordinator,
      onFrozenChange,
      onMultiSymbolChange,
      onCommitText,
    } = setup(140);
    const ur = { text: 'UR:CRYPTO-PSBT/1-17/LPAD', centerX: 0.28, centerY: 0.5 };
    const eth = { text: 'ethereum:0x1', centerX: 0.76, centerY: 0.26 };

    coordinator.receiveFrame([ur, eth]);
    expect(coordinator.phase).toBe('selecting');
    expect(onFrozenChange).toHaveBeenCalledWith(true);

    coordinator.selectSymbol(ur);
    expect(coordinator.phase).toBe('ur-streaming');
    expect(onFrozenChange).toHaveBeenLastCalledWith(false);
    expect(onMultiSymbolChange).toHaveBeenLastCalledWith(true);
    expect(onCommitText).toHaveBeenCalledWith(ur.text, 'ur');
  });

  test('two competing animated URs in one frame: selected second UR still receives all its parts', () => {
    const { coordinator, onCommitText } = setup(140);
    const urA1 = { text: 'UR:BYTES/1-3/LPADAO', centerX: 0.25, centerY: 0.5 };
    const urB1 = { text: 'UR:CRYPTO-PSBT/1-2/LPADAO', centerX: 0.75, centerY: 0.5 };

    coordinator.receiveFrame([urA1, urB1]);
    expect(coordinator.phase).toBe('selecting');

    coordinator.selectSymbol(urB1);
    expect(coordinator.phase).toBe('ur-streaming');
    expect(onCommitText).toHaveBeenCalledWith(urB1.text, 'ur');

    // Subsequent frame still lists competing UR A first and selected UR B second;
    // only UR B (matching type/seqLen) is forwarded.
    const urA2 = { text: 'UR:BYTES/2-3/LPAEAO', centerX: 0.25, centerY: 0.5 };
    const urB2 = { text: 'UR:CRYPTO-PSBT/2-2/LPAEAO', centerX: 0.75, centerY: 0.5 };
    coordinator.receiveFrame([urA2, urB2]);
    expect(onCommitText).not.toHaveBeenCalledWith(urA2.text, 'ur');
    expect(onCommitText).toHaveBeenCalledWith(urB2.text, 'ur');
  });

  test('selecting a malformed UR whose session rejects it keeps selection open', () => {
    const onFrozenChange = jest.fn();
    const onMultiSymbolChange = jest.fn();
    const onSelectingChange = jest.fn();
    const onCommitText = jest.fn(() => ({ status: 'ignored' as const, reason: 'malformed' }));
    const coordinator = createMultiSymbolCoordinator({
      aggregationWindowMs: 140,
      onFrozenChange,
      onMultiSymbolChange,
      onSelectingChange,
      onCommitText,
    });

    const badUr = { text: 'UR:BYTES/1-2/LPADAO', centerX: 0.25, centerY: 0.5 };
    const otherUr = { text: 'UR:BYTES/1-2/LPAEAO', centerX: 0.75, centerY: 0.5 };
    coordinator.receiveFrame([badUr, otherUr]);
    expect(coordinator.phase).toBe('selecting');

    coordinator.selectSymbol(badUr);
    // Must stay in selecting/frozen so a different UR cannot bind on the next frame
    expect(coordinator.phase).toBe('selecting');
    expect(onFrozenChange).toHaveBeenLastCalledWith(true);
    expect(onCommitText).toHaveBeenCalledTimes(1);

    coordinator.receiveFrame([badUr, otherUr]);
    expect(onCommitText).toHaveBeenCalledTimes(1);
  });

  test('foreign UR in single-symbol ur-streaming triggers multiSymbol recovery', () => {
    const onFrozenChange = jest.fn();
    const onMultiSymbolChange = jest.fn();
    const onSelectingChange = jest.fn();
    const onCommitText = jest.fn((text: string) => {
      if (text.includes('CRYPTO-PSBT')) return { status: 'progress' as const };
      return { status: 'ignored' as const, reason: 'foreign' };
    });
    const coordinator = createMultiSymbolCoordinator({
      aggregationWindowMs: 140,
      onFrozenChange,
      onMultiSymbolChange,
      onSelectingChange,
      onCommitText,
    });

    coordinator.receiveFrame([
      { text: 'UR:CRYPTO-PSBT/1-3/LPADAO', centerX: 0.5, centerY: 0.5 },
    ]);
    expect(coordinator.phase).toBe('ur-streaming');
    expect(onMultiSymbolChange).toHaveBeenLastCalledWith(false);

    // Single-symbol detector later returns a duplicate of the bound UR -> stays single-symbol
    onCommitText.mockReturnValueOnce({ status: 'ignored', reason: 'duplicate' });
    coordinator.receiveFrame([
      { text: 'UR:CRYPTO-PSBT/1-3/LPADAO', centerX: 0.5, centerY: 0.5 },
    ]);
    expect(onMultiSymbolChange).toHaveBeenLastCalledWith(false);

    // Single-symbol detector later returns a foreign UR instead -> recovers multiSymbol=true
    coordinator.receiveFrame([
      { text: 'UR:BYTES/1-2/LPADAO', centerX: 0.5, centerY: 0.5 },
    ]);
    expect(onMultiSymbolChange).toHaveBeenLastCalledWith(true);
  });

  test('terminal UR rejection ends attempt and ignored automatic UR preserves pending static scan', () => {
    const onFrozenChange = jest.fn();
    const onMultiSymbolChange = jest.fn();
    const onSelectingChange = jest.fn();
    const onCommitText = jest.fn((_text: string, mode: 'static' | 'ur') =>
      mode === 'ur' ? ({ status: 'ignored' as const, reason: 'malformed' }) : undefined
    );
    const coordinator = createMultiSymbolCoordinator({
      aggregationWindowMs: 140,
      onFrozenChange,
      onMultiSymbolChange,
      onSelectingChange,
      onCommitText,
    });

    // Pending static scan stays intact if an ignored automatic UR briefly appears
    coordinator.receiveFrame([{ text: 'ethereum:0xabc', centerX: 0.5, centerY: 0.5 }]);
    expect(coordinator.phase).toBe('aggregating');
    coordinator.receiveFrame([{ text: 'UR:BYTES/1-2/LPADAO', centerX: 0.5, centerY: 0.5 }]);
    expect(coordinator.phase).toBe('aggregating');
    jest.advanceTimersByTime(140);
    expect(coordinator.phase).toBe('resolved');
    expect(onCommitText).toHaveBeenLastCalledWith('ethereum:0xabc', 'static');

    // Terminal UR rejection during selection closes selection and ends the attempt
    coordinator.reset();
    onCommitText.mockReturnValueOnce({ status: 'rejected' as const, reason: 'too-large' } as never);
    const badUr = { text: 'UR:CRYPTO-PSBT/1-999/LPADAO', centerX: 0.25, centerY: 0.5 };
    const eth = { text: 'ethereum:0xdef', centerX: 0.75, centerY: 0.5 };
    coordinator.receiveFrame([badUr, eth]);
    expect(coordinator.phase).toBe('selecting');
    coordinator.selectSymbol(badUr);
    expect(coordinator.phase).toBe('resolved');
    expect(onSelectingChange).toHaveBeenLastCalledWith([]);
  });
});
