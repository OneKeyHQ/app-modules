# @onekeyfe/react-native-native-sheet

A native iOS and Android bottom-sheet host for arbitrary React Native content.
The platform owns presentation, stacking, gestures, and transitions; React
Native owns the sheet body. No action-row or icon schema is required.

## Behavior

- iOS uses `UISheetPresentationController` with a custom detent.
- Android uses `BottomSheetDialog` and `BottomSheetBehavior`.
- Multiple sheets form a native presentation stack. Closing a lower sheet also
  closes its descendants in top-to-bottom order.
- Explicit `height` changes and settled auto-content measurements animate the
  native surface with its bottom edge fixed. Growth moves the top edge upward;
  shrinkage moves it downward. Both directions remain clamped by `maxHeight`.
- `NativeSheetSecurityProvider` force-dismisses descendant sheets without an
  animation while the app lock is active, so native windows never cover the
  security surface.

## Usage

```tsx
import {
  NativeSheet,
  NativeSheetHost,
  NativeSheetSecurityProvider,
} from '@onekeyfe/react-native-native-sheet';

function AppRoot({ isLocked, children }) {
  return (
    <NativeSheetSecurityProvider blocked={isLocked}>
      {children}
      <NativeSheetHost />
    </NativeSheetSecurityProvider>
  );
}

function RenameSheet({ open, onOpenChange }) {
  return (
    <NativeSheet
      open={open}
      onOpenChange={onOpenChange}
      height={360}
      backgroundColor="#fff"
      dismissOnOverlayPress
    >
      {/* Any React Native subtree: form fields, lists, QR codes, WebViews, etc. */}
      <RenameForm />
    </NativeSheet>
  );
}

function showScannerSheet() {
  const handle = NativeSheet.show({
    height: 480,
    dismissOnOverlayPress: true,
    renderContent: ({ close }) => <Scanner onDone={close} />,
  });
  return handle;
}
```

The caller owns `open` and should update it from `onOpenChange`. The interaction
names match the existing React Native Sheet API: `dismissOnOverlayPress` controls
overlay taps and `dismissOnSnapToBottom` controls drag dismissal. The legacy
`dismissOnBackdropPress` and `dismissOnPanDown` names remain as aliases.

The native backdrop fades with the platform presentation transition. Keyboard
avoidance is owned by `UISheetPresentationController` on iOS and the dialog
window's resize mode on Android; callers should not translate the sheet in JS.
Update `height` after asynchronously loaded content settles when the outer
surface needs more room. Keep scrolling content inside the current height when
the outer surface should remain fixed.

Mount one `NativeSheetHost` inside `NativeSheetSecurityProvider` at the app
root before calling `NativeSheet.show()`. The imperative call returns a handle
with `close()` and keeps its React subtree mounted until the native exit
transition completes. `onOpenChange`, `onDismiss`, and
`onAnimationComplete({ open })` are each delivered once per transition.

NativeSheet intentionally does not accept JavaScript-only Sheet props such as
`transition`, `transitionConfig`, `portalProps`, `zIndex`, or scroll-lock
options. Presentation order, transitions, overlay level, and keyboard avoidance
are platform-owned. Use `height` for a caller-controlled animated detent, or
omit it to keep measuring intrinsic React content while the sheet is open.

## License

MIT
