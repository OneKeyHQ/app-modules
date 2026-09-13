import {
  addNativeSheetRegistryEntry,
  closeNativeSheetRegistryEntry,
  finishAllNativeSheetRegistryEntries,
  finishNativeSheetRegistryEntry,
  getNativeSheetRegistrySnapshot,
  markNativeSheetRegistryEntryPresentationRequested,
  resetNativeSheetRegistryForTests,
  setNativeSheetRegistryBlocked,
} from '../NativeSheetRegistry';

describe('NativeSheet imperative registry', () => {
  beforeEach(() => resetNativeSheetRegistryForTests());

  test('closes before native presentation and destroys exactly once', () => {
    const onAnimationComplete = jest.fn();
    const onOpenChange = jest.fn();
    const onDismiss = jest.fn();
    const id = addNativeSheetRegistryEntry(
      {
        renderContent: null,
        onAnimationComplete,
        onOpenChange,
        onDismiss,
      },
      null
    );

    closeNativeSheetRegistryEntry(id);
    closeNativeSheetRegistryEntry(id);
    finishNativeSheetRegistryEntry(id, 'programmatic');
    finishNativeSheetRegistryEntry(id, 'programmatic');

    expect(onOpenChange.mock.calls).toEqual([[true], [false]]);
    expect(onDismiss).toHaveBeenCalledTimes(1);
    expect(onDismiss).toHaveBeenCalledWith('programmatic');
    expect(onAnimationComplete).toHaveBeenCalledWith({ open: false });
    expect(getNativeSheetRegistrySnapshot()).toEqual([]);
  });

  test('keeps a requested presentation mounted until native dismissal', () => {
    const onDismiss = jest.fn();
    const id = addNativeSheetRegistryEntry(
      { renderContent: null, onDismiss },
      null
    );
    markNativeSheetRegistryEntryPresentationRequested(id);

    closeNativeSheetRegistryEntry(id);

    expect(getNativeSheetRegistrySnapshot()).toHaveLength(1);
    expect(getNativeSheetRegistrySnapshot()[0]?.open).toBe(false);
    expect(onDismiss).not.toHaveBeenCalled();

    finishNativeSheetRegistryEntry(id, 'programmatic');
    expect(onDismiss).toHaveBeenCalledTimes(1);
    expect(getNativeSheetRegistrySnapshot()).toEqual([]);
  });

  test('falls back when a requested native presentation never acknowledges close', () => {
    jest.useFakeTimers();
    const onAnimationComplete = jest.fn();
    const onDismiss = jest.fn();
    const id = addNativeSheetRegistryEntry(
      { renderContent: null, onAnimationComplete, onDismiss },
      null
    );
    markNativeSheetRegistryEntryPresentationRequested(id);

    closeNativeSheetRegistryEntry(id);
    jest.advanceTimersByTime(5_000);
    finishNativeSheetRegistryEntry(id, 'programmatic');

    expect(onDismiss).toHaveBeenCalledTimes(1);
    expect(onDismiss).toHaveBeenCalledWith('programmatic');
    expect(onAnimationComplete).toHaveBeenCalledTimes(1);
    expect(onAnimationComplete).toHaveBeenCalledWith({ open: false });
    expect(getNativeSheetRegistrySnapshot()).toEqual([]);
    jest.useRealTimers();
  });

  test('security fallback clears every stacked entry exactly once', () => {
    const firstDismiss = jest.fn();
    const secondDismiss = jest.fn();
    addNativeSheetRegistryEntry(
      { renderContent: null, onDismiss: firstDismiss },
      null
    );
    addNativeSheetRegistryEntry(
      { renderContent: null, onDismiss: secondDismiss },
      null
    );

    finishAllNativeSheetRegistryEntries('security');
    finishAllNativeSheetRegistryEntries('security');

    expect(firstDismiss).toHaveBeenCalledTimes(1);
    expect(secondDismiss).toHaveBeenCalledTimes(1);
    expect(getNativeSheetRegistrySnapshot()).toEqual([]);
  });

  test('does not mount a new entry while the security provider is blocked', () => {
    const onOpenChange = jest.fn();
    const onDismiss = jest.fn();
    setNativeSheetRegistryBlocked(true);

    addNativeSheetRegistryEntry(
      { renderContent: null, onOpenChange, onDismiss },
      null
    );

    expect(onOpenChange.mock.calls).toEqual([[true], [false]]);
    expect(onDismiss).toHaveBeenCalledWith('security');
    expect(getNativeSheetRegistrySnapshot()).toEqual([]);
  });
});
