# @onekeyfe/react-native-native-sheet

A native iOS and Android bottom-sheet host for arbitrary React Native content.
The platform owns presentation, stacking, gestures, and transitions; React
Native owns the sheet body. No action-row or icon schema is required.

## Behavior

- iOS uses `UISheetPresentationController` with a fixed custom detent.
- Android uses `BottomSheetDialog` and `BottomSheetBehavior`.
- Multiple sheets form a native presentation stack. Closing a lower sheet also
  closes its descendants in top-to-bottom order.
- `height` is captured when a sheet opens. Async child updates reflow inside the
  existing surface and do not resize the outer sheet.
- `NativeSheetSecurityProvider` force-dismisses descendant sheets without an
  animation while the app lock is active, so native windows never cover the
  security surface.

## Usage

```tsx
import {
  NativeSheet,
  NativeSheetSecurityProvider,
} from '@onekeyfe/react-native-native-sheet';

function AppRoot({ isLocked, children }) {
  return (
    <NativeSheetSecurityProvider blocked={isLocked}>
      {children}
    </NativeSheetSecurityProvider>
  );
}

function RenameSheet({ open, onClose }) {
  return (
    <NativeSheet
      open={open}
      height={360}
      backgroundColor="#fff"
      onDismiss={onClose}
    >
      {/* Any React Native subtree: form fields, lists, QR codes, WebViews, etc. */}
      <RenameForm />
    </NativeSheet>
  );
}
```

The caller owns `open` and must set it to `false` from `onDismiss`. Put scrolling
or asynchronously loaded content inside a fixed-height layout so it can update
without changing the native detent.

## License

MIT
