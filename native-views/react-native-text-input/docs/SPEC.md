# React Native Text Input behavioral contract

Status: Implemented in source; iOS device interaction remains to be verified.

## Purpose, scope, and ownership

This package wraps React Native text inputs and emits paste events for text and
images. The native input owns the platform paste action; callers own handling the
emitted event and any temporary image file URL. The package does not inspect
application-specific clipboard data.

## Public API and defaults

`TextInput` accepts React Native `TextInputProps` and an optional `onPaste`
callback. The callback receives `nativeEvent.items`; each item may contain
`type` and `data`. An image item has a MIME type and temporary file URL, and a
text item has type `text/plain`. With no `onPaste` callback, there is no
JavaScript paste subscription. This cache does not add a public prop or change
the callback payload.

## Lifecycle, concurrency, and cache

The iOS observer starts with the image cache uninitialized, registers for
pasteboard changes, app activation, and text-input begin-editing, and requests
an initial refresh. It uses one serial background queue for pasteboard image
availability reads and coalesces concurrent notifications. A notification
during a read requires another read before the cache is current. The cache is
process-local and is not persisted; its value is only a hint for command
availability. The paste handler checks the actual clipboard content when used.

## Platform behavior

- iOS `RCTUITextField` and `RCTUITextView` expose Paste for an image-only
  clipboard. When an image is pasted, the native observer loads its data and
  emits `OneKeyTextInputPaste` with a MIME type and temporary file URL. Text
  paste events use `text/plain`. If image loading fails, the native paste
  fallback remains available.
- iOS command validation must not read `UIPasteboard` on the main thread.
  While the cache is uninitialized or a refresh is outstanding, Paste remains
  available. Once a refresh completes with no image, normal React Native
  Paste gating applies.
- Android keeps its existing paste watcher behavior. This iOS cache does not
  change Android or Web paste behavior.

## Failure, fallback, and resource budget

The cache may briefly allow Paste when the clipboard contains no image. In
that case, the native paste action falls back to the text input's normal
behavior. If loading an image fails, the native paste fallback remains
available. Command validation performs atomic reads only; pasteboard XPC work
stays on the serial background queue. No image data is retained in the cache.

## Conformance and acceptance

The iOS implementation is in `ios/OneKeyTextInputPasteObserver.mm`. Focused
native tests cover image-only Paste while a refresh is blocked after first
focus and foreground activation. Simulator interaction should also confirm
that Paste appears and emits the image event in both cases; that interaction
has not yet been verified.
