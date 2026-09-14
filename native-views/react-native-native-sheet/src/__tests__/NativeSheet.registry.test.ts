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
    setNativeSheetRegistryBlocked(Symbol('security-provider'), true);

    addNativeSheetRegistryEntry(
      { renderContent: null, onOpenChange, onDismiss },
      null
    );

    expect(onOpenChange.mock.calls).toEqual([[true], [false]]);
    expect(onDismiss).toHaveBeenCalledWith('security');
    expect(getNativeSheetRegistrySnapshot()).toEqual([]);
  });

  test('keeps the registry blocked until every blocking provider is cleared', () => {
    const outerProvider = Symbol('outer-provider');
    const innerProvider = Symbol('inner-provider');
    const onDismiss = jest.fn();
    setNativeSheetRegistryBlocked(outerProvider, true);
    setNativeSheetRegistryBlocked(innerProvider, false);

    addNativeSheetRegistryEntry({ renderContent: null, onDismiss }, null);
    setNativeSheetRegistryBlocked(innerProvider, true);
    setNativeSheetRegistryBlocked(innerProvider, false);
    addNativeSheetRegistryEntry({ renderContent: null, onDismiss }, null);

    expect(onDismiss).toHaveBeenCalledTimes(2);
    expect(onDismiss).toHaveBeenNthCalledWith(1, 'security');
    expect(onDismiss).toHaveBeenNthCalledWith(2, 'security');
    expect(getNativeSheetRegistrySnapshot()).toEqual([]);

    setNativeSheetRegistryBlocked(outerProvider, false);
    addNativeSheetRegistryEntry({ renderContent: null, onDismiss }, null);
    expect(getNativeSheetRegistrySnapshot()).toHaveLength(1);
  });

  test('security fallback does not dismiss entries opened after unblocking', () => {
    jest.useFakeTimers();
    const blocker = Symbol('security-provider');
    const firstDismiss = jest.fn();
    const secondDismiss = jest.fn();
    const firstId = addNativeSheetRegistryEntry(
      { renderContent: null, onDismiss: firstDismiss },
      null
    );
    markNativeSheetRegistryEntryPresentationRequested(firstId);

    setNativeSheetRegistryBlocked(blocker, true);
    finishNativeSheetRegistryEntry(firstId, 'security');
    setNativeSheetRegistryBlocked(blocker, false);
    addNativeSheetRegistryEntry(
      { renderContent: null, onDismiss: secondDismiss },
      null
    );
    jest.advanceTimersByTime(500);

    expect(firstDismiss).toHaveBeenCalledTimes(1);
    expect(secondDismiss).not.toHaveBeenCalled();
    expect(getNativeSheetRegistrySnapshot()).toHaveLength(1);
    jest.useRealTimers();
  });
});
